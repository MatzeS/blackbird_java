#include "DS18B20.h"
#include "../lib/DS18X20/onewire.h"
#include "../lib/DS18X20/ds18x20.h"


DS18B20::Handler::Handler() {
    ow_set_bus(&PIND, &PORTD, &DDRD, PD6);
}

void DS18B20::Handler::loop() {
}

void error(){
    DDRB |= _BV(PB5);
    PORTB |= _BV(PB5);
}

void DS18B20::Handler::handle(uint16_t dataSize, uint8_t *data) {

    uint8_t flag = data[0];
    uint8_t *address = &data[1];

    if (flag == DS18B20_READ_TEMP) {

        if (DS18X20_start_meas(DS18X20_POWER_EXTERN, address) == DS18X20_OK) {
            delayMilliseconds(DS18B20_TCONV_12BIT);

            int32_t temp;
            if(DS18X20_read_maxres(address, &temp) == DS18X20_OK){

                TempReadResponse readResponse(address, temp);
                Blackbird.sendPacket(&readResponse);

            }else{
                error();
            }

        }else
            error();

    }

}


DS18B20::TempReadResponse::TempReadResponse(uint8_t* address, int32_t temp) {
    commandByte = CMB_DS18B20;
    dataSize = 1 + 8 + 4;
    data = (uint8_t *) malloc(dataSize);
    data[0] = DS18B20_READ_TEMP;
    for(uint8_t i = 0; i < 8; i++)
        data[1 + i] = address[i];

    union { int32_t i; uint32_t u; } t;
    t.i = temp;
    putUINT32(data, 9, t.u);
}
