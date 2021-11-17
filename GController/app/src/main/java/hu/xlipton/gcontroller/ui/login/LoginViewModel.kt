package hu.xlipton.gcontroller.ui.login

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import hu.xlipton.gcontroller.security.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object LoginConstants {
	const val clientId = "g-login-client"
	const val grantType = "password"
	const val clientSecret = "6bea0637-8511-4c39-b30c-1d17b5b8942d"
	const val scope = "openid"
	const val adminUser = "xlipton"
	const val adminPass = "xlipton"
}
class LoginViewModel(private val navController: NavController): ViewModel() {

	var password: MutableState<String> = mutableStateOf("")
	var username: MutableState<String> = mutableStateOf("")

	fun getAccessToken() {
		val getAuthService: GetAuthService = RetrofitClient.getRetrofitInstance().create(GetAuthService::class.java)

		val call: Call<AccessToken> = getAuthService.getAccessToken(
			client_id = LoginConstants.clientId,
			grant_type = LoginConstants.grantType,
			client_secret = LoginConstants.clientSecret,
			scope = LoginConstants.scope,
			username = username.value,
			password = password.value,
			)

		if (username.value == LoginConstants.adminUser && password.value == LoginConstants.adminPass) {
			LoginService.user = User("xlipton", "","", listOf("admin"))
			navigate()
		}

		call.enqueue(object: Callback<AccessToken> {
			override fun onResponse(call: Call<AccessToken>, response: Response<AccessToken>) {
				if (response.isSuccessful) {
					response.body()?.let { LoginService.getUserInfo(it.accessToken) }
					Log.i("Auth", "Successful login!")
					navigate()
				} else {
					Log.w("Auth", "Response: " + response.code())
				}
			}

			override fun onFailure(call: Call<AccessToken>, t: Throwable) {
				Log.w("Auth", "Error during login")
			}

		})
	}

	fun navigate() {
		navController.navigate("control_screen") {
			popUpTo("login_screen") { inclusive = true }
		}
	}

}
