# PakBzer — Object-Oriented Programming Project Report

**Project:** PakBzer — A Pakistani E-Commerce Web Application
**Language:** Java 17 · **Framework:** Spring Boot 3 · **Paradigm:** Object-Oriented Programming

---

## 1. Abstract

PakBzer is a full-stack online clothing store for the Pakistani market. It lets users browse five
category sections (Men, Women, Kids, Boy, Adult), search products, add items to a cart, register and
log in (including Google OAuth2), write reviews with star ratings, and pay through Stripe Checkout.

The purpose of the project is to demonstrate the four pillars of **Object-Oriented Programming
(OOP)** — *Encapsulation, Inheritance, Abstraction, and Polymorphism* — together with supporting
relationships such as *composition* and *association*, inside a realistic, layered application.

---

## 2. System Architecture

PakBzer follows a classic **layered (n-tier) architecture**. Each layer has a single responsibility
and talks only to the layer directly beneath it:

```
   Browser (HTML / Bootstrap)
        │  HTTP request
        ▼
 ┌──────────────────┐   Controllers (com.pakbzer.controller)
 │  Presentation    │   – receive requests, return Thymeleaf views
 └────────┬─────────┘
          ▼
 ┌──────────────────┐   Services (com.pakbzer.service)
 │  Business Logic  │   – cart rules, registration, reviews, Stripe
 └────────┬─────────┘
          ▼
 ┌──────────────────┐   Repositories (com.pakbzer.repository)
 │  Data Access     │   – Spring Data JPA
 └────────┬─────────┘
          ▼
 ┌──────────────────┐   Entities (com.pakbzer.model) ↔ H2 Database
 │  Domain Model    │
 └──────────────────┘
```

| Package | Responsibility |
|---|---|
| `model` | Domain objects (entities) — the heart of the OOP design |
| `repository` | Database access via Spring Data JPA interfaces |
| `service` | Business logic and rules |
| `controller` | Handle web requests, choose which page to show |
| `security` | Authentication, authorization, OAuth2 |
| `config` | Configuration and initial data seeding |

---

## 3. The Four Pillars of OOP in PakBzer

### 3.1 Encapsulation

**Definition:** Bundling data (fields) and the methods that operate on them inside a class, and
hiding the internal state behind `private` fields exposed only through controlled accessors.

**In the code:** Every entity keeps its fields `private` and exposes them through getters/setters.
More importantly, *business rules are encapsulated as methods* so callers cannot corrupt the state.

```java
// Product.java — the average rating is computed INSIDE the object.
private List<Review> reviews = new ArrayList<>();

public double getAverageRating() {
    if (reviews == null || reviews.isEmpty()) {
        return 0.0;
    }
    return reviews.stream().mapToInt(Review::getRating).average().orElse(0.0);
}

public boolean isInStock() {
    return stock > 0;
}
```

```java
// CartItem.java — the line total is derived, never stored or set from outside.
public BigDecimal getLineTotal() {
    return unitPrice.multiply(BigDecimal.valueOf(quantity));
}
```

The outside world never calculates a rating or a line total itself — it *asks the object*. This is
encapsulation: data + behaviour live together, and the internals are protected.

---

### 3.2 Inheritance

**Definition:** A class (subclass) acquires fields and behaviour from another class (superclass),
promoting code reuse.

**In the code:** Every persistent entity inherits identity and audit fields from the abstract
`BaseEntity`, so that code is written **once** and reused by `Product`, `User`, and `Review`.

```java
// BaseEntity.java  (superclass)
@MappedSuperclass
public abstract class BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist protected void onCreate() { createdAt = updatedAt = LocalDateTime.now(); }
    @PreUpdate  protected void onUpdate() { updatedAt = LocalDateTime.now(); }
    // getters ...
}
```

```java
// Product.java, User.java, Review.java  (subclasses)
public class Product extends BaseEntity { ... }
public class User    extends BaseEntity { ... }
public class Review  extends BaseEntity { ... }
```

Because `Product`, `User`, and `Review` all `extend BaseEntity`, they automatically gain `id`,
`createdAt`, `updatedAt`, and the timestamp logic — without copying a single line.

---

### 3.3 Abstraction

**Definition:** Exposing *what* an object does while hiding *how* it does it, using abstract classes
and interfaces.

**In the code:** Two forms of abstraction are used:

