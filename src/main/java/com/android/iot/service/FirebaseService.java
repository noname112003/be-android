package com.android.iot.service;

import com.android.iot.entity.DeviceData;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DatabaseError;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
public class FirebaseService {

    private final FirebaseDatabase firebaseDatabase;

    public FirebaseService(FirebaseDatabase firebaseDatabase) {
        this.firebaseDatabase = firebaseDatabase;
    }

    public CompletableFuture<String> readData() {
        CompletableFuture<String> future = new CompletableFuture<>();
        DatabaseReference ref = firebaseDatabase.getReference("");

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Lấy dữ liệu từ snapshot và trả về dưới dạng JSON
                String data = dataSnapshot.getValue().toString();
                future.complete(data);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                future.completeExceptionally(new RuntimeException("Error reading data: " + databaseError.getMessage()));
            }
        });

        return future;
    }

    // Phương thức push dữ liệu lên Firebase sau khi xóa dữ liệu cũ
    public void pushData(String path, DeviceData deviceData) {
        // Định nghĩa reference đến Firebase Database tại một đường dẫn cụ thể
        DatabaseReference ref = firebaseDatabase.getReference(path);

        // Xóa dữ liệu cũ trước khi đẩy dữ liệu mới lên
        ref.removeValue((databaseError, databaseReference) -> {
            if (databaseError != null) {
                System.out.println("Data could not be removed: " + databaseError.getMessage());
            } else {
                System.out.println("Old data removed successfully.");

                // Sau khi xóa dữ liệu cũ, đẩy dữ liệu mới lên
                Map<String, Object> dataMap = new HashMap<>();
                dataMap.put("voltage", deviceData.getVoltage());
                dataMap.put("current", deviceData.getCurrent());
                dataMap.put("power", deviceData.getPower());
                dataMap.put("energy", deviceData.getEnergy());
                dataMap.put("frequency", deviceData.getFrequency());
                dataMap.put("powerFactor", deviceData.getPowerFactor());
                dataMap.put("relay", deviceData.getRelay());

                // Đẩy dữ liệu mới lên Firebase
                ref.setValue(dataMap, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        if (databaseError != null) {
                            System.out.println("Data could not be saved: " + databaseError.getMessage());
                        } else {
                            System.out.println("Data pushed to Firebase successfully");
                        }
                    }
                });
            }
        });
    }
}
