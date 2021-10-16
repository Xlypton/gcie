package hu.xlipton.gcontroller.gestures

import android.util.Log
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.MutableLiveData
import com.google.mediapipe.formats.proto.LandmarkProto
import com.google.mediapipe.solutions.hands.HandsResult
import hu.xlipton.gcontroller.common.Utils
import java.util.*
import kotlin.math.abs

/** Extract gestures from the given hand landmark results **/
class GestureExtractor(private val sliderQueueLength: Int, private val swipeQueueLength: Int, private val selectQueueLength: Int) {
	private var previousSliderResults: Queue<Int> = LinkedList()
	private var previousSwipeResults: Queue<List<LandmarkProto.NormalizedLandmark>> = LinkedList()
	private var previousSelectResults: Queue<Int> = LinkedList()

	val activeControl: MutableState<Int> = mutableStateOf(0)
	private val activeControlsRange = 0..3

	val sliderValue: MutableState<Int> = mutableStateOf(0)

	val fixedSliderValue: MutableState<Int> = mutableStateOf(0)

	val rotaryKnobValue: MutableState<Float> = mutableStateOf(0f)
	private var previousAngle = 0f
	private var startingVectorX = 0f
	private var startingVectorY = 0f

	val switchValue: MutableState<Boolean> = mutableStateOf(false)

	val selectValue : MutableState<List<Boolean>> = mutableStateOf(listOf(false, false, false, false))

	// Calls that one control function which is selected to be the active control
	fun theExtractor(handsResult: HandsResult){
		val numHands = handsResult.multiHandLandmarks().size

		for (i in 0 until numHands) {
			val handLandmarkList = handsResult.multiHandLandmarks()[i].landmarkList
			when(activeControl.value) {
				1 -> calculateSliderValue(handLandmarkList = handLandmarkList)
				2 -> calculateRotaryKnobValue(handLandmarkList = handLandmarkList)
				3 -> calculateSelect(handLandmarkList = handLandmarkList)
			}

			// Swipe values are always calculated because they are needed to switch active control
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

		// Queuing the previous results so we can compare them
		var isReady = false
		if (previousSliderResults.count() <  sliderQueueLength) {
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
				if (abs(it - currentDistance) > 1) {
					isSliderValueSet = false
					return@forEach
				}
			}

			if (isSliderValueSet) {
				fixedSliderValue.value = currentDistance
				previousSliderResults.clear()
				Log.i(TAG, "sliderValueSet= $currentDistance")
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
				Log.i(TAG, "StartingVector CLEARED")
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

		// Queuing the previous results so we can compare them
		var isReady = false
		if (previousSwipeResults.count() < swipeQueueLength) {
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
						if (currentSwipeValue.y - 0.4f > previousSwipeValue.y && currentSwipeValue.y - 0.6f <
							previousSwipeValue.y ) {
							Log.i("swipe", "DOWN")
							if (activeControl.value != activeControlsRange.last) {
								activeControl.value++
							}
							previousSwipeResults.clear()
							return@breaker

						} else if (currentSwipeValue.y + 0.4f < previousSwipeValue.y && currentSwipeValue.y + 0.6f >
							previousSwipeValue.y	) {
							Log.i("swipe", "UP")
							if (activeControl.value != activeControlsRange.first) {
								activeControl.value--
							}
							previousSwipeResults.clear()
							return@breaker
						}
						if (activeControl.value == 0) {
							if(currentSwipeValue.x - 0.3f > previousSwipeValue.x && currentSwipeValue.x - 0.5f <
								previousSwipeValue.x ) {
								Log.i("swipe", "RIGHT")
								switchValue.value = true
								previousSwipeResults.clear()
								return@breaker

							} else if (currentSwipeValue.x + 0.3f < previousSwipeValue.x && currentSwipeValue.x + 0.5f >
								previousSwipeValue.x	) {
								Log.i("swipe", "LEFT")
								switchValue.value = false
								previousSwipeResults.clear()
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

		// Check if the index finger is on the right part of the screen
		if (indexFingerTipY > 0.6f && indexFingerTipY < 0.8f) {
			if (indexFingerTipX < 0.5f && indexFingerTipY > 0.7f) {
				currentSelectValue = 3
			} else if (indexFingerTipX > 0.5f && indexFingerTipY > 0.7f) {
				currentSelectValue = 4
			} else if (indexFingerTipX < 0.5f && indexFingerTipY < 0.7f) {
				currentSelectValue = 1
			} else if (indexFingerTipX > 0.5f && indexFingerTipY < 0.7f) {
				currentSelectValue = 2
			}

			// Queuing the previous results so we can compare them
			var isReady = false
			if (previousSelectResults.count() < selectQueueLength) {
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
					previousSelectResults.clear()
					Log.i("select", "currentSelectValue= $currentSelectValue")
				}
			}
		}
	}

	companion object {
		private const val TAG = "gestureExtractor"
	}
}