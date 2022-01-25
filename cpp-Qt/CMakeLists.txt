cmake_minimum_required(VERSION 3.5)
if ("${PROJECT_NAME}" STREQUAL "")
    message(FATAL_ERROR "Missing project name")
endif ()

set(CMAKE_INCLUDE_CURRENT_DIR ON)
set(CMAKE_AUTOUIC ON)
set(CMAKE_AUTOMOC ON)
set(CMAKE_AUTORCC ON)
set(CMAKE_CXX_STANDARD 11)
set(CMAKE_CXX_STANDARD_REQUIRED ON)
set(CMAKE_C_STANDARD 99)
set(QT_SDK_PATH "/Volumes/SanDisk/Users/huang/qt-unified-macOS-x64-4.2.0-online/6.2.2/macos")
set(QT_SDK_LIB_PATH "${QT_SDK_PATH}/lib")
set(CMAKE_PREFIX_PATH "${QT_SDK_LIB_PATH}/cmake")
set(PROJECT_ROOT_PATH ${CMAKE_CURRENT_SOURCE_DIR}/../..)
set(LIB_ROOT_PATH ${PROJECT_ROOT_PATH}/lib)
set(BINARY_PATH ${LIB_ROOT_PATH}/bin)
set(INCLUDE_PATH ${LIB_ROOT_PATH}/inc)
set(IMPLEMENT_PATH ${LIB_ROOT_PATH}/impl)

include_directories(${QT_SDK_LIB_PATH}/QtCore.framework/headers)
include_directories(${QT_SDK_LIB_PATH}/QtNetwork.framework/headers)
include_directories(${QT_SDK_LIB_PATH}/QtWidgets.framework/headers)
include_directories(${PROJECT_ROOT_PATH})
include_directories(${INCLUDE_PATH})
include_directories(${INCLUDE_PATH}/quazip)
include_directories(${INCLUDE_PATH}/SESAMi)
include_directories(${INCLUDE_PATH}/xml2json)

find_package(Qt6 COMPONENTS
        Core
        Gui
        Widgets
        Network
        Core5Compat
        REQUIRED
        )

file(GLOB SOURCE_FILES
        "*.cpp"
        "*.qrc"
        "${PROJECT_ROOT_PATH}/*.cpp"
        "${PROJECT_ROOT_PATH}/*.qrc"
        "${IMPLEMENT_PATH}/*.cpp"
        "${IMPLEMENT_PATH}/*.qrc"
        "${IMPLEMENT_PATH}/base64/base64/*.cpp"
        "${IMPLEMENT_PATH}/SESAMi/*.cpp"
        "${IMPLEMENT_PATH}/SESAMi/*.qrc"
        "${IMPLEMENT_PATH}/SESAMi/Body/*.cpp"
        )

link_libraries(${BINARY_PATH}/libQtAES.a)
add_executable(${PROJECT_NAME} ${SOURCE_FILES})

target_link_libraries(${PROJECT_NAME} PRIVATE
        Qt::Core
        Qt::Gui
        Qt::Widgets
        Qt::Network
        Qt::Core5Compat
        ${BINARY_PATH}/libquazip1-qt6d.1.0.0.dylib)

