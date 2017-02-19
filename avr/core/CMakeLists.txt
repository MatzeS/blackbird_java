cmake_minimum_required(VERSION 2.8)

get_filename_component(PARENT_DIR ${CMAKE_PARENT_LIST_FILE} DIRECTORY)
set(CURR_DIR ${CMAKE_CURRENT_LIST_DIR})

project(core)

include(${CURR_DIR}/../general.cmake)

message(STATUS "Loading config file")

file(READ ${PARENT_DIR}/config.avr configFile)
string(REPLACE "\n" ";" configList ${configFile})

message(STATUS "Add defines")

foreach (configLine ${configList})
    string(SUBSTRING ${configLine} 0 1 configLineStart1)
    string(SUBSTRING ${configLine} 0 2 configLineStart2)
    if (NOT( (configLineStart2 STREQUAL "//") OR (configLineStart1 STREQUAL "#") ) )
        string(REPLACE " " ";" configLineArgs ${configLine})
        list(GET configLineArgs 0 dev_name)
        list(GET configLineArgs 1 dev_value)

        message(STATUS ${dev_name}=${dev_value})
        add_definitions(-D${dev_name}=${dev_value})
    endif ()
endforeach ()

message(STATUS "End of config")


add_subdirectory(${CMAKE_CURRENT_LIST_DIR}/src bin/src)
add_subdirectory(${CMAKE_CURRENT_LIST_DIR}/lib bin/lib)