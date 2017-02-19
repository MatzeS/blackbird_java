
#ifndef CORE_PACKET_H
#define CORE_PACKET_H

#include <stddef.h>
#include <stdint.h>
#include <stdlib.h>

#include "CommandBytes.h"


class AbstractPacket {
protected:
    uint8_t commandByte;
    uint8_t dataSize;
    uint8_t *data;
public:
    virtual uint8_t getCommandByte() const;

    virtual uint8_t *getData() const;

    virtual uint8_t getDataSize() const;

    AbstractPacket(uint8_t commandByte, uint8_t dataSize, uint8_t *data);

    AbstractPacket();

    ~AbstractPacket();
};

void putUINT8(uint8_t *data, uint8_t index, uint8_t value);

void putUINT16(uint8_t *data, uint8_t index, uint16_t value);

void putUINT32(uint8_t *data, uint8_t index, uint32_t value);

void putFloat(uint8_t *data, uint8_t index, float value);

#endif //CORE_PACKET_H
