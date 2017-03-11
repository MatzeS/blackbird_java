#ifndef CORE_COMMONINTERRUPT_H
#define CORE_COMMONINTERRUPT_H

#include "AbstractPacket.h"
#include "AbstractHandler.h"

namespace CommonInterrupt {

    class Handler : public AbstractHandler {
    public:
        Handler();

        virtual void handle(uint16_t dataSize, uint8_t *data) override;

        virtual void loop() override;

        virtual void trigger();

    };

    class Packet : public AbstractPacket {
    public:
        Packet();
    };

}


#endif //CORE_COMMONINTERRUPT_H
