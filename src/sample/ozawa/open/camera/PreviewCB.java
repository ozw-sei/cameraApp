package sample.ozawa.open.camera;

import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.util.Log;
import sample.ozawa.open.QR.QRReader;

public class PreviewCB
		implements PreviewCallback
{
	//カメラ解像度
	private final int CAMERA_RESOLUTION_WIDTH;
	private final int CAMERA_RESOLUTION_HEIGHT;
	private int sarchPictureWidth;
	private int sarchPictureHeight;

	static String TAG = "PreviewCallback";

	private QRReader reader = new QRReader();

	public PreviewCB( final int cameraResolutionWidth,
					  final int cameraResolutionHeight ) {
		CAMERA_RESOLUTION_WIDTH = cameraResolutionWidth;
		CAMERA_RESOLUTION_HEIGHT = cameraResolutionHeight;

		sarchPictureWidth = cameraResolutionWidth;
		sarchPictureHeight = cameraResolutionHeight;
	}


	@Override
	public final void onPreviewFrame( byte[] data, Camera camera ) {
		// プレビューコールバックが呼ばれました。
		Log.v(
				TAG, "PreviewFrame_Call" );
		try {
			//取り込んだ画像の読み込み
			reader.readYUV(
					data, CAMERA_RESOLUTION_WIDTH, CAMERA_RESOLUTION_HEIGHT, 0, 0,
					sarchPictureWidth, sarchPictureHeight, false );
		}
		catch ( Exception e ) {
			// Bitmapの生成に失敗
			Log.v(
					TAG, "error in readYUV" );
		}
	}

	public void sarchPictureSize( final int width, final int height ) {
		sarchPictureWidth = width;
		sarchPictureHeight = height;
	}
}
