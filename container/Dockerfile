# build it with: docker build -t e/younic .
# run it with: docker run -p 127.0.0.1:8080:8080 -it escv/younic

FROM openjdk:11-jre-slim

MAINTAINER Andre Albert <andre.albert82@googlemail.com>

WORKDIR /opt/younic

# EXPOSE 8080/tcp

RUN apt update && \
    apt -y install git && \
    apt -y install unzip && \
    rm -rf /var/lib/apt/lists/* && \
    rm -rf /var/cache/apt/*

# ADD http://andre.webofferte.com/younic/younic.zip ./younic.zip 
ADD younic.zip ./younic.zip 

RUN	unzip younic.zip && \
	rm younic.zip

# RUN git clone https://github.com/escv/younic-sample.git /opt/younic/cms-root
EXPOSE 8080
ENTRYPOINT ["./start.sh"]
