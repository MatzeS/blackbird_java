#ifndef CORE_RCSWITCH_H
#define CORE_RCSWITCH_H

#include "AbstractHandler.h"
#include "../lib/rc-switch/RCSwitch.h"

#define SWITCH 10
#define ACTIVATE_LISTENING 20
#define DEACTIVATE_LISTENING 30

namespace RCSwitch {

    class Handler : public AbstractHandler {
    private:
        bool receive;
        RCSwitchLib rcswitch;
    public:
        Handler();

        virtual void handle(uint16_t dataSize, uint8_t *data) override;

        virtual void loop() override;
    };


    class ReceivePacket : public AbstractPacket {
    public:
        ReceivePacket(uint32_t value, uint16_t bitLength, uint16_t protocol);
    };

}

#endif //CORE_RCSWITCH_H
