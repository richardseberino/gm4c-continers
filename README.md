

#####################################################################################
# Kafka
#####################################################################################
## Cria o container de Kfaka
docker run -d -p 2181:2181 -p 3030:3030 -p 8081-8083:8081-8083 -p 9581-9585:9581-9585 -p 9092:9092 --name=kafka  -e ADV_HOST=127.0.0.1 landoop/fast-data-dev:latest

## Se necessário Start do container
docker start kafka

## Entra no container do Kafka
docker exec -ti kafka bash

## Roda esses comandos para criar a fila

kafka-topics --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic tef

kafka-topics --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic senha

kafka-topics --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic conta

kafka-topics --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic limite


#####################################################################################
# Cassandra
#####################################################################################

Docker User/Pass: cassandra / cassandra

## Cria o container do cassandra
docker run --name cassandra -p 7000:7000 -p 9042:9042 -d cassandra:latest

## Se necessário Start do container
docker start cassandra

## entra no container do casandra 
docker exec -it cassandra bash

## inicia a sessão interativa com o banco de dados
cqlsh

## cria o banco de dados
create keyspace itau WITH replication = {'class':'SimpleStrategy', 'replication_factor' : 1};
use itau;

## cria as tabelas ;
CREATE TABLE tef (id_tef uuid PRIMARY KEY, evento text, tipo text, agencia_origem int, conta_origem int, dv_origem int, agencia_destino int, conta_destino int, dv_destino int, timestamp timestamp, valor decimal, senha text, transacionId text, rc_simulacao text,msg_simulacao text, rc_senha text, msg_senha text, rc_limite text, msg_limite text, rc_credito text, msg_credito text, rc_debito text, msg_debito text, rc_efetivacao text, msg_efetivacao text);

CREATE TABLE senha (id_senha uuid PRIMARY KEY, agencia int, conta int, dv int, senha text);

CREATE TABLE limite(id_limite uuid PRIMARY KEY, agencia int, conta int, dv int, valor_limite decimal, valor_utilizado decimal, timestamp_limite text);

CREATE TABLE conta (id_conta uuid PRIMARY KEY, agencia int, conta int, dv int, valor_saldo decimal, bloqueio int);

## popula as tabeleas
INSERT INTO senha(id_senha, agencia, conta, dv, senha) VALUES (uuid(), 10, 10, 0, '123456');

INSERT INTO senha(id_senha, agencia, conta, dv, senha) VALUES (uuid(), 10, 11, 0, '123456');

INSERT INTO senha(id_senha, agencia, conta, dv, senha) VALUES (uuid(), 10, 12, 0, '123456');

INSERT INTO senha(id_senha, agencia, conta, dv, senha) VALUES (uuid(), 10, 13, 0, '123456');

INSERT INTO senha(id_senha, agencia, conta, dv, senha) VALUES (uuid(), 10, 14, 0, '123456');

INSERT INTO senha(id_senha, agencia, conta, dv, senha) VALUES (uuid(), 10, 15, 0, '123456');

INSERT INTO senha(id_senha, agencia, conta, dv, senha) VALUES (uuid(), 10, 16, 0, '123456');

INSERT INTO senha(id_senha, agencia, conta, dv, senha) VALUES (uuid(), 10, 17, 0, '123456');

INSERT INTO senha(id_senha, agencia, conta, dv, senha) VALUES (uuid(), 10, 18, 0, '123456');

INSERT INTO senha(id_senha, agencia, conta, dv, senha) VALUES (uuid(), 10, 19, 0, '123456');

INSERT INTO senha(id_senha, agencia, conta, dv, senha) VALUES (uuid(), 20, 20, 0, '123456');

INSERT INTO senha(id_senha, agencia, conta, dv, senha) VALUES (uuid(), 20, 21, 0, '123456');

INSERT INTO senha(id_senha, agencia, conta, dv, senha) VALUES (uuid(), 20, 22, 0, '123456');

INSERT INTO senha(id_senha, agencia, conta, dv, senha) VALUES (uuid(), 20, 23, 0, '123456');

INSERT INTO senha(id_senha, agencia, conta, dv, senha) VALUES (uuid(), 20, 24, 0, '123456');

INSERT INTO senha(id_senha, agencia, conta, dv, senha) VALUES (uuid(), 20, 25, 0, '123456');

INSERT INTO senha(id_senha, agencia, conta, dv, senha) VALUES (uuid(), 20, 26, 0, '123456');

