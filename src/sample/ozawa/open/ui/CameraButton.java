package sample.ozawa.open.ui;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import sample.ozawa.open.R;

public class CameraButton
		extends RectButton
{
	private static final int BUTTON_WIDTH = ( int ) ( 100 * 1.5 );
	private static final int BUTTON_HEIGHT = ( int ) ( 50 * 1.5 );
	private Bitmap mTexture = null;

	public CameraButton( final float x, final float y, Resources r ) {
		super( BUTTON_WIDTH, BUTTON_HEIGHT, x, y );
		mTexture = BitmapFactory.decodeResource(
				r, R.drawable.camera );
	}

	@Override
	public void draw( Canvas c, Paint p ) {
		c.drawBitmap( mTexture, mPosition.x, mPosition.y, p );
	}
}
