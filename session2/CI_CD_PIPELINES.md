# CI/CD Pipelines — Quick Guide

A Jenkins pipeline defines the steps to build, test, and deploy software. Pipelines can be created via UI (Freestyle) or code (Declarative/Scripted).

Declarative Pipeline (recommended)
- YAML-like syntax in `Jenkinsfile`.
- Stages, steps, post-build actions.
- Easy to read and version-control.

Basic structure

```groovy
pipeline {
  agent any
  stages {
    stage('Checkout') {
      steps {
        checkout scm
      }
    }
    stage('Build') {
      steps {
        sh 'mvn clean package'
      }
    }
    stage('Test') {
      steps {
        sh 'mvn test'
      }
    }
  }
  post {
    always {
      junit 'target/surefire-reports/**/*.xml'
    }
    failure {
      echo 'Build failed!'
    }
  }
}
```

Key concepts

- **Agent**: where pipeline runs (any, label, docker).
- **Stages**: logical groupings of steps (Checkout, Build, Test, Deploy).
- **Steps**: individual commands (sh, bat, git, etc.).
- **Post**: run after pipeline completes (always, success, failure, unstable).

Best practices

- Keep Jenkinsfile in repo root or `jenkins/` folder.
- Use declarative syntax for simplicity.
- Parameterize jobs (branch, environment).
- Archive test reports and logs.
- Use credentials plugin for secrets.
- Fail fast: test early and often.
- Use agents/labels to distribute load.
