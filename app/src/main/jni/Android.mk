LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := processActivity
LOCAL_SRC_FILES := ConComponent.cpp processActivity.cpp processing.cpp recnum.cpp Thinning.cpp
LOCAL_LDLIBS += -llog


include $(BUILD_SHARED_LIBRARY)
