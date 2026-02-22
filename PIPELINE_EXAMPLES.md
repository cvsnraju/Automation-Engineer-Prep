# Jenkins Pipeline Examples & Patterns

Complete guide with examples for various pipeline patterns and use cases.

## Table of Contents
1. [Basic Pipeline Structure](#basic-pipeline-structure)
2. [Advanced Pipeline Patterns](#advanced-pipeline-patterns)
3. [Parallel Execution](#parallel-execution)
4. [Error Handling](#error-handling)
5. [Notifications & Reporting](#notifications--reporting)
6. [Security & Credentials](#security--credentials)
7. [Performance Optimization](#performance-optimization)

---

## Basic Pipeline Structure

### Minimal Pipeline
```groovy
pipeline {
    agent any
    
    stages {
        stage('Build') {
            steps {
                echo 'Building...'
                sh 'mvn clean package'
            }
        }
        stage('Test') {
            steps {
                echo 'Testing...'
                sh 'mvn test'
            }
        }
    }
    
    post {
        always {
            junit '**/target/surefire-reports/*.xml'
        }
    }
}
```

### Pipeline with Declarative Options
```groovy
pipeline {
    agent any
    
    options {
        buildDiscarder(logRotator(numToKeepStr: '10'))
        timeout(time: 1, unit: 'HOURS')
        timestamps()
        disableConcurrentBuilds()
    }
    
    parameters {
        string(name: 'BRANCH', defaultValue: 'main', description: 'Git branch')
        choice(name: 'ENV', choices: ['dev', 'staging', 'prod'], description: 'Deployment environment')
        booleanParam(name: 'SKIP_TESTS', defaultValue: false, description: 'Skip tests?')
    }
    
    stages {
        stage('Setup') {
            steps {
                echo "Building branch: ${params.BRANCH}"
                echo "Environment: ${params.ENV}"
            }
        }
    }
}
```

---

## Advanced Pipeline Patterns

### Multi-Stage Pipeline with Conditional Execution
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
                sh 'mvn clean package -DskipTests'
            }
        }
        
        stage('Unit Tests') {
            steps {
                sh 'mvn test'
            }
        }
        
        stage('Integration Tests') {
            when {
                branch 'main'  // Only on main branch
            }
            steps {
                sh 'mvn verify -P integration-tests'
            }
        }
        
        stage('Code Quality') {
            when {
                expression { currentBuild.result == 'SUCCESS' }
            }
            steps {
                sh 'mvn sonar:sonar'
            }
        }
        
        stage('Security Scan') {
            when {
                expression { params.ENV == 'prod' }
            }
            steps {
                sh 'mvn dependency-check:check'
            }
        }
        
        stage('Approval') {
            when {
                branch 'main'
                expression { params.ENV == 'prod' }
            }
            input {
                message "Deploy to production?"
                ok "Deploy"
                submitter "admin,deployer"
            }
            steps {
                echo "Approved for deployment"
            }
        }
        
        stage('Deploy') {
            when {
                expression { currentBuild.result == 'SUCCESS' }
            }
            steps {
                sh './deploy.sh ${ENV}'
            }
        }
        
        stage('Smoke Tests') {
            when {
                expression { currentBuild.result == 'SUCCESS' }
            }
            steps {
                sh 'mvn test -P smoke-tests -DtestEnvironment=${ENV}'
            }
        }
    }
    
    post {
        always {
            junit '**/target/surefire-reports/*.xml'
            publishHTML([
                reportDir: 'target/surefire-reports',
                reportFiles: 'index.html',
                reportName: 'Test Report'
            ])
        }
        success {
            echo '✓ Build successful!'
        }
        failure {
            echo '✗ Build failed!'
        }
        cleanup {
            deleteDir()
        }
    }
}
```

---

## Parallel Execution

### Parallel Tests
```groovy
pipeline {
    agent any
    
    stages {
        stage('Build') {
            steps {
                sh 'mvn clean package -DskipTests'
            }
        }
        
        stage('Parallel Tests') {
            parallel {
                stage('Unit Tests') {
                    steps {
                        sh 'mvn test -P unit-tests'
                    }
                }
                stage('Integration Tests') {
                    steps {
                        sh 'mvn test -P integration-tests'
                    }
                }
                stage('API Tests') {
                    steps {
                        sh 'mvn test -P api-tests'
                    }
                }
                stage('Performance Tests') {
                    steps {
                        sh 'mvn test -P performance-tests'
                    }
                }
            }
        }
    }
    
    post {
        always {
            junit '**/target/surefire-reports/*.xml'
        }
    }
}
```

### Parallel Stages with Dependencies
```groovy
pipeline {
    agent any
    
    stages {
        stage('Build') {
            steps {
                sh 'mvn clean package'
            }
        }
        
        stage('Test') {
            parallel {
                stage('Component 1 Tests') {
                    agent any
                    steps {
                        sh 'mvn test -P component1'
                    }
                }
                stage('Component 2 Tests') {
                    agent any
                    steps {
                        sh 'mvn test -P component2'
                    }
                }
                stage('Component 3 Tests') {
                    agent any
                    steps {
                        sh 'mvn test -P component3'
                    }
                }
            }
        }
        
        stage('Archive Results') {
            steps {
                junit '**/target/surefire-reports/*.xml'
            }
        }
    }
}
```

---

## Error Handling

### Try-Catch Pattern
```groovy
pipeline {
    agent any
    
    stages {
        stage('Build') {
            steps {
                script {
                    try {
                        sh 'mvn clean package'
                    } catch (Exception e) {
                        echo "Build failed: ${e.message}"
                        currentBuild.result = 'FAILURE'
                        throw e
                    }
                }
            }
        }
        
        stage('Test') {
            steps {
                script {
                    try {
                        sh 'mvn test'
                    } catch (Exception e) {
                        echo "Tests failed: ${e.message}"
                        // Continue to next stage but mark as unstable
                        currentBuild.result = 'UNSTABLE'
                    }
                }
            }
        }
    }
    
    post {
        failure {
            script {
                if (env.BRANCH_NAME == 'main') {
                    // Rollback or notification for main branch
                    echo "CRITICAL: Main branch build failed!"
                }
            }
        }
    }
}
```

### Timeout and Retry
```groovy
pipeline {
    agent any
    
    stages {
        stage('Build with Retry') {
            steps {
                retry(3) {
                    timeout(time: 30, unit: 'MINUTES') {
                        sh 'mvn clean package'
                    }
                }
            }
        }
        
        stage('Test with Timeout') {
            steps {
                timeout(time: 1, unit: 'HOURS') {
                    sh 'mvn test'
                }
            }
        }
    }
}
```

---

## Notifications & Reporting

### Comprehensive Notifications
```groovy
pipeline {
    agent any
    
    post {
        always {
            // Archive test results
            junit '**/target/surefire-reports/*.xml'
            
            // Archive code coverage
            publishHTML([
                reportDir: 'target/site/jacoco',
                reportFiles: 'index.html',
                reportName: 'Code Coverage Report'
            ])
            
            // Archive build logs
            archiveArtifacts artifacts: '**/target/surefire-reports/**', 
                             allowEmptyArchive: true
        }
        
        success {
            // Email notification
            emailext(
                subject: "Build Success: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                body: """
                    Build successful!
                    Job: ${env.JOB_NAME}
                    Build Number: ${env.BUILD_NUMBER}
                    Build URL: ${env.BUILD_URL}
                    Branch: ${env.BRANCH_NAME}
                """,
                to: "team@example.com",
                attachmentsPattern: '**/target/surefire-reports/*.html'
            )
            
            // Slack notification
            slackSend(
                color: 'good',
                message: """
                    Build Success:
                    Job: ${env.JOB_NAME}
                    Build #${env.BUILD_NUMBER}
                    Branch: ${env.BRANCH_NAME}
                    Time: ${env.BUILD_TIMESTAMP}
                """,
                channel: '#automation-ci'
            )
        }
        
        failure {
            // Email notification
            emailext(
                subject: "Build Failed: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                body: """
                    Build failed!
                    Job: ${env.JOB_NAME}
                    Build Number: ${env.BUILD_NUMBER}
                    Build URL: ${env.BUILD_URL}
                    Branch: ${env.BRANCH_NAME}
                    
                    Please check the build logs for details.
                """,
                to: "team@example.com",
                attachLog: true
            )
            
            // Slack notification with details
            slackSend(
                color: 'danger',
                message: """
                    Build Failed:
                    Job: ${env.JOB_NAME}
                    Build #${env.BUILD_NUMBER}
                    Branch: ${env.BRANCH_NAME}
                    Failure: Check logs at ${env.BUILD_URL}
                """,
                channel: '#automation-ci'
            )
        }
        
        unstable {
            slackSend(
                color: 'warning',
                message: "Build Unstable: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                channel: '#automation-ci'
            )
        }
    }
}
```

---

## Security & Credentials

### Using Credentials in Pipeline
```groovy
pipeline {
    agent any
    
    environment {
        // Using credentials plugin
        GITHUB_CREDS = credentials('github-credentials')
        DOCKER_REGISTRY = credentials('docker-registry')
        MAVEN_SETTINGS = credentials('maven-settings-file')
    }
    
    stages {
        stage('Clone Repository') {
            steps {
                checkout([
                    $class: 'GitSCM',
                    userRemoteConfigs: [[
                        url: 'https://github.com/your-repo.git',
                        credentialsId: 'github-credentials'
                    ]],
                    branches: [[name: '${BRANCH_NAME}']]
                ])
            }
        }
        
        stage('Build') {
            steps {
                withCredentials([
                    file(credentialsId: 'maven-settings-file', variable: 'MAVEN_SETTINGS'),
                    usernamePassword(credentialsId: 'docker-registry', 
                                   usernameVariable: 'DOCKER_USER', 
                                   passwordVariable: 'DOCKER_PASS')
                ]) {
                    sh '''
                        mvn clean package -s ${MAVEN_SETTINGS}
                    '''
                }
            }
        }
        
        stage('Push to Registry') {
            steps {
                withCredentials([
                    usernamePassword(credentialsId: 'docker-registry',
                                   usernameVariable: 'DOCKER_USER',
                                   passwordVariable: 'DOCKER_PASS')
                ]) {
                    sh '''
                        docker login -u ${DOCKER_USER} -p ${DOCKER_PASS}
                        docker push my-registry/my-image:latest
                        docker logout
                    '''
                }
            }
        }
    }
}
```

### SSH Key for Deployments
```groovy
pipeline {
    agent any
    
    stages {
        stage('Deploy') {
            steps {
                withCredentials([sshUserPrivateKey(
                    credentialsId: 'deployment-ssh-key',
                    keyFileVariable: 'SSH_KEY',
                    usernameVariable: 'SSH_USER'
                )]) {
                    sh '''
                        ssh -i ${SSH_KEY} -o StrictHostKeyChecking=no \
                            ${SSH_USER}@production-server.com \
                            "cd /app && ./deploy.sh"
                    '''
                }
            }
        }
    }
}
```

---

## Performance Optimization

### Parallel Execution with Matrix
```groovy
pipeline {
    agent any
    
    options {
        // Cache Maven dependencies
        timestamps()
    }
    
    stages {
        stage('Test Matrix') {
            parallel {
                stage('Java 11') {
                    agent any
                    environment {
                        JAVA_VERSION = '11'
                    }
                    steps {
                        sh 'mvn test'
                    }
                }
                stage('Java 17') {
                    agent any
                    environment {
                        JAVA_VERSION = '17'
                    }
                    steps {
                        sh 'mvn test'
                    }
                }
                stage('Java 21') {
                    agent any
                    environment {
                        JAVA_VERSION = '21'
                    }
                    steps {
                        sh 'mvn test'
                    }
                }
            }
        }
    }
}
```

### Incremental Build
```groovy
pipeline {
    agent any
    
    options {
        skipDefaultCheckout()
    }
    
    stages {
        stage('Checkout') {
            steps {
                checkout([
                    $class: 'GitSCM',
                    branches: [[name: '*/main']],
                    userRemoteConfigs: [[url: 'https://github.com/your-repo.git']],
                    extensions: [
                        [$class: 'CloneOption', shallow: true, depth: 1],
                        [$class: 'SubmoduleOption', disableSubmodules: false],
                        [$class: 'RelativeTargetDirectory', relativeTargetDir: 'repo']
                    ]
                ])
            }
        }
        
        stage('Incremental Build') {
            steps {
                sh '''
                    # Only build changed modules
                    git diff HEAD~1 --name-only | \
                    grep -E '.*\.java$|pom\.xml' | \
                    xargs -I {} mvn clean package -pl $(dirname {})
                '''
            }
        }
    }
}
```

---

## Complete Real-World Example

See the main [Jenkinsfile](../Jenkinsfile) in the repository root for a complete production-ready pipeline.

## Testing Pipeline Syntax

```bash
# Validate Jenkinsfile syntax locally
curl -X POST -F "jenkinsfile=<Jenkinsfile" http://localhost:8080/pipeline-model-converter/validate

# Or using Jenkins CLI
java -jar jenkins-cli.jar -s http://localhost:8080 \
  declarative-linter < Jenkinsfile
```

## Resources

- [Jenkins Pipeline Documentation](https://www.jenkins.io/doc/book/pipeline/)
- [Groovy Language Reference](https://groovy-lang.org/syntax.html)
- [Pipeline Steps Reference](https://www.jenkins.io/doc/pipeline/steps/)
- [Blue Ocean UI Guide](https://www.jenkins.io/doc/book/blueocean/)
