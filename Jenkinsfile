pipeline {
    agent any

    environment {
        GITHUB_REPO = 'https://github.com/Sahilb123/Sahil.git'
        TOMCAT_URL = 'http://192.168.5.175:8080/manager'
        TOMCAT_CREDENTIALS_ID = 'c7cb4226-1fbd-4320-a11c-01b63e426fec' // Jenkins credentials for Tomcat
        CONTEXT_PATH = '/selenium-app' // Change to your application's context path
        GITHUB_CREDENTIALS_ID = 'aef1d9d5-a974-4142-bae1-8a8030105286' // Correctly enclosed in quotes
    }

    stages {
        stage('Clean Workspace') {
            steps {
                deleteDir() // Clean the workspace before starting the pipeline
            }
        }
        stage('Checkout') {
            steps {
                git branch: 'main', credentialsId: "${GITHUB_CREDENTIALS_ID}", url: "${GITHUB_REPO}" // Checkout main branch
            }
        }
        stage('Install Maven') {
            steps {
                script {
                    // Check if Maven is installed and install it using wget if not
                    sh '''
                        if ! command -v mvn &> /dev/null; then
                            echo "Maven not found. Installing Maven..."
                            MAVEN_VERSION=3.9.5
                            MAVEN_URL=https://downloads.apache.org/maven/maven-3/${MAVEN_VERSION}/binaries/apache-maven-${MAVEN_VERSION}-bin.tar.gz
                            wget ${MAVEN_URL} -O /tmp/maven.tar.gz
                            mkdir -p $WORKSPACE/maven
                            tar -xzf /tmp/maven.tar.gz -C $WORKSPACE/maven --strip-components=1
                            echo "Maven installed successfully."
                        else
                            echo "Maven is already installed."
                        fi
                    '''
                }
            }
        }
        stage('Build WAR') {
            steps {
                script {
                    // Set environment variables for Maven
                    env.M2_HOME = "${WORKSPACE}/maven" // Set the Maven home to the installation directory
                    env.PATH = "${M2_HOME}/bin:" + env.PATH // Update the PATH to include Maven

                    // Read pom.xml to get artifactId and version
                    def pom = readMavenPom file: 'pom.xml'
                    def warFileName = "target/${pom.artifactId}-${pom.version}.war"

                    // Run Maven clean package
                    sh 'mvn clean package -X' // This generates the WAR file in debug mode
                }
            }
        }
        stage('Deploy to Tomcat') {
            steps {
                script {
                    // Get the artifact name from the pom.xml
                    def pom = readMavenPom file: 'pom.xml'
                    def warFileName = "target/${pom.artifactId}-${pom.version}.war"

                    // Get Tomcat credentials from Jenkins credentials store
                    withCredentials([usernamePassword(credentialsId: TOMCAT_CREDENTIALS_ID, usernameVariable: 'TOMCAT_USERNAME', passwordVariable: 'TOMCAT_PASSWORD')]) {
                        // Deploy the WAR file to Tomcat using credentials from Jenkins
                        def deployCommand = "curl --user ${TOMCAT_USERNAME}:${TOMCAT_PASSWORD} --upload-file ${warFileName} ${TOMCAT_URL}/deploy?path=${CONTEXT_PATH}&update=true"
                        sh deployCommand
                    }
                }
            }
        }
    }

    post {
        always {
            echo 'Pipeline finished!'
        }
        success {
            echo 'Pipeline completed successfully.'
        }
        failure {
            echo 'Pipeline failed.'
        }
    }
}
