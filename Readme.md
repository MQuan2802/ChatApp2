
mvn spring-boot:run #dont run this command with sudo
docker pull bitnami/postgresql
docker run --name chat_app  -p 5432:5432 -e POSTGRES_HOST_AUTH_METHOD=trust -e POSTGRES_USER=chat_app -e POSTGRES_PASSWORD=password -e POSTGRES_DB=chat_app  -d bitnami/postgresql
docker start chat_app
docker inspect chat_app | grep "IPAddress"
docker exec -it chat_app psql -h [postgres_container_ip] -p 5432 -U chat_app -d chat_app

