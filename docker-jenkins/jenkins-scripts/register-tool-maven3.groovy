import jenkins.model.*
import hudson.slaves.EnvironmentVariablesNodeProperty
import hudson.tasks.Maven
import hudson.util.DescribableList
import jenkins.model.Jenkins

String mavenToolId = 'maven3'

List mavenInstallations = [ new Maven.MavenInstallation(mavenToolId, '/opt/maven', []) ]
Jenkins.instance.getDescriptorByType(Maven.DescriptorImpl.class).installations = mavenInstallations

// Create a global environment variable to reference the maven tool installation
DescribableList globalNodeProperties = Jenkins.instance.globalNodeProperties
EnvironmentVariablesNodeProperty environmentVariables = globalNodeProperties.find({ it in EnvironmentVariablesNodeProperty })
if (!environmentVariables) {
  environmentVariables = new EnvironmentVariablesNodeProperty()
  globalNodeProperties << environmentVariables
}

environmentVariables.envVars.put('MAVEN', mavenToolId)
