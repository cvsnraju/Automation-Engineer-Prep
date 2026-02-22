# Jenkins Pipeline Setup Guide - End to End

Complete guide to set up Jenkins and execute the end-to-end pipeline for the Automation Engineer Prep project.

## Table of Contents
1. [Jenkins Installation](#jenkins-installation)
2. [Initial Setup & Configuration](#initial-setup--configuration)
3. [Plugin Installation](#plugin-installation)
4. [GitHub Integration](#github-integration)
5. [Creating the Pipeline Job](#creating-the-pipeline-job)
6. [Webhook Configuration](#webhook-configuration)
7. [Running the Pipeline](#running-the-pipeline)
8. [Monitoring & Troubleshooting](#monitoring--troubleshooting)

---

## Jenkins Installation

### macOS Installation

#### Using Homebrew (Recommended)
```bash
# Install Jenkins via Homebrew
brew install jenkins-lts

# Start Jenkins service
brew services start jenkins-lts

# Check Jenkins status
brew services list | grep jenkins
```

#### Manual Installation
```bash
# Download Jenkins WAR
wget https://mirrors.jenkins.io/war-stable/latest/jenkins.war

# Run Jenkins
java -jar jenkins.war --httpPort=8080
```

#### Docker Installation (Alternative)
```bash
# Pull Jenkins Docker image
docker pull jenkins/jenkins:lts

# Run Jenkins container
docker run -d \
  -p 8080:8080 \
  -p 50000:50000 \
  -v jenkins_home:/var/jenkins_home \
  -v /var/run/docker.sock:/var/run/docker.sock \
  --name jenkins \
  jenkins/jenkins:lts

# Check logs
docker logs -f jenkins
```

### Verify Installation
```bash
# Jenkins should be accessible at http://localhost:8080
# Check logs
tail -f /var/log/jenkins/jenkins.log  # macOS with Homebrew

# Unlock Jenkins (initial setup)
cat ~/.jenkins/secrets/initialAdminPassword
```

---

## Initial Setup & Configuration

### 1. Access Jenkins Web Interface
- Open browser: `http://localhost:8080`
- You'll see "Unlock Jenkins" page
- Copy initial admin password from the file path shown

### 2. Initial Admin Password
```bash
# macOS with Homebrew
cat ~/.jenkins/secrets/initialAdminPassword

# Docker
docker exec jenkins cat /var/jenkins_home/secrets/initialAdminPassword

# Manual installation
cat /tmp/jenkins-*/secret/initialAdminPassword
```

### 3. Complete Setup Wizard
1. Paste admin password
2. Click "Install suggested plugins" (or select plugins manually)
3. Create first admin user
4. Configure Jenkins URL: `http://localhost:8080`

### 4. Configure Java & Maven (macOS)

#### Edit Jenkins Configuration
Go to **Manage Jenkins** → **Configure System**

**Add JDK:**
- **Name:** `Java11`
- **JAVA_HOME:** `/Library/Java/JavaVirtualMachines/jdk-11.jdk/Contents/Home`

**Add Maven:**
- **Name:** `Maven3.8`
- **MAVEN_HOME:** `/usr/local/Cellar/maven/3.8.1`

```bash
# Verify installations
java -version
mvn -version
```

---

## Plugin Installation

Required plugins for this pipeline:

### Install via Dashboard

1. Go to **Manage Jenkins** → **Manage Plugins**
2. Click **Available plugins**
3. Search and install each plugin:

#### Essential Plugins
- **Pipeline** (Declarative & Scripted)
- **Git** (Git client & Git SCM)
- **GitHub** (GitHub API & GitHub Branch Source)
- **GitLab** (if using GitLab)
- **Email Extension** (Email notifications)
- **JUnit Plugin** (Test results)
- **Cobertura** (Code coverage)
- **SonarQube Scanner** (Code quality)

#### Testing Plugins
- **Test Results Analyzer**
- **Log Parser**
- **Timestamper**

#### Notification Plugins
- **Slack Notification**
- **Microsoft Teams Notification**

#### Script Installation
```bash
# Create plugins.txt
cat > plugins.txt << 'EOF'
workflow-aggregator
git
github
email-ext
junit
cobertura
sonarqube-generic-coverage
timestamper
log-parser
EOF

# Install plugins (via Jenkins CLI)
java -jar jenkins-cli.jar -s http://localhost:8080 \
  install-plugin $(cat plugins.txt | tr '\n' ' ')
```

---

## GitHub Integration

### 1. Create GitHub Personal Access Token

1. Go to GitHub → **Settings** → **Developer settings** → **Personal access tokens**
2. Click **Generate new token**
3. **Token name:** `Jenkins-Automation`
4. **Scopes:**
   - ✓ repo (full control)
   - ✓ admin:org_hook
   - ✓ admin:repo_hook
5. Copy token (you won't see it again)

### 2. Add GitHub Credentials to Jenkins

1. Go to **Manage Jenkins** → **Manage Credentials**
2. Click **Jenkins** → **Global credentials (unrestricted)**
3. Click **Add Credentials**
   - **Kind:** Username with password
   - **Username:** `your-github-username`
   - **Password:** `<paste-your-personal-access-token>`
   - **ID:** `github-credentials`
   - **Description:** `GitHub Automation Credentials`

### 3. Configure GitHub Server

1. Go to **Manage Jenkins** → **Configure System**
2. Find **GitHub** section
3. Click **Add GitHub Server**
   - **Name:** `GitHub`
   - **API URL:** `https://api.github.com`
   - **Credentials:** Select `github-credentials`
4. Click **Test connection**

---

## Creating the Pipeline Job

### Option 1: Create Multibranch Pipeline (Recommended)

1. Click **New Item**
2. **Item name:** `Automation-Engineer-Prep`
3. **Type:** Select **Multibranch Pipeline**
4. Click **OK**

#### Configure Branch Sources
1. Click **Add source** → **Git**
   - **Project Repository:** `https://github.com/YOUR_USERNAME/Automation-Engineer-Prep.git`
   - **Credentials:** Select `github-credentials`

2. Click **Add source** → **GitHub**
   - **Credentials:** Select `github-credentials`
   - **Owner:** `YOUR_USERNAME`
   - **Repository:** `Automation-Engineer-Prep`

#### Configure Scan Triggers
1. **Periodically if not otherwise run:** Check and set `5 minutes`
2. **Build pull requests:** Check
3. **Build origin branches:** Check

#### Path Filters
- **Include branches:** `main|develop`
- **Exclude branches:** `temp-.*`

### Option 2: Create Declarative Pipeline Job

1. Click **New Item**
2. **Item name:** `Automation-Engineer-Prep-Pipeline`
3. **Type:** Select **Pipeline**
4. Click **OK**

#### Configure Pipeline

Under **Pipeline** section:
- **Definition:** Pipeline script from SCM
- **SCM:** Git
  - **Repository URL:** `https://github.com/YOUR_USERNAME/Automation-Engineer-Prep.git`
  - **Credentials:** `github-credentials`
  - **Branch Specifier:** `*/main`
  - **Script Path:** `Jenkinsfile`

Click **Save**

---

## Webhook Configuration

### Step 1: Get Jenkins Webhook URL

```
http://your-jenkins-domain:8080/github-webhook/
```

**For local development (ngrok):**
```bash
# Install ngrok
brew install ngrok

# Create ngrok account and add token
ngrok authtoken YOUR_NGROK_TOKEN

# Expose Jenkins to internet
ngrok http 8080

# Your public URL: https://xxxxx-xxxxx.ngrok.io/github-webhook/
```

### Step 2: Configure Webhook in GitHub

1. Go to GitHub repo → **Settings** → **Webhooks**
2. Click **Add webhook**
   - **Payload URL:** `http://your-jenkins-domain:8080/github-webhook/`
   - **Content type:** `application/json`
   - **Events:** 
     - ✓ Push events
     - ✓ Pull requests
     - ✓ Releases
   - ✓ Active
3. Click **Add webhook**

### Step 3: Verify Webhook

1. In GitHub webhook settings, scroll down to **Recent Deliveries**
2. Click on a delivery to see request/response
3. Should see green checkmark (✓ 200 OK)

### Step 4: Enable Webhook Trigger in Jenkins Job

For **Multibranch Pipeline:**
- Automatically enabled for GitHub branches

For **Declarative Pipeline:**
1. Edit job → **Build Triggers**
2. Check **GitHub hook trigger for GITScm polling**
3. Save

---

## Running the Pipeline

### Method 1: Manual Trigger

1. Go to Jenkins job dashboard
2. Click **Build with Parameters** (if configured)
3. Select:
   - **TEST_ENVIRONMENT:** dev/staging/prod
   - **RUN_UNIT_TESTS:** ✓
   - **RUN_INTEGRATION_TESTS:** ✓
4. Click **Build**

### Method 2: Git Commit Trigger

Simply push to your repository:
```bash
git add .
git commit -m "Trigger Jenkins pipeline"
git push origin main
```

Jenkins will automatically trigger via webhook.

### Method 3: Jenkins CLI

```bash
# Download Jenkins CLI
wget http://localhost:8080/jnlpJars/jenkins-cli.jar

# Trigger build
java -jar jenkins-cli.jar -s http://localhost:8080 \
  build "Automation-Engineer-Prep" -w

# With parameters
java -jar jenkins-cli.jar -s http://localhost:8080 \
  build "Automation-Engineer-Prep" \
  -p TEST_ENVIRONMENT=staging \
  -w
```

---

## Monitoring & Troubleshooting

### View Build Logs

1. Go to job → **Build History**
2. Click build number (e.g., `#5`)
3. Click **Console Output**

### Live Log Streaming
```bash
# SSH to Jenkins server and tail logs
tail -f /var/log/jenkins/jenkins.log
```

### Common Issues & Solutions

#### Issue 1: "Permission denied" on Git operations
**Solution:**
```bash
# Ensure Jenkins user has permission
sudo chown -R jenkins:jenkins ~/.jenkins
chmod -R 755 ~/.jenkins
```

#### Issue 2: Maven build fails - "Command not found"
**Solution:**
- Go to **Manage Jenkins** → **Configure System**
- Verify MAVEN_HOME path
- Add Maven to Jenkins environment variables

#### Issue 3: Tests timeout or hang
**Solution:** In Jenkinsfile, adjust timeout:
```groovy
options {
    timeout(time: 2, unit: 'HOURS')
}
```

#### Issue 4: Webhook not triggering
**Solution:**
```bash
# Check GitHub webhook delivery
# Go to repo → Settings → Webhooks → Recent Deliveries
# If 404: Verify /github-webhook/ endpoint
# If 403: Check Jenkins authentication

# Test from command line
curl -X POST http://localhost:8080/github-webhook/ \
  -H "Content-Type: application/json" \
  -d '{"action":"opened"}'
```

#### Issue 5: "JAVA_HOME not set"
**Solution:**
```bash
# Add to Jenkinsfile environment block
environment {
    JAVA_HOME = "/Library/Java/JavaVirtualMachines/jdk-11.jdk/Contents/Home"
    PATH = "${JAVA_HOME}/bin:${PATH}"
}
```

### Monitor Pipeline Execution

**Real-time Monitoring:**
1. Click build number → **Console Output**
2. See stage-by-stage execution

**Pipeline View:**
1. Install **Pipeline Stage View** plugin
2. Click **Stage View** tab on build page

**Blue Ocean Interface (Modern UI):**
1. Install **Blue Ocean** plugin
2. Access at `http://localhost:8080/blue`

---

## Advanced Configuration

### Email Notifications

1. Go to **Manage Jenkins** → **Configure System** → **Email Notification**
2. **SMTP server:** `smtp.gmail.com`
3. **SMTP Port:** `587`
4. **Use TLS:** ✓

**In Jenkinsfile:**
```groovy
post {
    success {
        emailext(
            subject: "Build Success: ${env.JOB_NAME}",
            body: "Build passed. Details: ${env.BUILD_URL}",
            to: "your-email@example.com"
        )
    }
    failure {
        emailext(
            subject: "Build Failed: ${env.JOB_NAME}",
            body: "Build failed. Check logs at: ${env.BUILD_URL}",
            to: "your-email@example.com"
        )
    }
}
```

### Slack Notifications

1. Install **Slack Notification** plugin
2. Go to **Manage Jenkins** → **Configure System** → **Slack**
3. Add Slack workspace and token

**In Jenkinsfile:**
```groovy
post {
    always {
        slackSend(
            channel: '#automation-tests',
            color: currentBuild.result == 'SUCCESS' ? 'good' : 'danger',
            message: "Build ${env.BUILD_NUMBER}: ${currentBuild.result}"
        )
    }
}
```

### SonarQube Integration

1. Install **SonarQube Scanner** plugin
2. Configure in **Manage Jenkins** → **Configure System**

**In Jenkinsfile:**
```groovy
stage('Code Analysis') {
    steps {
        withSonarQubeEnv('SonarQube') {
            sh 'mvn clean package sonar:sonar'
        }
    }
}
```

---

## Security Best Practices

1. **Credentials Management**
   - Always use Jenkins Credentials Store
   - Never hardcode secrets in Jenkinsfile

2. **RBAC (Role-Based Access Control)**
   - Install **Role-based Authorization Strategy** plugin
   - Define roles: Admin, Developers, Viewers

3. **Secure Jenkins URL**
   - Use HTTPS with valid certificate
   - Enable CSRF protection

4. **Audit Logging**
   - Monitor build logs
   - Archive logs for compliance

---

## Next Steps

1. ✓ Update `YOUR_USERNAME` and `YOUR_GITHUB_USERNAME` in Jenkinsfile
2. ✓ Adjust email and Slack channels for your team
3. ✓ Configure environment-specific deployment scripts
4. ✓ Add code quality gates
5. ✓ Set up database migration scripts
6. ✓ Configure artifact repository (Artifactory, Nexus)

---

## Quick Reference - Commands

```bash
# Start/Stop Jenkins (Homebrew)
brew services start jenkins-lts
brew services stop jenkins-lts
brew services restart jenkins-lts

# Check Jenkins status
curl -I http://localhost:8080

# View Jenkins logs
tail -f ~/.jenkins/log/jenkins.log

# Restart Jenkins (via web)
curl -X POST http://localhost:8080/restart

# List all jobs (via CLI)
java -jar jenkins-cli.jar -s http://localhost:8080 list-jobs

# Trigger build (via CLI)
java -jar jenkins-cli.jar -s http://localhost:8080 build JOB_NAME -w

# View credentials
java -jar jenkins-cli.jar -s http://localhost:8080 list-credentials-as-xml
```

---

## Support & Resources

- **Jenkins Documentation:** https://www.jenkins.io/doc/
- **Pipeline Syntax:** https://www.jenkins.io/doc/book/pipeline/
- **GitHub Plugin:** https://plugins.jenkins.io/github/
- **Blue Ocean UI:** https://www.jenkins.io/doc/book/blueocean/
