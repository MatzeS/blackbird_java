
add_avr_executable(
        seed_executablename

        ${BLACKBIRD_AVR_PATH}/core/src/main.cpp
)

avr_target_link_libraries(
        seed_executablename

        core
)
