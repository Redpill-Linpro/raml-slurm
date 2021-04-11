#! /bin/sh
curl -L https://github.com/stedolan/jq/releases/download/jq-1.6/jq-linux64 --output jq
chmod +x ./jq
./jq --version
cp ./jq /usr/bin/
gu install native-image
chmod +x compile.sh
