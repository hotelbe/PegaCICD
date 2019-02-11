def pegaFetchArtifactory() {
  script {
		properties = readProperties file: "$SLAVE_HOME/workspace/$JOB_NAME/properties/${IMPORT_ENVIRONMENT}.properties"
    common = readProperties file: "$SLAVE_HOME/workspace/$JOB_NAME/properties/COMMON.properties"
	  def server = Artifactory.newServer url: "${common.ARTIFACTORY_URL}", credentialsId: "${ARTIFACTORY_CREDENTIAL_ID}"
    server.bypassProxy = true
		def downloadSpec = """{
                    "files": [
                         {
                           "pattern": "${properties.SOURCE_REPOSITORY}/${productName}${productVersion}.zip",
                           "target": "$SLAVE_HOME/workspace/$JOB_NAME/prpcServiceUtils_73/destination/"
                         }
                     ]
                  }"""

    server.download(downloadSpec) 
  }
}
return this