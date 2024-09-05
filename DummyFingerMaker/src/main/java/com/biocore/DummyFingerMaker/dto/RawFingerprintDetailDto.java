package com.biocore.DummyFingerMaker.dto;
import lombok.Data;

@Data
public class RawFingerprintDetailDto {

    private String data;
    private int height;
    private int width;
    private int quality;

    public String getData() {
        return data != null ? data : "";
    }

    public void setData(String data) {
        this.data = data;
    }

}
