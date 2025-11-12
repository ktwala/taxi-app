# Taxi Service

A Spring Boot microservice for managing taxi information in the Taxi Management system, including vehicle details, driver assignments, and route assignments.

## Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Technology Stack](#technology-stack)
- [Architecture](#architecture)
- [Getting Started](#getting-started)
- [API Documentation](#api-documentation)
- [Database Schema](#database-schema)
- [Testing](#testing)
- [Docker Deployment](#docker-deployment)
- [Configuration](#configuration)
- [Contributing](#contributing)

## Overview

The Taxi Service is a microservice that provides comprehensive management of taxi fleet information. It handles taxi records, driver assignments, route assignments, and maintains a complete audit trail of all changes.

## Features

### Core Functionality

- **Taxi Management**: Full CRUD operations for taxi records
- **Driver Assignment**: Assign and manage driver assignments to taxis
- **Route Assignment**: Assign taxis to specific routes
- **Fleet Queries**: Query taxis by various criteria (status, driver, route, vehicle type, etc.)
- **Status Management**: Track taxi availability and maintenance status

### Audit Components

- **Automatic Audit Logging**: PostgreSQL triggers automatically log all changes
- **Comprehensive Tracking**: Track who made changes, when, and what was changed
- **Query Capabilities**: Search audit logs by table, record, user, action type, or date range
- **Change History**: View complete history of changes for any taxi record

### Integration

- **Feign Clients**: Integration with Driver and Route microservices
- **Fallback Mechanisms**: Graceful degradation when external services are unavailable
- **Cross-Service Validation**: Validate driver and route assignments with external services

## Technology Stack

- **Java 17**
- **Spring Boot 3.2.0**
- **Spring Data JPA**
- **PostgreSQL 15**
- **Spring Cloud OpenFeign**
- **Lombok**
- **Maven**
- **Docker & Docker Compose**
- **JUnit 5 & Mockito** (Testing)

## Architecture

The service follows a layered architecture:

```
┌─────────────────────────────────────┐
│         REST Controllers            │
│  (TaxiController, AuditController)  │
└─────────────────┬───────────────────┘
                  │
┌─────────────────▼───────────────────┐
│         Service Layer               │
│  (TaxiService, AuditService)        │
└─────────────────┬───────────────────┘
                  │
┌─────────────────▼───────────────────┐
│       Repository Layer              │
│  (Spring Data JPA Repositories)     │
└─────────────────┬───────────────────┘
                  │
┌─────────────────▼───────────────────┐
│         PostgreSQL Database         │
│    (with Audit Triggers)            │
└─────────────────────────────────────┘
```

## Getting Started

### Prerequisites

- Java 17 or higher
- Maven 3.8+
- PostgreSQL 15+ (or use Docker)
- Docker & Docker Compose (optional)

### Installation

1. **Clone the repository**

```bash
git clone <repository-url>
cd taxi-app
```

2. **Configure the database**

Update `src/main/resources/application.yml` with your database credentials:

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

4. **Run the application**

```bash
mvn spring-boot:run
```

The service will start on `http://localhost:8082`

### Using Docker

```bash
docker-compose up -d
```

This will start:
- PostgreSQL database
- Taxi Service application
- PgAdmin (optional, for database management)

## API Documentation

### Taxi Management Endpoints

#### Get All Taxis

```http
GET /api/taxis
```

**Response:**
```json
{
  "success": true,
  "message": "Taxis retrieved successfully",
  "data": [
    {
      "taxiId": 1,
      "licensePlate": "TX-001-ABC",
      "model": "Camry",
      "manufacturer": "Toyota",
      "year": 2022,
      "capacity": 4,
      "color": "White",
      "status": "AVAILABLE",
      "driverId": null,
      "driverName": null,
      "routeId": null,
      "routeName": null,
      "fuelType": "HYBRID",
      "vehicleType": "SEDAN",
      "notes": "Well maintained vehicle",
      "createdAt": "2025-01-15T10:30:00",
      "updatedAt": "2025-01-15T10:30:00"
    }
  ]
}
```

#### Get Taxi by ID

```http
GET /api/taxis/{taxiId}
```

#### Get Taxis by Driver ID

```http
GET /api/taxis/driver/{driverId}
```

#### Get Taxis by Route ID

```http
GET /api/taxis/route/{routeId}
```

#### Get Available Taxis

```http
GET /api/taxis/available
```

#### Create New Taxi

```http
POST /api/taxis
Content-Type: application/json

{
  "licensePlate": "TX-004-XYZ",
  "model": "Corolla",
  "manufacturer": "Toyota",
  "year": 2023,
  "capacity": 4,
  "color": "Blue",
  "status": "AVAILABLE",
  "fuelType": "PETROL",
  "vehicleType": "SEDAN",
  "notes": "New vehicle"
}
```

#### Update Taxi

```http
PUT /api/taxis/{taxiId}
Content-Type: application/json

{
  "licensePlate": "TX-004-XYZ",
  "model": "Corolla",
  "manufacturer": "Toyota",
  "year": 2023,
  "capacity": 4,
  "color": "Blue",
  "status": "MAINTENANCE",
  "fuelType": "PETROL",
  "vehicleType": "SEDAN",
  "notes": "In maintenance"
}
```

#### Delete Taxi

```http
DELETE /api/taxis/{taxiId}
```

#### Assign Driver to Taxi

```http
PUT /api/taxis/{taxiId}/assign-driver/{driverId}
```

#### Assign Route to Taxi

```http
PUT /api/taxis/{taxiId}/assign-route/{routeId}
```

### Audit Log Endpoints

#### Get Audit Logs by Table Name

```http
GET /api/taxis/audit/table/{tableName}
```

#### Get Audit Logs for Specific Record

```http
GET /api/taxis/audit/record/{tableName}/{recordId}
```

#### Get Audit Logs by User

```http
GET /api/taxis/audit/user/{actionBy}
```

#### Get Audit Logs by Action Type

```http
GET /api/taxis/audit/action/{actionType}
```

Action types: `INSERT`, `UPDATE`, `DELETE`

#### Get Audit Logs by Date Range

```http
GET /api/taxis/audit/date-range?startDate=2025-01-01T00:00:00&endDate=2025-01-31T23:59:59
```

#### Get Complete Audit History

```http
GET /api/taxis/audit/history/{tableName}/{recordId}
```

### Status Values

- **Taxi Status**: `AVAILABLE`, `ASSIGNED`, `MAINTENANCE`, `OUT_OF_SERVICE`
- **Fuel Types**: `PETROL`, `DIESEL`, `ELECTRIC`, `HYBRID`, `CNG`
- **Vehicle Types**: `SEDAN`, `SUV`, `VAN`, `MINIBUS`, `HATCHBACK`

## Database Schema

### Taxis Table

| Column | Type | Description |
|--------|------|-------------|
| taxi_id | BIGSERIAL | Primary key |
| license_plate | VARCHAR(20) | Unique license plate |
| model | VARCHAR(100) | Vehicle model |
| manufacturer | VARCHAR(100) | Vehicle manufacturer |
| year | INTEGER | Manufacturing year |
| capacity | INTEGER | Passenger capacity |
| color | VARCHAR(50) | Vehicle color |
| status | VARCHAR(20) | Current status |
| driver_id | BIGINT | Assigned driver ID |
| route_id | BIGINT | Assigned route ID |
| fuel_type | VARCHAR(20) | Fuel type |
| vehicle_type | VARCHAR(30) | Vehicle type |
| notes | TEXT | Additional notes |
| created_at | TIMESTAMP | Creation timestamp |
| updated_at | TIMESTAMP | Last update timestamp |

### Taxi Service Audit Log Table

| Column | Type | Description |
|--------|------|-------------|
| id | BIGSERIAL | Primary key |
| table_name | VARCHAR(100) | Affected table |
| record_id | BIGINT | Record ID |
| action_type | VARCHAR(20) | INSERT/UPDATE/DELETE |
| action_by | VARCHAR(100) | User who made change |
| action_timestamp | TIMESTAMP | When change occurred |
| old_value | TEXT | Before state (JSON) |
| new_value | TEXT | After state (JSON) |
| changes | TEXT | Description of changes |
| ip_address | VARCHAR(50) | IP address |
| user_agent | VARCHAR(200) | User agent |

### Database Triggers

The service uses PostgreSQL triggers to automatically capture all changes:

- **Insert Trigger**: Logs creation of new taxi records
- **Update Trigger**: Logs modifications with detailed field-level changes
- **Delete Trigger**: Logs deletion of taxi records

## Testing

### Run Unit Tests

```bash
mvn test
```

### Run Integration Tests

```bash
mvn verify
```

### Test Coverage

The project includes comprehensive unit tests for:
- Service layer (TaxiService, TaxiServiceAuditService)
- Repository layer
- Controller layer
- Exception handling

## Docker Deployment

### Build Docker Image

```bash
docker build -t taxi-service:latest .
```

### Run with Docker Compose

```bash
docker-compose up -d
```

Services available:
- **Taxi Service**: http://localhost:8082
- **PostgreSQL**: localhost:5432
- **PgAdmin**: http://localhost:5050 (admin@taxi.com / admin)

### Stop Services

```bash
docker-compose down
```

### View Logs

```bash
docker-compose logs -f taxi-service
```

## Configuration

### Environment Variables

| Variable | Default | Description |
|----------|---------|-------------|
| DB_HOST | localhost | Database host |
| DB_PORT | 5432 | Database port |
| DB_NAME | taxidb | Database name |
| DB_USERNAME | postgres | Database username |
| DB_PASSWORD | postgres | Database password |
| SERVER_PORT | 8082 | Application port |
| DRIVER_SERVICE_URL | http://localhost:8081 | Driver service URL |
| ROUTE_SERVICE_URL | http://localhost:8083 | Route service URL |
| LOG_LEVEL | DEBUG | Application log level |

### Profile-Specific Configuration

- **dev**: Development profile with detailed logging
- **prod**: Production profile with optimized settings
- **test**: Test profile using H2 in-memory database

Activate profile:
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

## Health Check

The service exposes health check endpoints:

```http
GET /actuator/health
```

## Error Handling

The service provides comprehensive error handling:

- **404 Not Found**: Resource doesn't exist
- **409 Conflict**: Duplicate resource (e.g., license plate)
- **400 Bad Request**: Validation errors
- **500 Internal Server Error**: Unexpected errors

All errors return a standard format:

```json
{
  "success": false,
  "error": "Error message",
  "data": null
}
```

## Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License.

## Support

For issues and questions:
- Create an issue in the repository
- Contact: support@taximanagement.com

## Version History

- **1.0.0** (2025-01-15): Initial release
  - Basic CRUD operations
  - Audit logging system
  - Driver and route integration
  - Docker support
