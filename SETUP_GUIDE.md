# Sparrow Backend Services Setup Guide

## 🚀 **Quick Start Setup**

### **Step 1: Prerequisites**
- Docker and Docker Compose installed
- Google Maps API Key (for tracking service)
- Postman (for testing)

### **Step 2: Environment Setup**

#### **Create .env file in root directory:**
```bash
# Google Maps API Key (Required for tracking service)
GOOGLE_MAPS_API_KEY=your_google_maps_api_key_here

# Optional - Override defaults
SPRING_PROFILES_ACTIVE=docker
MONGO_URI=mongodb://mongo:27017/sparrow
REDIS_HOST=redis
KAFKA_BROKER=kafka:9092
```

#### **Get Google Maps API Key:**
1. Go to [Google Cloud Console](https://console.cloud.google.com/)
2. Create new project or select existing
3. Enable these APIs:
   - Geocoding API
   - Static Maps API
   - Maps JavaScript API
4. Create API credentials
5. Copy the API key to your `.env` file

### **Step 3: Start All Services**
```bash
# Start all services
docker-compose up -d

# Check service status
docker-compose ps

# View logs
docker-compose logs -f tracking-service
docker-compose logs -f consolidation-service
```

### **Step 4: Verify Services are Running**
```bash
# Check health endpoints
curl http://localhost:8008/actuator/health  # Tracking Service
curl http://localhost:8009/actuator/health  # Consolidation Service
curl http://localhost:8080/actuator/health  # API Gateway
```

---

## 🧪 **Testing with Postman**

### **Step 1: Get Authentication Token**

#### **Login to get JWT token:**
```http
POST http://localhost:8080/api/auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "admin123"
}
```

**Expected Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer"
}
```

**Save the token for all subsequent requests!**

---

## 📍 **Tracking Service Testing**

### **1. Update Tracking Location**
```http
POST http://localhost:8008/api/tracking/update
Authorization: Bearer {your_jwt_token}
Content-Type: application/json

{
  "trackingNumber": "TRK123456",
  "status": "IN_TRANSIT",
  "description": "Package picked up from warehouse",
  "latitude": 40.7128,
  "longitude": -74.0060,
  "driverId": "DRIVER001",
  "vehicleId": "VEHICLE001",
  "notes": "Package in transit to customer",
  "accuracy": 10.0
}
```

**Expected Response:**
```json
{
  "trackingNumber": "TRK123456",
  "status": "IN_TRANSIT",
  "latitude": 40.7128,
  "longitude": -74.0060,
  "address": "New York, NY, USA",
  "city": "New York",
  "state": "New York",
  "zipCode": "10001",
  "country": "United States",
  "placeId": "ChIJN1t_tDeuEmsRUsoyG83frY4",
  "mapUrl": "https://www.google.com/maps?q=40.7128,-74.0060",
  "staticMapUrl": "https://maps.googleapis.com/maps/api/staticmap?...",
  "estimatedDeliveryTime": 60.0,
  "currentLocation": "New York, NY, USA",
  "isDelivered": false,
  "deliveryStatus": "IN_TRANSIT",
  "history": ["..."]
}
```

### **2. Get Tracking Information**
```http
GET http://localhost:8008/api/tracking/TRK123456
Authorization: Bearer {your_jwt_token}
```

### **3. Get Parcels Near Location**
```http
GET http://localhost:8008/api/tracking/nearby?lat=40.7128&lng=-74.0060&radius=5
Authorization: Bearer {your_jwt_token}
```

### **4. Get Tracking by Driver**
```http
GET http://localhost:8008/api/tracking/driver/DRIVER001
Authorization: Bearer {your_jwt_token}
```

---

## 📦 **Consolidation Service Testing**

### **1. Create Consolidation Group**
```http
POST http://localhost:8009/api/consolidation/groups
Authorization: Bearer {your_jwt_token}
Content-Type: application/json

{
  "type": "ZIP_CODE",
  "destinationZip": "10001",
  "destinationCity": "New York",
  "destinationState": "New York",
  "destinationCountry": "USA",
  "parcelIds": ["PARCEL001", "PARCEL002", "PARCEL003"],
  "trackingNumbers": ["TRK123456", "TRK123457", "TRK123458"],
  "assignedDriver": "DRIVER001",
  "assignedVehicle": "VEHICLE001",
  "warehouseId": "WAREHOUSE001",
  "notes": "Consolidated delivery to Manhattan"
}
```

**Expected Response:**
```json
{
  "id": "consolidation-group-id",
  "groupId": "CONS-2024-001",
  "type": "ZIP_CODE",
  "destinationZip": "10001",
  "destinationCity": "New York",
  "destinationState": "New York",
  "destinationCountry": "USA",
  "parcelIds": ["PARCEL001", "PARCEL002", "PARCEL003"],
  "trackingNumbers": ["TRK123456", "TRK123457", "TRK123458"],
  "totalWeight": 25.5,
  "totalVolume": 2.3,
  "parcelCount": 3,
  "status": "PENDING",
  "assignedDriver": "DRIVER001",
  "assignedVehicle": "VEHICLE001",
  "warehouseId": "WAREHOUSE001",
  "estimatedCost": 150.00,
  "createdAt": "2024-01-15T10:30:00Z"
}
```

### **2. Get Consolidation Group**
```http
GET http://localhost:8009/api/consolidation/groups/CONS-2024-001
Authorization: Bearer {your_jwt_token}
```

### **3. Update Consolidation Status**
```http
PUT http://localhost:8009/api/consolidation/groups/CONS-2024-001/status
Authorization: Bearer {your_jwt_token}
Content-Type: application/json

