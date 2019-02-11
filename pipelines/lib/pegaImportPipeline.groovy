def pegaImport() {
  script {
   sh ''' 
      set +x
      chmod -R 766 $WORKSPACE/
      source $WORKSPACE/properties/$IMPORT_ENVIRONMENT.properties
      source $WORKSPACE/properties/COMMON.properties                       
      export SystemName=$BUILD_TAG
      export TargetHost=$HOST
      export TargetUser=$IMPORT_PEGA_APP_USERNAME
      export TargetPassword=$IMPORT_PEGA_APP_PASSWORD
      export ARTIFACTS_DIR=$ARTIFACTS_DIR
      $ANT_HOME/bin/ant -file $PEGA_HOME/scripts/samples/jenkins/Jenkins-build.xml -DproductVersion=$productVersion -DproductName=$productName -DapplicationName=$applicationName -DapplicationVersion=$applicationVersion importprops
      $PEGA_HOME/scripts/utils/prpcServiceUtils.sh import --connPropFile $PEGA_HOME/scripts/utils/${SystemName}_import.properties --artifactsDir $WORKSPACE
      ''' 
  }
}
return this