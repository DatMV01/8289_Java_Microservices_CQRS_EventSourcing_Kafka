# bank-account-cqrs-event-sourcing-kafka

# Apache Kafka
docker network create --attachable -d bridge techbankNet
docker network ps
docker-compose up -d
![img.png](img.png)

# MongoDB
docker run -it -d --name mongo-container -p 27017:27017 --network techbankNet --restart always -v mongodb_data_container:/data/db mongo:latest

# MySQL
docker run -it -d --name mysql-container -p 3306:3306 --network techbankNet -e MYSQL_ROOT_PASSWORD=techbankRootPsw --restart always -v mysql_data_container:/var/lib/mysql mysql:latest

# Client tools in Docker – Adminer:
docker run -it -d --name adminer -p 8080:8080 --network techbankNet -e ADMINER_DEFAULT_SERVER=mysql-container --restart always adminer:latest 

