package hu.xlipton.gcontroller.ui.controller

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import hu.xlipton.gcontroller.*
import hu.xlipton.gcontroller.gestures.mediapipe.MediaPipeHands
import hu.xlipton.gcontroller.ui.controls.RotaryKnobControl
import hu.xlipton.gcontroller.ui.controls.SelectControl
import hu.xlipton.gcontroller.ui.controls.SliderControl
import hu.xlipton.gcontroller.ui.controls.SwitchControl
import hu.xlipton.gcontroller.ui.theme.GControllerTheme
import kotlin.math.roundToInt

@Composable
fun ControllerScreenContent(controllerViewModel: ControllerViewModel) {
	val sliderValue: Int by controllerViewModel.sliderValue.observeAsState(initial = 5)
	val switchState: Boolean by controllerViewModel.switchState.observeAsState(initial = false)
	val fixedSliderValues: String by controllerViewModel.fixedSliderValue.observeAsState(initial = "0")
	val rotaryKnobValue: Float by controllerViewModel.rotaryKnobValue.observeAsState(initial = 0f)

	//val activeColor = if (controllerViewModel.activeControl.value == 1) MaterialTheme.colors.secondary else MaterialTheme.colors.background

	val colorSwitch = if (controllerViewModel.activeControl.value == 0) MaterialTheme.colors.activeControlColor else MaterialTheme
		.colors
		.background
	val colorSlider = if (controllerViewModel.activeControl.value == 1) MaterialTheme.colors.activeControlColor else MaterialTheme
		.colors
		.background
	val colorRotaryKnob = if (controllerViewModel.activeControl.value == 2) MaterialTheme.colors.activeControlColor else MaterialTheme
		.colors
		.background
	val colorSelect = if (controllerViewModel.activeControl.value == 3) MaterialTheme.colors.activeControlColor else MaterialTheme
		.colors
		.background

	Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
		SwitchControl(onCheckedChange = { controllerViewModel.onSwitchChange() }, checked = switchState, color = colorSwitch)
		SliderControl(sliderValue.toFloat(), onValueChange = { controllerViewModel.onSliderValueChange(it.roundToInt()) },
			fixedSliderValues = fixedSliderValues, color = colorSlider)
		RotaryKnobControl(radians = rotaryKnobValue, color = colorRotaryKnob)
		//SelectControl(checked = , color = )
	}
	HandsCameraView(controllerViewModel = controllerViewModel)
}

private val Colors.activeControlColor: Color
	@Composable get() = if (isLight) Color(0xffeeeeee) else Color(0xff1e2b33)

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
	//val mediaPipeHands: MediaPipeHands = MediaPipeHands(MainActivity)
	GControllerTheme {
		//RotaryKnobControl(color = MaterialTheme.colors.primaryVariant)
		//DisplayValue(sliderValue = 0f, fixedSliderValues = "0")
		//SliderControl(value = 10f, onValueChange = {}, fixedSliderValues = "23", color = MaterialTheme.colors.secondaryVariant)
		//SwitchControl(onCheckedChange = {}, checked = true, color = MaterialTheme.colors.secondaryVariant)
		SelectControl(listOf(true, false, true, false), MaterialTheme.colors.activeControlColor)
	}
}

