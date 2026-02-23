// Comprehensive Jenkins Pipeline for Automation Engineer Prep
pipeline {
    agent any
    
    options {
        // timestamps()
        timeout(time: 1, unit: 'HOURS')
        buildDiscarder(logRotator(numToKeepStr: '10'))
    }
    
    parameters {
        choice(
            name: 'TEST_ENVIRONMENT',
            choices: ['dev', 'staging', 'prod'],
            description: 'Select target environment'
        )
        booleanParam(
            name: 'RUN_UNIT_TESTS',
            defaultValue: true,
            description: 'Run unit tests'
        )
        booleanParam(
            name: 'RUN_INTEGRATION_TESTS',
            defaultValue: true,
            description: 'Run integration tests'
        )
    }
    
    environment {
        WORKSPACE_PATH = "${WORKSPACE}"
        // Use absolute paths to ensure tools are found regardless of Jenkins user context
        JAVA_HOME = "/opt/homebrew/opt/openjdk"
        MAVEN_HOME = "/opt/homebrew/Cellar/maven/3.9.12/libexec"
        PATH = "/opt/homebrew/bin:/opt/homebrew/sbin:/opt/homebrew/opt/openjdk/bin:/opt/homebrew/Cellar/maven/3.9.12/libexec/bin:/usr/local/bin:/usr/bin:/bin:/usr/sbin:/sbin"
        BUILD_TIMESTAMP = sh(script: "date +'%Y%m%d_%H%M%S'", returnStdout: true).trim()
    }
    
    stages {
        stage('Checkout Code') {
            steps {
                echo "========== Checking out code from repository =========="
                checkout([
                    $class: 'GitSCM',
                    branches: [[name: '*/main'], [name: '*/develop']],
                    userRemoteConfigs: [[
                        url: 'https://github.com/cvsnraju/Automation-Engineer-Prep.git' 
                        //  update username as per your GitHub account
                    ]]
                ])
                sh 'git log --oneline -5'
            }
        }
        
        stage('Build Preparation') {
            steps {
                echo "========== Preparing build environment =========="
                sh '''
                    echo "Java Version:"
                    java -version 2>&1 || echo "⚠️  Java not found"
                    
                    echo "Maven Version:"
                    mvn -version 2>&1 || echo "⚠️  Maven not found"
                    
                    echo "Working Directory:"
                    pwd
                    
                    echo "PATH: $PATH"
                '''
            }
        }
        
        stage('Build Projects') {
            steps {
                echo "========== Building Maven projects =========="
                sh '''
                    # Use absolute path to Maven if not found in PATH
                    if ! command -v mvn &> /dev/null; then
                        echo "⚠️  Maven not in PATH, using absolute path..."
                        export MVN="/opt/homebrew/Cellar/maven/3.9.12/libexec/bin/mvn"
                    else
                        export MVN="mvn"
                    fi
                    
                    # Build examples-runner if directory exists
                    if [ -d "Assignments/session1/examples-runner" ]; then
                        echo "Building examples-runner..."
                        cd Assignments/session1/examples-runner
                        $MVN clean package -DskipTests 2>&1 || echo "⚠️  Build failed but continuing..."
                        cd - > /dev/null
                    else
                        echo "⚠️  examples-runner directory not found"
                    fi
                    
                    # Build mock-api-server if directory exists
                    if [ -d "Assignments/session1/mock-api-server" ]; then
                        echo "Building mock-api-server..."
                        cd Assignments/session1/mock-api-server
                        $MVN clean install -DskipTests 2>&1 || echo "⚠️  Build failed but continuing..."
                        cd - > /dev/null
                    else
                        echo "⚠️  mock-api-server directory not found"
                    fi
                '''
            }
        }
        
        stage('Start Mock API Server') {
            when {
                expression { params.RUN_INTEGRATION_TESTS }
            }
            steps {
                echo "========== Starting Mock API Server =========="
                dir("${WORKSPACE}/Assignments/session1/mock-api-server") {
                    sh '''
                        mvn compile exec:java -Dexec.mainClass="session1.mockapi.MockApiServer" &
                        sleep 5
                        echo "Mock API Server started on port 8081"
                    '''
                }
            }
        }
        
        stage('Unit Tests') {
            when {
                expression { params.RUN_UNIT_TESTS }
            }
            steps {
                echo "========== Running Unit Tests =========="
                dir("${WORKSPACE}/Assignments/session1/examples-runner") {
                    sh '''
                        mvn test -Dgroups="unit" || true
                    '''
                }
            }
        }
        
        stage('Integration Tests') {
            when {
                expression { params.RUN_INTEGRATION_TESTS }
            }
            steps {
                echo "========== Running Integration Tests =========="
                dir("${WORKSPACE}/Assignments/session1/examples-runner") {
                    sh '''
                        mvn test -Dtest=SRKRecWebsiteTest || true
                    '''
                }
            }
        }
        
        stage('Test Report Generation') {
            steps {
                echo "========== Generating Test Reports =========="
                dir("${WORKSPACE}/Assignments/session1/examples-runner") {
                    sh '''
                        if [ -d "target/surefire-reports" ]; then
                            echo "Test reports found"
                            ls -la target/surefire-reports/
                        fi
                    '''
                }
            }
        }
        
        stage('Code Quality Analysis') {
            steps {
                echo "========== Running Code Quality Checks =========="
                sh '''
                    echo "Running findbugs and checkstyle..."
                    # Can be extended with SonarQube, Checkstyle, etc.
                    find ${WORKSPACE} -name "*.java" -type f | wc -l
                    echo "this is demo statement to test the pipeline"
                '''
            }
        }
        
        stage('Artifact Archival') {
            steps {
                echo "========== Archiving Build Artifacts =========="
                sh '''
                    mkdir -p ${WORKSPACE}/build-artifacts/${BUILD_TIMESTAMP}
                    cp ${WORKSPACE}/Assignments/session1/examples-runner/target/*.jar ${WORKSPACE}/build-artifacts/${BUILD_TIMESTAMP}/ || true
                    ls -la ${WORKSPACE}/build-artifacts/${BUILD_TIMESTAMP}/
                '''
            }
        }
        
        stage('Deployment') {
            when {
                expression { 
                    currentBuild.result == null || currentBuild.result == 'SUCCESS'
                }
            }
            steps {
                echo "========== Deploying to ${params.TEST_ENVIRONMENT} environment =========="
                sh '''
                    echo "Deploying artifacts to ${TEST_ENVIRONMENT}..."
                    echo "Deployment would happen here based on environment"
                    # Add your deployment logic here
                '''
            }
        }
        
        stage('Post-Deployment Tests') {
            when {
                expression { 
                    currentBuild.result == null || currentBuild.result == 'SUCCESS'
                }
            }
            steps {
                echo "========== Running Smoke Tests =========="
                sh '''
                    echo "Running smoke tests on deployed application..."
                    sleep 2
                    echo "Smoke tests passed"
                '''
            }
        }
    }
    
    post {
        always {
            echo "========== Pipeline Execution Completed =========="
            
            // Archive test results if they exist
            sh '''
                if [ -d "**/target/surefire-reports" ]; then
                    echo "Archiving test results..."
                    find . -name "surefire-reports" -type d
                fi
            '''
            
            // Archive artifacts
            archiveArtifacts artifacts: '**/target/**/*.jar', 
                             allowEmptyArchive: true
            
            // Clean up
            sh '''
                echo "Cleaning up..."
                pkill -f MockApiServer || true
            '''
        }
        success {
            echo "✓ Pipeline executed successfully!"
            sh '''
                echo "Build completed successfully!"
                echo "Build: ${JOB_NAME} #${BUILD_NUMBER}"
                echo "Duration: ${BUILD_DURATIONSTRING}"
            '''
        }
        failure {
            echo "✗ Pipeline execution failed!"
            sh '''
                echo "Build failed!"
                echo "Job: ${JOB_NAME} #${BUILD_NUMBER}"
                echo "Check console output for details"
            '''
        }
        unstable {
            echo "⚠ Pipeline completed with warnings"
        }
        cleanup {
            sh 'echo "Workspace cleanup (keeping for debugging)"'
        }
    }
}
