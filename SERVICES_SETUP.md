# Sparrow Backend Services Setup

This document provides setup instructions for the newly implemented Tracking Service and Consolidation Service.

## Services Overview

### 1. Tracking Service (Port 8008)
- **Purpose**: Real-time parcel tracking with Google Maps API integration
- **Features**:
  - GPS location tracking
  - Google Maps reverse geocoding
  - Status updates and history
  - Driver and vehicle assignment
  - Kafka event publishing

### 2. Consolidation Service (Port 8003)
- **Purpose**: Group parcels by ZIP code, city, or location for efficient delivery
- **Features**:
  - Automatic parcel consolidation by ZIP/city
  - Manual consolidation group creation
  - Status tracking and updates
  - Cost optimization
  - Kafka event publishing

## Prerequisites

### Google Maps API Key
1. Go to [Google Cloud Console](https://console.cloud.google.com/)
2. Create a new project or select existing one
3. Enable the following APIs:
   - Maps JavaScript API
   - Geocoding API
   - Places API
4. Create credentials (API Key)
5. Set the API key as environment variable:
   ```bash
   export GOOGLE_MAPS_API_KEY="your-api-key-here"
   ```

## Environment Variables

Create a `.env` file in the project root:

```env
# Google Maps API
GOOGLE_MAPS_API_KEY=your-google-maps-api-key

# Database
MONGO_URI=mongodb://mongo:27017/sparrow

# Redis
REDIS_HOST=redis
REDIS_PORT=6379

# Kafka
KAFKA_BROKER=kafka:9092

# JWT Secret (for development)
JWT_SECRET=your-jwt-secret-key
```

## API Endpoints

### Tracking Service

#### Update Tracking
```http
POST /api/tracking/update
Authorization: Bearer <jwt-token>
Content-Type: application/json

{
  "trackingNumber": "SPRW-1234567890-123456",
  "status": "IN_TRANSIT",
  "description": "Package is on the way",
  "latitude": 40.7128,
  "longitude": -74.0060,
  "driverId": "driver-123",
  "vehicleId": "vehicle-456",
  "notes": "Traffic delay expected"
}
```

#### Get Tracking Info
```http
GET /api/tracking/{trackingNumber}
Authorization: Bearer <jwt-token>
```

#### Get Tracking by Driver
```http
GET /api/tracking/driver/{driverId}
Authorization: Bearer <jwt-token>
```

#### Get Tracking by Location
```http
GET /api/tracking/location?latitude=40.7128&longitude=-74.0060&radiusKm=5.0
Authorization: Bearer <jwt-token>
```

### Consolidation Service

#### Create Consolidation Group
```http
POST /api/consolidation/create
Authorization: Bearer <jwt-token>
Content-Type: application/json

{
  "type": "ZIP_CODE",
  "destinationZip": "10001",
  "destinationCity": "New York",
  "destinationState": "NY",
  "destinationCountry": "USA",
  "parcelIds": ["parcel-1", "parcel-2", "parcel-3"],
  "warehouseId": "warehouse-123",
  "assignedDriver": "driver-456",
  "assignedVehicle": "vehicle-789"
}
```

#### Get Consolidation Group
```http
GET /api/consolidation/{groupId}
Authorization: Bearer <jwt-token>
```

#### Auto-consolidate by ZIP
```http
POST /api/consolidation/auto-consolidate/zip/{destinationZip}
Authorization: Bearer <jwt-token>
```

#### Auto-consolidate by City
```http
POST /api/consolidation/auto-consolidate/city/{destinationCity}
Authorization: Bearer <jwt-token>
```

#### Update Consolidation Status
```http
PUT /api/consolidation/{groupId}/status/{status}
Authorization: Bearer <jwt-token>
```

## Database Collections

### Tracking Service Collections
- `tracking_events`: Historical tracking events
- `parcel_locations`: Current parcel locations with geospatial indexing

### Consolidation Service Collections
- `consolidation_groups`: Consolidation group information
- `parcel_consolidations`: Individual parcel consolidation records

## Kafka Topics

### Tracking Service Topics
- `tracking-updates`: Tracking event updates
- `tracking-status-changes`: Status change notifications

### Consolidation Service Topics
- `consolidation-events`: New consolidation group events
- `consolidation-status-updates`: Status update events

## Security

Both services implement JWT-based authentication with role-based access control:

- **ADMIN**: Full access to all operations
- **STAFF**: Can create and manage consolidations, view tracking
- **DRIVER**: Can update tracking, view assigned consolidations
- **CUSTOMER**: Can view tracking information for their parcels

## Running the Services

1. **Start infrastructure services**:
   ```bash
   docker-compose up -d mongo redis kafka zookeeper
   ```

2. **Build and start the services**:
   ```bash
   docker-compose up --build tracking-service consolidation-service
   ```

3. **Or start all services**:
   ```bash
   docker-compose up --build
   ```

## Testing

### Health Checks
- Tracking Service: `GET http://localhost:8008/api/tracking/health`
- Consolidation Service: `GET http://localhost:8003/api/consolidation/health`

### Sample Test Data

#### Create a test parcel (via Parcel Service)
```http
POST /api/parcels
Authorization: Bearer <jwt-token>
Content-Type: application/json

{
  "senderName": "John Doe",
  "receiverName": "Jane Smith",
  "origin": "New York, NY",
  "destination": "Los Angeles, CA",
  "weightKg": 2.5,
  "dimensions": "30x20x15 cm"
}
```

#### Update tracking for the parcel
```http
POST /api/tracking/update
Authorization: Bearer <jwt-token>
Content-Type: application/json

{
  "trackingNumber": "SPRW-1234567890-123456",
  "status": "PICKED_UP",
  "description": "Package picked up from sender",
  "latitude": 40.7128,
  "longitude": -74.0060,
  "driverId": "driver-123"
}
```

## Monitoring

Both services expose actuator endpoints for monitoring:
- Health: `/actuator/health`
- Metrics: `/actuator/metrics`
- Info: `/actuator/info`

## Troubleshooting

### Common Issues

1. **Google Maps API errors**: Ensure API key is valid and required APIs are enabled
2. **Kafka connection issues**: Check if Kafka and Zookeeper are running
3. **MongoDB connection issues**: Verify MongoDB is accessible
4. **JWT validation errors**: Ensure JWT secret is consistent across services

### Logs
Check service logs:
```bash
docker-compose logs tracking-service
docker-compose logs consolidation-service
```

