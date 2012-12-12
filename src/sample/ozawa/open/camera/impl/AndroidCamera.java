package sample.ozawa.open.camera.impl;

import android.hardware.Camera;
import android.os.Handler;
import android.util.Log;
import android.view.SurfaceHolder;
import sample.ozawa.open.camera.CameraAttach;
import sample.ozawa.open.camera.MYCamera;
import sample.ozawa.open.camera.PreviewCB;

import java.io.IOException;
import java.util.List;

public class AndroidCamera
		implements MYCamera,
		Camera.AutoFocusCallback,
		CameraAttach
{
	// タグ
	final String TAG = "CameraController";

	// カメラクラス
	private Camera mCamera;
	private Camera.Size mCameraResolutionSize;

	// PreviewCallBack
	private PreviewCB mPreviewCallBack;

	private static final int mOrientation = 90;

	private static AndroidCamera mInstance;

	private static boolean mRunning = false;

	private Handler mHandler = new Handler();

	// コンストラクタ
	private AndroidCamera() {
		// initialize
		mCamera = null;
		mCameraResolutionSize = null;
	}

	public static MYCamera create() {
		mInstance = new AndroidCamera();
		return mInstance;
	}

	@Override
	public void open() {
		if ( !mRunning ) {

			// カメラを開く
			mCamera = Camera.open();
			Camera.Parameters param = mCamera.getParameters();

			// カメラ取得サイズの設定
			final List<Camera.Size> supportedResolution = param
					.getSupportedPreviewSizes();
			mCameraResolutionSize = supportedResolution.get( 0 );

			// カメラサイズをセット
			param.setPreviewSize(
					mCameraResolutionSize.width,
					mCameraResolutionSize.height );

			// プレビューの角度を設定
			mCamera.setDisplayOrientation( mOrientation );

			mCamera.setParameters( param );
			mRunning = true;
		}
	}

	@Override
	public final void release() {
		if ( mRunning ) {
			// 後始末
			mCamera.setPreviewCallback( null );
			mCamera.autoFocus( null );
			mCamera.stopPreview();
			mCamera.release();
			mCamera = null;
			mRunning = false;
		}
	}

	@Override
	public final void startPreview() {
		// プレビュー開始
		mCamera.startPreview();
	}

	@Override
	public final void setPreviewDisplay( SurfaceHolder holder ) {
		try {
			mCamera.setPreviewDisplay( holder );
		}
		catch ( IOException e ) {
			e.printStackTrace();
		}
	}

	@Override
	public float getResolutionWidth() {
		return mCameraResolutionSize.width;
	}

	@Override
	public float getResolutionHeight() {
		return mCameraResolutionSize.height;
	}

	@Override
	public final void onAutoFocus( boolean success, Camera camera ) {
		// 成功しなかった場合、呼び出す
		mHandler.postDelayed(
				focus, 1200 );
	}

	@Override
	public void stopPreview() {
		mHandler.removeCallbacks( focus );
		mCamera.cancelAutoFocus();
		mCamera.setPreviewCallback( null );
		mCamera.stopPreview();
	}

	@Override
	public void TorchLightOn() {
		Camera.Parameters cameraParam = mCamera.getParameters();

		if ( AndroidCameraSupported.supportedTorchLight( cameraParam ) ) {
			cameraParam.setFlashMode( Camera.Parameters.FLASH_MODE_TORCH );
		} else {
			Log.v(
					TAG, "torchLight not supported!" );
		}
		mCamera.setParameters( cameraParam );

	}

	@Override
	public void TorchLightOff() {
		Camera.Parameters cameraParam = mCamera.getParameters();
		// トーチライトをオフ
		if ( AndroidCameraSupported.supportedTorchLight( cameraParam ) ) {
			cameraParam.setFlashMode( Camera.Parameters.FLASH_MODE_OFF );
		} else {
			Log.v(
					TAG, "torchLight not supported!" );
		}
		mCamera.setParameters( cameraParam );
	}

	@Override
	public void MarkerSarchOn() {
		mPreviewCallBack = new PreviewCB(
				mCameraResolutionSize.width,
				mCameraResolutionSize.height );
		// PreviewCallBackの設定
		mCamera.setPreviewCallback( mPreviewCallBack );
	}

	@Override
	public void MarkerSarchOff() {
		mCamera.setPreviewCallback( null );
	}

	@Override
	public void AutoFocusOn() {
		mCamera.autoFocus( this );
	}

	@Override
	public void AutoFocusOff() {
		mCamera.cancelAutoFocus();
	}

	@Override
	public CameraAttach getCameraAttach() {
		return mInstance;
	}

	private void focus() {
		mCamera.autoFocus( this );
	}

	private Runnable focus = new Runnable()
	{
		@Override
		public void run() {
			focus();
		}
	};
}
