# Taxi Association Management System

A comprehensive Spring Boot microservice for managing taxi associations, including member management, financial operations, disciplinary workflows, and complete audit trails.

## Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Technology Stack](#technology-stack)
- [Architecture](#architecture)
- [Database Schema](#database-schema)
- [Getting Started](#getting-started)
- [API Documentation](#api-documentation)
- [Configuration](#configuration)
- [Docker Deployment](#docker-deployment)
- [Testing](#testing)
- [Security](#security)

## Overview

The Taxi Association Management System is a comprehensive backend solution for managing taxi associations. It provides complete functionality for:

- **User & Role Management**: Multi-role user system with customizable permissions
- **Member Management**: Association member registration, blacklisting, and profile management
- **Membership Applications**: Multi-stage approval workflow (Secretary → Chairperson)
- **Financial Management**: Levy payments, fines, bank payment verification, and receipts
- **Disciplinary Workflows**: Structured appeal process for fine disputes
- **Notification System**: Automated notifications for payments, fines, and reminders
- **Audit Trail**: Complete change history with JSONB storage
- **Fleet Management**: Taxi, driver, and route assignment

## Features

### User Management Module
- **Role-Based Access Control**: Customizable roles with JSON-based permissions
- **User CRUD Operations**: Complete user lifecycle management
- **User Activation/Deactivation**: Control user access
- **Driver Management**: Driver registration and license tracking
- **Route Management**: Route creation with activation controls

### Member Management Module
- **Association Member CRUD**: Complete member profile management
- **Squad Number Tracking**: Unique member identification
- **Blacklist Management**: Member blacklisting and removal
- **Member Search**: Keyword-based member search
- **Membership Applications**:
  - Multi-stage approval (Secretary → Chairperson)
  - Document attachment support
  - Application status tracking
  - Review comments and notes
- **Member Finance**:
  - Joining fee tracking
  - Membership card issuance
  - Payment status monitoring

### Financial Management Module
- **Levy Payments**:
  - Weekly levy recording
  - Payment processing with multiple payment methods
  - Receipt attachment
  - Financial aggregation (totals by member, period)
  - Outstanding payment tracking
- **Levy Fines**:
  - Fine issuance with reason tracking
  - Fine payment processing
  - Status management (Unpaid, Owing, Paid, Waived)
  - Outstanding fine calculations
  - Fine collection reports
- **Bank Payments**:
  - Bank payment recording
  - Transaction reference tracking
  - Payment verification workflow
  - Unverified payment monitoring
- **Receipts**:
  - Automated receipt generation
  - Unique receipt numbering
  - Receipt querying by member, date range, issuer
  - PDF-ready receipt data

### Workflow Management Module
- **Disciplinary Workflows**:
  - Fine appeal process initiation
  - Secretary decision recording
  - Chairperson decision with override capability
  - Workflow status tracking
  - Pending workflow queries
- **Notifications**:
  - Custom notification sending
  - Payment reminders
  - Fine notices
  - Mark as read functionality
  - Unread notification tracking

### Support & Audit Module
- **Audit Logging**:
  - Automatic change tracking with PostgreSQL triggers
  - JSONB storage for old/new values
  - Query by table, record, user, action type, date range
  - Complete audit history for any record
- **Taxi Management**:
  - Simplified taxi records (plate number, model)
  - Driver assignment
  - Route assignment
  - Unassigned taxi tracking
- **Payment Methods**:
  - Payment method configuration
  - Method activation/deactivation
  - Active method filtering

## Technology Stack

### Core Technologies
- **Java 17**: Programming language
- **Spring Boot 3.2.0**: Application framework
- **Spring Data JPA**: ORM and data access
- **PostgreSQL 15**: Relational database with JSONB support
- **Hypersistence Utils 3.7.0**: JSON/JSONB type handling
- **Maven**: Build and dependency management

### Spring Ecosystem
- **Spring Cloud OpenFeign**: Microservice communication
- **Spring Validation**: Bean validation
- **Spring Web**: REST API support
- **Spring Transaction**: Transaction management

### Development Tools
- **Lombok**: Code generation
- **SLF4J**: Logging framework
- **JUnit 5**: Testing framework
- **Mockito**: Mocking framework

### DevOps
- **Docker**: Containerization
- **Docker Compose**: Multi-container orchestration

## Architecture

The service follows a layered architecture pattern:

```
┌─────────────────────────────────────────────────────────────┐
│                    REST Controllers (16)                     │
│  User │ Member │ Financial │ Workflow │ Common │ Audit      │
└───────────────────────────┬─────────────────────────────────┘
                            │
┌───────────────────────────▼─────────────────────────────────┐
│                    Service Layer (16)                        │
│  Business Logic │ Validation │ Transaction Management       │
└───────────────────────────┬─────────────────────────────────┘
                            │
┌───────────────────────────▼─────────────────────────────────┐
│                   Repository Layer (17)                      │
│  Spring Data JPA │ Custom Queries │ Aggregations            │
└───────────────────────────┬─────────────────────────────────┘
                            │
┌───────────────────────────▼─────────────────────────────────┐
│                  PostgreSQL Database (15 tables)             │
│  JSONB Support │ Triggers │ Constraints │ Indexes            │
└─────────────────────────────────────────────────────────────┘
```

### Design Patterns
- **Layered Architecture**: Separation of concerns
- **Repository Pattern**: Data access abstraction
- **DTO Pattern**: Data transfer objects for API
- **Service Pattern**: Business logic encapsulation
- **Builder Pattern**: Immutable object construction (Lombok)
- **Singleton Pattern**: Spring bean management

## Database Schema

### Complete Table Structure (15 Tables)

#### User Management Tables
1. **user_roles**: User role definitions with JSON permissions
2. **users**: System users with role assignments
3. **driver**: Driver information and licenses
4. **route**: Route definitions with activation status

#### Member Management Tables
5. **assoc_member**: Association members with squad numbers
6. **membership_application**: Application records with workflow status
7. **membership_application_documents**: Document attachments for applications
8. **member_finance**: Joining fees and membership card tracking

#### Financial Tables
9. **payment_method**: Available payment methods
10. **levy_payments**: Weekly levy payment records
11. **levy_fines**: Fine issuance and payment tracking
12. **bank_payment**: Bank payment verification records
13. **receipt**: Receipt generation and tracking

#### Workflow & Support Tables
14. **levy_fine_disciplinary_workflow**: Disciplinary appeal process
15. **notification**: Notification records and read status
16. **audit_log**: Comprehensive change audit trail (JSONB)

#### Fleet Management
17. **taxi**: Taxi records with driver/route assignments

### Key Relationships
```
users ──→ user_roles (many-to-one)
assoc_member ──→ membership_application (one-to-many)
assoc_member ──→ levy_payments (one-to-many)
assoc_member ──→ levy_fines (one-to-many)
levy_fines ──→ levy_fine_disciplinary_workflow (one-to-one)
taxi ──→ driver (many-to-one)
taxi ──→ route (many-to-one)
```

### Audit Triggers
All tables (except audit_log) have automatic audit triggers:
- **INSERT**: Captures new record creation
- **UPDATE**: Captures before/after state in JSONB
- **DELETE**: Captures deleted record state

## Getting Started

### Prerequisites

- Java 17 or higher
- Maven 3.8+
- PostgreSQL 15+
- Docker & Docker Compose (optional)

### Installation

1. **Clone the repository**

```bash
git clone <repository-url>
cd taxi-app
```

2. **Configure the database**

Update `src/main/resources/application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/taxidb
    username: postgres
    password: postgres
```

3. **Build the project**

```bash
mvn clean install
```

4. **Initialize the database**

The schema will be automatically created from `src/main/resources/schema.sql` on first run.

5. **Run the application**

```bash
mvn spring-boot:run
```

The service will start on `http://localhost:8082`

### Quick Start with Docker

```bash
docker-compose up -d
```

This starts:
- PostgreSQL database on port 5432
- Taxi Service on port 8082

## API Documentation

### Base URL
```
http://localhost:8082/api
```

### Response Format
All endpoints return a standard ApiResponse wrapper:

```json
{
  "success": true,
  "message": "Operation successful",
  "data": { ... }
}
```

### Module Endpoints Summary

#### User Management (`/api/...`)

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/user-roles` | POST | Create user role |
| `/user-roles/{roleId}` | PUT | Update user role |
| `/user-roles/{roleId}` | DELETE | Delete user role |
| `/user-roles` | GET | Get all roles |
| `/users` | POST | Create user |
| `/users/{userId}` | PUT | Update user |
| `/users/{userId}/activate` | PATCH | Activate user |
| `/users/{userId}/deactivate` | PATCH | Deactivate user |
| `/users/username/{username}` | GET | Get user by username |
| `/users/active` | GET | Get active users |
| `/drivers` | POST | Create driver |
| `/drivers/{driverId}` | PUT | Update driver |
| `/drivers/license/{licenseNumber}` | GET | Get driver by license |
| `/routes` | POST | Create route |
| `/routes/{routeId}/activate` | PATCH | Activate route |
| `/routes/{routeId}/deactivate` | PATCH | Deactivate route |
| `/routes/search?keyword=` | GET | Search routes |

#### Member Management (`/api/...`)

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/members` | POST | Create member |
| `/members/{memberId}` | PUT | Update member |
| `/members/{memberId}/blacklist` | PATCH | Blacklist member |
| `/members/{memberId}/remove-blacklist` | PATCH | Remove blacklist |
| `/members/squad/{squadNumber}` | GET | Get member by squad |
| `/members/blacklisted` | GET | Get blacklisted members |
| `/members/search?keyword=` | GET | Search members |
| `/membership-applications` | POST | Submit application |
| `/membership-applications/{id}/secretary-review` | PATCH | Secretary review |
| `/membership-applications/{id}/chairperson-review` | PATCH | Chairperson review |
| `/membership-applications/pending/secretary` | GET | Pending secretary review |
| `/membership-applications/pending/chairperson` | GET | Pending chairperson |
| `/member-finances` | POST | Create member finance |
| `/member-finances/{id}/joining-fee` | PATCH | Record joining fee |
| `/member-finances/{id}/membership-card` | PATCH | Issue membership card |
| `/member-finances/pending/joining-fee` | GET | Pending joining fees |

#### Financial Management (`/api/...`)

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/levy-payments` | POST | Record levy payment |
| `/levy-payments/{id}/process` | PATCH | Process payment |
| `/levy-payments/{id}/attach-receipt` | PATCH | Attach receipt |
| `/levy-payments/pending` | GET | Get pending payments |
| `/levy-payments/member/{memberId}/total-paid` | GET | Total paid by member |
| `/levy-payments/total-collected` | GET | Total collected in period |
| `/levy-fines` | POST | Issue fine |
| `/levy-fines/{id}/process-payment` | PATCH | Process fine payment |
| `/levy-fines/{id}/status` | PATCH | Update fine status |
| `/levy-fines/unpaid` | GET | Get unpaid fines |
| `/levy-fines/owing` | GET | Get owing fines |
| `/levy-fines/member/{memberId}/total-outstanding` | GET | Total outstanding |
| `/bank-payments` | POST | Record bank payment |
| `/bank-payments/{id}/verify` | PATCH | Verify payment |
| `/bank-payments/unverified` | GET | Get unverified payments |
| `/bank-payments/transaction/{ref}` | GET | Get by transaction ref |
| `/receipts` | POST | Generate receipt |
| `/receipts/number/{receiptNumber}` | GET | Get by receipt number |
| `/receipts/date-range` | GET | Get receipts by date |

#### Workflow Management (`/api/...`)

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/disciplinary-workflows/initiate/{fineId}` | POST | Initiate workflow |
| `/disciplinary-workflows/{id}/secretary-decision` | PATCH | Secretary decision |
| `/disciplinary-workflows/{id}/chairperson-decision` | PATCH | Chairperson decision |
| `/disciplinary-workflows/pending/secretary` | GET | Pending secretary |
| `/disciplinary-workflows/pending/chairperson` | GET | Pending chairperson |
| `/notifications` | POST | Send notification |
| `/notifications/{id}/mark-read` | PATCH | Mark as read |
| `/notifications/member/{memberId}/mark-all-read` | PATCH | Mark all read |
| `/notifications/payment-reminder/{memberId}` | POST | Send payment reminder |
| `/notifications/fine-notice` | POST | Send fine notice |
| `/notifications/member/{memberId}/unread` | GET | Get unread |

#### Support & Audit (`/api/...`)

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/audit-logs/table/{tableName}` | GET | Get logs by table |
| `/audit-logs/table/{tableName}/record/{id}` | GET | Get logs for record |
| `/audit-logs/user/{username}` | GET | Get logs by user |
| `/audit-logs/action-type/{type}` | GET | Get logs by action |
| `/audit-logs/date-range` | GET | Get logs by date |
| `/taxis` | POST | Create taxi |
| `/taxis/{taxiId}/assign-driver/{driverId}` | PATCH | Assign driver |
| `/taxis/{taxiId}/assign-route/{routeId}` | PATCH | Assign route |
| `/taxis/plate/{plateNumber}` | GET | Get by plate number |
| `/taxis/unassigned` | GET | Get unassigned taxis |
| `/payment-methods` | POST | Create payment method |
| `/payment-methods/{id}/activate` | PATCH | Activate method |
| `/payment-methods/{id}/deactivate` | PATCH | Deactivate method |
| `/payment-methods/active` | GET | Get active methods |

### Example API Calls

#### Create Association Member
```bash
curl -X POST http://localhost:8082/api/members \
  -H "Content-Type: application/json" \
  -d '{
    "name": "John Doe",
    "phone": "+1234567890",
    "email": "john.doe@example.com",
    "address": "123 Main Street",
    "squadNumber": "SQ-001",
    "membershipStatus": "Active"
  }'
```

#### Submit Membership Application
```bash
curl -X POST http://localhost:8082/api/membership-applications \
  -H "Content-Type: application/json" \
  -d '{
    "applicantName": "Jane Smith",
    "contactDetails": "+0987654321",
    "applicationStatus": "Pending",
    "submittedBy": "admin"
  }'
```

#### Record Levy Payment
```bash
curl -X POST http://localhost:8082/api/levy-payments \
  -H "Content-Type: application/json" \
  -d '{
    "assocMemberId": 1,
    "weekStartDate": "2025-01-13",
    "weekEndDate": "2025-01-19",
    "amount": 500.00,
    "paymentStatus": "Pending",
    "createdBy": "admin"
  }'
```

#### Process Payment
```bash
curl -X PATCH http://localhost:8082/api/levy-payments/1/process?paymentMethodId=1&currentUser=admin
```

#### Issue Fine
```bash
curl -X POST http://localhost:8082/api/levy-fines \
  -H "Content-Type: application/json" \
  -d '{
    "assocMemberId": 1,
    "fineAmount": 200.00,
    "reason": "Late payment",
    "fineStatus": "Unpaid",
    "issuedBy": "secretary"
  }'
```

#### Secretary Review Application
```bash
curl -X PATCH http://localhost:8082/api/membership-applications/1/secretary-review \
  -H "Content-Type: application/json" \
  -d '{
    "decision": "Approved",
    "reviewNotes": "All documents verified",
    "reviewedBy": "secretary"
  }'
```

## Configuration

### Application Profiles

#### Development Profile (`application-dev.yml`)
```yaml
spring:
  jpa:
    show-sql: true
    properties:
      hibernate:
        format_sql: true
logging:
  level:
    com.taxiservice: DEBUG
```

#### Production Profile (`application-prod.yml`)
```yaml
spring:
  jpa:
    show-sql: false
logging:
  level:
    com.taxiservice: INFO
```

### Environment Variables

| Variable | Default | Description |
|----------|---------|-------------|
| DB_HOST | localhost | Database host |
| DB_PORT | 5432 | Database port |
| DB_NAME | taxidb | Database name |
| DB_USERNAME | postgres | Database username |
| DB_PASSWORD | postgres | Database password |
| SERVER_PORT | 8082 | Application port |
| LOG_LEVEL | DEBUG | Logging level |

### Activate Profile
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

Or set environment variable:
```bash
export SPRING_PROFILES_ACTIVE=prod
mvn spring-boot:run
```

## Docker Deployment

### Docker Compose Configuration

The `docker-compose.yml` includes:
- PostgreSQL 15 database
- Taxi Service application
- Health checks
- Volume persistence

### Build and Run

```bash
# Build the application
mvn clean package

# Start all services
docker-compose up -d

# View logs
docker-compose logs -f

# Stop services
docker-compose down

# Remove volumes
docker-compose down -v
```

### Docker Commands

```bash
# Build custom image
docker build -t taxi-service:latest .

# Run standalone
docker run -p 8082:8082 \
  -e DB_HOST=host.docker.internal \
  -e DB_PORT=5432 \
  taxi-service:latest

# Check container status
docker ps

# Execute commands in container
docker exec -it taxi-service bash
```

## Testing

### Run All Tests
```bash
mvn test
```

### Run Specific Test Class
```bash
mvn test -Dtest=TaxiServiceTest
```

### Run Integration Tests
```bash
mvn verify
```

### Test Coverage
The project includes comprehensive tests for:
- Service layer business logic
- Repository custom queries
- Controller endpoint validation
- Exception handling
- DTO validation

### Example Test
```java
@Test
void shouldCreateAssocMember() {
    AssocMemberRequest request = AssocMemberRequest.builder()
        .name("John Doe")
        .squadNumber("SQ-001")
        .build();

    AssocMemberResponse response = assocMemberService.createMember(request);

    assertNotNull(response);
    assertEquals("John Doe", response.getName());
}
```

## Security

### Current Security Status
⚠️ **Note**: This is a backend service without authentication/authorization implemented. Security considerations:

1. **Password Hashing**: Currently disabled (placeholder in UserService)
   ```java
   // TODO: Implement password hashing
   user.setPasswordHash(request.getPassword());
   ```

2. **API Security**: No JWT or OAuth2 implementation

3. **Recommended Enhancements**:
   - Implement Spring Security
   - Add JWT authentication
   - Implement password encryption (BCrypt)
   - Add role-based endpoint authorization
   - Enable CORS configuration
   - Add rate limiting
   - Implement API key authentication

### Future Security Implementation
```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) {
        return http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/users/**").hasRole("ADMIN")
                .requestMatchers("/api/members/**").hasAnyRole("ADMIN", "SECRETARY")
                .anyRequest().authenticated()
            )
            .oauth2ResourceServer(OAuth2ResourceServerConfigurer::jwt)
            .build();
    }
}
```

## Error Handling

### Standard Error Response
```json
{
  "success": false,
  "message": "Error message description",
  "data": null
}
```

### HTTP Status Codes
- **200 OK**: Successful operation
- **201 Created**: Resource created successfully
- **400 Bad Request**: Validation error
- **404 Not Found**: Resource not found
- **409 Conflict**: Duplicate resource (e.g., squad number)
- **500 Internal Server Error**: Unexpected error

### Custom Exceptions
- `ResourceNotFoundException`: Entity not found
- `DuplicateResourceException`: Unique constraint violation
- `IllegalStateException`: Invalid operation state

## Project Statistics

- **Total Java Files**: 119
- **Controllers**: 16 (100+ endpoints)
- **Services**: 16 (with transaction management)
- **Repositories**: 17 (with custom queries)
- **Entities**: 17 (with audit triggers)
- **DTOs**: 64 (Request/Response pairs)
- **Database Tables**: 15 + audit_log
- **Lines of Code**: ~6,500+

## Project Structure

```
taxi-app/
├── src/
│   ├── main/
│   │   ├── java/com/taxiservice/
│   │   │   ├── controller/
│   │   │   │   ├── common/      # Audit, Taxi, PaymentMethod
│   │   │   │   ├── financial/   # Levy, Fine, Bank, Receipt
│   │   │   │   ├── member/      # Member, Application, Finance
│   │   │   │   ├── user/        # User, Role, Driver, Route
│   │   │   │   └── workflow/    # Disciplinary, Notification
│   │   │   ├── dto/
│   │   │   │   ├── common/      # Shared DTOs
│   │   │   │   ├── financial/   # Financial DTOs
│   │   │   │   ├── member/      # Member DTOs
│   │   │   │   ├── user/        # User DTOs
│   │   │   │   └── workflow/    # Workflow DTOs
│   │   │   ├── entity/          # 17 JPA entities
│   │   │   ├── exception/       # Custom exceptions
│   │   │   ├── repository/      # 17 Spring Data repositories
│   │   │   ├── service/
│   │   │   │   ├── common/      # PaymentMethodService
│   │   │   │   ├── financial/   # Financial services
│   │   │   │   ├── member/      # Member services
│   │   │   │   ├── user/        # User services
│   │   │   │   └── workflow/    # Workflow services
│   │   │   └── TaxiServiceApplication.java
│   │   └── resources/
│   │       ├── application.yml
│   │       ├── application-dev.yml
│   │       ├── application-prod.yml
│   │       └── schema.sql
│   └── test/                    # Comprehensive test suite
├── pom.xml
├── Dockerfile
├── docker-compose.yml
└── README.md
```

## Roadmap

### Version 1.1 (Planned)
- [ ] Spring Security integration
- [ ] JWT authentication
- [ ] Password encryption
- [ ] Role-based authorization
- [ ] API documentation with Swagger/OpenAPI

### Version 1.2 (Planned)
- [ ] Report generation (PDF/Excel)
- [ ] Email notifications
- [ ] SMS integration
- [ ] Dashboard analytics
- [ ] Bulk import/export

### Version 2.0 (Future)
- [ ] Mobile app integration
- [ ] Real-time notifications (WebSocket)
- [ ] Payment gateway integration
- [ ] Document management system
- [ ] Advanced reporting and analytics

## Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

### Code Standards
- Follow Java naming conventions
- Write comprehensive JavaDoc comments
- Include unit tests for new features
- Maintain test coverage above 80%
- Use Lombok annotations appropriately

## Troubleshooting

### Common Issues

**Database Connection Failed**
```bash
# Check PostgreSQL is running
docker ps

# Check connection
psql -h localhost -U postgres -d taxidb
```

**Port Already in Use**
```bash
# Find process using port 8082
lsof -i :8082

# Kill the process
kill -9 <PID>
```

**Schema Not Created**
```bash
# Manually create schema
psql -h localhost -U postgres -d taxidb -f src/main/resources/schema.sql
```

## Support

For issues and questions:
- Create an issue in the repository
- Email: support@taxiassociation.com
- Documentation: [Full API Docs](./docs/API.md)

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Acknowledgments

- Spring Boot team for the excellent framework
- PostgreSQL community for JSONB support
- Hypersistence team for JSONB utilities
- All contributors to this project

## Version History

### Version 1.0.0 (2025-01-15)
- ✅ Complete user and role management
- ✅ Association member management
- ✅ Membership application workflow
- ✅ Financial management (levies, fines, payments, receipts)
- ✅ Disciplinary workflow system
- ✅ Notification system
- ✅ Comprehensive audit logging
- ✅ Fleet management (taxis, drivers, routes)
- ✅ 100+ REST API endpoints
- ✅ Docker support
- ✅ Multi-profile configuration

---

**Built with ❤️ for Taxi Associations**
