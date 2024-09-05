package com.biocore.DummyFingerMaker.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.Map;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FingerprintDto {

    private String trackingNumber;

    private Map<String, RawFingerprintDetailDto> fingerprintRawData;

    private Map<String, IsoFingerprintDetailDto> fngerprintISOData;
}
