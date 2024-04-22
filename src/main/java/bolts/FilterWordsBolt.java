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

public class FilterWordsBolt extends BaseRichBolt {

    private static final String[] forbiddenPhrases = {"va", "es", "lo", "en", "de", "un", "el", "le"};

    private OutputCollector collector;

    @Override
    public void prepare(final Map stormConf, final TopologyContext context, final OutputCollector collector) {
        this.collector = collector;
    }

    @Override
    public void execute(final Tuple input) {
        final String word = input.getStringByField("word");
        if (!Arrays.stream(forbiddenPhrases).anyMatch(w -> w.equals(word))) {
            collector.emit(Collections.singletonList(word));
        }
    }

    @Override
    public void declareOutputFields(final OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("word"));
    }

}