{
  "status": "IN_TRANSIT",
  "notes": "Group picked up and in transit"
}
```

### **4. Get All Consolidation Groups**
```http
GET http://localhost:8009/api/consolidation/groups
Authorization: Bearer {your_jwt_token}
```

---

## 🌐 **API Gateway Testing**

### **1. Update Tracking via Gateway**
```http
POST http://localhost:8080/api/tracking/update
Authorization: Bearer {your_jwt_token}
Content-Type: application/json

{
  "trackingNumber": "TRK123456",
  "status": "OUT_FOR_DELIVERY",
  "latitude": 40.7589,
  "longitude": -73.9851,
  "driverId": "DRIVER001"
}
```

### **2. Get Tracking via Gateway**
```http
GET http://localhost:8080/api/tracking/TRK123456
Authorization: Bearer {your_jwt_token}
```

---

## 📊 **Test Scenarios**

### **Scenario 1: Complete Delivery Flow**
1. **Create tracking**: POST `/api/tracking/update` with status "PICKED_UP"
2. **Update location**: POST `/api/tracking/update` with status "IN_TRANSIT"
3. **Final delivery**: POST `/api/tracking/update` with status "DELIVERED"
4. **Check history**: GET `/api/tracking/TRK123456`

### **Scenario 2: Consolidation Flow**
1. **Create group**: POST `/api/consolidation/groups`
2. **Update status**: PUT `/api/consolidation/groups/{id}/status`
3. **Track parcels**: GET `/api/tracking/driver/{driverId}`

### **Scenario 3: Location-based Queries**
1. **Update multiple locations**: POST `/api/tracking/update` for different parcels
2. **Find nearby**: GET `/api/tracking/nearby?lat=40.7128&lng=-74.0060&radius=5`

---

## 🚨 **Troubleshooting**

### **Common Issues:**

#### **1. "Invalid API Key" Error**
- Check Google Maps API is enabled
- Verify API key in `.env` file
- Ensure billing is set up

#### **2. "Service Unavailable"**
```bash
# Check if services are running
docker-compose ps

# Restart specific service
docker-compose restart tracking-service

# View logs
docker-compose logs tracking-service
```

#### **3. "Authentication Failed"**
- Verify JWT token is valid
- Check token expiration
- Re-login to get new token

#### **4. "Database Connection Failed"**
```bash
# Check MongoDB
docker-compose logs mongo

# Restart database
docker-compose restart mongo
```

### **Health Check Endpoints:**
- Tracking Service: `http://localhost:8008/actuator/health`
- Consolidation Service: `http://localhost:8009/actuator/health`
- API Gateway: `http://localhost:8080/actuator/health`

---

## 📱 **Frontend Integration**

### **React Component Example:**
```jsx
import React, { useState, useEffect } from 'react';

const TrackingComponent = ({ trackingNumber }) => {
  const [trackingData, setTrackingData] = useState(null);

  useEffect(() => {
    const fetchTracking = async () => {
      const response = await fetch(`/api/tracking/${trackingNumber}`, {
        headers: {
          'Authorization': `Bearer ${localStorage.getItem('token')}`
        }
      });
      const data = await response.json();
      setTrackingData(data);
    };

    fetchTracking();
  }, [trackingNumber]);

  if (!trackingData) return <div>Loading...</div>;

  return (
    <div>
      <h3>Package: {trackingData.trackingNumber}</h3>
      <p>Status: {trackingData.deliveryStatus}</p>
      <p>Location: {trackingData.currentLocation}</p>
      <p>ETA: {trackingData.estimatedDeliveryTime} minutes</p>
      
      {/* Static Map */}
      <img src={trackingData.staticMapUrl} alt="Location" />
      
      {/* Interactive Map Link */}
      <a href={trackingData.mapUrl} target="_blank">
        View on Google Maps
      </a>
    </div>
  );
};
```

---

## 🎯 **Success Criteria**

✅ **Tracking Service Working:**
- Location updates with Google Maps integration
- Status tracking with history
- Geospatial queries
- Real-time updates via Kafka

✅ **Consolidation Service Working:**
- Parcel grouping by location
- Status management
- Resource assignment
- Cost tracking

✅ **API Gateway Working:**
- Route forwarding to services
- Authentication
- Load balancing

✅ **Frontend Ready:**
- Google Maps URLs generated
- Real-time data available
- Mobile-friendly endpoints
