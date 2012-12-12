package sample.ozawa.open.ui;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import sample.ozawa.open.math.Vector2;

public abstract class RectButton
{
	// Field
	private final int BUTTON_WIDTH;
	private final int BUTTON_HEIGHT;
	protected Vector2 mPosition;
	private RectF mBoundingRect;

	public RectButton(
			final int texWidth, final int texHeight,
			final float x, final float y ) {
		// initialize
		BUTTON_WIDTH = texWidth;
		BUTTON_HEIGHT = texHeight;
		mPosition = new Vector2( x, y );
		mBoundingRect = new RectF();
		mBoundingRect.left = mPosition.x;
		mBoundingRect.top = mPosition.y;
		mBoundingRect.right = mPosition.x + BUTTON_WIDTH;
		mBoundingRect.bottom = mPosition.y + BUTTON_HEIGHT;
	}

	public boolean isPressed( float x, float y ) {

		if ( x > mBoundingRect.left &&
				x < mBoundingRect.right &&
				y > mBoundingRect.left &&
				y < mBoundingRect.bottom ) {
			return true;
		}
		return false;
	}

	public abstract void draw( final Canvas c, final Paint p );
}
