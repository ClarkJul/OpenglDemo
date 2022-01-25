cmake_minimum_required(VERSION 3.6.0)
set(CMAKE_VERBOSE_MAKEFILE on)
set(CMAKE_CXX_STANDARD 11)
set(CMAKE_CXX_STANDARD_REQUIRED TRUE)

set(CMAKE_LIBRARY_OUTPUT_DIRECTORY ${CMAKE_SOURCE_DIR}/../libs/${ANDROID_ABI}/)

add_library(
        VideoCollection
        SHARED
        NativeRenderJni.cpp
        MyGLRenderContext.cpp
        GLUtils.cpp
        MultiSample.cpp
        VideoCollection.cpp
        tcl_common_function.cpp
        tcl_classification_detection.cpp
        tcl_media_function.cpp
        utils.cpp)

find_library(
        log-lib
        log)

include_directories(include)

set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -L${CMAKE_SOURCE_DIR}/../jniLibs/${ANDROID_ABI}")
target_link_libraries(
        VideoCollection
        opencv_java4
        mediandk

        tcltflitev2
        yuv

        EGL
        GLESv3
        android
        ${log-lib})