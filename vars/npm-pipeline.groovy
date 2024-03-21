// This script defines a function called 'checkoutAndBuild'
// This function can be reused in multiple Jenkinsfiles

def checkoutAndBuild(String branchName, String repoUrl) {

    // Checkout the code from the provided repository URL and branch
    stage('Checkout') {
        steps {
            script {
                echo "Checkout Test"
            }
        }
    }

    // Build the code using Maven (replace with your build command)
    stage('Build') {
        steps {
            echo 'Build Test' // You might need to adjust this command based on your project
        }
    }
}

// This line exposes the 'checkoutAndBuild' function to be used in Jenkinsfiles
return this