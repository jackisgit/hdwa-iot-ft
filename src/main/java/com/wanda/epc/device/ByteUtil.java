package com.wanda.epc.device;


import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.nio.ByteBuffer;

public class ByteUtil {

//	private static ByteBuffer buffer = ByteBuffer.allocate(8);

    /**
     * @param x
     * @return
     */
    public static byte intToByte(int x) {
        return (byte) x;
    }

    public static float byte2float(byte[] b) {
        int l;
        l = b[1];
        l &= 0xff;
        l |= ((long) b[0] << 8);
        l &= 0xffff;
        l |= ((long) b[3] << 16);
        l &= 0xffffff;
        l |= ((long) b[2] << 24);
        return Float.intBitsToFloat(l);
    }

    public static byte[] float2byte(float f) {

        // 把float转换为byte[]
        int fbit = Float.floatToIntBits(f);

        byte[] b = new byte[4];
        for (int i = 0; i < 4; i++) {
            b[i] = (byte) (fbit >> (24 - i * 8));
        }

        // 翻转数组
        int len = b.length;
        // 建立一个与源数组元素类型相同的数组
        byte[] dest = new byte[len];
        // 为了防止修改源数组，将源数组拷贝一份副本
        System.arraycopy(b, 0, dest, 0, len);
        byte temp;
        // 将顺位第i个与倒数第i个交换
        for (int i = 0; i < len / 2; ++i) {
            temp = dest[i];
            dest[i] = dest[len - i - 1];
            dest[len - i - 1] = temp;
        }

        return dest;

    }

