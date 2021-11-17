package hu.xlipton.gcontroller.gestures

import android.util.Log
import androidx.compose.runtime.*
import com.google.mediapipe.formats.proto.LandmarkProto
import com.google.mediapipe.solutions.hands.HandsResult
import hu.xlipton.control.api.ControlApi
import hu.xlipton.control.model.ControlData
import hu.xlipton.gcontroller.common.Utils
import hu.xlipton.gcontroller.security.LoginService
import kotlinx.coroutines.*
import java.lang.Exception
import java.math.MathContext
import java.util.*
import kotlin.math.PI
import kotlin.math.abs

object GestureExtractorConstants {
	const val serverIp = "http://gdevice:8000"
	const val serverRequestDelay = 1000L

	const val sliderQueueLength = 40
	const val sliderResult = 0

	const val swipeQueueLength = 4
	const val verticalSwipeLowerBound = 0.5f
	const val verticalSwipeUpperBound = 0.7f
	const val horizontalSwipeLowerBound = 0.3f
	const val horizontalSwipeUpperBound = 0.5f

	const val selectQueueLength = 40
	const val selectPositionLowerBound = 0.7f
	const val selectPositionUpperBound = 0.9f
	const val selectVerticalDivider = 0.5f
	const val selectHorizontalDivider = 0.8f

	const val pushQueueLength = 3
	const val pushValue = 20
}

/** Extract gestures from the given hand landmark results **/
class GestureExtractor {
	private val controlApiService: ControlApi = ControlApi(GestureExtractorConstants.serverIp)
	private val ioScope = CoroutineScope(Dispatchers.IO + Job() )

	private var previousSliderResults: Queue<Int> = LinkedList()
	private var previousSwipeResults: Queue<List<LandmarkProto.NormalizedLandmark>> = LinkedList()
	private var previousSelectResults: Queue<Int> = LinkedList()
	private var previousPushResults: Queue<Int> = LinkedList()

	val activeControl: MutableState<Int> = mutableStateOf(1)
	private val activeControlsRange = 0..4

	val isServerStarted: MutableState<Boolean> = mutableStateOf(false)

	val sliderValue: MutableState<Int> = mutableStateOf(0)
	var sliderValueToSend = 0

	val fixedSliderValue: MutableState<Int> = mutableStateOf(0)

	val rotaryKnobValue: MutableState<Float> = mutableStateOf(0f)
	var rotaryKnobValueToSend = 0f

	private var previousAngle = 0f
	private var startingVectorX = 0f
	private var startingVectorY = 0f

	val switchValue: MutableState<Boolean> = mutableStateOf(false)
	var switchValueToSend = false

	val selectValue : MutableState<List<Boolean>> = mutableStateOf(listOf(false, false, false, false))
	var selectValueToSend = mutableListOf<Boolean>(false, false, false, false)

	val error: MutableState<String> = mutableStateOf("")

	private fun updateGDevice(){
		try {
			ioScope.launch {
				while (isActive) {
					controlApiService.addControls(
						ControlData(
							switch = switchValueToSend,
							slider = sliderValueToSend,
							rotaryknob = ((rotaryKnobValueToSend * 180 / PI).toFloat()),
							select = selectValueToSend,
						)
					)
					delay(GestureExtractorConstants.serverRequestDelay)
				}
			}
		} catch (e: Exception) {
			Log.e("network", "Error: ${e.message}")
			error.value = e.message.toString()
		}
	}

	val nurse: Boolean = LoginService.user.roles.find { it == "nurse" } != null
	val headNurse: Boolean = LoginService.user.roles.find { it == "head_nurse" } != null
	val doctor: Boolean = LoginService.user.roles.find { it == "doctor" } != null
	val admin: Boolean = LoginService.user.userName == "xlipton"

	// Calls that one control function which is selected to be the active control
	fun theExtractor(handsResult: HandsResult){
		val numHands = handsResult.multiHandLandmarks().size

		for (i in 0 until numHands) {
			val handLandmarkList = handsResult.multiHandLandmarks()[i].landmarkList
			when(activeControl.value) {
				0 -> calculatePush(handLandmarkList = handLandmarkList)
				2 -> if (doctor || admin) calculateSliderValue(handLandmarkList = handLandmarkList)
				3 -> calculateRotaryKnobValue(handLandmarkList = handLandmarkList)
				4 -> calculateSelect(handLandmarkList = handLandmarkList)
			}

			// Swipe values are always calculated because they are needed to switch active control
			calculateSwipe(handLandmarkList = handLandmarkList)
		}
	}

