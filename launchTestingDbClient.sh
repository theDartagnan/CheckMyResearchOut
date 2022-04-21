#!/bin/bash
echo "Attempt to connect to Check My Research Out Mongo server..."
docker run -ti --rm --name test-cmro-mongo-cli \
    --network cmrotestnet \
    mongo:4.4.13 mongo \
        --host test-cmro-mongo-srv \
        -u admin -p testpwd --authenticationDatabase admin \
        checkmro

echo "Bye."