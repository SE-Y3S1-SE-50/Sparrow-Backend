# Google Maps Integration Guide

## Overview
The Tracking Service now includes comprehensive Google Maps API integration for real-time location tracking and frontend map display.

## 🗺️ **How It Works**

### 1. **Driver Location Updates**
- Drivers send GPS coordinates (latitude/longitude) to the tracking service
- Google Maps API converts coordinates to human-readable addresses
- Location data is stored in MongoDB with geospatial indexing
- Real-time updates are published to Kafka for other services

### 2. **Frontend Map Display**
The tracking service provides multiple Google Maps URLs for frontend integration:

#### **Interactive Map URL**
```
https://www.google.com/maps?q={latitude},{longitude}
```
- Opens Google Maps with the exact location
- Perfect for "View on Map" buttons

#### **Static Map Image**
```
https://maps.googleapis.com/maps/api/staticmap?center={lat},{lng}&zoom=15&size=400x300&markers=color:red%7C{lat},{lng}&key={API_KEY}
```
- Returns a static map image
- Ideal for tracking cards and thumbnails
- No JavaScript required

### 3. **Response Data Structure**
```json
{
  "trackingNumber": "TRK123456",
  "status": "IN_TRANSIT",
  "latitude": 40.7128,
  "longitude": -74.0060,
  "address": "123 Main St, New York, NY 10001",
  "city": "New York",
  "state": "New York",
  "zipCode": "10001",
  "country": "United States",
  "placeId": "ChIJN1t_tDeuEmsRUsoyG83frY4",
  "mapUrl": "https://www.google.com/maps?q=40.7128,-74.0060",
  "staticMapUrl": "https://maps.googleapis.com/maps/api/staticmap?...",
  "estimatedDeliveryTime": 60.0,
  "currentLocation": "123 Main St, New York, NY 10001",
  "isDelivered": false,
  "deliveryStatus": "IN_TRANSIT",
  "history": ["..."]
}
```

## 🚀 **Frontend Integration Examples**

### **React Component Example**
```jsx
import React from 'react';

const TrackingMap = ({ trackingData }) => {
  return (
    <div className="tracking-map">
      <h3>Package Location</h3>
      
      {/* Static Map Image */}
      <img 
        src={trackingData.staticMapUrl} 
        alt="Package location"
        className="map-thumbnail"
      />
      
      {/* Interactive Map Link */}
      <a 
        href={trackingData.mapUrl} 
        target="_blank" 
        rel="noopener noreferrer"
        className="map-link"
      >
        View on Google Maps
      </a>
      
      {/* Location Details */}
      <div className="location-details">
        <p><strong>Current Location:</strong> {trackingData.currentLocation}</p>
        <p><strong>Status:</strong> {trackingData.deliveryStatus}</p>
        <p><strong>ETA:</strong> {trackingData.estimatedDeliveryTime} minutes</p>
      </div>
    </div>
  );
};
```

### **Real-time Updates with WebSocket**
```javascript
// Connect to tracking updates
const ws = new WebSocket('ws://localhost:8008/tracking/updates');

ws.onmessage = (event) => {
  const trackingData = JSON.parse(event.data);
  
  // Update map with new location
  updateMapMarker(trackingData.latitude, trackingData.longitude);
  
  // Update status and ETA
  updateStatus(trackingData.deliveryStatus, trackingData.estimatedDeliveryTime);
};
```

## 📍 **API Endpoints**

### **Update Tracking Location**
```http
POST /api/tracking/update
Authorization: Bearer {JWT_TOKEN}
Content-Type: application/json

{
  "trackingNumber": "TRK123456",
  "status": "IN_TRANSIT",
  "latitude": 40.7128,
  "longitude": -74.0060,
  "driverId": "DRIVER001",
  "vehicleId": "VEHICLE001",
  "notes": "Package picked up from warehouse"
}
```

### **Get Tracking Information**
```http
GET /api/tracking/{trackingNumber}
Authorization: Bearer {JWT_TOKEN}
```

### **Get Parcels Near Location**
```http
GET /api/tracking/nearby?lat=40.7128&lng=-74.0060&radius=5
Authorization: Bearer {JWT_TOKEN}
```

