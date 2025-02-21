import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Properties;

public class Producer {
    public static void main(String[] args) {
        // Конфигурация продюсера – адрес сервера, сериализаторы для ключа и значения.
        Properties properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        // Параметры сериализации сообщения
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.put(ProducerConfig.ACKS_CONFIG, "all"); //At Least Once
        properties.put(ProducerConfig.RETRIES_CONFIG, 2); //повторные попытки в случае неудачной отправки

        // Создание продюсера

        try (KafkaProducer<String, String> producer = new KafkaProducer<>(properties)) {
            ObjectMapper objectMapper = new ObjectMapper();
            User newUserData = new User(1, "zeka", "zeka@yandex.ru");
            // Сериализуем объект в JSON-строку
            String jsonMessage = objectMapper.writeValueAsString(newUserData);
            System.out.println(jsonMessage);

            // Отправка сообщения
            ProducerRecord<String, String> record = new ProducerRecord<>("test_topic", "key-1", jsonMessage);
            producer.send(record);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}