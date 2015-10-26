import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.util.Collector;

/**
 * Created by Jan Leupolt on 25.10.15.
 */
public class App {

    public static void main(String[] args) throws Exception {
        //Load l = new Load().getInstance();
        //l.loadSoccerFile("/Users/KInD/Downloads/full-game2");
        final ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
        DataSet<String> text = env.readTextFile("/Users/KInD/Downloads/full-game2");
        /*

        DataSet<String> text = env.fromElements(
                "Who's there?",
                "I think I hear them. Stand, ho! Who's there?",
                "Jan ist auch dabei",
                "Jenny ist auch dabei",
                " wer ist noch dabei"
        );
        */
        DataSet<Tuple2<Integer,Integer,Integer, Integer,Integer,Integer,Integer, Integer,Integer,Integer,Integer, Integer,Integer>> wordCounts = text
                .flatMap(new LineSplitter())
                .groupBy(0)

                .sum(1);

        wordCounts.print();

        env.execute("Word Count Example");
    }

    public static class LineSplitter implements FlatMapFunction<String, Tuple2<String, Integer>> {
        public void flatMap(String line, Collector<Tuple2<String, Integer>> out) {
            for (String word : line.split(",")) {
                out.collect(new Tuple2<String, Integer>(word, 1));
            }
        }
    }

}
