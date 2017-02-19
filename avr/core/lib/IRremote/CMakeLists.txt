
file(GLOB allFiles
        "*.h"
        "*.cpp"
        )

add_avr_library(
        irremote

        ${allFiles}
)