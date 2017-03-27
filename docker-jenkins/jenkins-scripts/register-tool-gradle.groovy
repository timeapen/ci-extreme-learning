import jenkins.model.*
import hudson.slaves.EnvironmentVariablesNodeProperty
import hudson.plugins.gradle.Gradle
import hudson.plugins.gradle.GradleInstallation
import hudson.util.DescribableList
import jenkins.model.Jenkins

String gradleToolId = 'gradle'

List gradleInstallations = [ new GradleInstallation(gradleToolId, '/usr/lib/gradle/3.2.1/', []) ]
Jenkins.instance.getDescriptorByType(Gradle.DescriptorImpl.class).installations = gradleInstallations

// Create a global environment variable to reference the maven tool installation
DescribableList globalNodeProperties = Jenkins.instance.globalNodeProperties
EnvironmentVariablesNodeProperty environmentVariables = globalNodeProperties.find({ it in EnvironmentVariablesNodeProperty })
if (!environmentVariables) {
  environmentVariables = new EnvironmentVariablesNodeProperty()
  globalNodeProperties << environmentVariables
}

environmentVariables.envVars.put('GRADLE', gradleToolId)
