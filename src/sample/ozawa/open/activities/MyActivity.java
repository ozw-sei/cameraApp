package sample.ozawa.open.activities;

import android.app.Activity;
import android.os.Bundle;
import sample.ozawa.open.R;

public class MyActivity extends Activity
{
	/** Called when the activity is first created. */
	@Override
	public void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );
		setContentView( R.layout.main );
	}
}
