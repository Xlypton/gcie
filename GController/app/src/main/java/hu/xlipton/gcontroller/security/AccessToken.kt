package hu.xlipton.gcontroller.security

import com.google.gson.annotations.SerializedName

class AccessToken (
	@SerializedName("access_token")
	var accessToken: String = "",
	@SerializedName("expires_in")
	var expiresIn: Int = 0,
	@SerializedName("refresh_expiresIn")
	var refreshExpiresIn: Int = 0,
	@SerializedName("refresh_token")
	var refreshToken: String = "",
	@SerializedName("token_type")
	var tokenType: String = "",
	@SerializedName("id_token")
	var idToken: String = "",
	@SerializedName("not-before-policy")
	var notBeforePolicy: Int = 0,
	@SerializedName("session_state")
	var sessionState: String = "",
	@SerializedName("scope")
	var scope: String = "",
)
