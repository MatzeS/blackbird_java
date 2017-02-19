#include "I2C.h"
#include "../lib/i2cmaster/i2cmaster.h"


void I2C::Handler::handle(uint16_t dataSize, uint8_t *data) {

    // MODE:
    // the first two bits are the mode
    // 00: WRITE
    // 01: READ
    uint8_t mode = data[0] >> 6;
    uint8_t slaveAddr = data[1];
    uint8_t i2cSlaveAddr = slaveAddr;
    i2cSlaveAddr <<= 1; // shift one to the right for the R/W flag
    i2cSlaveAddr &= ~(1 << 0); // clearing R/W flag

    if (mode == I2C_WRITE) {

        i2c_start_wait(i2cSlaveAddr + I2C_WRITE);

        //write remaining bytes to the i2c device
        for (uint8_t i = 2; i < dataSize; i++)
            i2c_write(data[i]);

        i2c_stop();

    } else if (mode == I2C_READ) {

        uint8_t regAddr = data[2];
        uint8_t numBytes = data[3];
        uint8_t *readData = (uint8_t *) malloc(numBytes);

        i2c_start_wait(i2cSlaveAddr + I2C_WRITE);
        i2c_write(regAddr);
        i2c_rep_start(i2cSlaveAddr + I2C_READ);

        for (int i = 0; i < numBytes; i++)
            readData[i] = i2c_read(i != (numBytes - 1));

        i2c_stop();

        ReadResponse answer(slaveAddr, regAddr, numBytes, readData);
        Blackbird.sendPacket(&answer);

        free(readData);

    } else{} //TODO

}

I2C::Handler::Handler() {

    i2c_init();

}

I2C::ReadResponse::ReadResponse(uint8_t slaveAddr, uint8_t regAddr, uint8_t dataSize, uint8_t *data) {

    commandByte = CMB_I2C;

    // DATA
    // I2C flag data, empty
    // slave address
    // register address
    // data (dataSize)

    this->data = (uint8_t *) malloc(3 + dataSize);

    this->data[0] = 0x00; // FLAGS
    this->data[1] = slaveAddr;
    this->data[2] = regAddr;
    for (uint8_t i = 0; i < dataSize; i++)
        this->data[3 + i] = data[i];

    this->dataSize = dataSize + 3;

}
