#ifndef BLACKBIRD_H
#define BLACKBIRD_H

#include <stdlib.h>
#include <stdint.h>
#include "AbstractHandler.h"
#include "AbstractPacket.h"

#define DATA_BUFFER_SIZE 255
//TODO exceed error

enum Expecting {
    NOTHING, CMD, DATA, ESC
};

typedef void (*transmitFunc)(uint8_t);

class BlackbirdClass {
private:
    transmitFunc transmit;

    AbstractHandler **handlers;
    uint8_t *handlersCommand;

    Expecting expect;
    uint8_t commandByte;
    uint16_t dataIndex;
    uint8_t *data;

public:

    BlackbirdClass();

    void parseByte(uint8_t byte);

    void sendPacket(AbstractPacket *packet);

    void attachHandler(uint8_t command, AbstractHandler *handler);

    void detachHandler(uint8_t command, AbstractHandler *handler);

    void loop();

    void setTransmitFunc(transmitFunc func);
};

extern BlackbirdClass Blackbird;


#endif //BLACKBIRD_H
