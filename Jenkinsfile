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
                sh 'gradle clean build -x test'
		
		// Archive the artifact to be accessible from the Artifacts tab into the Blue Ocean interface, just to have it handy
		archiveArtifacts 'build/libs/*.jar'
		
		// Test	
                sh 'gradle test'
				
            }
            // Save the reports always
            post {
                always {
                    // Record the jUnit test
                    junit 'build/test-results/test/*.xml'
                }
            }
        }
        stage('Staging image creation') {
            agent any
            options {
                skipDefaultCheckout true
            }
            steps {
                // The Dockerfile.artifact copies the code into the image and run the jar generation.
                echo 'Creating the image...'

                // This will search for a Dockerfile.artifact in the working directory and build the image to the local repository
                sh "docker build -t \"ditas/vdc-resolution-engine:staging\" -f Dockerfile.artifact ."
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

                echo "Pushing the image ditas/vdc-resolution-engine:staging"
                sh "docker push ditas/vdc-resolution-engine:staging"
                echo "Done "
            }
        }
        stage('Deployment in Staging') {
            agent any
            options {
                // Don't need to checkout Git again
                skipDefaultCheckout true
            }
            steps {
		        // Deploy to Staging environment calling the deployment script
                sh './jenkins/deploy/deploy-staging.sh' 
            }
        }
	    stage('Dredd API validation') {
	        agent any
	        steps {
	            sh './jenkins/dredd/run-api-test.sh'
	        }
	    }	
    }
}