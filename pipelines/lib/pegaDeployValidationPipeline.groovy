def pegaDeployValidation() {
            script {
                sh '''
                  set +x
                  source $WORKSPACE/properties/$IMPORT_ENVIRONMENT.properties
                  status="$(curl -is $HOST/prweb/PRRestService/monitor/pingservice/ping | head -1)"
                  validate=( $status )
                  if [ ${validate[-2]} == "200" ]; then
                      echo Pega Application Services are available
                  else
                      echo Pega Application is not responding, Aborting this build.  
                      exit 1
                  fi''' 
                  }
            }
return this