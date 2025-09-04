# Postman Authentication Guide

This guide shows you how to register new users and login using Postman to get JWT tokens for testing the Sparrow Backend services.

## Prerequisites

1. **Start the services** using Docker Compose:
   ```bash
   docker-compose up -d
   ```

2. **Verify services are running**:
   - Auth Service: http://localhost:8081/actuator/health
   - API Gateway: http://localhost:8080/actuator/health

## User Registration

### 1. Register a New User

**Endpoint**: `POST http://localhost:8081/api/auth/register`

**Headers**:
```
Content-Type: application/json
```

**Request Body** (JSON):
```json
{
  "username": "john_doe",
  "email": "john.doe@example.com",
  "password": "password123",
  "role": "CUSTOMER"
}
```

**Available Roles**:
- `ADMIN` - Full system access
- `STAFF` - Warehouse and management access
- `DRIVER` - Delivery and tracking access
- `CUSTOMER` - Basic user access

**Example Response** (200 OK):
```json
{
  "id": "507f1f77bcf86cd799439011",
  "username": "john_doe",
  "message": "Registered"
}
```

### 2. Register Different User Types

**Admin User**:
```json
{
  "username": "admin_user",
  "email": "admin@sparrow.com",
  "password": "admin123",
  "role": "ADMIN"
}
```

**Driver User**:
```json
{
  "username": "driver_mike",
  "email": "mike@sparrow.com",
  "password": "driver123",
  "role": "DRIVER"
}
```

**Staff User**:
```json
{
  "username": "staff_sarah",
  "email": "sarah@sparrow.com",
  "password": "staff123",
  "role": "STAFF"
}
```

## User Login

### 1. Login to Get JWT Token

**Endpoint**: `POST http://localhost:8081/api/auth/login`

**Headers**:
```
Content-Type: application/json
```

**Request Body** (JSON):
```json
{
  "username": "john_doe",
  "password": "password123"
}
```

**Example Response** (200 OK):
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJqb2huX2RvZSIsInJvbGUiOiJDVVNUT01FUiIsImlhdCI6MTY5MzU5MjAwMCwiZXhwIjoxNjkzNTk1NjAwfQ.example_signature_here"
}
```

### 2. Using the JWT Token

Copy the `token` value from the login response and use it in subsequent API calls:

**Headers for Protected Endpoints**:
```
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJqb2huX2RvZSIsInJvbGUiOiJDVVNUT01FUiIsImlhdCI6MTY5MzU5MjAwMCwiZXhwIjoxNjkzNTk1NjAwfQ.example_signature_here
Content-Type: application/json
```

## Testing User Profile

### Get Current User Info

**Endpoint**: `GET http://localhost:8081/api/auth/me`

**Headers**:
```
Authorization: Bearer YOUR_JWT_TOKEN_HERE
```

**Example Response** (200 OK):
```json
{
  "username": "john_doe",
  "authorities": [
    {
      "authority": "ROLE_CUSTOMER"
    }
  ]
}
```

## Postman Collection Setup

### 1. Create Environment Variables

In Postman, create an environment with these variables:

| Variable | Initial Value | Current Value |
|----------|---------------|---------------|
| `base_url` | `http://localhost:8081` | `http://localhost:8081` |
| `api_gateway_url` | `http://localhost:8080` | `http://localhost:8080` |
| `jwt_token` | (empty) | (will be set after login) |

### 2. Pre-request Script for Login

Add this script to automatically set the JWT token after login:

```javascript
if (pm.response.code === 200) {
    const responseJson = pm.response.json();
    if (responseJson.token) {
        pm.environment.set("jwt_token", responseJson.token);
    }
}
```

### 3. Authorization Setup

For protected endpoints, set Authorization to:
- **Type**: Bearer Token
- **Token**: `{{jwt_token}}`

## Complete Postman Workflow

### Step 1: Register Users
1. Create requests for different user types (ADMIN, STAFF, DRIVER, CUSTOMER)
2. Test registration with valid data
3. Test registration with duplicate username/email (should fail)

### Step 2: Login and Get Tokens
1. Login with each registered user
2. Copy the JWT token from response
3. Set the token in environment variable

### Step 3: Test Protected Endpoints
1. Use the JWT token to access protected endpoints
2. Test different role-based access (ADMIN vs CUSTOMER)
3. Test token expiration (tokens expire in 1 hour)

## Error Handling

### Common Registration Errors

**400 Bad Request - Username already exists**:
```json
{
  "timestamp": "2023-09-01T12:00:00.000+00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Username already exists"
}
```

**400 Bad Request - Invalid role**:
```json
{
  "timestamp": "2023-09-01T12:00:00.000+00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Invalid role"
}
```

### Common Login Errors

**401 Unauthorized - Invalid credentials**:
```json
{
  "timestamp": "2023-09-01T12:00:00.000+00:00",
  "status": 401,
  "error": "Unauthorized",
  "message": "Invalid credentials"
}
```

## Testing with Other Services

Once you have a JWT token, you can test the other services:

### Tracking Service (Port 8082)
- **Base URL**: `http://localhost:8082`
- **Endpoints**: `/api/tracking/*`
- **Required Role**: DRIVER, STAFF, or ADMIN

### Consolidation Service (Port 8083)
- **Base URL**: `http://localhost:8083`
- **Endpoints**: `/api/consolidation/*`
- **Required Role**: STAFF or ADMIN

### API Gateway (Port 8080)
- **Base URL**: `http://localhost:8080`
- **Endpoints**: `/api/tracking/*`, `/api/consolidation/*`
- **Routes requests to appropriate services**

## Security Notes

1. **JWT Secret**: The default JWT secret is for development only. Change it in production.
2. **Token Expiration**: Tokens expire in 1 hour (3600000 milliseconds).
3. **Password Requirements**: Minimum 6 characters, maximum 100 characters.
4. **Username Requirements**: Minimum 3 characters, maximum 30 characters.
5. **Email Validation**: Must be a valid email format.

## Troubleshooting

### Service Not Running
- Check if Docker containers are running: `docker-compose ps`
- Check service logs: `docker-compose logs auth-service`

### Connection Refused
- Verify the service is running on the correct port
- Check if the port is not blocked by firewall

### Invalid Token
- Ensure the token is copied correctly (no extra spaces)
- Check if the token has expired
- Verify the Authorization header format: `Bearer <token>`

### Role-based Access Denied
- Ensure the user has the correct role for the endpoint
- Check the endpoint's `@PreAuthorize` annotation requirements
