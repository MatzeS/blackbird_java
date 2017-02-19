package blackbird.core.avr;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class ByteHelper {

    public static byte setBit(byte byteValue, int bit, boolean highLevel) {
        if (highLevel) {
            return (byte) (byteValue | bitMaskHigh(bit));
        } else {
            return (byte) (byteValue & bitMaskLow(bit));
        }
    }

    private static byte bitMaskHigh(int bit) {
        return (byte) (1 << bit);
    }

    private static int bitMaskLow(int bit) {
        int mask = 0;
        for (int eachBit = 8 - 1; eachBit >= 0; eachBit--) {
            mask |= (eachBit == bit ? 0 : 1);
            if (eachBit > 0)
                mask <<= 1;
        }
        return mask;
    }

    public static int decode2Byte(byte[] data, int index) {
        return data[index + 1] | (data[index] << 8);
    }

    public static int decode4Byte(byte[] data, int index) {
        return ByteBuffer.wrap(data).order(ByteOrder.BIG_ENDIAN).getInt(index);
    }

    public static long decode8Byte(byte[] data) {
        return decode8Byte(data, 0);
    }

    public static long decode8Byte(byte[] data, int index) {
        return ByteBuffer.wrap(data).order(ByteOrder.BIG_ENDIAN).getLong(index);
        // return (long)decode4Byte(data, index + 4) | ((long)decode4Byte(data, index) << 32);
    }

    public static float decodeFloat(byte[] data, int start) {
        byte[] bytes = new byte[4];
        System.arraycopy(data, start, bytes, 0, 4);
        return ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).getFloat();
    }

    /**
     * The MSB is placed in the first byte.
     */
    public static byte[] encode(long value) {
        byte[] data = new byte[8];
        for (int i = 0; i < data.length; i++)
            data[7 - i] = (byte) (0xFF & (value >> (8 * i)));
        return data;
    }

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

    public static boolean testBit(int i, int index) {
        return (i & (1L << index)) != 0;
    }

    public static boolean testBit(byte b, int index) {
        return (b & (1L << index)) != 0;
    }

}
