pipeline {
    agent { label 'PegaSlave' }
               
    parameters {
      string(defaultValue: "NBA", description: 'What is the product name?', name: 'productName')
      string(defaultValue: "", description: 'What is the product version?', name: 'productVersion')
      choice(choices: 'DEV\nTEST', description: 'Environment to Export from?', name: 'EXPORT_ENVIRONMENT')
      password(defaultValue: "106d1ec1-b549-44cd-bd71-b19099e7c39c", description: 'Artifactory credential key', name: 'ARTIFACTORY_CREDENTIAL_ID')
      string(defaultValue: "DevOps", description: "Username for pega export", name: 'EXPORT_PEGA_APP_USERNAME')
      password(defaultValue: "", description: "Password for pega export", name: 'EXPORT_PEGA_APP_PASSWORD')
    }
   
    stages {
      stage('Code Scan') {
          steps {
            script{
              echo """Skipping code scan
                def pegaCodeScan = load "pipelines/lib/pegaCodeScanPipeline.groovy"
		            pegaCodeScan.pegaCodeScan()
              """
            }
         }
      } 
        
	  stage('Export from environment') {          
      steps {              
        echo "Exporting application from ${EXPORT_ENVIRONMENT} environment : " 
        script {
          def pegaExport = load "pipelines/lib/pegaExportPipeline.groovy"
          pegaExport.pegaExport()               
        }
      }
    }

    stage('Artifact Promotion') {
      steps {
        script {
          def pegaPublishArtifactory = load "pipelines/lib/pegaPublishArtifactoryPipeline.groovy"
			    pegaPublishArtifactory.pegaPublishArtifactory() 
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