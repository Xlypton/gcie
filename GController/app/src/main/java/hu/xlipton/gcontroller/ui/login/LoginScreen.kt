import android.graphics.Color
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color.Companion.Gray
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import hu.xlipton.gcontroller.R
import hu.xlipton.gcontroller.ui.controller.launch
import hu.xlipton.gcontroller.ui.login.LoginViewModel

@Composable
fun LoginScreen(loginViewModel: LoginViewModel = viewModel(), navController: NavController) {

	val usernameValue = loginViewModel.username
	val passwordValue = loginViewModel.password

	val passwordVisibility = remember { mutableStateOf(false) }
	val focusRequester = remember { FocusRequester() }

	Column(
		horizontalAlignment = Alignment.CenterHorizontally,
		verticalArrangement = Arrangement.Center,
		modifier = Modifier
			.fillMaxWidth()
			.fillMaxHeight()
			.padding(10.dp)
	) {
		Text(
			text = "Sign In",
			style = TextStyle(
				fontWeight = FontWeight.Bold,
				letterSpacing = 2.sp,
				fontSize = 30.sp
			)
		)
		Spacer(modifier = Modifier.padding(20.dp))
		Column(horizontalAlignment = Alignment.CenterHorizontally) {
			OutlinedTextField(
				value = usernameValue.value,
				onValueChange = { usernameValue.value = it },
				label = { Text(text = "Username") },
				placeholder = { Text(text = "Username") },
				singleLine = true,
				modifier = Modifier.fillMaxWidth(0.8f),
				keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
			)

			OutlinedTextField(
				value = passwordValue.value,
				onValueChange = { passwordValue.value = it },
				trailingIcon = {
					IconButton(onClick = {
						passwordVisibility.value = !passwordVisibility.value
					}) {
						Icon(
							imageVector = Icons.Filled.Face,
							contentDescription = "password_visibility",
							tint = if (passwordVisibility.value) MaterialTheme.colors.primary else Gray
						)
					}
				},
				label = { Text("Password") },
				placeholder = { Text(text = "Password") },
				singleLine = true,
				visualTransformation = if (passwordVisibility.value) VisualTransformation.None
				else PasswordVisualTransformation(),
				modifier = Modifier
					.fillMaxWidth(0.8f)
					.focusRequester(focusRequester = focusRequester),
				keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
			)

			Spacer(modifier = Modifier.padding(10.dp))
			Button(
				onClick = { loginViewModel.getAccessToken() },
				modifier = Modifier
					.fillMaxWidth(0.8f)
					.height(50.dp)
			) {
				Text(text = "Sign In")
			}

			Spacer(modifier = Modifier.padding(20.dp))
		}
	}
}