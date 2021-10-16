package hu.xlipton.gcontroller

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
import androidx.compose.foundation.layout.*
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import hu.xlipton.gcontroller.ui.controller.ControllerScreenContent
import hu.xlipton.gcontroller.ui.controller.ControllerViewModel

class MainActivity : ComponentActivity(), SensorEventListener {
	private lateinit var sensorManager: SensorManager
	private var proximity: Sensor? = null

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
		proximity = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY)

		setContent {
			GControllerTheme {
				// A surface container using the 'background' color from the theme
				Surface(color = MaterialTheme.colors.background) {
					SystemUi(windows = this.window)
					ControllerScreenContent(ControllerViewModel())
				}
			}
		}

	}

	override fun onSensorChanged(event: SensorEvent?) {
		if (event?.values?.get(0) == 0f) {
			//mainViewModel.onSwitchChange()
			//Log.i("proxy", "Proxy sensor registered")
		}
	}

	override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
		//Log.i("proxy", "accuracy: $accuracy")
	}

	override fun onResume() {
		// Register a listener for the sensor.
		super.onResume()

		proximity?.also { proximity ->
			sensorManager.registerListener(this, proximity, SensorManager.SENSOR_DELAY_NORMAL)
		}
	}

	override fun onPause() {
		// Be sure to unregister the sensor when the activity pauses.
		super.onPause()
		sensorManager.unregisterListener(this)
	}

	@Composable
	fun SystemUi(windows: Window) =
		MaterialTheme {
			windows.statusBarColor = MaterialTheme.colors.surface.toArgb()
			windows.navigationBarColor = MaterialTheme.colors.surface.toArgb()
		}

}

