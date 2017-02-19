#include <string.h>
#include "DeviceIdentification.h"

void DeviceIdentification::Handler::handle(uint16_t dataSize, uint8_t *data) {
    DeviceIdentification::Response packet;
    Blackbird.sendPacket(&packet);
}

DeviceIdentification::Response::Response() {

    commandByte = CMB_DEVICE_IDENTIFICATION;

#define xstr(s) str(s)
#define str(s) #s

    const char* name = xstr(DEVICE_NAME);

    dataSize = strlen(name);
    data = (uint8_t *) malloc(dataSize);

    for(size_t i = 0; i < dataSize; i++)
        data[i] = (uint8_t) name[i];

}
