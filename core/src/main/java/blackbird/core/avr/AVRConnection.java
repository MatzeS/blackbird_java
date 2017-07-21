package blackbird.core.avr;

import blackbird.core.avr.DS18B20.DS18B20Parser;
import blackbird.core.avr.i2c.I2CParser;
import blackbird.core.avr.packets.TransmittableAVRPacket;
import blackbird.core.avr.parsers.*;
import blackbird.core.connection.Connection;
import blackbird.core.connection.Packet;
import blackbird.core.connection.PacketConnection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.function.Predicate;

public abstract class AVRConnection extends PacketConnection {

    private Logger logger = LogManager.getLogger(AVRConnection.class);

    private HashMap<Byte, PacketParser> packetParsers;

    //parsing buffer values
    private Expect expect;
    private byte commandByte;
    private int dataIndex;
    private byte[] data;


    public AVRConnection(Connection connection) throws IOException {

        super(connection);

        packetParsers = new HashMap<>();

        addPacketParser(new DeviceIdentificationParser());
        addPacketParser(new I2CParser());
        addPacketParser(new CapSensePinParser());
        addPacketParser(new BasicPinParser());
        addPacketParser(new RCReceiveParser());
        addPacketParser(new IRParser());
        addPacketParser(new CommonInterruptParser());
        addPacketParser(new DS18B20Parser());

    }


    public void addPacketParser(PacketParser packetParser) {

        packetParsers.put(packetParser.getCommandByte(), packetParser);
    }


    public void consumeInputStream() {

        try {
            InputStream inputStream = getInputStream();

            while (inputStream.available() > 0) {

                int read = inputStream.read();

                // end of stream
                if (read == -1)
                    throw new IOException("end of stream");

                parse((byte) read);

            }
        } catch (IOException e) {
            logger.error("I/O Exception, error on input stream", e);
        }
    }


    @Override
    protected void firePacketReceived(Packet packet) {

        super.firePacketReceived(packet);
    }


    private void parse(byte incomingByte) {

        //logger.info("rec: " + Integer.toHexString(incomingByte & 0xFF) + "/" + incomingByte);

        if (incomingByte == (byte) 0xFF) {

            // expecting DATA when the last byte was an escape byte and ESC when the last 8 bytes were 7 data bytes and their ESC byte
            if (expect == Expect.DATA || expect == Expect.ESC) {

                // last block was not full and is not escaped
                if ((dataIndex - 1) % 8 != 0) {
                    byte escByte = data[dataIndex - 1];
                    int leftBytes = (dataIndex - 1) % 8;
                    for (byte i = 0; i < leftBytes; i++)
                        if ((escByte & (1L << i)) != 0)
                            data[dataIndex - 1 - 1 - i] = (byte) 0xFF;

                    dataIndex = dataIndex - 1;
                }

                //logger.info("parsing");

                PacketParser parser = packetParsers.get(commandByte);

                if (parser != null) {

                    byte[] data = new byte[dataIndex];
                    System.arraycopy(this.data, 0, data, 0, dataIndex);
                    try {
                        Packet packet = parser.parse(data);
                        if (packet != null) {
                            //logger.info(packet);
                            firePacketReceived(packet);

                        } else
                            logger.error("parser returned null package");

                    } catch (Exception e) {
                        logger.error(e);
                    }

                } else
                    logger.error("no parser found");

            }

            expect = Expect.CMD;

        } else if (expect == Expect.CMD) {

            commandByte = incomingByte;

            dataIndex = 0;
            data = new byte[256]; //TODO

            expect = Expect.DATA;

        } else if (expect == Expect.DATA) {

            data[dataIndex] = incomingByte;
            dataIndex++;

            if (dataIndex % 8 == 0)
                expect = Expect.ESC;


        } else if (expect == Expect.ESC) {

            byte fullFlag = data[dataIndex - 1];

            for (int i = 0; i < 8; i++)
                if ((incomingByte & (1L << i)) != 0) {
                    data[dataIndex - 1 - i] = (byte) 0xFF;
                }

            if (incomingByte == (byte) 0x7F) // special possible full 8x 0xFF block
                if (fullFlag == (byte) 0xF0)
                    data[dataIndex - 8] = (byte) 0xFF;

            expect = Expect.DATA;

        } else
            logger.error("unhandled byte");   //TODO error

    }


