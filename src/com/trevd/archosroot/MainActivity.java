package com.trevd.archosroot;

import android.app.Activity;
import android.os.Bundle;
import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.view.View;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends Activity
{

	private final static String LOG_TAG = "ARCHOS ROOTER";
	private final static String EXPLOIT_FILENAME = "exploit";
	private final static String KERNEL_FILENAME = "kernel";
	private final static String MINUS_C = "-c";
	private final static String KD_FLASHER = "kd_flasher -i %s -k %s";
	private final static String REBOOT_INTO_SDE = "reboot_into -s sde";
	private final static String REBOOT_RECOVERY = "reboot recovery";
	private final static String ERROR_RESOURCE_NOT_FOUND = "Resource %s [ %d ] Not Found";
	private final static String ERROR_RESOURCE_ID_INVALID = "Resource ID invalid [ %d]";
	private final static String ERROR_RESOURCE_FILENAME_NULL = "Resource Output Filename is null";
	private final static String ERROR_RESOURCE_FILENAME_EMPTY = "Resource Output Filename is empty";
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		if ( extractRawResource(R.raw.exploit,EXPLOIT_FILENAME) == false )
			return ; 
			
		if ( extractRawResource(R.raw.kernel,KERNEL_FILENAME) == false ) 
			return ;
		flashSDEKernel();
		
		
	}
	private void flashSDEKernel(){
		try{
			File exploitfile = getFileStreamPath (EXPLOIT_FILENAME);
			exploitfile.setExecutable(true);
			File kernelfile = getFileStreamPath (KERNEL_FILENAME);
			String kdflasher = String.format(KD_FLASHER,exploitfile.getAbsolutePath(),kernelfile.getAbsolutePath());
			Process process = Runtime.getRuntime().exec(new String[]{exploitfile.getAbsolutePath(),MINUS_C,kdflasher});
			process = Runtime.getRuntime().exec(new String[]{exploitfile.getAbsolutePath(),MINUS_C,REBOOT_INTO_SDE});
			process = Runtime.getRuntime().exec(new String[]{exploitfile.getAbsolutePath(),MINUS_C,REBOOT_RECOVERY});
		}catch ( Exception e ){
			Log.e(LOG_TAG,"Extracting",e);		  
		}
	}
	private boolean extractRawResource(int id,String name){
		String errorString = null ; 
		if(id < 0 )
			errorString = String.format(ERROR_RESOURCE_ID_INVALID,id);
					
		if(name == null )
			errorString = ERROR_RESOURCE_FILENAME_NULL ; 
		else if ( name.length() == 0 ) 
			errorString = ERROR_RESOURCE_FILENAME_NULL ; 
		if ( errorString != null ) {
			Log.e(LOG_TAG,errorString);
			return false ; 
		}
			
		InputStream ins = null;
		
		try{
			ins = getResources().openRawResource (id);
		}catch ( android.content.res.Resources.NotFoundException e ){
			Log.e(LOG_TAG,String.format(ERROR_RESOURCE_NOT_FOUND,name,id),e);
			return false ; 		  
		}
		FileOutputStream fos =null;
		byte[] buffer = null ;
		try{
			buffer = new byte[ins.available()];
			ins.read(buffer);
			ins.close();
			fos = openFileOutput(name, Context.MODE_PRIVATE);
			fos.write(buffer);
			fos.close();
		}catch ( Exception e ){
			Log.e(LOG_TAG,"Extracting",e);		  
			return false; 
		}
		return true;
	}
	
	/** Called when the user clicks the Root button */
	public void rootButtonClicked(View view) {
		
		return ; 

	}

}
