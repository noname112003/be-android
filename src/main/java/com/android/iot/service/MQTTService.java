package com.android.iot.service;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MQTTService {

    private final MqttClient mqttClient;

    @Autowired
    public MQTTService(MqttClient mqttClient) {
        this.mqttClient = mqttClient;
    }

    public void sendMessage(String topic, String message) throws Exception {
        MqttMessage mqttMessage = new MqttMessage(message.getBytes());
        mqttMessage.setQos(1);
        mqttClient.publish(topic, mqttMessage);
        System.out.println("Message sent to topic: " + topic);
    }
    // Hàm gửi giá trị relay về MQTT broker
    public void sendRelayStatus(String topic, int relayStatus) throws Exception {
        // Tạo đối tượng JSON để chứa giá trị relay
        JSONObject jsonMessage = new JSONObject();
        jsonMessage.put("relay", relayStatus); // Thêm giá trị relay vào JSON

        // Chuyển JSON thành chuỗi và gửi qua MQTT
        sendMessage(topic, jsonMessage.toString());
    }
}
