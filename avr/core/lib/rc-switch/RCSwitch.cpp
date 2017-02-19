#include <stdint.h>
#include "RCSwitch.h"

namespace RCSwitch {
    volatile uint8_t *transmitDDR, *transmitPORT;
    volatile uint8_t transmitBIT;
}

#ifndef  RCSwitchLibDisableReceiving
unsigned long RCSwitchLib::nReceivedValue = 0;
unsigned int RCSwitchLib::nReceivedBitlength = 0;
unsigned int RCSwitchLib::nReceivedDelay = 0;
unsigned int RCSwitchLib::nReceivedProtocol = 0;
int RCSwitchLib::nReceiveTolerance = 60;
#endif
unsigned int RCSwitchLib::timings[RCSwitchLib_MAX_CHANGES];

RCSwitchLib::RCSwitchLib() {
    this->nTransmitterPin = -1;
    this->setPulseLength(350);
    this->setRepeatTransmit(10);
    this->setProtocol(1);
#ifndef  RCSwitchLibDisableReceiving
    this->nReceiverInterrupt = -1;
    this->setReceiveTolerance(60);
    RCSwitchLib::nReceivedValue = 0;
#endif
}

/**
  * Sets the protocol to send.
  */
void RCSwitchLib::setProtocol(int nProtocol) {
    this->nProtocol = nProtocol;
    if (nProtocol == 1) {
        this->setPulseLength(350);
    } else if (nProtocol == 2) {
        this->setPulseLength(650);
    } else if (nProtocol == 3) {
        this->setPulseLength(100);
    }
}

/**
  * Sets the protocol to send with pulse length in microseconds.
  */
void RCSwitchLib::setProtocol(int nProtocol, int nPulseLength) {
    this->nProtocol = nProtocol;
    this->setPulseLength(nPulseLength);
}


/**
  * Sets pulse length in microseconds
  */
void RCSwitchLib::setPulseLength(int nPulseLength) {
    this->nPulseLength = nPulseLength;
}

/**
 * Sets Repeat Transmits
 */
void RCSwitchLib::setRepeatTransmit(int nRepeatTransmit) {
    this->nRepeatTransmit = nRepeatTransmit;
}

/**
 * Set Receiving Tolerance
 */
#ifndef  RCSwitchLibDisableReceiving

void RCSwitchLib::setReceiveTolerance(int nPercent) {
    RCSwitchLib::nReceiveTolerance = nPercent;
}

#endif


/**
 * Enable transmissions
 *
 * @param nTransmitterPin    Arduino Pin to which the sender is connected to
 */
void RCSwitchLib::enableTransmit(int nTransmitterPin) {
    this->nTransmitterPin = nTransmitterPin;

    using namespace RCSwitch;
    *transmitDDR |= (1 << transmitBIT);

}

/**
  * Disable transmissions
  */
void RCSwitchLib::disableTransmit() {
    this->nTransmitterPin = -1;
}

/**
 * Switch a remote switch on (Type D REV)
 *
 * @param sGroup        Code of the switch group (A,B,C,D)
 * @param nDevice       Number of the switch itself (1..3)
 */
void RCSwitchLib::switchOn(char sGroup, int nDevice) {
    this->sendTriState(this->getCodeWordD(sGroup, nDevice, true));
}

/**
 * Switch a remote switch off (Type D REV)
 *
 * @param sGroup        Code of the switch group (A,B,C,D)
 * @param nDevice       Number of the switch itself (1..3)
 */
void RCSwitchLib::switchOff(char sGroup, int nDevice) {
    this->sendTriState(this->getCodeWordD(sGroup, nDevice, false));
}

/**
 * Switch a remote switch on (Type C Intertechno)
 *
 * @param sFamily  Familycode (a..f)
 * @param nGroup   Number of group (1..4)
 * @param nDevice  Number of device (1..4)
  */
void RCSwitchLib::switchOn(char sFamily, int nGroup, int nDevice) {
    this->sendTriState(this->getCodeWordC(sFamily, nGroup, nDevice, true));
}

