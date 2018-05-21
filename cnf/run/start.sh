#!/bin/bash
rm -rf felix-cache
java -Dlog4j.configuration=file:///home/younic/conf/log4j.properties -jar bin/felix.jar
