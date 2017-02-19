#include "AbstractHandler.h"

void AbstractHandler::loop() {
    //NOP
}

void AbstractHandler::executeLoopWithTimeout() {
    if (loopTimeout == 0)
        loop();
    else if (millis() - loopLastTrigger >= loopTimeout) {
        loop();
        loopLastTrigger = millis();
    }
}