/**
 * Switch a remote switch off (Type C Intertechno)
 *
 * @param sFamily  Familycode (a..f)
 * @param nGroup   Number of group (1..4)
 * @param nDevice  Number of device (1..4)
 */
void RCSwitchLib::switchOff(char sFamily, int nGroup, int nDevice) {
    this->sendTriState(this->getCodeWordC(sFamily, nGroup, nDevice, false));
}

/**
 * Switch a remote switch on (Type B with two rotary/sliding switches)
 *
 * @param nAddressCode  Number of the switch group (1..4)
 * @param nChannelCode  Number of the switch itself (1..4)
 */
void RCSwitchLib::switchOn(int nAddressCode, int nChannelCode) {
    this->sendTriState(this->getCodeWordB(nAddressCode, nChannelCode, true));
}

/**
 * Switch a remote switch off (Type B with two rotary/sliding switches)
 *
 * @param nAddressCode  Number of the switch group (1..4)
 * @param nChannelCode  Number of the switch itself (1..4)
 */
void RCSwitchLib::switchOff(int nAddressCode, int nChannelCode) {
    this->sendTriState(this->getCodeWordB(nAddressCode, nChannelCode, false));
}

/**
 * Deprecated, use switchOn(char* sGroup, char* sDevice) instead!
 * Switch a remote switch on (Type A with 10 pole DIP switches)
 *
 * @param sGroup        Code of the switch group (refers to DIP switches 1..5 where "1" = on and "0" = off, if all DIP switches are on it's "11111")
 * @param nChannelCode  Number of the switch itself (1..5)
 */
void RCSwitchLib::switchOn(char *sGroup, int nChannel) {
    char *code[6] = {"00000", "10000", "01000", "00100", "00010", "00001"};
    this->switchOn(sGroup, code[nChannel]);
}

/**
 * Deprecated, use switchOff(char* sGroup, char* sDevice) instead!
 * Switch a remote switch off (Type A with 10 pole DIP switches)
 *
 * @param sGroup        Code of the switch group (refers to DIP switches 1..5 where "1" = on and "0" = off, if all DIP switches are on it's "11111")
 * @param nChannelCode  Number of the switch itself (1..5)
 */
void RCSwitchLib::switchOff(char *sGroup, int nChannel) {
    char *code[6] = {"00000", "10000", "01000", "00100", "00010", "00001"};
    this->switchOff(sGroup, code[nChannel]);
}

/**
 * Switch a remote switch on (Type A with 10 pole DIP switches)
 *
 * @param sGroup        Code of the switch group (refers to DIP switches 1..5 where "1" = on and "0" = off, if all DIP switches are on it's "11111")
 * @param sDevice       Code of the switch device (refers to DIP switches 6..10 (A..E) where "1" = on and "0" = off, if all DIP switches are on it's "11111")
 */
void RCSwitchLib::switchOn(char *sGroup, char *sDevice) {
    this->sendTriState(this->getCodeWordA(sGroup, sDevice, true));
}

/**
 * Switch a remote switch off (Type A with 10 pole DIP switches)
 *
 * @param sGroup        Code of the switch group (refers to DIP switches 1..5 where "1" = on and "0" = off, if all DIP switches are on it's "11111")
 * @param sDevice       Code of the switch device (refers to DIP switches 6..10 (A..E) where "1" = on and "0" = off, if all DIP switches are on it's "11111")
 */
void RCSwitchLib::switchOff(char *sGroup, char *sDevice) {
    this->sendTriState(this->getCodeWordA(sGroup, sDevice, false));
}