1. **Abstract class** — `BaseEntity` is declared `abstract`; you can never create a raw
   `new BaseEntity()`, it only exists to be specialised.

2. **Interface** — `ProductService` defines the catalogue *contract*; callers depend on the
   interface and never on the concrete implementation.

```java
// ProductService.java — the abstraction (the "what")
public interface ProductService {
    List<Product> findAll();
    Optional<Product> findById(Long id);
    List<Product> findByCategory(Category category);
    List<Product> search(String query);
    Product save(Product product);
}
```

Controllers simply declare `private final ProductService productService;`. They do not know or care
*how* products are fetched — that detail is hidden behind the interface.

---

### 3.4 Polymorphism

**Definition:** The ability of one interface/reference to take many forms — the actual method that
runs is decided by the real object type at runtime.

**In the code, two kinds of polymorphism appear:**

**(a) Interface polymorphism** — `ProductServiceImpl` *is-a* `ProductService`:

```java
// ProductServiceImpl.java
@Service
public class ProductServiceImpl implements ProductService {
    @Override public List<Product> search(String query) {
        if (query == null || query.isBlank()) return Collections.emptyList();
        String q = query.trim();
        return productRepository
            .findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseOrBrandContainingIgnoreCase(q, q, q);
    }
}
```

If we later wrote a `CachedProductServiceImpl` or `MongoProductServiceImpl`, the controllers would
**not change at all** — that is the power of polymorphism.

**(b) Method overriding (subtype polymorphism)** — we extend Spring's `OidcUserService` and override
`loadUser` to plug in our own behaviour (auto-creating a PakBzer account on first Google login):

```java
// CustomOidcUserService.java
public class CustomOidcUserService extends OidcUserService {
    @Override
    public OidcUser loadUser(OidcUserRequest req) throws OAuth2AuthenticationException {
        OidcUser oidcUser = super.loadUser(req);          // call the parent version
        userService.findOrCreateGoogleUser(oidcUser.getEmail(), oidcUser.getFullName());
        return oidcUser;
    }
}
```

Spring Security holds a reference of type `OidcUserService`, but at runtime our overridden version
runs — classic runtime polymorphism.

---

## 4. Supporting OOP Relationships

### 4.1 Composition / Aggregation
The `Cart` is *composed of* many `CartItem` objects and owns all cart logic:

```java
// Cart.java  (session-scoped) — "HAS-A" relationship
private final List<CartItem> items = new ArrayList<>();

public synchronized void add(Product product, int quantity) {
    for (CartItem item : items) {
        if (item.getProductId().equals(product.getId())) {  // already in cart?
            item.increaseQuantity(quantity);
            return;
        }
    }
    items.add(new CartItem(product.getId(), product.getName(),
                           product.getImageUrl(), product.getPrice(), quantity));
}

public BigDecimal getTotalPrice() {
    return items.stream().map(CartItem::getLineTotal).reduce(BigDecimal.ZERO, BigDecimal::add);
}
```

### 4.2 Association (one-to-many)
A `Product` is associated with many `Review`s (a one-to-many relationship mapped with JPA):

```java
// Product.java
@OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
private List<Review> reviews = new ArrayList<>();

// Review.java
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "product_id")
private Product product;
```

### 4.3 Enumerations
`Category` is an enum where **each constant carries its own data and behaviour** — itself a small
example of encapsulation:

```java
public enum Category {
    MEN("Men", "Men's Shalwar Kameez...", "https://...men.jpg"),
    WOMEN("Women", "Lawn Suits, Sarees...", "https://...women.jpg"),
    // ...
    private final String displayName, description, heroImage;
    Category(String d, String desc, String img) { ... }
    public String getDisplayName() { return displayName; }
}
```

---

## 5. File-by-File Explanation of the Java Code

### `model/` — Domain Layer
| File | What it does |
|---|---|
| **BaseEntity.java** | Abstract superclass. Holds `id`, `createdAt`, `updatedAt` and auto-sets timestamps via `@PrePersist`/`@PreUpdate`. Root of the inheritance hierarchy. |
| **Product.java** | A product for sale. Extends `BaseEntity`. Encapsulates `getAverageRating()`, `getReviewCount()`, `isInStock()`. Owns a list of `Review`s. |
| **User.java** | A registered customer. Stores email, name, BCrypt password (null for Google users), `Role`, and `provider` (LOCAL/GOOGLE). |
| **Review.java** | A 1–5 star review linked to a `Product`. |
| **CartItem.java** | One line in the cart (product id, name, price, quantity). Computes its own `getLineTotal()`. |
| **Category.java** | Enum of the five store sections, each with display name, description and hero image. |
| **Role.java** | Enum of user roles: `USER`, `ADMIN`. |

