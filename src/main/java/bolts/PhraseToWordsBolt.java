package bolts;

import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

public class PhraseToWordsBolt extends BaseRichBolt {

    private OutputCollector collector;

    @Override
    public void prepare(final Map stormConf, final TopologyContext context, final OutputCollector collector) {
        this.collector = collector;
    }

    @Override
    public void execute(final Tuple input) {
        final String phrase = input.getStringByField("phrase");
        Arrays.stream(phrase.split(" ")).forEach(w -> collector.emit(Collections.singletonList(w)));
    }

    @Override
    public void declareOutputFields(final OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("word"));
    }

}
