# MuleSoft ServiceNow Incident Integration

This MuleSoft project integrates with ServiceNow to manage incidents. The application provides two main flows:

- **Create Incident Flow**: Creates a new incident in ServiceNow.
- **Get Incident Flow**: Retrieves details of an existing incident from ServiceNow.

---

## 🛠 Features

- ServiceNow integration using MuleSoft ServiceNow Connector
- Externalized configuration using `properties.yaml` file
- Simple, clean design for easy extension

---

## 📂 Flows

### 1️⃣ Create Incident Flow

- Accepts input payload with `caller`, `comments`, and `short_description`.
- Transforms the payload into XML (for SOAP) or JSON (for REST) as required by ServiceNow.
- Calls ServiceNow to create a new incident.

### 2️⃣ Get Incident Flow

- Accepts an `incident sys_id` as input.
- Retrieves incident details from ServiceNow.
- Returns the incident information as response.

---

## 🔧 Configuration

ServiceNow connection details are externalized in `properties.yaml` located in `src/main/resources/`.

### Sample `properties.yaml`

```yaml
servicenow.host: your-instance.service-now.com
servicenow.username: your-username
servicenow.password: your-password
