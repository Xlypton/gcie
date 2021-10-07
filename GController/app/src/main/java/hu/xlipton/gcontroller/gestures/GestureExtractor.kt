package hu.xlipton.gcontroller.gestures

import android.util.Log
import android.util.Range
import androidx.lifecycle.MutableLiveData
import autovalue.shaded.com.`google$`.common.primitives.`$UnsignedBytes`.toInt
import com.google.mediapipe.formats.proto.LandmarkProto
import kotlin.math.roundToInt
import com.google.mediapipe.solutions.hands.HandsResult
import hu.xlipton.gcontroller.common.Utils
import java.util.*
import kotlin.math.abs
import kotlin.math.sqrt

class GestureExtractor {

	private var previousSliderResults: Queue<Int> = LinkedList()
	private val sliderQueueLength: Int = 40
	private var previousRotaryKnobResults: Queue<List<LandmarkProto.NormalizedLandmark>> = LinkedList()
	private val rotaryKnobQueueLength: Int = 5
	private var previousSelectResults: Queue<Int> = LinkedList()
	private val selectQueueLength: Int = 40

	val sliderValue = MutableLiveData(0)
	val fixedSliderValues = MutableLiveData("")

	val rotaryKnobValue = MutableLiveData(0f)
	private var previousAngle = 0f
	private var startingVectorX = 0f
	private var startingVectorY = 0f

	val activeControl = MutableLiveData(0)
	private val activeControlsRange = 0..3

	val selectValue = MutableLiveData(mutableListOf(false, false, false, false))

	val switchValue = MutableLiveData(false)

	fun theExtractor(handsResult: HandsResult,){
		val numHands = handsResult.multiHandLandmarks().size

		for (i in 0 until numHands) {
			val handLandmarkList = handsResult.multiHandLandmarks()[i].landmarkList
			when(activeControl.value) {
				1 -> calculateSliderValue(handLandmarkList = handLandmarkList)
				2 -> calculateRotaryKnobValue(handLandmarkList = handLandmarkList)
				3 -> calculateSelect(handLandmarkList = handLandmarkList)
			}

			calculateSwipe(handLandmarkList = handLandmarkList)
		}
	}

