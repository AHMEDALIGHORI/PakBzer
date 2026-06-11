#!/usr/bin/env python3
"""Generate PakBzer line-by-line code explanation Word document."""
import html
import os
import re
from pathlib import Path

ROOT = Path(__file__).parent
OUT = ROOT / "PakBzer_Complete_Code_Explanation.doc"

FILES = [
    "pom.xml",
    "src/main/java/com/pakbzer/PakBzerApplication.java",
    "src/main/java/com/pakbzer/model/BaseEntity.java",
    "src/main/java/com/pakbzer/model/Role.java",
    "src/main/java/com/pakbzer/model/Category.java",
    "src/main/java/com/pakbzer/model/User.java",
    "src/main/java/com/pakbzer/model/Product.java",
    "src/main/java/com/pakbzer/model/Review.java",
    "src/main/java/com/pakbzer/model/CartItem.java",
    "src/main/java/com/pakbzer/repository/ProductRepository.java",
    "src/main/java/com/pakbzer/repository/UserRepository.java",
    "src/main/java/com/pakbzer/repository/ReviewRepository.java",
    "src/main/java/com/pakbzer/service/ProductService.java",
    "src/main/java/com/pakbzer/service/impl/ProductServiceImpl.java",
    "src/main/java/com/pakbzer/service/UserService.java",
    "src/main/java/com/pakbzer/service/ReviewService.java",
    "src/main/java/com/pakbzer/service/Cart.java",
    "src/main/java/com/pakbzer/service/StripeService.java",
    "src/main/java/com/pakbzer/config/StripeProperties.java",
    "src/main/java/com/pakbzer/config/PasswordConfig.java",
    "src/main/java/com/pakbzer/config/DataSeeder.java",
    "src/main/java/com/pakbzer/security/SecurityConfig.java",
    "src/main/java/com/pakbzer/security/CustomUserDetailsService.java",
    "src/main/java/com/pakbzer/security/CustomOidcUserService.java",
    "src/main/java/com/pakbzer/controller/GlobalControllerAdvice.java",
    "src/main/java/com/pakbzer/controller/HomeController.java",
    "src/main/java/com/pakbzer/controller/ProductController.java",
    "src/main/java/com/pakbzer/controller/CartController.java",
    "src/main/java/com/pakbzer/controller/AuthController.java",
    "src/main/java/com/pakbzer/controller/ReviewController.java",
    "src/main/java/com/pakbzer/controller/CheckoutController.java",
    "src/main/resources/application.properties",
    "src/main/resources/static/css/styles.css",
    "src/main/resources/templates/fragments/layout.html",
    "src/main/resources/templates/fragments/components.html",
    "src/main/resources/templates/index.html",
    "src/main/resources/templates/login.html",
    "src/main/resources/templates/register.html",
    "src/main/resources/templates/search.html",
    "src/main/resources/templates/category.html",
    "src/main/resources/templates/product.html",
    "src/main/resources/templates/cart.html",
    "src/main/resources/templates/checkout-success.html",
]

IMPORT_EXPLAIN = {
    "org.springframework.boot.SpringApplication": "Starts the embedded Spring Boot application.",
    "org.springframework.boot.autoconfigure.SpringBootApplication": "Enables auto-configuration, component scan, and marks the main class.",
    "jakarta.persistence": "JPA annotations for database mapping (entities, relationships).",
    "org.springframework.stereotype": "Spring stereotype annotations (@Service, @Component, @Controller).",
    "org.springframework.web.bind.annotation": "Spring MVC annotations for HTTP routes and request parameters.",
    "org.springframework.security": "Spring Security for authentication and authorization.",
    "org.springframework.data.jpa.repository": "Spring Data JPA repository interfaces.",
    "com.stripe": "Stripe payment SDK for checkout sessions.",
    "java.math.BigDecimal": "Precise decimal type used for money (prices) to avoid floating-point errors.",
    "java.time.LocalDateTime": "Date-time without timezone for created/updated timestamps.",
    "java.io.Serializable": "Allows objects to be stored in HTTP session (cart items).",
    "java.util": "Java collections (List, ArrayList, etc.).",
    "org.thymeleaf": "Server-side HTML template engine used for views.",
}

