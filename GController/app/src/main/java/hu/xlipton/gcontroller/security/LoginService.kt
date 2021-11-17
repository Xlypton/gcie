package hu.xlipton.gcontroller.security

import com.auth0.android.jwt.JWT

object LoginService {

	var user: User = User("no user", "", "", listOf())

	fun saveCredentials(userName: String, password: String) {
	}

	fun getUserInfo(accessToken: String)  {
		val jwt: JWT = JWT(accessToken)
		val userName: String = jwt.getClaim("preferred_username").asString().orEmpty()
		val resourceAccess = jwt.getClaim("realm_access").asObject(ResourceAccess::class.java)
		val firstName: String = jwt.getClaim("given_name").asString().orEmpty()
		val lastName: String = jwt.getClaim("family_name").asString().orEmpty()

		if (resourceAccess != null) {
			this.user = User(userName = userName, firstName = firstName, lastName = lastName, roles = resourceAccess.roles)
		}
	}
}
