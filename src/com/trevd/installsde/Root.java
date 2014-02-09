package com.trevd.installsde;
public class Root  {
	static { 
		System.loadLibrary("installsde_jni");
	}
    static native int getroot();
}
