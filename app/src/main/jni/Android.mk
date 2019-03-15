LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE := changeBrightness

LOCAL_SRC_FILES := HelloJNI.c

LOCAL_LDLIBS := -llog

LOCAL_LDFLAGS := -ljnigraphics

include $(BUILD_SHARED_LIBRARY)