<div align="center">

<img src="https://readme-typing-svg.demolab.com?font=Poppins&weight=700&size=40&duration=3000&pause=800&color=01411C&center=true&vCenter=true&width=800&height=80&lines=PakBzer;Pakistan's+Online+Fashion+Store;Built+with+Java+%2B+Spring+Boot+OOP" alt="PakBzer" />

<p>
  <img src="https://img.shields.io/badge/Java-17%2B-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white" />
  <img src="https://img.shields.io/badge/Spring_Boot-3.2.5-6DB33F?style=for-the-badge&logo=springboot&logoColor=white" />
  <img src="https://img.shields.io/badge/Thymeleaf-005F0F?style=for-the-badge&logo=thymeleaf&logoColor=white" />
  <img src="https://img.shields.io/badge/H2_Database-1021FF?style=for-the-badge&logo=h2&logoColor=white" />
  <img src="https://img.shields.io/badge/Stripe-635BFF?style=for-the-badge&logo=stripe&logoColor=white" />
  <img src="https://img.shields.io/badge/OAuth2-Google-4285F4?style=for-the-badge&logo=google&logoColor=white" />
</p>

<p>
  <img src="https://img.shields.io/badge/OOP-Encapsulation%20%7C%20Inheritance%20%7C%20Abstraction%20%7C%20Polymorphism-01411C?style=flat-square" />
  <img src="https://img.shields.io/badge/Made_in-Pakistan-046A38?style=flat-square" />
</p>

