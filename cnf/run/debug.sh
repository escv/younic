#!/bin/bash
rm -rf felix-cache
java -Xdebug -Xrunjdwp:server=y,transport=dt_socket,address=8000,suspend=n -Dlog4j.configuration=file:///Users/aalbert/Development/cynic/cnf/run/conf/log4j.properties -jar bin/felix.jar
