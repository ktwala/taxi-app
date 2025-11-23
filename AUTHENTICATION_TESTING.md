# Authentication Testing Guide

This guide explains how to test the JWT authentication implementation.

## Prerequisites

1. Ensure the application is running:
```bash
mvn spring-boot:run
```

2. Ensure PostgreSQL database is running with a MEMBER role created:
```sql
-- Connect to your database and create a MEMBER role if it doesn't exist
INSERT INTO user_roles (role_name, permissions)
VALUES ('MEMBER', '{"view": true, "edit": false}')
ON CONFLICT (role_name) DO NOTHING;
```

## Testing with cURL

### 1. Register a New User

```bash
curl -X POST http://localhost:8082/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "test@example.com",
    "password": "password123",
    "phoneNumber": "1234567890",
    "firstName": "Test",
    "lastName": "User"
  }'
```

**Expected Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "type": "Bearer",
  "userId": 1,
  "username": "testuser",
  "email": "test@example.com",
  "roles": ["ROLE_MEMBER"]
}
```

### 2. Login with Existing User

```bash
curl -X POST http://localhost:8082/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "password123"
  }'
```

**Expected Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "type": "Bearer",
  "userId": 1,
  "username": "testuser",
  "email": "test@example.com",
  "roles": ["ROLE_MEMBER"]
}
```

### 3. Access Protected Endpoint Without Token

```bash
curl -X GET http://localhost:8082/api/users
```

**Expected Response:** `401 Unauthorized`

### 4. Access Protected Endpoint With Token

```bash
# First, save the token from login response
TOKEN="eyJhbGciOiJIUzI1NiJ9..."

curl -X GET http://localhost:8082/api/users \
  -H "Authorization: Bearer $TOKEN"
```

**Expected Response:**
- If user is MEMBER: `403 Forbidden` (UserController requires ADMIN role)
- If user is ADMIN: `200 OK` with user list

### 5. Get Current User Details

```bash
curl -X GET http://localhost:8082/api/auth/me \
  -H "Authorization: Bearer $TOKEN"
```

**Expected Response:**
```json
{
  "userId": 1,
  "username": "testuser",
  "email": "test@example.com",
  "fullName": "Test User",
  "active": true,
  "roleName": "MEMBER"
}
```

## Testing with Swagger UI

1. Open Swagger UI: http://localhost:8082/swagger-ui.html

2. Click the **"Authorize"** button at the top right

3. **Option A: Register a new user**
   - Expand `POST /api/auth/register`
   - Click "Try it out"
   - Fill in the request body
   - Execute
   - Copy the `token` from the response

4. **Option B: Login with existing user**
   - Expand `POST /api/auth/login`
   - Click "Try it out"
   - Fill in username and password
   - Execute
   - Copy the `token` from the response

5. **Set the JWT token:**
   - Click "Authorize" button
   - Enter: `Bearer <your-token>` (without the angle brackets)
   - Click "Authorize"
   - Click "Close"

6. **Test protected endpoints:**
   - Try accessing any endpoint (e.g., `GET /api/users`)
   - You should see the request includes the Authorization header
   - Endpoints requiring roles you don't have will return 403

## Testing Different Roles

### Create ADMIN User (via Database)

Since only ADMIN can create users, you need to create the first ADMIN user via database:

```sql
-- First, create an ADMIN role if it doesn't exist
INSERT INTO user_roles (role_name, permissions)
VALUES ('ADMIN', '{"full_access": true}')
ON CONFLICT (role_name) DO NOTHING;

-- Get the ADMIN role_id
SELECT role_id FROM user_roles WHERE role_name = 'ADMIN';

-- Create an ADMIN user (assuming role_id is 1)
INSERT INTO users (username, password_hash, full_name, contact_email, role_id, active, created_at, updated_at)
VALUES (
  'admin',
  '$2a$10$N9qo8uLOickgx2ZMRZoMye.IxRWJTjhQpJ5hgH4qPmPDPgRJXUGVy', -- Password: admin123
  'System Administrator',
  'admin@taxiservice.com',
  1,  -- ADMIN role_id
  true,
  NOW(),
  NOW()
);
```

### Test ADMIN Access

1. Login as admin:
```bash
curl -X POST http://localhost:8082/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "admin123"
  }'
```

2. Use the token to access ADMIN-only endpoints:
```bash
curl -X GET http://localhost:8082/api/users \
  -H "Authorization: Bearer <admin-token>"
```

## Expected Behaviors

### Public Endpoints (No Authentication Required)
- `POST /api/auth/register` ✅
- `POST /api/auth/login` ✅
- `/swagger-ui/**` ✅
- `/actuator/**` ✅

### Protected Endpoints (Authentication Required)
- All other `/api/**` endpoints require valid JWT token

### Role-Based Access
- **ADMIN Role:**
  - Full access to `/api/users/**` endpoints
  - Can create, update, delete users

- **MEMBER Role:**
  - Cannot access `/api/users/**` (403 Forbidden)
  - Can access endpoints without role restrictions

## Common Issues

### 401 Unauthorized
- Token is missing or malformed
- Token has expired (default: 24 hours)
- User credentials are incorrect

### 403 Forbidden
- Token is valid but user doesn't have required role
- Example: MEMBER trying to access ADMIN-only endpoints

### 500 Internal Server Error
- MEMBER role doesn't exist in database
- Database connection issues
- Password encoding issues

## Troubleshooting

1. **Registration fails with "UserRole MEMBER not found":**
   ```sql
   INSERT INTO user_roles (role_name) VALUES ('MEMBER');
   ```

2. **Password validation fails:**
   - Ensure password is at least 6 characters
   - Check that PasswordEncoder bean is configured

3. **Token validation fails:**
   - Verify JWT secret is configured in application.yml
   - Check token hasn't expired
   - Ensure token is sent as `Bearer <token>` format

## Security Notes

⚠️ **Important:**
- The default JWT secret in application.yml is for development only
- In production, use a strong secret via environment variable: `JWT_SECRET`
- Default expiration is 24 hours; adjust via: `JWT_EXPIRATION`
- First ADMIN user must be created via database
- All passwords are hashed using BCrypt
- UserService now properly hashes passwords before storing
