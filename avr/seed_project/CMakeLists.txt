cmake_minimum_required(VERSION 2.8)

project(seed_project_name)

set(AVR_UPLOADTOOL_PORT /dev/ttyUSB0)
set(AVR_UPLOADTOOL_BAUD_RATE 57600)
set(AVR_PROGRAMMER arduino)
set(AVR_MCU atmega328p)
set(AVR_H_FUSE 0xd9)
set(AVR_L_FUSE 0xc3)
set(MCU_SPEED "16000000UL")


set(BLACKBIRD_AVR_PATH ${CMAKE_CURRENT_LIST_DIR}/../../../blackbird/avr)
include(${BLACKBIRD_AVR_PATH}/core/CMakeLists.txt)

add_subdirectory(src)