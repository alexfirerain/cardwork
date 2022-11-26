 # Сервис перевода денег
 
 
**CardWork** ‒ это REST-сервис для осуществления денежных переводов в согласованной работе с клиентским [веб-приложением](https://github.com/serp-ya/card-transfer) в соответствии со спецификацией "[Money Transfer Service Specification](https://github.com/netology-code/jd-homeworks/blob/master/diploma/MoneyTransferServiceSpecification.yaml)".

 ### Запуск приложения
Как клиентская, так и серверная часть могут быть запущены несколькими способами: на локальной машине как самостоятельные скомпилированные приложения либо как докер-контейнеры, а клиентское веб-приложение также может быть запущено удалённо (в частности по адресу https://serp-ya.github.io/card-transfer/), в последнем случае этот адрес должен быть отражён в свойстве `application:front-url` файла `application.yml`.