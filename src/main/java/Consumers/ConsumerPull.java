package Consumers;

import BlockUser.BlockUser;
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
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9093,localhost:9094,localhost:9095,localhost:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "my-consumer-group-2"); //группа консьюмеров для чтения топика
        //параметры десериализации ключа и значения сообщения
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false"); //ручной коммит

        ObjectMapper objectMapper = new ObjectMapper();

        // Чтение сообщений
        try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props)){
            // Подписка на топик
            consumer.subscribe(Collections.singletonList("blocked_users"));
            while (true) {
                //обращение к брокеру кафки за сообщениями с интервалом в 5 секунд
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(5000));
                for (ConsumerRecord<String, String> record : records) {
                    BlockUser userNewData = objectMapper.readValue(record.value(), BlockUser.class); //десериализация сообщения
                    System.out.printf("Получено сообщение блокировки: key = %s, \nПользователь = %s заблокировал пользователя user_name = %s\n offset = %d%n",
                            record.key(), userNewData.getSender(), userNewData.getBlockUser(), record.offset());
                }

                consumer.commitSync(); //подтверждение смещение коммитов

                Thread.sleep(2000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}