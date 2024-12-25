package com.android.iot.service;

import com.android.iot.entity.DeviceData;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class MQTTSubscriberService {

    private final MqttClient mqttClient;
    private final FirebaseService firebaseService; // Service để push dữ liệu lên Firebase
    private final ObjectMapper objectMapper; // Dùng để parse JSON

    @Autowired
    public MQTTSubscriberService(MqttClient mqttClient, FirebaseService firebaseService, ObjectMapper objectMapper) {
        this.mqttClient = mqttClient;
        this.firebaseService = firebaseService;
        this.objectMapper = objectMapper;
    }

    @PostConstruct
    public void subscribe() {
        try {
            mqttClient.subscribe("pzem/data", (topic, message) -> {
                String msg = new String(message.getPayload());
                System.out.println("Message received from topic " + topic + ": " + msg);

                // Parse JSON thành đối tượng DeviceData
                try {
                    DeviceData deviceData = objectMapper.readValue(msg, DeviceData.class);
                    // Gọi phương thức pushData để đẩy dữ liệu lên Firebase
                    firebaseService.pushData("", deviceData);
                } catch (Exception e) {
                    System.err.println("Failed to parse JSON message: " + e.getMessage());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
