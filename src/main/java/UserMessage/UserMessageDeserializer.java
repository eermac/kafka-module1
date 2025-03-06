package UserMessage;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Deserializer;

public class UserMessageDeserializer implements Deserializer<UserMessage> {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public UserMessage deserialize(String topic, byte[] data) {
        if (data == null) {
            return null;
        }
        try {
            return objectMapper.readValue(data, UserMessage.class);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка десериализации объекта UserMessage", e);
        }
    }
}