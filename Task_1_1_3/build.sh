#!/usr/bin/env bash

mkdir -p build/classes

javac ./src/main/java/*.java -d ./build/classes

jar cfe ./build/app.jar Main -C ./build/classes .

java -jar ./build/app.jar