pipeline {
    agent any
    environment {
        GITHUB_REPO = 'https://github.com/Sahilb123/Sahil.git'
        TOMCAT_URL = 'http://192.168.5.175:8080/manager'
        TOMCAT_CREDENTIALS_ID = 'c7cb4226-1fbd-4320-a11c-01b63e426fec' // Jenkins credentials for Tomcat
        SELENIUM_HOST = 'http://192.168.5.82:4444/wd/hub'
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
                script {
                    // Checkout main branch
                    git branch: 'main', credentialsId: "${GITHUB_CREDENTIALS_ID}", url: "${GITHUB_REPO}"

                    // Search for pom.xml in the workspace
                    def pomFile = sh(script: 'find . -name pom.xml', returnStdout: true).trim()
                    env.POM_DIR = pomFile ? pomFile.substring(0, pomFile.lastIndexOf('/')) : null // Extract directory path
                    if (!env.POM_DIR) {
                        error("No pom.xml found in the repository.")
                    }
                }
            }
        }
        stage('Build WAR') {
            steps {
                script {
                    // Navigate to the directory containing the pom.xml and run Maven
                    dir(env.POM_DIR) {
                        sh 'mvn clean package -X' // Run Maven with debug logging
                    }
                }
            }
        }
        stage('Deploy to Tomcat') {
            steps {
                script {
                    // Get the artifact name from the pom.xml
                    def pom = readMavenPom file: "${env.POM_DIR}/pom.xml"
                    def warFileName = "target/${pom.artifactId}-${pom.version}.war"

                    // Deploy the WAR file to Tomcat
                    def deployCommand = "curl --user <username>:<password> --upload-file ${warFileName} ${TOMCAT_URL}/deploy?path=${CONTEXT_PATH}&update=true"
                    sh deployCommand
                }
            }
        }
        stage('Selenium Test') {
            steps {
                script {
                    // Run the Selenium test suite against the deployed application
                    sh '''
                    mvn -Dtest=RunSeleniumTests test
                    '''
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
