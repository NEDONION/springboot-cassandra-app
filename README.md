# SpringBoot Cassandra Demo - A CRUD Java Spring Boot with Cassandra


## Requisites
- Java
- Spring Boot
- JPA
- Cassandra

## Get Started
- Clone the repository, and install Maven Dependencies


## getAllTutorials

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

## createTutorial

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