/**
 * Returns a char[13], representing the Code Word to be send.
 * A Code Word consists of 9 address bits, 3 data bits and one sync bit but in our case only the first 8 address bits and the last 2 data bits were used.
 * A Code Bit can have 4 different states: "F" (floating), "0" (low), "1" (high), "S" (synchronous bit)
 *
 * +-------------------------------+--------------------------------+-----------------------------------------+-----------------------------------------+----------------------+------------+
 * | 4 bits address (switch group) | 4 bits address (switch number) | 1 bit address (not used, so never mind) | 1 bit address (not used, so never mind) | 2 data bits (on|off) | 1 sync bit |
 * | 1=0FFF 2=F0FF 3=FF0F 4=FFF0   | 1=0FFF 2=F0FF 3=FF0F 4=FFF0    | F                                       | F                                       | on=FF off=F0         | S          |
 * +-------------------------------+--------------------------------+-----------------------------------------+-----------------------------------------+----------------------+------------+
 *
 * @param nAddressCode  Number of the switch group (1..4)
 * @param nChannelCode  Number of the switch itself (1..4)
 * @param bStatus       Wether to switch on (true) or off (false)
 *
 * @return char[13]
 */
char *RCSwitchLib::getCodeWordB(int nAddressCode, int nChannelCode, bool bStatus) {
    int nReturnPos = 0;
    static char sReturn[13];

    char *code[5] = {"FFFF", "0FFF", "F0FF", "FF0F", "FFF0"};
    if (nAddressCode < 1 || nAddressCode > 4 || nChannelCode < 1 || nChannelCode > 4) {
        return '\0';
    }
    for (int i = 0; i < 4; i++) {
        sReturn[nReturnPos++] = code[nAddressCode][i];
    }

    for (int i = 0; i < 4; i++) {
        sReturn[nReturnPos++] = code[nChannelCode][i];
    }

    sReturn[nReturnPos++] = 'F';
    sReturn[nReturnPos++] = 'F';
    sReturn[nReturnPos++] = 'F';

    if (bStatus) {
        sReturn[nReturnPos++] = 'F';
    } else {
        sReturn[nReturnPos++] = '0';
    }

    sReturn[nReturnPos] = '\0';

    return sReturn;
}

/**
 * Returns a char[13], representing the Code Word to be send.
 *
 * getCodeWordA(char*, char*)
 *
 */
char *RCSwitchLib::getCodeWordA(char *sGroup, char *sDevice, bool bOn) {
    static char sDipSwitches[13];
    int i = 0;
    int j = 0;

    for (i = 0; i < 5; i++) {
        if (sGroup[i] == '0') {
            sDipSwitches[j++] = 'F';
        } else {
            sDipSwitches[j++] = '0';
        }
    }

    for (i = 0; i < 5; i++) {
        if (sDevice[i] == '0') {
            sDipSwitches[j++] = 'F';
        } else {
            sDipSwitches[j++] = '0';
        }
    }

    if (bOn) {
        sDipSwitches[j++] = '0';
        sDipSwitches[j++] = 'F';
    } else {
        sDipSwitches[j++] = 'F';
        sDipSwitches[j++] = '0';
    }

    sDipSwitches[j] = '\0';

    return sDipSwitches;
}

/**
 * Like getCodeWord (Type C = Intertechno)
 */
char *RCSwitchLib::getCodeWordC(char sFamily, int nGroup, int nDevice, bool bStatus) {
    static char sReturn[13];
    int nReturnPos = 0;

    if ((uint8_t) sFamily < 97 || (uint8_t) sFamily > 112 || nGroup < 1 || nGroup > 4 || nDevice < 1 || nDevice > 4) {
        return '\0';
    }

    char *sDeviceGroupCode = dec2binWzerofill((nDevice - 1) + (nGroup - 1) * 4, 4);
    char familycode[16][5] = {"0000", "F000", "0F00", "FF00", "00F0", "F0F0", "0FF0", "FFF0", "000F", "F00F", "0F0F",
                              "FF0F", "00FF", "F0FF", "0FFF", "FFFF"};
    for (int i = 0; i < 4; i++) {
        sReturn[nReturnPos++] = familycode[(int) sFamily - 97][i];
    }
    for (int i = 0; i < 4; i++) {
        sReturn[nReturnPos++] = (sDeviceGroupCode[3 - i] == '1' ? 'F' : '0');
    }
    sReturn[nReturnPos++] = '0';
    sReturn[nReturnPos++] = 'F';
    sReturn[nReturnPos++] = 'F';
    if (bStatus) {
        sReturn[nReturnPos++] = 'F';
    } else {
        sReturn[nReturnPos++] = '0';
    }
    sReturn[nReturnPos] = '\0';
    return sReturn;
}

