# 📬 Notification Service

**Java Spring Boot • Kafka • Redis • Docker**
A scalable, channel-agnostic notification microservice for delivering emails, OTPs, and future channels like SMS, WhatsApp, and Push notifications.

---

# 🚀 Overview

This Notification Service handles:

* **Email notifications** (async delivery via Kafka)
* **OTP generation & verification** using Redis
* **Channel-agnostic architecture** (Strategy Pattern)
* **Event-driven processing** (Kafka producers & consumers)
* **Future expansion** (add SMS, WhatsApp, Push easily)

It runs fully containerized using **Docker** and supports both local development and multi-service architectures.

---

# ✨ Features

### ✅ 1. Email Notifications (Asynchronous)

* REST endpoint to send email notifications
* Published to Kafka → processed by consumer
* SMTP integration using Mailhog (for local testing)
* Fully decoupled: caller doesn’t wait for delivery

---

### ✅ 2. OTP System (Secure & Stateless)

* Generates OTP with configurable:

  * Length
  * TTL
  * Maximum attempts
* Stored in Redis (no database required)
* Supports multiple OTP purposes:

  * `PASSWORD_RESET`
  * `EMAIL_VERIFICATION`
* Verification includes:

  * Attempt tracking
  * Expiry validation
  * Auto-delete on success/failure

---

### ✅ 3. Channel-Agnostic Architecture

Built using **Strategy Pattern**:

```
NotificationDispatcher → NotificationChannel → Channel Implementations
```

Implemented:

* Email channel

Future Ready:

* SMS (Twilio / AWS SNS)
* WhatsApp (Meta / Twilio)
* Push Notifications (FCM / APNs)

---

### ✅ 4. Kafka-Based Delivery Pipeline

* Producers publish events
* Consumers process and deliver notifications
* Supports:

  * Retries
  * Dead-letter queues (future)
  * Failure alerts

---

# 🧩 High-Level Architecture

```
   Client / Other Services
               |
               v
   REST → Notification API
               |
               v
      Notification Dispatcher
               |
               v
         Notification Channels
               |
               v
           Kafka Topic
               |
               v
     Kafka Consumer → Email Sender
               |
               v
   SMTP → Mailhog → Inbox
```

---

# 🗄 Tech Stack

### Backend

* Java 17
* Spring Boot 3
* Spring Kafka
* Spring Mail
* Spring Web
* Spring Validation
* Spring Data Redis

### Infrastructure

* Kafka (KRaft mode — no ZooKeeper)
* Redis
* Mailhog
* Docker & Docker Compose

### Design Patterns

* Strategy Pattern
* Dispatcher Pattern
* Event-driven architecture

---

# 🐳 Running the Service (Docker)

This project includes a `docker-compose.yml` that starts:

| Service              | Purpose                   |
| -------------------- | ------------------------- |
| Notification Service | Your Spring Boot app      |
| Kafka (KRaft)        | Notification queue        |
| Redis                | OTP storage               |
| Mailhog              | Local SMTP server & inbox |

## 1️⃣ Clone the repository

```bash
git clone https://github.com/sanah-saleem/NotificationService.git
cd NotificationService
```

## 2️⃣ Start all services

```bash
docker compose up
```

Or detached mode:

```bash
docker compose up -d
```

### Once running:

* Notification API → `http://localhost:8082`
* Mailhog UI → `http://localhost:8025`
* SMTP server → `localhost:1025`

---

# 📡 API Endpoints

## 1. Send Email Notification

```
POST /api/notifications/email
```

### Request Body

```json
{
  "to": "user@example.com",
  "subject": "Hello!",
  "body": "This is a test email."
}
```

---

## 2. Request OTP

```
POST /api/otp/request
```

### Request Body

```json
{
  "userId": "12345",
  "purpose": "PASSWORD_RESET",
  "email": "user@example.com",
  "channelType": "EMAIL"
}
```

---

## 3. Verify OTP

```
POST /api/otp/verify
```

### Request Body

```json
{
  "userId": "12345",
  "purpose": "PASSWORD_RESET",
  "otp": "123456"
}
```

### Responses

* `VALID`
* `INVALID`
* `EXPIRED_OR_NOT_FOUND`
* `TOO_MANY_ATTEMPTS`

---

# 🔧 Configuration

All settings are configurable via `application.yml` or environment variables:

| Setting                        | Purpose          |
| ------------------------------ | ---------------- |
| `notification.otp.length`      | OTP digit length |
| `notification.otp.ttlSeconds`  | Expiry time      |
| `notification.otp.maxAttempts` | Max attempts     |
| `spring.kafka.*`               | Kafka settings   |
| `spring.data.redis.*`          | Redis settings   |
| `spring.mail.*`                | SMTP settings    |

---

# 🔐 Security (Planned Enhancements)

* JWT validation for inter-service communication
* IP whitelisting for internal-only access
* Rate limiting (Redis-based)
* API keys for external use

---

# 📚 Documentation (Planned)

* Auto-generated API docs with Swagger/OpenAPI
* `/swagger-ui.html` endpoint

---

# 🎨 Email Templates (Planned)

Move from plain text → HTML templates using:

* Thymeleaf
* FreeMarker

---

# ☸ Resilience & Monitoring (Planned)

* Kafka retry policies
* Dead-letter topics
* SMTP fallback provider
* Actuator health checks
* Prometheus / Grafana metrics
* Distributed tracing with OpenTelemetry

---

# 🧪 Testing Strategy (Planned)

* Unit tests for OTP logic, Redis operations
* Integration tests with Embedded Kafka
* Testcontainers:

  * Redis
  * Kafka
  * Mailhog
* End-to-end tests using full docker-compose

---

# 🧭 Roadmap

* Integrate fully with User Management service
* Add SMS + WhatsApp channels
* Add HTML email templates
* Add rate limiting
* Add OpenAPI docs
* Add monitoring & tracing
* Add DLQ support

---

# 🏁 Summary

This Notification Service is:

✔ Scalable — Kafka + Redis
✔ Extendable — new channels plug in easily
✔ Maintainable — clean architecture + patterns
✔ Production-ready — event-driven, async, containerized
✔ Recruiter-friendly — easy to run and test
