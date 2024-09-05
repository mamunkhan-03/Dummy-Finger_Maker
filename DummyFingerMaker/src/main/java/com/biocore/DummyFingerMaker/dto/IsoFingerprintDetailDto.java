package com.biocore.DummyFingerMaker.dto;

import lombok.Data;

@Data
public class IsoFingerprintDetailDto {
    private String data;
    public String getData() {
        return data != null ? data : "";
    }


    public void setData(String data) {
        this.data = data;
    }
}