## 🔧 **Configuration**

### **Environment Variables**
```bash
# Required
GOOGLE_MAPS_API_KEY=your_google_maps_api_key_here

# Optional (with defaults)
SPRING_PROFILES_ACTIVE=docker
MONGO_URI=mongodb://mongo:27017/sparrow_tracking
REDIS_HOST=redis
KAFKA_BROKER=kafka:9092
```

### **Google Maps API Setup**
1. Go to [Google Cloud Console](https://console.cloud.google.com/)
2. Create a new project or select existing
3. Enable these APIs:
   - Geocoding API
   - Static Maps API
   - Maps JavaScript API (for frontend)
4. Create API credentials
5. Add the API key to your `.env` file

## 🗄️ **Database Schema**

### **TrackingEvent Collection**
```javascript
{
  "_id": ObjectId("..."),
  "trackingNumber": "TRK123456",
  "status": "IN_TRANSIT",
  "latitude": 40.7128,
  "longitude": -74.0060,
  "address": "123 Main St, New York, NY 10001",
  "city": "New York",
  "state": "New York",
  "zipCode": "10001",
  "country": "United States",
  "placeId": "ChIJN1t_tDeuEmsRUsoyG83frY4",
  "formattedAddress": "123 Main St, New York, NY 10001, USA",
  "timestamp": ISODate("2024-01-15T10:30:00Z"),
  "driverId": "DRIVER001",
  "vehicleId": "VEHICLE001",
  "accuracy": 10.0
}
```

### **ParcelLocation Collection (with Geospatial Index)**
```javascript
{
  "_id": ObjectId("..."),
  "trackingNumber": "TRK123456",
  "location": {
    "type": "Point",
    "coordinates": [-74.0060, 40.7128] // [longitude, latitude]
  },
  "address": "123 Main St, New York, NY 10001",
  "timestamp": ISODate("2024-01-15T10:30:00Z"),
  "status": "IN_TRANSIT"
}
```

## 🧪 **Testing**

### **Test with Postman**
1. **Update Location:**
   ```http
   POST http://localhost:8008/api/tracking/update
   Headers: Authorization: Bearer {your_jwt_token}
   Body: {
     "trackingNumber": "TEST123",
     "status": "IN_TRANSIT",
     "latitude": 40.7128,
     "longitude": -74.0060,
     "driverId": "DRIVER001"
   }
   ```

2. **Get Tracking Info:**
   ```http
   GET http://localhost:8008/api/tracking/TEST123
   Headers: Authorization: Bearer {your_jwt_token}
   ```

### **Expected Response**
```json
{
  "trackingNumber": "TEST123",
  "status": "IN_TRANSIT",
  "latitude": 40.7128,
  "longitude": -74.0060,
  "address": "New York, NY, USA",
  "placeId": "ChIJN1t_tDeuEmsRUsoyG83frY4",
  "mapUrl": "https://www.google.com/maps?q=40.7128,-74.0060",
  "staticMapUrl": "https://maps.googleapis.com/maps/api/staticmap?...",
  "estimatedDeliveryTime": 60.0,
  "currentLocation": "New York, NY, USA",
  "isDelivered": false,
  "deliveryStatus": "IN_TRANSIT"
}
```

## 🚨 **Troubleshooting**

### **Common Issues**
1. **"Invalid API Key" Error**
   - Check if Google Maps API is enabled
   - Verify API key is correct
   - Ensure billing is set up

2. **"No Results" from Geocoding**
   - Check coordinates are valid
   - Verify API quotas haven't been exceeded

3. **Static Map Not Loading**
   - Check Static Maps API is enabled
   - Verify API key has proper permissions

### **Monitoring**
- Check service logs: `docker logs tracking-service`
- Monitor API usage in Google Cloud Console
- Use health endpoint: `GET /actuator/health`

## 📱 **Mobile Integration**
For mobile apps, you can use the same API endpoints. The static map URLs work well for mobile displays, and the interactive map URLs will open the device's default map app.

## 🔒 **Security Considerations**
- API key should be kept secure
- Use environment variables, never hardcode
- Consider API key restrictions (HTTP referrers, IP addresses)
- Monitor API usage to prevent abuse
