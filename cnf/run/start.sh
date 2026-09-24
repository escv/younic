#!/bin/sh

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
# jdk.util.zip.disableZip64ExtraFieldValidation: legacy third-party bundles (e.g. Aries JAX-RS
# embedded libs) ship zips that fail JDK 21 strict zip validation; Java <= 11 tolerated them.
java -Djdk.util.zip.disableZip64ExtraFieldValidation=true -Dlog4j.configuration="file://$(pwd)/conf/log4j.properties" -jar bin/felix.jar
echo $! > younic.pid
