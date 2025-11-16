# Services Implementation Guide

## Overview
This document provides a summary of the service layer implementations needed for the Taxi Management System.

## Core Services to Implement

### 1. User Management Services

**UserService** - Manages system users
- createUser(UserRequest) : UserResponse
- updateUser(Long userId, UserRequest) : UserResponse
- deleteUser(Long userId)
- getUserById(Long userId) : UserResponse
- getAllUsers() : List<UserResponse>
- getUsersByRole(Long roleId) : List<UserResponse>
- activateUser(Long userId)
- deactivateUser(Long userId)

**UserRoleService** - Manages user roles
- createRole(UserRoleRequest) : UserRoleResponse
- updateRole(Long roleId, UserRoleRequest) : UserRoleResponse
- deleteRole(Long roleId)
- getRoleById(Long roleId) : UserRoleResponse
- getAllRoles() : List<UserRoleResponse>

**DriverService** - Manages drivers
- createDriver(DriverRequest) : DriverResponse
- updateDriver(Long driverId, DriverRequest) : DriverResponse
- deleteDriver(Long driverId)
- getDriverById(Long driverId) : DriverResponse
- getAllDrivers() : List<DriverResponse>

**RouteService** - Manages routes
- createRoute(RouteRequest) : RouteResponse
- updateRoute(Long routeId, RouteRequest) : RouteResponse
- deleteRoute(Long routeId)
- getRouteById(Long routeId) : RouteResponse
- getAllRoutes() : List<RouteResponse>
- getActiveRoutes() : List<RouteResponse>

### 2. Member Management Services

**AssocMemberService** - Manages association members
- createMember(AssocMemberRequest, String currentUser) : AssocMemberResponse
- updateMember(Long memberId, AssocMemberRequest, String currentUser) : AssocMemberResponse
- deleteMember(Long memberId)
- getMemberById(Long memberId) : AssocMemberResponse
- getAllMembers() : List<AssocMemberResponse>
- getActiveMembers() : List<AssocMemberResponse>
- blacklistMember(Long memberId, String reason)
- removeBlacklist(Long memberId)

**MembershipApplicationService** - Manages membership applications
- submitApplication(MembershipApplicationRequest) : MembershipApplicationResponse
- secretaryReview(Long applicationId, ApplicationReviewRequest) : MembershipApplicationResponse
- chairpersonReview(Long applicationId, ApplicationReviewRequest) : MembershipApplicationResponse
- getApplicationById(Long applicationId) : MembershipApplicationResponse
- getPendingSecretaryReview() : List<MembershipApplicationResponse>
- getPendingChairpersonReview() : List<MembershipApplicationResponse>
- approveApplication(Long applicationId) - Creates AssocMember

**MemberFinanceService** - Manages member finances
- createMemberFinance(MemberFinanceRequest) : MemberFinanceResponse
- recordJoiningFeePayment(Long financeId)
- issueMembershipCard(Long financeId)
- getFinanceByMemberId(Long memberId) : MemberFinanceResponse
- getPendingJoiningFees() : List<MemberFinanceResponse>

### 3. Financial Services

**LevyPaymentService** - Manages levy payments
- recordLevyPayment(LevyPaymentRequest, String currentUser) : LevyPaymentResponse
- processPayment(Long levyPaymentId, Long paymentMethodId, String currentUser) : LevyPaymentResponse
- getPaymentById(Long paymentId) : LevyPaymentResponse
- getPaymentsByMember(Long memberId) : List<LevyPaymentResponse>
- getPendingPayments() : List<LevyPaymentResponse>
- getTotalCollected(LocalDate startDate, LocalDate endDate) : BigDecimal
- generateWeeklyLevies(LocalDate weekStart, LocalDate weekEnd, BigDecimal amount)

**LevyFineService** - Manages fines
- issueFine(LevyFineRequest, String currentUser) : LevyFineResponse
- processFinePayment(Long fineId, Long paymentMethodId, String currentUser) : LevyFineResponse
- getFineById(Long fineId) : LevyFineResponse
- getFinesByMember(Long memberId) : List<LevyFineResponse>
- getUnpaidFines() : List<LevyFineResponse>
- getTotalOutstanding(Long memberId) : BigDecimal

