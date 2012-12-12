package sample.ozawa.open.activities;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;
import sample.ozawa.open.LinkedPicture;
import sample.ozawa.open.QR.QRFileDataManager;
import sample.ozawa.open.QR.QRReader;
import sample.ozawa.open.math.Vector2;
import sample.ozawa.open.ui.*;

//AR関連のディレクタ
public class ARView
		extends View
{
	// 定数
	enum MARKER_STATE
	{
		// マーカーを発見！
		FOUND,
		// マーカーを発見できず
		NOT_FOUND,
		// マーカーのコンテンツと登録してるコンテンツの中身が一致
		CONTENT_MATCH,
		// マーカーのコンテンツと登録してるコンテンツの中身が不一致
		CONTENT_NOT_MATCH,
	}

	// スクリーン解像度
	private final Vector2 mScreenResolution;
	// カメラ解像度
	private final Vector2 mCameraResolution;

	// 現在のマーカーの状態
	private static MARKER_STATE mState = MARKER_STATE.NOT_FOUND;

	// スタータクラス
	private final FirstScreen mFirstScreen;
	// TAG
	// private static final String TAG = "ARView";

	// 処理開始時間
	private static long mStartTime = 0;
	// 処理経過時間
	private static float mTickTime = 0;

	// マーカー追跡矩形のインスタンス
	private BoundingRect mBoundingRect;

	// 登録された画像
	private LinkedPicture mLinkedPicture;

	// 各ボタンオブジェクト
	private LightButton mlightButton;
	private DeleteButton mDeleteButton;
	private CameraButton mCameraButton;
	private FilerButton mFilerButton;
	private InfomationButton mInfoButton;

	// 読み込みデータ管理クラス
	private QRFileDataManager mManager;

	// 状態管理
	private static MARKER_STATE mPrevState = MARKER_STATE.NOT_FOUND;
	private static MARKER_STATE mPrev2State = MARKER_STATE.NOT_FOUND;

	// 登録してあるコンテンツ
	private String mRegisteredContent = null;

	// コンストラクタ
	public ARView( FirstScreen starterClass ) {
		super( starterClass );

		Resources res = this.getContext().getResources();

		mFirstScreen = starterClass;

		// スクリーン解像度
		mScreenResolution = new Vector2( mFirstScreen.getScreenResolutionWidth(),
				mFirstScreen.getScreenResolutionHeight() );
		// カメラ解像度
		mCameraResolution = new Vector2( mFirstScreen.getCameraResolutionWidth(),
				mFirstScreen.getCameraResolutonHeight() );

		// とりあえず、未発見に初期化
		mState = MARKER_STATE.NOT_FOUND;

		// マーカ追跡矩形
		mBoundingRect = new BoundingRect( BoundingRect.SCREEN_ORIENTATION.PORTRAIT );

		// 登録したテクスチャ
		mLinkedPicture = new LinkedPicture( ( int ) mScreenResolution.x,
				( int ) mScreenResolution.y );

		// カメラボタン
		mCameraButton = new CameraButton(
				mScreenResolution.x / 2 + 100,
				mScreenResolution.y - 50
				, res );

		// ファイラを開くボタン
		mFilerButton = new FilerButton(
				( mScreenResolution.x / 2 ) - 100,
				mScreenResolution.y - 50, res );

		// 削除ボタン
		mDeleteButton = new DeleteButton(
				( mScreenResolution.x / 2 ) + 100,
				( mScreenResolution.y ) - 50, res );

		// インフォメーションボタン
		// 画面の中心から左に100ピクセル移動した場所
		mInfoButton = new InfomationButton(
				( mScreenResolution.x / 2 ) - 100,
				mScreenResolution.y - 50, res );

		// ライトボタン
		mlightButton = new LightButton(
				( mScreenResolution.x - 100 ), 50, res );
	}

	public void onResume() {
		mManager = new QRFileDataManager();
		try {
			// ファイルに書き出されているデータの読み込み
			mManager.load();
			mRegisteredContent = mManager.getFileMarkerContent();
		}
		catch ( Exception e ) {
			e.printStackTrace();
			mRegisteredContent = null;
		}
	}

	public void onPause() {
		// いつか入れる
	}

	// //////////////////////////////////////////////////////////
	@Override
	public void onDraw( Canvas canvas ) {
		super.onDraw( canvas );
		mStartTime = System.nanoTime();
		Paint paint = new Paint();

		// 描画
		mlightButton.draw(
				canvas, paint );
		// マーカーが以前見つかった状態だったのならば、待機する
		if ( mState != MARKER_STATE.CONTENT_MATCH &&
				mPrevState == MARKER_STATE.CONTENT_MATCH ) {
			while ( true ) {
				float deltaTime = ( System.nanoTime() - mStartTime ) / 1000000000.0f;
				mTickTime += deltaTime;
				mStartTime = System.nanoTime();
				if ( mTickTime < 0.5 ) {
					break;
				}
			}
			mTickTime = 0;
		}
		// マーカーが見つかったかどうかの検査
		mState = QRReader.isFound() ? MARKER_STATE.FOUND
				: MARKER_STATE.NOT_FOUND;

		if ( mPrev2State != MARKER_STATE.NOT_FOUND &&
				mPrevState != MARKER_STATE.NOT_FOUND &&
				mState == MARKER_STATE.NOT_FOUND ) {
			mState = MARKER_STATE.FOUND;
			mPrevState = MARKER_STATE.NOT_FOUND;
		}

		// マーカーが見つかった！
		if ( mState != MARKER_STATE.NOT_FOUND ) {
			// マーカーの位置
			final RectF pos = QRReader.getQRResult().getMarkerPosition();

			// 位置をセット
			mBoundingRect.setRect( pos );

			// カメラ解像度とスクリーン解像度の拡大比率を投げる
			mBoundingRect.setScaleRate(
					mCameraResolution.x, mCameraResolution.y,
					mScreenResolution.x, mScreenResolution.y );
			// レンダリング
			mBoundingRect.draw(
					canvas, paint );

			// QRのコンテンツと登録してあるコンテンツが一致したかどうか
			mState = mManager.checkMarkerData( mRegisteredContent ) ?
					MARKER_STATE.CONTENT_MATCH : MARKER_STATE.CONTENT_NOT_MATCH;

			// 登録したマーカーではなかった！
			if ( mState == MARKER_STATE.CONTENT_NOT_MATCH ) {
				mCameraButton.draw(
						canvas, paint );

				mFilerButton.draw(
						canvas, paint );
			}
			// 一致した時はギャラリー画像を表示する
			if ( mState == MARKER_STATE.CONTENT_MATCH ) {
				// ギャラリー画像のパスを取得
				final String path = mManager.getGalleryPath();
				// 初期化
				mLinkedPicture.initialize(
						path, mManager.getGalleryOrientation() );
				mLinkedPicture.draw(
						canvas, paint );
				mInfoButton.draw(
						canvas, paint );
				mDeleteButton.draw(
						canvas, paint );

				mStartTime = System.nanoTime();
			} // 一致しなかった時

		}

		mPrev2State = mPrevState;
		mPrevState = mState;
		// 再描画
		invalidate();
	}

	@Override
	public boolean onTouchEvent( MotionEvent e ) {
		Log.v(
				"onTouchEvent", "onTouchEvent" );
		if ( e.getAction() == MotionEvent.ACTION_DOWN ) {
			// タッチ位置をベクトルに変換
			switch ( mState ) {
				// マーカーと登録コンテンツが一致している時
				case CONTENT_MATCH:
					// タッチ検出
					if ( mDeleteButton.isPressed(
							e.getX(), e.getY() ) ) {

						// ファイル削除
						QRFileDataManager.fileDelete();
						Toast.makeText(
								mFirstScreen, "登録データを削除しました。", Toast.LENGTH_LONG ).show();
						if ( mRegisteredContent.isEmpty() == false ) {
							mRegisteredContent = null;
						}

						mState = MARKER_STATE.NOT_FOUND;
					}
					// インフォメーションボタンのクリックを検出
					if ( mInfoButton.isPressed(
							e.getX(), e.getY() ) ) {
						mState = MARKER_STATE.NOT_FOUND;
						// 画像と登録した日付を表示する画面へ遷移
						mFirstScreen.transitionScreen(
								PhotoAndDateInfomationView.class );
					}
					break;
				case CONTENT_NOT_MATCH:
					// カメラボタンの当たり判定（半径）
					if ( mCameraButton.isPressed(
							e.getX(), e.getY() ) ) {
						mState = MARKER_STATE.NOT_FOUND;
						// 画面遷移
						// スナップモード
						mFirstScreen.transitionScreen( TakeAPictureView.class );

					}
					// ファイラボタンの当たり判定
					if ( mFilerButton.isPressed(
							e.getX(), e.getY() ) ) {
						mFirstScreen.openFiler();

						mState = MARKER_STATE.NOT_FOUND;
					}
					break;
				case NOT_FOUND:
					break;
				case FOUND:
					break;
			}
			// ライトボタンのタッチ検出
			if ( mlightButton.isPressed(
					e.getX(), e.getY() ) ) {
				mlightButton.chengeState();
				mFirstScreen.changeToachLightState();
			}
		}
		return false;
	}
}