    /**
     * 字节转换为浮点
     *
     * @param b     字节（至少4个字节）
     * @param index 开始位置
     * @return
     */
    public static float bytesToFloat(byte[] b, int index) {
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

    public static float byte2floatSwapped(byte[] b) {
        int l;
        l = b[3];
        l &= 0xff;
        l |= ((long) b[2] << 8);
        l &= 0xffff;
        l |= ((long) b[1] << 16);
        l &= 0xffffff;
        l |= ((long) b[0] << 24);
        return Float.intBitsToFloat(l);
    }

    /**
     * 十六进制字符转byte数组
     *
     * @param str
     * @return
     */
    public static byte[] hexStringToBytes(String str, byte[] dest, int offset) {
        if (str == null || str.trim().equals("")) {
            return dest;
        }
        if (dest == null) {
            dest = new byte[str.length() / 2];
            offset = 0;
        }
        for (int i = 0; i < str.length() / 2 && i < dest.length; i++) {
            String subStr = str.substring(i * 2, i * 2 + 2);
            dest[offset + i] = (byte) Integer.parseInt(subStr, 16);
        }

        return dest;
    }

    public static byte[] hexStringToBytes(String str) {
        if (str.length() % 2 == 1) {
            str = "0" + str;
        }
        byte[] dest = new byte[str.length() / 2];
        dest = hexStringToBytes(str, dest, 0);
        return dest;
    }

    /**
     * byte杞琲nt
     *
     * @param b
     * @return
     */
    public static int byteToInt(byte b) {
        // Java鐨刡yte鏄湁绗﹀彿锛岄繃 &0xFF杞负鏃犵鍙�
        return b & 0xFF;
    }

    /**
     * byte[]杞琲nt
     *
     * @param b
     * @return
     */
    public static int byteArrayToInt(byte[] b) {
        if (b.length >= 4) {
            return b[3] & 0xFF | (b[2] & 0xFF) << 8 | (b[1] & 0xFF) << 16 | (b[0] & 0xFF) << 24;
        } else if (b.length == 1) {
            return byteToInt(b[0]);
        } else {
            byte[] data = new byte[4];
            int index = 4;
            for (byte bb : b) {
                data[--index] = bb;
            }
            return data[3] & 0xFF | (data[2] & 0xFF) << 8 | (data[1] & 0xFF) << 16 | (data[0] & 0xFF) << 24;
        }
    }

    public static int byteArrayToInt(byte[] b, int index) {
        return b[index + 3] & 0xFF | (b[index + 2] & 0xFF) << 8 | (b[index + 1] & 0xFF) << 16
                | (b[index + 0] & 0xFF) << 24;
    }

    /**
     * int杞琤yte[]
     *
     * @param a
     * @return
     */
    public static byte[] intToByteArray(int a) {
        return intToByteArray(a, 4);
    }

    public static byte[] intToByteArray(int a, int len) {
        byte[] ret = new byte[]{(byte) ((a >> 24) & 0xFF), (byte) ((a >> 16) & 0xFF), (byte) ((a >> 8) & 0xFF),
                (byte) (a & 0xFF)};
        byte[] dest = new byte[len];
        System.arraycopy(ret, 4 - len, dest, 0, len);
        return dest;
    }

    /**
     * short杞琤yte[]
     *
     * @param b
     * @param s
     * @param index
     */
    public static void byteArrToShort(byte b[], short s, int index) {
        b[index + 1] = (byte) (s >> 8);
        b[index + 0] = (byte) (s >> 0);
    }

    /**
     * byte[]杞瑂hort
     *
     * @param b
     * @param index
     * @return
     */
    public static short byteArrToShort(byte[] b, int index) {
        return (short) (((b[index + 0] << 8) | b[index + 1] & 0xff));
    }

    /**
     * byte转short
     *
     * @param hBit 高子节
     * @param lBit 字节
     * @return
     */
    public static short byteArrToShort(byte hBit, byte lBit) {
        return (short) (((hBit << 8) | lBit & 0xff));
    }

    /**
     * 16浣峴hort杞琤yte[]
     *
     * @param s
     * @param dest
     * @param destOffset
     * @return
     */
    public static byte[] shortToByteArr(int s, byte[] dest, int destOffset, int sort) {
        if (sort == 0) { // 楂樹綅鍦ㄥ墠锛屼綆浣嶅湪鍚�
            for (int i = 0; i < 2; i++) {
                int offset = (1 - i) * 8;
                dest[destOffset + i] = (byte) ((s >>> offset) & 0xff);
            }
        } else {
            for (int i = 0; i < 2; i++) {
                int offset = (1 - i) * 8;
                dest[destOffset + (1 - i)] = (byte) ((s >>> offset) & 0xff);
            }
        }
        return dest;
    }

    public static byte[] shortToByteArr(short s) {
        byte[] targets = new byte[2];
        for (int i = 0; i < 2; i++) {
            int offset = (targets.length - 1 - i) * 8;
            targets[i] = (byte) ((s >>> offset) & 0xff);
        }
        return targets;
    }

    /**
     * byte[]杞6浣峴hort
     *
     * @param b
     * @return
     */
    public static short byteArrToShort(byte[] b) {
        return byteArrToShort(b, 0);
    }

    /**
     * long杞琤yte[]
     *
     * @param x
     * @return
     */
    public static byte[] longToBytes(long x) {
        ByteBuffer buffer = ByteBuffer.allocate(8);
        buffer.putLong(0, x);
        return buffer.array();
    }

    /**
     * byte[]杞琇ong
     *
     * @param bytes
     * @return
     */
    public static long bytesToLong(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.allocate(8);
        buffer.put(bytes, 0, Math.min(8, bytes.length));
        buffer.flip();// need flip
        return buffer.getLong();
    }

    /**
     * 浠巄yte[]涓娊鍙栨柊鐨刡yte[]
     *
     * @param data  - 鍏冩暟鎹�
     * @param start - 寮嬩綅缃�
     * @param end   - 缁撴潫浣嶇疆
     * @return 鏂癰yte[]
     */
    public static byte[] getByteArr(byte[] data, int start, int end) {
        byte[] ret = new byte[end - start];
        for (int i = 0; (start + i) < end; i++) {
            ret[i] = data[start + i];
        }
        return ret;
    }

    /**
     * byte[]杞琲nputstream
     *
     * @param b
     * @return
     */
    public static InputStream readByteArr(byte[] b) {
        return new ByteArrayInputStream(b);
    }

    /**
     * byte鏁扮粍鍐呮暟瀛楁槸鍚︾浉鍚�
     *
     * @param s1
     * @param s2
     * @return
     */
    public static boolean isEq(byte[] s1, byte[] s2) {
        int slen = s1.length;
        if (slen == s2.length) {
            for (int index = 0; index < slen; index++) {
                if (s1[index] != s2[index]) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    /**
     * byte鏁扮粍杞崲涓篠tirng
     *
     * @param s1     -鏁扮粍
     * @param encode -瀛楃闆�
     * @param err    -杞崲閿欒鏃惰繑鍥炶鏂囧瓧
     * @return
     */
    public static String getString(byte[] s1, String encode, String err) {
        return getString(s1, 0, s1.length, encode, err);
    }

    public static String getString(byte[] s1, int offset, int length, String encode, String err) {
        try {
            return new String(s1, offset, length, encode);
        } catch (UnsupportedEncodingException e) {
            return err == null ? null : err;
        }
    }

    public static String getString(byte[] s1, int offset, int length, String encode) {
        return getString(s1, offset, length, encode, null);
    }

    /**
     * byte鏁扮粍杞崲涓篠tirng
     *
     * @param s1-鏁扮粍
     * @param encode-瀛楃闆�
     * @return
     */
    public static String getString(byte[] s1, String encode) {
        return getString(s1, encode, null);
    }

    /**
     * 瀛楄妭鏁扮粍杞6杩涘埗瀛楃涓�
     *
     * @param b
     * @return
     */
    public static String byteArrToHexString(byte[] b) {
        return byteArrToHexString(b, false);
    }

    /**
     * 字节转16进制字符串
     *
     * @param b        源字节数组
     * @param isFormat 转换的字符串是否每两个字节+一个空格
     * @return
     */
    public static String byteArrToHexString(byte[] b, boolean isFormat) {
        String result = "";
        if (b != null) {
            for (int i = 0; i < b.length; i++) {
                result += Integer.toString((b[i] & 0xff) + 0x100, 16).substring(1);
            }
        }
        if (isFormat) {
            String regex = "(.{2})";
            return result.toUpperCase().replaceAll(regex, "$1 ");
        } else {
            return result.toUpperCase();
        }
    }

    public static String byteArrToHexString(byte[] b, int offset, int len) {
        String result = "";
        for (int i = offset; i < offset + len; i++) {
            result += Integer.toString((b[i] & 0xff) + 0x100, 16).substring(1);
        }
        return result.toUpperCase();
    }

    /**
     * 16杩涘埗瀛楃鍒涜浆int
     *
     * @param hexString
     * @return
     */
    public static int hexStringToInt(String hexString) {
        return Integer.parseInt(hexString, 16);
    }

    /**
     * 鍗佽繘鍒惰浆浜岃繘鍒�
     *
     * @param i
     * @return
     */
    public static String intToBinary(int i) {
        return Integer.toBinaryString(i);
    }

    /**
     * 鎶奍P鍦板潃杞寲涓哄瓧鑺傛暟缁�
     *
     * @param ipAddr
     * @return byte[]
     */
    public static byte[] ipToBytesByInet(String ipAddr) {
        try {
            return InetAddress.getByName(ipAddr).getAddress();
        } catch (Exception e) {
            throw new IllegalArgumentException(ipAddr + " is invalid IP");
        }
    }

    public static int bytesToInt(byte b[], int offset) {
        return b[offset + 3] & 0xff | (b[offset + 2] & 0xff) << 8 | (b[offset + 1] & 0xff) << 16
                | (b[offset] & 0xff) << 24;
    }

    public static int bytesToUbyte(byte[] array, int offset) {
        return array[offset] & 0xff;
    }

    /**
     * 鎶奿nt->ip鍦板潃
     *
     * @param ipInt
     * @return String
     */
    public static String intToIp(int ipInt) {
        return new StringBuilder().append(((ipInt >> 24) & 0xff)).append('.').append((ipInt >> 16) & 0xff).append('.')
                .append((ipInt >> 8) & 0xff).append('.').append((ipInt & 0xff)).toString();
    }

    public static void shortToBytes(short n, byte[] array, int offset) {
        array[offset + 1] = (byte) (n & 0xff);
        array[offset] = (byte) ((n >> 8) & 0xff);
    }

    /**
     * 十进制数转为2进制数
     *
     * @param num  十进制数
     * @param size 返回的位数 （可以根据自己需求设置）
     * @return
     */
    public static String decimalToBinary(int num, int size) {
        if (size < (Integer.SIZE - Integer.numberOfLeadingZeros(num))) {
            throw new RuntimeException("传入size小于num二进制位数");
        }
        StringBuilder binStr = new StringBuilder();
        for (int i = size - 1; i >= 0; i--) {
            binStr.append(num >>> i & 1);
        }
        return binStr.toString();
    }

}
