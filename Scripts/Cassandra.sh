SELECT * FROM “my_keyspace_sre”.“tef”;
SELECT * FROM “my_keyspace_sre”.“senha”;
SELECT * FROM “my_keyspace_sre”.“limite”;
SELECT * FROM “my_keyspace_sre”.“conta”;

DROP_KEYSPACE "my_keyspace_sre";
DROP_TABLE "my_keyspace_sre"."tef"; 
DROP_TABLE "my_keyspace_sre"."senha";
DROP_TABLE "my_keyspace_sre"."limite";
DROP_TABLE "my_keyspace_sre"."conta";

DESCRIBE KEYSPACE "my_keyspace_sre";
DESCRIBE TABLE "my_keyspace_sre"."tef"; 
DESCRIBE TABLE "my_keyspace_sre"."senha";
DESCRIBE TABLE "my_keyspace_sre"."limite";
DESCRIBE TABLE "my_keyspace_sre"."conta";

CREATE KEYSPACE "my_keyspace_sre"
  WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1};

CREATE TABLE "my_keyspace_sre"."tef" 
(
      id_tef uuid PRIMARY KEY,
      evento text,
      tipo text,
      agencia_origem text,
      conta_origem text,
      dv_origem text,
      agencia_destino text,
      conta_destino text,
      dv_destino text,
      timestamp timestamp,
      valor decimal,
      senha text,
      rc_simulacao text,
      msg_simulacao text,
      rc_senha text,
      msg_senha text,
      rc_limite text,
      msg_limite text,
      rc_credito text,
      msg_credito text,
      rc_debito text,
      msg_debito text,
      rc_efetivacao text,
      msg_efetivacao text
 );

CREATE TABLE "my_keyspace_sre"."senha" 
(
      id_senha uuid PRIMARY KEY,
      agencia text,
      conta text,
      dv text,
      senha text
 );

CREATE TABLE "my_keyspace_sre"."limite" 
(
      id_limite uuid PRIMARY KEY,
      agencia text,
      conta text,
      dv text,
      valor_limite decimal,
      valor_utilizado decimal,
      timestamp_limite text
 );

CREATE TABLE "my_keyspace_sre"."conta" 
(
      id_conta uuid PRIMARY KEY,
      agencia text,
      conta text,
      dv text,
      valor_saldo decimal,
      bloqueio int
 );

INSERT INTO "my_keyspace_sre"."tef" 
(
      id_tef,
      evento,
      tipo,
      agencia_origem,
      conta_origem,
      dv_origem,
      agencia_destino,
      conta_destino,
      dv_destino,
      timestamp,
      valor,
      senha,
      rc_simulacao,
      msg_simulacao,
      rc_senha,
      msg_senha,
      rc_limite,
      msg_limite,
      rc_credito,
      msg_credito,
      rc_debito,
      msg_debito,
      rc_efetivacao,
      msg_efetivacao
)
VALUES 
(
      uuid(),
      'simulacao',
      'TEF_CC_CC',
      '1234',
      '98765',
      '0',
      '1234',
      '98765',
      '0',
      '2020-02-10 09:00:00.000',
      100.00,
      'MTIzNDU2',
      '99',
      'XXX',
      '99',
      'XXX',
      '99',
      'XXX',
      '99',
      'XXX',
      '99',
      'XXX',
      '99',
      'XXX'
);

INSERT INTO "my_keyspace_sre"."senha" 
(
      id_senha,
      agencia,
      conta,
      dv,
      senha
)
VALUES 
(
      uuid(),
      '1234',
      '98765',
      '0',
      'MTIzNDU2'
);


INSERT INTO "my_keyspace_sre"."limite" 
(
      id_limite,
      agencia,
      conta,
      dv,
      valor_limite,
      valor_utilizado,
      timestamp_limite
)
VALUES 
(
      uuid(),
      '1234',
      '98765',
      '0',
      100.00,
      100.00,
      '2020-02-10 09:00:00.000'
);


INSERT INTO "my_keyspace_sre"."conta" 
(
      id_conta,
      agencia,
      conta,
      dv,
      valor_saldo,
      bloqueio
)
VALUES 
(
      uuid(),
      '1234',
      '98765',
      '0',
      100.00,
      0
 );
