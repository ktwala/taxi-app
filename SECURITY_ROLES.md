# Security Roles and Access Control

This document describes the role-based access control (RBAC) configuration for the Taxi Management API.

## Roles

The system supports the following roles:

- **ADMIN** - Full system access
- **CHAIRPERSON** - Approval authority for applications, disciplinary actions, financial oversight
- **SECRETARY** - Administrative operations for members, applications, and basic management
- **MEMBER** - Limited access to own data and basic queries

## Controller Access Control

### Authentication (No restrictions)
- **AuthController** - `/api/auth/**`
  - All endpoints are public for login/registration

### User Management
- **UserController** - `/api/users` ✅ **SECURED**
  - All endpoints: `@PreAuthorize("hasRole('ADMIN')")`
  - Only administrators can manage users

- **UserRoleController** - `/api/user-roles`
  - Recommended: `@PreAuthorize("hasRole('ADMIN')")`
  - Only administrators should manage roles

### Driver & Route Management
- **DriverController** - `/api/drivers`
  - Recommended: `@PreAuthorize("hasAnyRole('ADMIN', 'SECRETARY')")`
  - Admins and secretaries can manage drivers

- **RouteController** - `/api/routes`
  - Recommended: `@PreAuthorize("hasAnyRole('ADMIN', 'SECRETARY')")`
  - Admins and secretaries can manage routes

- **TaxiController** - `/api/taxis`
  - Recommended: `@PreAuthorize("hasAnyRole('ADMIN', 'SECRETARY')")`
  - Admins and secretaries can manage taxis

### Member Management
- **AssocMemberController** - `/api/members`
  - Recommended:
    - POST/PUT/DELETE: `@PreAuthorize("hasAnyRole('ADMIN', 'SECRETARY')")`
    - GET: `@PreAuthorize("hasAnyRole('ADMIN', 'SECRETARY', 'MEMBER')")`
  - Members can view, but only staff can modify

- **MembershipApplicationController** - `/api/membership-applications`
  - Recommended:
    - POST: `@PreAuthorize("isAuthenticated()")` (anyone can apply)
    - PUT/PATCH (review/approve): `@PreAuthorize("hasAnyRole('ADMIN', 'SECRETARY', 'CHAIRPERSON')")`
    - GET: `@PreAuthorize("hasAnyRole('ADMIN', 'SECRETARY', 'CHAIRPERSON')")`

- **MemberFinanceController** - `/api/member-finances`
  - Recommended: `@PreAuthorize("hasAnyRole('ADMIN', 'SECRETARY')")`

### Financial Management
- **LevyPaymentController** - `/api/levy-payments`
  - Recommended:
    - POST/PUT: `@PreAuthorize("hasAnyRole('ADMIN', 'SECRETARY')")`
    - GET: `@PreAuthorize("hasAnyRole('ADMIN', 'SECRETARY', 'CHAIRPERSON', 'MEMBER')")`

- **LevyFineController** - `/api/levy-fines`
  - Recommended: `@PreAuthorize("hasAnyRole('ADMIN', 'SECRETARY', 'CHAIRPERSON')")`

- **BankPaymentController** - `/api/bank-payments`
  - Recommended: `@PreAuthorize("hasAnyRole('ADMIN', 'SECRETARY')")`

- **ReceiptController** - `/api/receipts`
  - Recommended:
    - POST: `@PreAuthorize("hasAnyRole('ADMIN', 'SECRETARY')")`
    - GET: `@PreAuthorize("hasAnyRole('ADMIN', 'SECRETARY', 'MEMBER')")`

- **PaymentMethodController** - `/api/payment-methods`
  - Recommended: `@PreAuthorize("hasAnyRole('ADMIN', 'SECRETARY')")`

### Workflows
- **DisciplinaryWorkflowController** - `/api/disciplinary-workflows`
  - Recommended:
    - POST: `@PreAuthorize("hasAnyRole('ADMIN', 'SECRETARY')")`
    - PATCH (decision): `@PreAuthorize("hasAnyRole('ADMIN', 'CHAIRPERSON')")`
    - GET: `@PreAuthorize("hasAnyRole('ADMIN', 'SECRETARY', 'CHAIRPERSON')")`

- **NotificationController** - `/api/notifications`
  - Recommended: `@PreAuthorize("isAuthenticated()")` (all logged in users)

### Audit & Monitoring
- **AuditController** - `/api/audit-logs`
  - Recommended: `@PreAuthorize("hasAnyRole('ADMIN', 'CHAIRPERSON')")`

- **TaxiServiceAuditController** - `/api/taxi-service-audits`
  - Recommended: `@PreAuthorize("hasAnyRole('ADMIN', 'CHAIRPERSON')")`

## Implementation Examples

### Class-Level Security
```java
@RestController
@RequestMapping("/api/users")
@PreAuthorize("hasRole('ADMIN')")
public class UserController {
    // All methods require ADMIN role
}
```

### Method-Level Security
```java
@RestController
@RequestMapping("/api/members")
public class AssocMemberController {

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SECRETARY')")
    public ResponseEntity<ApiResponse<MemberResponse>> createMember(...) {
        // Only ADMIN and SECRETARY can create
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SECRETARY', 'MEMBER')")
    public ResponseEntity<ApiResponse<List<MemberResponse>>> getAllMembers() {
        // All authenticated users can view
    }
}
```

### Expression-Based Security
```java
@GetMapping("/{id}")
@PreAuthorize("hasRole('ADMIN') or @memberSecurityService.isOwner(#id, principal.userId)")
public ResponseEntity<MemberResponse> getMember(@PathVariable Long id) {
    // Admin can view any member, or user can view their own record
}
```

## Next Steps

To implement complete security:

1. Add `@PreAuthorize` annotations to controllers as per recommendations above
2. Consider creating custom security service for complex authorization logic
3. Implement data filtering in services to ensure users only see their own data when appropriate
4. Add integration tests to verify security rules
5. Document any role changes in this file

## Testing Security

When testing with different roles:

1. Create test users with different roles
2. Login with each user to get JWT token
3. Test endpoint access with appropriate tokens
4. Verify unauthorized access returns 403 Forbidden
5. Verify unauthenticated access returns 401 Unauthorized

## Notes

- UserController has already been secured with ADMIN role
- All other controllers currently allow authenticated access
- Consider implementing row-level security for member data
- Audit logs should track who performed what actions