INSERT INTO senha(id_senha, agencia, conta, dv, senha) VALUES (uuid(), 20, 27, 0, '123456');

INSERT INTO senha(id_senha, agencia, conta, dv, senha) VALUES (uuid(), 20, 28, 0, '123456');

INSERT INTO senha(id_senha, agencia, conta, dv, senha) VALUES (uuid(), 20, 29, 0, '123456');

INSERT INTO limite (id_limite, agencia, conta, dv, valor_limite, valor_utilizado, timestamp_limite) VALUES (uuid(),10, 10, 0, 100.00, 100.00, '2020-02-10 09:00:00.000');

INSERT INTO limite (id_limite, agencia, conta, dv, valor_limite, valor_utilizado, timestamp_limite) VALUES (uuid(),10, 11, 0, 100.00, 100.00, '2020-02-10 09:00:00.000');

INSERT INTO limite (id_limite, agencia, conta, dv, valor_limite, valor_utilizado, timestamp_limite) VALUES (uuid(),10, 12, 0, 100.00, 100.00, '2020-02-10 09:00:00.000');

INSERT INTO limite (id_limite, agencia, conta, dv, valor_limite, valor_utilizado, timestamp_limite) VALUES (uuid(),10, 13, 0, 100.00, 100.00, '2020-02-10 09:00:00.000');

INSERT INTO limite (id_limite, agencia, conta, dv, valor_limite, valor_utilizado, timestamp_limite) VALUES (uuid(),10, 14, 0, 100.00, 100.00, '2020-02-10 09:00:00.000');

INSERT INTO limite (id_limite, agencia, conta, dv, valor_limite, valor_utilizado, timestamp_limite) VALUES (uuid(),10, 15, 0, 100.00, 100.00, '2020-02-10 09:00:00.000');

INSERT INTO limite (id_limite, agencia, conta, dv, valor_limite, valor_utilizado, timestamp_limite) VALUES (uuid(),10, 16, 0, 100.00, 100.00, '2020-02-10 09:00:00.000');

INSERT INTO limite (id_limite, agencia, conta, dv, valor_limite, valor_utilizado, timestamp_limite) VALUES (uuid(),10, 17, 0, 100.00, 100.00, '2020-02-10 09:00:00.000');

INSERT INTO limite (id_limite, agencia, conta, dv, valor_limite, valor_utilizado, timestamp_limite) VALUES (uuid(),10, 18, 0, 100.00, 100.00, '2020-02-10 09:00:00.000');

INSERT INTO limite (id_limite, agencia, conta, dv, valor_limite, valor_utilizado, timestamp_limite) VALUES (uuid(),10, 19, 0, 100.00, 100.00, '2020-02-10 09:00:00.000');

INSERT INTO limite (id_limite, agencia, conta, dv, valor_limite, valor_utilizado, timestamp_limite) VALUES (uuid(),20, 20, 0, 100.00, 100.00, '2020-02-10 09:00:00.000');

INSERT INTO limite (id_limite, agencia, conta, dv, valor_limite, valor_utilizado, timestamp_limite) VALUES (uuid(),20, 21, 0, 100.00, 100.00, '2020-02-10 09:00:00.000');

INSERT INTO limite (id_limite, agencia, conta, dv, valor_limite, valor_utilizado, timestamp_limite) VALUES (uuid(),20, 22, 0, 100.00, 100.00, '2020-02-10 09:00:00.000');

INSERT INTO limite (id_limite, agencia, conta, dv, valor_limite, valor_utilizado, timestamp_limite) VALUES (uuid(),20, 23, 0, 100.00, 100.00, '2020-02-10 09:00:00.000');

INSERT INTO limite (id_limite, agencia, conta, dv, valor_limite, valor_utilizado, timestamp_limite) VALUES (uuid(),20, 24, 0, 100.00, 100.00, '2020-02-10 09:00:00.000');

INSERT INTO limite (id_limite, agencia, conta, dv, valor_limite, valor_utilizado, timestamp_limite) VALUES (uuid(),20, 25, 0, 100.00, 100.00, '2020-02-10 09:00:00.000');

INSERT INTO limite (id_limite, agencia, conta, dv, valor_limite, valor_utilizado, timestamp_limite) VALUES (uuid(),20, 26, 0, 100.00, 100.00, '2020-02-10 09:00:00.000');

INSERT INTO limite (id_limite, agencia, conta, dv, valor_limite, valor_utilizado, timestamp_limite) VALUES (uuid(),20, 27, 0, 100.00, 100.00, '2020-02-10 09:00:00.000');

