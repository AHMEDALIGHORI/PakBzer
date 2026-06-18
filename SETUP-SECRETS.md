# PakBzer Secrets Setup

This project uses Spring Boot environment variables for deployment and local
development. Do **not** commit a real `.env` file to GitHub.

## Values this project uses today

| Variable | Used by | Notes |
|---|---|---|
| `GOOGLE_CLIENT_ID` | Google login | Required for OAuth2 login |
| `GOOGLE_CLIENT_SECRET` | Google login | Required for OAuth2 login |
| `GOOGLE_REDIRECT_URI` | Google login | Must match your deployed callback URL |
| `STRIPE_SECRET_KEY` | Stripe checkout | Required for real checkout sessions |
| `STRIPE_PUBLISHABLE_KEY` | Stripe checkout | Also required because Stripe is only enabled when both keys look real |
| `STRIPE_CURRENCY` | Stripe checkout | Default `pkr` |
| `APP_BASE_URL` | Stripe redirects | Used for success/cancel URLs |
| `SPRING_DATASOURCE_URL` | Database | Default H2 file database |
| `SPRING_DATASOURCE_DRIVER` | Database | Default `org.h2.Driver` |
| `SPRING_DATASOURCE_USERNAME` | Database | Default `sa` |
| `SPRING_DATASOURCE_PASSWORD` | Database | Default blank |
| `H2_CONSOLE_ENABLED` | H2 console | Usually `false` in production |
| `THYMELEAF_CACHE` | Views | Usually `true` in production |
| `PORT` | Host runtime | Only needed on some platforms/local Docker |

## Values not used by the current code

These are **not wired into the app right now**:

- `STRIPE_WEBHOOK_SECRET`
- `FRONTEND_URL`
- `CORS_ORIGINS`
- `SENDGRID_API_KEY`
- `SENDGRID_FROM`
- `DB_HOST`
- `DB_PORT`
- `DB_NAME`
- `DB_USER`
- `DB_PASSWORD`
- `SPRING_PROFILES_ACTIVE`
- `GCP_PROJECT_ID`
- `GCP_STORAGE_BUCKET`

If you want any of those, we would need to add new code for them.

## Local setup

1. Copy `env.example` to `.env`
2. Paste your real keys into `.env`
3. Keep `.env` out of GitHub
4. Run the app with `mvn spring-boot:run`

## Render / Cloud Run setup

Add the variables from `render.env.example` or this guide to the platform's
environment variable screen. Do not upload your local `.env` file.

## Security note

If any real secret was pasted into chat, rotate it in the provider dashboard.
That is especially important for:

- Google OAuth client secret
- Stripe secret key

