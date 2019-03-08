package me.rec;

import me.rec.utils.Config;
import me.rec.utils.ModelBuilder;
import net.librec.job.RecommenderJob;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
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
                    // override default configuration
                    config = new Config(recProp);
                    buildFile(config);
                    RecommenderJob job = new RecommenderJob(config);
                    LOG.info(String.format("Running <%s> job for <%s>", rec, dataset));
                    try {
                        job.runJob();
                    } catch (Exception e) {
                        LOG.error(e);
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                LOG.error(e);
            }
        }

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
        ModelBuilder.build(conf, format);
        String inputPath = conf.get(pathKey);
        conf.set(Config.DATA_INPUT_PATH, inputPath);
    }
}
