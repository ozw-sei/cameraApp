package sample.ozawa.open.math;

import android.util.FloatMath;

//疑似構造体
public class Vector2
{
	public float x;
	public float y;

	/**
	 * @param x
	 * 		位置X
	 * @param y
	 * 		位置Y
	 */
	public Vector2( float x, float y ) {
		this.x = x;
		this.y = y;
	}

	/**
	 * @param vec1
	 * 		　点１
	 * @param vec2
	 * 		　点２
	 * @return 二点間の距離を返す
	 */

	public static float distance( final Vector2 vec1, final Vector2 vec2 ) {
		return FloatMath.sqrt( ( vec1.x - vec2.x ) * ( vec1.x - vec2.x )
				+ ( vec1.y - vec2.y ) * ( vec1.y - vec2.y ) );
	}
}
