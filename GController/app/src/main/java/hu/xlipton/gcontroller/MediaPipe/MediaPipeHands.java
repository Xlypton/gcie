/*
 * Author : AdNovum Informatik AG
 */

package hu.xlipton.gcontroller.MediaPipe;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import com.google.mediapipe.formats.proto.LandmarkProto;
import com.google.mediapipe.solutioncore.CameraInput;
import com.google.mediapipe.solutioncore.SolutionGlSurfaceView;
import com.google.mediapipe.solutions.hands.HandLandmark;
import com.google.mediapipe.solutions.hands.Hands;
import com.google.mediapipe.solutions.hands.HandsOptions;
import com.google.mediapipe.solutions.hands.HandsResult;

public class MediaPipeHands {
	private static final String TAG = "MediaPipeHands";

	private Hands hands;

	private final Context context;

	private final GestureExtractor gestureExtractor;

	// Run the pipeline and the model inference on GPU or CPU.
	private static final boolean RUN_ON_GPU = true;

	// Live camera demo UI and camera components.
	private CameraInput cameraInput;
	private SolutionGlSurfaceView<HandsResult> glSurfaceView;

	//TODO 20-Sep-2021/kerip: Add IoC
	public MediaPipeHands(Context context, GestureExtractor gestureExtractor) {
		this.context = context;
		this.gestureExtractor = gestureExtractor;
	}

	/** The core MediaPipe Hands setup workflow for its streaming mode. */
	public SolutionGlSurfaceView<HandsResult> setupStreamingModePipeline() {
		// Initializes a new MediaPipe Hands instance in the streaming mode.
		hands =
				new Hands(
						context,
						HandsOptions.builder()
								.setMode(HandsOptions.STREAMING_MODE)
								.setMaxNumHands(1)
								.setRunOnGpu(RUN_ON_GPU)
								.build());
		hands.setErrorListener((message, e) -> Log.e(TAG, "MediaPipe Hands error:" + message));

		// Initializes a new CameraInput instance and connects it to MediaPipe Hands.
		cameraInput = new CameraInput((Activity) context);
		cameraInput.setNewFrameListener(textureFrame -> hands.send(textureFrame));

		// Initializes a new Gl surface view with a user-defined HandsResultGlRenderer.
		glSurfaceView =
				new SolutionGlSurfaceView<>(context, hands.getGlContext(), hands.getGlMajorVersion());
		glSurfaceView.setSolutionResultRenderer(new HandsResultGlRenderer());
		glSurfaceView.setRenderInputImage(true);
		hands.setResultListener(
				handsResult -> {
					//logWristLandmark(handsResult, /*showPixelValues=*/ false);
					glSurfaceView.setRenderData(handsResult);
					glSurfaceView.requestRender();
					gestureExtractor.calculateSliderValue(handsResult);
				});

		// The runnable to start camera after the gl surface view is attached.
		// For video input source, videoInput.start() will be called when the video uri is available.
		glSurfaceView.post(this::startCamera);


		// Updates the preview layout.
		glSurfaceView.setVisibility(View.VISIBLE);
		return glSurfaceView;
	}

	private void startCamera() {
		cameraInput.start(
				(Activity) context,
				hands.getGlContext(),
				CameraInput.CameraFacing.FRONT,
				glSurfaceView.getWidth(),
				glSurfaceView.getHeight());
	}

	private void stopCurrentPipeline() {
		if (cameraInput != null) {
			cameraInput.setNewFrameListener(null);
			cameraInput.close();
		}
		if (glSurfaceView != null) {
			glSurfaceView.setVisibility(View.GONE);
		}
		if (hands != null) {
			hands.close();
		}
	}

	private void logWristLandmark(HandsResult result, boolean showPixelValues) {
		LandmarkProto.NormalizedLandmark wristLandmark = Hands.getHandLandmark(result, 0, HandLandmark.WRIST);
		// For Bitmaps, show the pixel values. For texture inputs, show the normalized coordinates.
		if (showPixelValues) {
			int width = result.inputBitmap().getWidth();
			int height = result.inputBitmap().getHeight();
			Log.i(
					TAG,
					String.format(
							"MediaPipe Hand wrist coordinates (pixel values): x=%f, y=%f",
							wristLandmark.getX() * width, wristLandmark.getY() * height));
		} else {
			Log.i(
					TAG,
					String.format(
							"MediaPipe Hand wrist normalized coordinates (value range: [0, 1]): x=%f, y=%f",
							wristLandmark.getX(), wristLandmark.getY()));
		}
	}
}
