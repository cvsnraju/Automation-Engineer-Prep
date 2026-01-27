# Debugging Pipeline Failures

Common issues and how to troubleshoot.

1. Build failures

Check:
- Compilation errors: Maven/Gradle logs.
- Test failures: `target/surefire-reports/` or test framework output.
- Dependency issues: `mvn dependency:tree` or check artifact repos.

Tips:
- Re-run build locally: `mvn clean package`.
- Check environment vars in Jenkins (Manage Jenkins → System Configuration).
- Use `set -x` (bash) or `echo` statements to debug script steps.

2. SCM checkout failures

Cause: SSH key not configured, branch doesn't exist, repo inaccessible.

Fix:
- Verify credentials: Jenkins → Credentials → check SSH key or token.
- Check branch name: `git ls-remote --heads <repo-url>`.
- Test git clone locally: `git clone <repo-url>`.

3. Pipeline won't start

Cause: Trigger not firing, SCM polling disabled, webhook misconfigured.

Fix:
- Verify trigger in job config: **Build Triggers**.
- Test polling manually: job → **Build Now**.
- Check webhook logs in git host (Github/Gitlab).
- Ensure Jenkins is reachable from git host (firewall, proxy).

4. Analyzing logs

- Jenkins console output: Job → **Console Output** (full build log).
- Pipeline stages: Click stage → view step logs.
- Log files: check `target/`, `logs/`, or custom locations.
- Grep for errors: `grep -i error <log-file>`.
- Timestamps: compare job start/end times with git push time.

5. Post-build failures

Cause: Test reports not archived, deployment script failed.

Fix:
- Check post-build actions: e.g., `junit 'target/surefire-reports/**/*.xml'`.
- Verify file paths exist after build.
- Log output early in pipeline to catch missing artifacts.

Tips for debugging

- Add verbose logging: `mvn -X` (debug mode).
- Use conditional steps: run only on failure or success.
- Capture environment: `env > /tmp/env.txt`.
- Use timestamps: `date` command before/after critical steps.
- Enable Jenkins debug logging: Manage Jenkins → System Log.
