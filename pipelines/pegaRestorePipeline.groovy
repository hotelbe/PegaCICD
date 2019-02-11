pipeline {
    agent any
               
    parameters {
        string(defaultValue: "DEV", description: 'What is Environment?', name: 'ENVIRONMENT')
        string(defaultValue: "TEST", description: 'Name of the label for restore point?', name: 'RESTORE_LABEL')
        string(defaultValue: "administrator@pega.com", description: 'Pega instance username', name: 'PEGA_APP_USERNAME')
        password(defaultValue: "", description: 'Pega instance password', name: 'PEGA_APP_PASSWORD')
    }


    stages {
        stage('Create a restore') {
           steps {
           echo "Create a restore with ${RESTORE_LABEL}"                            
                   sh '''
                        set +x
						chmod -R 766 $WORKSPACE/
						source $WORKSPACE/properties/$ENVIRONMENT.properties
                        source $WORKSPACE/properties/COMMON.properties                        
						export SystemName=$BUILD_TAG
						export SourceHost=$HOST
			            export SourceUser=$PEGA_APP_USERNAME
			            export SourcePassword=$PEGA_APP_PASSWORD
						export RestoreAction=create
						export RestoreLabel=$RESTORE_LABEL
                        export PEGA_HOME=$PEGA_HOME                        
						$ANT_HOME/bin/ant -file $PEGA_HOME/scripts/samples/jenkins/Jenkins-build.xml restoreprops
						$PEGA_HOME/scripts/utils/prpcServiceUtils.sh manageRestorePoints --connPropFile $PEGA_HOME/scripts/utils/${SystemName}_restore.properties
                        '''
                   }     
        }
        stage('List of restore points') {           
           steps {                
                echo "List of restore points "                           
                   sh ''' 
						set +x
                        source $WORKSPACE/properties/$ENVIRONMENT.properties
                        source $WORKSPACE/properties/COMMON.properties
						export SystemName=$BUILD_TAG
						export SourceHost=$HOST
						export SourceUser=$PEGA_APP_USERNAME
						export SourcePassword=$PEGA_APP_PASSWORD
						export RestoreAction=list
                        export PEGA_HOME=$PEGA_HOME                        
						$ANT_HOME/bin/ant -file $PEGA_HOME/scripts/samples/jenkins/Jenkins-build.xml restoreprops
						$PEGA_HOME/scripts/utils/prpcServiceUtils.sh manageRestorePoints --connPropFile $PEGA_HOME/scripts/utils/${SystemName}_restore.properties
                        '''
                   }     
        }
		
		}
        }