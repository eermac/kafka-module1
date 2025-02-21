<h1>Инструкция по запуску (с пояснением работы):</h1>

1. Выполнить команду docker-compose up -d (устанавливает и запускает контейнеры)

2. Проверить, что все контейнеры запущены командой docker ps (должно быть 3 контейнера: 1 zookeeper, 2 kafka)

3. В терминале программы docker desktop переключиться на терминал контейнера с кафкой и выполнить команду по созданию
топика кафки (в моем случае нужно перейти в директорию с kafka-topics):
cd ..
cd ..
cd usr/bin
kafka-topics --create --topic test_topic --bootstrap-server localhost:9092 --partitions 3 --replication-factor 2
   (можно также из терминала зайти через команду docker exec -it <id контейнера> /bin)
4. Убедиться, что топик создан командой: kafka-topics --describe --topic test_topic --bootstrap-server localhost:9092
5. Запустить консьюмеры (я запускаю из idea) поочередно src/main/java/ConsumerPull, затем src/main/java/ConsumerPush
6. Затем запустить Producer, который отправляет сообщение в кафку 1 раз, в этот момент можно смотреть пришло ли сообщение
до consumer кафки через логи в idea. Косньюмеры прочитают сообщение параллельно друг другу.