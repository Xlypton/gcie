package hu.xlipton.gcontroller.ui.controls

import android.graphics.drawable.shapes.Shape
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import hu.xlipton.gcontroller.ui.controller.activeControlColor
import hu.xlipton.gcontroller.ui.theme.GControllerTheme
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Text
import hu.xlipton.gcontroller.ui.controller.launch

@Composable
fun ConnectButton(onClick: () -> Unit, surfaceColor: Color, buttonBackground: Color, buttonText: String) {
	Surface(color = surfaceColor, modifier = Modifier
		.fillMaxWidth()
		.height(140.dp)
		, shape = RoundedCornerShape(16.dp)) {
		Button(
			onClick,
			modifier = Modifier
				.scale(1.5f)
				.padding(top = 50.dp, bottom = 50.dp, start = 120.dp, end = 120.dp),
			elevation = ButtonDefaults.elevation(10.dp),
			shape = RoundedCornerShape(30.dp),
			colors = ButtonDefaults.buttonColors(backgroundColor = buttonBackground),
			content = { Text(buttonText, color = Color.White) },
		)
	}
}

@Preview(showBackground = true)
@Composable
fun ConnectButtonPreview() {
	GControllerTheme {
		ConnectButton({},MaterialTheme.colors.activeControlColor, MaterialTheme.colors.launch, "Disconnect server")
	}
}

