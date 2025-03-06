package BlockUser;

public class BlockUser {
    private int sender_id;
    private int block_user_id;

    public BlockUser(int sender_id, int block_user_id) {
        this.sender_id = sender_id;
        this.block_user_id = block_user_id;
    }

    public BlockUser() {
    }

    public Integer getSender() {
        return sender_id;
    }

    public void setSender(int sender_id) {
        this.sender_id = sender_id;
    }

    public Integer getBlockUser() {
        return block_user_id;
    }

    public void setBlockUser(int block_user_id) {
        this.block_user_id = block_user_id;
    }
}
