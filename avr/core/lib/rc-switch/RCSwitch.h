/*
  RCSwitchLib - Arduino libary for remote control outlet switches
  Copyright (c) 2011 Suat �zg�r.  All right reserved.

  Contributors:
  - Andre Koehler / info(at)tomate-online(dot)de
  - Gordeev Andrey Vladimirovich / gordeev(at)openpyro(dot)com
  - Skineffect / http://forum.ardumote.com/viewtopic.php?f=2&t=46
  - Dominik Fischer / dom_fischer(at)web(dot)de
  - Frank Oltmanns / <first name>.<last name>(at)gmail(dot)com
  
  Project home: http://code.google.com/p/rc-switch/

  This library is free software; you can redistribute it and/or
  modify it under the terms of the GNU Lesser General Public
  License as published by the Free Software Foundation; either
  version 2.1 of the License, or (at your option) any later version.

  This library is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
  Lesser General Public License for more details.

  You should have received a copy of the GNU Lesser General Public
  License along with this library; if not, write to the Free Software
  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
*/
#ifndef _RCSwitchLib_h
#define _RCSwitchLib_h

#include "../AVRTools/SystemClock.h"

namespace RCSwitch {

    extern volatile uint8_t *transmitDDR, *transmitPORT;
    extern volatile uint8_t transmitBIT;

}



// Number of maximum High/Low changes per packet.
// We can handle up to (unsigned long) => 32 bit * 2 H/L changes per bit + 2 for sync
#define RCSwitchLib_MAX_CHANGES 67

#define PROTOCOL3_SYNC_FACTOR   71
#define PROTOCOL3_0_HIGH_CYCLES  4
#define PROTOCOL3_0_LOW_CYCLES  11
#define PROTOCOL3_1_HIGH_CYCLES  9
#define PROTOCOL3_1_LOW_CYCLES   6


#include <stddef.h>

class RCSwitchLib {

public:
    RCSwitchLib();

    void switchOn(int nGroupNumber, int nSwitchNumber);

    void switchOff(int nGroupNumber, int nSwitchNumber);

    void switchOn(char *sGroup, int nSwitchNumber);

    void switchOff(char *sGroup, int nSwitchNumber);

    void switchOn(char sFamily, int nGroup, int nDevice);

    void switchOff(char sFamily, int nGroup, int nDevice);

    void switchOn(char *sGroup, char *sDevice);

    void switchOff(char *sGroup, char *sDevice);

    void switchOn(char sGroup, int nDevice);

    void switchOff(char sGroup, int nDevice);

    void sendTriState(char *Code);

    void send(unsigned long Code, unsigned int length);

    void send(char *Code);

#ifndef  RCSwitchLibDisableReceiving

    void enableReceive(int interrupt);

    void enableReceive();

    void disableReceive();

    bool available();

    void resetAvailable();

    unsigned long getReceivedValue();

    unsigned int getReceivedBitlength();

    unsigned int getReceivedDelay();

    unsigned int getReceivedProtocol();

    unsigned int *getReceivedRawdata();

#endif

    void enableTransmit(int nTransmitterPin);

    void disableTransmit();

    void setPulseLength(int nPulseLength);

    void setRepeatTransmit(int nRepeatTransmit);

#ifndef  RCSwitchLibDisableReceiving

    void setReceiveTolerance(int nPercent);

#endif

    void setProtocol(int nProtocol);

    void setProtocol(int nProtocol, int nPulseLength);

    static void handleInterrupt();

private:
    char *getCodeWordB(int nGroupNumber, int nSwitchNumber, bool bStatus);

    char *getCodeWordA(char *sGroup, int nSwitchNumber, bool bStatus);

    char *getCodeWordA(char *sGroup, char *sDevice, bool bStatus);

    char *getCodeWordC(char sFamily, int nGroup, int nDevice, bool bStatus);

    char *getCodeWordD(char group, int nDevice, bool bStatus);

    void sendT0();

    void sendT1();

    void sendTF();

    void send0();

    void send1();

    void sendSync();

    void transmit(int nHighPulses, int nLowPulses);

    static char *dec2binWzerofill(unsigned long dec, unsigned int length);

    static char *dec2binWcharfill(unsigned long dec, unsigned int length, char fill);

#ifndef  RCSwitchLibDisableReceiving

    static bool receiveProtocol1(unsigned int changeCount);

    static bool receiveProtocol2(unsigned int changeCount);

    static bool receiveProtocol3(unsigned int changeCount);

    int nReceiverInterrupt;
#endif
    int nTransmitterPin;
    int nPulseLength;
    int nRepeatTransmit;
    char nProtocol;

#ifndef  RCSwitchLibDisableReceiving
    static int nReceiveTolerance;
    static unsigned long nReceivedValue;
    static unsigned int nReceivedBitlength;
    static unsigned int nReceivedDelay;
    static unsigned int nReceivedProtocol;
#endif
    /* 
     * timings[0] contains sync timing, followed by a number of bits
     */
    static unsigned int timings[RCSwitchLib_MAX_CHANGES];


};

#endif
