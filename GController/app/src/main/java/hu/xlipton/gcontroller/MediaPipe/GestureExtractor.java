/*
 * Author : AdNovum Informatik AG
 */

package hu.xlipton.gcontroller.MediaPipe;

import java.util.List;

import androidx.lifecycle.MutableLiveData;
import com.google.mediapipe.formats.proto.LandmarkProto;
import com.google.mediapipe.solutions.hands.HandsResult;

public class GestureExtractor {
	private static final String TAG = "GestureExtractor";

	public MutableLiveData<Float> sliderValue = new MutableLiveData<>(0f);

	public void calculateSliderValue(HandsResult handsResult) {
		int numHands = handsResult.multiHandLandmarks().size();
		for (int i = 0; i < numHands; ++i) {
			List<LandmarkProto.NormalizedLandmark> handLandmarkList = handsResult.multiHandLandmarks().get(i).getLandmarkList();
			float thumbTipX = handLandmarkList.get(4).getX();
			float thumbTipY = handLandmarkList.get(4).getY();

			float indexFingerTipX = handLandmarkList.get(8).getX();
			float indexFingerTipY = handLandmarkList.get(8).getY();

			float distance = (float) Math
					.sqrt((thumbTipX - indexFingerTipX) * (thumbTipX - indexFingerTipX) + (thumbTipY - indexFingerTipY) * (
							thumbTipY - indexFingerTipY)) * 100f;

			//Log.i(TAG, "distance= " + distance);

			this.sliderValue.postValue(distance);
		}
	}

}
