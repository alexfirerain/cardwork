 # Сервис перевода денег **CardWork**
 
 
**"CardWork"** ‒ это REST-сервис для осуществления и журналирования денежных переводов в согласованной работе с клиентским веб-приложением в соответствии со спецификацией "[Money Transfer Service Specification](https://github.com/netology-code/jd-homeworks/blob/master/diploma/MoneyTransferServiceSpecification.yaml)". Работа программного комплекса денежных переводов состоит во взаимодействии двух независимых приложений: передовое (клиентское) веб-приложение (["Card Transfer"](https://github.com/serp-ya/card-transfer)) и тыловое (серверное) приложение-сервис (**данный проект**).

 ### Запуск программного комплекса
Как клиентская, так и серверная части могут быть запущены несколькими способами: на локальной машине как самостоятельные скомпилированные приложения либо как докер-контейнеры, а клиентское веб-приложение также может быть запущено удалённо (в частности оно доступно по адресу https://serp-ya.github.io/card-transfer/). Для запуска программного комплекса как двух приложений ОС должна поддерживать Java и NodeJS, для запуска через докер-контейнеры ‒ поддерживать Docker.

Рекомендуемый способ запуска ‒ через "Докер-Компоуз", который запускает на локальной машине докер-контейнеры с передовой и тыловой частями, обеспечивая согласованность их настроек.

В конфигурации, упакованной в **этом проекте**, предполагается, что на машине доступен образ проекта "Card-Transfer", названный "`cardtransfer-app`" (для сборки такого образа в папку проекта "Card-Transfer" следует скопировать файл [`Dockerfile.front`](Dockerfile.front) и выполнить в ней команду `docker build . -f Dockerfile.front -t cardtransfer-app`). Для создания образа **данного проекта** в его папке следует выполнить команду `docker build . -t cardwork-app`. Когда оба образа с нужными именами присутствуют в системе, программный комплекс может запускаться командой `docker-compose up -d` в корневой папке **проекта**. 

**"CardWork"** запускается на порту 5500, "Card-Transfer" запускается на порту 3000.

При необходимости модификации параметров запуска программного комплекса необходимые изменения могут быть сделаны в конфигурационных файлах приложений (либо созданы дополнительные профили настроек). Настройки **данного проекта** содержатся в `application.yml`:

* `server: port` ‒ порт, на котором **"CardWork"** ожидает запросов;
* `logging: file: name` ‒ адрес журнала переводов (относительно корня проекта);
* `application: commission` ‒ размер взымаемой при переводах комиссии (как число двойной точности, где 0,01 означает 1%);
* `application: front-url` ‒ адрес, по которому запущено передовое веб-приложение, локальный (например `"http://localhost:3000"`) или удалённый (например `"https://serp-ya.github.io"`).

## Принцип работы и архитектура проекта

**Приложение-сервис** принимает от передового приложения запросы на перевод денежных средств и запросы на подтверждение запрошенного перевода в формате json согласно спецификации "[Money Transfer Service Specification](https://github.com/netology-code/jd-homeworks/blob/master/diploma/MoneyTransferServiceSpecification.yaml)". В ответ на эти запросы приложение, согласно спецификации, отсылает в формате json присвоенный номер операции с HTTP-кодом 200 в случае успеха операции либо описание проблемы с кодом 400 или 500 в случае неуспеха. 

Приложение спроектировано на языке Java с применением каркасов "Spring" и "Spring Boot", автоматизирующих генерацию и связывание компонентов, при сборке также используется "Gradle".
Общий принцип построения приложения следует распространённой в подобных решениях трёхслойной модели MVC.

#### Модель предметной области

Приложение работает с запросами на перевод, содержащими необходимые данные: номер, срок действия и CVV карты отправителя, номер карты получателя и предмет перевода, оформленный как вложенный объект, состоящий из идентификатора валюты перевода и суммы перевода в центовых единицах (т.е. копейках). Минимальная величина суммы перевода равна 100 копейкам (ограничение задаётся архитектурой веб-приложения), максимальная в копейках ограничена ёмкостью типа `int`. В модели **"CardWork"** этому запросу соответствует класс [**Перевод**](src/main/java/ru/netology/cardwork/model/Transfer.java) (с вложенным классом **СуммаПеревода**). Объекты этого класса также используются в приложении для работы с переводами как сущностями. 

Сведения о карте отправителя (номер, срок действия и CVV) представлены внутри класса **Перевод** в виде класса [**Карта**](src/main/java/ru/netology/cardwork/model/Card.java). Сведения о сроке валидности карты хранятся в виде объекта java-класса `Date` и могут быть запрошены как в формате `Date`, так и в формате строки вида `ММ/ГГ`, соответствующей представлению в классе **Перевод** и означающей последний месяц, когда карта годна. 

Информация о соответствии карт денежным средствам на счетах реализована через эмуляцию банковской структуры в виде репозитория, хранящего объекты типа [**Аккаунт**](src/main/java/ru/netology/cardwork/model/Account.java). Объект типа **Аккаунт** содержит соответствующий ему объект типа **Карта**, прилежащие ему контактные данные и один или несколько валютных счетов, средства на которых представлены аналогично представлению в классе **Перевод**, а также флаг, сигнализирующий активность аккаунта. Состояние счёта допустимо как положительное, так и отрицательное, в пределах ёмкости типа `int` (несмотря на это используемая реализация репозитория не позволит списывать со счёта больше, чем его положительный баланс).

#### Сервис переводов
Собственно функционал REST-сервиса реализован в _контроллерах_ и _сервисе_.

Компонент [**КонтроллерПереводов**](src/main/java/ru/netology/cardwork/controller/TransferController.java) принимает
от передового приложения на оконечник `"/transfer"` запросы на перевод, и сформированный из запроса объект типа **Перевод** передаётся на обработку в **СлужбуПереводов**, чтобы был там зафиксирован как заявка на перевод, ожидающая подтверждения. В случае успешного приёма заявки от _сервиса_ (службы) возвращается DTO-объект, содержащий присвоенный операции перевода идентификатор, и он отправляется обратно веб-приложению.

На оконечник `/confirmOperation` _контроллер_ принимает от передового приложения запрос на подтверждение операции, содержащий ранее назначенный идентификатор и код подтверждения. Сформированный из этого запроса DTO-объект передаётся _сервису_. В случае успеха операции от _сервиса_ снова приходит DTO-объект с идентификатором операции перевода, которая теперь завершена, и он отправляется веб-приложению.

>Подразумевается, будто при процедуре _верификации_ код подтверждения сообщается человеку, совершающему перевод, и человек отправляет его на **наш сервис переводов**. Реальная реализация приложения "Card-Transfer" сама присылает запрос с кодом подтверждения следом за запросом на перевод, и этот код всегда равен строке `"0000"`.

Компонент [**СлужбаПереводов**](src/main/java/ru/netology/cardwork/service/TransferService.java) выполняет основную логику приложения. Содержит мапу ожидающих подтверждения заявок на перевод, где в качестве ключей используются присвоенные заявкам "операционные ИДы", а также значение комиссии, которая должна удерживаться с переводов ‒ оно при инициализации компонента берётся из глобального свойства `application:commission`.

Получив запрос на перевод, _сервис_ запрашивает у _репозитория_, возможен ли такой перевод (учитывая установленную комиссию). Если запрошенный перевод по какой-либо причине невозможно осуществить, выбрасывается исключение и обработка заявки прекращается. Если же возражений со стороны _репозитория_ не последовало, у него запрашиваются контактные данные, связанные с картой отправителя, и эти данные вместе со ссылкой на запрос передаются _поставщику верификации_, чтобы он инициировал процедуру верификации (т.е. сгенерировал и отослал пользователю проверочный код). Если этот процесс завершился без возникновения исключительных ситуаций, у _поставщика операционных идентификаторов_ запрашивается очередной свободный идентификатор, и на него мапится запрос на перевод как ожидающая подтверждения заявка, а сам идентификатор операции оборачивается в соответствующий DTO-объект и возвращается _контроллеру_.

Получив запрос с подтверждением перевода, _сервис_ сперва проверяет, ожидает ли подтверждения перевод с таким идентификатором операции, и если нет, обработка прекращается с выбросом исключения. Если же заявка с таким ИДом операции актуальна, соответствующий объект типа **Перевод** и полученный для него код подтверждения отсылаются на проверку _поставщику верификации_. Если верификатор отвечает, что код не соответствует, обработка останавливается с выбросом исключения. Если же программа без исключительных ситуаций добралась до этой точки, наконец _репозиторию_ отправляется команда на осуществление запрошенной транзакции (с указанием установленной комиссии). В начале осуществления транзакции _репозиторий_ ещё раз проверит её возможность и, если вдруг окажется, что провести такой перевод нельзя (т.е. если за время ожидания подтверждения какой-то из вовлечённых аккаунтов перестал быть активным, либо средства на аккаунте отправителя в требуемой валюте стали недостаточными), он, опять же, прервёт выполнение выбросом исключения, и транзакция не будет осуществлена. Во всех перечисленных случаях прерывания неподтверждённая заявка на перевод остаётся в мапе. И только если исполнение программы дошло до следующей строчки, т.е. _репозиторий_ успешно завершил транзакцию, выполненный перевод удаляется из мапы, а _контроллеру_ отправляется его операционный идентификатор как знак того, что перевод успешно подтверждён и осуществлён.

Компонент [**КонтроллерИсключений**](src/main/java/ru/netology/cardwork/controller/ExceptionController.java) перехватывает возникающие в приложении исключительные ситуации и отсылает веб-приложению, согласно спецификации, соответствующий DTO-объект, содержащий номер ошибки (в данной реализации сквозная нумерация от начала работы приложения) и сообщение, описывающее возникшую проблему. Ошибки, возникшие вследствие неправильного или невалидного запроса от клиента, по возможности маркируются HTTP-статусом 400, а ошибки, соответствующие неадекватности работы **"CardWork"**, статусом 500.

#### Эмуляция банковских операций

Для демонстрации операций с денежными счетами в приложение **"CardWork"** добавлена условная реализация банковских структур, соответствующих обеспечению таких операций. В неё входят _репозиторий_, хранящий сведения о привязанных к картам счетах, и два _провайдера_ служебной информации: поставщик операционных идентификаторов и поставщик верификации. Необходимое взаимодействие с ними описано через интерфейсы:
* [**ПоставщикОперационныхИД**](src/main/java/ru/netology/cardwork/providers/id/OperationIdProvider.java) делает только одно: поставляет очередную строку, которая будет использована в качестве операционного ИД для очередного перевода.
* [**ПоставщикВерификации**](src/main/java/ru/netology/cardwork/providers/verification/VerificationProvider.java) олицетворяет функционал, связанный с процедурой верификации перевода через код подтверждения операции:
  * _инициировать_ операцию верификации для некоторого данного объекта типа **Перевод** с использований полученных контактных данных. Подразумевается, что проверочный код генерируется и сопоставляется конкретной ожидающей подтверждения транзакции, а также высылается человеку на основе контактных данных.
  * _проверить_, является ли некоторый код правильным для подтверждения некоторой ожидающей транзакции (представленной объектом типа **Перевод**).
* [**РепозиторийПригодныйДляПереводов**](src/main/java/ru/netology/cardwork/repository/TransferSuitableRepository.java) олицетворяет функционал для взаимодействия с REST-_сервисом_, а именно:
  * _сообщить_ строку контактных данных, связанных с заданной банковской картой;
  * _проверить_ объект типа **Перевод** на возможность совершения соответствующей транзакции с учётом снятия указанной комиссии;
  * _осуществить_ операцию транзакции, соответствующую полученному объекту типа **Перевод** с учётом указанной комиссии.   
* [**УправляемыйРепозиторийАккаунтов**](src/main/java/ru/netology/cardwork/repository/ManageableAccountRepository.java) олицетворяет функционал управления аккаунтами: их добавление, изменение и удаление. Набор методов определён задачами тестирования и демонстрации, так что содержит например метод, возвращающий строку с форматированным представлением состояния всех аккаунтов, что едва ли могло бы иметь применение в промышленности.

Подразумевается, что в _реальном_ программном комплексе эти функции лежат на самостоятельном банковском ПО, с которым REST-сервис денежных переводов должен был бы взаимодействовать через надлежащий АПИ. Однако в **данном учебном задании** этот АПИ неизвестен. Поэтому для демонстрации функциональности, внешне напоминающей реальные переводы, приложение содержит условную их реализацию:
* >Компонент [**ДемоИмплПоставщикаОперационныхИД**](src/main/java/ru/netology/cardwork/providers/id/OperationIdProviderDemoImpl.java) _выдаёт_ последовательность целых чисел в виде строк, начиная с `"0"`, инициализируемую вместе с объектом. Также содержит методы _просмотра_ текущего состояния счётчика (т.е. номера, который будет присвоен при следующем запросе) и его _обнуления_ ‒ для задач тестирования и демонстрации. 
* > Компонент [**ДемоИмплПоставщикаВерификации**](src/main/java/ru/netology/cardwork/providers/verification/VerificationProviderDemoImpl.java) при запуске процедуры _верификации_ не делает ничего, её вызов просто фиксируется в журнале работы. _Сличение_ присланного веб-приложением проверочного кода осществляется со строковой константой (`"0000"` в данной реализации). Также реализация содержит метод, оглашающий константу ‒ для задач тестирования и демонстрации.
* > Компонент [**ДемоИмплРепозиторияАккаунтов**](src/main/java/ru/netology/cardwork/repository/AccountRepositoryDemoImpl.java) воплощает оба репозиторных интерфейса, являясь **ПригоднымДляПереводов**, чтобы взаимодействовать с REST-сервисом, и **Управляемым**, чтобы можно было наполнять его аккаунтами для задач тестирования и демонстрации. Репозиторий хранит объекты типа **Аккаунт** с помощью мапы, где в качестве ключей используются сопряжённые с аккаунтами объекты типа **Карта**. Также репозиторий содержит особый счёт, на котором аккумулируется снятая за переводы комиссия, решённый в виде мапы с обозначениями валюты в качестве ключей. Никаких дополнительных инструментов для работы с этим комиссионным счётом в данной реализации не предусмотрено. Репозиторий предоставлят информацию об аккаунтах, картах и счетах на основе их актуального состояния и производит операции, соответствующие движению средств между счетами.

## Тестирование и демонстрация работы
Класс [DemoData](src/main/java/ru/netology/cardwork/repository/DemoData.java) содержит несколько примеров объектов типа **Карта**, **Аккаунт** и **Перевод**, чтобы использовать при тестировании.

Аккаунты, определённые в этом классе, автоматически подгружаются в _репозиторий_ при его инициализации, так что корректность работы программного комплекса может быть продемонстрирована при взаимодействии пользователя непосредственно с передовым браузерным веб-приложением (запущенным локально либо удалённо), учитывая, какие карты с какими счетами определены в демо-данных. 

Примеры HTTP-запросов к REST-сервису (переводы между картами из демо-данных, a также примеры невалидных запросов) представлены в файле [`test-requests.http`](src/test/test-requests.http).


🖥️ 7531 (2022 от Р.Х.)