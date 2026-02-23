# DevOps Lifecycle Phases with Jenkins Case Study

## DevOps Lifecycle Overview

DevOps lifecycle consists of 8 phases working in a continuous cycle:

```
Plan → Code → Build → Test → Release → Deploy → Operate → Monitor → (Back to Plan)
```

---

## Phase-by-Phase Breakdown with Jenkins

### 1️⃣ **PLAN**
Plan and prioritize features, infrastructure needs, and testing strategy.

**Jenkins Role:** N/A (External tools: Jira, Confluence)

**Case Study Example:**
- Team decides to automate SRK website testing
- Creates test plan for UI, API, and performance testing
- Identifies testing environments (dev, staging, prod)

---

### 2️⃣ **CODE**
Developers write and commit code to version control system.

**Jenkins Role:** Listens to SCM changes via webhooks

**Case Study Example:**
```
Developer commits new test automation code:
├─ git commit -m "Add login page UI tests"
├─ git push origin feature/login-tests
└─ GitHub webhook triggers Jenkins
```

**Jenkinsfile Stage:**
```groovy
stage('Checkout Code') {
    steps {
        checkout scm
        sh 'git log --oneline -5'
    }
}
```

---

### 3️⃣ **BUILD**
Compile code, resolve dependencies, create artifacts.

**Jenkins Role:** Execute Maven/Gradle builds

**Case Study Example:**
```
Jenkins builds automation test project:
├─ Clean previous build
├─ Resolve Maven dependencies
├─ Compile Java test code
├─ Package into JAR/WAR
└─ Store artifacts
```

**Jenkinsfile Stage:**
```groovy
stage('Build Projects') {
    steps {
        sh 'mvn clean package -DskipTests'
    }
}
```

**Output:** Test JAR ready for execution

---

### 4️⃣ **TEST**
Execute automated tests: Unit, Integration, Performance, Security.

**Jenkins Role:** Run test suites, collect results, generate reports

**Case Study Example:**
```
Parallel Test Execution:
├─ Unit Tests (TestNG) - 2 min
│  └─ Validates individual components
├─ Integration Tests - 5 min
│  └─ Tests with Mock API Server
└─ Performance Tests - 3 min
   └─ Load testing with 100 concurrent users
```

**Jenkinsfile Stages:**
```groovy
stage('Unit Tests') {
    steps {
        sh 'mvn test'
    }
}

stage('Integration Tests') {
    parallel {
        stage('API Tests') {
            steps {
                sh 'mvn test -P api-tests'
            }
        }
        stage('UI Tests') {
            steps {
                sh 'mvn test -P ui-tests'
            }
        }
    }
}
```

**Output:** Test Report (TXT, XML, HTML)
```
Tests run: 150
Passed: 148
Failed: 2 (logged as known issue)
Skipped: 0
Pass Rate: 98.67%
```

---

### 5️⃣ **RELEASE**
Prepare and package code for production deployment.

**Jenkins Role:** Create release artifacts, version management, sign binaries

**Case Study Example:**
```
Jenkins Release Process:
├─ Version bump: 1.0.0 → 1.1.0
├─ Create release branch
├─ Sign artifacts with GPG key
├─ Generate release notes
└─ Upload to artifact repository (Nexus/Artifactory)
```

**Jenkinsfile Stage:**
```groovy
stage('Artifact Archival') {
    steps {
        sh '''
            mkdir -p build-artifacts/${BUILD_TIMESTAMP}
            cp target/*.jar build-artifacts/${BUILD_TIMESTAMP}/
        '''
        archiveArtifacts artifacts: '**/build-artifacts/**'
    }
}
```

---

### 6️⃣ **DEPLOY**
Automatically deploy to target environments.

**Jenkins Role:** Execute deployment scripts, manage configurations

**Case Study Example:**
```
Multi-Environment Deployment:
├─ Dev Environment (Auto-deploy on build success)
│  └─ Deploy immediately
├─ Staging Environment (Auto-deploy)
│  └─ Run smoke tests
└─ Production Environment (Manual approval required)
   └─ Get manager approval → Deploy → Monitor
```

