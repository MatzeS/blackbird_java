#include "BlackbirdClass.h"

BlackbirdClass Blackbird;

BlackbirdClass::BlackbirdClass() {
    expect = NOTHING;

    dataIndex = 0;
    data = (uint8_t *) malloc(DATA_BUFFER_SIZE * sizeof(uint8_t));

    handlers = (AbstractHandler **) malloc(16);
    handlersCommand = (uint8_t *) malloc(16);

    for (int i = 0; i < 16; i++)
        handlersCommand[i] = 0x00;

}

void BlackbirdClass::sendPacket(AbstractPacket *packet) {

    unsigned char cmdByte = (unsigned char) packet->getCommandByte();
    //TODO check command byte

    uint16_t dataSize = packet->getDataSize();


    transmit(0xFF);
    transmit(cmdByte);

    unsigned char dataByte;
    for (size_t dataIndex = 0; dataIndex < dataSize; dataIndex++) {

        dataByte = (unsigned char) packet->getData()[dataIndex];
        if (dataByte != 0xFF)
            transmit(dataByte);
            // last byte in block encodes a full "0xFF" block
        else if ((dataIndex + 1) % 8 == 0) {

            // check if the block is a full start byte block
            bool fullBlock = true;
            for (size_t i = 1; i < 8; i++)
                if (packet->getData()[dataIndex - i] != 0xFF) {
                    fullBlock = false;
                    break;
                }

            if (fullBlock)
                transmit(0xF0); // full block flag in the last block byte
            else
                transmit(0x00);


        } else
            transmit(0x00); // empty data byte, decoded to start byte via escape byte


        if ((dataIndex + 1) % 8 == 0) {
            // end of block -> send escape byte

            uint8_t escapeByte = 0x00;
            // set escape byte bits
            for (size_t i = 0; i < 8; i++)
                if (packet->getData()[dataIndex - i] == 0xFF)
                    escapeByte |= (1 << i);

            // avoid full escape byte
            if (escapeByte == 0xFF)
                escapeByte = 0x7F;

            transmit(escapeByte);

        } else if ((dataIndex + 1) == dataSize) {
            // end of packet -> last escape byte incomplete

            uint8_t escapeByte = 0x00;
            // set escape byte bits
            size_t leftBytes = dataIndex % 8 + 1;
            for (size_t i = 0; i < leftBytes; i++)
                if (packet->getData()[dataIndex - i] == 0xFF)
                    escapeByte |= (1 << i);

            transmit( escapeByte);

        }

    }

    transmit(0xFF); //DELIMITER END BYTE

}

void BlackbirdClass::parseByte(uint8_t incomingByte) {

    if (incomingByte == 0xFF) {

        if (expect == DATA || expect == ESC) {

            // last block was not full and is not escaped
            if ((dataIndex - 1) % 8 != 0) {
                uint8_t escByte = data[dataIndex - 1];
                uint16_t leftBytes = (dataIndex - 1) % 8;
                for (uint8_t i = 0; i < leftBytes; i++)
                    if (escByte & (1 << i))
                        data[dataIndex - 1 - 1 - i] = 0xFF;

                dataIndex = dataIndex - 1;
            }

            bool handled = false;
            for (uint8_t i = 0; i < 16; i++)
                if (handlersCommand[i] == commandByte) {
                    handlers[i]->handle(dataIndex, data);
                    handled = true;
                    break;
                }

            if (!handled)
                transmit(0x22); //TODO ERROR;

        }

        expect = CMD;

    } else if (expect == CMD) {

        commandByte = incomingByte;

        dataIndex = 0;

        expect = DATA;

    } else if (expect == DATA) {

        data[dataIndex] = incomingByte;
        dataIndex++;

        if (dataIndex % 8 == 0)
            expect = ESC;

    } else if (expect == ESC) {

        uint8_t fullFlag = data[dataIndex - 1];

        for (uint8_t i = 0; i < 8; i++) {
            if (incomingByte & (1 << i))
                data[dataIndex - 1 - i] = 0xFF;
        }

        if (incomingByte == 0x7F) // special possible full 8x 0xFF block
            if (fullFlag == 0xF0)
                data[dataIndex - 8] = 0xFF;

        expect = DATA;

    } else
        transmit(0x33);   //TODO error

}

void BlackbirdClass::attachHandler(uint8_t command, AbstractHandler *handler) {
    for (uint8_t i = 0; i < 16; i++)
        if (handlersCommand[i] == command || handlersCommand[i] == 0x00) {
            handlers[i] = handler;
            handlersCommand[i] = command;
            return;
        }
}

void BlackbirdClass::detachHandler(uint8_t command, AbstractHandler *handler) {
    for (uint8_t i = 0; i < sizeof(handlers); i++)
        if (handlersCommand[i] == command) {
            handlers[i] = NULL;
            handlersCommand[i] = 0x00;
            return;
        }
}

void BlackbirdClass::loop() {
    for (uint8_t i = 0; i < 16; i++)
        if (handlersCommand[i] != 0x00)
            handlers[i]->executeLoopWithTimeout();
}

void BlackbirdClass::setTransmitFunc(transmitFunc func) {
    transmit = func;
}