	private fun calculatePush(handLandmarkList: MutableList<LandmarkProto.NormalizedLandmark>) {
		val palmBottom = handLandmarkList[0]
		val palmTop = handLandmarkList[5]

		val pushValue = Utils.convertHandsFloatToUsableInt(Utils.calculateDistanceFromCoordinates(palmBottom.x, palmBottom.y,
			palmTop.x, palmTop.y))
		//Log.i("push", "distance: $pushValue")

		// Queuing the previous results so we can compare them
		var isReady = false
		if (previousPushResults.count() < GestureExtractorConstants.pushQueueLength) {
			previousPushResults.add(pushValue)
		} else {
			previousPushResults.add(pushValue)
			previousPushResults.remove()

			isReady = true
		}

		if (isReady) {
			val currentPushValue = previousPushResults.peek()

			previousPushResults.forEach {
				//Log.i("push", "distance from queue" + (it - currentPushValue))
				if (abs(it - currentPushValue) > GestureExtractorConstants.pushValue) {
					if (!isServerStarted.value) {
						this.updateGDevice()
						Log.i("network", "Connecting to server...")
					} else {
						if (this.ioScope.isActive) this.ioScope.cancel()
						Log.i("network", "Successfully disconnected from server!")
					}

					isServerStarted.value = !isServerStarted.value
					previousPushResults.clear()

					return@forEach
				}
			}
		}
	}

	private fun calculateSliderValue(handLandmarkList: MutableList<LandmarkProto.NormalizedLandmark>, ) {
		val thumbTipX = handLandmarkList[4].x
		val thumbTipY = handLandmarkList[4].y
		val indexFingerTipX = handLandmarkList[8].x
		val indexFingerTipY = handLandmarkList[8].y
		val distance = Utils.convertHandsFloatToUsableInt(Utils.calculateDistanceFromCoordinates(thumbTipX, thumbTipY,
			indexFingerTipX, indexFingerTipY))

		// Queuing the previous results so we can compare them
		var isReady = false
		if (previousSliderResults.count() <  GestureExtractorConstants.sliderQueueLength) {
			previousSliderResults.add(distance)
		} else {
			previousSliderResults.add(distance)
			previousSliderResults.remove()

			isReady = true
		}

		// If the queue is ready we iterate through it and check the distance of the two finger
		if (isReady) {
			val currentDistance: Int = previousSliderResults.peek()
			var isSliderValueSet = true

			previousSliderResults.forEach {
				if (abs(it - currentDistance) > GestureExtractorConstants.sliderResult) {
					isSliderValueSet = false
					return@forEach
				}
			}

			if (isSliderValueSet) {
				fixedSliderValue.value = currentDistance
				sliderValueToSend = currentDistance
				previousSliderResults.clear()
				//Log.i(TAG, "sliderValueSet= $currentDistance")
			}
		}
		sliderValue.value = distance
	}

	private fun calculateRotaryKnobValue(handLandmarkList: MutableList<LandmarkProto.NormalizedLandmark>) {
		val centerX = handLandmarkList[0].x
		val centerY = handLandmarkList[0].y

		val vectorX = handLandmarkList[5].x
		val vectorY = handLandmarkList[5].y

		var input = true

		for (i in 20 downTo 8 step 4) {
			val distalPah = Utils.calculateDistanceFromCoordinates(handLandmarkList[i].x, handLandmarkList[i].y, centerX, centerY)
			val itermediatePah = Utils.calculateDistanceFromCoordinates(handLandmarkList[i - 1].x, handLandmarkList[i - 1].y,
				centerX,
				centerY)
			if (distalPah > itermediatePah) {
				input = false
				startingVectorX = 0f
				startingVectorY = 0f
				//Log.i(TAG, "StartingVector CLEARED")
			}
		}

		if (input) {
			val derivedVectorX = abs(vectorX - centerX)
			val derivedVectorY = abs(vectorY - centerY)

			if(startingVectorX == 0f && startingVectorY == 0f) {
				previousAngle = rotaryKnobValue.value

				startingVectorX = derivedVectorX
				startingVectorY = derivedVectorY
			}

			val angle = Utils.calculateVectorsAngle(startingVectorX, startingVectorY, derivedVectorX, derivedVectorY) + previousAngle
			rotaryKnobValue.value = angle
			rotaryKnobValueToSend = angle
			/*
			Log.i(TAG, "StartingVector: ($startingVectorX, $$startingVectorY) | derivedVector: ($derivedVectorX, " +
					"$derivedVectorY) | Rotation " +
					"angle: $angle")
			 */
		}
	}

	private fun calculateSwipe(handLandmarkList: MutableList<LandmarkProto.NormalizedLandmark>) {
		val indexFinger = handLandmarkList[8]
		val middleFinger = handLandmarkList[12]
		val ringFinger = handLandmarkList[16]

		val swipeValues: List<LandmarkProto.NormalizedLandmark> = listOf(indexFinger, middleFinger, ringFinger)

		// Queuing the previous results so we can compare them
		var isReady = false
		if (previousSwipeResults.count() < GestureExtractorConstants.swipeQueueLength) {
			previousSwipeResults.add(swipeValues)
		} else {
			previousSwipeResults.add(swipeValues)
			previousSwipeResults.remove()

			isReady = true
		}

		// If the queue is ready we iterate through it and check the direction and the distance of the hand movement
		if (isReady) {
			let breaker@{
				swipeValues.forEach outer@{ currentSwipeValue ->
					previousSwipeResults.peek().forEach { previousSwipeValue ->
						if (calculateVerticalSwipe(currentSwipeValue, previousSwipeValue)) return@breaker

						if (activeControl.value == 1 && (headNurse || admin)) {
							if (calculateHorizontalSwipe(currentSwipeValue, previousSwipeValue)) return@breaker
						}
					}
				}
			}
		}
	}

