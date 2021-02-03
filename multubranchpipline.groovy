dockerRegistry = 'registry.****/repository/***/'
dockerRegistryUrl = "https://${dockerRegistry}"
node('master') {
    stage('Checkout') {
        checkout scm
    }
    stage('Docker build and push') {
        withCredentials([[$class: 'FileBinding', credentialsId: '***_bitbucket_key', variable: 'id_rsa']]) {
            sh '''
                rm -f id_rsa || true
                cp ${id_rsa} .
            '''
        }
        docker.withRegistry(dockerRegistryUrl, '***_nexus_key') {
            projectName = env.JOB_NAME.split('/')[1]
            fullName = dockerRegistry + projectName
            customImageID = docker.build("${fullName}:1.0.${env.BUILD_ID}")
            def deployBranches = ['master','qa']
            if (env.BRANCH_NAME in deployBranches) {
                customImageID.push()
                customImageBranch = docker.build("${fullName}:${env.BRANCH_NAME}")
                customImageBranch.push()

            }
        }
    }
}
