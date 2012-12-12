package sample.ozawa.open.QR;

import android.graphics.RectF;
import com.google.zxing.Result;
import com.google.zxing.ResultPoint;

public class QRResult
{
	// 結果データの保持用
	private Result mQRResult;
	// マーカーから読み込んだ文字列情報
	private String mMarkerContent;
	// アライメントパターンデータの位置データ
	private RectF mMarkerPosisition;
	// 登録した日付
	private final long mRegisteredDate;

	// コンストラクタ
	public QRResult() {
		mMarkerPosisition = new RectF();
		mRegisteredDate = System.currentTimeMillis();
		mMarkerContent = null;
	}

	public QRResult( Result result ) {
		this.mQRResult = result;
		_initialize( mQRResult );
		mMarkerPosisition = new RectF();
		mRegisteredDate = System.currentTimeMillis();
		mMarkerContent = null;
	}

	public void setResultData( Result result ) {
		this.mQRResult = result;
		_initialize( this.mQRResult );
	}

	public String getMarkerContent() {
		return mMarkerContent;
	}

	public RectF getMarkerPosition() {
		return mMarkerPosisition;
	}

	public long getRegisteredDate() {
		return mRegisteredDate;
	}

	// 初期化
	private void _initialize( Result result ) {
		this._setPosition( result );
		mMarkerContent = result.getText();
	}

	// 位置登録処理
	private void _setPosition( Result result ) {
		ResultPoint[] points = result.getResultPoints();
		// float left,right,top,bottom;
		// 検出結果から位置情報を取得

		//左,右側を取得
		mMarkerPosisition.left = points[ 0 ].getX();
		mMarkerPosisition.right = points[ 0 ].getX();

		for ( int i = 0; i < points.length; i++ ) {
			if ( mMarkerPosisition.left > points[ i ].getX() ) {
				mMarkerPosisition.left = points[ i ].getX();
			}
			if ( mMarkerPosisition.right < points[ i ].getX() ) {
				mMarkerPosisition.right = points[ i ].getX();
			}
		}
		//上下の取得
		mMarkerPosisition.top = points[ 0 ].getY();
		mMarkerPosisition.bottom = points[ 0 ].getY();
		for ( int i = 0; i < points.length; i++ ) {
			//マーカーの下の位置がより大きい時
			if ( mMarkerPosisition.bottom < points[ i ].getY() ) {
				mMarkerPosisition.bottom = points[ i ].getY();
			}
			//マーカーの上がより小さい時
			if ( mMarkerPosisition.top > points[ i ].getY() ) {
				mMarkerPosisition.top = points[ i ].getY();
			}
		}
	}
}