![divider](https://raw.githubusercontent.com/Trilokia/Trilokia/379277808c61ef204768a61bbc5d25bc7798ccf1/bottom_header.svg)

</div>

## 🛍️ About PakBzer

> **PakBzer** is a full-stack e-commerce web application for the Pakistani market, built to demonstrate **Object-Oriented Programming fundamentals in Java** using **Spring Boot**. Shop traditional and modern fashion across five sections, sign in, add to cart, leave reviews, and pay securely with Stripe.

<div align="center">

| 🧕 Women | 👔 Men | 🧒 Kids | 👦 Boy | 🤵 Adult |
|:---:|:---:|:---:|:---:|:---:|
| Lawn • Saree • Lehenga | Kurta • Sherwani • Waistcoat | Frock • Hoodie • Uniform | Cargo • Jacket • Tracksuit | Suit • Gown • Watch |

</div>

## ✨ Features

- 🗂️ **Category sections** — Men, Women, Kids, Boy, Adult (each with ~10 products + images)
- 🔍 **Search** across product name, description & brand
- 🛒 **Add to cart** with quantity update & remove (session cart)
- ⭐ **Reviews & star ratings** — average rating computed per product
- 🔐 **Authentication** — create account, sign in, sign out (BCrypt-hashed passwords)
- 🟦 **Google OAuth2 login**
- 💳 **Stripe Checkout** payments (PKR) with safe demo-mode fallback
- 📱 **Responsive UI** — Bootstrap 5, green/white Pakistani theme

## 🧱 Tech Stack

<div align="center">

| Layer | Technology |
|---|---|
| **Language** | Java 17 |
| **Backend** | Spring Boot 3 (Web, Security, OAuth2, Data JPA, Validation) |
| **Database** | H2 (file-based, auto-seeded) |
| **Payments** | Stripe Java SDK |
| **Frontend** | Thymeleaf + Bootstrap 5 |
| **Build** | Maven |

</div>

## 🧠 OOP Concepts Demonstrated

```text
                          ┌────────────────────┐
                          │  «abstract»        │
                          │   BaseEntity       │   id, createdAt, updatedAt
                          └─────────┬──────────┘
              ┌─────────────────────┼─────────────────────┐
              │                     │                     │
        ┌─────▼─────┐         ┌─────▼─────┐         ┌─────▼─────┐
        │  Product  │         │   User    │         │  Review   │
        └─────┬─────┘         └───────────┘         └───────────┘
              │ 1
              │ owns *                ProductService (interface)
        ┌─────▼─────┐                        ▲  polymorphism
        │  Review   │                        │
        └───────────┘                ProductServiceImpl
```

| Pillar | Where it lives |
|---|---|
| **Inheritance** | `Product`, `User`, `Review` all extend abstract `BaseEntity` |
| **Abstraction** | `BaseEntity` (abstract class) + `ProductService` (interface) |
| **Encapsulation** | private fields + getters/setters; logic like `Product.getAverageRating()`, `Cart.add()` |
| **Polymorphism** | `ProductService` → `ProductServiceImpl`; `CustomOidcUserService extends OidcUserService` (override) |
| **Composition** | `Cart` aggregates `CartItem`s; `Product` owns many `Review`s |

> 📄 A full, detailed walkthrough of every class and how OOP is applied lives in **[REPORT.md](REPORT.md)**.

## 🚀 Getting Started

### Prerequisites
- **JDK 17+**
- **Maven 3.9+**

### Run
```bash
mvn spring-boot:run
```
Open 👉 **http://localhost:8080**

H2 console: **http://localhost:8080/h2-console** (JDBC URL `jdbc:h2:file:./data/pakbzer`, user `sa`, no password).

### 🔑 Configure your keys
Edit `src/main/resources/application.properties` for local development, or set the environment variables listed in `env.example`:

```properties
# Stripe
stripe.secret-key=sk_test_xxxxxxxx
stripe.publishable-key=pk_test_xxxxxxxx

# Google OAuth2  (redirect URI: http://localhost:8080/login/oauth2/code/google)
spring.security.oauth2.client.registration.google.client-id=YOUR_CLIENT_ID
spring.security.oauth2.client.registration.google.client-secret=YOUR_CLIENT_SECRET
```
> 💡 No Stripe key? Checkout runs in **demo mode** (order confirmed, no real charge). Test card: `4242 4242 4242 4242`.

## 🌐 Deployment

PakBzer is a **single Spring Boot application**. The frontend is rendered by Thymeleaf inside the same backend, so you do **not** need a separate frontend deployment.

### Recommended deployment shape
- Deploy the whole app to a Java-capable host such as Render, Railway, Fly.io, or Google Cloud Run.
- Set environment variables for Google OAuth2, Stripe, and the public base URL.
- Use the deployed domain as the Google redirect URI.

### Required environment variables
- `GOOGLE_CLIENT_ID`
- `GOOGLE_CLIENT_SECRET`
- `GOOGLE_REDIRECT_URI`
- `APP_BASE_URL`
- `STRIPE_SECRET_KEY`
- `STRIPE_PUBLISHABLE_KEY`
- `STRIPE_CURRENCY`
- `PORT` when your host assigns one

### Google OAuth2 redirect URI
- Local: `http://localhost:8080/login/oauth2/code/google`
- Deploy: `https://your-domain.com/login/oauth2/code/google`

### Important note about Vercel
Vercel is optimized around frontend frameworks and serverless functions. Because PakBzer uses Thymeleaf server-side rendering, the simplest deployment is to host the Spring Boot app as one service rather than splitting frontend and backend.

## 🗂️ Project Structure

```
src/main/java/com/pakbzer
├── PakBzerApplication.java        # entry point
├── model/                         # domain entities (OOP core)
│   ├── BaseEntity.java            # abstract base class  ← inheritance root
│   ├── Category.java              # enum: Men/Women/Kids/Boy/Adult
│   ├── Product.java  User.java  Review.java  CartItem.java  Role.java
├── repository/                    # Spring Data JPA repositories
├── service/                       # business logic (interface + impl)
│   ├── ProductService.java  impl/ProductServiceImpl.java
│   ├── UserService.java  ReviewService.java  StripeService.java  Cart.java
├── security/                      # auth, OAuth2, security config
├── controller/                    # Spring MVC controllers
└── config/                        # Stripe props, password config, data seeder
src/main/resources
├── templates/  (Thymeleaf views)
├── static/css/styles.css
└── application.properties
```

<div align="center">

![divider](https://raw.githubusercontent.com/Trilokia/Trilokia/379277808c61ef204768a61bbc5d25bc7798ccf1/bottom_header.svg)

**🇵🇰 PakBzer — Made in Pakistan** • © 2026

</div>
