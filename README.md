# raml-slurm

raml slurper and model validator

simple clojure tool that reads in a file containing newline-delimited
URLs (so e.g. file:// or http:// works), slurps them and dispatches to the RAML validator.


Comes containerized for building native-images, run to build and extract
the native image to the present directory.
```sh
make clean build extract
```
