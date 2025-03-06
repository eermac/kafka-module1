<h1>Инструкция по запуску (с пояснением работы):</h1>

1) Создать топики:
Топик message для отправки сообщений
   kafka-topics --create --topic message --bootstrap-server localhost:9092 --partitions 3 --replication-factor 2
Топик filtered_message для отцензуренных сообщений и сообщений без заблокированных пользователей
   kafka-topics --create --topic filtered_messages --bootstrap-server localhost:9092 --partitions 3 --replication-factor 2
Топик blocked_users для блокировки пользователей
   kafka-topics --create --topic blocked_users --bootstrap-server localhost:9092 --partitions 3 --replication-factor 2
Топик censor_words для цензуры слов
   kafka-topics --create --topic censor_words --bootstrap-server localhost:9092 --partitions 3 --replication-factor 2
2) Запускаем приложение consumerPush - это приложение наш "конечный пользователь", который получает 
отфильтрованные сообщения из топика filtered_messages
3) Запускаем приложение SimpleKafkaStreamExample - это реализация библиотеки kafka stream. В нем мы превращаем данные 
в потоки и таблицы, чтобы затем отфильтровать данные в режиме реального времени
4) Запускаем приложение producerSendMessage - это реализация отправки сообщений. В нем мы отправляем сообщения 
с задержкой в топик message
5) В этот момент, приложение SimpleKafkaStreamExample обрабатывает полученные сообщения, проверяет наличие в нем цензуры
или заблокированных пользователей, но т.к. цензуры еще не было, и никто никого не блокировал, все сообщения дойдут до 
топика filtered_messages и попадут в приложение consumerPush
6) Затем добавляем цензуру через работу приложения ProducerAdminCensor (отправляет данные в топик censor_words), 
также один пользователь блокирует другого через приложение ProducerBlockUser (отправляет данные в топик blocked_users)
7) Запускаем вновь приложение по отправке данных producerSendMessage, в этот раз в приложении SimpleKafkaStreamExample
сработает цензура и блокировка пользователя, в результате в топик filtered_messages попадут отфильтрованные сообщения, 
а сообщения от заблокированного пользователя не дойдут до топика filtered_messages и приложения consumerPush.

Примечание: по какой то причине запуск может сопровождаться ошибкой подключения java.net.UnknownHostException: kafka2,
но если несколько раз перезапускать приложение, ошибка пропадает (делаю через MacOS и Docker Desktop)

Примечание 2: я пытался реализовать выборку заблокированных пользователей через leftjoin stream и table, но по итогу не вышло.
На мой взгляд мало примеров реализации тех или иных функций в теоретической части.