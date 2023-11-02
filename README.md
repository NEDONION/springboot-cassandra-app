# A Demo App with Spring Boot, Cassandra and Kafka

## Features
- RESTful CRUD
- Notification
- Email Sender
- Kafka Producer & Consumer
- Logging Aspect, enable annotations, support custom context and persistence
- Async Logging Manager

## Requisites
- Java
- Spring Boot
- JPA
- Cassandra
- Email
- Kafka
- Zookeeper
- TransmittableThreadLocal

## Get Started
- Clone the repository, and install Maven Dependencies
- Install Kafka, Zookeeper...

### Set up Cassandra

```shell
create keyspace ned_learning with replication={'class':'SimpleStrategy', 'replication_factor':1};
```

```shell
use ned_learning;
 
CREATE TABLE tutorial(
   id timeuuid PRIMARY KEY,
   title text,
   description text,
   published boolean
);
```

```shell
use ned_learning;

  CREATE TABLE person (
    id UUID PRIMARY KEY,
    name TEXT,
    age INT,
    email TEXT,
    tutorialIds LIST<UUID>
);
```

```shell
use ned_learning;

CREATE TABLE log_record (
    id TEXT PRIMARY KEY,
    biz_id TEXT,
    exception TEXT,
    operate_date TIMESTAMP,
    is_success BOOLEAN,
    msg TEXT,
    execute_result TEXT,
    execution_time BIGINT,
    client_ip      TEXT,
    user_agent     TEXT
);
```

Spark Aggregation data tables
```shell
CREATE TABLE ned_learning.biz_id_aggregation (
    operate_day DATE,
    biz_id TEXT,
    count_per_biz_id INT,
    PRIMARY KEY (operate_day, biz_id)
);

CREATE TABLE ned_learning.client_ip_aggregation (
    operate_day DATE,
    client_ip TEXT,
    count_per_client_ip INT,
    PRIMARY KEY (operate_day, client_ip)
);
```

### Set up Kafka

```shell
# create topics
kafka-topics.sh --create --zookeeper <your-IP-address>:2181 --replication-factor 1 --partitions 1 --topic demo-cassandra-email
# or
kafka-topics.sh --create --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1 --topic demo-cassandra-email
kafka-topics.sh --create --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1 --topic demo-log-record
```

## Documentations
### CRUD sample - getAllTutorials

```shell
curl --location 'http://localhost:9999/api/tutorials'
```

```json
// Output:
[
    {
        "id": "3dbe0870-6ca2-11ee-81bf-8fcfd0279adb",
        "title": "Lucas is testing cassandra",
        "description": "testing cassandra",
        "published": false
    },
    {
        "id": "621c7590-c765-11ed-8bba-198cdd48c909",
        "title": "cs test ring",
        "description": "testing CS books",
        "published": false
    },
    {
        "id": "ba020390-c762-11ed-9254-61fc6bb16b37",
        "title": "DDIA",
        "description": "DDIA is a famous well-known CS book",
        "published": true
    },
    {
        "id": "73177a60-c76b-11ed-8432-85c2ed56c550",
        "title": "business test book",
        "description": "testing business books",
        "published": false
    }
]
```

### CRUD sample - createTutorial

```shell
curl --location 'http://localhost:9999/api/tutorials' \
--header 'Content-Type: application/json' \
--data '{
    "title": "Lucas is testing cassandra",
    "description": "testing cassandra"
}'
```

```json
// Output:
{
    "id": "3dbe0870-6ca2-11ee-81bf-8fcfd0279adb",
    "title": "Lucas is testing cassandra",
    "description": "testing cassandra",
    "published": false
}
```