**Jenkinsfile Stage:**
```groovy
stage('Deployment') {
    when {
        expression { currentBuild.result == 'SUCCESS' }
    }
    steps {
        script {
            if (params.TEST_ENVIRONMENT == 'prod') {
                input 'Approve production deployment?'
            }
        }
        sh './deploy.sh ${TEST_ENVIRONMENT}'
    }
}
```

**Deployment Output:**
```
[INFO] Deploying to staging...
[INFO] Copying artifacts to /opt/staging/
[INFO] Starting application...
[INFO] Deployment completed in 45 seconds
```

---

### 7️⃣ **OPERATE**
Manage and maintain applications in production.

**Jenkins Role:** Execute operational tasks, manage infrastructure

**Case Study Example:**
```
Operational Tasks:
├─ Health checks (every 5 min)
├─ Backup operations (daily)
├─ Configuration updates
├─ Scaling based on load
└─ Incident response automation
```

**Jenkins Scheduled Job:**
```groovy
triggers {
    cron('H/5 * * * *')  // Health check every 5 minutes
}

stage('Health Check') {
    steps {
        sh '''
            curl -f http://localhost:8080/health || \
            (echo "Application down" && exit 1)
        '''
    }
}
```

---

### 8️⃣ **MONITOR**
Collect metrics, logs, and alerts for continuous improvement.

**Jenkins Role:** Collect test metrics, build metrics, trigger alerts

**Case Study Example:**
```
Monitoring Metrics Collected:
├─ Build Metrics
│  ├─ Build duration: 12 minutes
│  ├─ Success rate: 98.5%
│  └─ Build frequency: 45 builds/day
├─ Test Metrics
│  ├─ Pass rate: 98.67%
│  ├─ Failure trend: ↓ (improving)
│  └─ Flaky tests: 2
├─ Deployment Metrics
│  ├─ Deployment frequency: 8/day
│  ├─ Lead time: 4 hours
│  └─ MTTR: 15 minutes
└─ Performance Metrics
   ├─ App response time: 250ms
   ├─ CPU usage: 45%
   └─ Memory usage: 60%
```

**Jenkinsfile Post-Build:**
```groovy
post {
    always {
        junit testResults: '**/target/surefire-reports/*.xml'
        publishHTML([
            reportDir: 'target/site/jacoco',
            reportFiles: 'index.html',
            reportName: 'Code Coverage'
        ])
        
        slackSend(
            color: currentBuild.result == 'SUCCESS' ? 'good' : 'danger',
            message: """
            Build #${BUILD_NUMBER}: ${currentBuild.result}
            Duration: ${currentBuild.durationString}
            Tests: 150 passed, 2 failed (98.67% pass rate)
            Coverage: 82%
            """
        )
    }
}
```

---

## Real-World Case Study: SRK Website Testing Pipeline

### Scenario
Automation Engineer Prep project needed to automate testing for SRK website with multi-environment support.

### Implementation Timeline

**Week 1: Plan & Code**
- Identified testing requirements
- Set up Selenium test framework
- Created TestNG test classes
- Committed to GitHub main branch

**Week 2: Build & Test**
```
Jenkins Pipeline Execution:
├─ Build: 2 min (Maven compile)
├─ Unit Tests: 3 min (45 tests)
├─ Integration Tests: 8 min (parallel execution)
│  ├─ API Tests: 5 min (25 tests)
│  └─ UI Tests: 5 min (30 tests)
└─ Code Quality: 2 min (SonarQube scan)
   Total: 15 minutes ⏱️
```

**Test Results:**
```
Total Tests: 100
✅ Passed: 99
❌ Failed: 1 (known issue on staging)
⏭️ Skipped: 0
Pass Rate: 99%
```

**Week 3: Release & Deploy**
```
Multi-Environment Deployment:
Dev         → Auto-deploy (immediate)
Staging     → Auto-deploy + smoke tests
Production  → Manual approval + smoke tests
```

