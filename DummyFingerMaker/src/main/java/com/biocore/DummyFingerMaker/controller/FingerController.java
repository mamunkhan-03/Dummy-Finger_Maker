package com.biocore.DummyFingerMaker.controller;
import com.biocore.DummyFingerMaker.service.FingerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/finger")
public class FingerController {
    private FingerService fingerService;
  
  public FingerController(FingerService fingerService) {
        this.fingerService = fingerService;
    }

    @PostMapping("/populate")
    public ResponseEntity<String> populateDummyData() {


        try {
            fingerService.populateDummyFingerData();
            //return ResponseEntity.ok("Dummy finger data population started.");
            return new ResponseEntity<>("Dummy finger data population started.", HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to populate dummy finger data: " + e.getMessage());
        }
    }

    @GetMapping("/populate/delete")
    public ResponseEntity<String> deleteData() {


        try {
            fingerService.deleteEnrolledFinger();
            //return ResponseEntity.ok("Dummy finger data population started.");
            return new ResponseEntity<>("Success", HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete " + e.getMessage());
        }


    }


    }
