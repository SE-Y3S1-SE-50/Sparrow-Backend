@echo off
echo Stopping infrastructure services...

echo Stopping Kafka...
docker stop kafka-local
docker rm kafka-local

echo Stopping Zookeeper...
docker stop zookeeper-local
docker rm zookeeper-local

echo Stopping Redis...
docker stop redis-local
docker rm redis-local

echo Stopping MongoDB...
docker stop mongo-local
docker rm mongo-local

echo All infrastructure services stopped!
pause
