package hu.xlipton.gcontroller.ui.controller

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import hu.xlipton.gcontroller.gestures.mediapipe.MediaPipeHands
import hu.xlipton.gcontroller.ui.controls.*
import hu.xlipton.gcontroller.ui.theme.GControllerTheme
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun ControllerScreenContent(controllerViewModel: ControllerViewModel) {
	val colorConnectButton = if (controllerViewModel.activeControl.value == 0) MaterialTheme.colors.activeControlColor else
		MaterialTheme
		.colors
		.background
	val colorSwitch = if (controllerViewModel.activeControl.value == 1) MaterialTheme.colors.activeControlColor else MaterialTheme
		.colors
		.background
	val colorSlider = if (controllerViewModel.activeControl.value == 2) MaterialTheme.colors.activeControlColor else MaterialTheme
		.colors
		.background
	val colorRotaryKnob = if (controllerViewModel.activeControl.value == 3) MaterialTheme.colors.activeControlColor else
		MaterialTheme
		.colors
		.background
	val colorSelect = if (controllerViewModel.activeControl.value == 4) MaterialTheme.colors.activeControlColor else MaterialTheme
		.colors
		.background

	val connectButtonBackground = if (controllerViewModel.isServerStarted.value) MaterialTheme.colors.stop else
		MaterialTheme
		.colors
		.launch

	val connectButtonText = if (controllerViewModel.isServerStarted.value) "Disconnect server" else "Connect to server"

	Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
		ConnectButton({}, colorConnectButton, connectButtonBackground, connectButtonText)

		SwitchControl(onCheckedChange = { controllerViewModel.onSwitchChange() }, checked = controllerViewModel.switchState.value,
			color = colorSwitch)

		SliderControl(controllerViewModel.sliderValue.value.toFloat(), onValueChange = { controllerViewModel
			.onSliderValueChange(it.roundToInt()) },
			fixedSliderValues = controllerViewModel.fixedSliderValue.value, color = colorSlider)

		RotaryKnobControl(radians = controllerViewModel.rotaryKnobValue.value, color = colorRotaryKnob)

		SelectControl(checked = controllerViewModel.selectValue.value, color = colorSelect)
	}

	HandsCameraView(controllerViewModel = controllerViewModel)

	/*
	val scaffoldState = rememberScaffoldState()

	Scaffold(scaffoldState = scaffoldState) {
		if (controllerViewModel.error.value.isNotEmpty()) {
			LaunchedEffect(controllerViewModel.error.value.isNotEmpty()) {
				try {
					when (scaffoldState.snackbarHostState.showSnackbar(
						controllerViewModel.error.value,
					)) {
						SnackbarResult.Dismissed -> {
						}
					}
				} finally {
					//onDismissSnackBarState()
				}
			}
		}
	}
	 */
}

val Colors.activeControlColor: Color
	@Composable get() = if (isLight) Color(0xffeeeeee) else Color(0xff1e2b33)

val Colors.launch: Color
	@Composable get() = Color(0xff00e0a5)

val Colors.stop: Color
	@Composable get() = Color(0xffff1a00)

//TODO 07-Oct-2021/kerip: Handle the lifecycles
@Composable
fun HandsCameraView(controllerViewModel: ControllerViewModel) {
	AndroidView(factory = { context ->
		val mediaPipeHands = MediaPipeHands(context, controllerViewModel.gestureExtractor)
		mediaPipeHands.setupStreamingModePipeline()
	})
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
	GControllerTheme {
		//RotaryKnobControl(color = MaterialTheme.colors.primaryVariant)
		//DisplayValue(sliderValue = 0f, fixedSliderValues = "0")
		//SliderControl(value = 10f, onValueChange = {}, fixedSliderValues = "23", color = MaterialTheme.colors.secondaryVariant)
		//SwitchControl(onCheckedChange = {}, checked = true, color = MaterialTheme.colors.secondaryVariant)
		//SelectControl(mutableListOf(true, false, true, false), MaterialTheme.colors.activeControlColor)
	}
}