/**
 * Decoding for the REV Switch Type
 *
 * Returns a char[13], representing the Tristate to be send.
 * A Code Word consists of 7 address bits and 5 command data bits.
 * A Code Bit can have 3 different states: "F" (floating), "0" (low), "1" (high)
 *
 * +-------------------------------+--------------------------------+-----------------------+
 * | 4 bits address (switch group) | 3 bits address (device number) | 5 bits (command data) |
 * | A=1FFF B=F1FF C=FF1F D=FFF1   | 1=0FFF 2=F0FF 3=FF0F 4=FFF0    | on=00010 off=00001    |
 * +-------------------------------+--------------------------------+-----------------------+
 *
 * Source: http://www.the-intruder.net/funksteckdosen-von-rev-uber-arduino-ansteuern/
 *
 * @param sGroup        Name of the switch group (A..D, resp. a..d) 
 * @param nDevice       Number of the switch itself (1..3)
 * @param bStatus       Wether to switch on (true) or off (false)
 *
 * @return char[13]
 */

char *RCSwitchLib::getCodeWordD(char sGroup, int nDevice, bool bStatus) {
    static char sReturn[13];
    int nReturnPos = 0;

    // Building 4 bits address
    // (Potential problem if dec2binWcharfill not returning correct string)
    char *sGroupCode;
    switch (sGroup) {
        case 'a':
        case 'A':
            sGroupCode = dec2binWcharfill(8, 4, 'F');
            break;
        case 'b':
        case 'B':
            sGroupCode = dec2binWcharfill(4, 4, 'F');
            break;
        case 'c':
        case 'C':
            sGroupCode = dec2binWcharfill(2, 4, 'F');
            break;
        case 'd':
        case 'D':
            sGroupCode = dec2binWcharfill(1, 4, 'F');
            break;
        default:
            return '\0';
    }

    for (int i = 0; i < 4; i++) {
        sReturn[nReturnPos++] = sGroupCode[i];
    }


    // Building 3 bits address
    // (Potential problem if dec2binWcharfill not returning correct string)
    char *sDevice;
    switch (nDevice) {
        case 1:
            sDevice = dec2binWcharfill(4, 3, 'F');
            break;
        case 2:
            sDevice = dec2binWcharfill(2, 3, 'F');
            break;
        case 3:
            sDevice = dec2binWcharfill(1, 3, 'F');
            break;
        default:
            return '\0';
    }

    for (int i = 0; i < 3; i++)
        sReturn[nReturnPos++] = sDevice[i];

    // fill up rest with zeros
    for (int i = 0; i < 5; i++)
        sReturn[nReturnPos++] = '0';

    // encode on or off
    if (bStatus)
        sReturn[10] = '1';
    else
        sReturn[11] = '1';

    // last position terminate string
    sReturn[12] = '\0';
    return sReturn;

}

/**
 * @param sCodeWord   /^[10FS]*$/  -> see getCodeWord
 */
void RCSwitchLib::sendTriState(char *sCodeWord) {
    for (int nRepeat = 0; nRepeat < nRepeatTransmit; nRepeat++) {
        int i = 0;
        while (sCodeWord[i] != '\0') {
            switch (sCodeWord[i]) {
                case '0':
                    this->sendT0();
                    break;
                case 'F':
                    this->sendTF();
                    break;
                case '1':
                    this->sendT1();
                    break;
            }
            i++;
        }
        this->sendSync();
    }
}

void RCSwitchLib::send(unsigned long Code, unsigned int length) {
    this->send(this->dec2binWzerofill(Code, length));
}

