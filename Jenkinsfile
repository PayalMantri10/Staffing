pipeline {
	
   environment {
    registry = "staffingaccolite/staffing"
    registryCredential = 'payalmantri10'
  }
	
	
   agent  any;
   tools {
      maven 'maven 3'
      jdk 'java 8'
   }
	
   stages {
	   
	   
  
        stage('Build') {
           steps {
		
			sh "mvn -B -DskipTests -P prod clean install";
		}
	    
         }

	   
	   
	   
         stage('Test') {
             steps {
                 sh 'mvn test -P prod'
             }
             post {
                 always {
                     junit 'target/surefire-reports/*.xml';
                 }
             }
         
          }
	   
	     stage('Building image') {
      steps{
        script {
		
          docker.build registry + ":latest"
		}
	     
        
      }
    }
	   
	
   stage('Docker Push') {
  
      steps {
        withCredentials([usernamePassword(credentialsId: 'dockerHub', passwordVariable: 'dockerHubPassword', usernameVariable: 'dockerHubUser')]) {
          
	  sh "docker login -u ${env.dockerHubUser} -p ${env.dockerHubPassword}"
          sh 'docker push staffingaccolite/staffing:latest'
        }
      }
	   
	   
        }
	   
	   stage('Deploy'){
		   steps{
			   dir('/home/accoliteadmin/Desktop/Staffing/Staffing'){
		   		sh '/home/accoliteadmin/Desktop/Staffing/deploy.sh'
			   }
		   	}
		   
	   }
     } 
}  
