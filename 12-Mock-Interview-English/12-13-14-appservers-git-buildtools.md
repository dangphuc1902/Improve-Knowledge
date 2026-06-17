# PART 12 — App Servers | PART 13 — Git | PART 14 — Build Tools

---

# ═══════════════════════════════════════
# PART 12: APPLICATION SERVERS
# ═══════════════════════════════════════

## Q1: Servlet Container vs Application Server

| | Servlet Container | Full Application Server |
|---|-------------------|------------------------|
| Definition | Runs Servlets/JSP | Servlet container + EJB + JMS + JTA |
| Examples | Tomcat, Jetty | JBoss/WildFly, WebSphere, WebLogic |
| Java EE support | Partial (Servlet/JSP) | Full Java EE / Jakarta EE |
| Use case | Spring Boot (embedded) | Enterprise EJB applications |

---

## Q2: Apache Tomcat

**What it is:** Lightweight servlet container. Implements Servlet and JSP specs.

**Components:**
```
Catalina → Servlet Container (core)
Coyote   → HTTP Connector (accepts connections)
Jasper   → JSP Engine
Cluster  → Session replication
```

**WAR Deployment:**
```
myapp.war
├── WEB-INF/
│   ├── web.xml          (deployment descriptor)
│   ├── classes/         (compiled .class files)
│   └── lib/             (JAR dependencies)
├── index.jsp
└── static/
```

```bash
# Deploy to Tomcat
cp myapp.war $TOMCAT_HOME/webapps/
# Tomcat auto-deploys and unpacks

# Or via Maven plugin
mvn tomcat7:deploy
```

**Spring Boot + Embedded Tomcat:**
> Spring Boot embeds Tomcat — no separate server needed. The app packages as a fat JAR with Tomcat inside.
```bash
java -jar myapp.jar
# Tomcat starts on port 8080
```

---

## Q3: JBoss / WildFly

**What it is:** Full Java EE application server (open source, Red Hat).

```
EAR deployment structure:
myapp.ear
├── META-INF/
│   └── application.xml
├── myapp.war       (web module)
└── myapp-ejb.jar   (EJB module)
```

**EAR (Enterprise Archive):** Packages multiple modules (WAR + EJB JAR) into one deployable unit.

**Key features over Tomcat:**
- EJB (Enterprise JavaBeans) support
- JTA (Java Transaction API) — distributed transactions
- JMS (Java Message Service)
- CDI (Context and Dependency Injection)
- JPA provider (Hibernate bundled)

---

## Q4: IBM WebSphere / BEA WebLogic

**WebSphere (IBM):**
- Enterprise-grade, heavily used in banking/insurance
- Supports full Java EE / Jakarta EE
- Admin Console for cluster/session management
- High availability with clustering and session replication

**WebLogic (Oracle, formerly BEA):**
- Full Java EE support
- Strong clustering and HA features
- Common in Oracle-stack enterprises
- Admin Server + Managed Servers topology

**ClassLoader Architecture (Key Interview Topic):**
```
Bootstrap ClassLoader (JVM built-ins: java.lang)
    ↓
Extension ClassLoader (jre/lib/ext)
    ↓
Application ClassLoader (classpath)
    ↓
Server ClassLoader (server libs)
    ↓
Application ClassLoader (WAR's WEB-INF/lib)
```

**ClassLoader issues in app servers:**
- `ClassCastException` when same class loaded by different ClassLoaders
- Stale class problem when hot-redeploy
- `java.lang.NoClassDefFoundError` — class in wrong scope

---

# ═══════════════════════════════════════
# PART 13: GIT
# ═══════════════════════════════════════

## Q5: Git Branching Strategy

**GitFlow (common in enterprise):**
```
main          ← production-ready
develop       ← integration branch
feature/xxx   ← new features (branch from develop)
release/x.x   ← release preparation
hotfix/xxx    ← production fixes (branch from main)
```

**Trunk-Based Development (modern CI/CD):**
```
main          ← everyone merges here frequently (short-lived feature branches)
```

---

## Q6: Merge vs Rebase

```bash
# MERGE: creates a merge commit, preserves history
git checkout main
git merge feature/login
# Result: non-linear history with merge commits ●

# REBASE: replays commits on top of target, linear history
git checkout feature/login
git rebase main
# Result: linear history, cleaner but rewrites commit hashes

# Rule: Never rebase shared/public branches
# Use rebase for local feature branches, merge for integrating to main
```

**Interactive Rebase (squash commits):**
```bash
git rebase -i HEAD~3    # interactive, last 3 commits
# pick, squash, fixup, reword...
```

---

## Q7: Cherry-Pick

```bash
# Apply specific commit from another branch
git cherry-pick abc1234         # apply one commit
git cherry-pick abc1234..def567 # apply range of commits
git cherry-pick abc1234 --no-commit  # apply changes without committing
```

