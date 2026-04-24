package dev.ngb.realtime.dispatch;

public interface MessageBroker {

    void sendToUser(String userId, String destination, Object payload);

    void sendToTopic(String topic, String destination, Object payload);

    void broadcast(String destination, Object payload);
}
