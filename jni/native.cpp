/*
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

#define LOG_TAG "Exploits Native"
#include <utils/Log.h>

#include <stdio.h>
#include <fcntl.h>
#include <android/asset_manager.h>
#include <android/asset_manager_jni.h>
#include "jni.h"
#include "JNIHelp.h"
static const char *classPathName = "com/trevd/installsde/Root";			
int exploit_main();
int writesde_internal(unsigned char* sde_source_bytes,const char* sde_dest_path);

static jint getroot(JNIEnv *env, jobject thiz) {
    return exploit_main();   
}
static jint write_sde_image(JNIEnv *env, jobject thiz,jobject assetManager) {

	AAssetManager* mgr = AAssetManager_fromJava(env, assetManager); 
	ALOGI("AAssetManager mgr=%p\n",mgr);
    AAssetDir* assetDir = AAssetManager_openDir(mgr, "");
    ALOGI("assetDir:%p\n",assetDir);
	const char* sde_dest_path = (const char*)NULL;
	while ((sde_dest_path = AAssetDir_getNextFileName(assetDir)) != NULL) {
		AAsset* asset = AAssetManager_open(mgr, sde_dest_path, AASSET_MODE_STREAMING);
		off_t sz = AAsset_getLength(asset);
		ALOGI("filename:%s %ld\n",sde_dest_path,sz);
		char* sde_source_bytes =  (char*)AAsset_getBuffer(asset);
		int fd  = open("/mnt/rawfs/custom",O_WRONLY);
		if (fd<0){
			ALOGE("Error:Cannot Create Output File %s!!\n",sde_dest_path);
			AAsset_close(asset);
			continue;	
		}
		ALOGI("Writing Destination:%s\n",sde_dest_path);
		long bytes = write(fd,sde_source_bytes,sz);
		if ( bytes <= 0 ){
			ALOGE("Error:Cannot Write Output File %s!!\n",sde_dest_path);
			AAsset_close(asset);
			close(fd);
			continue;	
		}
		ALOGI("%s written - %ld!!\n",sde_dest_path,bytes);
		if(fsync(fd) == -1){
			ALOGE("fsync failed Error: %d %s!!\n",errno,strerror(errno));
			AAsset_close(asset);
			close(fd);
			continue;	
		}
		AAsset_close(asset);
	}
	AAssetDir_close(assetDir);
	free(mgr);
    return 0 ; 
}
static JNINativeMethod methods[] = {
  {"getroot", "()I", (void*)getroot },
  {"write_sde_image", "(Landroid/content/res/AssetManager;)I", (void*)write_sde_image },
  //{"write_sde_image", "([BILjava/lang/String;)I", (void*)write_sde_image },
};

/*
 * Register several native methods for one class.
 */
static int registerNativeMethods(JNIEnv* env, const char* className,
    JNINativeMethod* gMethods, int numMethods)
{
    jclass clazz;

    clazz = env->FindClass(className);
    if (clazz == NULL) {
        ALOGE("Native registration unable to find class '%s'", className);
        return JNI_FALSE;
    }
    if (env->RegisterNatives(clazz, gMethods, numMethods) < 0) {
        ALOGE("RegisterNatives failed for '%s'", className);
        return JNI_FALSE;
    }

    return JNI_TRUE;
}

/*
 * Register native methods for all classes we know about.
 *
 * returns JNI_TRUE on success.
 */
static int registerNatives(JNIEnv* env)
{
  if (!registerNativeMethods(env, classPathName,
                 methods, sizeof(methods) / sizeof(methods[0]))) {
    return JNI_FALSE;
  }

  return JNI_TRUE;
}


// ----------------------------------------------------------------------------

/*
 * This is called by the VM when the shared library is first loaded.
 */
 
typedef union {
    JNIEnv* env;
    void* venv;
} UnionJNIEnvToVoid;

jint JNI_OnLoad(JavaVM* vm, void* reserved)
{
    UnionJNIEnvToVoid uenv;
    uenv.venv = NULL;
    jint result = -1;
    JNIEnv* env = NULL;
    
    ALOGI("JNI_OnLoad");

    if (vm->GetEnv(&uenv.venv, JNI_VERSION_1_4) != JNI_OK) {
        ALOGE("ERROR: GetEnv failed");
        goto bail;
    }
    env = uenv.env;

    if (registerNatives(env) != JNI_TRUE) {
        ALOGE("ERROR: registerNatives failed");
        goto bail;
    }
    
    result = JNI_VERSION_1_4;
    
bail:
    return result;
}
