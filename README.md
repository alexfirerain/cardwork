 # Сервис перевода денег **CardWork**
 
 
**"CardWork"** ‒ это REST-сервис для осуществления и журналирования денежных переводов в согласованной работе с клиентским веб-приложением в соответствии со спецификацией "[Money Transfer Service Specification](https://github.com/netology-code/jd-homeworks/blob/master/diploma/MoneyTransferServiceSpecification.yaml)". Работа программного комплекса денежных переводов состоит во взаимодействии двух независимых приложений: передовое (клиентское) веб-приложение (["Card Transfer"](https://github.com/serp-ya/card-transfer)) и тыловое (серверное) приложение-сервис (**данный проект**).

 ### Запуск программного комплекса
Как клиентская, так и серверная части могут быть запущены несколькими способами: на локальной машине как самостоятельные скомпилированные приложения либо как докер-контейнеры, а клиентское веб-приложение также может быть запущено удалённо (в частности оно доступно по адресу https://serp-ya.github.io/card-transfer/). Для запуска программного комплекса как двух приложений ОС должна поддерживать Java и NodeJS, для запуска через докер-контейнеры ‒ поддерживать Docker.

Рекомендуемый способ запуска ‒ через "Докер-Компоуз" командой `docker-compose up` в корневой папке **проекта**, которая запускает на локальной машине докер-контейнеры с передовой и тыловой частями, обеспечивая согласованность их настроек. Предполагается, что проект "Card-Transfer" также присутствует на машине по адресу, отражённому в `docker-compose.yml`, и имеет адекватный `Dockerfile`. **"CardWork"** запускается на порту 5500, "Card-Transfer" запускается на порту 3000.

При необходимости модификации параметров запуска программного комплекса необходимые изменения могут быть сделаны в конфигурационных файлах приложений (либо созданы дополнительные профили настроек). Настройки **данного проекта** содержатся в `application.yml`:

* `server: port` ‒ порт, на котором **"CardWork"** ожидает запросов;
* `logging: file: name` ‒ адрес журнала переводов (относительно корня проекта);
* `application: commission` ‒ размер взымаемой при переводах комиссии (как число двойной точности, где 0,01 означает 1%);
* `application: front-url` ‒ адрес, по которому запущено передовое веб-приложение, локальный (например `"http://localhost:3000"`) или удалённый (например `"https://serp-ya.github.io"`).

## Принцип работы и архитектура проекта

**Приложение-сервис** принимает от передового приложения запросы на перевод денежных средств и запросы на подтверждение запрошенного перевода в формате json согласно спецификации "[Money Transfer Service Specification](https://github.com/netology-code/jd-homeworks/blob/master/diploma/MoneyTransferServiceSpecification.yaml)". В ответ на эти запросы приложение, согласно спецификации, отсылает в формате json присвоенный номер операции с HTTP-кодом 200 в случае успеха операции либо описание проблемы с кодом 400 или 500 в случае неуспеха. 

Приложение спроектировано на языке Java с применением каркаса "Spring" и "Spring Boot", а также сборщика "Gradle".
Общий принцип построения приложения следует распространённой в подобных решениях трёхслойной модели MVC.

#### Модель предметной области

Приложение работает с запросами на перевод, содержащими необходимые данные: номер, срок действия и CVV карты отправителя, номер карты получателя и предмет перевода, оформленный как вложенный объект, состоящий из идентификатора валюты перевода и суммы перевода в центовых единицах (т.е. копейках). Минимальная величина суммы перевода равна 100 копейкам (ограничение задаётся архитектурой веб-приложения), максимальная в копейках ограничена ёмкостью типа `int`. В модели **"CardWork"** этому запросу соответствует класс [**Перевод**](src/main/java/ru/netology/cardwork/model/Transfer.java) (с вложенным классом **СуммаПеревода**). Объекты этого класса также используются в приложении для работы с переводами как сущностями. 

Сведения о карте отправителя (номер, срок действия и CVV) представлены внутри класса **Перевод** в виде класса [**Карта**](src/main/java/ru/netology/cardwork/model/Card.java). Сведения о сроке валидности карты хранятся в виде объекта java-класса `Date` и могут быть запрошены как в формате `Date`, так и в формате строки вида `ММ/ГГ`, соответствующей представлению в классе **Перевод** и означающей последний месяц, когда карта годна. 

Информация о соответствии карт денежным средствам на счетах реализована через эмуляцию банковской структуры в виде репозитория, хранящего объекты типа [**Аккаунт**](src/main/java/ru/netology/cardwork/model/Account.java). Объект типа **Аккаунт** содержит соответствующий ему объект типа **Карта**, прилежащие ему контактные данные и один или несколько валютных счетов, средства на которых представлены аналогично представлению в классе **Перевод**, а также флаг, сигнализирующий активность аккаунта. Состояние счёта допустимо как положительное, так и отрицательное, в пределах ёмкости типа `int` (несмотря на это используемая реализация репозитория не позволит списывать со счёта больше, чем его положительный баланс).

#### Сервис переводов
Собственно функционал REST-сервиса реализован в _контроллерах_ и _сервисе_.

Компонент [**КонтроллерПереводов**](src/main/java/ru/netology/cardwork/controller/TransferController.java) принимает
от передового приложения на оконечник `"/transfer"` запросы на перевод, и сформированный из запроса объект типа **Перевод** передаётся на обработку в **СлужбуПереводов**. В случае успешного приёма запроса от _сервиса_ (службы) возвращается DTO-объект, содержащий присвоенный операции перевода идентификатор, и он отправляется обратно веб-приложению.

На оконечник `/confirmOperation` _контроллер_ принимает от передового приложения запрос на подтверждение операции, содержащий ранее назначенный идентификатор и код подтверждения. Сформированный из этого запроса DTO-объект передаётся _сервису_. В случае успеха операции от _сервиса_ снова приходит DTO-объект с идентификатором операции перевода, которая теперь завершена, и он отправляется веб-приложению.

>Подразумевается, будто код подтверждения сообщается человеку, совершающему перевод, и человек отправляет его на наш сервис переводов. Реальная реализация приложения "Card-Transfer" сама присылает запрос с код подтверждения следом за запросом на перевод, и этот код всегда равен строке `0000`.

Компонент [**СлужбаПереводов**](src/main/java/ru/netology/cardwork/service/TransferService.java) выполняет основную логику приложения.





Компонент [**КонтроллерИсключений**](src/main/java/ru/netology/cardwork/controller/ExceptionController.java) перехватывает возникающие в приложении исключительные ситуации и отсылает веб-приложению, согласно спецификации, соответствующий DTO-объект, содержащий номер ошибки (в данной реализации сквозная нумерация от начала работы приложения) и сообщение, описывающее возникшую проблему. Ошибки, возникшие вследствие неправильного или невалидного запроса от клиента, по возможности маркируются HTTP-статусом 400, а ошибки, соответствующие неадекватности работы **"CardWork"**, статусом 500.

#### Эмуляция банковских операций

Для демонстрации операций с денежными счетами в приложение **"CardWork"** добавлена условная реализация банковских структур, соответствующих обеспечению таких операций. В неё входят _репозиторий_, хранящий сведения о привязанных к картам счетах, и два _провайдера_ служебной информации: поставщик операционных идентификаторов и поставщик верификации. Необходимое взаимодействие с ними описано через интерфейсы:
* [**ПоставщикОперационныхИД**](src/main/java/ru/netology/cardwork/providers/id/OperationIdProvider.java)
* [**ПоставщикВерификации**](src/main/java/ru/netology/cardwork/providers/verification/VerificationProvider.java)
* [**РепозиторийПригодныйДляПереводов**](src/main/java/ru/netology/cardwork/repository/TransferSuitableRepository.java)
* [**УправляемыйРепозиторийАккаунтов**](src/main/java/ru/netology/cardwork/repository/ManageableAccountRepository.java)

Подразумевается, что в _реальном_ программном комплексе эти функции лежат на самостоятельном банковском ПО, с которым REST-сервис денежных переводов должен был бы взаимодействовать через надлежащий АПИ. Однако в данном учебном задании этот АПИ неизвестен. Поэтому для демонстрации функциональности, внешне напоминающей реальные переводы, приложение содержит условную их реализацию:






🖥️ 7531 (2022 от Р.Х.)