void RCSwitchLib::send(char *sCodeWord) {
    for (int nRepeat = 0; nRepeat < nRepeatTransmit; nRepeat++) {
        int i = 0;
        while (sCodeWord[i] != '\0') {
            switch (sCodeWord[i]) {
                case '0':
                    this->send0();
                    break;
                case '1':
                    this->send1();
                    break;
            }
            i++;
        }
        this->sendSync();
    }
}

void RCSwitchLib::transmit(int nHighPulses, int nLowPulses) {
#ifndef RCSwitchLibDisableReceiving
    bool disabled_Receive = false;
    int nReceiverInterrupt_backup = nReceiverInterrupt;
#endif
    if (this->nTransmitterPin != -1) {
#ifndef  RCSwitchLibDisableReceiving
        if (this->nReceiverInterrupt != -1) {
            this->disableReceive();
            disabled_Receive = true;
        }
#endif

        using namespace RCSwitch;

        *transmitPORT |= (1 << transmitBIT);
        delayMicroseconds(this->nPulseLength * nHighPulses);
        *transmitPORT &= ~(1 << transmitBIT);
        delayMicroseconds(this->nPulseLength * nLowPulses);

#ifndef  RCSwitchLibDisableReceiving
        if (disabled_Receive) {
            this->enableReceive(nReceiverInterrupt_backup);
        }
#endif
    }
}

/**
 * Sends a "0" Bit
 *                       _    
 * Waveform Protocol 1: | |___
 *                       _  
 * Waveform Protocol 2: | |__
 */
void RCSwitchLib::send0() {
    if (this->nProtocol == 1) {
        this->transmit(1, 3);
    } else if (this->nProtocol == 2) {
        this->transmit(1, 2);
    } else if (this->nProtocol == 3) {
        this->transmit(4, 11);
    }
}

/**
 * Sends a "1" Bit
 *                       ___  
 * Waveform Protocol 1: |   |_
 *                       __  
 * Waveform Protocol 2: |  |_
 */
void RCSwitchLib::send1() {
    if (this->nProtocol == 1) {
        this->transmit(3, 1);
    } else if (this->nProtocol == 2) {
        this->transmit(2, 1);
    } else if (this->nProtocol == 3) {
        this->transmit(9, 6);
    }
}


/**
 * Sends a Tri-State "0" Bit
 *            _     _
 * Waveform: | |___| |___
 */
void RCSwitchLib::sendT0() {
    this->transmit(1, 3);
    this->transmit(1, 3);
}

/**
 * Sends a Tri-State "1" Bit
 *            ___   ___
 * Waveform: |   |_|   |_
 */
void RCSwitchLib::sendT1() {
    this->transmit(3, 1);
    this->transmit(3, 1);
}

/**
 * Sends a Tri-State "F" Bit
 *            _     ___
 * Waveform: | |___|   |_
 */
void RCSwitchLib::sendTF() {
    this->transmit(1, 3);
    this->transmit(3, 1);
}

/**
 * Sends a "Sync" Bit
 *                       _
 * Waveform Protocol 1: | |_______________________________
 *                       _
 * Waveform Protocol 2: | |__________
 */
void RCSwitchLib::sendSync() {

    if (this->nProtocol == 1) {
        this->transmit(1, 31);
    } else if (this->nProtocol == 2) {
        this->transmit(1, 10);
    } else if (this->nProtocol == 3) {
        this->transmit(1, 71);
    }
}

#ifndef  RCSwitchLibDisableReceiving

/**
 * Enable receiving data
 */
void RCSwitchLib::enableReceive(int interrupt) {
    this->nReceiverInterrupt = interrupt;
    this->enableReceive();
}

void RCSwitchLib::enableReceive() {
    if (this->nReceiverInterrupt != -1) {
        RCSwitchLib::nReceivedValue = 0;
        RCSwitchLib::nReceivedBitlength = 0;
//        attachInterrupt(this->nReceiverInterrupt, handleInterrupt, CHANGE);
    }
}

/**
 * Disable receiving data
 */
void RCSwitchLib::disableReceive() {
//    detachInterrupt(this->nReceiverInterrupt);
    this->nReceiverInterrupt = -1;
}

