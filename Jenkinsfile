pipeline {
    agent any

    tools {
        // Install the Maven version configured as "M3" and add it to the path.
        maven "M3"
    }

    stages {
        stage('Build') {
            steps {
                // Run Maven on a Unix agent.
                sh "mvn clean package"
            }
        }
        stage('Test') {
             steps {
                sh "mvn test"
             }
        }
        stage('Deploy')
        {
            steps{
                sh "cp /var/lib/jenkins/workspace/ontology-editor/target/*.jar /home/app/ontology-editor.jar"
                sh "kill `pgrep -f ontology-editor.jar` || true"
                sh "JENKINS_NODE_COOKIE=dontKillMe nohup java -jar /home/app/ontology-editor.jar >/dev/null 2>&1 &"
            }
        }
    }
}