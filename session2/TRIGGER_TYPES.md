# Trigger Types — SCM Polling vs. Webhooks

Triggers tell Jenkins when to run a pipeline.

SCM Polling

- Jenkins periodically checks git/svn for changes (e.g., every 5 min).
- Simple to configure but less efficient (many empty polls).
- No external setup required.

Jenkinsfile trigger:
```groovy
pipeline {
  triggers {
    pollSCM('H/5 * * * *')  // cron syntax: every 5 minutes
  }
}
```

Webhooks

- Git repo sends a POST to Jenkins when code is pushed.
- Instant feedback; no polling overhead.
- Requires webhook URL to be accessible from git host.

Github/Gitlab webhook setup

1. In Jenkins job config: **Build Triggers** → **GitHub hook trigger** (or GitLab).
2. Obtain Jenkins webhook URL: `http://<jenkins-host>:8080/github-webhook/` (or `/gitlab-webhook/`).
3. In Github repo: **Settings** → **Webhooks** → **Add webhook**.
   - Payload URL: `http://<jenkins-host>:8080/github-webhook/`
   - Events: Push, Pull Request.
4. Gitlab repo: **Settings** → **Integrations** → **Jenkins CI**.
   - Jenkins URL: `http://<jenkins-host>:8080`

Comparison

| Aspect | Polling | Webhook |
|--------|---------|---------|
| Setup | Easy | Requires network access |
| Latency | 5–10 min | Instant |
| Load | High (many polls) | Low (event-driven) |
| Best for | Testing, closed networks | Production CI/CD |
