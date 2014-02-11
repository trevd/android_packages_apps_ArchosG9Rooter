package com.trevd.installsde;

import android.app.Activity;
import android.os.Bundle;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.AssetManager;
import android.util.Log;
import android.view.View;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import com.trevd.installsde.Root;
public class MainActivity extends Activity
{

	private final static String LOG_TAG = "ARCHOS ROOTER";
	private final static String REBOOT_INTO_SDE = "reboot_into -s sde";
	private final static String REBOOT_RECOVERY = "reboot recovery";
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
	}
	/** Called when the user clicks the Root button */
	public void rootButtonClicked(View view) {
		
		Root.getroot();
		Root.write_sde_image(getAssets());
		try{
			Process process = Runtime.getRuntime().exec(REBOOT_INTO_SDE);
			process = Runtime.getRuntime().exec(REBOOT_RECOVERY);
		}catch(IOException e){
			e.printStackTrace();
		}
		return ; 

	}

}
