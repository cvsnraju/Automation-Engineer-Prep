# Jenkins Pipeline - Quick Start Guide

Get a Jenkins pipeline up and running in 5 minutes!

## Quick Start (macOS)

### 1. Install Jenkins & Dependencies
```bash
# Make script executable
chmod +x setup-jenkins.sh

# Run setup script
./setup-jenkins.sh

# Output will show:
# - Initial Admin Password
# - Jenkins URL
# - Next steps
```

### 2. Open Jenkins
```
http://localhost:8080
```

### 3. Complete Web Setup (5 minutes)
1. Paste admin password from script output
2. Click **Install suggested plugins**
3. Create admin user
4. Set Jenkins URL to `http://localhost:8080`

### 4. Configure GitHub Connection

#### Create GitHub Token
1. Go to GitHub → **Settings** → **Developer settings** → **Personal access tokens**
2. Generate new token with scopes: `repo`, `admin:org_hook`, `admin:repo_hook`
3. Copy token

#### Add to Jenkins
1. Go to Jenkins → **Manage Jenkins** → **Manage Credentials**
2. Click **Jenkins** → **Global credentials**
3. **Add Credentials:**
   - Kind: `Username with password`
   - Username: `your-github-username`
   - Password: `<paste-token>`
   - ID: `github-credentials`

### 5. Create Pipeline Job

#### Option A: Simple (Declarative Pipeline)
1. Click **New Item**
2. Name: `Automation-Tests`
3. Type: **Pipeline**
4. In **Pipeline** section:
   - Definition: `Pipeline script from SCM`
   - SCM: `Git`
   - Repository URL: `https://github.com/YOUR_USERNAME/Automation-Engineer-Prep.git`
   - Credentials: `github-credentials`
   - Script Path: `Jenkinsfile`
5. Click **Save**

#### Option B: Advanced (Multibranch)
1. Click **New Item**
2. Name: `Automation-Tests-Multi`
3. Type: **Multibranch Pipeline**
4. Add source: **GitHub**
   - Credentials: `github-credentials`
   - Owner: `YOUR_USERNAME`
   - Repository: `Automation-Engineer-Prep`
5. Click **Save**

### 6. Run Pipeline
```bash
# Option 1: Manual trigger
# Go to job → Click "Build"

# Option 2: Git push trigger (after webhook setup)
git add .
git commit -m "Trigger build"
git push origin main
```

---

## Setup with Docker (Alternative)

### Prerequisites
- Docker installed
- Docker Compose installed

### Run
```bash
# Start Jenkins + Mock API
docker-compose up -d

# View logs
docker-compose logs -f jenkins

# Jenkins URL
http://localhost:8080

# Get initial password
docker exec jenkins-automation cat /var/jenkins_home/secrets/initialAdminPassword
```

### Stop
```bash
docker-compose down
```

---

## Verify Setup

### Check Jenkins Status
```bash
# Is Jenkins running?
curl http://localhost:8080

# Check Java
java -version

# Check Maven
mvn -version

# View available jobs
java -jar jenkins-cli.jar -s http://localhost:8080 list-jobs
```

### Trigger Test Build
```bash
# Manual trigger via CLI
java -jar jenkins-cli.jar -s http://localhost:8080 \
  build "Automation-Tests" -w

# Watch console
# Jenkins dashboard → Job → Latest Build → Console Output
```

---

## Common Commands

```bash
# Stop/Start Jenkins
brew services stop jenkins-lts
brew services start jenkins-lts

# View logs
tail -f ~/.jenkins/log/jenkins.log

# Restart Jenkins
curl -X POST http://localhost:8080/restart

# Check plugins installed
curl http://localhost:8080/pluginManager/api/json?tree=plugins[shortName,version,active]

# Reload configuration
java -jar jenkins-cli.jar -s http://localhost:8080 reload-configuration
```

---

## Setup Webhook (Optional - for automatic triggers)

### For Local Development (using ngrok)

```bash
# Install ngrok
brew install ngrok

# Start ngrok tunnel
ngrok http 8080

# You'll see: https://xxxxx.ngrok.io

# Add to Jenkins job → Build Triggers
# Check: GitHub hook trigger for GITScm polling
```

### For Production

1. GitHub Repo → **Settings** → **Webhooks** → **Add webhook**
   - Payload URL: `http://your-jenkins-domain:8080/github-webhook/`
   - Content type: `application/json`
   - Events: Push, Pull requests
   - Active: ✓

2. Jenkins Job → **Build Triggers**
   - Check: `GitHub hook trigger for GITScm polling`

---

## File Structure

```
.
├── Jenkinsfile                      # Main pipeline definition
├── JENKINS_SETUP.md                 # Detailed setup guide
├── PIPELINE_EXAMPLES.md             # Pipeline examples & patterns
├── setup-jenkins.sh                 # Automated setup script
├── configure-jenkins-plugins.sh     # Plugin configuration script
├── jenkins.yaml                     # JCasC configuration
├── docker-compose.yml               # Docker setup
└── Assignments/
    └── session1/
        ├── examples-runner/         # Test project
        └── mock-api-server/         # API for integration tests
```

---

## Troubleshooting

### Jenkins won't start
```bash
# Check if port 8080 is in use
lsof -i :8080

# Kill process on 8080
kill -9 <PID>

# Try different port
java -jar jenkins.war --httpPort=8888
```

### Pipeline can't find Jenkinsfile
- Verify: Repository has `Jenkinsfile` in root
- Verify: Jenkins has access to repository
- Verify: Script Path is set to `Jenkinsfile`

### Tests fail in pipeline
- Run locally first: `mvn test`
- Check Maven: `mvn -version`
- Check Java: `java -version`
- View console output in Jenkins

### Webhook not triggering
1. Check GitHub → Settings → Webhooks → View recent deliveries
2. Should see HTTP 200 response
3. Enable: Jenkins job → Build Triggers → GitHub hook trigger

---

## Next Steps

1. ✓ Install Jenkins
2. ✓ Configure GitHub credentials
3. ✓ Create pipeline job
4. ✓ Run test build
5. **Next:** Read [JENKINS_SETUP.md](JENKINS_SETUP.md) for advanced configuration
6. **Next:** Read [PIPELINE_EXAMPLES.md](PIPELINE_EXAMPLES.md) for pipeline patterns

---

## Support

- **Jenkins Docs:** https://www.jenkins.io/doc/
- **Pipeline Docs:** https://www.jenkins.io/doc/book/pipeline/
- **GitHub Integration:** https://plugins.jenkins.io/github/
- **Issues:** Check Jenkins system logs: `tail -f ~/.jenkins/log/jenkins.log`

---

## Time Estimate

| Task | Time |
|------|------|
| Install Jenkins | 5 min |
| Initial setup | 3 min |
| Add credentials | 3 min |
| Create job | 3 min |
| First build | 5 min |
| **Total** | **~20 min** |

Start now! 🚀
