import net.librec.conf.Configured;
import org.junit.Test;
import utils.Config;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.*;

public class JobTest {

    @Test
    public void buildDataSetConfs() {
        try {
            Config root = new Config();
            root.set(Config.DATA_INPUT_PATH, "file1.csv;file2.csv;file3.csv");
            root.set(Config.TEST_DATA_PATH, "test1.csv;test2.csv,test3.csv");
            Job job = new Job(null);
            for (Config conf : job.buildDataSetConfs(root)) {
                System.out.println("Next:");
                System.out.printf("%s %s\n", conf.get(Config.DATA_INPUT_PATH), conf.get(Config.TEST_DATA_PATH));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}