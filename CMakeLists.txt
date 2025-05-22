cmake_minimum_required(VERSION 3.27)
project(UTPProject)

set(CMAKE_CXX_STANDARD 20)

include_directories(UTPproject PRIVATE "C:/Users/sanch/.jdks/jdk-21.0.7/include")
include_directories(UTPproject PRIVATE "C:/Users/sanch/.jdks/jdk-21.0.7/include/win32")

add_library(UTPProject SHARED
        Mechanic.h
        Mechanic.cpp)