bool RCSwitchLib::available() {
    return RCSwitchLib::nReceivedValue != 0;
}

void RCSwitchLib::resetAvailable() {
    RCSwitchLib::nReceivedValue = 0;
}

unsigned long RCSwitchLib::getReceivedValue() {
    return RCSwitchLib::nReceivedValue;
}

unsigned int RCSwitchLib::getReceivedBitlength() {
    return RCSwitchLib::nReceivedBitlength;
}

unsigned int RCSwitchLib::getReceivedDelay() {
    return RCSwitchLib::nReceivedDelay;
}

unsigned int RCSwitchLib::getReceivedProtocol() {
    return RCSwitchLib::nReceivedProtocol;
}

unsigned int *RCSwitchLib::getReceivedRawdata() {
    return RCSwitchLib::timings;
}

/**
 *
 */
bool RCSwitchLib::receiveProtocol1(unsigned int changeCount) {

    unsigned long code = 0;
    unsigned long delay = RCSwitchLib::timings[0] / 31;
    unsigned long delayTolerance = delay * RCSwitchLib::nReceiveTolerance * 0.01;

    for (unsigned int i = 1; i < changeCount; i = i + 2) {

        if (RCSwitchLib::timings[i] > delay - delayTolerance && RCSwitchLib::timings[i] < delay + delayTolerance &&
            RCSwitchLib::timings[i + 1] > delay * 3 - delayTolerance &&
            RCSwitchLib::timings[i + 1] < delay * 3 + delayTolerance) {
            code = code << 1;
        } else if (RCSwitchLib::timings[i] > delay * 3 - delayTolerance &&
                   RCSwitchLib::timings[i] < delay * 3 + delayTolerance &&
                   RCSwitchLib::timings[i + 1] > delay - delayTolerance &&
                   RCSwitchLib::timings[i + 1] < delay + delayTolerance) {
            code += 1;
            code = code << 1;
        } else {
            // Failed
            i = changeCount;
            code = 0;
        }
    }
    code = code >> 1;
    if (changeCount > 6) {    // ignore < 4bit values as there are no devices sending 4bit values => noise
        RCSwitchLib::nReceivedValue = code;
        RCSwitchLib::nReceivedBitlength = changeCount / 2;
        RCSwitchLib::nReceivedDelay = delay;
        RCSwitchLib::nReceivedProtocol = 1;
    }

    if (code == 0) {
        return false;
    } else {
        return true;
    }


}

bool RCSwitchLib::receiveProtocol2(unsigned int changeCount) {

    unsigned long code = 0;
    unsigned long delay = RCSwitchLib::timings[0] / 10;
    unsigned long delayTolerance = delay * RCSwitchLib::nReceiveTolerance * 0.01;

    for (unsigned int i = 1; i < changeCount; i = i + 2) {

        if (RCSwitchLib::timings[i] > delay - delayTolerance && RCSwitchLib::timings[i] < delay + delayTolerance &&
            RCSwitchLib::timings[i + 1] > delay * 2 - delayTolerance &&
            RCSwitchLib::timings[i + 1] < delay * 2 + delayTolerance) {
            code = code << 1;
        } else if (RCSwitchLib::timings[i] > delay * 2 - delayTolerance &&
                   RCSwitchLib::timings[i] < delay * 2 + delayTolerance &&
                   RCSwitchLib::timings[i + 1] > delay - delayTolerance &&
                   RCSwitchLib::timings[i + 1] < delay + delayTolerance) {
            code += 1;
            code = code << 1;
        } else {
            // Failed
            i = changeCount;
            code = 0;
        }
    }
    code = code >> 1;
    if (changeCount > 6) {    // ignore < 4bit values as there are no devices sending 4bit values => noise
        RCSwitchLib::nReceivedValue = code;
        RCSwitchLib::nReceivedBitlength = changeCount / 2;
        RCSwitchLib::nReceivedDelay = delay;
        RCSwitchLib::nReceivedProtocol = 2;
    }

    if (code == 0) {
        return false;
    } else {
        return true;
    }

}

