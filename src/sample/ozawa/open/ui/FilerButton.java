package sample.ozawa.open.ui;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import sample.ozawa.open.R;

public class FilerButton extends RectButton
{
	//ピクセル数
	private static final int TEX_WIDTH = ( int ) ( 100 * 1.5 );
	private static final int TEX_HEIGHT = ( int ) ( 50 * 1.5 );
	private Bitmap mTexture = null;

	public FilerButton( float x, float y, Resources r ) {
		super( TEX_WIDTH, TEX_HEIGHT, x, y );
		mTexture = BitmapFactory.decodeResource(
				r, R.drawable.filer );
	}

	@Override
	public void draw( Canvas c, Paint p ) {
		c.drawBitmap( mTexture, mPosition.x, mPosition.y, p );
	}
}

