package com.thfw.robotheart.util;

import com.thfw.base.utils.LogUtil;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

final public class BleUtil {
    private final static String TAG = BleUtil.class.getSimpleName();

    public static BleAdvertisedData parseAdertisedData(byte[] advertisedData) {

        LogUtil.d(TAG, "parseAdertisedData advertisedData = " + advertisedData);
        List<UUID> uuids = new ArrayList<UUID>();
        String name = null;
        if (advertisedData == null) {
            return new BleAdvertisedData(uuids, name);
        }

        name = parseDeviceName(advertisedData);
        LogUtil.d(TAG, "parseAdertisedData advertisedData  name = " + name);
        return new BleAdvertisedData(uuids, name);
    }


    public static String parseDeviceName(byte[] scanRecord) {
        String ret = null;
        if (null == scanRecord) {
            return ret;
        }

        ByteBuffer buffer = ByteBuffer.wrap(scanRecord).order(ByteOrder.LITTLE_ENDIAN);
        while (buffer.remaining() > 2) {
            byte length = buffer.get();
            if (length == 0)
                break;

            byte type = buffer.get();
            length -= 1;
            switch (type) {
                case 0x01: // Flags
                    buffer.get(); // flags
                    length--;
                    break;
                case 0x02: // Partial list of 16-bit UUIDs
                case 0x03: // Complete list of 16-bit UUIDs
                case 0x14: // List of 16-bit Service Solicitation UUIDs
                    while (length >= 2) {
                        buffer.getShort();
                        length -= 2;
                    }
                    break;
                case 0x04: // Partial list of 32 bit service UUIDs
                case 0x05: // Complete list of 32 bit service UUIDs
                    while (length >= 4) {
                        buffer.getInt();
                        length -= 4;
                    }
                    break;
                case 0x06: // Partial list of 128-bit UUIDs
                case 0x07: // Complete list of 128-bit UUIDs
                case 0x15: // List of 128-bit Service Solicitation UUIDs
                    while (length >= 16) {
                        long lsb = buffer.getLong();
                        long msb = buffer.getLong();
                        length -= 16;
                    }
                    break;
                case 0x08: // Short local device name
                case 0x09: // Complete local device name
                    byte sb[] = new byte[length];
                    buffer.get(sb, 0, length);
                    length = 0;
                    ret = new String(sb).trim();
                    return ret;
                case (byte) 0xFF: // Manufacturer Specific Data
                    buffer.getShort();
                    length -= 2;
                    break;
                default: // skip
                    break;
            }
            if (length > 0) {
                buffer.position(buffer.position() + length);
            }
        }
        return ret;
    }


    public static class BleAdvertisedData {
        private List<UUID> mUuids;
        private String mName;

        public BleAdvertisedData(List<UUID> uuids, String name) {
            mUuids = uuids;
            mName = name;
        }

        public List<UUID> getUuids() {
            return mUuids;
        }

        public String getName() {
            return mName;
        }
    }
}
