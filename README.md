# ☁️ Langverse with Spring Boot Microservice

This repository contains a complete ecosystem of microservices developed in **Spring Boot**, using **Keycloak** for authentication, **MySQL** as a database, and **AWS S3** for media storage.
Each service is Dockerized and orchestrated using **Docker Compose** to facilitate deployment locally or on servers like AWS EC2.
---

## 🧱 Project Structure

```
SpringMicroservices/
│
├── microservice-gateway/        # API Gateway (Spring Cloud Gateway)
├── microservice-eureka/         # Service Discovery (Eureka Server)
├── microservice-config/         # Centralized Configuration Server
├── microservice-report/         # Reports and analytics
├── microservice-auth/           # Authentication with Keycloak
├── microservice-movie/          # Managing movies and series (with FFmpeg + S3)
├── microservice-music/          # Managing songs and albums (with S3)
├── microservice-speaking/       # Speaking practice (voice recognition)
├── microservice-notes/          # Personal notes and notebooks
└── docker-compose.yml           # Service orchestration
```

---

## 🔐 Authentication – Keycloak

The system uses **Keycloak** as an identity server to manage users, roles, and JWT tokens.
### Basic environment configuration

Variables defined in the `.env` file:

```bash
# ========= 🔐 KEYCLOAK ==========
KEYCLOAK_ADMIN_PASSWORD=admin
KEYCLOAK_USER_ADMIN=admin
ISSUER_URI=http://keycloak:8080/realms/microservices-realm
JWT_CLIENT_SECRET=SdIHsGS7S8S9r0dvt5ARgKRF2BhOahYp
JWT_RESOURCE_ID=microservice-client
TOKEN_URL=http://keycloak:8080/realms/microservices-realm/protocol/openid-connect/token
TOKEN_VALIDATION_URL=http://keycloak:8080/realms/microservices-realm/protocol/openid-connect/token/introspect
JWK_SET_URI=http://keycloak:8080/realms/microservices-realm/protocol/openid-connect/certs
KEYCLOAK_SERVER_URL=http://keycloak:8080
KEYCLOAK_REALM_NAME=microservices-realm
KEYCLOAK_REALM_MASTER=master
KEYCLOAK_ADMIN_CLI=admin-cli
```

---

## 🐬 Databases – MySQL

Each microservice has its own independent database.

```bash
# ========= 🧠 SPEAKING SERVICE ==========
SPEAKING_DB_URL=jdbc:mysql://mysql-speaking:3306/speakingdb?createDatabaseIfNotExist=true&allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC

# ========= 📒 NOTES SERVICE ==========
NOTES_DB_URL=jdbc:mysql://mysql-notes:3306/notedb?createDatabaseIfNotExist=true&allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC

# ========= 🎵 MUSIC SERVICE ==========
MUSIC_DB_URL=jdbc:mysql://mysql-music:3306/musicdb?createDatabaseIfNotExist=true&allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC

# ========= 🎬 MOVIE SERVICE ==========
MOVIE_DB_URL=jdbc:mysql://mysql-movie:3306/moviesdb?createDatabaseIfNotExist=true&allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC
```

Global username and password:

```bash
MYSQL_USER=root
MYSQL_PASSWORD=mysql
```

---

## ☁️ AWS S3 – Media Storage

Used by the **Movie** and **Music** services to store video, audio, and subtitle files.
> ⚠️ Do not share your AWS keys publicly.

```bash
# ========= ☁️ AWS ==========
AWS_ACCESS_KEY=<YOUR_ACCESS_KEY>
AWS_SECRET_KEY=<YOUR_SECRET_KEY>
AWS_REGION=<YOUR_REGION>
AWS_BUCKET=<YOUR_BUCKET_MOVIE>
AWS_BUCKET_MUSIC=<YOUR_BUCKET_MUSIC>
```

---

## 🧰Main Technologies

