package spouts;

import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichSpout;
import org.apache.storm.tuple.Fields;

import java.util.Collections;
import java.util.Map;
import java.util.Random;

public class SendPhrasesSpout extends BaseRichSpout {

    private static final String[] phrases = {"Hola como estas", "Hola como te va", "Este es otro ejemplo",
            "Ejemplo es lo que sobra", "Estas en el horno", "El horno de mi mama", "Estas fraces son un ejemplo de encaje",
            "Encaje tiene el vestido de mi mama", "Mama es mal", "Mama es buena", "Vamos los pibes", "Pibes era un perfume",
            "El perfume un gran libro es", "Ahora le pinto Yoda"};

    private SpoutOutputCollector collector;

    @Override
    public void open(final Map conf, final TopologyContext context, final SpoutOutputCollector collector) {
        this.collector = collector;
    }

    @Override
    public void nextTuple() {
        int rnd = new Random().nextInt(phrases.length);
        collector.emit(Collections.singletonList(phrases[rnd]));
    }

    @Override
    public void declareOutputFields(final OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("phrase"));
    }

}
