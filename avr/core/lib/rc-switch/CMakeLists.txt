
add_avr_library(
        rc-switch

        RCSwitch.cpp
        RCSwitch.h
)

avr_target_link_libraries(
        rc-switch

        avrtools
)
