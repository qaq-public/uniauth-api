TAG?=2.2.0
NAME:=uniauth-api
DOCKER_REPOSITORY:=blacklee123
DOCKER_IMAGE_NAME:=$(DOCKER_REPOSITORY)/$(NAME)
VERSION:=$(shell grep 'image: $(DOCKER_IMAGE_NAME):' kustomize/deployment.yaml | awk -F: '{ print $$3}')
EXTRA_RUN_ARGS?=


build:
	docker buildx build --platform linux/amd64 -f Dockerfile -t $(DOCKER_IMAGE_NAME):$(VERSION) .

build-graal:
	docker buildx build --platform linux/amd64 -f graal.Dockerfile -t $(DOCKER_IMAGE_NAME):$(VERSION) .

push:
	docker tag $(DOCKER_IMAGE_NAME):$(VERSION) $(DOCKER_IMAGE_NAME):latest
	docker push $(DOCKER_IMAGE_NAME):$(VERSION)
	docker push $(DOCKER_IMAGE_NAME):latest

test:
	docker rm -f ordercenter || true
	docker run -dp 23333:23333 --name=ordercenter $(DOCKER_IMAGE_NAME):$(VERSION)
	docker ps | grep $(DOCKER_IMAGE_NAME)
	curl http://127.0.0.1:23333/start-form/sensitivity

version-set:
	next="$(TAG)" && \
	current="$(VERSION)" && \
	sed -i '' "s/$(NAME):$$current/$(NAME):$$next/g" kustomize/deployment.yaml && \
    sed -i '' "s/<version>$$current<\/version>/<version>$$next<\/version>/g" pom.xml && \
	echo "Version $$next set in code and kustomize"

version-set-ubuntu:
	next="$(TAG)" && \
	current="$(VERSION)" && \
	sed -i "s/$(NAME):$$current/$(NAME):$$next/g" kustomize/deployment.yaml && \
	sed -i "s/$(NAME)-$$current/$(NAME)-$$next/g" Dockerfile && \
    sed -i "s/<version>$$current<\/version>/<version>$$next<\/version>/g" pom.xml && \
	echo "Version $$next set in code, dockfile and kustomize"

release:
	git tag $(VERSION)
	git push origin $(VERSION)

rollout: build push
	kubectl rollout restart -n qaq-dev deployment $(NAME)