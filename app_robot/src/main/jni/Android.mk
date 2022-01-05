LOCAL_PATH := $(call my-dir)
#LOCAL_LDFLAGS := -Wl,--unresolved-symbols=ignore-all
#LOCAL_LDLIBS := --lfoo
#LOCAL_SHARED_LIBRARIES := libutils
include $(CLEAR_VARS)

OPENCV_CAMERA_MODULES:=on
OPENCV_INSTALL_MODULES:=on
OPENCV_LIB_TYPE:=SHARED
#ifdef OPENCV_ANDROID_SDK
#  ifneq ("","$(wildcard $(OPENCV_ANDROID_SDK)/OpenCV.mk)")
#    include ${OPENCV_ANDROID_SDK}/OpenCV.mk
#  else
#    include ${OPENCV_ANDROID_SDK}/sdk/native/jni/OpenCV.mk
#  endif
#else
#  include ../../sdk/native/jni/OpenCV.mk
#endif
include C:\Users\pengs\Desktop\openCv\OpenCV-android-sdk\sdk\native\jni\OpenCV.mk

LOCAL_SRC_FILES  := DetectionBasedTracker_jni.cpp
LOCAL_C_INCLUDES += $(LOCAL_PATH)
LOCAL_LDLIBS     += -llog -ldl

LOCAL_MODULE     := detection_based_tracker

include $(BUILD_SHARED_LIBRARY)
