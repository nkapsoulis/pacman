#!/bin/bash

cp -r src/img bin/.
cp -r src/levels bin/.
cp -r src/sounds bin/.
cp -r src/HighScores.txt bin/.
cd src/
javac -d ../bin Pacman.java
cd ../bin/
java Pacman
