# 📖 Build Tools (Maven & Gradle) + Git - Lý Thuyết & Bài Tập

> **Tuần 8 | Công cụ phát triển chuyên nghiệp**

---

## MAVEN

### 1. Maven POM Structure

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
                             https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <!-- Project coordinates -->
    <groupId>com.example</groupId>
    <artifactId>my-app</artifactId>
    <version>1.0.0-SNAPSHOT</version>
    <packaging>jar</packaging>

    <name>My Application</name>
    <description>Backend Java Developer practice project</description>

    <!-- Parent for Spring Boot projects -->
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.0</version>
    </parent>

    <!-- Properties -->
    <properties>
        <java.version>17</java.version>
        <maven.compiler.source>17</maven.compiler.source>
        <maven.compiler.target>17</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    </properties>

    <!-- Dependencies -->
    <dependencies>
        <!-- Spring Boot Starter -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <!-- Database -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>
        <dependency>
            <groupId>com.mysql</groupId>
            <artifactId>mysql-connector-j</artifactId>
            <scope>runtime</scope>
        </dependency>

        <!-- Testing (test scope) -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>

        <!-- Lombok (provided scope) -->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>
    </dependencies>

    <!-- Build -->
    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
                <configuration>
                    <excludes>
                        <exclude>
                            <groupId>org.projectlombok</groupId>
                            <artifactId>lombok</artifactId>
                        </exclude>
                    </excludes>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
```

---

### 2. Maven Lifecycle & Commands

```
Build Lifecycle Phases (in order):
1. validate    → Kiểm tra project valid
2. compile     → Compile source code
3. test        → Chạy unit tests
4. package     → Đóng gói (JAR/WAR)
5. verify      → Kiểm tra package
6. install     → Cài vào local repo (~/.m2)
7. deploy      → Deploy lên remote repo
```

```bash
# Common Maven commands
mvn --version                  # Xem version
mvn compile                    # Compile only
mvn test                       # Compile + test
mvn package                    # Compile + test + package
mvn package -DskipTests        # Package skip tests
mvn install                    # Install to local repo
mvn clean                      # Xóa target/ folder
mvn clean package              # Clean build
mvn clean install -DskipTests  # Production build

# Spring Boot
mvn spring-boot:run            # Run Spring Boot app
mvn spring-boot:build-image    # Build Docker image

# Dependency management
mvn dependency:tree            # Xem dependency tree
mvn dependency:resolve         # Download all deps
mvn versions:display-updates   # Xem versions có thể update

# Generate project
mvn archetype:generate \
  -DgroupId=com.example \
  -DartifactId=my-app \
  -DarchetypeArtifactId=maven-archetype-quickstart
```

---

### 3. Maven Dependency Scopes

| Scope | Compile | Test | Runtime | Packaged |
|-------|---------|------|---------|---------|
| `compile` (default) | ✅ | ✅ | ✅ | ✅ |
| `test` | ❌ | ✅ | ❌ | ❌ |
| `provided` | ✅ | ✅ | ❌ | ❌ |
| `runtime` | ❌ | ✅ | ✅ | ✅ |
| `optional` | ✅ | ✅ | ✅ | Depends |

---

## GRADLE

### 1. Gradle Build File (build.gradle.kts)

```kotlin
plugins {
    id("org.springframework.boot") version "3.2.0"
    id("io.spring.dependency-management") version "1.1.4"
    kotlin("jvm") version "1.9.20"
}

group = "com.example"
version = "0.0.1-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_17
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    runtimeOnly("com.mysql:mysql-connector-j")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
```

### 2. Gradle Commands

```bash
# Common Gradle commands (use ./gradlew on Unix)
./gradlew --version
./gradlew build             # Build project
./gradlew test              # Run tests
./gradlew bootRun           # Run Spring Boot
./gradlew clean build       # Clean build
./gradlew dependencies      # Show dependencies
./gradlew tasks             # List available tasks
./gradlew build -x test     # Build skip tests
```

---

### 3. Maven vs Gradle

| Tiêu chí | Maven | Gradle |
|---------|-------|--------|
| Config | XML (verbose) | Groovy/Kotlin DSL (concise) |
| Performance | Chậm hơn | Nhanh hơn (incremental builds, cache) |
| Flexibility | Convention over config | Rất linh hoạt |
| Learning curve | Dễ hơn | Khó hơn |
| Phổ biến | Rất phổ biến (Spring) | Phổ biến (Android, Kotlin) |

---

## GIT

### 1. Git Workflow

```bash
# Khởi tạo
git init                          # Init repo mới
git clone <url>                   # Clone remote repo
git clone <url> --depth 1         # Shallow clone (faster)

# Basic workflow
git status                        # Xem trạng thái
git add .                         # Stage tất cả thay đổi
git add src/                      # Stage folder cụ thể
git commit -m "feat: add user API"
git push origin main

# Branch
git branch                        # List branches
git branch feature/user-api       # Tạo branch mới
git checkout feature/user-api     # Chuyển branch
git checkout -b feature/login     # Tạo + chuyển
git merge feature/user-api        # Merge vào branch hiện tại
git branch -d feature/user-api    # Xóa branch local
git push origin --delete feature/user-api  # Xóa remote

