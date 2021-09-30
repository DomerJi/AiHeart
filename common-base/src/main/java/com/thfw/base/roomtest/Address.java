package com.thfw.base.roomtest;

import androidx.room.ColumnInfo;

class Address {
    public String street;
    public String state;
    public String city;

    @ColumnInfo(name = "post_code")
    public int postCode;
}