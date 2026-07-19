# 🍔 Logiroute

![Java 21](https://img.shields.io/badge/Java-21-orange.svg)
![Spring Boot 4.1](https://img.shields.io/badge/Spring_Boot-4.1.0-brightgreen.svg)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue.svg)
![Angular](https://img.shields.io/badge/Angular-Frontend-red.svg)

**Logiroute** is a comprehensive, scalable food delivery and logistics platform. It is built entirely on a microservices architecture to handle the complex workflows of restaurant catalog management, user orders, and real-time courier logistics.

---

## 🏗 Architecture & Services

Logiroute is structured as a monorepo containing several independent microservices. 

| Service | Status | Description |
| :--- | :--- | :--- |
| **`gateway-service`** | 🚧 Planned | The API Gateway that handles incoming traffic, authentication, and routes requests to the appropriate backend microservices. |
| **`menu-service`** | 🟢 Active | Manages restaurant profiles, categories, menu items, and customization option groups. Fully backed by PostgreSQL with Flyway migrations. |
| **`order-service`** | 🚧 Planned | Handles the lifecycle of a food order, payment processing states, and restaurant acceptance. |
| **`delivery-service`** | 🚧 Planned | Handles the logistics engine: courier matching, route optimization, and real-time delivery tracking. |
| **`angular-frontend`**| 🚧 Planned | The customer and restaurant-facing web portal built with Angular. |

## 🛠 Tech Stack

- **Backend:** Java 21, Spring Boot 4.1, Spring Data JPA
- **Database:** PostgreSQL (with Flyway for schema migrations)
- **Mapping:** MapStruct
- **Testing:** JUnit 5, MockMvc, Testcontainers
- **Frontend:** Angular

## 🚀 Getting Started

### Prerequisites
- JDK 21+
- Docker (for Testcontainers and database hosting)
- Maven

### Running a Service (e.g., Menu Service)

Navigate to the specific service directory and use the Maven wrapper:

```bash
cd apps/menu-service
./mvnw spring-boot:run
```

To run the test suite for a service:
```bash
./mvnw test
```

## 🌿 Branching Strategy

We use a strict slash-based convention to keep our monorepo branches organized. All branches must follow this format:

`[service-name]/[type]/[description]`

**Examples:**
- `menu-service/feat/controllers`
- `order-service/fix/payment-bug`
- `gateway-service/chore/update-deps`