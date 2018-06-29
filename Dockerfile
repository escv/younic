# build it with: docker build -t escv/younic .
# run it with: docker run -p 127.0.0.1:8080:8080 -it escv/younic

FROM anapsix/alpine-java:latest
MAINTAINER Andre Albert <andre.albert82@googlemail.com>

WORKDIR /opt/younic

# EXPOSE 8080/tcp

RUN apk --update add git openssh && \
    rm -rf /var/lib/apt/lists/* && \
    rm /var/cache/apk/*

# ADD http://andre.webofferte.com/younic/younic.zip ./younic.zip 
ADD younic.zip ./younic.zip 

RUN	unzip younic.zip && \
	rm younic.zip

RUN git clone https://github.com/escv/younic-sample.git /opt/younic/cms-root

ENTRYPOINT ["./start.sh"]
