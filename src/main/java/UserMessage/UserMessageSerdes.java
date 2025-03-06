package UserMessage;

import org.apache.kafka.common.serialization.Serdes;

public class UserMessageSerdes extends Serdes.WrapperSerde<UserMessage> {
    public UserMessageSerdes() {
        super(new UserMessageSerializer(), new UserMessageDeserializer());
    }
}