**Use case:** Hotfix was made on `hotfix/bug-123` — cherry-pick it to `develop` too.

---

## Q8: Git Stash

```bash
# Save dirty working directory temporarily
git stash                      # stash with auto message
git stash push -m "WIP: login" # stash with message

# List stashes
git stash list

# Apply and drop
git stash pop       # apply most recent, remove from stash
git stash apply stash@{1}  # apply specific, keep in stash
git stash drop stash@{1}   # delete specific stash
git stash clear             # delete all stashes
```

---

## Q9: Conflict Resolution Scenario

**Scenario:** "You and a colleague both modified the same file. Walk me through resolving the conflict."

```bash
git pull origin main

# Auto-merge conflict:
<<<<<<< HEAD
// Your version
String userId = getUserId(request);
=======
// Their version
Long userId = extractUserId(token);
>>>>>>> origin/main

# Steps:
# 1. Understand both changes
# 2. Edit file to correct resolution
String userId = extractUserId(token).toString(); // merged decision

# 3. Stage and commit
git add src/main/java/.../Controller.java
git commit -m "resolve: merge userId extraction approach"
git push
```

**Prevent conflicts:**
- Communicate before touching shared files
- Small, frequent commits
- Short-lived feature branches
- Code review with early feedback

---

## Q10: Git Commands Cheat Sheet

```bash
# Branch
git branch -a                    # list all branches (local + remote)
git checkout -b feature/login    # create and switch
git branch -d feature/login      # delete local branch
git push origin --delete feature/login  # delete remote branch

# Reset
git reset --soft HEAD~1    # undo commit, keep staged
git reset --mixed HEAD~1   # undo commit, unstage (default)
git reset --hard HEAD~1    # undo commit, discard changes (DANGEROUS)

# Reflog (recovery)
git reflog                 # see all HEAD movements
git reset --hard HEAD@{3}  # go back to state 3 moves ago

# Log
git log --oneline --graph --all  # visual branch graph

# Diff
git diff HEAD~1 HEAD       # diff last commit
git diff main..feature     # diff between branches
```

---

# ═══════════════════════════════════════
# PART 14: BUILD TOOLS
# ═══════════════════════════════════════

## Q11: Maven vs Gradle vs Ant

| | Maven | Gradle | Ant |
|---|-------|--------|-----|
| Config language | XML (pom.xml) | Groovy/Kotlin DSL | XML (build.xml) |
| Paradigm | Convention over configuration | Flexible, programmatic | Fully manual |
| Dependency mgmt | Yes (Central repo) | Yes (Maven repos compatible) | No (manual) |
| Build lifecycle | Fixed (validate→compile→test→package→install→deploy) | Task-based, flexible | Task-based, no lifecycle |
| Incremental build | No | Yes (build cache) | No |
| Learning curve | Medium | Steeper | Low |
| Modern preference | ★★★★ | ★★★★★ | Legacy only |

---

## Q12: Maven Build Lifecycle

```
validate   → check project is correct
initialize → setup build state
generate-sources
process-sources
generate-resources
process-resources
compile    → compile source code
process-classes
generate-test-sources
process-test-sources
test-compile
process-test-resources
test       → run unit tests (JUnit)
prepare-package
package    → build JAR/WAR
verify     → run integration tests
install    → install to local ~/.m2 repo
deploy     → deploy to remote repository
```

```bash
mvn clean package          # clean then package
mvn clean install -DskipTests  # skip tests
mvn dependency:tree        # show dependency tree
mvn spring-boot:run        # run Spring Boot app
```

---

## Q13: Maven pom.xml Key Sections

```xml
<project>
  <groupId>com.example</groupId>
  <artifactId>fpm-user-service</artifactId>
  <version>1.0.0-SNAPSHOT</version>
  <packaging>jar</packaging>

  <parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>3.5.0</version>
  </parent>

  <properties>
    <java.version>21</java.version>
  </properties>

  <dependencies>
    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-web</artifactId>
      <!-- version from parent BOM -->
    </dependency>
    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-test</artifactId>
      <scope>test</scope>
    </dependency>
  </dependencies>

  <build>
    <plugins>
      <plugin>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-maven-plugin</artifactId>
      </plugin>
    </plugins>
  </build>
</project>
```

---

## Q14: Gradle basics

```groovy
// build.gradle
plugins {
    id 'java'
    id 'org.springframework.boot' version '3.5.0'
    id 'io.spring.dependency-management' version '1.1.4'
}

group = 'com.example'
version = '1.0.0-SNAPSHOT'

java {
    sourceCompatibility = JavaVersion.VERSION_21
}

repositories {
    mavenCentral()
}

dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-web'
    implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
    testImplementation 'org.springframework.boot:spring-boot-starter-test'
}
```

```bash
./gradlew bootRun          # run app
./gradlew clean build      # clean and build
./gradlew test             # run tests
./gradlew dependencies     # show dependency tree
```
