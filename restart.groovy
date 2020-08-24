def configureEnvBeforeBuild(toolName) {
    def currentBuildToolHome = tool toolName
    env.PATH = "${currentBuildToolHome}/bin:${env.PATH}"
}

timestamps {
    ansiColor('term') {
        node('content') {
            stage('Restart stream server'){
                configureEnvBeforeBuild('node-8')
                withEnv([
                    'JENKINS_NODE_COOKIE=dontKillMe',
                    'NODE_ENV=production'
                ]) {
                    sh '''
                        pm2 list
                        pm2 restart livestream
                    '''.stripIndent()
                }
            }
        }
    }
}
