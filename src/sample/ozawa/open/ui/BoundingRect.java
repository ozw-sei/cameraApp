package sample.ozawa.open.ui;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.util.Log;

public class BoundingRect
{
	/*
	   * QRマーカーを追跡する矩形クラス
	   */
	// 画面向き系の定数
	public enum SCREEN_ORIENTATION
	{
		PORTRAIT,
		LAND_SCAPE,
	}

	private final SCREEN_ORIENTATION mScreenOrientation;

	private static final String TAG = "BoundingRect";

	// 表示する矩形の拡大比
	private static float mScaleRate_x = 1;
	private static float mScaleRate_y = 1;

	// スクリーン幅。縦は今使ってないけど、いつか使うかな？
	private static float mScreenWidth = 0;
	private static float mScreenHeight = 0;

	private RectF mBoundRect;

	/**
	 * @param orientation
	 * 		　スクリーンの向き
	 */
	public BoundingRect( final SCREEN_ORIENTATION orientation ) {
		mScreenOrientation = orientation;
	}

	/**
	 * 普通は スクリーン解像度/カメラ解像度
	 *
	 * @param scaleRateX
	 * 		　追跡する矩形の横の拡大比
	 * @param scaleRateY
	 * 		　追跡する矩形の縦の拡大比
	 */
	public void setScaleRate( final float cameraResolutionWidth,
							  final float cameraResolutionHeight, final float screenResolutionWidth,
							  final float screenResolutionHeight ) {
		mScreenHeight = screenResolutionHeight;
		mScreenWidth = screenResolutionWidth;

		mScaleRate_x = mScreenWidth / cameraResolutionHeight;
		mScaleRate_y = mScreenHeight / cameraResolutionWidth;

	}

	/**
	 * @param r
	 * 		追跡する矩形
	 */
	public void setRect( final RectF r ) {
		mBoundRect = r;
	}

	public void draw( final Canvas c, final Paint p ) {
		float left = 0, right = 0, top = 0, bottom = 0;

		// 矩形の輪郭のみ描画
		p.setStyle( Style.STROKE );
		// 矩形の色は赤
		p.setColor( Color.RED );
		// 矩形の輪郭の太さ
		p.setStrokeWidth( 5 );

		switch ( mScreenOrientation ) {
			// 縦画面
			case PORTRAIT:
				// 追跡矩形描画
				left = mScreenWidth - ( mBoundRect.bottom * mScaleRate_x );
				right = mScreenWidth - ( mBoundRect.top * mScaleRate_x );
				top = mBoundRect.left * mScaleRate_y;
				bottom = mBoundRect.right * mScaleRate_y;

				break;
			// 横画面
			case LAND_SCAPE:
				// 追跡矩形描画
				/*
					   * left = mBoundRect.left * mScaleRate_x; right = mBoundRect.right *
					   * mScaleRate_x; top = mBoundRect.top * mScaleRate_y; bottom =
					   * mBoundRect.bottom * mScaleRate_y;
					   */
				break;
		}
		Log.v(
				TAG, "left = " + left + "top = " + top + "right = " + right + "bottom = "
				+ bottom );

		c.drawRect(
				left, top, right, bottom, p );
	}
}
