#ifndef CORE_HANDLER_H
#define CORE_HANDLER_H

#include <avr/io.h>
#include <avr/interrupt.h>
#include <stdlib.h>
#include <stdint.h>

#include "../lib/AVRTools/SystemClock.h"
#include "AbstractPacket.h"

class AbstractHandler {
protected:
    uint32_t loopTimeout = 0;
    unsigned long loopLastTrigger = 0;
public:
    virtual void handle(uint16_t dataSize, uint8_t *data)= 0;
    void executeLoopWithTimeout();

    virtual void loop();
};

//has to be down here to define handler
#include "BlackbirdClass.h"

#endif //CORE_HANDLER_H
