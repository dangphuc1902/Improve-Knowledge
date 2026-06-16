# PART 10 — Web Services (REST & SOAP)

---

## 🔷 REST

### Q1: What are the REST Principles (Constraints)?

**Ideal Answer (6 constraints):**

| Constraint | Description |
|-----------|-------------|
| **Client-Server** | Separation of concerns. UI & backend evolve independently. |
| **Stateless** | Each request contains all info needed. No session state on server. |
| **Cacheable** | Responses must declare if cacheable. Improves performance. |
| **Uniform Interface** | Standard resource identification (URI), standard manipulation (HTTP verbs) |
| **Layered System** | Client doesn't know if connected directly to server or via proxy/load balancer |
| **Code on Demand** (optional) | Server can send executable code (JS) to client |

---

### Q2: HTTP Methods and When to Use Them

| Method | Idempotent | Safe | Use Case |
|--------|-----------|------|---------|
| GET | ✅ | ✅ | Read resource |
| POST | ❌ | ❌ | Create resource, trigger action |
| PUT | ✅ | ❌ | Replace entire resource |
| PATCH | ❌* | ❌ | Partial update |
| DELETE | ✅ | ❌ | Delete resource |
| HEAD | ✅ | ✅ | Like GET, no body (check if resource exists) |
| OPTIONS | ✅ | ✅ | CORS preflight, supported methods |

*PATCH can be made idempotent with proper implementation

**Idempotent:** Multiple identical requests = same result as one request.
**Safe:** Request doesn't change server state.

---

### Q3: HTTP Status Codes

```
1xx — Informational
2xx — Success
    200 OK
    201 Created
    204 No Content
    206 Partial Content

3xx — Redirection
    301 Moved Permanently
    302 Found (temporary redirect)
    304 Not Modified (cache hit)

4xx — Client Error
    400 Bad Request          (invalid input)
    401 Unauthorized         (not authenticated)
    403 Forbidden            (authenticated but no permission)
    404 Not Found
    405 Method Not Allowed
    409 Conflict             (duplicate resource)
    422 Unprocessable Entity (validation failed)
    429 Too Many Requests    (rate limited)

5xx — Server Error
    500 Internal Server Error
    502 Bad Gateway
    503 Service Unavailable
    504 Gateway Timeout
```

**Common Interview Trap:** "What's the difference between 401 and 403?"
> 401 = Not authenticated (no valid credentials provided)
> 403 = Authenticated but not authorized (you're logged in but don't have permission)

---

### Q4: REST API Design Best Practices

```
✅ Use nouns for resources (not verbs)
GET /users          → list users
GET /users/{id}     → get user
POST /users         → create user
PUT /users/{id}     → update user (full replace)
PATCH /users/{id}   → partial update
DELETE /users/{id}  → delete user

❌ Bad:
GET /getUsers
POST /createUser
GET /deleteUser?id=1

✅ Nested resources (relationships):
GET /users/{userId}/orders         → user's orders
GET /users/{userId}/orders/{id}    → specific order
POST /users/{userId}/orders        → create order for user

✅ Filtering, sorting, pagination:
GET /products?category=electronics&sort=price&order=asc&page=1&size=20

✅ Versioning:
/api/v1/users
/api/v2/users
```

---

### Q5: REST Authentication & Authorization

**Authentication** (Who are you?):
```
1. Basic Auth: Base64(username:password) in header — only HTTPS
   Authorization: Basic cGh1YzpwYXNzd29yZA==

2. Bearer Token (JWT):
   Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...

3. API Key:
   X-API-Key: sk-1234567890
   or ?api_key=sk-1234567890
```

**JWT Structure:**
```
header.payload.signature

{alg: "HS256", typ: "JWT"}
.
{sub: "123", name: "Phuc", roles: ["USER"], iat: 1623456789, exp: 1623460389}
.
HMACSHA256(base64(header) + "." + base64(payload), secret)
```

**Authorization** (What can you do?):
```
RBAC (Role-Based Access Control):
  User has Role(s) → Role has Permission(s)
  
ABAC (Attribute-Based Access Control):
  User.department == Resource.department → allow

Row-Level Security:
  User can only access their own data
  WHERE user_id = authenticated_user_id
```

---

## 🔷 SOAP

### Q6: What is SOAP?

**SOAP (Simple Object Access Protocol):**
- XML-based messaging protocol
- Platform and language independent
- Can use HTTP, SMTP, TCP
- Strict contract defined by WSDL

**SOAP Message Structure:**
```xml
<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
  <soap:Header>
    <!-- Authentication, routing info -->
    <auth:Token>Bearer xyz123</auth:Token>
  </soap:Header>
  <soap:Body>
    <!-- Actual request/response -->
    <GetUser>
      <userId>123</userId>
    </GetUser>
  </soap:Body>
  <soap:Fault>  <!-- Only in error responses -->
    <faultcode>soap:Client</faultcode>
    <faultstring>User not found</faultstring>
  </soap:Fault>
</soap:Envelope>
```

---

### Q7: WSDL

**WSDL (Web Services Description Language):**
> An XML document that describes SOAP web service: what operations it provides, what data types it uses, and how to call it.

```xml
<definitions name="UserService" ...>

  <types>  <!-- Data types (XML Schema) -->
    <xsd:element name="GetUserRequest">
      <xsd:complexType>
        <xsd:sequence>
          <xsd:element name="userId" type="xsd:long"/>
        </xsd:sequence>
      </xsd:complexType>
    </xsd:element>
  </types>

  <message name="GetUserInput">  <!-- Input message -->
    <part name="parameters" element="GetUserRequest"/>
  </message>

  <portType name="UserPortType">  <!-- Interface: operations -->
    <operation name="getUser">
      <input message="GetUserInput"/>
      <output message="GetUserOutput"/>
    </operation>
  </portType>

  <binding ...>  <!-- Transport + encoding -->
  </binding>

  <service name="UserService">  <!-- Endpoint URL -->
    <port name="UserPort" binding="UserBinding">
      <soap:address location="http://api.example.com/user-service"/>
    </port>
  </service>
</definitions>
```

---

## 🔷 REST vs SOAP Comparison

| Aspect | REST | SOAP |
|--------|------|------|
| Protocol | HTTP only | HTTP, SMTP, TCP, etc. |
| Data format | JSON, XML, etc. | XML only |
| Contract | OpenAPI/Swagger (optional) | WSDL (required) |
| State | Stateless | Can be stateful |
| Performance | Faster (lighter payload) | Slower (XML overhead) |
| Security | HTTPS + JWT/OAuth | WS-Security (built-in) |
| Error handling | HTTP status codes | SOAP Fault |
| Learning curve | Easy | Complex |
| Use case | Public APIs, mobile, web | Banking, enterprise, legacy |

**When to use SOAP:**
- Enterprise integration (banking, insurance, telecom legacy)
- When you need built-in security (WS-Security, WS-ReliableMessaging)
- Strict contract needed
- Non-HTTP transport

**When to use REST:**
- Public APIs, mobile apps, microservices
- When simplicity and performance matter
- JSON clients (JavaScript, mobile)
