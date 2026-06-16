# PART 13-14 - GIT & BUILD TOOLS

> **Topics**: Git branching · Merge · Rebase · Maven · Gradle · Ant

---

## PART 13 — GIT

### Q1. Git Branching Strategies

**GitFlow (Enterprise Standard):**
```
main (production)
├── develop (integration branch)
│   ├── feature/JIRA-123-user-auth
│   ├── feature/JIRA-456-payment-api
│   └── release/1.2.0
│       → merge to main + develop when stable
└── hotfix/critical-bug-fix
    → merge to main + develop immediately
```

**Trunk-Based Development (Modern/CI-CD):**
```
main (always deployable)
├── feature/short-lived-branch (max 1-2 days)
└── release/v1.2.0 (cut from main)
```

```bash
# Daily workflow
git checkout develop
git pull origin develop

# Create feature branch
git checkout -b feature/JIRA-789-add-payment-gateway

# Work, commit
git add .
git commit -m "feat(payment): add Stripe integration

- Add StripePaymentService implementation
- Add payment config properties  
- Add unit tests for payment flow

Closes JIRA-789"

# Push and create PR
git push origin feature/JIRA-789-add-payment-gateway
```

---

### Q2. Merge vs Rebase

**Merge:**
```bash
# Creates a merge commit, preserves history
git checkout develop
git merge feature/payment-gateway

# History:
# A---B---C---D (feature branch)
#              \
# E---F---G---H---M (develop, M = merge commit)
```

**Rebase:**
```bash
# Replays commits on top of target branch — linear history
git checkout feature/payment-gateway
git rebase develop

# Before: 
# A---B---C (develop)
#      \
#       D---E (feature)

# After rebase:
# A---B---C---D'---E' (feature replayed on top of C)
```

**When to use:**
| Scenario | Use |
|---|---|
| Merging a feature branch to main/develop | `git merge` (preserves PR history) |
| Keeping feature branch up-to-date with develop | `git rebase develop` |
| Cleaning up commits before PR | `git rebase -i HEAD~3` (interactive) |

**Interactive Rebase — Clean up commits:**
```bash
git rebase -i HEAD~3  # Edit last 3 commits

# Opens editor:
pick abc123 Add payment service
pick def456 Fix typo
pick ghi789 Add tests

# Change to:
pick abc123 Add payment service
squash def456 Fix typo       # Combine with previous
pick ghi789 Add tests
# Result: 2 clean commits instead of 3
```

---

### Q3. Cherry Pick

```bash
# Apply specific commit from another branch to current branch
git log --oneline feature/auth  # Find commit hash
# abc123 Add JWT token generation

git checkout hotfix/security
git cherry-pick abc123  # Apply only that commit

# Cherry-pick range
git cherry-pick abc123..def456

# Cherry-pick without committing (for review)
git cherry-pick --no-commit abc123
```

**Use case**: Critical bug fix was applied to feature branch — cherry-pick it to hotfix branch without merging entire feature.

---

### Q4. Git Stash

```bash
# Save unfinished work temporarily
git stash push -m "WIP: payment validation"

# Switch context (urgent bug fix)
git checkout hotfix/critical-bug
# ... fix bug, commit, push ...

# Return to original work
git checkout feature/payment
git stash pop              # Apply and drop stash
# or
git stash apply stash@{0}  # Apply but keep in stash list

# View all stashes
git stash list
# stash@{0}: On feature/payment: WIP: payment validation
# stash@{1}: On feature/auth: WIP: JWT implementation

# Apply specific stash
git stash apply stash@{1}

# Drop stash
git stash drop stash@{0}
git stash clear  # Drop all
```

---

### Q5. Conflict Resolution

```bash
# Scenario: Merge conflict in OrderService.java
git merge feature/order-optimization

# Auto-merge failed. Fix conflicts in:
#     src/main/java/com/example/service/OrderService.java

# Open the file - see conflict markers:
# <<<<<<< HEAD (your branch)
# public Order processOrder(Long orderId) {
#     Order order = orderRepository.findById(orderId).orElseThrow();
#     return orderService.validate(order);
# =======
# public OrderResponse processOrder(Long orderId) {
#     var order = orderRepository.findByIdWithItems(orderId).orElseThrow();
#     return mapper.toResponse(orderService.validate(order));
# >>>>>>> feature/order-optimization

# After resolving:
git add src/main/java/com/example/service/OrderService.java
git commit -m "merge: resolve OrderService conflict

Keep OrderResponse return type from feature/order-optimization
Keep validation logic from HEAD"

# Best practices to minimize conflicts:
# 1. Keep feature branches short-lived
# 2. Rebase frequently against main/develop
# 3. Small, focused commits
# 4. Communicate with team about touching same files
```

---

### Q6. Git Practical Scenarios