    public void removePacketParser(PacketParser packetParser) {

        packetParsers.remove(packetParser.getCommandByte());
    }


    @Override
    public void send(Packet packet) throws IOException {

        if (packet instanceof TransmittableAVRPacket)
            send((TransmittableAVRPacket) packet);
        else
            throw new UnsupportedOperationException("a avr connection can only send TransmittableAVRPacket's");
    }


    public synchronized void send(TransmittableAVRPacket packet) throws IOException {

        byte cmdByte = packet.getCommandByte();
        //check command byte

        byte[] data = packet.toByteArray();
        ByteArrayOutputStream transmitData = new ByteArrayOutputStream();

        transmitData.write(0xFF);
        transmitData.write(cmdByte);

        for (int dataIndex = 0; dataIndex < data.length; dataIndex++) {

            byte dataByte = data[dataIndex];
            if (dataByte != (byte) 0xFF)
                transmitData.write(dataByte);
                // last byte in block encodes a full "0xFF" block
            else if ((dataIndex + 1) % 8 == 0) {

                // check if the block is a full start byte block
                boolean fullBlock = true;
                for (int i = 1; i < 8; i++)
                    if (data[dataIndex - i] != (byte) 0xFF) {
                        fullBlock = false;
                        break;
                    }

                if (fullBlock)
                    transmitData.write(0xF0); // full block flag in the last block byte
                else
                    transmitData.write(0x00);


            } else
                transmitData.write(0x00); // empty data byte, decoded to start byte via escape byte


            if ((dataIndex + 1) % 8 == 0) {
                // end of block -> send escape byte

                byte escapeByte = 0x00;
                // set escape byte bits
                for (int i = 0; i < 8; i++)
                    if (data[dataIndex - i] == (byte) 0xFF)
                        escapeByte |= (1 << i);

                // avoid full escape byte
                if (escapeByte == (byte) 0xFF)
                    escapeByte = (byte) 0x7F;

                transmitData.write(escapeByte);

            } else if ((dataIndex + 1) == data.length) {
                // end of packet -> last escape byte escaping not 8 bytes

                byte escapeByte = 0x00;
                // set escape byte bits
                int leftBytes = dataIndex % 8 + 1;
                for (int i = 0; i < leftBytes; i++)
                    if (data[dataIndex - i] == (byte) 0xFF)
                        escapeByte |= (1 << i);

                transmitData.write(escapeByte);

            }

        }

        transmitData.write(0xFF); //DELIMITER END BYTE

        //data = transmitData.toByteArray();
        //for (int i = 0; i < data.length; i++)
        //    logger.info(ByteHelper.toHex(data[i]));

        getOutputStream().write(transmitData.toByteArray());
        getOutputStream().flush();

    }


    @Override
    public synchronized <R extends Packet> R sendAndReceive(Packet request, Class<R> expectedPacketType) throws IOException {

        return super.sendAndReceive(request, expectedPacketType);
    }


    @Override
    public synchronized <R extends Packet> R sendAndReceive(Packet request, Class<R> expectedPacketType, long timeout) throws IOException {

        return super.sendAndReceive(request, expectedPacketType, timeout);
    }


    @Override
    public synchronized Packet sendAndReceive(Packet request, Predicate<Packet> filter, long timeout) throws IOException {

        return super.sendAndReceive(request, filter, timeout);
    }


    @Override
    public synchronized <R extends Packet> R sendAndReceive(Packet request, Class<R> expectedPacketType, Predicate<R> filter) throws IOException {

        return super.sendAndReceive(request, expectedPacketType, filter);
    }


    @Override
    public synchronized <R extends Packet> R sendAndReceive(Packet request, Class<R> expectedPacketType, Predicate<R> filter, long timeout) throws IOException {

        return super.sendAndReceive(request, expectedPacketType, filter, timeout);
    }


    @Override
    public synchronized Packet sendAndReceive(Packet request, Predicate<Packet> filter) throws IOException {

        return super.sendAndReceive(request, filter);
    }


    @Override
    public synchronized Packet sendAndReceiveAnswer(
            Packet request,
            Predicate<Packet> filter,
            long timeout) throws IOException {

        throw new UnsupportedOperationException("avr's have no answer mechanic");
    }


    enum Expect {
        NOTHING, CMD, DATA, ESC
    }

}
