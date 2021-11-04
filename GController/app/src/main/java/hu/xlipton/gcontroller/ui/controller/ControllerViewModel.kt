package hu.xlipton.gcontroller.ui.controller

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hu.xlipton.gcontroller.gestures.GestureExtractor
import java.util.*

class ControllerViewModel : ViewModel() {
	val gestureExtractor: GestureExtractor =
		GestureExtractor()

	val activeControl = gestureExtractor.activeControl

	val sliderValue = gestureExtractor.sliderValue

	val fixedSliderValue = gestureExtractor.fixedSliderValue

    val rotaryKnobValue = gestureExtractor.rotaryKnobValue

	val switchState = gestureExtractor.switchValue

	val selectValue = gestureExtractor.selectValue

	val isServerStarted = gestureExtractor.isServerStarted

	val error = gestureExtractor.error

	fun onSliderValueChange(newSliderValue: Int) {
	}

	fun onSwitchChange() {
	}
}
