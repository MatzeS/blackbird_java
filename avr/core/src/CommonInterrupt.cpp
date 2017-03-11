#include "CommonInterrupt.h"

uint8_t commonInterruptPacketDispatch;

void CommonInterrupt::Handler::handle(uint16_t dataSize, uint8_t *data) {

}

CommonInterrupt::Handler::Handler() {

    // set interrupt

    DDRD |= (1 << PD7); // TODO?
    PORTD |= (1 << PD7);

    cli();
    PCMSK2 |= (1 << PCINT23);
    PCICR |= (1 << PCIE2);
    sei();

}

void CommonInterrupt::Handler::loop() {
    if (commonInterruptPacketDispatch > 0) {
        commonInterruptPacketDispatch--;
        Packet interruptPacket;
        Blackbird.sendPacket(&interruptPacket);
    }
}

void CommonInterrupt::Handler::trigger() {
    cli();
    commonInterruptPacketDispatch++;
    sei();
}

CommonInterrupt::Packet::Packet() {
    commandByte = CMB_COMMON_INTERRUPT;
    dataSize = 1;
    data = (uint8_t *) malloc(dataSize);
}
