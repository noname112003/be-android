package com.android.iot.service;

import com.android.iot.entity.DeviceData;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.auth.oauth2.GoogleCredentials;
import org.eclipse.paho.client.mqttv3.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class MQTTFirebase {

    public static void main(String[] args) {
        try {
            InputStream serviceAccount = MQTTFirebase.class.getClassLoader().getResourceAsStream("btl-iot-9c8bc-firebase-adminsdk-pywxh-3cf3a9541d.json");

            if (serviceAccount == null) {
                throw new IOException("Tệp khóa dịch vụ không tìm thấy!");
            }

            // Cấu hình Firebase Admin SDK
            FirebaseOptions firebaseOptions = new FirebaseOptions.Builder() // Đổi tên từ 'options' thành 'firebaseOptions'
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .setDatabaseUrl("https://btl-iot-9c8bc-default-rtdb.asia-southeast1.firebasedatabase.app/") // Thay bằng URL Firebase của bạn
                    .build();

            // Khởi tạo Firebase
            FirebaseApp.initializeApp(firebaseOptions); // Sử dụng firebaseOptions thay cho options

            // Khởi tạo Firebase Realtime Database
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference ref = database.getReference("");

            // Cấu hình MQTT Client
            String broker = "broker.hivemq.com";  // Địa chỉ broker MQTT của bạn
            String topic = "pzem/data";  // Topic bạn muốn nhận dữ liệu

            MqttClient client = new MqttClient(broker, MqttClient.generateClientId());
            MqttConnectOptions mqttOptions = new MqttConnectOptions(); // Đổi tên từ 'options' thành 'mqttOptions'
            mqttOptions.setCleanSession(true);

            // Kết nối đến MQTT Broker
            client.connect(mqttOptions); // Sử dụng mqttOptions thay cho options
            System.out.println("Connected to MQTT Broker");

            // Đăng ký vào topic
            client.subscribe(topic, (topic1, message) -> {
                String payload = new String(message.getPayload());
                System.out.println("Received message: " + payload);

                // Giả sử payload là chuỗi JSON, bạn cần chuyển đổi nó thành đối tượng DeviceData
                ObjectMapper objectMapper = new ObjectMapper();
                try {
                    // Chuyển đổi JSON thành đối tượng DeviceData
                    DeviceData deviceData = objectMapper.readValue(payload, DeviceData.class);

                    // Đẩy đối tượng lên Firebase
                    pushDataToFirebase(ref, deviceData);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            // Để MQTT client tiếp tục nhận tin nhắn
            client.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                    System.out.println("Connection lost: " + cause.getMessage());
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    String payload = new String(message.getPayload());
                    System.out.println("Message arrived: " + payload);

                    // Giả sử payload là chuỗi JSON, bạn cần chuyển đổi nó thành đối tượng DeviceData
                    ObjectMapper objectMapper = new ObjectMapper();
                    try {
                        // Chuyển đổi JSON thành đối tượng DeviceData
                        DeviceData deviceData = objectMapper.readValue(payload, DeviceData.class);

                        // Đẩy dữ liệu lên Firebase
                        pushDataToFirebase(ref, deviceData);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                    // Không cần xử lý khi gửi thành công (trong trường hợp này)
                }
            });

        } catch (MqttException | IOException e) {
            e.printStackTrace();
        }
    }

    // Phương thức đẩy dữ liệu lên Firebase
    private static void pushDataToFirebase(DatabaseReference ref, DeviceData deviceData) {
        try {
            // Chuyển đối tượng DeviceData thành Map<String, Object>
            Map<String, Object> dataMap = new HashMap<>();
            dataMap.put("voltage", deviceData.getVoltage());
            dataMap.put("current", deviceData.getCurrent());
            dataMap.put("power", deviceData.getPower());
            dataMap.put("energy", deviceData.getEnergy());
            dataMap.put("frequency", deviceData.getFrequency());
            dataMap.put("powerFactor", deviceData.getPowerFactor());
            dataMap.put("relay", deviceData.getRelay());

            // Đẩy dữ liệu vào Firebase Realtime Database với CompletionListener
            ref.push().setValue(dataMap, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    if (databaseError != null) {
                        System.out.println("Data could not be saved: " + databaseError.getMessage());
                    } else {
                        System.out.println("Data pushed to Firebase successfully");
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
