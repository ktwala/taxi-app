# API Endpoint Fixes

This document details the fixes made to resolve endpoint errors and provides correct usage examples.

## Issues Resolved

### 1. Taxi Driver/Route Assignment Errors

**Previous Error:**
```
500 Internal Server Error
"No static resource api/taxis/1/driver/1"
```

**Fix:** Added alternative endpoints that support both PATCH and PUT methods with flexible path formats.

**Correct Usage:**

**Option A: Original endpoints (PATCH method)**
```bash
# Assign driver to taxi
PATCH /api/taxis/{taxiId}/assign-driver/{driverId}

curl -X PATCH http://localhost:8082/api/taxis/1/assign-driver/1 \
  -H "Authorization: Bearer <token>"

# Assign route to taxi
PATCH /api/taxis/{taxiId}/assign-route/{routeId}

curl -X PATCH http://localhost:8082/api/taxis/1/assign-route/1 \
  -H "Authorization: Bearer <token>"
```

**Option B: Alternative endpoints (PUT method) - NEW**
```bash
# Assign driver to taxi
PUT /api/taxis/{taxiId}/driver/{driverId}

curl -X PUT http://localhost:8082/api/taxis/1/driver/1 \
  -H "Authorization: Bearer <token>"

# Assign route to taxi
PUT /api/taxis/{taxiId}/route/{routeId}

curl -X PUT http://localhost:8082/api/taxis/1/route/1 \
  -H "Authorization: Bearer <token>"
```

---

### 2. Membership Application Review Error

**Previous Error:**
```
500 Internal Server Error
"Request method 'GET' is not supported"
```

**Fix:** The endpoints use PATCH/PUT methods, not GET. Added PUT method support as alternative.

**Correct Usage:**

**Secretary Review:**
```bash
# Option A: PATCH method (original)
PATCH /api/membership-applications/{applicationId}/secretary-review

curl -X PATCH http://localhost:8082/api/membership-applications/1/secretary-review \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{
    "approved": true,
    "comments": "Application looks good",
    "reviewerName": "John Doe"
  }'

# Option B: PUT method (alternative) - NEW
PUT /api/membership-applications/{applicationId}/secretary-review

curl -X PUT http://localhost:8082/api/membership-applications/1/secretary-review \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{
    "approved": true,
    "comments": "Application looks good",
    "reviewerName": "John Doe"
  }'
```

---

### 3. Final Approval Error

**Previous Error:**
```
500 Internal Server Error
"Request method 'PUT' is not supported"
```

**Fix:** Added PUT method support in addition to PATCH for chairperson review.

**Correct Usage:**

**Chairperson Review (Final Approval):**
```bash
# Option A: PATCH method (original)
PATCH /api/membership-applications/{applicationId}/chairperson-review

curl -X PATCH http://localhost:8082/api/membership-applications/1/chairperson-review \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{
    "approved": true,
    "comments": "Approved for membership",
    "reviewerName": "Jane Smith"
  }'

# Option B: PUT method (alternative) - NEW
PUT /api/membership-applications/{applicationId}/chairperson-review

curl -X PUT http://localhost:8082/api/membership-applications/1/chairperson-review \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{
    "approved": true,
    "comments": "Approved for membership",
    "reviewerName": "Jane Smith"
  }'
```

---

### 4. Create Association Member Error

**Previous Error:**
```
500 Internal Server Error
"Required request parameter 'currentUser' for method parameter type String is not present"
```

**Fix:** The `currentUser` parameter is now automatically extracted from the authenticated user's JWT token. You no longer need to provide it as a query parameter.

**Correct Usage:**

**Before (INCORRECT):**
```bash
POST /api/members?currentUser=admin

curl -X POST "http://localhost:8082/api/members?currentUser=admin" \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{...}'
```

**After (CORRECT) - NEW:**
```bash
POST /api/members

curl -X POST http://localhost:8082/api/members \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "John Doe",
    "squadNumber": "SQ001",
    "contactNumber": "1234567890",
    "email": "john@example.com",
    "address": "123 Main St",
    "joiningDate": "2025-01-01",
    "blacklisted": false
  }'
```

The system now automatically uses the authenticated user from your JWT token.

---

## Complete Workflow: Review and Approve Membership Application

Here's the complete workflow with corrected endpoints:

### Step 1: Create/Submit Application
```bash
POST /api/membership-applications

curl -X POST http://localhost:8082/api/membership-applications \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{
    "applicantName": "John Smith",
    "contactNumber": "1234567890",
    "email": "john.smith@example.com",
    "address": "456 Oak Avenue",
    "routeId": 1,
    "applicationStatus": "PENDING"
  }'
```

**Response:** Note the `applicationId` from the response (e.g., `"applicationId": 1`)

---

### Step 2: Secretary Review
```bash
# Use either PATCH or PUT
PUT /api/membership-applications/1/secretary-review

curl -X PUT http://localhost:8082/api/membership-applications/1/secretary-review \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{
    "approved": true,
    "comments": "Documents verified, applicant meets requirements",
    "reviewerName": "Secretary Name"
  }'
```

---

### Step 3: Chairperson Review (Final Approval)
```bash
# Use either PATCH or PUT
PUT /api/membership-applications/1/chairperson-review

curl -X PUT http://localhost:8082/api/membership-applications/1/chairperson-review \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{
    "approved": true,
    "comments": "Final approval granted",
    "reviewerName": "Chairperson Name"
  }'
```

---

### Step 4: Create Association Member (after approval)
```bash
POST /api/members

curl -X POST http://localhost:8082/api/members \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "John Smith",
    "squadNumber": "SQ001",
    "contactNumber": "1234567890",
    "email": "john.smith@example.com",
    "address": "456 Oak Avenue",
    "joiningDate": "2025-01-01",
    "blacklisted": false
  }'
```

---

## Summary of Changes

1. **TaxiController** - Added PUT alternatives for driver/route assignment:
   - `PUT /api/taxis/{taxiId}/driver/{driverId}` (new)
   - `PUT /api/taxis/{taxiId}/route/{routeId}` (new)

2. **MembershipApplicationController** - Added PUT method support:
   - `PUT /api/membership-applications/{applicationId}/secretary-review` (new)
   - `PUT /api/membership-applications/{applicationId}/chairperson-review` (new)

3. **AssocMemberController** - Removed `currentUser` query parameter:
   - Now uses authenticated user from JWT token automatically
   - Applied to: `createMember`, `updateMember`, `blacklistMember`, `removeBlacklist`

## Testing in Swagger UI

All endpoints are available in Swagger UI at: http://localhost:8082/swagger-ui.html

1. Click "Authorize" and enter your JWT token
2. Find the endpoint under the appropriate section
3. Click "Try it out"
4. Fill in the required fields
5. Execute

The Swagger UI will show all available HTTP methods for each endpoint.

## Important Notes

- **Authentication Required:** All fixed endpoints require a valid JWT token
- **HTTP Methods:** Both PATCH and PUT are now supported for review/assignment operations
- **Auto-populated Fields:** The `currentUser` is automatically extracted from your JWT token
- **Flexible Paths:** Multiple path formats are supported for better compatibility

## Troubleshooting

If you still encounter errors:

1. **401 Unauthorized** - Your JWT token is invalid or expired. Login again.
2. **403 Forbidden** - You don't have the required role for this operation.
3. **404 Not Found** - Check the URL path and ensure the resource exists.
4. **400 Bad Request** - Check your request body matches the required format.

For role requirements, see: `SECURITY_ROLES.md`
For authentication setup, see: `AUTHENTICATION_TESTING.md`
