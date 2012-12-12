package sample.ozawa.open.ui;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import sample.ozawa.open.R;

public class InfomationButton extends RectButton
{
	private static final int TEX_WIDTH = ( int ) ( 100 * 1.5f );
	private static final int TEX_HEIGHT = ( int ) ( 50 * 1.5f );
	private Bitmap mTexture = null;

	public InfomationButton( final float x, final float y, final Resources r ) {
		super( TEX_WIDTH, TEX_HEIGHT, x, y );
		mTexture = BitmapFactory.decodeResource(
				r, R.drawable.infomationbutton );
	}

	@Override
	public void draw( Canvas c, Paint p ) {
		c.drawBitmap( mTexture, mPosition.x, mPosition.y, p );
	}
}
