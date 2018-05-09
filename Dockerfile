# build it with: docker build -t escv/younic .
# run it with: docker run -p 127.0.0.1:8080:8080 -it escv/younic

FROM anapsix/alpine-java:latest
MAINTAINER Andre Albert <andre.albert82@googlemail.com>

WORKDIR /home/younic

RUN apk --update add git openssh && \
    rm -rf /var/lib/apt/lists/* && \
    rm /var/cache/apk/*

ADD https://github.com/escv/younic/releases/download/v0.2/younic.zip ./younic.zip 

RUN	unzip younic.zip && \
	rm younic.zip

RUN git clone https://github.com/escv/younic-sample.git /home/younic/cms-root

ENTRYPOINT ["./start.sh"]