package hu.xlipton.gcontroller

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import hu.xlipton.gcontroller.gestures.GestureExtractor
import hu.xlipton.gcontroller.gestures.mediapipe.MediaPipeHands
import hu.xlipton.gcontroller.ui.theme.GControllerTheme
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotateRad
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import hu.xlipton.gcontroller.ui.controller.ControllerScreenContent
import hu.xlipton.gcontroller.ui.controller.ControllerViewModel
import kotlin.math.PI
import kotlin.math.roundToInt

class MainActivity : ComponentActivity(), SensorEventListener {
	private lateinit var sensorManager: SensorManager
	private var proximity: Sensor? = null

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		this.window.statusBarColor = Color.Transparent.value.toInt()
		sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
		proximity = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY)

		setContent {
			GControllerTheme {
				// A surface container using the 'background' color from the theme
				Surface(color = MaterialTheme.colors.background) {
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


}

