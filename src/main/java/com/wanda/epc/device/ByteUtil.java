package com.wanda.epc.device;

import java.io.ByteArrayOutputStream;

/**
 * JAVA类型 与C 类型转换
 *
 * @author ssc
 */
public class ByteUtil {
    // 以下 是整型数 和 网络字节序的 byte[] 数组之间的转换

    /**
     * long 转 byte[]
     *
     * @param long
     * @return byte[]
     */
    public static byte[] longToBytes(long n) {
        byte[] b = new byte[8];
        b[7] = (byte) (n & 0xff);
        b[6] = (byte) (n >> 8 & 0xff);
        b[5] = (byte) (n >> 16 & 0xff);
        b[4] = (byte) (n >> 24 & 0xff);
        b[3] = (byte) (n >> 32 & 0xff);
        b[2] = (byte) (n >> 40 & 0xff);
        b[1] = (byte) (n >> 48 & 0xff);
        b[0] = (byte) (n >> 56 & 0xff);
        return b;
    }

    /**
     * @param long
     * @param array  bytes数组
     * @param offset 起始位
     */
    public static void longToBytes(long n, byte[] array, int offset) {
        array[7 + offset] = (byte) (n & 0xff);
        array[6 + offset] = (byte) (n >> 8 & 0xff);
        array[5 + offset] = (byte) (n >> 16 & 0xff);
        array[4 + offset] = (byte) (n >> 24 & 0xff);
        array[3 + offset] = (byte) (n >> 32 & 0xff);
        array[2 + offset] = (byte) (n >> 40 & 0xff);
        array[1 + offset] = (byte) (n >> 48 & 0xff);
        array[0 + offset] = (byte) (n >> 56 & 0xff);
    }

    /**
     * bytes转long
     *
     * @param bytes
     * @return
     */
    public static long bytesToLong(byte[] array) {
        return ((((long) array[0] & 0xff) << 56)
                | (((long) array[1] & 0xff) << 48)
                | (((long) array[2] & 0xff) << 40)
                | (((long) array[3] & 0xff) << 32)
                | (((long) array[4] & 0xff) << 24)
                | (((long) array[5] & 0xff) << 16)
                | (((long) array[6] & 0xff) << 8) | (((long) array[7] & 0xff) << 0));
    }

    /**
     * byte 转long
     *
     * @param array  bytes数组
     * @param offset 起始位
     * @return
     */
    public static long bytesToLong(byte[] array, int offset) {
        return ((((long) array[offset + 0] & 0xff) << 56)
                | (((long) array[offset + 1] & 0xff) << 48)
                | (((long) array[offset + 2] & 0xff) << 40)
                | (((long) array[offset + 3] & 0xff) << 32)
                | (((long) array[offset + 4] & 0xff) << 24)
                | (((long) array[offset + 5] & 0xff) << 16)
                | (((long) array[offset + 6] & 0xff) << 8) | (((long) array[offset + 7] & 0xff) << 0));
    }

    /**
     * int 转 bytes
     *
     * @param n
     * @return
     */
    public static byte[] intToBytes(int n) {
        byte[] b = new byte[4];
        b[3] = (byte) (n & 0xff);
        b[2] = (byte) (n >> 8 & 0xff);
        b[1] = (byte) (n >> 16 & 0xff);
        b[0] = (byte) (n >> 24 & 0xff);
        return b;
    }

    public static void intToBytes(int n, byte[] array, int offset) {
        array[3 + offset] = (byte) (n & 0xff);
        array[2 + offset] = (byte) (n >> 8 & 0xff);
        array[1 + offset] = (byte) (n >> 16 & 0xff);
        array[offset] = (byte) (n >> 24 & 0xff);
    }

    /**
     * INT 类型的 byte高字节放到低地址上
     *
     * @param b
     * @return
     */
    public static byte[] intHToL(byte[] b) {
        byte[] temp = new byte[b.length];
        for (int i = 0; i < temp.length; i++) {
            temp[i] = b[b.length - 1 - i];
        }
        return temp;
    }

    /**
     * byte 转 int
     *
     * @param b
     * @return
     */
    public static int bytesToInt(byte b[]) {
        return b[3] & 0xff | (b[2] & 0xff) << 8 | (b[1] & 0xff) << 16
                | (b[0] & 0xff) << 24;
    }

