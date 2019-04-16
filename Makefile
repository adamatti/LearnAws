.DEFAULT_GOAL := help

.PHONY: help
help: ## show this help
	@grep -E '^[a-zA-Z_-]+:.*?## .*$$' $(MAKEFILE_LIST) | sort | awk 'BEGIN {FS = ":.*?## "}; {printf "\033[36m%-30s\033[0m %s\n", $$1, $$2}'

docker-start: ## start docker dependencies
	docker-compose up -d

docker-stop: ## stop docker containers
	docker-compose stop -t 0

docker-remove: docker-stop
	docker-compose rm -fv

docker-restart: docker-stop docker-start ## docker restart

docker-recreate: docker-remove docker-start