# IoT Data Ingestion Service

This service acts as the unified entry point for all IoT telemetry data. It accepts data via multiple protocols (HTTP, MQTT, TCP), validates authentication, adds tracing headers, and forwards messages to the internal processing pipeline (RabbitMQ/Kafka).

## 🚀 Connectivity Information

| Protocol | Port | Host | Auth Method | Use Case |
| :--- | :--- | :--- | :--- | :--- |
| **HTTP (REST)** | `8090` | `localhost` | Header: `X-Auth-Token` | Firmware updates, config, high-latency devices. |
| **MQTT** | `1883` | `localhost` | Username/Password | Low-power devices, unreliable networks (2G/3G). |
| **TCP (Raw)** | `9090` | `localhost` | Packet-based / IP Whitelist | Ultra-low overhead, legacy hardware (e.g., Teltonika). |

---

## 1. HTTP Interface (REST)

**Endpoint:** `POST /api/v1/telemetry/{deviceId}`

This is a synchronous interface. The device waits for a `202 Accepted` response before considering the packet sent.

### Headers
| Header | Value | Required | Description |
| :--- | :--- | :--- | :--- |
| `Content-Type` | `application/json` | ✅ | |
| `X-Auth-Token` | `secret-123` | ✅ | Device Secret or API Key. |
| `X-Correlation-ID` | `uuid-v4` | ❌ | Trace ID for debugging. If missing, server generates one. |
| `User-Agent` | `Model/Firmware` | ❌ | E.g., `Teltonika-FMB120/1.0`. |

### Example Request (cURL)
```bash
curl -v -X POST "http://localhost:8090/api/v1/telemetry/truck-101" \
  -H "Content-Type: application/json" \
  -H "X-Auth-Token: secret-123" \
  -H "X-Correlation-ID: 7f9g8h12-3456-7890-abcd-ef1234567890" \
  -H "User-Agent: Teltonika-FMB120/1.0" \
  -d '{
    "timestamp": 1704967200000,
    "priority": "normal",
    "data": {
      "latitude": 19.0760,
      "longitude": 72.8777,
      "speed": 65.5,
      "ignition": true,
      "fuelLevel": 78.5
    }
  }'
```
Success Response: 202 Accepted

### Part 2: Copy this and paste it right after Part 1

## 2. MQTT Interface (Pub/Sub)

**Broker:** `tcp://localhost:1883`

This is an asynchronous, "fire-and-forget" interface. Suitable for constrained bandwidth.

### Topic Structure
Format: `iiotedge/{tenantId}/{deviceId}/uplink`

* `tenantId`: The organization ID (e.g., `org1`).
* `deviceId`: The unique hardware ID (e.g., `truck-103`).

### Example Publish (Mosquitto)
```bash
mosquitto_pub -h localhost -p 1883 \
  -u "device_user" -P "device_password" \
  -t "iiotedge/org1/truck-103/uplink" \
  -m '{
    "timestamp": 1704967210000,
    "data": {
      "latitude": 28.6139,
      "longitude": 77.2090,
      "speed": 55.0,
      "battery": 12.8,
      "status": "MOVING"
    }
  }'
```

### 3. TCP Interface (Raw Socket)
   Socket: tcp://localhost:9090

Used for high-frequency data or legacy trackers that cannot run an HTTP/MQTT stack. Expects Newline Delimited JSON (NDJSON).

Packet Format
The server expects a raw JSON string followed by a newline character \n.

```bash
echo '{"deviceId": "truck-tcp-01", "timestamp": 1704967220000, "token": "secret-123", "data": {"temp": 22.5, "rpm": 1500}}' | nc localhost 9090
```