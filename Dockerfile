# AlpineLinux with a glibc-2.27-r0 and Oracle Java 8
FROM anapsix/alpine-java:latest
MAINTAINER Andre Albert <tandre.albert82@googlemail.com>

WORKDIR /home/younic
EXPOSE 8080/tcp

RUN apk --update add git openssh && \
    rm -rf /var/lib/apt/lists/* && \
    rm /var/cache/apk/*

ADD http://andre.webofferte.com/younic/younic.zip ./younic.zip 

RUN	unzip younic.zip && \
	rm younic.zip

RUN git clone https://github.com/escv/younic-sample.git /home/younic/cms-root

ENTRYPOINT ["./start.sh"]