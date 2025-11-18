#!/bin/bash

# Comprehensive Test Script for Taxi Management System
# This script tests complete user journeys end-to-end

set -e  # Exit on error

BASE_URL="http://localhost:8082"
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Function to print colored output
print_success() {
    echo -e "${GREEN}✓ $1${NC}"
}

print_error() {
    echo -e "${RED}✗ $1${NC}"
}

print_info() {
    echo -e "${YELLOW}ℹ $1${NC}"
}

# Function to make API call and extract ID from response
api_post() {
    local endpoint=$1
    local data=$2
    local response=$(curl -s -X POST "$BASE_URL$endpoint" \
        -H "Content-Type: application/json" \
        -d "$data")
    echo "$response"
}

api_get() {
    local endpoint=$1
    curl -s -X GET "$BASE_URL$endpoint"
}

api_put() {
    local endpoint=$1
    local data=$2
    curl -s -X PUT "$BASE_URL$endpoint" \
        -H "Content-Type: application/json" \
        -d "$data"
}

# Extract ID from JSON response
extract_id() {
    local json=$1
    local field=$2
    echo "$json" | grep -o "\"$field\":[0-9]*" | grep -o "[0-9]*"
}

echo "================================"
echo "Taxi Management System - Complete Workflow Test"
echo "================================"
echo ""

# Test 1: Health Check
print_info "Test 1: Checking application health..."
health=$(curl -s "$BASE_URL/actuator/health")
if echo "$health" | grep -q '"status":"UP"'; then
    print_success "Application is healthy"
else
    print_error "Application is not healthy"
    exit 1
fi
echo ""

# Test 2: Create User Role
print_info "Test 2: Creating user role..."
role_response=$(api_post "/api/user-roles" '{
  "roleName": "Admin Clerk",
  "permissions": "{\"canManageMembers\": true, \"canManagePayments\": true}"
}')
role_id=$(extract_id "$role_response" "roleId")
if [ -n "$role_id" ]; then
    print_success "User role created with ID: $role_id"
else
    print_error "Failed to create user role"
    echo "$role_response"
fi
echo ""

# Test 3: Create User
print_info "Test 3: Creating user..."
user_response=$(api_post "/api/users" "{
  \"username\": \"test_admin\",
  \"password\": \"SecurePass123!\",
  \"fullName\": \"Test Administrator\",
  \"contactEmail\": \"admin@test.com\",
  \"roleId\": $role_id,
  \"active\": true
}")
user_id=$(extract_id "$user_response" "userId")
if [ -n "$user_id" ]; then
    print_success "User created with ID: $user_id"
else
    print_error "Failed to create user"
fi
echo ""

# Test 4: Create Driver
print_info "Test 4: Creating driver..."
driver_response=$(api_post "/api/drivers" '{
  "name": "John Driver",
  "licenseNumber": "DL-2025-001",
  "contactNumber": "+27123456789"
}')
driver_id=$(extract_id "$driver_response" "driverId")
if [ -n "$driver_id" ]; then
    print_success "Driver created with ID: $driver_id"
else
    print_error "Failed to create driver"
fi
echo ""

# Test 5: Create Route
print_info "Test 5: Creating route..."
route_response=$(api_post "/api/routes" '{
  "name": "Route A - CBD to Sandton",
  "startPoint": "Johannesburg CBD",
  "endPoint": "Sandton",
  "isActive": true
}')
route_id=$(extract_id "$route_response" "routeId")
if [ -n "$route_id" ]; then
    print_success "Route created with ID: $route_id"
else
    print_error "Failed to create route"
fi
echo ""

# Test 6: Create Taxi
print_info "Test 6: Creating taxi..."
taxi_response=$(api_post "/api/taxis" '{
  "plateNumber": "GP-123-TEST",
  "model": "Toyota Quantum",
  "capacity": 15
}')
taxi_id=$(extract_id "$taxi_response" "taxiId")
if [ -n "$taxi_id" ]; then
    print_success "Taxi created with ID: $taxi_id"
