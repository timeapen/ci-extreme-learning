import jenkins.model.*
import hudson.security.HudsonPrivateSecurityRealm
import hudson.security.SecurityRealm

Jenkins jenkins = Jenkins.instance

if (jenkins.securityRealm == SecurityRealm.NO_AUTHENTICATION) {
  SecurityRealm securityRealm = new HudsonPrivateSecurityRealm(false)
  securityRealm.createAccount("admin", "admin")
  jenkins.securityRealm = securityRealm
} else {
  jenkins.getUser("admin")?.addProperty(HudsonPrivateSecurityRealm.Details.fromPlainPassword("admin"))
}
