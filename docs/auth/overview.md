# Authentication API

## Endpoints

| Method | Path                      | Description                                   | Success | Key Errors          |
|--------|---------------------------|-----------------------------------------------|---------|---------------------|
| POST   | `/api/auth/register`      | Register a new account                        | 201     | 409, 422            |
| POST   | `/api/auth/verify-email`  | Verify email with OTP                         | 200     | 404, 409, 422       |
| POST   | `/api/auth/verify-email/resend` | Resend verification email             | 200     | 404, 409            |
| POST   | `/api/auth/login`         | Login with email & password                   | 200     | 401, 403            |
| POST   | `/api/auth/login/verify`  | Complete 2FA login with OTP                   | 200     | 422, 429            |
| POST   | `/api/auth/oauth`         | Login via OAuth provider                      | 200     | 401, 409            |
| POST   | `/api/auth/token/refresh` | Refresh access token                          | 200     | 401                 |
| POST   | `/api/auth/logout`        | Logout / revoke session                       | 204     | 401                 |
| POST   | `/api/auth/forgot-password` | Request password reset OTP                  | 200     | 404                 |
| POST   | `/api/auth/reset-password`  | Reset password with OTP                     | 200     | 404, 422            |
| POST   | `/api/profiles`           | Create a profile for the authenticated account| 201     | 400, 401, 403, 404, 409 |

## High-Level Flow

```mermaid
flowchart LR
    subgraph Registration
        R1[POST /register] --> R2[POST /verify-email]
        R1 -.-> R3[POST /verify-email/resend]
        R3 -.-> R2
        R2 --> P1[POST /profiles]
    end

    subgraph Login
        L1[POST /login] -->|tokens| Done((Authenticated))
        L1 -->|requiresVerification| L2[POST /login/verify]
        L2 --> Done
    end

    subgraph OAuth
        O1[POST /oauth] --> Done
    end

    subgraph "Token Lifecycle"
        Done --> T1[POST /token/refresh]
        T1 --> Done
        Done --> T2[POST /logout]
    end

    subgraph "Password Reset"
        P1R[POST /forgot-password] --> P2R[POST /reset-password]
    end
```