| Technology              | Main use                                    |
| ------------------------ | ------------------------------------------------- |
| **Spring Boot 3**        | Backend of each microservice                    |
| **Spring Cloud**         | Communication between microservices               |
| **Eureka Server**        | Service Discovery                      |
| **Spring Cloud Gateway** | Unified API Entry                         |
| **Keycloak**             |Authentication and authorization (OAuth2 + JWT)      |
| **MySQL**                | Data storage                         |
| **AWS S3**               | Multimedia files                              |
| **Docker Compose**       | Container orchestration                     |
| **FFmpeg**               | Audio/Video Processing (microservice-movie) |
| **JasperReports**        | Dynamic report generation in PDF/Excel formats |
| **Retrofit (Android)**   | Consuming APIs from the mobile app               |

---
### Installation
```bash
git clone https://github.com/Lenin-LG/langverse-backend.git
cd langverse-backend
```
---
## 📱 Android Frontend
The mobile client is available at:
👉 [Langverse Android App](https://github.com/Lenin-LG/langverse-frontend)

Built with **Kotlin**, uses **Retrofit**, **RecyclerView**, and integrates **Google Speech Recognition** for speaking practice.

---
## 🚀 Running with Docker Compose

### 1️⃣ Build the images

```bash
docker compose build
```

### 2️⃣ Lift all services

```bash
docker compose up -d
```

### 3️⃣ View logs (optional)

```bash
docker compose logs -f
```

---
## 📘 API Documentation
The backend includes an integrated Swagger UI for exploring endpoints and testing requests.

🔗 **Swagger URL:**  
http://localhost:8080/swagger-ui/index.html

All endpoints are documented with request/response models and authentication requirements.

---

## 🧾 Modules and Features

| Microservice        | Description                                | Puerto |
|---------------------| ------------------------------------------ |--------|
| **Gateway**         | API Request Entry                 | 8080   |
| **Eureka**          | Service registration                    | 8761   |
| **Config**          | Centralized configuration                 | 8888   |
| **Auth (Keycloak)** | Authentication                              | 8091   |
| **Movie**           | Uploading and managing movies/series     | 8092   |
| **Music**           | Uploading songs and albums           | 8093   |
| **Notes**           | User Notebooks and Notes| 8094   |
| **Speaking**        | Pronunciation practice and analysis             | 8095   |
| **Report**          | Report generation                    | 8096   |

---

## 🔄 Authentication Flow

1. The user logs into Keycloak.
2. Obtains a **JWT token**.
3. The token is sent in each request as `Authorization: Bearer <token>`.
4. Each microservice validates the token using the configured **issuer URI**.
5. The `userId` is automatically inferred from the JWT.

---

## 🔧 Safety recommendations

* Do not upload your `.env` to GitHub (add it to `.gitignore`).
* Create production-specific AWS and JWT keys.
* Use HTTPS and CORS configured on the Gateway.
* Limit access to Keycloak to internal networks or VPNs only.

---

## 📘 Upcoming Enhancements

* [ ] **Implement monitoring with Prometheus + Grafana**
- Enable tracking of microservice performance metrics in Docker containers.
- Integrate basic alerts with AWS CloudWatch or AlertManager.

* [ ] **Integrate asynchronous messaging with RabbitMQ or Kafka**
- Improve communication between microservices, especially for heavy processing tasks or events.

* [ ] **Add unit tests and continuous integration (CI/CD)**
- Configure GitHub Actions to automatically run tests and deploy to EC2.
- Upload Docker images to **AWS ECR** for faster and cleaner deployment.

* [ ] **Implement caching with Redis**
- Speed ​​up frequent queries (e.g., topics or phrases in the Speaking microservice).

* [ ] **Migrate database to AWS RDS**
- Replace the local database with a managed database with automatic backups.

* [ ] **Use AWS Secrets Manager**
- Protect project passwords, tokens, and private keys.

* [ ] **Configure domain and HTTPS with AWS Route 53 + Load Balancer**
- Assign a custom domain and enable HTTPS with automatic certificates.

* [ ] **Evaluate migration to AWS ECS or EKS**
- Replace `docker-compose` with an orchestration system for greater scalability.

---

## 🧑‍💻 Author

**Lenin Laura Garcia**
Backend Developer | Spring Boot + Kotlin + AWS
📍 Lima, Perú

---

