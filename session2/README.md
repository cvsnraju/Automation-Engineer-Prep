# Session 2 — Jenkins CI/CD

This folder contains documentation and examples for setting up and managing Jenkins pipelines.

Topics covered:

- **CI/CD Pipelines**: Setting up basic and advanced Jenkins pipelines
- **Trigger Types**: SCM polling and webhooks
- **Debugging**: Failure analysis and log inspection

Files

- [CI_CD_PIPELINES.md](CI_CD_PIPELINES.md) — Pipeline setup and best practices
- [TRIGGER_TYPES.md](TRIGGER_TYPES.md) — SCM polling vs. webhooks with examples
- [DEBUGGING.md](DEBUGGING.md) — Troubleshooting failures and analyzing logs
- `examples/` — Example Jenkinsfiles and pipeline scripts

Quick start

1. Download Jenkins war file and place in this folder (or use Docker).
2. Run Jenkins:
   ```bash
   java -jar jenkins.war
   ```
3. Access at `http://localhost:8080`.
4. Create a new pipeline job and use one of the example Jenkinsfiles from `examples/`.
5. Configure trigger type (SCM polling or webhook) per [TRIGGER_TYPES.md](TRIGGER_TYPES.md).
6. Monitor logs and debug failures using [DEBUGGING.md](DEBUGGING.md).
