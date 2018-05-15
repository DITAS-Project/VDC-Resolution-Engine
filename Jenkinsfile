// Your repo link in Jenkins: http://178.22.71.23:8080/job/VDC-Resolution-Engine/job/master/
pipeline {
    agent none
    stages {
        stage('Build - test') {
            agent {
                dockerfile {
                    filename 'Dockerfile.build'
                }
            }
            steps {
		// Build 
                // sh 'gradle whatever'
		echo "Build stage ..."    
		// Test	
                // sh 'gradle whatever'
				
            }
            // Save the reports always
            post {
                always {
                    // Record the jUnit test
                    junit 'target/surefire-reports/*.xml'
                }
            }
        }
        stage('Image creation') {
            agent any
            options {
                skipDefaultCheckout true
            }
            steps {
                // The Dockerfile.artifact copies the code into the image and run the jar generation.
                echo 'Creating the image...'

                // This will search for a Dockerfile.artifact in the working directory and build the image to the local repository
                sh "docker build -t \"ditas/vdc-resolution-engine\" -f Dockerfile.artifact ."
                echo "Done"
		    
                // Get the password from a file. This reads the file from the host, not the container. Slaves already have the password in there.
                echo 'Retrieving Docker Hub password from /opt/ditas-docker-hub.passwd...'
                script {
                    password = readFile '/opt/ditas-docker-hub.passwd'
                }
                echo "Done"

                echo 'Login to Docker Hub as ditasgeneric...'
                sh "docker login -u ditasgeneric -p ${password}"
                echo "Done"

                echo "Pushing the image ditas/vdc-resolution-engine:latest..."
                sh "docker push ditas/vdc-resolution-engine:latest"
                echo "Done "
            }
        }
        stage('Image deploy') {
            agent any
            options {
                // Don't need to checkout Git again
                skipDefaultCheckout true
            }
            steps {
		// TODO: Uncomment this when the previous stages run correctly
		// Deploy to Staging environment calling the deployment script
                //sh './jenkins/deploy/deploy-staging.sh'
		echo "Deploy stage ..."  
            }
        }
    }
}
