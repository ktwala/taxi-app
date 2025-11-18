# Taxi Management System - API Documentation

## Table of Contents
- [Overview](#overview)
- [Base URL](#base-url)
- [Authentication](#authentication)
- [Response Format](#response-format)
- [Error Codes](#error-codes)
- [API Endpoints](#api-endpoints)
  - [User Management](#1-user-management)
  - [Driver Management](#2-driver-management)
  - [Route Management](#3-route-management)
  - [Taxi Management](#4-taxi-management)
  - [Payment Methods](#5-payment-methods)
  - [Member Management](#6-member-management)
  - [Financial Management](#7-financial-management)
  - [Disciplinary Workflow](#8-disciplinary-workflow)
  - [Notifications](#9-notifications)
  - [Audit Logs](#10-audit-logs)

---

## Overview

The Taxi Management System API provides comprehensive endpoints for managing taxi association operations including member management, financial transactions, levy payments, fines, and disciplinary workflows.

**Version:** 1.0.0
**License:** MIT

---

## Base URL

```
Local Development: http://localhost:8082
Production: https://api.taxiservice.com
```

---

## Authentication

**Note:** Authentication is not yet implemented. Future versions will use JWT Bearer tokens.

```http
Authorization: Bearer <token>
```

---

## Response Format

All API responses follow this standard format:

### Success Response
```json
{
  "success": true,
  "message": "Operation completed successfully",
  "data": {
    // Response data object
  },
  "timestamp": "2025-11-16T10:30:00"
}
```

### Error Response
```json
{
  "success": false,
  "message": "Error description",
  "errors": [
    {
      "field": "fieldName",
      "message": "Validation error message"
    }
  ],
  "timestamp": "2025-11-16T10:30:00"
}
```

---

## Error Codes

| HTTP Code | Description |
|-----------|-------------|
| 200 | Success |
| 201 | Created |
| 400 | Bad Request - Invalid input |
| 404 | Not Found - Resource doesn't exist |
| 409 | Conflict - Duplicate resource |
| 500 | Internal Server Error |

---

## API Endpoints

---

## 1. User Management

### 1.1 Create User Role

Create a new user role with specific permissions.

**Endpoint:** `POST /api/user-roles`

**Request Body:**
```json
{
  "roleName": "string (required, unique, max 50 chars)",
  "permissions": "string (JSON format, optional)"
}
```

**Example Request:**
```json
{
  "roleName": "Admin Clerk",
  "permissions": "{\"canManageMembers\": true, \"canManagePayments\": true, \"canViewReports\": true}"
}
```

**Response:** `200 OK`
```json
{
  "success": true,
  "message": "User role created successfully",
  "data": {
    "roleId": 1,
    "roleName": "Admin Clerk",
    "permissions": "{\"canManageMembers\": true, \"canManagePayments\": true}",
    "createdAt": "2025-11-16T10:30:00",
    "updatedAt": "2025-11-16T10:30:00"
  }
}
```

**Possible Errors:**
- `409 Conflict` - Role name already exists

---

### 1.2 Get All User Roles

**Endpoint:** `GET /api/user-roles`

**Response:** `200 OK`
```json
{
  "success": true,
  "data": [
    {
      "roleId": 1,
      "roleName": "Admin Clerk",
      "permissions": "{}",
      "createdAt": "2025-11-16T10:30:00"
    }
  ]
}
```

---

### 1.3 Create User

Create a new system user.

**Endpoint:** `POST /api/users`

**Request Body:**
```json
{
  "username": "string (required, unique, 3-50 chars)",
  "password": "string (required, min 6 chars)",
  "fullName": "string (required)",
  "contactEmail": "string (email format, optional)",
  "roleId": "number (required)",
  "active": "boolean (optional, default: true)"
}
```

**Example Request:**
```json
{
  "username": "admin_user",
  "password": "SecurePass123!",
  "fullName": "John Administrator",
  "contactEmail": "admin@taxiservice.com",
  "roleId": 1,
  "active": true
}
```

**Response:** `200 OK`
```json
{
  "success": true,
  "message": "User created successfully",
  "data": {
    "userId": 1,
    "username": "admin_user",
    "fullName": "John Administrator",
    "contactEmail": "admin@taxiservice.com",
    "roleId": 1,
    "roleName": "Admin Clerk",
    "active": true,
    "createdAt": "2025-11-16T10:30:00",
    "updatedAt": "2025-11-16T10:30:00"
  }
}
```

**Possible Errors:**
- `400 Bad Request` - Validation errors
- `404 Not Found` - Role ID doesn't exist
- `409 Conflict` - Username or email already exists

---

### 1.4 Get All Users

**Endpoint:** `GET /api/users`

**Response:** `200 OK`
```json
{
  "success": true,
  "data": [/* array of users */]
}
```

---

### 1.5 Get User by ID

**Endpoint:** `GET /api/users/{userId}`

**Path Parameters:**
- `userId` (number, required) - The user ID

**Response:** `200 OK`
```json
{
  "success": true,
  "data": {/* user object */}
}
```

**Possible Errors:**
- `404 Not Found` - User doesn't exist

---

### 1.6 Update User

**Endpoint:** `PUT /api/users/{userId}`

**Request Body:** Same as Create User

**Response:** `200 OK`

---

### 1.7 Delete User

**Endpoint:** `DELETE /api/users/{userId}`

**Response:** `200 OK`

---

## 2. Driver Management

### 2.1 Create Driver

**Endpoint:** `POST /api/drivers`

**Request Body:**
```json
{
  "name": "string (required)",
  "licenseNumber": "string (required, unique)",
  "contactNumber": "string (required)"
}
```

**Example:**
```json
{
  "name": "John Driver",
  "licenseNumber": "DL-2025-001234",
  "contactNumber": "+27123456789"
}
```

**Response:** `200 OK`
```json
{
  "success": true,
  "data": {
    "driverId": 1,
    "name": "John Driver",
    "licenseNumber": "DL-2025-001234",
    "contactNumber": "+27123456789",
    "createdAt": "2025-11-16T10:30:00",
    "updatedAt": "2025-11-16T10:30:00"
  }
}
```

---

### 2.2 Get All Drivers

**Endpoint:** `GET /api/drivers`

---

### 2.3 Get Driver by ID

**Endpoint:** `GET /api/drivers/{driverId}`

---

### 2.4 Update Driver

**Endpoint:** `PUT /api/drivers/{driverId}`

---

### 2.5 Delete Driver

**Endpoint:** `DELETE /api/drivers/{driverId}`

---

## 3. Route Management

### 3.1 Create Route

**Endpoint:** `POST /api/routes`

**Request Body:**
```json
{
  "name": "string (required)",
  "startPoint": "string (required)",
  "endPoint": "string (required)",
  "isActive": "boolean (optional, default: true)"
}
```

**Example:**
```json
{
  "name": "Route A - City Center",
  "startPoint": "Johannesburg CBD",
  "endPoint": "Sandton",
  "isActive": true
}
```

**Response:** `200 OK`
```json
{
  "success": true,
  "data": {
    "routeId": 1,
    "name": "Route A - City Center",
    "startPoint": "Johannesburg CBD",
    "endPoint": "Sandton",
    "isActive": true,
    "createdAt": "2025-11-16T10:30:00",
    "updatedAt": "2025-11-16T10:30:00"
  }
}
```

---

### 3.2 Get All Routes

**Endpoint:** `GET /api/routes`

---

### 3.3 Get Active Routes

**Endpoint:** `GET /api/routes/active`

Returns only routes where `isActive = true`

---

### 3.4 Get Route by ID

**Endpoint:** `GET /api/routes/{routeId}`

---

### 3.5 Update Route

**Endpoint:** `PUT /api/routes/{routeId}`

---

### 3.6 Delete Route

**Endpoint:** `DELETE /api/routes/{routeId}`

---

## 4. Taxi Management

### 4.1 Create Taxi

**Endpoint:** `POST /api/taxis`

**Request Body:**
```json
{
  "plateNumber": "string (required, unique)",
  "model": "string (optional)",
  "capacity": "number (optional)",
  "driverId": "number (optional)",
  "routeId": "number (optional)"
}
```

**Example:**
```json
{
  "plateNumber": "GP-123-456",
  "model": "Toyota Quantum",
  "capacity": 15
}
```

**Response:** `200 OK`
```json
{
  "success": true,
  "data": {
    "taxiId": 1,
    "plateNumber": "GP-123-456",
    "model": "Toyota Quantum",
    "capacity": 15,
    "driverId": null,
    "routeId": null,
    "createdAt": "2025-11-16T10:30:00",
    "updatedAt": "2025-11-16T10:30:00"
  }
}
```

**Possible Errors:**
- `409 Conflict` - Plate number already exists

---

### 4.2 Get All Taxis

**Endpoint:** `GET /api/taxis`

---

### 4.3 Get Taxi by ID

**Endpoint:** `GET /api/taxis/{taxiId}`

---

### 4.4 Assign Driver to Taxi

**Endpoint:** `PUT /api/taxis/{taxiId}/driver/{driverId}`

**Path Parameters:**
- `taxiId` (number, required)
- `driverId` (number, required)

**Response:** `200 OK`

**Possible Errors:**
- `404 Not Found` - Taxi or driver doesn't exist

---

### 4.5 Assign Route to Taxi

**Endpoint:** `PUT /api/taxis/{taxiId}/route/{routeId}`

**Path Parameters:**
- `taxiId` (number, required)
- `routeId` (number, required)

**Response:** `200 OK`

---

### 4.6 Get Taxis by Driver

**Endpoint:** `GET /api/taxis/driver/{driverId}`

---

### 4.7 Get Taxis by Route

**Endpoint:** `GET /api/taxis/route/{routeId}`

---

### 4.8 Get Unassigned Taxis

**Endpoint:** `GET /api/taxis/unassigned`

Returns taxis without assigned drivers.

---

## 5. Payment Methods

### 5.1 Create Payment Method

**Endpoint:** `POST /api/payment-methods`

**Request Body:**
```json
{
  "methodName": "string (required, unique, max 50 chars)",
  "description": "string (optional)"
}
```

**Example:**
```json
{
  "methodName": "Cash",
  "description": "Cash payment at office"
}
```

**Supported Payment Methods:**
- Cash
- Bank Transfer
- EFT (Electronic Funds Transfer)
- Cheque

---

### 5.2 Get All Payment Methods

**Endpoint:** `GET /api/payment-methods`

---

### 5.3 Get Active Payment Methods

**Endpoint:** `GET /api/payment-methods/active`

---

## 6. Member Management

### 6.1 Create Membership Application

**Endpoint:** `POST /api/membership-applications`

**Request Body:**
```json
{
  "applicantName": "string (required)",
  "contactNumber": "string (required)",
  "routeId": "number (optional)",
  "applicationStatus": "string (optional, default: 'Pending')"
}
```

**Application Status Values:**
- `Pending` - Initial state
- `Under Review` - Secretary reviewing
- `Interview Required` - Requires interview
- `Approved` - Approved by both secretary and chairperson
- `Rejected` - Application rejected

**Example:**
```json
{
  "applicantName": "Sarah Applicant",
  "contactNumber": "+27987654321",
  "routeId": 1,
  "applicationStatus": "Pending"
}
```

**Response:** `200 OK`
```json
{
  "success": true,
  "data": {
    "applicationId": 1,
    "applicantName": "Sarah Applicant",
    "contactNumber": "+27987654321",
    "routeId": 1,
    "applicationStatus": "Pending",
    "secretaryDecision": null,
    "chairpersonDecision": null,
    "createdAt": "2025-11-16T10:30:00"
  }
}
```

---

### 6.2 Secretary Review Application

**Endpoint:** `PUT /api/membership-applications/{applicationId}/secretary-review`

**Request Body:**
```json
{
  "decision": "string (required: 'Approved', 'Rejected', 'Interview')",
  "comments": "string (optional)"
}
```

**Example:**
```json
{
  "decision": "Approved",
  "comments": "All documents verified successfully"
}
```

---

### 6.3 Chairperson Final Approval

**Endpoint:** `PUT /api/membership-applications/{applicationId}/chairperson-review`

**Request Body:**
```json
{
  "decision": "string (required: 'Approved' or 'Rejected')",
  "comments": "string (optional)"
}
```

---

### 6.4 Get All Applications

**Endpoint:** `GET /api/membership-applications`

---

### 6.5 Get Applications by Status

**Endpoint:** `GET /api/membership-applications/status/{status}`

---

### 6.6 Create Association Member

**Endpoint:** `POST /api/members`

**Request Body:**
```json
{
  "name": "string (required)",
  "contactNumber": "string (required)",
  "squadNumber": "string (required, unique)",
  "blacklisted": "boolean (optional, default: false)"
}
```

**Example:**
```json
{
  "name": "Sarah Member",
  "contactNumber": "+27987654321",
  "squadNumber": "SQ-001",
  "blacklisted": false
}
```

**Response:** `200 OK`
```json
{
  "success": true,
  "data": {
    "assocMemberId": 1,
    "name": "Sarah Member",
    "contactNumber": "+27987654321",
    "squadNumber": "SQ-001",
    "blacklisted": false,
    "createdAt": "2025-11-16T10:30:00",
    "updatedAt": "2025-11-16T10:30:00"
  }
}
```

---

### 6.7 Get All Members

**Endpoint:** `GET /api/members`

---

### 6.8 Get Active Members

**Endpoint:** `GET /api/members/active`

Returns non-blacklisted members.

---

### 6.9 Get Member by ID

**Endpoint:** `GET /api/members/{memberId}`

---

### 6.10 Blacklist Member

**Endpoint:** `PUT /api/members/{memberId}/blacklist`

---

### 6.11 Remove from Blacklist

**Endpoint:** `PUT /api/members/{memberId}/unblacklist`

---

## 7. Financial Management

### 7.1 Create Levy Payment

**Endpoint:** `POST /api/levy-payments`

**Request Body:**
```json
{
  "assocMemberId": "number (required)",
  "weekStartDate": "date (required, format: YYYY-MM-DD)",
  "weekEndDate": "date (required, format: YYYY-MM-DD)",
  "amount": "number (required, positive)",
  "paymentStatus": "string (optional, default: 'Pending')",
  "paymentMethodId": "number (optional)"
}
```

**Payment Status Values:**
- `Pending` - Payment not yet made
- `Paid` - Payment completed
- `Overdue` - Payment past due date

**Example:**
```json
{
  "assocMemberId": 1,
  "weekStartDate": "2025-11-10",
  "weekEndDate": "2025-11-16",
  "amount": 500.00,
  "paymentStatus": "Pending",
  "paymentMethodId": 1
}
```

**Response:** `200 OK`
```json
{
  "success": true,
  "data": {
    "levyPaymentId": 1,
    "assocMemberId": 1,
    "weekStartDate": "2025-11-10",
    "weekEndDate": "2025-11-16",
    "amount": 500.00,
    "paymentStatus": "Pending",
    "paymentMethodId": 1,
    "createdAt": "2025-11-16T10:30:00"
  }
}
```

---

### 7.2 Get Member Levy Payments

**Endpoint:** `GET /api/levy-payments/member/{memberId}`

---

### 7.3 Get Overdue Levy Payments

**Endpoint:** `GET /api/levy-payments/overdue`

---

### 7.4 Update Levy Payment Status

**Endpoint:** `PUT /api/levy-payments/{paymentId}/status`

**Request Body:**
```json
{
  "paymentStatus": "string (required: 'Paid', 'Pending', 'Overdue')"
}
```

---

### 7.5 Create Fine

**Endpoint:** `POST /api/levy-fines`

**Request Body:**
```json
{
  "assocMemberId": "number (required)",
  "fineAmount": "number (required, positive)",
  "fineReason": "string (required)",
  "fineStatus": "string (optional, default: 'Unpaid')",
  "paymentMethodId": "number (optional)"
}
```

**Fine Status Values:**
- `Unpaid` - Fine not yet paid
- `Paid` - Fine settled
- `Disputed` - Under disciplinary review
- `Waived` - Fine waived by chairperson

**Example:**
```json
{
  "assocMemberId": 1,
  "fineAmount": 100.00,
  "fineReason": "Late payment of weekly levy",
  "fineStatus": "Unpaid"
}
```

---

### 7.6 Get Member Fines

**Endpoint:** `GET /api/levy-fines/member/{memberId}`

---

### 7.7 Get Unpaid Fines

**Endpoint:** `GET /api/levy-fines/unpaid`

---

### 7.8 Get Member Finance Summary

**Endpoint:** `GET /api/member-finance/{memberId}`

**Response:**
```json
{
  "success": true,
  "data": {
    "memberFinanceId": 1,
    "assocMemberId": 1,
    "joiningFeeAmount": 1000.00,
    "joiningFeePaid": true,
    "membershipCardIssued": true,
    "outstandingBalance": 150.00,
    "totalLevyPaid": 5000.00,
    "totalFinesPaid": 200.00
  }
}
```

---

### 7.9 Get Member Outstanding Balance

**Endpoint:** `GET /api/member-finance/{memberId}/balance`

**Response:**
```json
{
  "success": true,
  "data": {
    "outstandingBalance": 150.00
  }
}
```

---

### 7.10 Create Receipt

**Endpoint:** `POST /api/receipts`

**Request Body:**
```json
{
  "assocMemberId": "number (required)",
  "amount": "number (required, positive)",
  "paymentMethodId": "number (required)",
  "receiptNumber": "string (required, unique)",
  "issuedBy": "string (required)"
}
```

**Example:**
```json
{
  "assocMemberId": 1,
  "amount": 500.00,
  "paymentMethodId": 1,
  "receiptNumber": "RCT-2025-001",
  "issuedBy": "John Cashier"
}
```

---

### 7.11 Get Member Receipts

**Endpoint:** `GET /api/receipts/member/{memberId}`

---

### 7.12 Get Receipt by Number

**Endpoint:** `GET /api/receipts/number/{receiptNumber}`

---

## 8. Disciplinary Workflow

### 8.1 Create Disciplinary Case

**Endpoint:** `POST /api/disciplinary-workflows`

**Request Body:**
```json
{
  "levyFineId": "number (required)",
  "assocMemberId": "number (required)",
  "caseStatement": "string (required)"
}
```

**Example:**
```json
{
  "levyFineId": 1,
  "assocMemberId": 1,
  "caseStatement": "I was unable to pay on time due to unexpected medical expenses. I am requesting a payment plan to settle the fine over the next month."
}
```

**Response:** `200 OK`
```json
{
  "success": true,
  "data": {
    "workflowId": 1,
    "levyFineId": 1,
    "assocMemberId": 1,
    "caseStatement": "...",
    "secretaryDecision": null,
    "chairpersonDecision": null,
    "paymentArrangement": null,
    "createdAt": "2025-11-16T10:30:00"
  }
}
```

---

### 8.2 Secretary Review

**Endpoint:** `PUT /api/disciplinary-workflows/{workflowId}/secretary-review`

**Request Body:**
```json
{
  "decision": "string (required: 'Approved', 'Rejected')",
  "paymentArrangement": "string (optional)",
  "override": "boolean (optional, default: false)"
}
```

**Example:**
```json
{
  "decision": "Approved",
  "paymentArrangement": "Payment plan: R50 per week for 2 weeks",
  "override": false
}
```

---

### 8.3 Chairperson Review

**Endpoint:** `PUT /api/disciplinary-workflows/{workflowId}/chairperson-review`

**Request Body:** Same as Secretary Review

---

### 8.4 Get Member Disciplinary Cases

**Endpoint:** `GET /api/disciplinary-workflows/member/{memberId}`

---

### 8.5 Get Pending Cases

**Endpoint:** `GET /api/disciplinary-workflows/pending`

---

## 9. Notifications

### 9.1 Send Notification

**Endpoint:** `POST /api/notifications`

**Request Body:**
```json
{
  "assocMemberId": "number (required)",
  "message": "string (required)",
  "notificationType": "string (required)"
}
```

**Notification Types:**
- `PAYMENT_REMINDER` - Levy payment due
- `FINE_NOTICE` - Fine issued
- `APPLICATION_STATUS` - Application update
- `SYSTEM_ALERT` - General system notification

**Example:**
```json
{
  "assocMemberId": 1,
  "message": "Your weekly levy payment of R500 is due by Friday",
  "notificationType": "PAYMENT_REMINDER"
}
```

---

### 9.2 Get Member Notifications

**Endpoint:** `GET /api/notifications/member/{memberId}`

---

### 9.3 Get Unread Notifications

**Endpoint:** `GET /api/notifications/member/{memberId}/unread`

---

### 9.4 Mark Notification as Read

**Endpoint:** `PUT /api/notifications/{notificationId}/read`

---

## 10. Audit Logs

### 10.1 Get Audit Logs by Table

**Endpoint:** `GET /api/audit/table/{tableName}`

**Path Parameters:**
- `tableName` (string, required) - Database table name

**Example:**
```
GET /api/audit/table/users
GET /api/audit/table/levy_payments
```

**Response:**
```json
{
  "success": true,
  "data": [
    {
      "auditId": 1,
      "tableName": "users",
      "recordId": 1,
      "actionType": "INSERT",
      "actionBy": "admin_user",
      "actionAt": "2025-11-16T10:30:00",
      "oldData": null,
      "newData": "{\"username\":\"admin_user\",...}"
    }
  ]
}
```

---

### 10.2 Get Audit Logs for Specific Record

**Endpoint:** `GET /api/audit/record/{tableName}/{recordId}`

**Path Parameters:**
- `tableName` (string, required)
- `recordId` (number, required)

---

### 10.3 Get Audit Logs by User

**Endpoint:** `GET /api/audit/user/{actionBy}`

---

### 10.4 Get Audit Logs by Action Type

**Endpoint:** `GET /api/audit/action/{actionType}`

**Action Types:**
- `INSERT`
- `UPDATE`
- `DELETE`

---

### 10.5 Get Audit Logs by Date Range

**Endpoint:** `GET /api/audit/date-range`

**Query Parameters:**
- `startDate` (date, required, format: YYYY-MM-DDTHH:mm:ss)
- `endDate` (date, required, format: YYYY-MM-DDTHH:mm:ss)

**Example:**
```
GET /api/audit/date-range?startDate=2025-11-01T00:00:00&endDate=2025-11-16T23:59:59
```

---

## Common Workflows

### Complete Member Onboarding

```
1. POST /api/membership-applications     (Create application)
2. PUT  /api/membership-applications/{id}/secretary-review  (Secretary approves)
3. PUT  /api/membership-applications/{id}/chairperson-review (Chairperson approves)
4. POST /api/members                     (Create member record)
5. POST /api/member-finance              (Set up finances)
```

### Weekly Levy Collection

```
1. GET  /api/members/active              (Get all active members)
2. POST /api/levy-payments (for each)    (Create levy payments)
3. POST /api/receipts (when paid)        (Issue receipts)
4. POST /api/notifications (reminders)   (Send payment reminders)
```

### Fine and Disciplinary Process

```
1. POST /api/levy-fines                  (Issue fine)
2. POST /api/notifications               (Notify member)
3. POST /api/disciplinary-workflows      (Member submits case)
4. PUT  /api/disciplinary-workflows/{id}/secretary-review (Review)
5. PUT  /api/disciplinary-workflows/{id}/chairperson-review (Final decision)
6. PUT  /api/levy-fines/{id}/status      (Update fine status)
```

---

## Additional Resources

- **Swagger UI:** http://localhost:8082/swagger-ui.html
- **OpenAPI Spec:** http://localhost:8082/api-docs
- **Health Check:** http://localhost:8082/actuator/health
- **Postman Collection:** See `/postman/Taxi-Management-API.postman_collection.json`

---

## Support

For support and questions, contact: support@taxiservice.com
