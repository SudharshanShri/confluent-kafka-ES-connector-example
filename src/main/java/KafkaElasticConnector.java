import io.confluent.connect.elasticsearch.ElasticsearchSinkTask;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.connect.sink.SinkRecord;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

public class KafkaElasticConnector {
    public static void main(String[] args) {
        ElasticsearchSinkTask sink=new ElasticsearchSinkTask();
        Map<String, String> props = getConnectorConfigMap();
        sink.start(props,null);
        Collection<SinkRecord> records;

    /*
        List<String> topics=new ArrayList<>();
        topics.add("test-elasticsearch-sink");
        consumer.subscribe(topics);
        consumer.assign(Arrays.asList(topicPartition));
        HashMap<TopicPartition, Long> beginningOffsets = new HashMap<>();
        beginningOffsets.put(topicPartition, 0L);
        consumer.assign(Arrays.asList(topicPartition));
       consumer.seek(topicPartition,0);*/

        KafkaConsumer<String, String> consumer = getStringStringKafkaConsumer();

        try {

            TopicPartition topicPartition = new TopicPartition("test-elasticsearch-sink",0);

            List<String> topics = new ArrayList<>();
            topics.add("test-elasticsearch-sink");
            consumer.subscribe(topics);
            do {
                ConsumerRecords kafka_records = consumer.poll(20000);
                records = process(kafka_records);
                consumer.commitSync();
                sink.open(new HashSet<>(Arrays.asList(topicPartition)));
                sink.put(records);
                sink.flush(null);
            }while(!records.isEmpty());

        }
        finally{
            consumer.close();
        }
    }

    private static Map<String, String> getConnectorConfigMap() {
        Map<String, String> props=new HashMap<>();

        props.put("type.name","elasticsearch-sink");
        props.put("connection.url","http://localhost:9200");
        props.put("key.ignore","true");
        props.put("schema.ignore","true");
        return props;
    }

    private static KafkaConsumer<String, String> getStringStringKafkaConsumer() {
        Map<String, String> kafka_props=new HashMap<>();
        try {
            kafka_props.put("client.id", InetAddress.getLocalHost().getHostName());
            kafka_props.put("group.id", "foo");
            kafka_props.put("bootstrap.servers", "localhost:9092");
            kafka_props.put("key.deserializer","org.apache.kafka.common.serialization.StringDeserializer");
            kafka_props.put("value.deserializer",KafkaAvroDeserializer.class.getName());
            kafka_props.put("schema.registry.url","http://localhost:8081");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        return (KafkaConsumer<String,String>) new KafkaConsumer(kafka_props);
    }

    private static Collection<SinkRecord> process(ConsumerRecords consumerRecords)
    {
        Collection<SinkRecord> records = new ArrayList<>();
        for(Object object:consumerRecords) {
            ConsumerRecord consumerRecord=(ConsumerRecord)object;
            SinkRecord sinkRecord = new SinkRecord(consumerRecord.topic(), consumerRecord.partition(), null, consumerRecord.key(), null, consumerRecord.value(), consumerRecord.offset());
            System.out.println(sinkRecord.topic());
            System.out.println(sinkRecord.value());
            records.add(sinkRecord);
        }
        return records;
    }
}
