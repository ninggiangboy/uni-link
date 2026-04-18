# Password Reset Flow

## `POST /api/auth/forgot-password`

Sends a password reset OTP to the specified email address.

**Request:**

```json
{
  "email": "user@example.com"
}
```

**Response:** `200 OK` (empty body)

**Errors:**

| Code | Error             | When                       |
|------|-------------------|----------------------------|
| 404  | ACCOUNT_NOT_FOUND | No account with this email |

---

## `POST /api/auth/reset-password`

Resets the account password using the OTP received via email.

**Request:**

```json
{
  "email": "user@example.com",
  "otpCode": "123456",
  "newPassword": "newSecurePassword456"
}
```

**Response:** `200 OK` (empty body)

**Errors:**

| Code | Error              | When                                       |
|------|--------------------|------------------------------------------- |
| 404  | ACCOUNT_NOT_FOUND  | No account with this email                 |
| 422  | INVALID_OTP        | OTP is invalid or expired                  |
| 422  | PASSWORD_SAME_AS_OLD | New password same as current password    |

---

## Sequence Diagram

```mermaid
sequenceDiagram
    participant C as Client
    participant API as AuthApi
    participant UC as UseCases
    participant DB as Database
    participant Mail as Email Service

    C->>API: POST /forgot-password {email}
    API->>UC: ForgotPasswordUseCase
    UC->>DB: Find account by email
    alt Not found
        UC-->>C: 404 ACCOUNT_NOT_FOUND
    else
        UC->>Mail: Send reset OTP
        UC-->>C: 200 OK
    end

    C->>API: POST /reset-password {email, otpCode, newPassword}
    API->>UC: ResetPasswordUseCase
    UC->>DB: Validate OTP & account
    alt Not found
        UC-->>C: 404 ACCOUNT_NOT_FOUND
    else Invalid OTP
        UC-->>C: 422 INVALID_OTP
    else Same password
        UC-->>C: 422 PASSWORD_SAME_AS_OLD
    else
        UC->>DB: Update password
        UC-->>C: 200 OK
    end
```
