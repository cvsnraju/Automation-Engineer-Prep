#!/bin/bash

# Jenkins Pipeline Setup Script
# This script automates initial Jenkins setup on macOS

set -e

echo "========== Jenkins Automated Setup =========="

# Colors for output
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Check if Jenkins is installed
check_jenkins() {
    if command -v brew &> /dev/null; then
        if brew list jenkins-lts &> /dev/null; then
            echo -e "${GREEN}✓ Jenkins is installed via Homebrew${NC}"
            return 0
        fi
    fi
    
    if [ -f "/usr/local/opt/jenkins-lts/homebrew.mxcl.jenkins-lts.plist" ]; then
        echo -e "${GREEN}✓ Jenkins is installed${NC}"
        return 0
    fi
    
    echo -e "${YELLOW}✗ Jenkins not found${NC}"
    return 1
}

# Install Jenkins
install_jenkins() {
    echo -e "${BLUE}Installing Jenkins via Homebrew...${NC}"
    brew install jenkins-lts
    echo -e "${GREEN}✓ Jenkins installed${NC}"
}

# Check Java
check_java() {
    if command -v java &> /dev/null; then
        echo -e "${GREEN}✓ Java is installed${NC}"
        java -version
        return 0
    fi
    
    echo -e "${YELLOW}✗ Java not found${NC}"
    return 1
}

# Check Maven
check_maven() {
    if command -v mvn &> /dev/null; then
        echo -e "${GREEN}✓ Maven is installed${NC}"
        mvn -version
        return 0
    fi
    
    echo -e "${YELLOW}✗ Maven not found${NC}"
    return 1
}

# Install Maven
install_maven() {
    echo -e "${BLUE}Installing Maven via Homebrew...${NC}"
    brew install maven
    echo -e "${GREEN}✓ Maven installed${NC}"
}

# Start Jenkins
start_jenkins() {
    echo -e "${BLUE}Starting Jenkins service...${NC}"
    brew services start jenkins-lts
    sleep 10
    echo -e "${GREEN}✓ Jenkins service started${NC}"
}

# Get Jenkins initial admin password
get_jenkins_password() {
    PASSWORD_FILE="~/.jenkins/secrets/initialAdminPassword"
    if [ -f "$PASSWORD_FILE" ]; then
        echo -e "${BLUE}Initial Admin Password:${NC}"
        cat "$PASSWORD_FILE"
        echo ""
    fi
}

# Check Jenkins connectivity
check_jenkins_connectivity() {
    echo -e "${BLUE}Checking Jenkins connectivity...${NC}"
    
    for i in {1..30}; do
        if curl -s http://localhost:8080 > /dev/null 2>&1; then
            echo -e "${GREEN}✓ Jenkins is accessible at http://localhost:8080${NC}"
            return 0
        fi
        echo "Waiting for Jenkins to start... ($i/30)"
        sleep 2
    done
    
    echo -e "${YELLOW}✗ Jenkins is not responding${NC}"
    return 1
}

# Main execution
main() {
    echo -e "${BLUE}========== System Requirements Check ==========${NC}\n"
    
    # Check prerequisites
    if ! check_java; then
        echo -e "${YELLOW}Please install Java first${NC}"
        exit 1
    fi
    
    if ! check_maven; then
        read -p "Maven not found. Install Maven? (y/n) " -n 1 -r
        echo
        if [[ $REPLY =~ ^[Yy]$ ]]; then
            install_maven
        fi
    fi
    
    echo ""
    echo -e "${BLUE}========== Jenkins Installation ==========${NC}\n"
    
    if ! check_jenkins; then
        read -p "Jenkins not found. Install Jenkins? (y/n) " -n 1 -r
        echo
        if [[ $REPLY =~ ^[Yy]$ ]]; then
            install_jenkins
        else
            echo "Exiting..."
            exit 1
        fi
    fi
    
    echo ""
    echo -e "${BLUE}========== Starting Jenkins ==========${NC}\n"
    
    # Check if Jenkins is running
    if ! curl -s http://localhost:8080 > /dev/null 2>&1; then
        start_jenkins
    else
        echo -e "${GREEN}✓ Jenkins is already running${NC}"
    fi
    
    echo ""
    echo -e "${BLUE}========== Jenkins Status ==========${NC}\n"
    
    check_jenkins_connectivity
    get_jenkins_password
    
    echo ""
    echo -e "${BLUE}========== Next Steps ==========${NC}\n"
    echo "1. Open browser: http://localhost:8080"
    echo "2. Use the initial admin password above to unlock Jenkins"
    echo "3. Follow the setup wizard to install plugins"
    echo "4. Create admin user account"
    echo "5. Read JENKINS_SETUP.md for detailed configuration steps"
    echo ""
}

main "$@"
