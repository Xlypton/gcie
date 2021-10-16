package hu.xlipton.gcontroller.gestures.mediapipe;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import com.google.mediapipe.formats.proto.LandmarkProto;
import com.google.mediapipe.solutioncore.CameraInput;
import com.google.mediapipe.solutions.hands.HandLandmark;
import com.google.mediapipe.solutions.hands.Hands;
import com.google.mediapipe.solutions.hands.HandsOptions;
import com.google.mediapipe.solutions.hands.HandsResult;
import hu.xlipton.gcontroller.gestures.GestureExtractor;

public class MediaPipeHands {
	private static final String TAG = "MediaPipeHands";

	private Hands hands;

	private final Context context;

	private final GestureExtractor gestureExtractor;

	// Run the pipeline and the model inference on GPU or CPU.
	private static final boolean RUN_ON_GPU = true;

	// Live camera demo UI and camera components with the custom GlSurfaceView implementation
	private CameraInput cameraInput;
	private CustomGlSurfaceView<HandsResult> glSurfaceView;

	public MediaPipeHands(Context context, GestureExtractor gestureExtractor) {
		this.context = context;
		this.gestureExtractor = gestureExtractor;
	}

	/** The core MediaPipe Hands setup workflow for its streaming mode. */
	public CustomGlSurfaceView<HandsResult> setupStreamingModePipeline() {
		// Initializes a new MediaPipe Hands instance in the streaming mode.
		hands =
				new Hands(
						context,
						HandsOptions.builder()
								.setStaticImageMode(false)
								.setMaxNumHands(1)
								.setRunOnGpu(RUN_ON_GPU)
								.build());
		hands.setErrorListener((message, e) -> Log.e(TAG, "MediaPipe Hands error:" + message));

		// Initializes a new CameraInput instance and connects it to MediaPipe Hands.
		cameraInput = new CameraInput((Activity) context);
		cameraInput.setNewFrameListener(textureFrame -> hands.send(textureFrame));

		// Initializes a new Gl surface view with a user-defined HandsResultGlRenderer.
		glSurfaceView =
				new CustomGlSurfaceView<>(context, hands.getGlContext(), hands.getGlMajorVersion());
		glSurfaceView.setSolutionResultRenderer(new hu.xlipton.gcontroller.gestures.mediapipe.HandsResultGlRenderer());
		glSurfaceView.setRenderInputImage(false);
		hands.setResultListener(
				handsResult -> {
					//logWristLandmark(handsResult, /*showPixelValues=*/ false);
					glSurfaceView.setRenderData(handsResult);
					glSurfaceView.requestRender();

					gestureExtractor.theExtractor(handsResult);
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