INSERT INTO limite (id_limite, agencia, conta, dv, valor_limite, valor_utilizado, timestamp_limite) VALUES (uuid(),20, 28, 0, 100.00, 100.00, '2020-02-10 09:00:00.000');

INSERT INTO limite (id_limite, agencia, conta, dv, valor_limite, valor_utilizado, timestamp_limite) VALUES (uuid(),20, 29, 0, 100.00, 100.00, '2020-02-10 09:00:00.000');

INSERT INTO conta (id_conta, agencia, conta, dv, valor_saldo, bloqueio) VALUES (uuid(), 10, 10, 0, 100.00, 0);

INSERT INTO conta (id_conta, agencia, conta, dv, valor_saldo, bloqueio) VALUES (uuid(), 10, 11, 0, 100.00, 0);

INSERT INTO conta (id_conta, agencia, conta, dv, valor_saldo, bloqueio) VALUES (uuid(), 10, 12, 0, 100.00, 0);

INSERT INTO conta (id_conta, agencia, conta, dv, valor_saldo, bloqueio) VALUES (uuid(), 10, 13, 0, 100.00, 0);

INSERT INTO conta (id_conta, agencia, conta, dv, valor_saldo, bloqueio) VALUES (uuid(), 10, 14, 0, 100.00, 0);

INSERT INTO conta (id_conta, agencia, conta, dv, valor_saldo, bloqueio) VALUES (uuid(), 10, 15, 0, 100.00, 0);

INSERT INTO conta (id_conta, agencia, conta, dv, valor_saldo, bloqueio) VALUES (uuid(), 10, 16, 0, 100.00, 0);

INSERT INTO conta (id_conta, agencia, conta, dv, valor_saldo, bloqueio) VALUES (uuid(), 10, 17, 0, 100.00, 0);

INSERT INTO conta (id_conta, agencia, conta, dv, valor_saldo, bloqueio) VALUES (uuid(), 10, 18, 0, 100.00, 0);

INSERT INTO conta (id_conta, agencia, conta, dv, valor_saldo, bloqueio) VALUES (uuid(), 10, 19, 0, 100.00, 0);

INSERT INTO conta (id_conta, agencia, conta, dv, valor_saldo, bloqueio) VALUES (uuid(), 20, 20, 0, 100.00, 0);

INSERT INTO conta (id_conta, agencia, conta, dv, valor_saldo, bloqueio) VALUES (uuid(), 20, 21, 0, 100.00, 0);

INSERT INTO conta (id_conta, agencia, conta, dv, valor_saldo, bloqueio) VALUES (uuid(), 20, 22, 0, 100.00, 0);

INSERT INTO conta (id_conta, agencia, conta, dv, valor_saldo, bloqueio) VALUES (uuid(), 20, 23, 0, 100.00, 0);

INSERT INTO conta (id_conta, agencia, conta, dv, valor_saldo, bloqueio) VALUES (uuid(), 20, 24, 0, 100.00, 0);

INSERT INTO conta (id_conta, agencia, conta, dv, valor_saldo, bloqueio) VALUES (uuid(), 20, 25, 0, 100.00, 0);

INSERT INTO conta (id_conta, agencia, conta, dv, valor_saldo, bloqueio) VALUES (uuid(), 20, 26, 0, 100.00, 0);

INSERT INTO conta (id_conta, agencia, conta, dv, valor_saldo, bloqueio) VALUES (uuid(), 20, 27, 0, 100.00, 0);

INSERT INTO conta (id_conta, agencia, conta, dv, valor_saldo, bloqueio) VALUES (uuid(), 20, 28, 0, 100.00, 0);

INSERT INTO conta (id_conta, agencia, conta, dv, valor_saldo, bloqueio) VALUES (uuid(), 20, 29, 0, 100.00, 0);

SELECT * FROM tef;

SELECT * FROM senha;

SELECT * FROM limite;

SELECT * FROM conta;

#####################################################################################
# Git Bash
#####################################################################################
ATENÇÃO!!! Executar uma única vez:
   git config --global user.name "FIRST_NAME LAST_NAME"
   git config --global user.email "MY_NAME@example.com"

1. Executar o Git Bash.
2. Entrar no diretório do Git do projeto (onde encontra o .GIT).
3. Baixar o repositório atualizado:
   git fetch
   git pull 
4. Fazer o Refresh + Project Clean no Eclipse (Spring Tool Suite)