	private fun calculateHorizontalSwipe(currentSwipeValue: LandmarkProto.NormalizedLandmark,
									   previousSwipeValue: LandmarkProto.NormalizedLandmark) : Boolean {
		if(currentSwipeValue.x - GestureExtractorConstants.horizontalSwipeLowerBound > previousSwipeValue.x &&
			currentSwipeValue.x - GestureExtractorConstants.horizontalSwipeUpperBound < previousSwipeValue.x) {
			Log.i("swipe", "RIGHT")
			switchValue.value = true
			switchValueToSend = true
			previousSwipeResults.clear()
			return true

		} else if (currentSwipeValue.x + GestureExtractorConstants.horizontalSwipeLowerBound < previousSwipeValue.x &&
			currentSwipeValue.x + GestureExtractorConstants.horizontalSwipeUpperBound > previousSwipeValue.x) {
			Log.i("swipe", "LEFT")
			switchValue.value = false
			switchValueToSend = false
			previousSwipeResults.clear()
			return true
		}

		return false
	}

	private fun calculateVerticalSwipe(currentSwipeValue: LandmarkProto.NormalizedLandmark,
									   previousSwipeValue: LandmarkProto.NormalizedLandmark) : Boolean{
		if (currentSwipeValue.y - GestureExtractorConstants.verticalSwipeLowerBound > previousSwipeValue.y &&
			currentSwipeValue.y - GestureExtractorConstants.verticalSwipeUpperBound < previousSwipeValue.y) {
			Log.i("swipe", "DOWN")
			if (activeControl.value != activeControlsRange.last) {
				activeControl.value++
			}
			previousSwipeResults.clear()
			return true

		} else if (currentSwipeValue.y + GestureExtractorConstants.verticalSwipeLowerBound  < previousSwipeValue.y &&
			currentSwipeValue.y + GestureExtractorConstants.verticalSwipeUpperBound  > previousSwipeValue.y) {
			Log.i("swipe", "UP")
			if (activeControl.value != activeControlsRange.first) {
				activeControl.value--
			}
			previousSwipeResults.clear()
			return true
		}

		return false
	}

	private fun calculateSelect(handLandmarkList: MutableList<LandmarkProto.NormalizedLandmark>) {
		val indexFingerTipX = handLandmarkList[8].x
		val indexFingerTipY = handLandmarkList[8].y

		var currentSelectValue = 0

		// Check if the index finger is on the right part of the screen
		if (indexFingerTipY > GestureExtractorConstants.selectPositionLowerBound &&
			indexFingerTipY < GestureExtractorConstants.selectPositionUpperBound) {

			if (indexFingerTipX < GestureExtractorConstants.selectVerticalDivider &&
				indexFingerTipY > GestureExtractorConstants.selectHorizontalDivider) {

				currentSelectValue = 3

			} else if (indexFingerTipX > GestureExtractorConstants.selectVerticalDivider &&
				indexFingerTipY > GestureExtractorConstants.selectHorizontalDivider) {

				currentSelectValue = 4

			} else if (indexFingerTipX < GestureExtractorConstants.selectVerticalDivider &&
				indexFingerTipY < GestureExtractorConstants.selectHorizontalDivider) {

				currentSelectValue = 1

			} else if (indexFingerTipX > GestureExtractorConstants.selectVerticalDivider &&
				indexFingerTipY < GestureExtractorConstants.selectHorizontalDivider) {

				currentSelectValue = 2

			}

			// Queuing the previous results so we can compare them
			var isReady = false
			if (previousSelectResults.count() < GestureExtractorConstants.selectQueueLength) {
				previousSelectResults.add(currentSelectValue)
			} else {
				previousSelectResults.add(currentSelectValue)
				previousSelectResults.remove()

				isReady = true
			}

			var isSelected = true

			if (isReady) {
				previousSelectResults.forEach {
					if (it != currentSelectValue) {
						isSelected = false
						return@forEach
					}
				}

				// If the filed is selected we update the corresponding list item (List<> needs to be used instead of
				// MutableList<> because the limitation of compose)
				if (isSelected) {
					selectValue.value = selectValue.value.toMutableList().also {
						it[currentSelectValue - 1] = !it[currentSelectValue - 1]
					}
					selectValueToSend[currentSelectValue - 1]  = !selectValueToSend[currentSelectValue - 1]

					previousSelectResults.clear()

					//Log.i("select", "currentSelectValue= $currentSelectValue")
				}
			}
		}
	}

	companion object {
		private const val TAG = "gestureExtractor"
	}
}