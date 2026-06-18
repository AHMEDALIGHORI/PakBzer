# PakBzer Secrets Setup

This project loads a local `.env` file automatically (via `spring-dotenv`) and
maps variables into Spring Boot configuration. Do **not** commit `.env` to GitHub.

## Quick start

1. Copy `env.example` to `.env`
2. Paste your real keys into `.env`
3. Run: `mvn spring-boot:run`
4. Open: http://localhost:8080

## Environment variables

| Variable | Used by | Notes |
|---|---|---|
| `GOOGLE_CLIENT_ID` | Google login | Required for OAuth2 login |
| `GOOGLE_CLIENT_SECRET` | Google login | Required for OAuth2 login |
| `GOOGLE_REDIRECT_URI` | Google login | Must match Google Cloud redirect URI |
| `STRIPE_SECRET_KEY` | Stripe checkout | Required for real checkout sessions |
| `STRIPE_PUBLISHABLE_KEY` | Stripe checkout | Required — Stripe is only enabled when both keys look real |
| `STRIPE_CURRENCY` | Stripe checkout | Default `pkr` |
| `STRIPE_WEBHOOK_SECRET` | Stripe webhooks | From `stripe listen --forward-to localhost:8080/api/webhooks/stripe` |
| `APP_BASE_URL` | Stripe redirects | Success/cancel URLs (default `http://localhost:8080`) |
| `FRONTEND_URL` | App config | Separate frontend URL if you add a SPA later |
| `CORS_ORIGINS` | CORS | Comma-separated origins allowed on `/api/**` |
| `SENDGRID_API_KEY` | Order emails | Sends confirmation on checkout success when set |
| `SENDGRID_FROM` | Order emails | Default `noreply@pakbazer.pk` |
| `DB_HOST` / `DB_PORT` / `DB_NAME` / `DB_USER` / `DB_PASSWORD` | PostgreSQL | Used when `SPRING_PROFILES_ACTIVE=prod` |
| `SPRING_PROFILES_ACTIVE` | Database | Set to `prod` for PostgreSQL; leave empty for local H2 |
| `GCP_PROJECT_ID` | Cloud Storage | Product image uploads when configured |
| `GCP_STORAGE_BUCKET` | Cloud Storage | Bucket name for uploads |
| `GOOGLE_APPLICATION_CREDENTIALS` | Cloud Storage | Path to GCP service-account JSON (local dev) |
| `SPRING_DATASOURCE_*` | H2 database | Default local file database |
| `H2_CONSOLE_ENABLED` | H2 console | `true` locally, `false` in production |
| `THYMELEAF_CACHE` | Views | `false` locally, `true` in production |
| `PORT` | Server | Host runtime port (default `8080`) |

## Google OAuth2 redirect URI (required)

The app sends this redirect URI to Google:

```
http://localhost:8080/login/oauth2/code/google
```

Add it in [Google Cloud Console](https://console.cloud.google.com/apis/credentials):

1. Open your OAuth 2.0 Client ID (Web application)
2. Under **Authorized redirect URIs**, click **Add URI**
3. Paste exactly: `http://localhost:8080/login/oauth2/code/google`
4. Save

If you use `127.0.0.1` in the browser, also add:
`http://127.0.0.1:8080/login/oauth2/code/google`

Without this, Google returns **Error 400: redirect_uri_mismatch** and sign-in cannot complete.

## Stripe webhooks (local)

```bash
stripe listen --forward-to localhost:8080/api/webhooks/stripe
```

Copy the `whsec_...` signing secret into `STRIPE_WEBHOOK_SECRET` in `.env`.

## PostgreSQL (production profile)

Set in `.env`:

```
SPRING_PROFILES_ACTIVE=prod
DB_HOST=localhost
DB_PORT=5432
DB_NAME=pakbazer
DB_USER=pakbazer
DB_PASSWORD=pakbazer
```

Create the database first, then start the app. Schema is auto-updated via JPA.

## Google Cloud Storage

1. Create a bucket in Google Cloud Console
2. Set `GCP_PROJECT_ID` and `GCP_STORAGE_BUCKET` in `.env`
3. Download a service-account key and set `GOOGLE_APPLICATION_CREDENTIALS` to its path

`StorageService` is ready for uploads; product images still use Unsplash URLs by default.

## Render / Cloud Run

Add variables from `render.env.example` to your host's environment screen.
Do not upload your local `.env` file.

## Security note

If any real secret was pasted into chat, rotate it in the provider dashboard:

- Google OAuth client secret
- Stripe secret key
