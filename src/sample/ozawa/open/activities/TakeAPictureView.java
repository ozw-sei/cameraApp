package sample.ozawa.open.activities;

import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.os.Bundle;
import android.os.Environment;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.*;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Toast;
import sample.ozawa.open.QR.QRFileDataManager;
import sample.ozawa.open.QR.QRReader;
import sample.ozawa.open.QR.QRResult;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class TakeAPictureView
		extends Activity
		implements SurfaceHolder.Callback, ShutterCallback, PictureCallback,
		OnClickListener
{
	// ////////////////Field///////////////////////////
	private final String TAG = "TakeAPictureView";
	// カメラデバイス
	private Camera mCamera;
	// プレビュー画像
	private SurfaceView mPreview;
	// 写真撮るボタン
	private Button mSnapShotButton;
	// 戻るボタン
	private Button mCancelButton;
	// 起動ロック
	private WakeLock mLock;
	// カメラデバイスを確保しているか否かのフラグ
	private boolean mCameraRunning = true;
	// 現在の日付
	private final String TODAY_DATE = String.valueOf( DateFormat.format(
			"yyyy-MM-dd kk.mm.ss", System.currentTimeMillis() ) );

	// ////////////////ActivityMethod//////////
	@Override
	public void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );
		Log.v(
				TAG, "onCreate" );
		// 画面生成
		mPreview = new SurfaceView( this );

		// SurfaceHolderCallBackを追加
		mPreview.getHolder().addCallback(
				this );
		mPreview.getHolder().setType(
				SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS );

		// カメラ生成
		mCamera = Camera.open();

		// レイアウト設定
		LinearLayout layout = new LinearLayout( this );
		layout.setOrientation( LinearLayout.HORIZONTAL );
		layout.setGravity( Gravity.CENTER_HORIZONTAL );

		// 写真撮影ボタン
		mSnapShotButton = new Button( this );
		mSnapShotButton.setText( "Take a Picture" );
		mSnapShotButton.setOnClickListener( this );
		layout.addView( mSnapShotButton );

		// キャンセルボタン
		mCancelButton = new Button( this );
		mCancelButton.setText( "Back" );
		mCancelButton.setOnClickListener( this );
		layout.addView( mCancelButton );

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

		// プレビュー画面をセット
		setContentView( mPreview );
		// スナップショットボタンをオーバーレイ
		addContentView(
				layout, new LinearLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT ) );

	}

	@Override
	public void onResume() {
		super.onResume();
		Log.v(
				TAG, "onResume" );
		mLock.acquire();
		if ( !mCameraRunning ) {
			mCamera = Camera.open();
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		Log.v(
				TAG, "onPause" );
		if ( mCameraRunning ) {
			mCamera.stopPreview();
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.v(
				TAG, "onDestroy" );
		if ( mCameraRunning ) {
			this._finalize();
		}
	}

	// ///////////////SurfaceHolderCallBackMethod/////////////////////////////////
	@Override
	public void surfaceChanged( SurfaceHolder holder, int format, int width,
								int height ) {
		// カメラ解像度を取得
		Camera.Parameters params = mCamera.getParameters();
		List<Camera.Size> sizes = params.getSupportedPreviewSizes();
		Camera.Size selected = sizes.get( 0 );
		params.setPreviewSize(
				selected.width, selected.height );
		mCamera.setParameters( params );
		// 縦持ちに設定
		mCamera.setDisplayOrientation( 90 );

		// プレビュー開始
		mCamera.startPreview();
		mCameraRunning = true;
	}

	@Override
	public void surfaceCreated( SurfaceHolder holder ) {
		// プレビューを表示するディスプレイを指定
		try {
			mCamera.setPreviewDisplay( mPreview.getHolder() );
		}
		catch ( IOException e ) {
			e.printStackTrace();
		}
	}

	@Override
	public void surfaceDestroyed( SurfaceHolder holder ) {
		//blank
	}

	// ////////////////PictureCallBackMethod/////////////////
	@Override
	public void onPictureTaken( byte[] data, Camera camera ) {
		Log.v( TAG, "onPictureTakenCall" );
		// SDカードのdataフォルダに保存
		String path = Environment.getExternalStorageDirectory() +
				"/data/" + System.currentTimeMillis() + ".jpg"; // 撮影した画像はストレージへ保存する.

		try {
			// 出力ストリーム
			FileOutputStream out = new FileOutputStream( path );
			// 書き出し
			out.write( data );
			out.flush();
			out.close();

			// 撮影が終了したので、ファイルに登録情報を書き込む。
			QRResult result = QRReader.getQRResult();
			/*
			QRFileDataManager.save(
					Long.toString( QRReader.decode( result.getMarkerContent() ) ), path, TODAY_DATE );
					*/
			QRFileDataManager.save(
					result.getMarkerContent(), path, TODAY_DATE );
			Log.v( TAG, "onPictureTakenCall1" );


		}
		catch ( FileNotFoundException e ) {
			e.printStackTrace();
			Log.v( TAG, "onPictureTakenCall2" );

		}
		catch ( IOException e ) {
			e.printStackTrace();
			Log.v( TAG, "onPictureTakenCall3" );

		}
		// 終了処理
		if ( mCameraRunning ) {
			_finalize();
		}
		// 前の画面へ戻る
		finish();

	}

	// ////////////////ShutterCallBackMethod//////////////////
	@Override
	public void onShutter() {
		Toast.makeText(
				this, "Click!", Toast.LENGTH_SHORT ).show();
	}

	// /////////////////OnClickListener////////////////////////
	@Override
	public void onClick( View v ) {
		// クリックされたボタンがキャンセルボタンだった場合、カメラ処理を終了し、前画面へ遷移
		if ( v == mCancelButton ) {
			_finalize();
			finish();
		}
		// 写真を撮るボタンだった場合、写真を撮り、それを書き込み、前画面へ遷移
		if ( v == mSnapShotButton ) {
			mCamera.takePicture(
					this, null, null, this );

		}
	}

	// ////////////////PrivateMethod////////////////////////////
	private void _finalize() {
		mCamera.stopPreview();
		mCamera.release();
		mCamera = null;
		mCameraRunning = false;
		mLock.release();
		mLock = null;
	}
}
