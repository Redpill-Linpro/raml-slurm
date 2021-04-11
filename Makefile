all: clean build extract

clean:
	rm -rf ./target
	rm -f ./app
	rm -f ./*.jar

build:
	podman build -t slurm-build .

extract:
	podman create --name slurm-extract slurm-build
	podman cp slurm-extract:/app ./
	podman rm slurm-extract
