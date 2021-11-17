package hu.xlipton.gcontroller.ui

import LoginScreen
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import hu.xlipton.gcontroller.ui.theme.GControllerTheme
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.Window
import android.view.WindowManager
import androidx.compose.foundation.layout.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import hu.xlipton.gcontroller.security.LoginService
import hu.xlipton.gcontroller.ui.controller.ControllerScreen
import hu.xlipton.gcontroller.ui.controller.ControllerViewModel
import hu.xlipton.gcontroller.ui.login.LoginViewModel

class MainActivity : ComponentActivity() {

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContent {
			val navController = rememberNavController()
			GControllerTheme {
				// A surface container using the 'background' color from the theme
				Surface(color = MaterialTheme.colors.background) {
					SystemUi(windows = this.window)
					GApplication(navController = navController)
				}
			}
		}
	}

	@Composable
	fun GApplication(navController: NavHostController){
		NavHost(navController = navController, startDestination = "login_screen") {
			composable("login_screen") { LoginScreen(loginViewModel = LoginViewModel(navController),
				navController = navController) }
			composable("control_screen") { ControllerScreen(navController = navController) }
		}
	}

	@Composable
	fun SystemUi(windows: Window) =
		MaterialTheme {
			windows.statusBarColor = MaterialTheme.colors.surface.toArgb()
			windows.navigationBarColor = MaterialTheme.colors.surface.toArgb()
		}

}

