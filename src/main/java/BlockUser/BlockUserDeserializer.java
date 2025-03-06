package BlockUser;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Deserializer;

public class BlockUserDeserializer implements Deserializer<BlockUser> {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public BlockUser deserialize(String topic, byte[] data) {
        if (data == null) {
            return null;
        }
        try {
            return objectMapper.readValue(data, BlockUser.class);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка десериализации объекта BlockUser", e);
        }
    }
}