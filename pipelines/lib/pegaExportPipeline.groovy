def pegaExport() {
            script {
                sh '''
                set +x
		        chmod -R 766 $WORKSPACE/
		        source $WORKSPACE/properties/$EXPORT_ENVIRONMENT.properties
                source $WORKSPACE/properties/COMMON.properties
			    export SystemName=$BUILD_TAG
            	export SourceHost=$HOST
			    export SourceUser=$EXPORT_PEGA_APP_USERNAME
			    export SourcePassword=$EXPORT_PEGA_APP_PASSWORD
			    $ANT_HOME/bin/ant -file $PEGA_HOME/scripts/samples/jenkins/Jenkins-build.xml -DproductVersion=$productVersion -DproductName=$productName -DapplicationName=$applicationName -DapplicationVersion=$applicationVersion exportprops
			    $PEGA_HOME/scripts/utils/prpcServiceUtils.sh export --connPropFile $PEGA_HOME/scripts/utils/${SystemName}_export.properties --artifactsDir $WORKSPACE
                cp $WORKSPACE/EXPORT_*/$BUILD_TAG/*.zip $ARTIFACTS_DIR'''
                  }
            }
return this