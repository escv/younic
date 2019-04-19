#!/bin/bash

# fallback if ENV Variable for git cms root was not set
if [ -z "$YOUNIC_CMS_ROOT_GIT" ]; then
	export YOUNIC_CMS_ROOT_GIT=https://github.com/escv/younic-sample.git
fi

if [ ! -d "cms-root/content" ]; then
  git clone --depth 1 $YOUNIC_CMS_ROOT_GIT cms-root
fi

## to start in ADMIN Mode (enable bundle-adm folder), uncomment the following line
# export YOUNIC_RUN_ADMIN=true

rm -rf felix-cache
java -Dlog4j.configuration="file://$(pwd)/conf/log4j.properties" -jar bin/felix.jar &
echo $! > younic.pid
