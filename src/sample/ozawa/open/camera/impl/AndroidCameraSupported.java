package sample.ozawa.open.camera.impl;

import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.util.Log;

import java.util.List;

public class AndroidCameraSupported
{
	private final static String TAG = "CameraSupported";

	//フォーカスモードが対応してるかどうかを調査する関数
	public static boolean supportedFocusMode( Camera.Parameters param ) {
		try {
			List<String> focusMode = param.getSupportedFocusModes();
			if ( focusMode.contains( Parameters.FOCUS_MODE_MACRO ) ) {
				return true;
			}
		}
		catch ( Exception e ) {
			e.printStackTrace();
		}
		return false;
	}

	public static boolean supportedTorchLight( Camera.Parameters param ) {
		try {
			//サポートしてるなら、trueをかえす
			List<String> flashMode = param.getSupportedFlashModes();
			if ( flashMode.contains( Parameters.FLASH_MODE_TORCH ) ) {
				return true;
			}
		}
		catch ( Exception e ) {
			//サポートしてない
			Log.v( TAG, "torchLight is not supported!!" );
			e.printStackTrace();
		}
		return false;
	}
}
