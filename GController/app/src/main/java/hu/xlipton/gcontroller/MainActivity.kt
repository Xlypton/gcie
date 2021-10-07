package hu.xlipton.gcontroller

import android.content.Context
import android.content.res.Resources
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
import hu.xlipton.gcontroller.mediapipe.MediaPipeHands
import hu.xlipton.gcontroller.ui.theme.GControllerTheme
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.rotateRad
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.graphics.translationMatrix
import kotlin.math.PI
import kotlin.math.roundToInt

class MainActivity : ComponentActivity(), SensorEventListener {
	private lateinit var sensorManager: SensorManager
	private var proximity: Sensor? = null
	private val mainViewModel: MainViewModel = MainViewModel()

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		this.window.statusBarColor = Color.Transparent.value.toInt()
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
			mainViewModel.onSwitchChange()
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

class MainViewModel : ViewModel() {
	val gestureExtractor: GestureExtractor =
		GestureExtractor()

	private val _activeControl: MutableLiveData<Int> = gestureExtractor.activeControl
	val activeControl: LiveData<Int> = _activeControl

	private val _sliderValue: MutableLiveData<Int> = gestureExtractor.sliderValue
	val sliderValue: LiveData<Int> = _sliderValue

	private val _fixedSliderValue: MutableLiveData<String> = gestureExtractor.fixedSliderValues
	val fixedSliderValue: LiveData<String> = _fixedSliderValue

	private val _rotaryKnobValue: MutableLiveData<Float> = gestureExtractor.rotaryKnobValue
	val rotaryKnobValue: LiveData<Float> = _rotaryKnobValue

	private val _switchState: MutableLiveData<Boolean> = gestureExtractor.switchValue
	val switchState: LiveData<Boolean> = _switchState

	/*
	private val _selectValue: MutableLiveData<Int> = gestureExtractor.selectValue
	val selectValue: LiveData<Int> = _selectValue
	when(_selectValue) {
		0 ->
	}

	 */

	fun onSliderValueChange(newSliderValue: Int) {
		_sliderValue.value = newSliderValue
	}

	fun onSwitchChange() {
		_switchState.value = !_switchState.value!!
	}
}

val Colors.activeControlColor: Color
	@Composable get() = if (isLight) Color(0xffeeeeee) else Color(0xff1e2b33)

@Composable
fun ScreenContent(mainViewModel: MainViewModel) {
	val sliderValue: Int by mainViewModel.sliderValue.observeAsState(initial = 5)
	val switchState: Boolean by mainViewModel.switchState.observeAsState(initial = false)
	val fixedSliderValues: String by mainViewModel.fixedSliderValue.observeAsState(initial = "0")
	val rotaryKnobValue: Float by mainViewModel.rotaryKnobValue.observeAsState(initial = 0f)

	//val activeColor = if (mainViewModel.activeControl.value == 1) MaterialTheme.colors.secondary else MaterialTheme.colors.background

	val colorSwitch = if (mainViewModel.activeControl.value == 0) MaterialTheme.colors.activeControlColor else MaterialTheme
		.colors
		.background
	val colorSlider = if (mainViewModel.activeControl.value == 1) MaterialTheme.colors.activeControlColor else MaterialTheme
		.colors
		.background
	val colorRotaryKnob = if (mainViewModel.activeControl.value == 2) MaterialTheme.colors.activeControlColor else MaterialTheme
		.colors
		.background
	val colorSelect = if (mainViewModel.activeControl.value == 3) MaterialTheme.colors.activeControlColor else MaterialTheme
		.colors
		.background

	Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
		SwitchControl(onCheckedChange = { mainViewModel.onSwitchChange() }, checked = switchState, color = colorSwitch)
		SliderControl(sliderValue.toFloat(), onValueChange = { mainViewModel.onSliderValueChange(it.roundToInt()) },
			fixedSliderValues = fixedSliderValues, color = colorSlider)
		RotaryKnobControl(radians = rotaryKnobValue, color = colorRotaryKnob)
		//SelectControl(checked = , color = )
	}
	HandsCameraView(mainViewModel = mainViewModel)
}

@Composable
fun HandsCameraView(mainViewModel: MainViewModel) {
	AndroidView(factory = { context ->
		val mediaPipeHands: MediaPipeHands = MediaPipeHands(context, mainViewModel.gestureExtractor)
		mediaPipeHands.setupStreamingModePipeline()
	})
}

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

@Composable
fun SwitchControl(onCheckedChange: () -> Unit, checked: Boolean, color: Color) {
	Surface(color = color, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
		Switch(
			checked = checked,
			onCheckedChange = { onCheckedChange },
			modifier = Modifier.then(
				Modifier
					.size(Dp(100f))
					.then(Modifier.scale(2.5f))
			))
	}
}

@Composable
fun SliderControl(value: Float, onValueChange: (Float) -> Unit, fixedSliderValues: String, color: Color) {
	Surface(color = color, shape = RoundedCornerShape(16.dp)) {
		Column(horizontalAlignment = Alignment.CenterHorizontally) {
			Text(value.toString(), Modifier.then(
				Modifier
					.padding(top = Dp(10f))
					.then(Modifier.absoluteOffset(y = 10.dp))))
			Slider(value, onValueChange, valueRange = 5f..40f, modifier = Modifier.padding(vertical = Dp(5f)))
			Text(text = "Slider value is set to: $fixedSliderValues", Modifier.then(
				Modifier
					.padding(bottom = Dp(10f))
					.then(
						Modifier
							.absoluteOffset(y = (-6).dp)
					)))
		}
	}
}

@Composable
fun RotaryKnobControl(radians: Float = 0f, color: Color) {
	Surface(color = color, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
		Column(horizontalAlignment = Alignment.CenterHorizontally) {
			Row(verticalAlignment = Alignment.CenterVertically) {
				Canvas(modifier = Modifier.size(Dp(150f))) {
					val canvasWidth = size.width
					val canvasHeight = size.height
					val canvasSize = size
					rotateRad(radians = radians) {
						drawCircle(
							color = Color(0xffff6e40),
							center = Offset(x = canvasWidth / 2, y = canvasHeight / 2),
							radius = size.minDimension / 2.5f
						)
						translate() {  }
						drawRoundRect(
							color = Color.DarkGray,
							cornerRadius = CornerRadius(2f,2f),
							topLeft = Offset(x = canvasWidth / 2F, y = canvasHeight / 6.5F),
							size = Size(canvasWidth / 50f, canvasHeight / 10f)
						)
					}
				}

				Text(text = (radians * 180 / PI).toInt().toString(),
					Modifier
						.then(Modifier.padding(end = Dp(20f), start = Dp(10f)))
						.then
							(
							Modifier
								.scale(1.5f)
						))
			}
		}
	}
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
	//val mediaPipeHands: MediaPipeHands = MediaPipeHands(MainActivity)
	GControllerTheme {
		//RotaryKnobControl(color = MaterialTheme.colors.primaryVariant)
		//DisplayValue(sliderValue = 0f, fixedSliderValues = "0")
		//SliderControl(value = 10f, onValueChange = {}, fixedSliderValues = "23", color = MaterialTheme.colors.secondaryVariant)
		//SwitchControl(onCheckedChange = {}, checked = true, color = MaterialTheme.colors.secondaryVariant)
		SelectControl(listOf(true, false, true, false), MaterialTheme.colors.activeControlColor)
	}
}