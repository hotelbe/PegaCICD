pipeline {
    agent { label 'PegaSlave' }
               
    parameters {
      string(defaultValue: "NBA", description: 'What is the product name?', name: 'productName')
      string(defaultValue: "20180326.235148", description: 'What is the product version?', name: 'productVersion')
      choice(choices: 'TEST\nSIT\nUAT', description: 'Environment to Export from?', name: 'IMPORT_ENVIRONMENT')
      string(defaultValue: "DevOps", description: 'Export environment pega instance username', name: 'IMPORT_PEGA_APP_USERNAME')
      password(defaultValue: "", description: 'Export environment pega instance password', name: 'IMPORT_PEGA_APP_PASSWORD')
      password(defaultValue: "adb35bb3-a973-42ff-b61a-e97b43497892", description: 'Artifactory credential key', name: 'ARTIFACTORY_CREDENTIAL_ID')
    }
   
    stages {
        stage('Fetch from Artifactory') {
          steps {
            echo 'Fetch from Artifactory' 
            script {            
              def pegaFetchArtifactory = load "pipelines/lib/pegaFetchArtifactoryPipeline.groovy"
              pegaFetchArtifactory.pegaFetchArtifactory()                   
            }
          }
        } 
        
        stage('Deploy to environment') {
          steps {
            echo "Deploying to ${IMPORT_ENVIRONMENT}"  
              script {
              def pegaImport = load "pipelines/lib/pegaImportPipeline.groovy"
	            pegaImport.pegaImport()               
            }
          }
        }


      stage('Deployment Validation') {
        steps {
          echo 'Deployment Validation'  
          script {
            def pegaDeployValidation = load "pipelines/lib/pegaDeployValidationPipeline.groovy"
	          pegaDeployValidation.pegaDeployValidation()
            build(job: 'AUTOMATED_TEST_PIPELINE', propagate: true, wait: true, parameters: [
              [$class: 'StringParameterValue', name: 'BITBUCKET_CREDENTIALS_ID', value: "7cf7c2da-ed24-4403-a2cc-550f1b38377a"],
              [$class: 'StringParameterValue', name: 'SAUCE_LABS', value: "528a4b09-8253-4ed2-9322-79bc0367af4b"]
            ])
          }
        }
      }    
    }
    post {
      failure {
        echo 'NOT SUCESSFUL'
        //slackSend (color: '#FF0000', message: "FAILED: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]' (${env.BUILD_URL})")
    }   
  }
}