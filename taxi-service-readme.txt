# Taxi Service

This service manages taxi information for the Taxi Management system, including vehicle details, driver assignments, and route assignments.

## Key Components

### Core Functionality

- **Taxi Management**: Create, update, and delete taxi records
- **Driver Assignment**: Assign drivers to taxis
- **Route Assignment**: Assign taxis to specific routes
- **Fleet Management**: Query taxis by various criteria

### Audit Components

- `TaxiServiceAuditLog`: Service-specific audit log entity
- `TaxiServiceAuditLogRepository`: Repository for accessing audit logs
- `TaxiServiceAuditService`: Service implementation for audit log management
- `TaxiServiceAuditController`: REST endpoint for audit log access

### Service Integration

- **Feign Clients**: Integration with Driver and Route services
- **Cross-Service Communication**: Retrieve driver and route details

### Database Configuration

- PostgreSQL database with audit triggers
- Automated audit logging for all changes to taxi records
- Database indexes for optimized audit queries

## API Endpoints

### Taxi Management Endpoints

- `GET /api/taxis`: Get all taxis
- `GET /api/taxis/{taxiId}`: Get taxi by ID
- `GET /api/taxis/driver/{driverId}`: Get taxis by driver ID
- `GET /api/taxis/route/{routeId}`: Get taxis by route ID
- `POST /api/taxis`: Create new taxi
- `PUT /api/taxis/{taxiId}`: Update taxi
- `DELETE /api/taxis/{taxiId}`: Delete taxi

### Audit Endpoints

- `GET /api/taxis/audit/table/{tableName}`: Get audit logs for a specific table
- `GET /api/taxis/audit/record/{tableName}/{recordId}`: Get audit logs for a specific record
- `GET /api/taxis/audit/user/{actionBy}`: Get audit logs by user
- `GET /api/taxis/audit/action/{actionType}`: Get audit logs by action type
- `GET /api/taxis/audit/date-range`: Get audit logs by date range

## Database Triggers

The service uses PostgreSQL triggers to automatically log all changes to the database. The trigger configuration ensures that:

1. All changes (inserts, updates, deletes) to taxi records are captured
2. User information is associated with each change
3. Full before/after snapshots are stored in the audit log

## Integration with Other Services

- **Driver Service**: Retrieves driver information when assigning drivers to taxis
- **Route Service**: Retrieves route information when assigning taxis to routes
- **Gateway Service**: Secured through the API Gateway
- **Audit Service**: Provides audit logs to the centralized Audit Service