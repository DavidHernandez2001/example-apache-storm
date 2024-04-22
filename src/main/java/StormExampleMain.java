import bolts.FilterWordsBolt;
import bolts.PhraseToWordsBolt;
import bolts.ToLowerCaseBolt;
import bolts.WordCountBolt;
import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.tuple.Fields;
import spouts.SendPhrasesSpout;

public class StormExampleMain {

    public static void main(final String[] args) {
        final TopologyBuilder topologyBuilder = new TopologyBuilder();
        topologyBuilder.setSpout("SendPhrasesSpout", new SendPhrasesSpout(), 10);
        topologyBuilder.setBolt("PhraseToWordsBolt", new PhraseToWordsBolt(), 15).shuffleGrouping("SendPhrasesSpout");
        topologyBuilder.setBolt("ToLowerCaseBolt", new ToLowerCaseBolt(), 40).shuffleGrouping("PhraseToWordsBolt");
        topologyBuilder.setBolt("FilterWordsBolt", new FilterWordsBolt(), 20).shuffleGrouping("ToLowerCaseBolt");
        topologyBuilder.setBolt("WordCountBolt", new WordCountBolt(), 10).
                fieldsGrouping("FilterWordsBolt", new Fields("word"));


        final Config config = new Config();
        final LocalCluster stormCluster = new LocalCluster();
        stormCluster.submitTopology("storm-example", config, topologyBuilder.createTopology());
    }

}
