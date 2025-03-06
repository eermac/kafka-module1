package BlockUser;

import org.apache.kafka.common.serialization.Serdes;

public class BlockUserSerdes extends Serdes.WrapperSerde<BlockUser> {
    public BlockUserSerdes() {
        super(new BlockUserSerializer(), new BlockUserDeserializer());
    }
}