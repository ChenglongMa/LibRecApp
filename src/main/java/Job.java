import net.librec.conf.Configured;
import net.librec.job.RecommenderJob;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.checkerframework.checker.units.qual.C;
import utils.Config;
import utils.DataConverter;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * @author Chenglong Ma
 */
public class Job implements Runnable {
    private static final Logger LOG = LogManager.getLogger(Job.class);
    private final Properties cmd;

    public Job(Properties cmd) {
        this.cmd = cmd;
    }

    @Override
    public void run() {
        // Config priority
        // 1. Command line
        // 2. recommender config
        // 3. dataset config
        // 4. default config
        Config config = null;
        String[] datasets = new String[0];
        try {
            config = new Config(cmd);
            datasets = config.getDatasets();

        } catch (IOException e) {
            e.printStackTrace();
        }
        for (String dataset : datasets) {
            try {
                // Read and load dataset config
                FileReader dataFile = config.getPropertyFile(dataset);
                Properties dataProp = new Properties();
                dataProp.putAll(cmd);
                dataProp.load(dataFile);

                String[] recs = config.getRecommenders();
                for (String rec : recs) {
                    Properties recProp = new Properties();
                    FileReader recFile = config.getPropertyFile(rec);
                    recProp.putAll(dataProp);
                    recProp.load(recFile);
                    recProp.putAll(cmd);
                    // override default configuration
                    config = new Config(recProp);
//                    buildFile(config);
                    LOG.info(String.format("Running <%s> job for <%s>", rec, dataset));
                    for (Config subConf : buildDataSetConfs(config)) {
                        run(subConf);
//                        System.out.println(subConf);
                    }
                }
            } catch (Exception e) {
                LOG.error(e);
            }
        }

    }

    private void run(Config config) {
        RecommenderJob job = new RecommenderJob(config);
        try {
            job.runJob();
        } catch (Exception e) {
            LOG.error(e);
            e.printStackTrace();
        }
    }


    public List<Config> buildDataSetConfs(Config conf) throws IOException {
        String sep = "[ \t:,;]";
        String[] trainDataPaths = conf.get(Configured.CONF_DATA_INPUT_PATH).trim().split(sep);
        String[] testDataPaths = conf.get(Config.TEST_DATA_PATH).trim().split(sep);
        List<Config> res = new ArrayList<>();
        for (String trainDataPath : trainDataPaths) {
            for (String testDataPath : testDataPaths) {
                Config newConf = new Config(conf);
                newConf.set(Configured.CONF_DATA_INPUT_PATH, trainDataPath);
                newConf.set(Config.TEST_DATA_PATH, testDataPath);
                res.add(newConf);
            }
        }
        return res;
    }

    private void buildFile(Config conf) throws IOException, NoSuchFieldException {
        String format = conf.get(Config.MODEL_FORMAT);
        String pathKey;
        switch (format) {
            default:
            case Config.ModelFormat.TEXT:
                pathKey = Config.TEXT_INPUT_PATH;
                break;
            case Config.ModelFormat.ARFF:
                pathKey = Config.ARFF_INPUT_PATH;
                break;
        }
        // convert to target formatted file
        DataConverter.build(conf, format);
        String inputPath = conf.get(pathKey);
        conf.set(Configured.CONF_DATA_INPUT_PATH, inputPath);
    }
}
