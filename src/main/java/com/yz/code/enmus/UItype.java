package com.yz.code.enmus;

import sun.awt.SunHints;

public enum UItype {
    EASYUI        ((byte) 1),
    BOOTSTRAP      ((byte) 2),
    VUE             ((byte) 3),

    ;

    private byte value;

    UItype(byte value){
        this.value = value;
    }


}