# Remote
git remote -v                     # Xem remotes
git fetch origin                  # Fetch không merge
git pull origin main              # Fetch + merge
git push origin feature/login     # Push branch
git push -u origin main           # Push + set upstream

# History
git log --oneline -10             # 10 commits gần nhất
git log --graph --all --oneline   # Visual branch graph
git diff                          # Unstaged changes
git diff --staged                 # Staged changes
git show <commit-hash>            # Xem commit cụ thể
```

---

### 2. Git Branching Strategy

```
main (production)
  └── develop (integration)
        ├── feature/user-login
        ├── feature/payment-api
        └── bugfix/null-pointer-fix
```

**Git Flow:**
```bash
# Feature development
git checkout develop
git checkout -b feature/user-auth
# ... code ...
git push origin feature/user-auth
# Create Pull Request → Code Review → Merge to develop

# Release
git checkout develop
git checkout -b release/1.2.0
# ... testing, bugfixes ...
git checkout main && git merge release/1.2.0
git tag -a v1.2.0 -m "Release 1.2.0"
git checkout develop && git merge release/1.2.0
```

---

### 3. Git Advanced

```bash
# Stash - lưu tạm thay đổi
git stash                         # Stash current changes
git stash push -m "WIP: user feature"
git stash list                    # List stashes
git stash pop                     # Apply + remove stash
git stash apply stash@{1}         # Apply specific stash

# Rebase - rewrite history
git rebase main                   # Rebase lên main
git rebase -i HEAD~3              # Interactive rebase 3 commits
# Dùng để: squash commits, edit messages, reorder

# Cherry-pick - lấy commit cụ thể
git cherry-pick <commit-hash>

# Reset
git reset HEAD~1                  # Undo last commit (keep changes)
git reset --hard HEAD~1           # Undo + discard changes (CAREFUL!)
git reset HEAD <file>             # Unstage file

# Revert - safe undo
git revert <commit-hash>          # Tạo commit mới đảo ngược

# Tags
git tag -a v1.0.0 -m "First release"
git push origin --tags
```

---

### 4. Conventional Commits

```
<type>(<scope>): <subject>

Types:
feat:     New feature
fix:      Bug fix
docs:     Documentation
style:    Formatting (no logic change)
refactor: Code restructure (no bug fix, no feature)
test:     Adding tests
chore:    Maintenance (build tools, deps)
perf:     Performance improvement
ci:       CI/CD changes

Examples:
feat(auth): add JWT authentication
fix(order): handle null product price
docs(readme): update installation steps
refactor(user): extract validation to service
test(payment): add unit tests for PaymentService
```

---

## INTERVIEW Q&A

### Q1: Maven dùng để làm gì? File POM chứa gì?

**Maven**: Build automation tool cho Java - quản lý project build, dependencies, plugins.

**POM** (Project Object Model) chứa:
- Project coordinates (groupId, artifactId, version)
- Dependencies (thư viện cần dùng)
- Build plugins (compiler, test runner, packaging)
- Properties (Java version, encoding)
- Profiles (môi trường khác nhau)

---

### Q2: git pull vs git fetch?

- **git fetch**: Download changes từ remote, KHÔNG merge vào local branch. Safe.
- **git pull**: git fetch + git merge tự động. Có thể gây conflicts.

Best practice: `git fetch` trước, review, rồi `git merge` hoặc `git rebase`.

---

### Q3: git merge vs git rebase?

- **merge**: Tạo "merge commit" - preserves history, non-destructive
- **rebase**: Rewrites history - cleaner linear history, nhưng nguy hiểm với shared branches

**Rule**: Không rebase branches đã push/share với người khác → sẽ gây confusion.

---

### Q4: Làm sao resolve merge conflict?

```bash
git merge feature/user-api
# CONFLICT (content): Merge conflict in UserService.java

# Mở file, tìm markers:
<<<<<<< HEAD (current branch)
    public User findById(Long id) { return repo.findById(id).orElse(null); }
=======
    public Optional<User> findById(Long id) { return repo.findById(id); }
>>>>>>> feature/user-api

# Chọn version đúng (hoặc combine), xóa markers
# Sau đó:
git add UserService.java
git commit -m "resolve merge conflict in UserService"
```

---

## BÀI TẬP

### Git Practice
1. Tạo GitHub repo mới `java-backend-practice`
2. Clone về local
3. Tạo branch `feature/rest-api`
4. Code Spring Boot REST API
5. Commit với conventional commits
6. Push + tạo Pull Request

### Maven Practice
1. Generate Spring Boot project tại https://start.spring.io
2. Chọn: Web, JPA, MySQL, Lombok, Validation
3. Import vào IntelliJ
4. Chạy `mvn clean package -DskipTests`
5. Xem JAR trong `target/` folder

---

*📌 Tiếp theo: [11-Application-Servers](../11-Application-Servers/Theory.md) | [12-Frontend-Basic](../12-Frontend-Basic/Theory.md)*
