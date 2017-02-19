
#include <stdlib.h>
#include "AbstractPacket.h"

uint8_t AbstractPacket::getCommandByte() const {
    return commandByte;
}

uint8_t *AbstractPacket::getData() const {
    return data;
}

uint8_t AbstractPacket::getDataSize() const {
    return dataSize;
}

AbstractPacket::AbstractPacket(uint8_t commandByte, uint8_t dataSize, uint8_t *data) :
        commandByte(commandByte),
        dataSize(dataSize),
        data(data) {}

AbstractPacket::AbstractPacket() {}

AbstractPacket::~AbstractPacket() {
    free(data);
}

void putUINT8(uint8_t *data, uint8_t index, uint8_t value) {
    data[index] = value;
}

void putUINT16(uint8_t *data, uint8_t index, uint16_t value) {
    putUINT8(data, index + 1, (uint8_t) value);
    putUINT8(data, index, (uint8_t) (value >> 8));
}

void putUINT32(uint8_t *data, uint8_t index, uint32_t value) {
    putUINT16(data, index + 2, (uint16_t) value);
    putUINT16(data, index, (uint16_t) (value >> 16));
}

union FloatBytes {
    float f;
    char c[sizeof(float)];
};

void putFloat(uint8_t *data, uint8_t index, float value) {
    FloatBytes fb;
    fb.f = value;
    for (int i = 0; i < 4; i++)
        data[index + i] = fb.c[i];
}
