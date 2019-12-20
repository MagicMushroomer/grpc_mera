"# grpc_mera" 
1)mvn clean,package
2)Запустить spring
3)cd $папка кафки$ 
.\bin\windows\zookeeper-server-start.bat .\config\zookeeper.properties
.\bin\windows\kafka-server-start.bat .\config\server.properties
4)Запуск grpc-server
5)Что нибудь написать в name localhost:8090/greeting?name="че нить"