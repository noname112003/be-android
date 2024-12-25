package com.android.iot.config;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.messaging.MessageChannel;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;

@Configuration
public class MqttConfig {

    @Bean
    public MqttClient mqttClient() throws MqttException {
        String brokerUrl = "tcp://broker.hivemq.com:1883"; // MQTT Broker URL
        String clientId = "ESP8266Client-"; // Unique Client ID

        // Create the MqttClient
        MqttClient mqttClient = new MqttClient(brokerUrl, clientId);

        // Configure connection options
        MqttConnectOptions options = new MqttConnectOptions();
        options.setServerURIs(new String[]{brokerUrl}); // Set broker URLs
        options.setCleanSession(true); // Clean session on reconnect
        options.setKeepAliveInterval(60); // Keep-alive interval in seconds

        // Connect the client
        mqttClient.connect(options);

        return mqttClient;
    }

//    @Bean
//    public MqttPahoClientFactory mqttPahoClientFactory() {
//        DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
//        MqttConnectOptions options = new MqttConnectOptions();
//        options.setServerURIs(new String[] { "tcp://broker.hivemq.com:1883" });
//        options.setCleanSession(true);
//        options.setKeepAliveInterval(60);
//        factory.setConnectionOptions(options);
//        return factory;
//    }
//
//    @Bean
//    public MqttPahoMessageDrivenChannelAdapter messageDrivenChannelAdapter(MqttPahoClientFactory mqttPahoClientFactory) {
//        MqttPahoMessageDrivenChannelAdapter adapter = new MqttPahoMessageDrivenChannelAdapter(
//                "ESP8266Client-", // Unique Client ID
//                mqttPahoClientFactory,
//                "pzem/data" // Topic to subscribe
//        );
//        adapter.setOutputChannel(messageChannel());
//        return adapter;
//    }
//
//    @Bean
//    public MessageChannel messageChannel() {
//        return new QueueChannel();
//    }
}