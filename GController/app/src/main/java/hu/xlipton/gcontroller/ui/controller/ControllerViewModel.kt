package hu.xlipton.gcontroller.ui.controller

import androidx.lifecycle.ViewModel
import hu.xlipton.gcontroller.gestures.GestureExtractor

class ControllerViewModel : ViewModel() {
	val gestureExtractor: GestureExtractor =
		GestureExtractor(40, 5, 40)

	val activeControl = gestureExtractor.activeControl

	val sliderValue = gestureExtractor.sliderValue

	val fixedSliderValue = gestureExtractor.fixedSliderValue

    val rotaryKnobValue = gestureExtractor.rotaryKnobValue

	val switchState = gestureExtractor.switchValue

	val selectValue = gestureExtractor.selectValue

	fun onSliderValueChange(newSliderValue: Int) {
	}

	fun onSwitchChange() {
	}
}
