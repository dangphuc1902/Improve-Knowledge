# 📖 Application Servers - Lý Thuyết & Interview

> **Tuần 7 | Servlet Containers & Application Servers**

---

## LÝ THUYẾT

### 1. Servlet Container vs Application Server

| | Servlet Container | Application Server |
|--|------------------|-------------------|
| Ví dụ | Apache Tomcat, Jetty | JBoss/WildFly, WebSphere, WebLogic |
| Hỗ trợ | Servlet, JSP, WebSocket | Servlet + EJB + JMS + JTA + CDI (Full Java EE/Jakarta EE) |
| Trọng lượng | Nhẹ | Nặng hơn |
| Phù hợp | Spring Boot apps | Legacy enterprise apps |

---

### 2. Apache Tomcat

**Tomcat** là Servlet Container phổ biến nhất, miễn phí, open-source.

#### Cấu trúc thư mục Tomcat:
```
apache-tomcat-10.x/
├── bin/           - startup.sh, shutdown.sh, catalina.sh
├── conf/          - server.xml, web.xml, context.xml, tomcat-users.xml
├── lib/           - Tomcat JARs
├── logs/          - catalina.out, access logs
├── webapps/       - Deployed apps (WAR files)
│     ├── ROOT/    - Default app at /
│     └── myapp/   - Your app at /myapp
├── work/          - Compiled JSP files
└── temp/          - Temporary files
```

#### server.xml cơ bản:
```xml
<Server port="8005" shutdown="SHUTDOWN">
  <Service name="Catalina">
    <!-- HTTP Connector -->
    <Connector port="8080" protocol="HTTP/1.1"
               connectionTimeout="20000"
               redirectPort="8443"
               maxThreads="200"
               minSpareThreads="25"/>
    
    <!-- HTTPS Connector -->
    <Connector port="8443" protocol="org.apache.coyote.http11.Http11NioProtocol"
               maxThreads="150" SSLEnabled="true">
      <SSLHostConfig>
        <Certificate certificateKeystoreFile="conf/localhost-rsa.jks" type="RSA"/>
      </SSLHostConfig>
    </Connector>

    <Engine name="Catalina" defaultHost="localhost">
      <Host name="localhost" appBase="webapps" unpackWARs="true" autoDeploy="true">
        <!-- Context cho app cụ thể -->
        <Context path="/myapp" docBase="/opt/myapp" reloadable="true"/>
      </Host>
    </Engine>
  </Service>
</Server>
```

#### Deploy WAR lên Tomcat:
```bash
# 1. Build WAR file
mvn clean package    # Creates target/myapp.war

# 2. Deploy
# Option A: Copy to webapps/
cp target/myapp.war /opt/tomcat/webapps/

# Option B: Tomcat Manager (via browser)
# http://localhost:8080/manager/html
# Cần cấu hình user trong conf/tomcat-users.xml

# conf/tomcat-users.xml
<tomcat-users>
  <role rolename="manager-gui"/>
  <role rolename="admin-gui"/>
  <user username="admin" password="secret" roles="manager-gui,admin-gui"/>
</tomcat-users>

# 3. Start/Stop
./bin/startup.sh     # Linux
./bin/shutdown.sh

# Trên Windows
bin\startup.bat
bin\shutdown.bat
```

---

### 3. JBoss / WildFly

**WildFly** (formerly JBoss AS) = Full Java EE Application Server, miễn phí.  
**JBoss EAP** = Enterprise version (có hỗ trợ từ Red Hat, trả phí).

#### Deploy lên WildFly:
```bash
# Standalone mode
./bin/standalone.sh

# Deploy WAR
./bin/jboss-cli.sh --connect
deploy /path/to/myapp.war

# Hoặc copy vào standalone/deployments/
cp myapp.war $JBOSS_HOME/standalone/deployments/
```

---

### 4. IBM WebSphere & Oracle WebLogic

**WebSphere (IBM):** Enterprise-grade, phổ biến trong banking/financial systems.  
**WebLogic (Oracle):** Tích hợp tốt với Oracle DB, phổ biến trong Oracle ecosystem.

Đặc điểm chung:
- Full Java EE support
- Advanced clustering và HA
- Tích hợp với enterprise monitoring
- Commercial license (trả phí)

---

### 5. Embedded Server (Spring Boot)

Spring Boot có **embedded Tomcat/Jetty/Undertow** → không cần cài server riêng:

```xml
<!-- Default: embedded Tomcat -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>

<!-- Switch to Jetty -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
    <exclusions>
        <exclusion>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-tomcat</artifactId>
        </exclusion>
    </exclusions>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-jetty</artifactId>
</dependency>
```

```properties
# Cấu hình embedded Tomcat
server.port=8080
server.tomcat.max-threads=200
server.tomcat.min-spare-threads=25
server.tomcat.connection-timeout=5000
server.tomcat.accept-count=100
```

---

## INTERVIEW Q&A

### Q1: Tomcat vs JBoss/WildFly khác nhau thế nào?

**Tomcat:**
- Servlet container only
- Nhẹ, nhanh khởi động
- Đủ cho Spring Boot apps
- Miễn phí

**JBoss/WildFly:**
- Full Java EE/Jakarta EE implementation
- EJB, JMS, CDI, JTA built-in
- Nặng hơn nhưng feature-rich
- Dùng cho legacy enterprise apps

---

### Q2: WAR vs JAR packaging?

- **JAR** (Java ARchive): Spring Boot executable JAR - chứa embedded server, `java -jar myapp.jar`
- **WAR** (Web ARchive): Deploy lên external server (Tomcat, JBoss), cần server cài sẵn

**Modern trend:** Dùng JAR với embedded server → dễ deploy (Docker), Cloud-native.

---

### Q3: Làm sao cấu hình datasource cho Tomcat?

```xml
<!-- context.xml hoặc server.xml -->
<Resource name="jdbc/MyDB"
          auth="Container"
          type="javax.sql.DataSource"
          driverClassName="com.mysql.cj.jdbc.Driver"
          url="jdbc:mysql://localhost:3306/mydb"
          username="root"
          password="secret"
          maxTotal="20"
          maxIdle="10"
          maxWaitMillis="-1"/>
```

---

### Q4: JNDI là gì?

**JNDI (Java Naming and Directory Interface)**: API để lookup resources (DataSource, JMS queues) bằng name string. Application Servers đăng ký resources vào JNDI → app lookup theo tên.

```java
// JNDI lookup (Java EE style)
Context ctx = new InitialContext();
DataSource ds = (DataSource) ctx.lookup("java:comp/env/jdbc/MyDB");
```

---

## BÀI TẬP

### Bài 1: Deploy Spring MVC lên Tomcat

1. Tạo Spring MVC project (packaging = WAR)
2. Extend `SpringBootServletInitializer` cho WAR deployment
3. `mvn clean package` → tạo WAR
4. Cài Tomcat, copy WAR vào `webapps/`
5. Start Tomcat, verify app chạy

```java
// Cho WAR deployment
@SpringBootApplication
public class MyApp extends SpringBootServletInitializer {
    
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(MyApp.class);
    }
    
    public static void main(String[] args) {
        SpringApplication.run(MyApp.class, args);
    }
}
```

```xml
<!-- pom.xml - thay đổi packaging -->
<packaging>war</packaging>

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-tomcat</artifactId>
    <scope>provided</scope>  <!-- Tomcat cung cấp, không bundle vào WAR -->
</dependency>
```

---

*📌 Tiếp theo: [12-Frontend-Basic](../12-Frontend-Basic/Theory.md)*
