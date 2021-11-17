package hu.xlipton.gcontroller.ui.controls

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.material.Switch
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun SwitchControl(onCheckedChange: () -> Unit, checked: Boolean, color: Color, isControlEnabled: Boolean) {
	Surface(color = color, modifier = Modifier.fillMaxWidth().height(140.dp), shape = RoundedCornerShape(16.dp)) {
		Switch(
			checked = checked,
			onCheckedChange = { onCheckedChange },
			modifier = Modifier.size(100.dp).scale(2.5f),
			enabled = isControlEnabled,
		)
	}
}