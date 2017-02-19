#ifndef CORE_DEVICEIDENTIFICATION_H
#define CORE_DEVICEIDENTIFICATION_H

#include "AbstractPacket.h"
#include "AbstractHandler.h"



namespace DeviceIdentification {

    extern const char* identification;

    class Response : public AbstractPacket {
    public:
        Response();
    };

    class Handler : public AbstractHandler {
    public:
        virtual void handle(uint16_t dataSize, uint8_t *data);
    };


}

#endif //CORE_DEVICEIDENTIFICATION_H