ANNOTATION_EXPLAIN = {
    "@SpringBootApplication": "Main Spring Boot entry: enables auto-config + component scanning in com.pakbzer.",
    "@Entity": "Marks class as a JPA database table entity.",
    "@Table": "Specifies the SQL table name for this entity.",
    "@MappedSuperclass": "Parent class whose fields are inherited by child entities but is not a table itself.",
    "@Id": "Primary key column in the database.",
    "@GeneratedValue": "Auto-generates primary key values (IDENTITY = database auto-increment).",
    "@Column": "Maps a field to a database column; can set nullable, length, unique.",
    "@OneToMany": "One product has many reviews; JPA manages the relationship.",
    "@ManyToOne": "Many reviews belong to one product.",
    "@JoinColumn": "Foreign key column name linking review to product.",
    "@Enumerated": "Stores enum as STRING (e.g. MEN) instead of number in DB.",
    "@PrePersist": "Method runs automatically before a new row is inserted.",
    "@PreUpdate": "Method runs automatically before an existing row is updated.",
    "@Service": "Spring bean for business logic layer.",
    "@Component": "Generic Spring-managed bean.",
    "@Controller": "Spring MVC controller: handles HTTP requests and returns views.",
    "@Configuration": "Class defines Spring @Bean configuration.",
    "@Bean": "Method produces a bean managed by Spring container.",
    "@Autowired": "Inject dependency (constructor injection preferred in this project).",
    "@ControllerAdvice": "Applies @ModelAttribute methods to all controllers globally.",
    "@GetMapping": "Handles HTTP GET request at the given URL path.",
    "@PostMapping": "Handles HTTP POST request (forms, add-to-cart, login).",
    "@PathVariable": "Reads a value from the URL path (e.g. /product/{id}).",
    "@RequestParam": "Reads a value from query string or form field.",
    "@ModelAttribute": "Adds a named value to the view model for every page.",
    "@Component": "Registers class as Spring component (DataSeeder runs at startup).",
    "@Scope": "Bean lifecycle: SESSION = one cart per browser session.",
    "@ConfigurationProperties": "Binds application.properties stripe.* keys to fields.",
    "@Override": "Implements or overrides a method from interface/superclass (polymorphism).",
}