	private fun calculateSliderValue(handLandmarkList: MutableList<LandmarkProto.NormalizedLandmark>, ) {
		val thumbTipX = handLandmarkList[4].x
		val thumbTipY = handLandmarkList[4].y
		val indexFingerTipX = handLandmarkList[8].x
		val indexFingerTipY = handLandmarkList[8].y
		val distance = Utils.convertHandsFloatToUsableInt(Utils.calculateDistanceFromCoordinates(thumbTipX, thumbTipY,
			indexFingerTipX, indexFingerTipY))

		var isReady = false

		if (previousSliderResults.count() <  sliderQueueLength) {
			previousSliderResults.add(distance)
		} else {
			previousSliderResults.add(distance)
			previousSliderResults.remove()

			isReady = true
		}


		if (isReady) {
			val currentDistance: Int = previousSliderResults.peek()
			var isSliderValueSet = true

			previousSliderResults.forEach {
				if (abs(it - currentDistance) > 1) {
					isSliderValueSet = false
					return@forEach
				}
			}

			if (isSliderValueSet) {
				fixedSliderValues.postValue("$currentDistance")
				previousSliderResults.clear()
				Log.i(TAG, "sliderValueSet= $currentDistance")
			}
			//Log.i(TAG, "sliderValueNOTset= " + currentDistance)
		}
		//Log.i(TAG, "distance= " + distance);
		sliderValue.postValue(distance)
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
				Log.i(TAG, "StartingVector CLEARED")
			}
		}

		if (input) {
			val derivedVectorX = abs(vectorX - centerX)
			val derivedVectorY = abs(vectorY - centerY)

			if(startingVectorX == 0f && startingVectorY == 0f) {
				previousAngle = rotaryKnobValue.value!!

				startingVectorX = derivedVectorX
				startingVectorY = derivedVectorY
			}

			val angle = Utils.calculateVectorsAngle(startingVectorX, startingVectorY, derivedVectorX, derivedVectorY) + previousAngle
			rotaryKnobValue.postValue(angle)
			Log.i(TAG, "StartingVector: ($startingVectorX, $$startingVectorY) | derivedVector: ($derivedVectorX, " +
					"$derivedVectorY) | Rotation " +
					"angle: $angle")
		}
	}

	private fun calculateSwipe(handLandmarkList: MutableList<LandmarkProto.NormalizedLandmark>) {
		val indexFinger = handLandmarkList[8]
		val middleFinger = handLandmarkList[12]
		val ringFinger = handLandmarkList[16]

		val swipeValues: List<LandmarkProto.NormalizedLandmark> = listOf(indexFinger, middleFinger, ringFinger)

		var isReady = false

		if (previousRotaryKnobResults.count() < rotaryKnobQueueLength) {
			previousRotaryKnobResults.add(swipeValues)
		} else {
			previousRotaryKnobResults.add(swipeValues)
			previousRotaryKnobResults.remove()

			isReady = true
		}

		if (isReady) {
			let breaker@{
				swipeValues.forEach outer@{ currentSwipeValue ->
					previousRotaryKnobResults.peek().forEach { previousSwipeValue ->
						if (currentSwipeValue.y - 0.4f > previousSwipeValue.y && currentSwipeValue.y - 0.6f <
							previousSwipeValue.y ) {
							Log.i("swipe", "DOWN")
							if (activeControl.value != activeControlsRange.last) {
								activeControl.postValue(activeControl.value?.plus(1))
							}
							previousRotaryKnobResults.clear()
							return@breaker

						} else if (currentSwipeValue.y + 0.4f < previousSwipeValue.y && currentSwipeValue.y + 0.6f >
							previousSwipeValue.y	) {
							Log.i("swipe", "UP")
							if (activeControl.value != activeControlsRange.first) {
								activeControl.postValue(activeControl.value?.minus(1))
							}
							previousRotaryKnobResults.clear()
							return@breaker
						}
						if (activeControl.value == 0) {
							if(currentSwipeValue.x - 0.3f > previousSwipeValue.x && currentSwipeValue.x - 0.5f <
								previousSwipeValue.x ) {
								Log.i("swipe", "RIGHT")
								switchValue.postValue(true)
								previousRotaryKnobResults.clear()
								return@breaker

							} else if (currentSwipeValue.x + 0.3f < previousSwipeValue.x && currentSwipeValue.x + 0.5f >
								previousSwipeValue.x	) {
								Log.i("swipe", "LEFT")
								switchValue.postValue(false)
								previousRotaryKnobResults.clear()
								return@breaker
							}
						}
					}
				}
			}
		}
	}

	private fun calculateSelect(handLandmarkList: MutableList<LandmarkProto.NormalizedLandmark>) {
		val indexFingerTipX = handLandmarkList[8].x
		val indexFingerTipY = handLandmarkList[8].y

		var currentSelectValue = 0

		if (indexFingerTipX < 0.5f && indexFingerTipY > 0.5f) {
			currentSelectValue = 1
		} else if (indexFingerTipX > 0.5f && indexFingerTipY > 0.5f) {
			currentSelectValue = 2
		} else if (indexFingerTipX < 0.5f && indexFingerTipY < 0.5f) {
			currentSelectValue = 3
		} else if (indexFingerTipX > 0.5f && indexFingerTipY < 0.5f) {
			currentSelectValue = 4
		}

		var isReady = false

		if (previousSliderResults.count() <  selectQueueLength) {
			previousSliderResults.add(currentSelectValue)
		} else {
			previousSliderResults.add(currentSelectValue)
			previousSliderResults.remove()

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

			if (isSelected) {
				when(currentSelectValue) {


				}
				//selectValue.postValue(currentSelectValue)
				previousSelectResults.clear()
				selectValue.value?.set(0, true)
				Log.i("select", "sliderValueSet= $currentSelectValue")
			}
		}

	}

	companion object {
		private const val TAG = "gestureExtractor"
	}
}