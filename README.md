
# 📬 Notification Service (Java Spring Boot, Kafka, Redis, Docker)

A scalable, channel-agnostic **Notification Microservice** built with **Spring Boot**, **Kafka**, **Redis**, and **Docker**.
Designed to handle **email notifications**, **OTP generation/verification**, and **future communication channels** (SMS, WhatsApp, Push, etc.).

---

# 🚀 Features

### ✅ **1. Email Notification (Async)**

* REST API for sending email notifications.
* Enqueued into **Kafka**, processed asynchronously by consumers.
* SMTP integration (using Mailhog in development).

### ✅ **2. OTP System (Secure & Stateless)**

* OTP generation with configurable TTL, length, and attempts.
* Stored in **Redis** (no database needed).
* Supports multiple OTP purposes:

  * `PASSWORD_RESET`
  * `EMAIL_VERIFICATION`
* OTP verification endpoint with:

  * Attempt tracking
  * Expiry validation
  * Auto-deletion on success/failure

### ✅ **3. Channel-Agnostic Architecture**

Built using the **Strategy Pattern**:

* `NotificationDispatcher` routes messages to the correct channel.
* `NotificationChannel` interface for defining channels.
* Implementations:

  * `EmailNotificationChannel` (ready)
  * Future support for:

    * SMS (Twilio, AWS SNS)
    * WhatsApp (Meta API / Twilio)
    * Push Notifications (FCM, APN)

### ✅ **4. Kafka-Based Delivery Pipeline**

* Producers publish notification messages.
* Consumers listen and perform actual delivery.
* Easy to add:

  * Retries
  * Dead-letter topics
  * Failure alerts

---

# 🧩 Architecture

```
User Management Service
         |
         | REST API calls (OTP request/verify)
         v
Notification Service (This Project)
         |
         | dispatch()
         v
Notification Channels (Email, SMS, WhatsApp, ...)
         |
         | Kafka producer
         v
Kafka Topic (notification-email-topic)
         |
         | consumer
         v
Email Sender → SMTP → Inbox/Mailhog
```

---

# 🗄 Technology Stack

### **Backend**

* Java 17
* Spring Boot 3.x
* Spring Web
* Spring Validation
* Spring Mail
* Spring Kafka
* Spring Data Redis

### **Infrastructure**

* Kafka (KRaft mode – no ZooKeeper)
* Redis
* Mailhog (local email testing)
* Docker & Docker Compose

### **Design Patterns**

* Strategy Pattern (Notification Channels)
* Dispatcher Pattern
* Asynchronous event-driven architecture

---

# 📡 APIs

## **1. Send Email Notification**

`POST /api/notifications/email`

```json
{
  "to": "user@example.com",
  "subject": "Hello!",
  "body": "This is a test email."
}
```

## **2. Request OTP**

`POST /api/otp/request`

```json
{
  "userId": "12345",
  "purpose": "PASSWORD_RESET",
  "email": "user@example.com",
  "channelType": "EMAIL"  // optional (defaults to EMAIL)
}
```

## **3. Verify OTP**

`POST /api/otp/verify`

```json
{
  "userId": "12345",
  "purpose": "PASSWORD_RESET",
  "otp": "123456"
}
```

**Possible Responses:**

* `VALID`
* `INVALID`
* `EXPIRED_OR_NOT_FOUND`
* `TOO_MANY_ATTEMPTS`

---

# 🔐 Security (Planned Enhancements)

### 1. **JWT Validation**

* Inter-service communication secured using JWT
* User management service signs tokens
* Notification service validates signature & claims

### 2. **IP Whitelisting**

* Only allow access from:

  * API Gateway
  * Internal services
  * Dev tools (optional)

### 3. **API Keys / Rate Limiting**

* Especially useful for notification bursts
* Redis can be reused for tracking rate limits

---

# 🔧 Configuration (Planned)

Configurable through `application.yml` or environment variables:

* OTP length
* OTP TTL (minutes)
* Max OTP attempts
* Max OTP requests per user per day
* Email templates (folder-based or Thymeleaf)
* Kafka retry policies
* Channel-specific configuration (SMS, WhatsApp)

---

# 🎨 Email Templates (Planned)

Move from plain text → real HTML templates.

**Goals:**

* Thymeleaf / FreeMarker support
* Template variables:

  * `{otp}`
  * `{username}`
  * `{purpose}`
  * `{expiry}`
* Custom branding for production

---

# ☸ Resilience & Observability (Planned)

### **Kafka Resilience**

* Retries with backoff
* Dead-letter topic: `notification-email-dlt`
* Alerts for delivery failures

### **Email Resilience**

* Auto retry for SMTP downtime
* Fallback SMTP provider (optional)

### **Monitoring**

* Actuator health:

  * Kafka connection
  * Redis connection
  * SMTP connection
* Prometheus metrics (optional)
* Distributed tracing with OpenTelemetry

---

# 📚 Documentation (Planned)

Enable auto-generated API docs using:

```
springdoc-openapi-starter-webmvc-ui
```

Then Swagger UI will be available at:

```
/swagger-ui.html
```

---

# 🐳 Docker Setup

### Current containers:

| Service              | Purpose                 |
| -------------------- | ----------------------- |
| Kafka (KRaft)        | Notification queue      |
| Redis                | OTP store               |
| Mailhog              | Local email inbox       |
| Notification Service | Spring Boot application |

A sample `docker-compose.yml` will include:

* Kafka KRaft
* Redis
* Mailhog
* Notification service image
* Networks & volumes

---

# 🧪 Testing Strategy (Planned)

### **Unit Tests**

* OTP generation
* Redis logic
* Attempt increment logic
* Dispatcher and channel routing

### **Integration Tests**

* Embedded Kafka (spring-kafka-test)
* Testcontainers:

  * Redis container
  * Kafka container
  * Mailhog container

### **End-to-End Tests**

* Start all services via docker-compose
* Call `/otp/request` → observe email in Mailhog
* Call `/otp/verify` → expect `VALID`

---

# 🧭 Roadmap

* [ ] Integrate with User Management Service
* [ ] Add JWT validation
* [ ] Add IP whitelisting
* [ ] Add OpenAPI/Swagger documentation
* [ ] Add HTML templates for emails
* [ ] Add SMS channel
* [ ] Add WhatsApp channel
* [ ] Add Kafka DLQ + retry policies
* [ ] Add rate limiting for OTP
* [ ] Add environment-specific configuration
* [ ] Add monitoring & metrics
* [ ] Add full E2E tests via Testcontainers

---

# 🏁 Summary

This Notification Service is designed to be:

* **Scalable** – Kafka + Redis enables high throughput
* **Extendable** – Add any future channel easily
* **Secure** – Upcoming JWT + IP whitelisting
* **Maintainable** – Clear separation of concerns
* **Production-ready** – Resilience patterns planned