### `repository/` — Data Access Layer
Spring Data JPA generates the SQL automatically from method names.
| File | What it does |
|---|---|
| **ProductRepository** | `findByCategory(...)` and a search method across name/description/brand. |
| **UserRepository** | `findByEmail(...)`, `existsByEmail(...)`. |
| **ReviewRepository** | `findByProductIdOrderByCreatedAtDesc(...)`. |

### `service/` — Business Logic Layer
| File | What it does |
|---|---|
| **ProductService** (interface) | Abstraction for catalogue operations. |
| **ProductServiceImpl** | Concrete implementation; validates the search query, delegates to the repository. |
| **UserService** | `register(...)` (rejects duplicate emails, hashes password) and `findOrCreateGoogleUser(...)`. |
| **ReviewService** | Adds reviews, clamps rating to 1–5, links to the product. |
| **Cart** | Session-scoped shopping cart; encapsulates add/update/remove/total logic. |
| **StripeService** | Builds a Stripe Checkout session from the cart; reports whether Stripe is configured. |

### `controller/` — Presentation Layer
| File | Routes | What it does |
|---|---|---|
| **HomeController** | `/`, `/search` | Builds the home page (preview per category) and search results. |
| **ProductController** | `/category/{c}`, `/product/{id}` | Lists products by category, shows product detail + reviews. |
| **CartController** | `/cart`, `/cart/add`, `/cart/update`, `/cart/remove` | Cart operations. |
| **ReviewController** | `/review/add` | Saves a new review (author = logged-in user). |
| **AuthController** | `/login`, `/register` | Shows forms and registers new accounts. |
| **CheckoutController** | `/checkout`, `/checkout/success` | Starts Stripe payment or demo checkout, then clears the cart. |
| **GlobalControllerAdvice** | (all pages) | Injects category list and live cart count into every view. |

### `security/` — Security Layer
| File | What it does |
|---|---|
| **SecurityConfig** | Declares which URLs are public vs protected, configures form login, Google OAuth2 login, and logout. |
| **CustomUserDetailsService** | Loads a local user by email for password login. |
| **CustomOidcUserService** | Overrides Google login to provision a PakBzer account on first sign-in. |

### `config/` — Configuration Layer
| File | What it does |
|---|---|
| **PasswordConfig** | Provides the `BCryptPasswordEncoder` bean (kept separate to avoid a dependency cycle). |
| **StripeProperties** | Binds `stripe.*` settings from `application.properties`. |
| **DataSeeder** | On first run, seeds ~50 Pakistani products (10 per category) with matching images and sample reviews. |

---

## 6. How a Key Feature Works End-to-End — "Add to Cart"

```
1. User clicks "Add to Cart" on a product card  (Thymeleaf form → POST /cart/add)
2. CartController.add(productId, quantity)        ← Presentation layer
3.   → ProductService.findById(productId)         ← Business layer (interface, polymorphic)
4.       → ProductRepository.findById(...)        ← Data layer (Spring Data JPA)
5.   → cart.add(product, quantity)                ← Cart encapsulates the rule:
        if product already in cart → increase qty, else → add new CartItem
6. Redirect to /cart, which renders the cart total via cart.getTotalPrice()
```

Notice how each layer only calls the next one, and the *rules* (e.g. "merge duplicate items",
"compute total") live **inside** the `Cart` object — encapsulation in action.

---

## 7. Conclusion

PakBzer shows that OOP is not just theory — each pillar solves a real problem in the application:

- **Encapsulation** keeps business rules (ratings, totals, cart merging) safe inside objects.
- **Inheritance** removes duplication via the shared `BaseEntity`.
- **Abstraction** (interfaces + abstract classes) lets layers depend on contracts, not details.
- **Polymorphism** makes the system extensible and lets us override framework behaviour cleanly.

Together with composition and association, these principles produce a codebase that is modular,
reusable, testable, and easy to extend — for example, adding a new payment method or a new product
category requires changing very little existing code.

---

*Report generated for the PakBzer OOP project.*
