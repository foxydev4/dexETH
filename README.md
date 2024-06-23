# BoarDFI API

## BoarDFI API - Local Development

To develop this application locally you need PostgreSQL instance on your localhost. To achieve this you should
use `docker/docker-compose.yml` file, for spinning up database instance locally.

### Prerequisites

1. Docker installed locally
2. That's all lol

You can do this by executing this command from your terminal.

**IMPORTANT: You must be in root directory of this project - not in /docker!**

```shell
docker compose -f docker/docker-compose.yml up -d database
```

## Other Apps - Local Development

To develop some other application that is using BoarDFI API, you need the whole stack running (database + boardfi-api)
on your localhost.

### Prerequisites

1. Docker installed locally
2. Once again - that's all. I'm Top G - I know 💸

To spin up whole stack locally, use will need to follow these steps:

1. Execute command for building the backend Docker image - details below.
2. Create file `secrets.env` inside `docker/` directory - **IMPORTANT: DO NOT COMMIT THIS FILE TO GIT REPOSITORY**
3. Based on `secrets.env.exmaple` create the same environment variables but assign correct values (add missing API keys)
4. Execute command for starting up the whole stack - details below.

**IMPORTANT: You must be in root directory of this project - not in /docker!**

### First Command - build backend Docker image

```shell
./gradlew bootBuildImage
```

### Second Command - start whole stack using docker compose

```shell
docker compose -f docker/docker-compose.yml up -d
```

## PORTS

- PostgreSQL database is available on port `5432`
- BoarDFI API is available on port `8080`

---

Happy coding 🚀
