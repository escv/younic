#!/bin/bash
if [ ! -d "/opt/younic/cms-root" ]; then
  git clone $YOUNIC_CMS_ROOT_GIT /opt/younic/cms-root
fi

rm -rf felix-cache
java -Dlog4j.configuration=file:///opt/younic/conf/log4j.properties -jar bin/felix.jar
