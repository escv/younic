#!/bin/bash
rm -rf felix-cache
java -Dlog4j.configuration=file:///Users/aalbert/Development/cynic/cnf/run/conf/log4j.properties -jar bin/felix.jar