else
    print_error "Failed to create taxi"
fi
echo ""

# Test 7: Assign Driver to Taxi
print_info "Test 7: Assigning driver to taxi..."
assign_response=$(api_put "/api/taxis/$taxi_id/driver/$driver_id" "")
if echo "$assign_response" | grep -q "success"; then
    print_success "Driver assigned to taxi"
else
    print_error "Failed to assign driver"
fi
echo ""

# Test 8: Assign Route to Taxi
print_info "Test 8: Assigning route to taxi..."
route_assign_response=$(api_put "/api/taxis/$taxi_id/route/$route_id" "")
if echo "$route_assign_response" | grep -q "success"; then
    print_success "Route assigned to taxi"
else
    print_error "Failed to assign route"
fi
echo ""

# Test 9: Create Payment Method
print_info "Test 9: Creating payment method..."
payment_method_response=$(api_post "/api/payment-methods" '{
  "methodName": "Cash",
  "description": "Cash payment at office"
}')
payment_method_id=$(extract_id "$payment_method_response" "paymentMethodId")
if [ -n "$payment_method_id" ]; then
    print_success "Payment method created with ID: $payment_method_id"
else
    print_error "Failed to create payment method"
fi
echo ""

# Test 10: Complete Member Onboarding Workflow
print_info "Test 10: Starting member onboarding workflow..."

# 10a: Create membership application
print_info "  10a: Creating membership application..."
app_response=$(api_post "/api/membership-applications" "{
  \"applicantName\": \"Sarah Applicant\",
  \"contactNumber\": \"+27987654321\",
  \"routeId\": $route_id,
  \"applicationStatus\": \"Pending\"
}")
app_id=$(extract_id "$app_response" "applicationId")
if [ -n "$app_id" ]; then
    print_success "  Membership application created with ID: $app_id"
else
    print_error "  Failed to create application"
fi

# 10b: Secretary review
print_info "  10b: Secretary reviewing application..."
secretary_response=$(api_put "/api/membership-applications/$app_id/secretary-review" '{
  "decision": "Approved",
  "comments": "All documents verified"
}')
if echo "$secretary_response" | grep -q "success"; then
    print_success "  Secretary approved application"
else
    print_error "  Secretary review failed"
fi

# 10c: Chairperson final approval
print_info "  10c: Chairperson final approval..."
chairperson_response=$(api_put "/api/membership-applications/$app_id/chairperson-review" '{
  "decision": "Approved",
  "comments": "Approved for membership"
}')
if echo "$chairperson_response" | grep -q "success"; then
    print_success "  Chairperson approved application"
else
    print_error "  Chairperson approval failed"
fi

# 10d: Create member
print_info "  10d: Creating association member..."
member_response=$(api_post "/api/members" '{
  "name": "Sarah Member",
  "contactNumber": "+27987654321",
  "squadNumber": "SQ-001",
  "blacklisted": false
}')
member_id=$(extract_id "$member_response" "assocMemberId")
if [ -n "$member_id" ]; then
    print_success "  Member created with ID: $member_id"
else
    print_error "  Failed to create member"
fi
echo ""

# Test 11: Financial Management Workflow
print_info "Test 11: Testing financial management..."

# 11a: Create levy payment
print_info "  11a: Creating levy payment..."
levy_response=$(api_post "/api/levy-payments" "{
  \"assocMemberId\": $member_id,
  \"weekStartDate\": \"2025-11-10\",
  \"weekEndDate\": \"2025-11-16\",
  \"amount\": 500.00,
  \"paymentStatus\": \"Pending\",
  \"paymentMethodId\": $payment_method_id
}")
if echo "$levy_response" | grep -q "levyPaymentId"; then
    print_success "  Levy payment created"
else
    print_error "  Failed to create levy payment"
fi

