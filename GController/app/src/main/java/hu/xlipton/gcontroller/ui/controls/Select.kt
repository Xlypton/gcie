/*
 * Author : AdNovum Informatik AG
 */

package hu.xlipton.gcontroller.ui.controls

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.RadioButton
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun SelectControl(checked: List<Boolean>, color: Color) {
	Surface(color = color, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
		Column(modifier = Modifier.height(100.dp), horizontalAlignment = Alignment.CenterHorizontally) {
			Row(modifier = Modifier.height(50.dp), horizontalArrangement = Arrangement.SpaceBetween,verticalAlignment = Alignment
				.CenterVertically) {
				RadioButton(selected = checked[0], onClick = {}, Modifier.padding(horizontal = 10.dp))
				Divider(
					modifier = Modifier
						.fillMaxHeight()
						.width(1.dp)
				)
				RadioButton(selected = checked[1], onClick = {}, Modifier.padding(horizontal = 10.dp))
			}
			Divider()
			Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
				RadioButton(selected = checked[2], onClick = {}, Modifier.padding(horizontal = 10.dp))
				Divider(
					modifier = Modifier
						.fillMaxHeight()
						.width(1.dp)
				)
				RadioButton(selected = checked[3], onClick = {}, Modifier.padding(horizontal = 10.dp))
			}
		}
	}
}