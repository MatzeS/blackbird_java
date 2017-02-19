#include "IR.h"

IR::Handler::Handler() {
    irrecv = new IRrecv(3);
    irrecv->enableIRIn();

    loopTimeout = 10;
}

void IR::Handler::handle(uint16_t dataSize, uint8_t *data) {

}

void IR::Handler::loop() {
    while (irrecv->decode(&results)) {
        IRReceivePacket receivePacket(results.value);
        Blackbird.sendPacket(&receivePacket);
        irrecv->resume(); // Receive the next value
    }
}

IR::IRReceivePacket::IRReceivePacket(uint32_t value) {
    commandByte = CMB_IR;
    dataSize = 5;
    data = (uint8_t *) malloc(dataSize);
    data[0] = 10; // RECEIVE FLAG
    putUINT32(data, 1, value);
}
