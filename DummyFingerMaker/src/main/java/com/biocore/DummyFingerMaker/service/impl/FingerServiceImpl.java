package com.biocore.DummyFingerMaker.service.impl;

import com.biocore.DummyFingerMaker.dto.FingerprintDto;
import com.biocore.DummyFingerMaker.dto.IsoFingerprintDetailDto;
import com.biocore.DummyFingerMaker.dto.RawFingerprintDetailDto;
import com.biocore.DummyFingerMaker.service.FingerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@Service
public class FingerServiceImpl implements FingerService {
    private RestTemplate restTemplate;
    private JdbcTemplate jdbcTemplate;
    private ObjectMapper objectMapper;
    private static final String API_URL = "http://192.168.157.201:8091/common/fingerprint";



    @Autowired
    public void FingerServiceImpl(RestTemplate restTemplate, JdbcTemplate jdbcTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.jdbcTemplate = jdbcTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public void populateDummyFingerData() {
        int count=0;
        // Query to get all customer numbers
        String customerQuery = """
              SELECT a.CUST_NO
                FROM GUMS.MB_USER_MST a, EMOB.MB_CUSTOMER_MST b, EMOB.ST_AGENT_POINT c
               WHERE     a.CUST_NO = b.CUST_NO
                     AND b.AGENT_POINT_ID = c.POINT_ID
                     AND b.AGENT_POINT_ID IN (50,
                                              500,
                                              889,
                                              2681,
                                              69)
                """;

        List<Long> customers = jdbcTemplate.queryForList(customerQuery, Long.class);

        if (customers.isEmpty()) {
            throw new IllegalStateException("No customers found");
        }
        System.out.println(customers);

        // Query to get all sample data
        String sampleQuery = "SELECT iso_data, raw_data, height, width FROM biocore.sample_fingerprints";
        List<SampleData> sampleDataList = jdbcTemplate.query(sampleQuery, (rs, rowNum) -> {
            SampleData sampleData = new SampleData();
            sampleData.setIsoData(rs.getBytes("iso_data"));
            sampleData.setRawData(rs.getBytes("raw_data"));
            sampleData.setHeight(rs.getInt("height"));
            sampleData.setWidth(rs.getInt("width"));
            return sampleData;
        });

        System.out.println("Sample Data List:");
        for (SampleData sampleData : sampleDataList) {
            System.out.println(sampleData);
        }

        if (sampleDataList.isEmpty()) {
            throw new IllegalStateException("No sample data found");
        }

        int batchSize = Math.min(sampleDataList.size(), customers.size());
        int numBatches = (int) Math.ceil((double) customers.size() / batchSize);

        // Process customers in batches
        for (int i = 0; i < numBatches; i++) {
            int startIndex = i * batchSize;
            int endIndex = Math.min(startIndex + batchSize, customers.size());
            List<Long> batchCustomers = customers.subList(startIndex, endIndex);

            for (int j = 0; j < batchCustomers.size(); j++) {
                Long custNo = batchCustomers.get(j);

                // Select the sample data by wrapping around if needed
                SampleData sampleData = sampleDataList.get(j % sampleDataList.size());

                RawFingerprintDetailDto rawDetail = new RawFingerprintDetailDto();
                rawDetail.setData(sampleData.getRawData() != null ? Base64.getEncoder().encodeToString(sampleData.getRawData()) : "");
                rawDetail.setHeight(sampleData.getHeight());
                rawDetail.setWidth(sampleData.getWidth());
                rawDetail.setQuality(90);

                IsoFingerprintDetailDto isoDetail = new IsoFingerprintDetailDto();
                isoDetail.setData(sampleData.getIsoData() != null ? Base64.getEncoder().encodeToString(sampleData.getIsoData()) : "");

                FingerprintDto fingerprintDto = new FingerprintDto();
                fingerprintDto.setTrackingNumber(custNo.toString());
                fingerprintDto.setFingerprintRawData(Map.of("rThumb", rawDetail));
                fingerprintDto.setFngerprintISOData(Map.of("rThumb", isoDetail));

                try {
                    String jsonBody = objectMapper.writeValueAsString(fingerprintDto);

                    HttpHeaders headers = new HttpHeaders();
                    headers.setContentType(MediaType.APPLICATION_JSON);
                    String accessToken = "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VybmFtZSI6IkFETUlOMDEiLCJyb2xlIjoiQkFPIiwiYXV0aFR5cGUiOiJzZXNzaW9uSWQiLCJzdWIiOiIiLCJqdGkiOiIwZWYwMmQzMi05MTY3LTRjMmQtOGMzNy1jYzljYzNiZTcyYzYiLCJpYXQiOjE3MjU1MTMwMTcsImV4cCI6MTcyNTU5OTQxN30.oSxAtSy6Y06-GYuWFjSpa_YPnqVG-TXN442gq4B4PUw";
                    headers.set("Authorization", "Bearer " + accessToken);
                    HttpEntity<String> requestEntity = new HttpEntity<>(jsonBody, headers);
                    ResponseEntity<String> response = restTemplate.postForEntity(API_URL, requestEntity, String.class);

                    System.out.println(count++);
                    System.out.println("API Response for customer " + custNo + ": " + response.getBody());
                } catch (Exception e) {

                    System.err.println("Failed to send data for customer " + custNo + ": " + e.getMessage());
                }
            }
        }
    }


    @Override
    public void deleteEnrolledFinger() {

        String customerQuery = """
SELECT cust_no
  FROM GUMS.MB_USER_MST
 WHERE USER_TYPE = 'BAO'
                """;

        List<Long> customersList = jdbcTemplate.queryForList(customerQuery, Long.class);
int count = 0;
        for (Long customerNumber : customersList)
        {
            String accessToken = "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VybmFtZSI6IkFETUlOMDEiLCJyb2xlIjoiQkFPIiwiYXV0aFR5cGUiOiJzZXNzaW9uSWQiLCJzdWIiOiIiLCJqdGkiOiIwZWYwMmQzMi05MTY3LTRjMmQtOGMzNy1jYzljYzNiZTcyYzYiLCJpYXQiOjE3MjU1MTMwMTcsImV4cCI6MTcyNTU5OTQxN30.oSxAtSy6Y06-GYuWFjSpa_YPnqVG-TXN442gq4B4PUw";

            String uri = (UriComponentsBuilder.fromUri(URI.create(API_URL))
                    .queryParam("trackingNumber" , customerNumber).toUriString());


            try{
                System.out.println(count++);
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                headers.setBearerAuth(accessToken);

                HttpEntity<String> entity = new HttpEntity<>(headers);
                ResponseEntity<String> response = restTemplate.exchange(
                        uri, HttpMethod.DELETE, entity, String.class
                );
                System.out.println(response.getBody());
            }catch (Exception e )
            {
                System.out.println(e.getMessage());
            }

        }

    }

}