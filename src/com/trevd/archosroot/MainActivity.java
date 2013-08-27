package com.trevd.archosroot;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import java.io.File;
import java.io.IOException;


public class MainActivity extends Activity
{

	private final static String LOG_TAG = "ARCHOS ROOTER";
	private final static String WORLD_WRITABLE_FILE = "/data/misc/smb/fusesmb.conf";
	private final static String LOCAL_PROP_FILE = "/data/local.prop";
	private final static String LINK_SHELL_COMMAND = "ln -s /data/local.prop /data/misc/smb/fusesmb.conf";
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
	}

	/** Called when the user clicks the Root button */
	public void rootButtonClicked(View view) {
		// Do something in response to button
		boolean tryDelete = false;
		boolean tryLink = false;
		boolean linkExists = false;

		File file = new File(WORLD_WRITABLE_FILE);
		if(file == null){
			Log.e(LOG_TAG,String.format("Cannot create file object for %s",WORLD_WRITABLE_FILE));
			return ;
		}

		Log.i(LOG_TAG,String.format("file.getAbsolutePath()=%s",file.getAbsolutePath())); 
		if ( file.exists() == true ) {
			Log.i(LOG_TAG,String.format("%s Found",WORLD_WRITABLE_FILE));
			tryDelete = true;
			tryLink = true;
		}else{
			Log.i(LOG_TAG,String.format("%s Not Found",WORLD_WRITABLE_FILE));
			tryDelete = false;
			tryLink = true;
		}

		// 
		if(tryDelete == true){
		   if(file.delete()){
		      Log.i(LOG_TAG,String.format("%s Deleted",WORLD_WRITABLE_FILE));
		      tryLink = true ;
		   }else{
		     Log.e(LOG_TAG,String.format("Could not %s Delete",WORLD_WRITABLE_FILE));
		     tryLink = false ;
		  }				
		}

		if(tryLink == true){
		    // Shell out to create a symlink because java is shit and can't abstract 
		    // a filesystem properly 
		    try{
		        Process p = Runtime.getRuntime().exec(new String[]{"ln", "-s", LOCAL_PROP_FILE, WORLD_WRITABLE_FILE});
		        p = null ;
	            }catch ( IOException e ){
	   	          Log.e(LOG_TAG,"Error While Symlinking",e);		  
		    }
		}
		file = null ; 
		return ; 

	}

}