**BankPaymentService** - Manages bank payments
- recordBankPayment(BankPaymentRequest) : BankPaymentResponse
- verifyPayment(Long bankPaymentId) : BankPaymentResponse
- getUnverifiedPayments() : List<BankPaymentResponse>

**ReceiptService** - Manages receipts
- generateReceipt(ReceiptRequest) : ReceiptResponse
- getReceiptByNumber(String receiptNumber) : ReceiptResponse
- getReceiptsByMember(Long memberId) : List<ReceiptResponse>

### 4. Workflow Services

**DisciplinaryWorkflowService** - Manages disciplinary workflows
- initiateWorkflow(DisciplinaryWorkflowRequest) : DisciplinaryWorkflowResponse
- secretaryDecision(Long workflowId, WorkflowDecisionRequest) : DisciplinaryWorkflowResponse
- chairpersonDecision(Long workflowId, WorkflowDecisionRequest) : DisciplinaryWorkflowResponse
- getPendingSecretaryDecisions() : List<DisciplinaryWorkflowResponse>
- getPendingChairpersonDecisions() : List<DisciplinaryWorkflowResponse>
- getWorkflowByFineId(Long fineId) : DisciplinaryWorkflowResponse

**NotificationService** - Manages notifications
- sendNotification(NotificationRequest) : NotificationResponse
- markAsRead(Long notificationId)
- getUnreadByMember(Long memberId) : List<NotificationResponse>
- sendPaymentReminder(Long memberId)
- sendFineNotice(Long memberId, Long fineId)

### 5. Support Services

**AuditService** - Manages audit logs
- getAuditLogsByTable(String tableName) : List<AuditLogResponse>
- getAuditHistory(String tableName, Long recordId) : List<AuditLogResponse>
- getAuditLogsByUser(String username) : List<AuditLogResponse>
- getAuditLogsByDateRange(LocalDateTime start, LocalDateTime end) : List<AuditLogResponse>

**TaxiService** (Updated) - Manages taxis
- createTaxi(TaxiRequest) : TaxiResponse
- updateTaxi(Long taxiId, TaxiRequest) : TaxiResponse
- deleteTaxi(Long taxiId)
- getTaxiById(Long taxiId) : TaxiResponse
- getAllTaxis() : List<TaxiResponse>
- getTaxisByDriver(Long driverId) : List<TaxiResponse>
- getTaxisByRoute(Long routeId) : List<TaxiResponse>
- assignDriver(Long taxiId, Long driverId) : TaxiResponse
- assignRoute(Long taxiId, Long routeId) : TaxiResponse

## Service Implementation Pattern

```java
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ExampleService {

    private final ExampleRepository repository;

    public ExampleResponse create(ExampleRequest request) {
        log.info("Creating example: {}", request);

        // 1. Validate
        validateRequest(request);

        // 2. Check duplicates
        if (repository.existsByName(request.getName())) {
            throw new DuplicateResourceException("Example", "name", request.getName());
        }

        // 3. Convert and save
        Example entity = convertToEntity(request);
        Example saved = repository.save(entity);

        // 4. Convert and return
        return convertToResponse(saved);
    }

    private void validateRequest(ExampleRequest request) {
        // Custom business validation
    }

    private Example convertToEntity(ExampleRequest request) {
        return Example.builder()
                .name(request.getName())
                .build();
    }

    private ExampleResponse convertToResponse(Example entity) {
        return ExampleResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .build();
    }
}
```

## Key Patterns

### 1. Transaction Management
- Use `@Transactional` on service classes
- Read-only operations: `@Transactional(readOnly = true)`

### 2. Error Handling
- Use custom exceptions (ResourceNotFoundException, DuplicateResourceException)
- Log all errors with context

### 3. Validation
- Bean Validation on DTOs
- Additional business validation in services

### 4. Auditing
- Use `createdBy` and `updatedBy` fields
- Pass current user context to services

### 5. Entity-DTO Conversion
- Separate methods for entity → DTO and DTO → entity
- Include related data (e.g., member names, route names)

## Testing Strategy

### Unit Tests
- Test each service method
- Mock dependencies
- Verify business logic

### Integration Tests
- Test database interactions
- Verify transactions
- Test cascading operations

---

*This guide provides the blueprint for implementing all services in the Taxi Management System.*