    /**
     * byte 转 int
     *
     * @param byte
     * @param offset 起始位
     * @return
     */
    public static int bytesToInt(byte b[], int offset) {
        return b[offset + 3] & 0xff | (b[offset + 2] & 0xff) << 8
                | (b[offset + 1] & 0xff) << 16 | (b[offset] & 0xff) << 24;
    }

    public static int getInt(byte[] bytes, int offset) {
        return (0xff & bytes[offset + 0]) | (0xff00 & (bytes[offset + 1] << 8))
                | (0xff0000 & (bytes[offset + 2] << 16))
                | (0xff000000 & (bytes[offset + 3] << 24));
    }

    /**
     * uint 转 byte
     *
     * @param uint
     * @return
     */
    public static byte[] uintToBytes(long uint) {
        byte[] b = new byte[4];
        b[3] = (byte) (uint & 0xff);
        b[2] = (byte) (uint >> 8 & 0xff);
        b[1] = (byte) (uint >> 16 & 0xff);
        b[0] = (byte) (uint >> 24 & 0xff);

        return b;
    }

    /**
     * uint 转 byte
     *
     * @param uint
     * @param byte   数组
     * @param offset 起始位
     * @return
     */
    public static void uintToBytes(long n, byte[] array, int offset) {
        array[3 + offset] = (byte) (n);
        array[2 + offset] = (byte) (n >> 8 & 0xff);
        array[1 + offset] = (byte) (n >> 16 & 0xff);
        array[offset] = (byte) (n >> 24 & 0xff);
    }

    /**
     * bytes 转 uint
     *
     * @param array
     * @return
     */
    public static long bytesToUint(byte[] array) {
        return ((long) (array[3] & 0xff)) | ((long) (array[2] & 0xff)) << 8
                | ((long) (array[1] & 0xff)) << 16
                | ((long) (array[0] & 0xff)) << 24;
    }

    /**
     * bytes 转 uint
     *
     * @param array  数组
     * @param offset 起始位置
     * @return
     */
    public static long bytesToUint(byte[] array, int offset) {
        return ((long) (array[offset + 3] & 0xff))
                | ((long) (array[offset + 2] & 0xff)) << 8
                | ((long) (array[offset + 1] & 0xff)) << 16
                | ((long) (array[offset] & 0xff)) << 24;
    }

    /**
     * short 转 bytes
     *
     * @param n
     * @return
     */
    public static byte[] shortToBytes(short n) {
        byte[] b = new byte[2];
        b[1] = (byte) (n & 0xff);
        b[0] = (byte) ((n >> 8) & 0xff);
        return b;
    }

    /**
     * short 加入到bytes 指定位置
     *
     * @param n      short
     * @param bytes  数组
     * @param offset 起始位
     */
    public static void shortToBytes(short n, byte[] array, int offset) {
        array[offset + 1] = (byte) (n & 0xff);
        array[offset] = (byte) ((n >> 8) & 0xff);
    }

    /**
     * byts 转 short
     *
     * @param b
     * @return
     */
    public static short bytesToShort(byte[] b) {
        return (short) (b[1] & 0xff | (b[0] & 0xff) << 8);
    }

    /**
     * byts 转 short
     *
     * @param byte
     * @param offset 起始位
     * @return
     */
    public static short bytesToShort(byte[] b, int offset) {
        return (short) (b[offset + 1] & 0xff | (b[offset] & 0xff) << 8);
    }

    /**
     * 2 byts 转 int
     *
     * @param byte
     * @param offset 起始位
     * @return
     */
    public static int bytesToUnInt(byte[] b, int offset) {
        return (int) (b[offset + 1] & 0xff | (b[offset] & 0xff) << 8);
    }

    /**
     * 小端
     *
     * @param b
     * @param offset
     * @return
     */
    public static short bytesToShortXd(byte[] b, int offset) {
        return (short) (b[offset] & 0xff | (b[offset + 1] & 0xff) << 8);
    }

    public static byte[] ushortToBytes(int n) {
        byte[] b = new byte[2];
        b[1] = (byte) (n & 0xff);
        b[0] = (byte) ((n >> 8) & 0xff);
        return b;
    }

    public static void ushortToBytes(int n, byte[] array, int offset) {
        array[offset + 1] = (byte) (n & 0xff);
        array[offset] = (byte) ((n >> 8) & 0xff);
    }

    public static int bytesToUshort(byte b[]) {
        return b[1] & 0xff | (b[0] & 0xff) << 8;
    }

