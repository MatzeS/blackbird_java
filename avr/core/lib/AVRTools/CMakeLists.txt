
file(GLOB allFiles
        "*.h"
        "*.cpp"
        )

add_avr_library(
        avrtools

        ArduinoMegaPins.h
        ArduinoPins.h

        Analog2Digital.cpp
        new.cpp
        SystemClock.cpp

        SPI.cpp
        SPI.h





)