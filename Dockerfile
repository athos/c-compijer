FROM clojure:tools-deps-alpine
MAINTAINER Shogo Ohta

RUN apk add --no-cache musl-dev gcc make

WORKDIR /root