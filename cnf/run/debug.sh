#!/bin/bash
rm -rf felix-cache
java -Xdebug -Xrunjdwp:server=y,transport=dt_socket,address=8000,suspend=y -Dlog4j.configuration="file:///opt/younic/conf/log4j.properties" -jar bin/felix.jar
