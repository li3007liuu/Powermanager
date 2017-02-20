// IMqttService.aidl
package com.splxtech.powermanagor;

// Declare any non-default types here with import statements

interface IMqttService {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    boolean mqttSubscribe(String topic,int mqttQOS);
    boolean mqttPubMessage(String topic,String Message,int mqttQOS);
}
