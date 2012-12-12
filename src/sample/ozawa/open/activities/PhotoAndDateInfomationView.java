package sample.ozawa.open.activities;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import sample.ozawa.open.QR.QRFileDataManager;
import sample.ozawa.open.R;
import sample.ozawa.open.math.Vector2;

public class PhotoAndDateInfomationView
		extends Activity
		implements
		OnClickListener
{
	private static final float MAX_SCALE = 2;
	private static final float MIN_SCALE = 0.5f;

	// 戻るボタンのビュー
	private ImageView mBackButtonView;
	// 登録解除ボタンのビュー
	private ImageView mDeleteButtonView;

	// 紐つけされが画像を描画するビュー
	private WebView mLinkedPictureRenderView = null;

	// 起動ロック
	private WakeLock mLock;
	private final String TAG = "PhotoAndDateInfomationViewActivity";

	float SCREEN_WIDTH;
	float SCREEN_HEIGHT;

	Bitmap mPicture;
	// 行列の状態
	private Matrix mNowMatrix = new Matrix();
	// 一つ前の行列の状態
	private Matrix mPrevMatrix = new Matrix();

	// 定数
	enum TOUCH_STATE
	{
		NONE,
		DRAG,
		ZOOM;
	}

	private static TOUCH_STATE mState = TOUCH_STATE.NONE;
	// タッチ開始座標
	private Vector2 mStartPoint = new Vector2( 0, 0 );
	private Vector2 mMidPoint = new Vector2( 0, 0 );
	private float mOldDistance;

	// コンストラクタ
	public PhotoAndDateInfomationView() {
		mLock = null;
	}

	@Override
	public void onCreate( Bundle bundle ) {
		super.onCreate( bundle );
		// 登録された日付を表示するビュー
		TextView registeredDateRenderView = null;

		// 起動ロックをかける
		PowerManager powerManager = ( PowerManager ) getSystemService( Context.POWER_SERVICE );
		mLock = powerManager.newWakeLock(
				PowerManager.FULL_WAKE_LOCK,
				"Activity" );
		mLock.acquire();

		// 端末の画面解像度を取得
		WindowManager windowManager = getWindowManager();
		Display display = windowManager.getDefaultDisplay();
		DisplayMetrics displayMetrics = new DisplayMetrics();
		display.getMetrics( displayMetrics );

		SCREEN_WIDTH = display.getWidth();
		SCREEN_HEIGHT = display.getHeight();

		// ビューのレイアウト
		LinearLayout layout = new LinearLayout( this );
		layout.setOrientation( LinearLayout.VERTICAL );
		layout.setLayoutParams( new LayoutParams(
				LinearLayout.LayoutParams.FILL_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT ) );

		// 背景色を青に指定
		layout.setBackgroundColor( Color.rgb(
				0, 0, 255 ) );

		// 配置するビューが中央に行くように指定
		layout.setGravity( Gravity.CENTER_HORIZONTAL );

		// layoutParamの初期化終了
		setContentView( layout );
		Log.v(
				TAG, "SetLayoutParam" );

		// 登録した日付を表示するTextViewを生成
		registeredDateRenderView = new TextView( this );
		// registeredDateRenderView.setText( "登録した日付を表示" );
		registeredDateRenderView.setLayoutParams( new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT ) );

		// 日付をセット
		QRFileDataManager manager = new QRFileDataManager();
		Log.v(
				TAG, "SetDateInfomation" );

		manager.load();
		String registeredDate = manager.getRegistrationDate();
		registeredDateRenderView.setText( "登録した日付 = " + registeredDate );

		// ビューに追加
		layout.addView( registeredDateRenderView );

		// ImageViewの生成
		mLinkedPictureRenderView = new WebView( this );
		mLinkedPictureRenderView.setLayoutParams( new LinearLayout.LayoutParams(
				( int ) SCREEN_WIDTH, LayoutParams.FILL_PARENT ) );
		// 画像のパスを取得
		String path = manager.getGalleryPath();

		// 拡大縮小に対応
		mLinkedPictureRenderView.getSettings().setBuiltInZoomControls(
				true );

		// 画像をセット
		mLinkedPictureRenderView.loadUrl( "file://" + path );


		// ビューに追加
		layout.addView( mLinkedPictureRenderView );
		Log.v(
				TAG, "SetPictureView" );

		LinearLayout buttonLinearLayout = new LinearLayout( this );
		buttonLinearLayout.setOrientation( LinearLayout.HORIZONTAL );
		buttonLinearLayout.setGravity( Gravity.BOTTOM );

		// 登録解除ボタン
		mDeleteButtonView = new ImageView( this );
		Resources cancelResource = mDeleteButtonView.getContext()
				.getResources();
		Bitmap cancelButton = BitmapFactory.decodeResource(
				cancelResource,
				R.drawable.deletebutton );
		mDeleteButtonView.setImageBitmap( cancelButton );
		mDeleteButtonView.setOnClickListener( this );
		buttonLinearLayout.setLayoutParams( new LinearLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT ) );

		buttonLinearLayout.addView( mDeleteButtonView );

		// 前画面へ遷移するボタン
		mBackButtonView = new ImageView( this );
		Resources backButtonResource = mBackButtonView.getContext()
				.getResources();
		Bitmap backButton = BitmapFactory.decodeResource(
				backButtonResource,
				R.drawable.back_button );
		mBackButtonView.setImageBitmap( backButton );
		mBackButtonView.setOnClickListener( this );

		mBackButtonView.setLayoutParams( new LinearLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT ) );

		buttonLinearLayout.addView( mBackButtonView );
		layout.addView( buttonLinearLayout );
	}

	@Override
	public void onResume() {
		super.onResume();
		mLock.acquire();
	}

	@Override
	public void onPause() {
		super.onPause();
		mLock.release();
		mLock = null;
	}

	@Override
	public void onClick( View v ) {

		// 戻るボタン
		if ( v == mBackButtonView ) {
			Log.v(
					TAG, "BackButtonClicked" );
			// 前画面へ遷移
			finish();
		}
		// 登録削除ボタン
		if ( v == mDeleteButtonView ) {
			Log.v(
					TAG, "DeleteButtonClicked" );
			QRFileDataManager.fileDelete();
			finish();
		}
	}
	/*
	   * private float filter(Matrix m, float s) { final float[] values = new
	   * float[9]; m.getValues( values ); final float nextScale = values[0] * s; if(
	   * nextScale > MAX_SCALE ) { s = MAX_SCALE / values[0]; } else if( nextScale <
	   * MIN_SCALE ) { s = MIN_SCALE / values[0]; } return s; }
	   *
	   *
	   * // ピンチ操作 private OnTouchListener listener = new OnTouchListener() {
	   *
	   * @Override public boolean onTouch(View v, MotionEvent e) { switch(
	   * e.getAction() & MotionEvent.ACTION_MASK ) { // ドラッグ開始 case
	   * MotionEvent.ACTION_DOWN: mState = TOUCH_STATE.DRAG; mStartPoint.x =
	   * e.getX(); mStartPoint.y = e.getY(); mPrevMatrix.set( mNowMatrix ); break;
	   *
	   * case MotionEvent.ACTION_MOVE:
	   *
	   * if( mState == TOUCH_STATE.DRAG ) { mNowMatrix.set( mPrevMatrix ); final
	   * float x = e.getX() - mStartPoint.x; final float y = e.getY() -
	   * mStartPoint.y; Log.v( TAG, "Drag x =" +x +"y = " +y );
	   *
	   * mNowMatrix.postTranslate( x, y ); } else if( mState == TOUCH_STATE.ZOOM ) {
	   * final float newDist = culcDistance( e ); // final float scaleRate = newDist
	   * / mOldDistance; final float scaleRate = filter( mNowMatrix, newDist /
	   * mOldDistance ); if( scaleRate > 0.5 ) { mNowMatrix.set( mPrevMatrix );
	   * mNowMatrix.postScale( scaleRate, scaleRate, mMidPoint.x, mMidPoint.y ); }
	   *
	   * } break;
	   *
	   * // タッチ終了 case MotionEvent.ACTION_UP: mState = TOUCH_STATE.NONE; break;
	   *
	   * // ズーム開始 case MotionEvent.ACTION_POINTER_DOWN: mState = TOUCH_STATE.ZOOM;
	   * mOldDistance = culcDistance( e ); culcMidPoint( mMidPoint, e ); break; //
	   * マルチタッチ終了 case MotionEvent.ACTION_POINTER_UP: mState = TOUCH_STATE.NONE;
	   * break; default: break; }
	   *
	   * // mLinkedPictureRenderView.setImageMatrix( mNowMatrix ); return false; }
	   *
	   * private float culcDistance(final MotionEvent e) { final float x = e.getX( 0
	   * ) - e.getX( 1 ); final float y = e.getY( 0 ) - e.getY( 1 ); return
	   * FloatMath.sqrt( x * x - y * y ); }
	   *
	   * // 中点を求める private void culcMidPoint(Vector2 outVector,final MotionEvent e)
	   * { outVector.x = ( e.getX( 0 ) + e.getX( 1 ) ) / 2; outVector.y = ( e.getY(
	   * 0 ) + e.getY( 1 ) ) / 2; } };
	   */

}
