import BlockUser.BlockUser;
import BlockUser.BlockUserSerdes;
import UserMessage.UserMessage;
import UserMessage.UserMessageSerdes;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.state.Stores;

import java.util.*;

public class SimpleKafkaStreamsExample {
    public static void main(String[] args) {
        try {
            // Конфигурация Kafka Streams
            Properties config = new Properties();
            config.put(StreamsConfig.APPLICATION_ID_CONFIG, "simple-kafka-streams-app");
            config.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9093,localhost:9094,localhost:9095,localhost:9092");
            config.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
            config.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());

            // Создание топологии
            StreamsBuilder builder = new StreamsBuilder();

            // Создание persistent state store
            builder.addStateStore(
                    Stores.keyValueStoreBuilder(
                            Stores.persistentKeyValueStore("block-users-store"),
                            Serdes.String(),
                            new BlockUserSerdes()
                    )
            );

            // Использование метода table() для создания таблицы из Kafka-топика
            KTable<String, BlockUser> table = builder.table(
                    "blocked_users", // Название Kafka-топика
                    Consumed.with(Serdes.String(), new BlockUserSerdes()) // Указываем сериализаторы для ключа и значения
            );

            KTable<String, String> tableCensorWords = builder.table(
                    "censor_words",
                    Consumed.with(Serdes.String(), Serdes.String())
            );

            // Создание потока с использованием кастомных Serdes для UserMessage
            KStream<String, UserMessage> stream = builder.stream("message",
                    Consumed.with(Serdes.String(), new UserMessageSerdes()));

            // Тут просто проверка, какие данные поступают в stream
            stream.foreach((key, value) -> {
                System.out.println("Stream\nKey: " + key + ", Value: " + value);
            });

            // Перебор значений из таблицы заблокированных пользователей и запись их в другую коллекцию
            HashMap<Integer, Integer> map = new HashMap<>();
            table.toStream().foreach((key, value) -> map.put(value.getSender(), value.getBlockUser()));

            // Перебор значений из таблицы заблокированных слов и запись их в другую коллекцию
            List<String> list = new ArrayList<>();
            tableCensorWords.toStream().foreach((key, value) -> list.add(value));

            //Фильтруем записи в потоке
            stream.filter((key, value) -> {
                for (String word: list) {
                    System.out.println("Слово " + word);
                    if (value.getMessage().contains(word)) {
                        System.out.println("\nTEST " + value.getMessage() + "\n");
                        value.setMessage(value.getMessage().replace(word, "!!!ЦЕНЗУРА!!!"));
                        System.out.println("\nTEST " + value.getMessage() + "\n");
                    }
                }

                if (map.containsKey(value.getReceiver()) & map.containsValue(value.getSender())) {
                    System.out.println("Заблокированный пользователь " + value.getSender()
                            + " отправил сообщение пользователю " + value.getReceiver()
                            + ".\nСообщение " + value.getMessage() + " заблокировано");
                    return false;
                } else {
                    System.out.println("Пользователь " + value.getSender()
                            + " отправил сообщение пользователю " + value.getReceiver()
                            + ".\nСообщение " + value.getMessage() + " отправлено");
                    return true;
                }
            }).to("filtered_messages");

            // Создание и запуск Kafka Streams
            KafkaStreams streams = new KafkaStreams(builder.build(), config);
            streams.start();
            System.out.println("Kafka Streams приложение успешно запущено.");
        } catch (Exception e) {
            System.err.println("Ошибка при запуске Kafka Streams приложения: " + e.getMessage());
        }
    }
}