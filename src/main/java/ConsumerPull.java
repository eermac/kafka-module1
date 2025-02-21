import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

public class ConsumerPull {
    public static void main(String[] args) {
        // Настройка консьюмера – адрес сервера, сериализаторы для ключа и значения
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "my-consumer-group-2"); //группа консьюмеров для чтения топика
        //параметры десериализации ключа и значения сообщения
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false"); //ручной коммит

        ObjectMapper objectMapper = new ObjectMapper();

        // Чтение сообщений
        try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props)){
            // Подписка на топик
            consumer.subscribe(Collections.singletonList("test_topic"));
            while (true) {
                //обращение к брокеру кафки за сообщениями с интервалом в 5 секунд
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(5000));
                for (ConsumerRecord<String, String> record : records) {
                    User userNewData = objectMapper.readValue(record.value(), User.class); //десериализация сообщения
                    System.out.printf("Получено сообщение: key = %s, user_id = %s, user_name = %s, user_email = %s, offset = %d%n",
                            record.key(), userNewData.getId(), userNewData.getName(), userNewData.getEmail(), record.offset());
                }

                consumer.commitSync(); //подтверждение смещение коммитов

                Thread.sleep(2000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}