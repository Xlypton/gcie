/*
 * Author : AdNovum Informatik AG
 */

package hu.xlipton.gcontroller.ui.controls

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Slider
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun SliderControl(value: Float, onValueChange: (Float) -> Unit, fixedSliderValues: String, color: Color) {
	Surface(color = color, shape = RoundedCornerShape(16.dp)) {
		Column(horizontalAlignment = Alignment.CenterHorizontally) {
			Text(value.toString(), Modifier.then(
				Modifier
					.padding(top = 10.dp)
					.then(Modifier.absoluteOffset(y = 10.dp))))
			Slider(value, onValueChange, valueRange = 5f..40f, modifier = Modifier.padding(vertical = 5.dp))
			Text(text = "Slider value is set to: $fixedSliderValues", Modifier.then(
				Modifier
					.padding(bottom = 10.dp)
					.then(
						Modifier
							.absoluteOffset(y = (-6).dp)
					)))
		}
	}
}