# 11b: Create fine
print_info "  11b: Creating fine for late payment..."
fine_response=$(api_post "/api/levy-fines" "{
  \"assocMemberId\": $member_id,
  \"fineAmount\": 50.00,
  \"fineReason\": \"Late payment of weekly levy\",
  \"fineStatus\": \"Unpaid\"
}")
fine_id=$(extract_id "$fine_response" "fineId")
if [ -n "$fine_id" ]; then
    print_success "  Fine created with ID: $fine_id"
else
    print_error "  Failed to create fine"
fi

# 11c: Check outstanding balance
print_info "  11c: Checking member's outstanding balance..."
balance_response=$(api_get "/api/member-finance/$member_id/balance")
if echo "$balance_response" | grep -q "outstandingBalance"; then
    print_success "  Retrieved member's outstanding balance"
else
    print_error "  Failed to retrieve balance"
fi

# 11d: Create receipt
print_info "  11d: Creating receipt..."
receipt_response=$(api_post "/api/receipts" "{
  \"assocMemberId\": $member_id,
  \"amount\": 500.00,
  \"paymentMethodId\": $payment_method_id,
  \"receiptNumber\": \"RCT-TEST-001\",
  \"issuedBy\": \"Test Cashier\"
}")
if echo "$receipt_response" | grep -q "receiptId"; then
    print_success "  Receipt created"
else
    print_error "  Failed to create receipt"
fi
echo ""

# Test 12: Disciplinary Workflow
print_info "Test 12: Testing disciplinary workflow..."

# 12a: Create disciplinary case
print_info "  12a: Creating disciplinary case..."
disciplinary_response=$(api_post "/api/disciplinary-workflows" "{
  \"levyFineId\": $fine_id,
  \"assocMemberId\": $member_id,
  \"caseStatement\": \"I was unable to pay due to financial difficulties. Requesting payment plan.\"
}")
disciplinary_id=$(extract_id "$disciplinary_response" "workflowId")
if [ -n "$disciplinary_id" ]; then
    print_success "  Disciplinary case created with ID: $disciplinary_id"
else
    print_error "  Failed to create disciplinary case"
fi

# 12b: Secretary review
print_info "  12b: Secretary reviewing disciplinary case..."
secretary_disc_response=$(api_put "/api/disciplinary-workflows/$disciplinary_id/secretary-review" '{
  "decision": "Approved",
  "paymentArrangement": "Payment plan: R25 per week for 2 weeks",
  "override": false
}')
if echo "$secretary_disc_response" | grep -q "success"; then
    print_success "  Secretary approved payment plan"
else
    print_error "  Secretary review failed"
fi
echo ""

# Test 13: Notification System
print_info "Test 13: Testing notification system..."
notification_response=$(api_post "/api/notifications" "{
  \"assocMemberId\": $member_id,
  \"message\": \"Your payment plan has been approved\",
  \"notificationType\": \"SYSTEM_ALERT\"
}")
if echo "$notification_response" | grep -q "notificationId"; then
    print_success "Notification sent"
else
    print_error "Failed to send notification"
fi
echo ""

# Test 14: Audit Logs
print_info "Test 14: Checking audit logs..."
audit_response=$(api_get "/api/audit/table/users")
if echo "$audit_response" | grep -q "auditId"; then
    print_success "Audit logs retrieved successfully"
else
    print_error "Failed to retrieve audit logs"
fi
echo ""

# Summary
echo "================================"
echo "Test Summary"
echo "================================"
print_success "All workflow tests completed!"
echo ""
echo "Created Resources:"
echo "  - User Role ID: $role_id"
echo "  - User ID: $user_id"
echo "  - Driver ID: $driver_id"
echo "  - Route ID: $route_id"
echo "  - Taxi ID: $taxi_id"
echo "  - Payment Method ID: $payment_method_id"
echo "  - Member ID: $member_id"
echo "  - Application ID: $app_id"
echo "  - Fine ID: $fine_id"
echo "  - Disciplinary Case ID: $disciplinary_id"
echo ""
print_info "You can now test these resources using the Swagger UI at:"
echo "  $BASE_URL/swagger-ui.html"
echo ""
