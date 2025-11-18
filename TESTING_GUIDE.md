# Testing Guide - Taxi Management System

This guide provides comprehensive instructions for testing the Taxi Management System using various tools and methods.

## Table of Contents
1. [Quick Start](#quick-start)
2. [Swagger/OpenAPI Documentation](#swaggeropenapi-documentation)
3. [Postman Collection](#postman-collection)
4. [Automated Test Scripts](#automated-test-scripts)
5. [Integration Tests](#integration-tests)
6. [Manual Testing with cURL](#manual-testing-with-curl)
7. [Testing Workflows](#testing-workflows)

---

## Quick Start

### Prerequisites
- Application running on `http://localhost:8082`
- PostgreSQL database running
- Postman (optional) or cURL installed

### Access Swagger UI
Once the application is running, access the interactive API documentation:

```
http://localhost:8082/swagger-ui.html
```

This provides:
- ✅ Complete API documentation
- ✅ Interactive API testing interface
- ✅ Request/response examples
- ✅ Field definitions and validation rules
- ✅ Try out endpoints directly in browser

---

## Swagger/OpenAPI Documentation

### Access Points

**Swagger UI (Interactive):**
```
http://localhost:8082/swagger-ui.html
```

**OpenAPI Specification (JSON):**
```
http://localhost:8082/api-docs
```

**OpenAPI Specification (YAML):**
```
http://localhost:8082/api-docs.yaml
```

### Features

1. **Browse All Endpoints:** Organized by controller/module
2. **View Request/Response Models:** See all DTOs with field types
3. **Test Endpoints:** Execute requests directly from the UI
4. **View Examples:** See sample requests and responses
5. **Export Specification:** Download OpenAPI spec for other tools

### How to Use Swagger UI

1. **Start the application**
   ```bash
   mvn spring-boot:run
   ```

2. **Open browser** to `http://localhost:8082/swagger-ui.html`

3. **Select an endpoint** (e.g., "POST /api/users")

4. **Click "Try it out"**

5. **Fill in request body** with example JSON

6. **Click "Execute"**

7. **View response** with status code and body

---

## Postman Collection

### Import Collection

1. Open Postman
2. Click "Import"
3. Select file: `/postman/Taxi-Management-API.postman_collection.json`
4. Collection will be imported with all endpoints organized

### Collection Features

- ✅ **100+ pre-configured requests**
- ✅ **Organized by module** (User, Driver, Member, Financial, etc.)
- ✅ **Environment variables** for dynamic IDs
- ✅ **Automated tests** for each request
- ✅ **Complete workflows** for end-to-end scenarios

### Using the Collection

#### 1. Set Base URL
The collection uses a variable `{{baseUrl}}` set to `http://localhost:8082`

#### 2. Run Requests in Sequence
The collection is designed to be run top-to-bottom:
1. Create User Role → stores `roleId`
2. Create User → uses `roleId`, stores `userId`
3. Create Driver → stores `driverId`
4. And so on...

#### 3. Automated Testing
Each request includes test scripts that:
- Verify response status
- Check response structure
- Extract and store IDs for subsequent requests

#### 4. Run Complete Collection
- Click "Run collection"
- Review test results
- See pass/fail for each request

---

## Automated Test Scripts

### Complete Workflow Test Script

Location: `/scripts/test-complete-workflow.sh`

This bash script tests all major workflows automatically.

#### Run the Script

```bash
# Make sure application is running
mvn spring-boot:run &

# Run the test script
./scripts/test-complete-workflow.sh
```

#### What It Tests

1. ✅ Health check
2. ✅ User management (roles and users)
3. ✅ Driver and route creation
4. ✅ Taxi creation and assignment
5. ✅ Payment method setup
6. ✅ Complete member onboarding workflow
7. ✅ Financial management (levies, fines, receipts)
8. ✅ Disciplinary workflow
9. ✅ Notification system
10. ✅ Audit logs

#### Output Example

```
================================
Taxi Management System - Complete Workflow Test
================================

✓ Application is healthy
✓ User role created with ID: 1
✓ User created with ID: 1
✓ Driver created with ID: 1
✓ Route created with ID: 1
✓ Taxi created with ID: 1
...
```

---

## Integration Tests

### Run Integration Tests

```bash
# Run all tests
mvn test

# Run specific integration test class
mvn test -Dtest=MemberOnboardingWorkflowIntegrationTest

# Run with coverage report
mvn test jacoco:report
```

### Available Integration Tests

#### 1. Member Onboarding Workflow Test
**File:** `MemberOnboardingWorkflowIntegrationTest.java`

**Tests:**
- Complete application → approval → membership journey
- Application rejection workflow
- Multi-step approval process

**Coverage:**
- Route creation
- Membership application submission
- Secretary review
- Chairperson approval
- Member creation
- Verification of approved members

#### 2. Financial Workflow Test
**File:** `FinancialWorkflowIntegrationTest.java`

**Tests:**
- Complete financial transaction workflow
- Overdue payment handling
- Fine issuance and disciplinary process

**Coverage:**
- Payment method creation
- Levy payment recording
- Fine issuance
- Receipt generation
- Outstanding balance calculation
- Notification sending

### View Test Results

```bash
# After running tests with coverage
open target/site/jacoco/index.html
```

---

## Manual Testing with cURL

### Example Requests

#### 1. Create User Role
```bash
curl -X POST http://localhost:8082/api/user-roles \
  -H "Content-Type: application/json" \
  -d '{
    "roleName": "Admin Clerk",
    "permissions": "{\"canManageMembers\": true}"
  }'
```

#### 2. Create User
```bash
curl -X POST http://localhost:8082/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "SecurePass123!",
    "fullName": "Admin User",
    "contactEmail": "admin@test.com",
    "roleId": 1,
    "active": true
  }'
```

#### 3. Get All Users
```bash
curl http://localhost:8082/api/users
```

#### 4. Create Taxi
```bash
curl -X POST http://localhost:8082/api/taxis \
  -H "Content-Type: application/json" \
  -d '{
    "plateNumber": "GP-123-456",
    "model": "Toyota Quantum",
    "capacity": 15
  }'
```

#### 5. Create Member
```bash
curl -X POST http://localhost:8082/api/members \
  -H "Content-Type: application/json" \
  -d '{
    "name": "John Member",
    "contactNumber": "+27123456789",
    "squadNumber": "SQ-001",
    "blacklisted": false
  }'
```

---

## Testing Workflows

### Workflow 1: Complete Member Onboarding

```bash
# 1. Create route
curl -X POST http://localhost:8082/api/routes \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Route A",
    "startPoint": "CBD",
    "endPoint": "Sandton",
    "isActive": true
  }'

# 2. Submit application
curl -X POST http://localhost:8082/api/membership-applications \
  -H "Content-Type: application/json" \
  -d '{
    "applicantName": "Jane Applicant",
    "contactNumber": "+27987654321",
    "routeId": 1,
    "applicationStatus": "Pending"
  }'

# 3. Secretary review
curl -X PUT http://localhost:8082/api/membership-applications/1/secretary-review \
  -H "Content-Type: application/json" \
  -d '{
    "decision": "Approved",
    "comments": "Documents verified"
  }'

# 4. Chairperson approval
curl -X PUT http://localhost:8082/api/membership-applications/1/chairperson-review \
  -H "Content-Type: application/json" \
  -d '{
    "decision": "Approved",
    "comments": "Approved for membership"
  }'

# 5. Create member
curl -X POST http://localhost:8082/api/members \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Jane Applicant",
    "contactNumber": "+27987654321",
    "squadNumber": "SQ-002",
    "blacklisted": false
  }'
```

### Workflow 2: Weekly Levy Collection

```bash
# 1. Create payment method
curl -X POST http://localhost:8082/api/payment-methods \
  -H "Content-Type: application/json" \
  -d '{
    "methodName": "Cash",
    "description": "Cash payment"
  }'

# 2. Create levy payment for member
curl -X POST http://localhost:8082/api/levy-payments \
  -H "Content-Type: application/json" \
  -d '{
    "assocMemberId": 1,
    "weekStartDate": "2025-11-10",
    "weekEndDate": "2025-11-16",
    "amount": 500.00,
    "paymentStatus": "Pending",
    "paymentMethodId": 1
  }'

# 3. Create receipt after payment
curl -X POST http://localhost:8082/api/receipts \
  -H "Content-Type: application/json" \
  -d '{
    "assocMemberId": 1,
    "amount": 500.00,
    "paymentMethodId": 1,
    "receiptNumber": "RCT-2025-001",
    "issuedBy": "Cashier Name"
  }'

# 4. Check outstanding balance
curl http://localhost:8082/api/member-finance/1/balance
```

### Workflow 3: Fine and Disciplinary Process

```bash
# 1. Issue fine
curl -X POST http://localhost:8082/api/levy-fines \
  -H "Content-Type: application/json" \
  -d '{
    "assocMemberId": 1,
    "fineAmount": 100.00,
    "fineReason": "Late payment",
    "fineStatus": "Unpaid"
  }'

# 2. Send notification
curl -X POST http://localhost:8082/api/notifications \
  -H "Content-Type: application/json" \
  -d '{
    "assocMemberId": 1,
    "message": "You have been issued a fine for late payment",
    "notificationType": "FINE_NOTICE"
  }'

# 3. Member submits disciplinary case
curl -X POST http://localhost:8082/api/disciplinary-workflows \
  -H "Content-Type: application/json" \
  -d '{
    "levyFineId": 1,
    "assocMemberId": 1,
    "caseStatement": "Financial difficulties, requesting payment plan"
  }'

# 4. Secretary reviews case
curl -X PUT http://localhost:8082/api/disciplinary-workflows/1/secretary-review \
  -H "Content-Type: application/json" \
  -d '{
    "decision": "Approved",
    "paymentArrangement": "R50 per week for 2 weeks",
    "override": false
  }'

# 5. Check disciplinary case status
curl http://localhost:8082/api/disciplinary-workflows/1
```

---

## Common Test Scenarios

### Scenario 1: New Member Registration
1. Create user role (if not exists)
2. Create user account
3. Submit membership application
4. Secretary reviews application
5. Chairperson approves
6. Create member record
7. Set up member finances

### Scenario 2: Monthly Financial Operations
1. Get all active members
2. Create levy payments for each member
3. Send payment reminders
4. Issue receipts for payments received
5. Issue fines for late payments
6. Generate financial reports

### Scenario 3: Taxi Fleet Management
1. Register new driver
2. Create route
3. Register new taxi
4. Assign driver to taxi
5. Assign taxi to route
6. Track taxi status

---

## Monitoring and Debugging

### Health Check
```bash
curl http://localhost:8082/actuator/health
```

### Application Metrics
```bash
curl http://localhost:8082/actuator/metrics
```

### View Audit Logs
```bash
# By table
curl http://localhost:8082/api/audit/table/users

# By record
curl http://localhost:8082/api/audit/record/users/1

# By user
curl http://localhost:8082/api/audit/user/admin_user

# By date range
curl "http://localhost:8082/api/audit/date-range?startDate=2025-11-01T00:00:00&endDate=2025-11-16T23:59:59"
```

---

## Database Inspection

### Connect to PostgreSQL
```bash
docker exec -it taxi-service-postgres psql -U postgres -d taxidb
```

### Common Queries
```sql
-- Check all tables
\dt

-- View users
SELECT * FROM users;

-- View members
SELECT * FROM assoc_members;

-- View levy payments
SELECT * FROM levy_payments WHERE payment_status = 'Pending';

-- View audit logs
SELECT * FROM audit_log ORDER BY action_at DESC LIMIT 10;

-- Exit
\q
```

---

## Troubleshooting

### Application won't start
```bash
# Check if port 8082 is available
lsof -i :8082

# Check PostgreSQL connection
docker ps | grep postgres

# View application logs
tail -f logs/application.log
```

### API returns 404
- Verify application is running
- Check endpoint path in Swagger UI
- Ensure correct HTTP method (GET, POST, PUT, DELETE)

### Validation errors
- Check request body matches required fields
- Verify field types (string, number, boolean)
- Check for required fields marked in API documentation

---

## Additional Resources

- **Full API Documentation:** `/API_DOCUMENTATION.md`
- **Swagger UI:** http://localhost:8082/swagger-ui.html
- **OpenAPI Spec:** http://localhost:8082/api-docs
- **Postman Collection:** `/postman/Taxi-Management-API.postman_collection.json`
- **Test Scripts:** `/scripts/test-complete-workflow.sh`
- **Integration Tests:** `/src/test/java/com/taxiservice/integration/`

---

## Support

For issues or questions:
- Check Swagger UI for endpoint documentation
- Review API_DOCUMENTATION.md for detailed specs
- Run integration tests to verify setup
- Check application logs for errors
