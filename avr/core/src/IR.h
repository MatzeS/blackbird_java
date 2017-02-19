#ifndef CORE_IR_H
#define CORE_IR_H


#include "AbstractHandler.h"
#include "../lib/IRremote/IRremote.h"

namespace IR {

    class Handler : public AbstractHandler {
        IRrecv *irrecv;
        decode_results results;
    public:
        Handler();

        virtual void handle(uint16_t dataSize, uint8_t *data) override;

        virtual void loop() override;
    };

    class IRReceivePacket : public AbstractPacket {
    public:
        IRReceivePacket(uint32_t value);
    };


}


#endif //CORE_IR_H