def explain_line(line: str, filepath: str, line_no: int, all_lines: list) -> str:
    s = line.strip()
    if not s:
        return "Blank line — separates logical blocks for readability."

    # XML pom
    if filepath.endswith("pom.xml"):
        if s.startswith("<?xml"):
            return "XML declaration — required header for Maven POM file."
        if "<parent>" in s or "spring-boot-starter-parent" in s:
            return "Inherits Spring Boot parent POM — provides default dependency versions and plugin config."
        if "spring-boot-starter-web" in s:
            return "Dependency: Spring MVC + embedded Tomcat for REST and web pages."
        if "thymeleaf" in s:
            return "Dependency: Thymeleaf template engine for HTML views."
        if "springsecurity6" in s or "spring-security" in s:
            return "Dependency: Spring Security for login, OAuth2, and access control."
        if "oauth2" in s:
            return "Dependency: OAuth2 client support for Google sign-in."
        if "data-jpa" in s:
            return "Dependency: JPA/Hibernate for database entities and repositories."
        if "h2database" in s or "<artifactId>h2</artifactId>" in s:
            return "Dependency: H2 embedded database — no MySQL install needed."
        if "stripe-java" in s:
            return "Dependency: Stripe Java SDK for payment checkout."
        if "java.version" in s:
            return "Sets Java 17 as the compile and runtime version."
        if "spring-boot-maven-plugin" in s:
            return "Maven plugin to package and run the app with mvn spring-boot:run."
        if "<groupId>" in s or "<artifactId>" in s or "<version>" in s:
            return "Maven coordinates identifying the project or dependency."
        return "Maven POM element — defines project build and libraries."

    # application.properties
    if filepath.endswith("application.properties"):
        if s.startswith("#"):
            return "Comment explaining the configuration section below."
        if "server.port" in s:
            return "Runs the web server on port 8080 (http://localhost:8080)."
        if "datasource.url" in s:
            return "H2 file database path — data saved in ./data/pakbzer on disk."
        if "ddl-auto=update" in s:
            return "Hibernate auto-creates/updates tables from Java entities."
        if "open-in-view" in s:
            return "Keeps DB session open during page render so product reviews load in templates."
        if "oauth2" in s or "google" in s:
            return "Google OAuth2 client ID/secret for 'Continue with Google' login."
        if "stripe" in s:
            return "Stripe API keys and currency (PKR) for checkout payments."
        if "thymeleaf.cache" in s:
            return "Disables template cache in dev so HTML changes show without restart."
        return "Spring Boot configuration property — controls app behavior at startup."

    # CSS
    if filepath.endswith(".css"):
        if s.startswith("/*") or s.startswith("*"):
            return "CSS comment."
        if s.startswith(":root"):
            return "CSS variables for Pakistani green/gold brand colors used site-wide."
        if "{" in s and ":" in s:
            return "CSS style rule — controls layout, colors, and appearance of UI elements."
        if s.endswith("{"):
            return "CSS selector — targets HTML elements/classes listed before {."
        return "CSS styling for PakBzer responsive e-commerce UI."

    # HTML templates
    if filepath.endswith(".html"):
        if s.startswith("<!DOCTYPE"):
            return "HTML5 document type declaration."
        if "xmlns:th" in s:
            return "Thymeleaf namespace — enables th:* attributes for server-side rendering."
        if "xmlns:sec" in s:
            return "Spring Security Thymeleaf extras — show/hide content based on login state."
        if "th:replace" in s:
            return "Inserts a reusable fragment (navbar, footer, head) from layout.html."
        if "th:each" in s:
            return "Loops over a list (products, categories) and repeats HTML for each item."
        if "th:href" in s or "th:action" in s:
            return "Generates correct URL using Spring URL syntax @{...}."
        if "th:text" in s or "th:if" in s:
            return "Displays dynamic data from controller model or conditional content."
        if "sec:authorize" in s:
            return "Shows element only if user is authenticated or not (Sign In vs Sign Out)."
        if "bootstrap" in s.lower():
            return "Bootstrap 5 CSS/JS — responsive grid, navbar, cards, buttons."
        if s.startswith("<") and ">" in s:
            return "HTML markup — structure of the web page shown in the browser."
        return "Template markup for PakBzer user interface."

    # Java
    if s.startswith("package "):
        return f"Package declaration — organizes class in Java module structure ({s.split()[1]})."

    if s.startswith("import "):
        imp = s.replace("import ", "").replace(";", "").strip()
        for key, val in IMPORT_EXPLAIN.items():
            if key in imp:
                return f"Import: {val}"
        return f"Imports class/library needed in this file: {imp}"

    if s.startswith("//") or s.startswith("/**") or s.startswith("*") or s.endswith("*/"):
        return "Comment — documents purpose for developers reading the code."

    for ann, exp in ANNOTATION_EXPLAIN.items():
        if ann in s:
            return exp

    if s.startswith("public enum "):
        return "Enum — fixed set of named constants (type-safe categories or roles)."
    if s.startswith("public interface "):
        return "Interface — defines contract (abstraction); implementation provided separately (polymorphism)."
    if s.startswith("public abstract class "):
        return "Abstract class — cannot be instantiated; subclasses inherit shared fields (inheritance)."
    if s.startswith("public class "):
        name = re.search(r"public class (\w+)", s)
        return f"Class declaration — blueprint for {name.group(1) if name else 'object'}; groups data and methods."

    if "private final" in s and ";" in s:
        return "Private final field — dependency injected via constructor; cannot be reassigned (encapsulation)."
    if "private " in s and ";" in s and "(" not in s:
        return "Private field — hidden internal state; accessed only via getters/setters (encapsulation)."

    if s.startswith("public ") and "(" in s and " get" in s:
        return "Getter method — read-only access to private field (encapsulation)."
    if s.startswith("public void set") or (s.startswith("public ") and " set" in s and "(" in s):
        return "Setter method — controlled write access to private field."
    if "public Product()" in s or "public User()" in s or "public Review()" in s or "public CartItem()" in s:
        return "No-arg constructor — required by JPA/Hibernate to create entity instances."
    if re.match(r"public \w+\([^)]+\)", s):
        return "Parameterized constructor — creates object with initial field values."

    if "return " in s:
        return "Returns a value to the caller — ends method execution with result."
    if s.startswith("if ") or s.startswith("} else"):
        return "Conditional branch — executes code only when condition is true."
    if "for (" in s or "for (" in s:
        return "Loop — repeats logic for each item in collection."
    if ".stream()" in s:
        return "Java Stream API — functional processing of collections (average, sum, filter)."
    if "throw new" in s:
        return "Throws exception — signals error to caller (e.g. duplicate email, product not found)."
    if s == "}" or s == "};" or s == "})," or s.endswith("});"):
        return "Closing brace — ends class, method, block, or lambda."
    if s.endswith("{") or s.endswith("() {"):
        return "Opening brace — starts method or block body."

    if "SpringApplication.run" in s:
        return "Bootstraps Spring: starts Tomcat, loads beans, listens on port 8080."
    if "productRepository" in s.lower() or "userRepository" in s.lower() or "reviewRepository" in s.lower():
        return "Calls Spring Data JPA repository — performs database query/save."
    if "passwordEncoder" in s:
        return "BCrypt hashes password before storing — never save plain text passwords."
    if "cart." in s:
        return "Delegates to session Cart bean — add/update/remove items or compute total."
    if "redirect:" in s:
        return "HTTP redirect — browser navigates to another URL after form submit."
    if 'return "' in s and ";" in s:
        return "Returns Thymeleaf view name — Spring resolves to templates/{name}.html."

    return "Executable statement — part of method logic implementing business or web behavior."


