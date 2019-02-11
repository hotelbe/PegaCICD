def pegaPublishArtifactory() {
  script {
	  properties = readProperties file: "$SLAVE_HOME/workspace/$JOB_NAME/properties/${EXPORT_ENVIRONMENT}.properties"
    common = readProperties file: "$SLAVE_HOME/workspace/$JOB_NAME/properties/COMMON.properties"
    def server = Artifactory.newServer url: "${common.ARTIFACTORY_URL}", credentialsId: "${ARTIFACTORY_CREDENTIAL_ID}"
    server.bypassProxy = true
	  def uploadSpec = """{
      "files": [
  			{
    			"pattern": "$SLAVE_HOME/workspace/$JOB_NAME/EXPORT_*/$BUILD_TAG/${productName}${productVersion}.zip" ,
    			"target": "${properties.TARGET_REPOSITORY}/"
  			}
      ]
	  }"""
    // Promote build
    server.upload(uploadSpec) 
  }
}
return this