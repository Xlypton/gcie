package hu.xlipton.gcontroller.ui.controls

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Slider
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import hu.xlipton.gcontroller.ui.controller.activeControlColor
import hu.xlipton.gcontroller.ui.theme.GControllerTheme

@Composable
fun SliderControl(value: Float, onValueChange: (Float) -> Unit, fixedSliderValues: Int, color: Color) {
	Surface(color = color, shape = RoundedCornerShape(16.dp), modifier = Modifier.height(140.dp)) {
		Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.SpaceAround, modifier =
		Modifier.fillMaxHeight()) {
			Text(value.toString(), Modifier.absoluteOffset(y = 10.dp))
			Slider(value, onValueChange, valueRange = 5f..40f)
			Text(text = "Slider value is set to: $fixedSliderValues", Modifier.then(
				Modifier.padding(bottom = 10.dp).absoluteOffset(y = (-6).dp)))
		}
	}
}

@Preview(showBackground = true)
@Composable
fun SliderKnobPreview() {
	GControllerTheme {
		SliderControl(value = 10f, onValueChange = {}, fixedSliderValues = 23, color = MaterialTheme.colors.activeControlColor)
	}
}