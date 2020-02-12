



## Cria o container de Kfaka
docker run -d -p 2181:2181 -p 3030:3030 -p 8081-8083:8081-8083 -p 9581-9585:9581-9585 -p 9092:9092 --name=kafka  -e ADV_HOST=127.0.0.1 landoop/fast-data-dev:latest

# Entra no container do Kafka
docker exec -ti kafka bash

# Roda esses comandos para criar a fila
kafka-topics --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic simulacao
kafka-topics --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic senha
kafka-topics --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic conta
kafka-topics --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic limite
