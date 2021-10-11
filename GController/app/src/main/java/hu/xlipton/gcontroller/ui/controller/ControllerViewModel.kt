package hu.xlipton.gcontroller.ui.controller

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import hu.xlipton.gcontroller.gestures.GestureExtractor

class ControllerViewModel : ViewModel() {
	val gestureExtractor: GestureExtractor =
		GestureExtractor(40, 5, 30)

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