**Week 4: Operate & Monitor**
```
Metrics Dashboard:
├─ Build Success Rate: 98.5%
├─ Test Pass Rate: 99%
├─ Deployment Frequency: 10/day
├─ Lead Time: 3 hours
└─ MTTR: 10 minutes
```

---

## Jenkins Pipeline Overview

```groovy
pipeline {
    agent any
    
    // PLAN Phase: Define parameters
    parameters {
        choice(name: 'TEST_ENVIRONMENT', 
               choices: ['dev', 'staging', 'prod'])
    }
    
    stages {
        // CODE Phase: Checkout
        stage('Checkout') {
            steps { checkout scm }
        }
        
        // BUILD Phase: Compile
        stage('Build') {
            steps { sh 'mvn clean package -DskipTests' }
        }
        
        // TEST Phase: Execute tests
        stage('Tests') {
            parallel {
                stage('Unit Tests') {
                    steps { sh 'mvn test' }
                }
                stage('Integration Tests') {
                    steps { sh 'mvn test -P integration' }
                }
            }
        }
        
        // RELEASE Phase: Archive
        stage('Archive') {
            steps { 
                archiveArtifacts artifacts: '**/target/*.jar'
            }
        }
        
        // DEPLOY Phase: Deploy
        stage('Deploy') {
            steps { 
                sh './deploy.sh ${TEST_ENVIRONMENT}'
            }
        }
        
        // OPERATE Phase: Health check
        stage('Health Check') {
            steps { 
                sh 'curl -f http://localhost:8080/health'
            }
        }
    }
    
    // MONITOR Phase: Collect metrics
    post {
        always {
            junit testResults: '**/surefire-reports/*.xml'
            slackSend(message: "Build ${BUILD_NUMBER}: ${currentBuild.result}")
        }
    }
}
```

---

## Key Benefits of This Pipeline

| Benefit | Impact | Metric |
|---------|--------|--------|
| **Automation** | Reduces manual effort | 50+ manual hours → 0 |
| **Speed** | Faster feedback loop | 15 min per build |
| **Quality** | Higher test coverage | 99% pass rate |
| **Reliability** | Consistent deployments | 98.5% success rate |
| **Visibility** | Real-time metrics | 10+ dashboards |
| **Risk Reduction** | Early issue detection | Bugs caught in minutes |

---

## DevOps Metrics Dashboard

```
┌─────────────────────────────────────┐
│ DEVOPS PIPELINE METRICS             │
├─────────────────────────────────────┤
│ Build Frequency     : 45/day        │
│ Deployment Success  : 98.5%         │
│ Lead Time          : 3 hours        │
│ MTTR (Mean Time)   : 10 minutes     │
│ Test Coverage      : 82%            │
│ Pass Rate          : 99%            │
│ Uptime             : 99.9%          │
│ Incident Response  : < 5 min        │
└─────────────────────────────────────┘
```

---

## Tools Used at Each Phase

| Phase | Tools |
|-------|-------|
| Plan | Jira, Confluence |
| Code | Git, GitHub |
| Build | Jenkins, Maven, Gradle |
| Test | TestNG, JUnit, Selenium |
| Release | Jenkins, Artifactory, Nexus |
| Deploy | Jenkins, Docker, Kubernetes |
| Operate | Kubernetes, ELK, Datadog |
| Monitor | Prometheus, Grafana, Jenkins |

---

## Continuous Improvement Cycle

```
Monitor Metrics
    ↓
Identify Issues
    ↓
Plan Improvements
    ↓
Implement Changes
    ↓
Test Changes
    ↓
Deploy Improvements
    ↓
(Back to Monitor)
```

---

## Conclusion

Jenkins serves as the **orchestrator** of the entire DevOps lifecycle, enabling:
- ✅ Automated code quality checks
- ✅ Continuous integration & testing
- ✅ Automated deployment pipelines
- ✅ Metrics collection & monitoring
- ✅ Rapid feedback loops
- ✅ Risk reduction through automation

The SRK Website Testing Pipeline demonstrates how Jenkins integrates all 8 phases into a seamless, automated workflow.

---

*See [Jenkinsfile](./Jenkinsfile) for complete production pipeline implementation.*
