#ifndef CORE_I2C_H
#define CORE_I2C_H

#include "AbstractHandler.h"

namespace I2C {

    class Handler : public AbstractHandler {
    public:
        Handler();

        virtual void handle(uint16_t dataSize, uint8_t *data);
    };

    class ReadResponse : public AbstractPacket {
    public:
        ReadResponse(uint8_t slaveAddr, uint8_t regAddr, uint8_t dataSize, uint8_t *data);
    };

}

#endif //CORE_I2C_H
