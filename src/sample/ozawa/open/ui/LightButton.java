package sample.ozawa.open.ui;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import sample.ozawa.open.R;

public class LightButton
		extends RectButton
{
	private static final int BUTTON_WIDTH = ( int ) ( 100 * 1.5 );
	private static final int BUTTON_HEIGHT = ( int ) ( 50 * 1.5 );
	private static final int OFF = 0;
	private static final int ON = 1;
	private static int mState = OFF;

	private Bitmap mOn = null;
	private Bitmap mOff = null;

	public LightButton( float x, float y, Resources r ) {
		super( BUTTON_WIDTH, BUTTON_HEIGHT, x, y );

		// ライトボタンの画像読み込み。on
		mOn = BitmapFactory.decodeResource(
				r, R.drawable.light_button_on );

		// off
		mOff = BitmapFactory.decodeResource(
				r, R.drawable.light_button_off );
	}

	public void chengeState() {
		if ( mState == OFF ) {
			mState = ON;
		} else {
			mState = OFF;
		}
	}

	@Override
	public void draw( Canvas c, Paint p ) {
		switch ( mState ) {
			case OFF:
				c.drawBitmap( mOff, mPosition.x, mPosition.y, p );
				break;
			case ON:
				c.drawBitmap( mOn, mPosition.x, mPosition.y, p );
				break;
			default:
		}
	}

}
