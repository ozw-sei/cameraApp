package sample.ozawa.open.activities;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.*;
import android.view.ViewGroup.LayoutParams;
import sample.ozawa.open.QR.QRFileDataManager;
import sample.ozawa.open.QR.QRReader;
import sample.ozawa.open.QR.QRResult;
import sample.ozawa.open.camera.CameraAttach;
import sample.ozawa.open.camera.MYCamera;
import sample.ozawa.open.camera.impl.AndroidCamera;

public class FirstScreen
		extends Activity
		implements SurfaceHolder.Callback
{
	// デバッグタグ
	private final String TAG = "StarterClass";
	// ギャラリーインテントの定数
	private static final int REQUEST_GALLERY = 0;
	// スクリーン解像度変数
	private static float mScreenResolutionWidth;
	private static float mScreenResolutionHeight;

	// カメラ解像度
	private static float mCameraResolutionWidth;
	private static float mCameraResolutionHeight;

	// 起動ロック
	private WakeLock mLock;

	// カメラインターフェース
	private MYCamera mCamera;
	// カメラプレビューをレンダリングするビュー
	private SurfaceView mPreview;

	// ARView
	ARView mARView;

	// カメラが現在走っているかどうか
	private static boolean mCameraRunning;

	@Override
	public void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );
		Log.v(
				TAG, "onCreate" );

		mCameraRunning = true;

		// 生成
		mCamera = AndroidCamera.create();
		mPreview = new SurfaceView( this );

		// 端末の画面解像度を取得
		WindowManager windowManager = getWindowManager();
		Display display = windowManager.getDefaultDisplay();
		DisplayMetrics displayMetrics = new DisplayMetrics();
		display.getMetrics( displayMetrics );

		// 起動ロックをかける
		PowerManager manager = ( PowerManager ) getSystemService( Context.POWER_SERVICE );
		mLock = manager.newWakeLock(
				PowerManager.FULL_WAKE_LOCK, "Activity" );
		mLock.acquire();

		// Windowをフルスクリーンに設定
		getWindow().addFlags(
				WindowManager.LayoutParams.FLAG_FULLSCREEN );
		// タイトル非表示
		requestWindowFeature( Window.FEATURE_NO_TITLE );

		// 画面を作る
		mPreview.getHolder().setType(
				SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS );
		mPreview.getHolder().addCallback(
				this );

		// 定数に代入
		mScreenResolutionWidth = display.getWidth();
		mScreenResolutionHeight = display.getHeight();

		// カメラをつくる
		mCamera.open();

		// カメラ解像度初期化
		mCameraResolutionWidth = mCamera.getResolutionWidth();
		mCameraResolutionHeight = mCamera.getResolutionHeight();
		Log.v(
				TAG, "mScreenResolutionWidth = " + mScreenResolutionWidth
				+ "mScreenResolutionHeight = " + mScreenResolutionHeight );
		Log.v(
				TAG, "mCameraResolutionWidth = " + mCameraResolutionWidth +
				"mCameraResolutionHeight = " + mCameraResolutionHeight );

		// ビューを設定
		setContentView( mPreview );

		mARView = new ARView( this );

		// オーバーレイビューを設定
		addContentView(
				mARView, new LayoutParams( LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT ) );

	}

	@Override
	public void onResume() {
		super.onResume();
		Log.v(
				TAG, "onResume" );
		mLock.acquire();
		mARView.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
		Log.v(
				TAG, "onPause" );
		mLock.release();
		mARView.onPause();

		if ( mCameraRunning ) {
			mCamera.stopPreview();
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.v(
				TAG, "onDestroy" );
		mLock = null;
		if ( mCameraRunning ) {
			mCamera.release();
		}
	}

	/**
	 * 画面の遷移
	 *
	 * @param classes
	 * 		遷移先のActivity
	 */

	public void transitionScreen( Class<?> classes ) {
		Intent intent = new Intent( this, classes );
		releaseCamera();
		startActivity( intent );
	}

	public final float getScreenResolutionWidth() {
		return mScreenResolutionWidth;
	}

	public final float getScreenResolutionHeight() {
		return mScreenResolutionHeight;
	}

	public final float getCameraResolutionWidth() {
		return mCameraResolutionWidth;
	}

	public float getCameraResolutonHeight() {
		return mCameraResolutionHeight;
	}

	public void openFiler() {
		mCamera.stopPreview();
		mCameraRunning = false;

		Intent intent = new Intent();
		intent.setType( "image/*" );
		intent.setAction( Intent.ACTION_GET_CONTENT );
		startActivityForResult(
				intent, REQUEST_GALLERY );

		resume();
	}

	/** カメラを閉じる */
	public void releaseCamera() {
		if ( mCameraRunning ) {
			mCamera.stopPreview();
			mCamera.release();
			mCameraRunning = false;
		}
	}

	private static boolean mLightState = false;

	/** トーチライトのステートを変更 */
	public void changeToachLightState() {
		Log.v(
				TAG, "touchFocus( ) call" );
		CameraAttach attach = mCamera.getCameraAttach();
		if ( mLightState ) {
			mLightState = false;
			attach.TorchLightOff();
		} else {
			mLightState = true;
			attach.TorchLightOn();
		}
	}

	@Override
	public void surfaceChanged( SurfaceHolder holder, int format, int width,
								int height ) {
		mCamera.startPreview();
		mCameraRunning = true;
	}

	/** カメラの動作再開 */
	private void resume() {
		if ( !mCameraRunning ) {
			mCamera.open();
			mCameraRunning = true;
		}
		mCamera.setPreviewDisplay( mPreview.getHolder() );
		// パラメータの初期化
		CameraAttach attach = mCamera.getCameraAttach();
		attach.AutoFocusOn();
		attach.MarkerSarchOn();
	}

	@Override
	public void surfaceCreated( SurfaceHolder holder ) {
		resume();
	}

	@Override
	public void surfaceDestroyed( SurfaceHolder holder ) {
		// blank
	}

	@Override
	protected void onActivityResult( int requestCode, int resultCode, Intent data ) {
		super.onActivityResult(
				requestCode, resultCode, data );
		Log.v(
				TAG, "onActivityResult" + resultCode );

		if ( resultCode == RESULT_CANCELED ) {
			resume();
			mCamera.startPreview();
			return;
		}
		Uri uri = null;
		if ( requestCode == REQUEST_GALLERY &&
				resultCode == RESULT_OK ) {
			uri = data.getData();
		} else {
			return;
		}
		if ( uri == null ) {
			return;
		}

		// コンテンツプロパイダからギャラリーのパスを取得
		final ContentResolver cr = getContentResolver();
		// 画像データのみに絞り込み
		final String[] columns = { MediaStore.Images.Media.DATA };
		final Cursor c = cr.query(
				uri, columns, null, null, null );
		c.moveToFirst();
		// パスを取得
		final String path = c.getString( 0 );

		// 現在の日付
		final String today = String.valueOf( DateFormat.format(
				"yyyy-MM-dd kk.mm.ss", System.currentTimeMillis() ) );

		final QRResult result = QRReader.getQRResult();
/*
		final String markerContent =
				Long.toString( QRReader.decode(
						result.getMarkerContent() ) );
						*/
		final String markerContent = result.getMarkerContent();
		Log.v(
				TAG, "markerContent = " + markerContent );

		// エラーだったら-1が帰ってくる
		if ( markerContent.equalsIgnoreCase( "-1" ) ) {
			return;
		}

		QRFileDataManager.save(
				markerContent,
				path, today );
	}
}
