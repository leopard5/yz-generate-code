package com.yz.code.enums;

public enum UItype {
    EASYUI       ((byte) 1),
    BOOTSTRAP    ((byte) 2),
    VUE          ((byte) 3),
    REACT        ((byte) 4),
    ANGULARJS    ((byte) 5),

    ;
    private byte value;

    UItype(byte value) {
        this.value = value;
    }

    public byte getValue() {
        return value;
    }

    public static boolean exists(Byte status) {
        if (status == null) {
            return false;
        }
        byte s = status.byteValue();
        return exists(s);
    }

    public static boolean exists(byte s) {
        for (UItype element : UItype.values()) {
            if (element.value == s) {
                return true;
            }
        }
        return false;
    }

    public boolean equal(Byte val) {
        return val == null ? false : val.byteValue() == this.value;
    }

}
