#include "RCSwitch.h"

RCSwitch::Handler::Handler() {

    rcswitch = RCSwitchLib();
    rcswitch.enableTransmit(3);

    receive = true;
    rcswitch.enableReceive(0);
    loopTimeout = 50;


    // config interrupt

    EICRA |= (1 << ISC00); // change
    EIMSK |= (1 << INT0);

}

ISR(INT0_vect){
    RCSwitchLib::handleInterrupt();
}

void RCSwitch::Handler::handle(uint16_t dataSize, uint8_t *data) {

    uint8_t flag = data[0];

    switch (flag) {
        case SWITCH: {
            char g[5], s[5];

            char *group = g;
            char *socket = s;
            for (int i = 0; i < 5; i++) {
                group[i] = (data[1] >> (4-i)) & 1 ? '1' : '0';
                socket[i] = (data[2] >> (4-i)) & 1 ? '1' : '0';
            }
            if ((data[1] >> 7) & 1)
                rcswitch.switchOn(group, socket);
            else
                rcswitch.switchOff(group, socket);
            break;
        }
        case ACTIVATE_LISTENING: {
            //rcswitch.enableReceive(2);
            break;
        }
        case DEACTIVATE_LISTENING: {
            //rcswitch.disableReceive();
            break;
        }
    }

}

void RCSwitch::Handler::loop() {

    if (receive)
        if (rcswitch.available()) {
            uint32_t value = (uint32_t) rcswitch.getReceivedValue();
            if (value != 0) {
                uint16_t bitLength = rcswitch.getReceivedBitlength();
                uint16_t protocol = rcswitch.getReceivedProtocol();
                ReceivePacket packet(value, bitLength, protocol);
                Blackbird.sendPacket(&packet);
            }
            rcswitch.resetAvailable();
        }

}



RCSwitch::ReceivePacket::ReceivePacket(uint32_t value, uint16_t bitLength, uint16_t protocol) {

    commandByte = RC_SWITCH;
    dataSize = 1 + 4 + 2 + 2;
    data = (uint8_t *) malloc(dataSize);
    data[0] = 0x00; //FLAG
    putUINT32(data, 1, value);
    putUINT16(data, 5, bitLength);
    putUINT16(data, 7, protocol);

}
