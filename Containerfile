FROM docker.io/clojure:openjdk-11-tools-deps-slim-buster AS builder
WORKDIR /usr/src/app
COPY . .
RUN clojure -X:depstar
FROM ghcr.io/graalvm/graalvm-ce:21.0.0.2 AS native
WORKDIR /usr/src/app
COPY --from=builder /usr/src/app/app.jar /usr/src/app/app.jar
COPY build-input.txt .
COPY helloworld.raml .
COPY *.sh .
RUN chmod +x configure.sh
RUN ./configure.sh
RUN ./compile.sh
FROM scratch
COPY --from=native /usr/src/app/app /
CMD ["/app"]