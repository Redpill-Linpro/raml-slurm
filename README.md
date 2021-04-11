# raml-slurm

raml slurper and model validator

simple clojure tool that reads in a file containing newline-delimited
URLs (so e.g. file:// or http:// works), slurps them and dispatches to the RAML validator.

## containerized
Comes containerized for building native-images, run to build and extract
the native image to the present directory.
```sh
make clean build extract
```

Note: the make commands use podman, replace with docker if you're using that instead.

## uberjar
To build an uberjar with clojure, install [clojure](https://clojure.org) (>= 1.10.x) on your system and execute
```
clj -X:depstar
```

## run

### native-image
```
$ ./app file-list.txt output-file.log
```
### uberjar
```
$ java -jar app.jar file-list.txt output-file.log
```
