package sample.ozawa.open.camera;

import android.view.SurfaceHolder;

public interface MYCamera
{
	public void open();

	public void release();

	public void startPreview();

	public void stopPreview();

	public void setPreviewDisplay( SurfaceHolder holder );

	public float getResolutionWidth();

	public float getResolutionHeight();

	public CameraAttach getCameraAttach();

}
