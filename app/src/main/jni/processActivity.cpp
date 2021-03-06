#include"jni.h"
#include<stdio.h>
#include "cn_hxc_imgrecognition_processActivity.h"

JNIEXPORT jintArray JNICALL Java_cn_hxc_Activity_processActivity_removeNoise(
        JNIEnv *env, jobject obj, jintArray by_, jint width, jint height) {
    jint *by = env->GetIntArrayElements(by_, NULL);
    //
    CodeComponent rescomponent[100];
    int count = ConCompLabelling8(rescomponent, by, width, height, 100);
    int a[count * 7];
    int j = 0;
    for (int i = 0; i < count; i++) {
        if (rescomponent[i].sign == FALSE) {
            a[j++] = 0;
            a[j++] = rescomponent[i].compshape.minx;
            a[j++] = rescomponent[i].compshape.maxx;
            a[j++] = rescomponent[i].compshape.miny;
            a[j++] = rescomponent[i].compshape.maxy;
            a[j++] = rescomponent[i].pixelnum;
            a[j++]=rescomponent[i].value;
        } else {
            a[j++] = 1;
            a[j++] = 0;
            a[j++] = 0;
            a[j++] = 0;
            a[j++] = 0;
            a[j++] = 0;
            a[j++]=0;
        }
    }
    jintArray array = env->NewIntArray(count * 7);
    env->SetIntArrayRegion(array, 0, count * 7, a);

    env->ReleaseIntArrayElements(by_, by, 0);

    return array;
}
JNIEXPORT jint JNICALL Java_cn_hxc_Activity_processActivity_callint(
        JNIEnv * env, jobject obj,jstring num_, jstring win2_, jstring whi2_,jint flag,jstring model_) {


    const char *num = env->GetStringUTFChars(num_, 0);
    const char *win2 = env->GetStringUTFChars(win2_, 0);
    const char *whi2 = env->GetStringUTFChars(whi2_, 0);
    const char *model = env->GetStringUTFChars(model_, 0);


    char cc[12]={0};

//    jing_ini_np( num,  win2, whi2,model);
//    int result= recnum_jingsvm(by1, width, height);

//    jing_ini_np( num, win2, whi2);
//
    jing_ini_np_svm( num, win2, whi2,model);

    env->ReleaseStringUTFChars(num_, num);
    env->ReleaseStringUTFChars(win2_, win2);
    env->ReleaseStringUTFChars(whi2_, whi2);
	env->ReleaseStringUTFChars(model_, model);


    //jstring strResult=CharTojstring(env,cc);
    //return strResult;
    return 0;
}
JNIEXPORT jint JNICALL Java_cn_hxc_Activity_processActivity_Recognize(
        JNIEnv * env, jobject obj, jintArray by1_, jint width, jint height,jint flag){
    jint *by1 = env->GetIntArrayElements(by1_, NULL);

    int result= recnum_jingsvm(by1, width, height);

    env->ReleaseIntArrayElements(by1_, by1, 0);

    return result;
}