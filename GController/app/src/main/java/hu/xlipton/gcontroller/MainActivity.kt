package hu.xlipton.gcontroller

import android.content.Context
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.mediapipe.components.PermissionHelper
import hu.xlipton.gcontroller.MediaPipe.GestureExtractor
import hu.xlipton.gcontroller.MediaPipe.MediaPipeHands
import hu.xlipton.gcontroller.ui.theme.GControllerTheme
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.unit.Dp

class MainActivity : ComponentActivity(), SensorEventListener {
	private lateinit var sensorManager: SensorManager
	private var proximity: Sensor? = null
	private val mainViewModel: MainViewModel = MainViewModel()

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
		proximity = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY)

		setContent {
			GControllerTheme {
				// A surface container using the 'background' color from the theme
				Surface(color = MaterialTheme.colors.background) {
					ScreenContent(mainViewModel = mainViewModel)
				}
			}
		}

	}

	override fun onSensorChanged(event: SensorEvent?) {
		if (event?.values?.get(0) == 0f) {
			mainViewModel.onButtonClick()
			Log.i("proxy", "Proxy sensor registered")
		}
	}

	override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
		Log.i("proxy", "accuracy: $accuracy")
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

class MainViewModel : ViewModel() {
	val gestureExtractor: GestureExtractor = GestureExtractor()

	private val _sliderValue: MutableLiveData<Float> = gestureExtractor.sliderValue
	val sliderValue: LiveData<Float> = _sliderValue

	private val _buttonValue: MutableLiveData<String> = MutableLiveData()
	val buttonValue: LiveData<String> = _buttonValue

	fun onSliderValueChange(newSliderValue: Float) {
		_sliderValue.value = newSliderValue
	}

	private var buttonCounter: Int = 0

	fun onButtonClick() {
		buttonCounter++
		_buttonValue.value = "Pressed:  $buttonCounter"
	}
}

@Composable
fun ScreenContent(mainViewModel: MainViewModel) {
	val sliderValue: Float by mainViewModel.sliderValue.observeAsState(initial = 5f)
	val buttonValue: String by mainViewModel.buttonValue.observeAsState(initial = "Not pressed")
	
	Column(modifier = Modifier.fillMaxSize()) {
		ButtonControl(onClick = { mainViewModel.onButtonClick() })
		SliderControl(sliderValue, onValueChange = { mainViewModel.onSliderValueChange(it) })
		DisplayValue(sliderValue = sliderValue, buttonValue = buttonValue)
		Divider()
		HandsCameraView(mainViewModel = mainViewModel)
	}
}

@Composable
fun HandsCameraView(mainViewModel: MainViewModel) {
	AndroidView(factory = { context ->
		val mediaPipeHands: MediaPipeHands = MediaPipeHands(context, mainViewModel.gestureExtractor)
		mediaPipeHands.setupStreamingModePipeline()
	})
}

@Composable
fun DisplayValue(sliderValue: Float, buttonValue: String) {
	Row() {
		Text(sliderValue.toString())
		Text(buttonValue, modifier = Modifier.padding(horizontal = Dp(10f)))
	}
}

@Composable
fun ButtonControl(onClick: () -> Unit) {
	Button(onClick = { onClick }, modifier = Modifier.padding(all = Dp(10f))) {
		Text(text = "ProxyButton")
	}
}

@Composable
fun SliderControl(value: Float, onValueChange: (Float) -> Unit) {
	Slider(value, onValueChange, valueRange = 5f..40f)
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
	//val mediaPipeHands: MediaPipeHands = MediaPipeHands(MainActivity)
	GControllerTheme {
		//ScreenContent()
	}
}