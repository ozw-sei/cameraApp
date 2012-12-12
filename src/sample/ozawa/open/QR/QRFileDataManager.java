package sample.ozawa.open.QR;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Environment;
import android.util.Log;

import java.io.*;

public class QRFileDataManager
{
	// QRコードの中に入っている文字列
	private String mMarkerContent = null;
	// 紐つけされてる画像のパス
	private String mPath = null;
	// ひもつけ画像の向き
	private String mOrientation = null;
	// 登録さえた日付
	private String mDate = null;
	// SDカードのパス
	private static final String mExternalStoragePath = Environment
			.getExternalStorageDirectory().getAbsolutePath()
			+ File.separator;

	public void load() {
		BufferedReader in = null;
		try {
			in = new BufferedReader( new InputStreamReader( readFile( ".Picup!" ) ) );
			// 登録されてる画像の数

			// 登録されてるコンテンツが画像か動画か

			// マーカーの中身コンテンツ読み込み
			mMarkerContent = in.readLine();
			// マーカーとひもつけされてる画像のパス
			mPath = in.readLine();
			// マーカーと画像が紐つけされた日付
			mDate = in.readLine();
			// 画像の向き
			mOrientation = in.readLine();
		}
		catch ( IOException e ) {
			e.printStackTrace();
		}
		catch ( NumberFormatException e ) {
			e.printStackTrace();
		}
		finally {
			try {
				if ( in != null ) {
					in.close();
				}
			}
			catch ( IOException e ) {
				e.printStackTrace();
			}
		}
	}

	public static void fileDelete() {
		// このパスのファイルを読み込み
		final File file = new File( mExternalStoragePath + ".Picup!" );
		try {
			// ファイルがあったらtrueを返す
			if ( file.exists() ) {
				Log.v(
						"File", "ファイルがあります" );
				// ファイルを削除できたら、trueを返す
				if ( file.delete() ) {
					Log.v(
							"File", "削除しました" );
				} else {
					Log.v(
							"File", "削除できませんでした" );
				}
			} else {
				Log.v(
						"File", "ファイルがありませんでした。" );
			}
		}
		catch ( Exception e ) {
			e.printStackTrace();
		}

	}

	public static void save(
			final String qrContent, final String linkedPicturePath,
			final String registeredDate ) {
		BufferedWriter out = null;
		try {
			out = new BufferedWriter( new OutputStreamWriter( writeFile( ".Picup!" ) ) );
			// QRコンテンツ書き出し
			out.write( qrContent + "\n" );
			// QRコンテンツと紐つけの画像のパスを書き出し
			out.write( linkedPicturePath + "\n" );
			// 登録した日付を書き出し
			out.write( registeredDate + "\n" );

			ExifInterface exifInterface = null;
			String orientation = null;
			try {
				exifInterface = new ExifInterface( linkedPicturePath );
				orientation = exifInterface
						.getAttribute( ExifInterface.TAG_ORIENTATION );
			}
			catch ( IOException e ) {
				e.printStackTrace();
			}
			if ( orientation == null ) {
				// new Exception();
				return;
			}
			if ( orientation.equals( "6" ) ) {
				out.write( "portrait" + "\n" );
			} else {
				out.write( "landScape" + "\n" );
			}

		}
		catch ( IOException e ) {
			e.printStackTrace();
		}
		finally {
			try {
				if ( out != null ) {
					out.close();
				}
			}
			catch ( IOException e ) {
				//blank
			}
		}
	}

	public String getFileMarkerContent() {
		return mMarkerContent;
	}

	public String getGalleryPath() {
		return mPath;
	}

	public String getRegistrationDate() {
		return mDate;
	}

	public String getGalleryOrientation() {
		return mOrientation;
	}

	public boolean checkMarkerData( final String markerContent ) {
		try {
			Log.v(
					"QRFileDataManager", "read code is " + markerContent );
			if ( mMarkerContent.equalsIgnoreCase( markerContent ) ) {
				// 照合した結果、合致した場合
				return true;
			}
		}
		catch ( Exception e ) {
			e.printStackTrace();
		}
		// 合致しなかった場合
		return false;
	}

	// 渡されたパスからギャラリー画像をつってきて、返す。
	// スクリーン幅が必要なため、それを引数
	public static Bitmap pathToGalleryPicture( final String path,
											   final int screenWidth,
											   final int screenHeight,
											   final String orientation ) {
		BitmapFactory.Options options;
		try {

			// 画像サイズの取得
			options = new BitmapFactory.Options();
			// trueにすると画像の変換を行わずにファイルサイズを取得できる
			options.inJustDecodeBounds = true;

			// ファイルを取得
			BitmapFactory.decodeFile(
					path, options );
			// 計数を計算
			int scaleWidth = options.outWidth / screenWidth + 1;
			int scaleHeight = options.outHeight / screenHeight + 1;
			int scale = Math.max(
					scaleWidth, scaleHeight );

			// 画像読み込み
			options = new BitmapFactory.Options();
			// リサイズするためにfalse
			options.inJustDecodeBounds = false;
			options.inSampleSize = scale;

			// Bitmapを生成
			Bitmap bmp = BitmapFactory.decodeFile(
					path, options );
			if ( orientation.equals( "portrait" ) ) {
				Matrix m = new Matrix();
				m.setRotate( 90 );
				bmp = Bitmap.createBitmap(
						bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), m, true );
			}
			// リサイズしたBitmapを返す
			return bmp;
		}
		catch ( Exception e ) {
			return null;
		}
	}

	private static InputStream readFile( String fileName ) throws IOException {
		return new FileInputStream( mExternalStoragePath + fileName );
	}

	private static OutputStream writeFile( String fileName ) throws IOException {
		return new FileOutputStream( mExternalStoragePath + fileName );
	}

}
