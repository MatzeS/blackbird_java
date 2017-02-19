add_avr_library(
        core

        CommandBytes.h

        BlackbirdClass.h
        BlackbirdClass.cpp

        AbstractPacket.h
        AbstractPacket.cpp

        AbstractHandler.h
        AbstractHandler.cpp



        DeviceIdentification.h
        DeviceIdentification.cpp

        CommonInterrupt.h
        CommonInterrupt.cpp

        I2C.h
        I2C.cpp

        RCSwitch.h
        RCSwitch.cpp

        IR.h
        IR.cpp

        DS18B20.h
        DS18B20.cpp

)

avr_target_link_libraries(
        core

        avrtools
        uart
        rc-switch
        i2cmaster
        irremote
        ds18x20
)
