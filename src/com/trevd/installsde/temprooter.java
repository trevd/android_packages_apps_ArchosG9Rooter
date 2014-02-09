package com.trevd.archosroot;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import java.nio.CharBuffer;
import java.io.File;
import java.io.Reader;
import java.io.FileReader;
import java.io.IOException;
import java.io.FileNotFoundException;


public class temprooter extends BroadcastReceiver {

	private final static String LOG_TAG = "ARCHOS ROOTER";
	private final static String LOCAL_PROP_FILE_CONTENTS = "ro.kernel.qemu=1";
	private final static String LOCAL_PROP_FILE = "/data/local.prop";

	private String readFromFileReader(Reader fr){
	

		if(fr == null ){
			Log.e(LOG_TAG,String.format("Cannot create file reader object for %s",LOCAL_PROP_FILE));
			return "";
		}
		CharBuffer cb = CharBuffer.allocate(17);
		
		try{
			/*if(fr.ready() == false ){
					Log.e(LOG_TAG,String.format("file reader not ready to read %s",LOCAL_PROP_FILE));
					return "";
			}*/
			
			fr.read(cb) ;
			fr.close();
			Log.i(LOG_TAG,String.format("cb.length %d",cb.toString().trim().length()));
		}catch ( IOException e ){
			Log.wtf(LOG_TAG,String.format("Shit! Where the fuck did %s go",LOCAL_PROP_FILE));
		}
		return cb.toString().trim();
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		if (!("android.intent.action.BOOT_COMPLETED".equals(intent.getAction()))) {
			//Log.v(LOG_TAG, "Broadcast Received - Wrong Station");
			return ; 
		}
		// Boot Completed - Check if the local.prop exists and it's writable

		boolean checkWrite = false; 
		
		File file = new File(LOCAL_PROP_FILE);
		if(file == null){
			Log.e(LOG_TAG,String.format("Cannot create file object for %s",LOCAL_PROP_FILE));
			return ;
		}
		// Considering we are checking the file exists before reading it - Why? when FileReader
		// will throw an not found exception..... Exceptions are expensive and a file not found
		// is just not exceptional enough to warrant a performance penalty of up 70 times.
		if ( file.exists() == true ) {
			Log.i(LOG_TAG,String.format("%s Found",LOCAL_PROP_FILE));

			try{
				
				FileReader fr = new FileReader(LOCAL_PROP_FILE);
				String contents = readFromFileReader(fr);
				fr = null ;
				if(contents.isEmpty() == true){
					Log.i(LOG_TAG,String.format("Need to write contents %s",LOCAL_PROP_FILE));
				}			
				
				

			}catch ( FileNotFoundException e ){
				// We should not hit this unless local.prop disappeared between checking
				// it exists and trying to read from it.
				Log.wtf(LOG_TAG,String.format("Shit! Where the fuck did %s go",LOCAL_PROP_FILE));
			}


			
		}else{
			Log.i(LOG_TAG,String.format("%s Not Found",LOCAL_PROP_FILE));
			checkWrite = false ; 
		}
		file = null ; 

		
	}
}