**Scenario: Undo last commit (not yet pushed)**
```bash
git reset HEAD~1          # Keep changes staged
git reset --soft HEAD~1   # Keep changes staged (same as above)
git reset --mixed HEAD~1  # Keep changes unstaged
git reset --hard HEAD~1   # DISCARD all changes (dangerous!)
```

**Scenario: Undo pushed commit**
```bash
# Create a new commit that reverses the changes (safe for shared branches)
git revert abc123
git push
```

**Scenario: Find which commit introduced a bug**
```bash
git bisect start
git bisect bad          # Current commit is broken
git bisect good v1.2.0  # This version was fine

# Git checks out middle commit - you test it
git bisect good  # or
git bisect bad
# ... repeat until git identifies the offending commit

git bisect reset  # Exit bisect mode
```

---

## PART 14 — MAVEN / GRADLE / ANT

### Q7. Maven Build Lifecycle

**Default Lifecycle Phases (in order):**
```
validate → compile → test → package → verify → install → deploy

validate:  Check project is correct and all info available
compile:   Compile source code
test:      Run unit tests (Surefire plugin)
package:   Create JAR/WAR
verify:    Run integration tests (Failsafe plugin)
install:   Install to local repository (~/.m2)
deploy:    Push to remote repository (Nexus/Artifactory)

# Skip tests
mvn package -DskipTests
mvn package -Dmaven.test.skip=true  # Skips compilation too

# Common commands
mvn clean compile
mvn clean package
mvn clean install
mvn dependency:tree         # Show dependency tree
mvn dependency:analyze      # Find unused/undeclared deps
mvn versions:display-dependency-updates  # Check for newer versions
```

**pom.xml Structure:**
```xml
<project>
    <modelVersion>4.0.0</modelVersion>
    
    <groupId>com.example</groupId>
    <artifactId>payment-service</artifactId>
    <version>1.2.0-SNAPSHOT</version>
    <packaging>jar</packaging>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.0</version>
    </parent>

    <properties>
        <java.version>21</java.version>
        <mapstruct.version>1.5.5.Final</mapstruct.version>
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        
        <!-- Test scope - not included in final artifact -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
        
        <!-- Provided - available at runtime by container -->
        <dependency>
            <groupId>jakarta.servlet</groupId>
            <artifactId>jakarta.servlet-api</artifactId>
            <scope>provided</scope>
        </dependency>
    </dependencies>

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

    <profiles>
        <profile>
            <id>production</id>
            <properties>
                <spring.profiles.active>production</spring.profiles.active>
            </properties>
        </profile>
    </profiles>
</project>
```

---

### Q8. Gradle Build

```groovy
// build.gradle (Groovy DSL)
plugins {
    id 'java'
    id 'org.springframework.boot' version '3.2.0'
    id 'io.spring.dependency-management' version '1.1.0'
}

group = 'com.example'
version = '1.2.0-SNAPSHOT'

java {
    sourceCompatibility = JavaVersion.VERSION_21
}

repositories {
    mavenCentral()
    maven { url 'https://repo.company.com/repository/maven-releases/' }
}

dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-web'
    implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
    runtimeOnly 'com.oracle.database.jdbc:ojdbc11'
    testImplementation 'org.springframework.boot:spring-boot-starter-test'
    testImplementation 'org.testcontainers:junit-jupiter:1.19.3'
}

// Custom task
tasks.register('generateBuildInfo') {
    doLast {
        def file = new File("$buildDir/build-info.properties")
        file.text = "version=${version}\nbuild.date=${new Date()}"
    }
}

// Common commands:
// ./gradlew build           - Build
// ./gradlew test            - Run tests
// ./gradlew bootRun         - Run Spring Boot app
// ./gradlew dependencies    - Show dependency tree
// ./gradlew clean build     - Clean and build
```

---

### Q9. Maven vs Gradle vs Ant Comparison

| Feature | Maven | Gradle | Ant |
|---|---|---|---|
| Configuration | XML (pom.xml) | Groovy/Kotlin DSL | XML (build.xml) |
| Convention | Strong (convention-over-config) | Flexible | None |
| Performance | Moderate | Fast (incremental, daemon, build cache) | Fast but manual |
| Dependency Mgmt | Maven Central, transitive deps | Same + Ivy | Manual (no built-in) |
| Multi-module | Supported | Supported (better) | Manual |
| Learning Curve | Moderate | Higher (more flexible) | Lower |
| Enterprise Use | Very common | Growing fast | Legacy |
| Build Cache | Basic | Advanced (local + remote) | No |
| Incremental Build | Limited | Yes | No |
| Best For | Standard Java projects | Complex builds, Android | Legacy projects |

**Why Gradle is faster:**
1. **Incremental builds**: Only rebuilds changed modules
2. **Build cache**: Reuse outputs from previous builds (even across machines)
3. **Daemon**: JVM stays warm between builds
4. **Parallel task execution**: Multiple tasks run simultaneously

---

*Next: [Part 15 - System Design](./Part-15-System-Design.md)*
