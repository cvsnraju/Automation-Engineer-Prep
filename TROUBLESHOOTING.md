# Jenkins Pipeline - Troubleshooting Guide

Complete troubleshooting guide for common Jenkins and pipeline issues.

## Table of Contents
1. [Installation Issues](#installation-issues)
2. [Configuration Issues](#configuration-issues)
3. [Build Failures](#build-failures)
4. [Test Failures](#test-failures)
5. [GitHub Integration Issues](#github-integration-issues)
6. [Performance Issues](#performance-issues)
7. [Notification Issues](#notification-issues)

---

## Installation Issues

### Issue: Jenkins Won't Start

**Symptoms:**
```
Jenkins is not accessible at http://localhost:8080
Service fails to start
```

**Solutions:**

#### Check if Jenkins process is running
```bash
ps aux | grep jenkins
# or
brew services list | grep jenkins
```

#### Check port conflict
```bash
# Is port 8080 in use?
lsof -i :8080

# Kill process on 8080
kill -9 <PID>

# Or use different port
java -jar jenkins.war --httpPort=8888
```

#### Check Java installation
```bash
java -version
# Should output: java version "11.0.x" or higher

# If not found:
brew install openjdk@11
```

#### Start Jenkins manually
```bash
# Homebrew
brew services start jenkins-lts

# Manual
cd /usr/local/Cellar/jenkins-lts/[VERSION]
java -jar jenkins.war --httpPort=8080

# Docker
docker-compose up -d
```

#### Check logs
```bash
# View logs
tail -f ~/.jenkins/log/jenkins.log

# Or
tail -f /var/log/jenkins/jenkins.log

# Docker
docker logs -f jenkins-automation
```

---

### Issue: "Permission denied" During Installation

**Symptoms:**
```
mkdir: /var/lib/jenkins: Permission denied
Cannot write to Jenkins home directory
```

**Solutions:**

```bash
# Fix Jenkins home permissions
sudo chown -R $USER:staff ~/.jenkins
chmod -R 755 ~/.jenkins

# If installed via Homebrew as service
sudo chown -R _jenkins:_jenkins /usr/local/opt/jenkins-lts
```

---

### Issue: Initial Admin Password Not Found

**Symptoms:**
```
No password file at ~/.jenkins/secrets/initialAdminPassword
Unlock Jenkins page won't accept password
```

**Solutions:**

```bash
# Check various password locations
cat ~/.jenkins/secrets/initialAdminPassword
cat /var/lib/jenkins/secrets/initialAdminPassword
docker exec jenkins-automation cat /var/jenkins_home/secrets/initialAdminPassword

# If still not found, reset Jenkins
# (Warning: This removes Jenkins state)
rm -rf ~/.jenkins
brew services restart jenkins-lts
```

---

## Configuration Issues

### Issue: Plugins Won't Install

**Symptoms:**
```
Plugin installation fails
"Failed to download plugin"
Timeout during plugin installation
```

**Solutions:**

#### Check internet connectivity
```bash
curl -I https://plugins.jenkins.io
```

#### Update Jenkins plugin repository
1. **Manage Jenkins** → **Configure System**
2. Scroll to **Update Site**
3. Change URL from `http://` to `https://`
4. Default: `https://updates.jenkins.io/update-center.json`

#### Install plugins via CLI
```bash
java -jar jenkins-cli.jar -s http://localhost:8080 \
  install-plugin workflow-aggregator \
  -restart
```

#### Increase timeout
1. **Manage Jenkins** → **Manage Plugins**
2. Increase: **Read timeout** and **Connection timeout**
3. Default: 60 seconds → try 120 seconds

---

### Issue: Java/Maven Not Found in Pipeline

**Symptoms:**
```
mvn: command not found
java: command not found
JAVA_HOME not set
```

**Solutions:**

#### Verify Java installation
```bash
java -version
echo $JAVA_HOME

# If not set:
export JAVA_HOME=$(/usr/libexec/java_home -v 11)
echo $JAVA_HOME
```

#### Configure in Jenkins
1. **Manage Jenkins** → **Configure System**
2. Find **JDK**
3. Add JDK:
   - **Name:** `Java11`
   - **JAVA_HOME:** `/Library/Java/JavaVirtualMachines/jdk-11.jdk/Contents/Home`
   - **Install automatically:** Unchecked

#### Add to Jenkinsfile
```groovy
pipeline {
    agent any
    
    environment {
        JAVA_HOME = "/Library/Java/JavaVirtualMachines/jdk-11.jdk/Contents/Home"
        MAVEN_HOME = "/usr/local/Cellar/maven/3.8.1"
        PATH = "${MAVEN_HOME}/bin:${JAVA_HOME}/bin:${PATH}"
    }
}
```

---

## Build Failures

### Issue: Build Fails with "Invalid POM"

**Symptoms:**
```
[FATAL] Non-parseable POM /path/to/pom.xml
```

**Solutions:**

```bash
# Validate POM locally
mvn validate

# Check XML syntax
xmllint pom.xml

# Fix common issues
# 1. Missing closing tags
# 2. Invalid characters
# 3. Incorrect indentation
```

---

### Issue: Maven Dependency Resolution Fails

**Symptoms:**
```
[FATAL] Failed to read artifact descriptor
Could not find artifact in repository
```

**Solutions:**

#### Clear Maven cache
```bash
rm -rf ~/.m2/repository
mvn clean install
```

#### Configure Maven settings
```bash
# Edit ~/.m2/settings.xml
<settings>
  <mirrors>
    <mirror>
      <id>central</id>
      <name>Maven Central</name>
      <url>https://repo.maven.apache.org/maven2</url>
      <mirrorOf>central</mirrorOf>
    </mirror>
  </mirrors>
</settings>
```

#### In Jenkinsfile
```groovy
stage('Build') {
    steps {
        sh 'mvn clean package -U'  // -U forces update
    }
}
```

---

### Issue: Build Timeout

**Symptoms:**
```
Build timed out after 1 hour
Process killed
```

**Solutions:**

#### Increase timeout in Jenkinsfile
```groovy
options {
    timeout(time: 2, unit: 'HOURS')  // Increase from 1 to 2 hours
}
```

#### Optimize build
```groovy
stage('Build') {
    steps {
        sh '''
            # Skip tests to speed up
            mvn clean package -DskipTests
            
            # Use parallel compilation
            mvn -T 1C clean package
            
            # Use incremental build
            mvn clean package pl /path/to/module
        '''
    }
}
```

---

### Issue: Out of Memory (OOM) Error

**Symptoms:**
```
java.lang.OutOfMemoryError: Java heap space
BUILD FAILURE
```

**Solutions:**

#### Increase Java heap size
```bash
# Jenkinsfile environment
environment {
    MAVEN_OPTS = "-Xmx2048m -Xms1024m"
}

# Or globally in ~/.mavenrc
export MAVEN_OPTS="-Xmx2048m -Xms1024m"
```

#### Jenkins system configuration
```groovy
// In system JVM options
-Xmx2048m -Xms1024m
```

#### Disable parallel tests
```groovy
stage('Test') {
    steps {
        sh 'mvn test -DthreadCount=1'
    }
}
```

---

## Test Failures

### Issue: Tests Fail Only in Jenkins (Pass Locally)

**Symptoms:**
```
Tests pass when run locally with: mvn test
Tests fail in Jenkins pipeline
```

**Solutions:**

#### Check Maven version
```bash
# Local
mvn -version

# In Jenkins
sh 'mvn -version'

# Version mismatch? Install same version
```

#### Check Java version
```bash
# Local
java -version

# In Jenkins
sh 'java -version'

# Install matching JDK in Jenkins
```

#### Check environment variables
```groovy
stage('Debug') {
    steps {
        sh 'env | sort'
        sh 'which java'
        sh 'which mvn'
    }
}
```

#### Check working directory
```groovy
stage('Test') {
    steps {
        sh '''
            pwd
            ls -la
            find . -name "*.class" -type f | head -5
        '''
    }
}
```

---

### Issue: Flaky Tests (Intermittent Failures)

**Symptoms:**
```
Same test passes sometimes, fails other times
Timing-dependent failures
```

**Solutions:**

#### Increase test timeout
```groovy
stage('Test') {
    steps {
        sh '''
            mvn test \
              -Dtestng.suiteXmlFiles=testng.xml \
              -DtestFailureIgnore=true \
              -DargLine="-Xmx1024m"
        '''
    }
}
```

#### Retry failing tests
```groovy
stage('Test') {
    steps {
        retry(3) {
            sh 'mvn test'
        }
    }
}
```

#### Run tests sequentially
```groovy
stage('Test') {
    steps {
        sh 'mvn test -DthreadCount=1 -DforkCount=1'
    }
}
```

---

### Issue: Test Reports Not Generated

**Symptoms:**
```
No test results in Jenkins
JUnit XML files not found
```

**Solutions:**

#### Verify test reports exist
```bash
find . -name "*surefire-reports*" -type d
find . -name "*.xml" -path "*/surefire-reports/*"
```

#### Update post-build action
```groovy
post {
    always {
        junit testResults: '**/target/surefire-reports/*.xml', 
              allowEmptyResults: true
    }
}
```

#### Generate reports explicitly
```groovy
stage('Test') {
    steps {
        sh '''
            mvn surefire:test
            mvn surefire-report:report
        '''
    }
}
```

---

## GitHub Integration Issues

### Issue: Webhook Not Triggering Build

**Symptoms:**
```
Push to GitHub, Jenkins doesn't build
Webhook shows failed delivery
```

**Solutions:**

#### Verify webhook setup
1. GitHub → **Repo** → **Settings** → **Webhooks**
2. Click webhook to see recent deliveries
3. Check HTTP status code:
   - **200:** Success
   - **404:** URL not found
   - **403:** Forbidden/Authentication

#### Fix Jenkins URL accessibility
```bash
# From GitHub server, can you reach Jenkins?
curl http://your-jenkins-domain:8080/github-webhook/

# For local development, use ngrok
ngrok http 8080
# Copy ngrok URL and update GitHub webhook
```

#### Enable webhook trigger in job
1. Job → **Configure**
2. **Build Triggers**
3. Check: **GitHub hook trigger for GITScm polling**
4. Save

#### Test webhook manually
```bash
# Simulate GitHub webhook
curl -X POST \
  -H "Content-Type: application/json" \
  -d '{"action":"opened","number":1}' \
  http://localhost:8080/github-webhook/
```

---

### Issue: GitHub Credentials Authentication Failed

**Symptoms:**
```
fatal: Authentication failed
Repository not accessible
403: Forbidden
```

**Solutions:**

#### Verify credentials in Jenkins
1. **Manage Jenkins** → **Manage Credentials**
2. Check: Credentials exist and are valid
3. Test connection by re-saving

#### Verify GitHub PAT
```bash
# Test GitHub token
curl -H "Authorization: token YOUR_TOKEN" \
  https://api.github.com/user

# Should return your GitHub user info
```

#### Update Jenkinsfile with correct credentials
```groovy
checkout([
    $class: 'GitSCM',
    branches: [[name: '*/main']],
    userRemoteConfigs: [[
        url: 'https://github.com/YOUR_USERNAME/YOUR_REPO.git',
        credentialsId: 'github-credentials'  // Verify this ID exists
    ]]
])
```

---

### Issue: Cannot Access GitHub from Jenkins

**Symptoms:**
```
github.com: nodename nor servname provided
Network is unreachable
```

**Solutions:**

#### Check network connectivity
```bash
# From Jenkins machine
ping github.com
curl https://api.github.com

# From Jenkins job
sh 'ping -c 1 github.com'
sh 'curl -I https://api.github.com'
```

#### Configure proxy (if behind firewall)
1. **Manage Jenkins** → **Configure System**
2. Find **HTTP Proxy Configuration**
3. Enter proxy details:
   - Proxy host
   - Proxy port
   - Username/password if required

---

## Performance Issues

### Issue: Pipeline Very Slow

**Symptoms:**
```
Build takes 30+ minutes
Lots of idle time
```

**Solutions:**

#### Enable parallel execution
```groovy
stage('Parallel Tests') {
    parallel {
        stage('Unit Tests') {
            steps {
                sh 'mvn test -P unit'
            }
        }
        stage('Integration Tests') {
            steps {
                sh 'mvn test -P integration'
            }
        }
    }
}
```

#### Cache Maven dependencies
```groovy
stage('Build') {
    steps {
        sh 'mvn clean install -o'  // Use offline mode
    }
}
```

#### Incremental build
```groovy
stage('Build') {
    steps {
        sh '''
            # Build only changed modules
            mvn -am -pl module1,module2 clean package
        '''
    }
}
```

#### Skip unnecessary steps
```groovy
stage('Tests') {
    when {
        expression { params.RUN_TESTS }
    }
    steps {
        sh 'mvn test'
    }
}
```

---

## Notification Issues

### Issue: Email Notifications Not Sending

**Symptoms:**
```
No emails received
Post-build action shows error
```

**Solutions:**

#### Verify SMTP configuration
1. **Manage Jenkins** → **Configure System**
2. **Email Notification:**
   - SMTP server: `smtp.gmail.com`
   - SMTP port: `587`
   - TLS: Enabled
   - From: `your-email@gmail.com`

#### Test email configuration
1. Click **Test configuration**
2. Should send test email
3. Check spam folder

#### Gmail-specific
```
# Gmail requires App Password (not regular password)
1. Enable 2FA on Gmail
2. Generate App Password: https://myaccount.google.com/apppasswords
3. Use App Password in Jenkins configuration
```

#### Use Extended Email Plugin
```groovy
post {
    failure {
        emailext(
            subject: "Build Failed: ${env.JOB_NAME}",
            body: "See ${env.BUILD_URL} for details",
            to: "team@example.com",
            mimeType: 'text/html',
            attachLog: true
        )
    }
}
```

---

### Issue: Slack Notifications Not Sending

**Symptoms:**
```
No messages in Slack channel
Webhook error in logs
```

**Solutions:**

#### Verify Slack integration
1. **Manage Jenkins** → **Configure System**
2. **Slack** section:
   - Workspace: Your workspace name
   - Channel: #channel-name
   - Webhook URL: Verify from Slack

#### Get Slack Webhook URL
1. Slack Workspace → **Settings** → **Apps**
2. Search: **Jenkins CI**
3. **Configuration** → **Incoming Webhooks**
4. Copy URL

#### Test Slack integration
```bash
# Manual test
curl -X POST -H 'Content-type: application/json' \
  --data '{"text":"Test message"}' \
  YOUR_SLACK_WEBHOOK_URL
```

#### Update Jenkinsfile
```groovy
post {
    always {
        slackSend(
            color: currentBuild.result == 'SUCCESS' ? 'good' : 'danger',
            message: "Build ${env.BUILD_NUMBER}: ${currentBuild.result}",
            channel: '#automation-ci'
        )
    }
}
```

---

## Getting Help

### Debug Mode
```bash
# Enable Jenkins debug logging
# Manage Jenkins → Configure System → Logger
# Add logger for package you want to debug
# E.g., org.jenkinsci.plugins.github
# Set to DEBUG level
```

### Check Logs
```bash
# Main Jenkins log
tail -f ~/.jenkins/log/jenkins.log

# Build-specific log
Jenkins → Job → Build → Console Output

# Docker
docker logs -f jenkins-automation
```

### Common Log Locations
```
~/.jenkins/log/jenkins.log              # Main log
~/.jenkins/jobs/JobName/builds/1/       # Build artifacts
~/.jenkins/logs/                         # All logs
/var/log/jenkins/jenkins.log             # System Jenkins
```

### Jenkins CLI Help
```bash
java -jar jenkins-cli.jar -s http://localhost:8080 help

# List available commands
java -jar jenkins-cli.jar -s http://localhost:8080 help | grep -i build
```

---

## Contact & Resources

- **Jenkins Issues:** https://issues.jenkins.io/
- **Stack Overflow:** Tag `jenkins`
- **Jenkins Chat:** https://jenkins.io/chat/
- **Local:** Check `~/.jenkins/log/jenkins.log`

---

**Still stuck? Check JENKINS_SETUP.md or JENKINS_QUICKSTART.md for additional guidance!**
