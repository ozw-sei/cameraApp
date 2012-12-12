package sample.ozawa.open;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import sample.ozawa.open.QR.QRFileDataManager;
import sample.ozawa.open.math.Vector2;

public class LinkedPicture
{
	// Bitmap本体
	private Bitmap mPicture = null;
	// Bitmapのパス
	private String mPicturePath = null;
	// 画面の向き
	private String mOrientation = null;

	private final int ImageWidth;
	private final int ImageHeight;

	private final int ScreenWidth;
	private final int ScreenHeight;

	private Vector2 mPosition = new Vector2( 0, 0 );

	// コンストラクタ
	public LinkedPicture( final int screenWidth, final int screenHeight ) {

		ScreenWidth = screenWidth;
		ScreenHeight = screenHeight;
		ImageWidth = screenWidth / 2;
		ImageHeight = screenHeight / 2;
	}

	public void initialize( final String path, final String orientation ) {
		initTextureOrientation( orientation );
		initPicturePath( path );
	}

	private void initPicturePath( final String path ) {
		mPicturePath = path;
		// パスからギャラリー画像のBMPを生成
		mPicture = QRFileDataManager.pathToGalleryPicture(
				mPicturePath,
				ImageWidth, ImageHeight, mOrientation );
	}

	private int initTextureOrientation( final String orientation ) {
//    if( mOrientation == null )
//      return -1;
		mOrientation = orientation;
		return 0;
	}

	public void draw( final Canvas c, final Paint p ) {

		// マーカーが合致したときに描画する画像の位置
		mPosition.x = ( ScreenWidth - mPicture.getWidth() ) / 2;
		mPosition.y = ScreenHeight / 2 - ( ScreenHeight / 4 );

		// マーカーと紐つけられている画像を描画
		c.drawBitmap(
				mPicture, mPosition.x,
				mPosition.y, p );
	}
}
