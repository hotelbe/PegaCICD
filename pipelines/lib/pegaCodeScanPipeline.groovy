def pegaCodeScan() {
            script {
                sh '''
		              set +x
                  chmod -R 766 $WORKSPACE/
                  source $WORKSPACE/properties/$EXPORT_ENVIRONMENT.properties
                  curl -u $EXPORT_PEGA_APP_USERNAME:$EXPORT_PEGA_APP_PASSWORD -X POST -is $HOST/prweb/PRRestService/PegaUnit/Pega-Landing-Application/pzGetApplicationQualityDetails?AccessGroup=NBA:Administrator > out.txt
                  score=`grep -oP '(?<=<Compliancescore>).*?(?=</Compliancescore>)' out.txt`
                  if [[ $score < 90 ]]; then
                      echo Compliance score is $score which is greater than 90%, hence proceding with export
                  else
                      echo Compliance score is $score which is less than 90%, hence aborting the job.  
                      exit 1
                  fi''' 
                  }
            }
return this