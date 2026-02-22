#!/bin/bash

# Configure Jenkins with necessary plugins via CLI
# Run this after initial Jenkins setup

set -e

JENKINS_URL="http://localhost:8080"
JENKINS_CLI="java -jar jenkins-cli.jar -s $JENKINS_URL"

echo "========== Jenkins Plugin Configuration =========="

# List of essential plugins
PLUGINS=(
    "workflow-aggregator"           # Pipeline support
    "workflow-durable-task-step"    # Task step support
    "workflow-scm-step"             # SCM step support
    "git"                           # Git plugin
    "git-client"                    # Git client
    "github"                        # GitHub integration
    "github-api"                    # GitHub API
    "email-ext"                     # Email extension
    "junit"                         # JUnit plugin
    "cobertura"                     # Code coverage
    "timestamper"                   # Timestamp in logs
    "log-parser"                    # Log parsing
    "xunit"                         # XUnit plugin
    "groovy-postbuild"              # Groovy postbuild
    "build-timeout"                 # Build timeout
    "credentials"                   # Credentials plugin
    "credentials-binding"           # Credential binding
    "ssh-credentials"               # SSH credentials
    "matrix-auth"                   # Role-based auth
    "authorize-project"             # Project authorization
)

# Download Jenkins CLI if not present
if [ ! -f "jenkins-cli.jar" ]; then
    echo "Downloading Jenkins CLI..."
    curl -o jenkins-cli.jar "$JENKINS_URL/jnlpJars/jenkins-cli.jar"
fi

echo "Installing plugins..."
for plugin in "${PLUGINS[@]}"; do
    echo "Installing: $plugin"
    java -jar jenkins-cli.jar -s "$JENKINS_URL" install-plugin "$plugin" || true
done

echo "Restarting Jenkins to load plugins..."
java -jar jenkins-cli.jar -s "$JENKINS_URL" restart || true

echo "Waiting for Jenkins to restart..."
sleep 30

echo "========== Plugin Installation Complete =========="
echo "Check Jenkins at http://localhost:8080"
