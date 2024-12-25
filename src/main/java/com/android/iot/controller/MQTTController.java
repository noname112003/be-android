package com.android.iot.controller;

import com.android.iot.service.MQTTService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/mqtt")
public class MQTTController {

    private final MQTTService mqttService;

    @Autowired
    public MQTTController(MQTTService mqttService) {
        this.mqttService = mqttService;
    }

    @PostMapping("/send")
    public String sendMessage(@RequestParam String topic, @RequestParam int relayStatus) {
        try {
            mqttService.sendRelayStatus(topic, relayStatus);
            return "Message sent successfully!";
        } catch (Exception e) {
            e.printStackTrace();
            return "Error sending message";
        }
    }
}
