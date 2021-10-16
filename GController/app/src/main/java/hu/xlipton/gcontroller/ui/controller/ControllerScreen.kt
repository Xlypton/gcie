package hu.xlipton.gcontroller.ui.controller

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import hu.xlipton.gcontroller.gestures.mediapipe.MediaPipeHands
import hu.xlipton.gcontroller.ui.controls.RotaryKnobControl
import hu.xlipton.gcontroller.ui.controls.SelectControl
import hu.xlipton.gcontroller.ui.controls.SliderControl
import hu.xlipton.gcontroller.ui.controls.SwitchControl
import hu.xlipton.gcontroller.ui.theme.GControllerTheme
import kotlin.math.roundToInt

@Composable
fun ControllerScreenContent(controllerViewModel: ControllerViewModel) {
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

	Column(modifier = Modifier.fillMaxSize().padding(top = 40.dp), horizontalAlignment = Alignment.CenterHorizontally) {

		SwitchControl(onCheckedChange = { controllerViewModel.onSwitchChange() }, checked = controllerViewModel.switchState.value,
			color = colorSwitch)

		SliderControl(controllerViewModel.sliderValue.value.toFloat(), onValueChange = { controllerViewModel
			.onSliderValueChange(it.roundToInt()) },
			fixedSliderValues = controllerViewModel.fixedSliderValue.value, color = colorSlider)

		RotaryKnobControl(radians = controllerViewModel.rotaryKnobValue.value, color = colorRotaryKnob)

		SelectControl(checked = controllerViewModel.selectValue.value, color = colorSelect)
	}

	HandsCameraView(controllerViewModel = controllerViewModel)
}

val Colors.activeControlColor: Color
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
	GControllerTheme {
		//RotaryKnobControl(color = MaterialTheme.colors.primaryVariant)
		//DisplayValue(sliderValue = 0f, fixedSliderValues = "0")
		//SliderControl(value = 10f, onValueChange = {}, fixedSliderValues = "23", color = MaterialTheme.colors.secondaryVariant)
		//SwitchControl(onCheckedChange = {}, checked = true, color = MaterialTheme.colors.secondaryVariant)
		//SelectControl(mutableListOf(true, false, true, false), MaterialTheme.colors.activeControlColor)
	}
}

