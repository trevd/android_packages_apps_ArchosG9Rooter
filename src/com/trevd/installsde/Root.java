package com.trevd.installsde;
public class Root  {
	static { 
		System.loadLibrary("installsde_jni");
	}
    static native int getroot();
    static native int write_sde_image(android.content.res.AssetManager AssetManager);
}
