#!/bin/bash
echo "Check My Research Out testing network..."
docker network create cmrotestnet

echo "Create Check My Research Out Mongo server..."
docker run -ti --rm --name test-cmro-mongo-srv \
    -e MONGO_INITDB_ROOT_USERNAME=admin \
    -e MONGO_INITDB_ROOT_PASSWORD=testpwd \
    -e MONGO_INITDB_DATABASE=checkmro \
    -p "127.0.0.1:27017:27017" \
    --network cmrotestnet \
    mongo:4.4.13

echo "Check My Research Out Mongo server server stopped."

echo "Check My Research Out testing network..."
docker network rm cmrotestnet

echo "Bye."