    public static int bytesToUshort(byte b[], int offset) {
        return b[offset + 1] & 0xff | (b[offset] & 0xff) << 8;
    }

    public static byte[] ubyteToBytes(int n) {
        byte[] b = new byte[1];
        b[0] = (byte) (n & 0xff);
        return b;
    }

    public static void ubyteToBytes(int n, byte[] array, int offset) {
        array[0] = (byte) (n & 0xff);
    }

    public static int bytesToUbyte(byte[] array) {
        return array[0] & 0xff;
    }

    public static int bytesToUbyte(byte[] array, int offset) {
        return array[offset] & 0xff;
    }

    // char 类型、 float、double 类型和 byte[] 数组之间的转换关系还需继续研究实现。

    /**
     * Convert a String of hexadecimal digits into the corresponding byte array
     * by encoding each two hexadecimal digits as a byte.
     *
     * @param digits Hexadecimal digits representation
     * @throws IllegalArgumentException if an invalid hexadecimal digit is found, or the input
     *                                  string contains an odd number of hexadecimal digits
     */
    public static byte[] hexStringToByteArray(String digits) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        for (int i = 0; i < digits.length(); i += 2) {
            char c1 = digits.charAt(i);
            if ((i + 1) >= digits.length()) {
                throw new IllegalArgumentException("hexUtil.odd");
            }
            char c2 = digits.charAt(i + 1);
            byte b = 0;
            if ((c1 >= '0') && (c1 <= '9'))
                b += ((c1 - '0') * 16);
            else if ((c1 >= 'a') && (c1 <= 'f'))
                b += ((c1 - 'a' + 10) * 16);
            else if ((c1 >= 'A') && (c1 <= 'F'))
                b += ((c1 - 'A' + 10) * 16);
            else
                throw new IllegalArgumentException("hexUtil.bad");

            if ((c2 >= '0') && (c2 <= '9'))
                b += (c2 - '0');
            else if ((c2 >= 'a') && (c2 <= 'f'))
                b += (c2 - 'a' + 10);
            else if ((c2 >= 'A') && (c2 <= 'F'))
                b += (c2 - 'A' + 10);
            else
                throw new IllegalArgumentException("hexUtil.bad");
            baos.write(b);
        }
        return (baos.toByteArray());

    }

    /**
     * Convert a byte array into a printable format containing a String of
     * hexadecimal digit characters (two per byte).
     *
     * @param bytes Byte array representation
     * @see this method consume too much memory, especailly when large byte
     * array.
     */
    public static String convertByteArrayToHexStr(byte bytes[]) {

        StringBuffer sb = new StringBuffer(bytes.length * 2);
        for (int i = 0; i < bytes.length; i++) {
            sb.append(convertDigitToHexChar((int) (bytes[i] >> 4)));
            sb.append(convertDigitToHexChar((int) (bytes[i] & 0x0f)));
        }
        return (sb.toString());

    }

    /**
     * convert byte array into hex string, with , as seperator
     *
     * @param bytes
     * @return
     * @see this method consume too much memory, especailly when large byte
     * array.
     */
    public static String convertByteArrayToHexStr2(byte bytes[]) {

        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < bytes.length; i++) {
            sb.append(convertDigitToHexChar((int) (bytes[i] >> 4)));
            sb.append(convertDigitToHexChar((int) (bytes[i] & 0x0f)));
            sb.append(",");
        }
        return (sb.toString().substring(0, sb.length() - 1));

    }

    /**
     * Convert the specified value (0 .. 15, i.e. 4bits) to the corresponding
     * hexadecimal digit.
     *
     * @param value Value to be converted
     */
    private static char convertDigitToHexChar(int value) {
        value = value & 0x0f;
        if (value >= 10)
            return ((char) (value - 10 + 'a'));
        else
            return ((char) (value + '0'));

    }

    public static String convertByteToHexStr(byte value) {
        StringBuffer sb = new StringBuffer();
        sb.append(convertDigitToHexChar((int) (value >> 4)));
        sb.append(convertDigitToHexChar((int) (value & 0x0f)));

        return (sb.toString());

    }

    public static byte[] convertToBytes(boolean[] bdata) {
        int byteCount = (bdata.length + 7) / 8;
        byte[] data = new byte[byteCount];
        for (int i = 0; i < bdata.length; i++)
            data[i / 8] |= (bdata[i] ? 1 : 0) << (i % 8);
        return data;
    }

    public static byte[] convertToBytes(short[] sdata) {
        int byteCount = sdata.length * 2;
        byte[] data = new byte[byteCount];
        for (int i = 0; i < sdata.length; i++) {
            data[i * 2] = (byte) (0xff & (sdata[i] >> 8));
            data[i * 2 + 1] = (byte) (0xff & sdata[i]);
        }
        return data;
    }

    public static boolean[] convertToBooleans(byte[] data) {
        boolean[] bdata = new boolean[data.length * 8];
        for (int i = 0; i < bdata.length; i++)
            bdata[i] = ((data[i / 8] >> (i % 8)) & 0x1) == 1;
        return bdata;
    }

    public static short[] convertToShorts(byte[] data) {
        short[] sdata = new short[data.length / 2];
        for (int i = 0; i < sdata.length; i++)
            sdata[i] = toShort(data[i * 2], data[i * 2 + 1]);
        return sdata;
    }

    public static short toShort(byte b1, byte b2) {
        return (short) ((b1 << 8) | (b2 & 0xff));
    }

    public static int toInt(byte b, byte b0, byte b1, byte b2) {
        return (int) ((b << 24) | (b0 << 16) | (b1 << 8) | (b2 & 0xff));
    }

    /**
     * 字节转换为浮点
     *
     * @param b     字节（至少4个字节）
     * @param index 开始位置
     * @return
     */
    public static float byte2float(byte[] b, int index) {
        int l;
        l = b[index + 0];
        l &= 0xff;
        l |= ((long) b[index + 1] << 8);
        l &= 0xffff;
        l |= ((long) b[index + 2] << 16);
        l &= 0xffffff;
        l |= ((long) b[index + 3] << 24);

        return Float.intBitsToFloat(l);
    }

    /**
     * 字节转换为浮点 (modbus)
     * <p>
     * 正常的 1 2 3 4 ， 顺序变为 3 4 1 2
     *
     * @param b     字节（至少4个字节）
     * @param index 开始位置
     * @return
     */
    public static float byte2floatModbus(byte[] b, int offset) {
        return Float.intBitsToFloat(b[offset + 1] & 0xff
                | (b[offset + 0] & 0xff) << 8 | (b[offset + 3] & 0xff) << 16
                | (b[offset + 2] & 0xff) << 24);
    }


    /**
     * 字节转换为浮点 (modbus)
     * <p>
     * 正常的 1 2 3 4 ， 顺序变为 3 4 1 2
     *
     * @param b     字节（至少4个字节）
     * @param index 开始位置
     * @return
     */
    public static int byte2intModbus(byte[] b, int offset) {
        return b[offset + 1] & 0xff
                | (b[offset + 0] & 0xff) << 8 | (b[offset + 3] & 0xff) << 16
                | (b[offset + 2] & 0xff) << 24;
    }


    /**
     * 字节转换为浮点 (modbus)
     * <p>
     * 正常的 1 2 3 4 ，
     *
     * @param b     字节（至少4个字节）
     * @param index 开始位置
     * @return
     */
    public static float byte2floatModbus2(byte[] b, int offset) {
        return Float.intBitsToFloat(ByteUtil.bytesToInt(b, offset));
    }

    /**
     * 字节转换为浮点
     *
     * @param 2     字节
     * @param index 开始位置
     * @return
     */
    public static float byte2float2(byte[] b, int index) {
        int l;
        l = b[index + 0];
        l &= 0xff;
        l |= ((long) b[index + 1] << 8);
        return Float.intBitsToFloat(l);
    }

    public static byte toByte(short value, boolean first) {
        if (first)
            return (byte) (0xff & (value >> 8));
        return (byte) (0xff & value);
    }

    /**
     * 低位在前，高位在后
     *
     * @param value
     * @param len
     * @return
     */
    public static byte[] intToByteArray(int value, int len) {
        byte[] b = new byte[len];
        for (int i = 0; i < len; i++) {
            int offset = (len - 1 - i) * 8;
            b[i] = (byte) ((value >> offset) & 0xFF);
        }
        return b;
    }

    /**
     * 大端模式，高字节放到高地址上
     *
     * @param value
     * @return
     */
    public static byte[] intToByteArrayBig(int value) {
        return intToByteArray(value, 4);
    }

    /**
     * 小端模式，高字节放到低地址上
     *
     * @param value
     * @return
     */
    public static byte[] intToByteArrayLittle(int value) {
        byte[] temp = intToByteArray(value, 4);
        byte[] reByte = new byte[4];

        for (int i = 0; i < reByte.length; i++) {
            reByte[i] = temp[temp.length - 1 - i];
        }

        return reByte;
    }

    /**
     * change byte array into a unsigned byte array
     *
     * @param source
     * @return
     */
    public static byte[] toUnsignedByteArray(byte[] source) {
        // ByteUtil.printByteArray(source);
        // to keep value in the 0-255
        int model = 256;
        if (source == null || source.length == 0) {
            return new byte[0];
        }
        byte[] dest = new byte[source.length];
        for (int i = 0; i < source.length; i++) {
            int tmp = ((source[i] + model) % model) & 0xff;
            dest[i] = (byte) tmp;
        }
        return dest;
    }

    /**
     * Convert the byte array to an int starting from the given offset. 大端模式
     *
     * @param b      The byte array
     * @param offset
     * @return The integer
     */

    public static int byteArrayToIntBig(byte[] b) {
        if (b.length > 4) {
            throw new RuntimeException("more than 4 byte");
        }
        int value = 0;
        for (int i = 0; i < b.length; i++) {
            int shift = (b.length - 1 - i) * 8;
            value += (b[i] & 0xFF) << shift;
        }
        return value;
    }

    /**
     * Convert the byte array to an int starting from the given offset.
     * 小端模式，将小端模式的数据转成正常的INT
     *
     * @param b      The byte array
     * @param offset
     * @return The integer
     */

    public static int byteArrayToIntLittle(byte[] b) {
        if (b.length > 4) {
            throw new RuntimeException("more than 4 byte");
        }
        int value = 0;
        // /小端
        b = ByteUtil.intHToL(b);
        // /END
        for (int i = 0; i < b.length; i++) {
            int shift = (b.length - 1 - i) * 8;
            value += (b[i] & 0xFF) << shift;
        }
        return value;
    }

    /**
     * 将指定byte数组以16进制的字符串返回
     *
     * @param b byte[]
     * @return void
     */
    public static String hex2String(byte[] b) {
        String hint = "0x";
        for (int i = 0; i < b.length; i++) {
            String str = Integer.toHexString(b[i] & 0xFF);
            if (str.length() == 1) {
                str = '0' + str;
            }
            hint = hint + " " + str;
        }
        return hint;
    }

    /**
     * 往数组添加int值
     *
     * @param msgBytes
     * @param offset
     * @param value
     * @param len
     */
    public static void appendInt(byte[] msgBytes, Integer offset, int value,
                                 int len) {
        byte[] vBytes = ByteUtil.intToByteArray(value, len);
        for (int i = 0; i < len; i++) {
            msgBytes[i + offset] = vBytes[i];
        }
    }

    /**
     * 往数组添加string值
     *
     * @param msgBytes
     * @param offset
     * @param str
     * @param len
     */
    public static void appendString(byte[] msgBytes, Integer offset,
                                    String str, int len) {
        byte[] sBytes = str.getBytes();
        if (len > sBytes.length)
            len = sBytes.length;
        for (int i = 0; i < len; i++) {
            msgBytes[i + offset] = sBytes[i];
        }
    }

    /**
     * 往数组添加byte值
     *
     * @param msgBytes
     * @param offset
     * @param b
     */
    public static void appendByte(byte[] msgBytes, Integer offset, byte[] b) {
        for (int i = 0; i < b.length; i++) {
            msgBytes[i + offset] = b[i];
        }
    }

    /**
     * 描述:将16进制字符串转ASCII码
     *
     * @param
     * @param hex
     * @return
     * @throws Exception
     * @author Administrator
     */
    public static String convertHexToString(String hex) {
        StringBuilder sb = new StringBuilder();
        StringBuilder temp = new StringBuilder();
        for (int i = 0; i < hex.length() - 1; i += 2) {
            String output = hex.substring(i, (i + 2));
            int decimal = Integer.parseInt(output, 16);
            sb.append((char) decimal);
            temp.append(decimal);
        }
        return sb.toString();
    }

    /**
     * 描述:将ASCII码转16进制字符串
     *
     * @param
     * @param str
     * @return
     * @throws Exception
     * @author Administrator
     */
    public String convertStringToHex(String str) {
        char[] chars = str.toCharArray();
        StringBuffer hex = new StringBuffer();
        for (int i = 0; i < chars.length; i++) {
            hex.append(Integer.toHexString((int) chars[i]));
        }
        return hex.toString();
    }

    /**
     * 把byte转为字符串的bit
     */
    public static String byteToBit(byte b) {
        return "" + (byte) ((b >> 7) & 0x1) + (byte) ((b >> 6) & 0x1)
                + (byte) ((b >> 5) & 0x1) + (byte) ((b >> 4) & 0x1)
                + (byte) ((b >> 3) & 0x1) + (byte) ((b >> 2) & 0x1)
                + (byte) ((b >> 1) & 0x1) + (byte) ((b >> 0) & 0x1);
    }

    /**
     * 把byte转为字符串的bit
     * <p>
     * 小端 在前
     */
    public static String byteToBit2(byte b) {
        return "" + (byte) ((b >> 0) & 0x1) + (byte) ((b >> 1) & 0x1)
                + (byte) ((b >> 2) & 0x1) + (byte) ((b >> 3) & 0x1)
                + (byte) ((b >> 4) & 0x1) + (byte) ((b >> 5) & 0x1)
                + (byte) ((b >> 6) & 0x1) + (byte) ((b >> 7) & 0x1);
    }

    static byte[] crc16_tab_h = {(byte) 0x00, (byte) 0xC1, (byte) 0x81,
            (byte) 0x40, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41,
            (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x00,
            (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x01, (byte) 0xC0,
            (byte) 0x80, (byte) 0x41, (byte) 0x00, (byte) 0xC1, (byte) 0x81,
            (byte) 0x40, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40,
            (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x01,
            (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x00, (byte) 0xC1,
            (byte) 0x81, (byte) 0x40, (byte) 0x00, (byte) 0xC1, (byte) 0x81,
            (byte) 0x40, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41,
            (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x01,
            (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x01, (byte) 0xC0,
            (byte) 0x80, (byte) 0x41, (byte) 0x00, (byte) 0xC1, (byte) 0x81,
            (byte) 0x40, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41,
            (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x00,
            (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x01, (byte) 0xC0,
            (byte) 0x80, (byte) 0x41, (byte) 0x00, (byte) 0xC1, (byte) 0x81,
            (byte) 0x40, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41,
            (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x00,
            (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x00, (byte) 0xC1,
            (byte) 0x81, (byte) 0x40, (byte) 0x01, (byte) 0xC0, (byte) 0x80,
            (byte) 0x41, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41,
            (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x01,
            (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x00, (byte) 0xC1,
            (byte) 0x81, (byte) 0x40, (byte) 0x00, (byte) 0xC1, (byte) 0x81,
            (byte) 0x40, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41,
            (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x00,
            (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x00, (byte) 0xC1,
            (byte) 0x81, (byte) 0x40, (byte) 0x01, (byte) 0xC0, (byte) 0x80,
            (byte) 0x41, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40,
            (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x01,
            (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x00, (byte) 0xC1,
            (byte) 0x81, (byte) 0x40, (byte) 0x00, (byte) 0xC1, (byte) 0x81,
            (byte) 0x40, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41,
            (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x00,
            (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x01, (byte) 0xC0,
            (byte) 0x80, (byte) 0x41, (byte) 0x00, (byte) 0xC1, (byte) 0x81,
            (byte) 0x40, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40,
            (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x00,
            (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x01, (byte) 0xC0,
            (byte) 0x80, (byte) 0x41, (byte) 0x01, (byte) 0xC0, (byte) 0x80,
            (byte) 0x41, (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40,
            (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x00,
            (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x00, (byte) 0xC1,
            (byte) 0x81, (byte) 0x40, (byte) 0x01, (byte) 0xC0, (byte) 0x80,
            (byte) 0x41, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41,
            (byte) 0x00, (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x00,
            (byte) 0xC1, (byte) 0x81, (byte) 0x40, (byte) 0x01, (byte) 0xC0,
            (byte) 0x80, (byte) 0x41, (byte) 0x00, (byte) 0xC1, (byte) 0x81,
            (byte) 0x40, (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41,
            (byte) 0x01, (byte) 0xC0, (byte) 0x80, (byte) 0x41, (byte) 0x00,
            (byte) 0xC1, (byte) 0x81, (byte) 0x40};

    static byte[] crc16_tab_l = {(byte) 0x00, (byte) 0xC0, (byte) 0xC1,
            (byte) 0x01, (byte) 0xC3, (byte) 0x03, (byte) 0x02, (byte) 0xC2,
            (byte) 0xC6, (byte) 0x06, (byte) 0x07, (byte) 0xC7, (byte) 0x05,
            (byte) 0xC5, (byte) 0xC4, (byte) 0x04, (byte) 0xCC, (byte) 0x0C,
            (byte) 0x0D, (byte) 0xCD, (byte) 0x0F, (byte) 0xCF, (byte) 0xCE,
            (byte) 0x0E, (byte) 0x0A, (byte) 0xCA, (byte) 0xCB, (byte) 0x0B,
            (byte) 0xC9, (byte) 0x09, (byte) 0x08, (byte) 0xC8, (byte) 0xD8,
            (byte) 0x18, (byte) 0x19, (byte) 0xD9, (byte) 0x1B, (byte) 0xDB,
            (byte) 0xDA, (byte) 0x1A, (byte) 0x1E, (byte) 0xDE, (byte) 0xDF,
            (byte) 0x1F, (byte) 0xDD, (byte) 0x1D, (byte) 0x1C, (byte) 0xDC,
            (byte) 0x14, (byte) 0xD4, (byte) 0xD5, (byte) 0x15, (byte) 0xD7,
            (byte) 0x17, (byte) 0x16, (byte) 0xD6, (byte) 0xD2, (byte) 0x12,
            (byte) 0x13, (byte) 0xD3, (byte) 0x11, (byte) 0xD1, (byte) 0xD0,
            (byte) 0x10, (byte) 0xF0, (byte) 0x30, (byte) 0x31, (byte) 0xF1,
            (byte) 0x33, (byte) 0xF3, (byte) 0xF2, (byte) 0x32, (byte) 0x36,
            (byte) 0xF6, (byte) 0xF7, (byte) 0x37, (byte) 0xF5, (byte) 0x35,
            (byte) 0x34, (byte) 0xF4, (byte) 0x3C, (byte) 0xFC, (byte) 0xFD,
            (byte) 0x3D, (byte) 0xFF, (byte) 0x3F, (byte) 0x3E, (byte) 0xFE,
            (byte) 0xFA, (byte) 0x3A, (byte) 0x3B, (byte) 0xFB, (byte) 0x39,
            (byte) 0xF9, (byte) 0xF8, (byte) 0x38, (byte) 0x28, (byte) 0xE8,
            (byte) 0xE9, (byte) 0x29, (byte) 0xEB, (byte) 0x2B, (byte) 0x2A,
            (byte) 0xEA, (byte) 0xEE, (byte) 0x2E, (byte) 0x2F, (byte) 0xEF,
            (byte) 0x2D, (byte) 0xED, (byte) 0xEC, (byte) 0x2C, (byte) 0xE4,
            (byte) 0x24, (byte) 0x25, (byte) 0xE5, (byte) 0x27, (byte) 0xE7,
            (byte) 0xE6, (byte) 0x26, (byte) 0x22, (byte) 0xE2, (byte) 0xE3,
            (byte) 0x23, (byte) 0xE1, (byte) 0x21, (byte) 0x20, (byte) 0xE0,
            (byte) 0xA0, (byte) 0x60, (byte) 0x61, (byte) 0xA1, (byte) 0x63,
            (byte) 0xA3, (byte) 0xA2, (byte) 0x62, (byte) 0x66, (byte) 0xA6,
            (byte) 0xA7, (byte) 0x67, (byte) 0xA5, (byte) 0x65, (byte) 0x64,
            (byte) 0xA4, (byte) 0x6C, (byte) 0xAC, (byte) 0xAD, (byte) 0x6D,
            (byte) 0xAF, (byte) 0x6F, (byte) 0x6E, (byte) 0xAE, (byte) 0xAA,
            (byte) 0x6A, (byte) 0x6B, (byte) 0xAB, (byte) 0x69, (byte) 0xA9,
            (byte) 0xA8, (byte) 0x68, (byte) 0x78, (byte) 0xB8, (byte) 0xB9,
            (byte) 0x79, (byte) 0xBB, (byte) 0x7B, (byte) 0x7A, (byte) 0xBA,
            (byte) 0xBE, (byte) 0x7E, (byte) 0x7F, (byte) 0xBF, (byte) 0x7D,
            (byte) 0xBD, (byte) 0xBC, (byte) 0x7C, (byte) 0xB4, (byte) 0x74,
            (byte) 0x75, (byte) 0xB5, (byte) 0x77, (byte) 0xB7, (byte) 0xB6,
            (byte) 0x76, (byte) 0x72, (byte) 0xB2, (byte) 0xB3, (byte) 0x73,
            (byte) 0xB1, (byte) 0x71, (byte) 0x70, (byte) 0xB0, (byte) 0x50,
            (byte) 0x90, (byte) 0x91, (byte) 0x51, (byte) 0x93, (byte) 0x53,
            (byte) 0x52, (byte) 0x92, (byte) 0x96, (byte) 0x56, (byte) 0x57,
            (byte) 0x97, (byte) 0x55, (byte) 0x95, (byte) 0x94, (byte) 0x54,
            (byte) 0x9C, (byte) 0x5C, (byte) 0x5D, (byte) 0x9D, (byte) 0x5F,
            (byte) 0x9F, (byte) 0x9E, (byte) 0x5E, (byte) 0x5A, (byte) 0x9A,
            (byte) 0x9B, (byte) 0x5B, (byte) 0x99, (byte) 0x59, (byte) 0x58,
            (byte) 0x98, (byte) 0x88, (byte) 0x48, (byte) 0x49, (byte) 0x89,
            (byte) 0x4B, (byte) 0x8B, (byte) 0x8A, (byte) 0x4A, (byte) 0x4E,
            (byte) 0x8E, (byte) 0x8F, (byte) 0x4F, (byte) 0x8D, (byte) 0x4D,
            (byte) 0x4C, (byte) 0x8C, (byte) 0x44, (byte) 0x84, (byte) 0x85,
            (byte) 0x45, (byte) 0x87, (byte) 0x47, (byte) 0x46, (byte) 0x86,
            (byte) 0x82, (byte) 0x42, (byte) 0x43, (byte) 0x83, (byte) 0x41,
            (byte) 0x81, (byte) 0x80, (byte) 0x40};

    /**
     * 计算CRC16校验
     *
     * @param data 需要计算的数组
     * @return CRC16校验值
     */
    public static int calcCrc16(byte[] data) {
        return calcCrc16(data, 0, data.length);
    }

    /**
     * 计算CRC16校验
     *
     * @param data   需要计算的数组
     * @param offset 起始位置
     * @param len    长度
     * @return CRC16校验值
     */
    public static int calcCrc16(byte[] data, int offset, int len) {
        return calcCrc16(data, offset, len, 0xffff);
    }

    /**
     * 计算CRC16校验
     *
     * @param data   需要计算的数组
     * @param offset 起始位置
     * @param len    长度
     * @param preval 之前的校验值
     * @return CRC16校验值
     */
    public static int calcCrc16(byte[] data, int offset, int len, int preval) {
        int ucCRCHi = (preval & 0xff00) >> 8;
        int ucCRCLo = preval & 0x00ff;
        int iIndex;
        for (int i = 0; i < len; ++i) {
            iIndex = (ucCRCLo ^ data[offset + i]) & 0x00ff;
            ucCRCLo = ucCRCHi ^ crc16_tab_h[iIndex];
            ucCRCHi = crc16_tab_l[iIndex];
        }
        return ((ucCRCHi & 0x00ff) << 8) | (ucCRCLo & 0x00ff) & 0xffff;
    }

    /**
     * 从一个byte[]数组中截取一部分
     *
     * @param src
     * @param begin
     * @param count
     * @return
     */
    public static byte[] subBytes(byte[] src, int begin, int count) {
        byte[] bs = new byte[count];
        for (int i = begin; i < begin + count; i++) bs[i - begin] = src[i];
        return bs;
    }

    // 测试
    public static void main(String[] args) {
        // 0x02 05 00 03 FF 00 , crc16=7C 09
        int crc = ByteUtil.calcCrc16(new byte[]{0x02, 0x05, 0x00, 0x03,
                (byte) 0xff, 0x00});
        System.out.println(String.format("0x%04x", crc));
    }

    /**
     * 字节数组转换为16进制字符串
     * @param src
     * @return
     */
    public static String bytes2Str(byte[] src){
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            if(i>0){
                stringBuilder.append(" ");
            }
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

}
