SHELL := /bin/bash

API_URL ?= http://localhost:8080

.PHONY: help backend frontend mock frontend-mock dev install frontend-install backend-test frontend-check backend-build e2e

help:
	@printf "Available targets:\n"
	@printf "  make backend         Start Spring Boot backend on port 8080\n"
	@printf "  make frontend        Start frontend against backend via VITE_API_URL=%s\n" "$(API_URL)"
	@printf "  make mock            Start Prism mock server on port 3001\n"
	@printf "  make frontend-mock   Start frontend with Prism mock via existing npm script\n"
	@printf "  make dev             Start backend in background and frontend in foreground\n"
	@printf "  make install         Install frontend npm dependencies\n"
	@printf "  make backend-build   Build backend sources\n"
	@printf "  make backend-test    Run backend tests\n"
	@printf "  make frontend-check  Run frontend type-check\n"
	@printf "  make e2e            Build frontend for backend API and run Playwright tests\n"

backend:
	cd backend && ./gradlew bootRun

frontend:
	cd frontend && VITE_API_URL=$(API_URL) npm run dev:client

mock:
	cd frontend && npm run mock

frontend-mock:
	cd frontend && npm run dev

dev:
	@set -euo pipefail; \
	trap 'kill 0' EXIT INT TERM; \
	$(MAKE) backend & \
	sleep 5; \
	$(MAKE) frontend

install:
	cd frontend && npm install

backend-build:
	cd backend && ./gradlew build

backend-test:
	cd backend && ./gradlew test

frontend-check:
	cd frontend && npm run type-check

e2e:
	cd frontend && npm run e2e