def file_section(filepath: str) -> str:
    path = ROOT / filepath.replace("/", os.sep)
    if not path.exists():
        return f"<h2>{html.escape(filepath)}</h2><p><i>File not found.</i></p>"
    lines = path.read_text(encoding="utf-8").splitlines()
    rows = []
    for i, line in enumerate(lines, 1):
        code = html.escape(line) if line.strip() else "&nbsp;"
        exp = html.escape(explain_line(line, filepath, i, lines))
        rows.append(
            f"<tr><td style='width:40pt;text-align:right;color:#666;'>{i}</td>"
            f"<td style='font-family:Consolas;font-size:9pt;background:#f4f4f4;'>{code}</td>"
            f"<td style='font-size:10pt;'>{exp}</td></tr>"
        )
    body = "\n".join(rows)
    return f"""
<h2 style='color:#01411C;border-bottom:2px solid #046A38;'>{html.escape(filepath)}</h2>
<p><b>Total lines:</b> {len(lines)}</p>
<table border='1' cellpadding='4' cellspacing='0' style='border-collapse:collapse;width:100%;'>
<tr style='background:#046A38;color:#fff;'><th>Line</th><th>Code</th><th>Purpose / Why we used it</th></tr>
{body}
</table>
<br/>
"""


def main():
    sections = []
    total = 0
    for f in FILES:
        p = ROOT / f.replace("/", os.sep)
        if p.exists():
            total += len(p.read_text(encoding="utf-8").splitlines())
        sections.append(file_section(f))

    doc = f"""<html xmlns:o="urn:schemas-microsoft-com:office:office"
xmlns:w="urn:schemas-microsoft-com:office:word" xmlns="http://www.w3.org/TR/REC-html40">
<head><meta charset="utf-8"/><title>PakBzer Complete Code Explanation</title>
<style>
body{{font-family:Calibri,sans-serif;font-size:11pt;margin:2cm;}}
h1{{color:#01411C;text-align:center;}}
</style></head>
<body>
<h1>PakBzer — Complete Line-by-Line Code Explanation</h1>
<p style="text-align:center;"><b>BSSE Project</b><br/>
Ahmed Ali Ghori (60117) &amp; Komal Raza (63424)</p>
<p>This document explains <b>every line</b> of the PakBzer project source code ({total} lines across {len(FILES)} files).
For each line: what it does and why it is used in the e-commerce application.</p>
<hr/>
{''.join(sections)}
<p style="text-align:center;color:#777;">End of documentation — PakBzer OOP Project</p>
</body></html>"""
    OUT.write_text(doc, encoding="utf-8")
    print(f"Created: {OUT}")
    print(f"Files: {len(FILES)}, Total lines documented: {total}")


if __name__ == "__main__":
    main()
