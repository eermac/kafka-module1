package Consumers;

import UserMessage.UserMessage;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

public class ConsumerPush {
    public static void main(String[] args) {
        // Настройка консьюмера – адрес сервера, сериализаторы для ключа и значения
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9093,localhost:9094,localhost:9095,localhost:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "my-consumer-group-1"); //группа консьюмеров для чтения топика
        //параметры десериализации ключа и значения сообщения
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true"); //явное указание автоматических коммитов (по умолчанию и так true)

        ObjectMapper objectMapper = new ObjectMapper();

        // Чтение сообщений
        try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props)) {
            // Подписка на топик
            consumer.subscribe(Collections.singletonList("filtered_messages"));
            while (true) {
                //обращение к брокеру кафки за сообщениями с интервалом в 0.01 секунд
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(10));
                for (ConsumerRecord<String, String> record : records) {
                    UserMessage userNewData = objectMapper.readValue(record.value(), UserMessage.class); //десериализация сообщения
                    System.out.printf("Получено сообщение: Отправитель = %s, Получатель = %s, Сообщение = %s, offset = %d%n",
                            userNewData.getSender(), userNewData.getReceiver(), userNewData.getMessage(), record.offset());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}