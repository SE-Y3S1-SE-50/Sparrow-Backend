@echo off
echo Starting infrastructure services...

echo Starting MongoDB...
start "MongoDB" docker run --name mongo-local -p 27017:27017 -d mongo:6.0

echo Starting Redis...
start "Redis" docker run --name redis-local -p 6379:6379 -d redis:7.2-alpine

echo Starting Zookeeper...
start "Zookeeper" docker run --name zookeeper-local -p 2181:2181 -e ZOOKEEPER_CLIENT_PORT=2181 -e ZOOKEEPER_TICK_TIME=2000 -d confluentinc/cp-zookeeper:7.6.1

timeout /t 10 /nobreak > nul

echo Starting Kafka...
start "Kafka" docker run --name kafka-local -p 9092:9092 --link zookeeper-local:zookeeper -e KAFKA_BROKER_ID=1 -e KAFKA_ZOOKEEPER_CONNECT=zookeeper:2181 -e KAFKA_ADVERTISED_LISTENERS=PLAINTEXT://localhost:9092 -e KAFKA_LISTENER_SECURITY_PROTOCOL_MAP=PLAINTEXT:PLAINTEXT -e KAFKA_INTER_BROKER_LISTENER_NAME=PLAINTEXT -e KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR=1 -d confluentinc/cp-kafka:7.6.1

echo Infrastructure services started!
echo MongoDB: localhost:27017
echo Redis: localhost:6379
echo Kafka: localhost:9092
echo.
echo Press any key to continue...
pause > nul
