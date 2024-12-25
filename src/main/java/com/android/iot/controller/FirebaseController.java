package com.android.iot.controller;

import com.android.iot.entity.DeviceData;
import com.android.iot.service.FirebaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@RestController
public class FirebaseController {

    @Autowired
    private FirebaseService firebaseService;

//    @GetMapping("/pushData")
//    public String pushData() {
//        firebaseService.pushData("Hello, Firebase!");
//        return "Data pushed to Firebase!";
//    }

    @GetMapping("/readData")
    public String readData() {
        try {
            // Gọi service để lấy dữ liệu từ Firebase
            CompletableFuture<String> dataFuture = firebaseService.readData();
            return dataFuture.get(); // Trả về dữ liệu đã được tải
        } catch (InterruptedException | ExecutionException e) {
            return "Error retrieving data: " + e.getMessage();
        }
    }
    // API pushData để đẩy dữ liệu lên Firebase
    @PostMapping("/pushData")
    public String pushData(@RequestBody DeviceData deviceData) {
        try {
            // Đẩy dữ liệu lên Firebase
            firebaseService.pushData("", deviceData);
            return "Data pushed to Firebase successfully!";
        } catch (Exception e) {
            return "Error pushing data: " + e.getMessage();
        }
    }
}