/** Protocol 3 is used by BL35P02.
 *
 */
bool RCSwitchLib::receiveProtocol3(unsigned int changeCount) {

    unsigned long code = 0;
    unsigned long delay = RCSwitchLib::timings[0] / PROTOCOL3_SYNC_FACTOR;
    unsigned long delayTolerance = delay * RCSwitchLib::nReceiveTolerance * 0.01;

    for (unsigned int i = 1; i < changeCount; i = i + 2) {

        if (RCSwitchLib::timings[i] > delay * PROTOCOL3_0_HIGH_CYCLES - delayTolerance
            && RCSwitchLib::timings[i] < delay * PROTOCOL3_0_HIGH_CYCLES + delayTolerance
            && RCSwitchLib::timings[i + 1] > delay * PROTOCOL3_0_LOW_CYCLES - delayTolerance
            && RCSwitchLib::timings[i + 1] < delay * PROTOCOL3_0_LOW_CYCLES + delayTolerance) {
            code = code << 1;
        } else if (RCSwitchLib::timings[i] > delay * PROTOCOL3_1_HIGH_CYCLES - delayTolerance
                   && RCSwitchLib::timings[i] < delay * PROTOCOL3_1_HIGH_CYCLES + delayTolerance
                   && RCSwitchLib::timings[i + 1] > delay * PROTOCOL3_1_LOW_CYCLES - delayTolerance
                   && RCSwitchLib::timings[i + 1] < delay * PROTOCOL3_1_LOW_CYCLES + delayTolerance) {
            code += 1;
            code = code << 1;
        } else {
            // Failed
            i = changeCount;
            code = 0;
        }
    }
    code = code >> 1;
    if (changeCount > 6) {    // ignore < 4bit values as there are no devices sending 4bit values => noise
        RCSwitchLib::nReceivedValue = code;
        RCSwitchLib::nReceivedBitlength = changeCount / 2;
        RCSwitchLib::nReceivedDelay = delay;
        RCSwitchLib::nReceivedProtocol = 3;
    }

    if (code == 0) {
        return false;
    } else {
        return true;
    }
}

void RCSwitchLib::handleInterrupt() {

    static unsigned int duration;
    static unsigned int changeCount;
    static unsigned long lastTime;
    static unsigned int repeatCount;


    long time = micros();
    duration = time - lastTime;

    if (duration > 5000 && duration > RCSwitchLib::timings[0] - 200 && duration < RCSwitchLib::timings[0] + 200) {
        repeatCount++;
        changeCount--;
        if (repeatCount == 2) {
            if (receiveProtocol1(changeCount) == false) {
                if (receiveProtocol2(changeCount) == false) {
                    if (receiveProtocol3(changeCount) == false) {
                        //failed
                    }
                }
            }
            repeatCount = 0;
        }
        changeCount = 0;
    } else if (duration > 5000) {
        changeCount = 0;
    }

    if (changeCount >= RCSwitchLib_MAX_CHANGES) {
        changeCount = 0;
        repeatCount = 0;
    }
    RCSwitchLib::timings[changeCount++] = duration;
    lastTime = time;
}

#endif

/**
  * Turns a decimal value to its binary representation
  */
char *RCSwitchLib::dec2binWzerofill(unsigned long Dec, unsigned int bitLength) {
    return dec2binWcharfill(Dec, bitLength, '0');
}

char *RCSwitchLib::dec2binWcharfill(unsigned long Dec, unsigned int bitLength, char fill) {
    static char bin[64];
    unsigned int i = 0;

    while (Dec > 0) {
        bin[32 + i++] = ((Dec & 1) > 0) ? '1' : fill;
        Dec = Dec >> 1;
    }

    for (unsigned int j = 0; j < bitLength; j++) {
        if (j >= bitLength - i) {
            bin[j] = bin[31 + i - (j - (bitLength - i))];
        } else {
            bin[j] = fill;
        }
    }
    bin[bitLength] = '\0';

    return bin;
}



