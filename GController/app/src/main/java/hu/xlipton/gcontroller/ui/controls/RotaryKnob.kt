package hu.xlipton.gcontroller.ui.controls

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotateRad
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import hu.xlipton.gcontroller.ui.controller.activeControlColor
import hu.xlipton.gcontroller.ui.theme.GControllerTheme
import kotlin.math.PI

@Composable
fun RotaryKnobControl(radians: Float = 0f, color: Color) {
	Surface(color = color, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
		Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround, verticalAlignment = Alignment.CenterVertically) {
			Canvas(modifier = Modifier.size(140.dp)) {
				val canvasWidth = size.width
				val canvasHeight = size.height
				rotateRad(radians = radians) {
					drawCircle(
						color = Color(0xffff6e40),
						center = Offset(x = canvasWidth / 2, y = canvasHeight / 2),
						radius = size.minDimension / 2.6f
					)
					translate() {  }
					drawRoundRect(
						color = Color.DarkGray,
						cornerRadius = CornerRadius(2f,2f),
						topLeft = Offset(x = canvasWidth / 2F, y = canvasHeight / 6F),
						size = Size(canvasWidth / 50f, canvasHeight / 10f)
					)
				}
			}

			Text(text = (radians * 180 / PI).toInt().toString(),
				Modifier.scale(1.5f).padding(end = 30.dp).width(40.dp))
		}
	}
}

@Preview(showBackground = true)
@Composable
fun RotaryKnobPreview() {
	GControllerTheme {
		RotaryKnobControl(color = MaterialTheme.colors.activeControlColor)
	}
}

