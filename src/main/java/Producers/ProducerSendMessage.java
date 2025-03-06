package Producers;

import UserMessage.UserMessage;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class ProducerSendMessage {
    public static void main(String[] args) {
        // Конфигурация продюсера – адрес сервера, сериализаторы для ключа и значения.
        Properties properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9093,localhost:9094,localhost:9095,localhost:9092");
        // Параметры сериализации сообщения
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.put(ProducerConfig.ACKS_CONFIG, "all"); //At Least Once
        properties.put(ProducerConfig.RETRIES_CONFIG, 2); //повторные попытки в случае неудачной отправки

        // Создание продюсера

        try (KafkaProducer<String, String> producer = new KafkaProducer<>(properties)) {
            ObjectMapper objectMapper = new ObjectMapper();

            List<UserMessage> listUserMessage = Arrays.asList(
                    new UserMessage(1, 9, "testMessage for my friend"),
                    new UserMessage(2, 5, "i love apple company"),
                    new UserMessage(8, 54, "it's cool!!!"),
                    new UserMessage(1, 9, "answer me pls"));

            // Сериализуем объект в JSON-строку
            for (UserMessage value: listUserMessage) {
                String jsonMessage = objectMapper.writeValueAsString(value);
                System.out.println(jsonMessage);

                // Отправка сообщения
                ProducerRecord<String, String> record = new ProducerRecord<>("message", "key-1", jsonMessage);
                producer.send(record);

                Thread.sleep(3000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}