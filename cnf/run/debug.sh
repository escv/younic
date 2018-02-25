rm -rf felix-cache && nohup java -Xdebug -Xrunjdwp:server=y,transport=dt_socket,address=8000,suspend=n -Dlog4j.configuration=file:///home/younic/cms/conf/log4j.properties -jar bin/felix.jar &
tail -f nohup.out
