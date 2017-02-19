#ifndef CORE_DSTEMP_H
#define CORE_DSTEMP_H


#include "AbstractHandler.h"

#define DS18B20_READ_TEMP 10

namespace DS18B20 {

    class Handler : public AbstractHandler {
    private:
        //uint8_t oneWirePins[MAX_SENSORS];
        //OneWire *oneWires[MAX_SENSORS];
        //DallasTemperature *sensors[MAX_SENSORS];

        //uint8_t getFreeSensorIndex();

        //DallasTemperature *getInstanceByPin(uint8_t pin);

    public:
        Handler();

        virtual void loop() override;

        virtual void handle(uint16_t dataSize, uint8_t *data) override;

    };

    class TempReadResponse : public AbstractPacket {
    public:
        TempReadResponse(uint8_t* address, int32_t temp);
    };


}


#endif //CORE_DSTEMP_H
