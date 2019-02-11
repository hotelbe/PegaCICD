pipeline {
    agent any
               
    parameters {
        string(defaultValue: "DEV", description: 'What is Environment?', name: 'ENVIRONMENT')
        choice(choices: 'true\nfalse', description: 'Specify whether to run the process in asynchronous mode. In asynchronous mode, the system queues the job and returns a job ID for each operation. You can later use this job ID to check the status of the rollback operation', name: 'ROLLBACK_ASYNC')
		choice(choices: 'true\nfalse', description: 'Set the downloadLogToFile parameter to write the log to a file', name: 'ROLLBACK_DOWNLOAD_LOGTOFILE')
        string(defaultValue: "administrator@pega.com", description: 'Pega instance username', name: 'PEGA_APP_USERNAME')
        password(defaultValue: "", description: 'Pega instance password', name: 'PEGA_APP_PASSWORD')
    }


    stages {
        stage('Rolling back to a restore point') {
           steps {
           echo "Rolling back to a restore point"                            
                   sh ''' 
						chmod -R 766 $WORKSPACE/
                        set +x
						source $WORKSPACE/properties/$ENVIRONMENT.properties
                        source $WORKSPACE/properties/COMMON.properties                        
						export SystemName=$BUILD_TAG
						export SourceHost=$HOST
			            export SourceUser=$PEGA_APP_USERNAME
			            export SourcePassword=$PEGA_APP_PASSWORD
						export RollbackAction=SystemRollback
						export RollbackAsync=$ROLLBACK_ASYNC
						export RollbackLogToFile=$ROLLBACK_DOWNLOAD_LOGTOFILE
                        export PEGA_HOME=$PEGA_HOME                        
						$ANT_HOME/bin/ant -file $PEGA_HOME/scripts/samples/jenkins/Jenkins-build.xml restoreprops
						$PEGA_HOME/scripts/utils/prpcServiceUtils.sh rollback --connPropFile $PEGA_HOME/scripts/utils/${SystemName}_restore.properties
                        '''
                   }     
        }
	
		}
        }