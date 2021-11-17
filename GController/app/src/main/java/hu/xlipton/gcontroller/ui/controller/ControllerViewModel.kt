package hu.xlipton.gcontroller.ui.controller

import androidx.lifecycle.ViewModel
import hu.xlipton.gcontroller.gestures.GestureExtractor
import hu.xlipton.gcontroller.security.LoginService
import java.util.*

class ControllerViewModel() : ViewModel() {
	val gestureExtractor: GestureExtractor =
		GestureExtractor()

	val activeControl = gestureExtractor.activeControl

	val sliderValue = gestureExtractor.sliderValue

	val fixedSliderValue = gestureExtractor.fixedSliderValue

    val rotaryKnobValue = gestureExtractor.rotaryKnobValue

	val switchValue = gestureExtractor.switchValue

	val selectValue = gestureExtractor.selectValue

	val isServerStarted = gestureExtractor.isServerStarted

	val error = gestureExtractor.error

	val isSliderEnabled = gestureExtractor.doctor || gestureExtractor.admin

	val user = LoginService.user

	val isSwitchEnabled = gestureExtractor.headNurse || gestureExtractor.admin
}
