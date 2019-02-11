pipeline {
    agent { label 'PegaSlave' }
               
    parameters {
        string(defaultValue: "DMSample", description: 'What is the product name?', name: 'productName')
        string(defaultValue: "20180326.235148", description: 'What is the product version?', name: 'productVersion')
	      choice(choices: 'DEV\nDEV1', description: 'Environment to Export from?', name: 'EXPORT_ENVIRONMENT')
        choice(choices: 'ST\nST1', description: 'Environment to Import to?', name: 'IMPORT_ENVIRONMENT')
        string(defaultValue: "administrator@pega.com", description: 'Export environment pega instance username', name: 'EXPORT_PEGA_APP_USERNAME')
        password(defaultValue: "", description: 'Export environment pega instance password', name: 'EXPORT_PEGA_APP_PASSWORD')
        string(defaultValue: "administrator@pega.com", description: 'Import environment pega instance username', name: 'IMPORT_PEGA_APP_USERNAME')
        password(defaultValue: "", description: 'Import environment pega instance password', name: 'IMPORT_PEGA_APP_PASSWORD')
        string(defaultValue: "admin", description: 'Artifactory username', name: 'ARTIFACTORY_USERNAME')
        password(defaultValue: "", description: 'Artifactory password', name: 'ARTIFACTORY_PASSWORD')
        // choices are newline separated
        // choice(choices: 'Check for merge conflicts\nRun unit tests', description: 'Provide the details of the stage to be executed', name: 'EXECUTE_STAGE')
    }
   
    stages {
        stage('Code Scan') {
            steps {
	            script{
                def pegaCodeScan = load "pipelines/lib/pegaCodeScanPipeline.groovy"
		            pegaCodeScan.pegaCodeScan()
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
          }
        }
      }
      stage('Artifact Promotion') {
        steps {
               // timeout(5) {                // timeout waiting for input after 60 minutes
          script {
                        // capture the approval details in approvalMap.
                        //  approvalMap = input id: 'APPROVAL', message: 'Hello, Artifact is being promoted. Kindly approve with comment', ok: 'Go Ahead', parameters: [string(defaultValue: '', description: '', name: 'Comments')], submitter: 'user1,user2,group1,admin', submitterParameter: 'APPROVER'                    
                        //  echo "Artifact Promotion to ${IMPORT_ENVIRONMENT} repository" 
                         // loadProperties()
 
		  
			      def pegaPublishArtifactory = load "pipelines/lib/pegaPublishArtifactoryPipeline.groovy"
			      pegaPublishArtifactory.pegaPublishArtifactory() 

          }                                                                                  
           //   }
                // print the details gathered from the approval
               // echo "This build was approved by: ${approvalMap['APPROVER']}"
               // echo "This is what I wanted to comment: ${approvalMap['Comments']}"
               // echo "Artifact Promoted to ${IMPORT_ENVIRONMENT} repository"             
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