#include <avr/io.h>
#include <avr/interrupt.h>
#include <string.h>

#include "../lib/uart/uart.h"

#include "BlackbirdClass.h"

#include "DeviceIdentification.h"
#include "CommonInterrupt.h"
#include "I2C.h"
#include "RCSwitch.h"
#include "IR.h"
#include "DS18B20.h"


#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wmissing-noreturn"


int main(void) {

    DeviceIdentification::Handler deviceIdentificationHandler;
    Blackbird.attachHandler(CMB_DEVICE_IDENTIFICATION, &deviceIdentificationHandler);

    CommonInterrupt::Handler commonInterruptHandler;
    Blackbird.attachHandler(CMB_COMMON_INTERRUPT, &commonInterruptHandler);

    I2C::Handler i2cHandler;
    Blackbird.attachHandler(CMB_I2C, &i2cHandler);


    RCSwitch::transmitDDR = &DDRD;
    RCSwitch::transmitPORT = &PORTD;
    RCSwitch::transmitBIT = 5;

    RCSwitch::Handler rcswitchHandler;
    Blackbird.attachHandler(RC_SWITCH, &rcswitchHandler);

    IR::Handler irHandler;
    Blackbird.attachHandler(CMB_IR, &irHandler);

    DS18B20::Handler ds18b20Handler;
    Blackbird.attachHandler(CMB_DS18B20, &ds18b20Handler);


    uart_init(UART_BAUD_SELECT(BAUD_RATE, F_CPU));
    Blackbird.setTransmitFunc((transmitFunc) &uart_putc);

    // uses timer0
    initSystemClock();


    sei();

    DDRB |= _BV(PB5);

    PORTB |= _BV(PB5);
    delay(200);
    PORTB &= ~_BV(PB5);
    delay(200);
    PORTB |= _BV(PB5);
    delay(200);
    PORTB &= ~_BV(PB5);

    //TODO cleanup

    unsigned int incomingByte;
    while (true) {

        while (true) {
            incomingByte = uart_getc();
            if (incomingByte & UART_FRAME_ERROR) {
                /* Framing Error detected, i.e no stop bit detected */
                //uart_puts_P("UART Frame Error: ");
                uart_putc(0x11);

            } else if (incomingByte & UART_OVERRUN_ERROR) {
                /*
                 * Overrun, a character already present in the UART UDR register was
                 * not read by the interrupt handler before the next character arrived,
                 * one or more received characters have been dropped
                 */
                //uart_puts_P("UART Overrun Error: ");
                uart_putc(0x12);

            } else if (incomingByte & UART_BUFFER_OVERFLOW) {
                /*
                 * We are not reading the receive buffer fast enough,
                 * one or more received character have been dropped
                 */
                //uart_puts_P("Buffer overflow error: ");
                uart_putc(0x13);

            } else if (incomingByte & UART_NO_DATA) {
                break;
            } else {
                Blackbird.parseByte(incomingByte);
            }
        }

        Blackbird.loop();

    }

}

#pragma clang diagnostic pop
