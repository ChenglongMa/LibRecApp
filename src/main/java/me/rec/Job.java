package me.rec;

import net.librec.common.LibrecException;
import net.librec.conf.Configuration;
import net.librec.job.RecommenderJob;
import net.librec.recommender.item.RecommendedItem;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileReader;
import java.io.IOException;
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
                    config = new Config(recProp);
                    RecommenderJob job = new RecommenderJob(config);
                    LOG.info(String.format("Running <%s> job for <%s>", rec, dataset));
                    try {
                        job.runJob();
                    } catch (Exception e) {
                        LOG.error(e);
                    }
                }
            } catch (Exception e) {
                LOG.error(e);
            }
        }

    }

    private class CustomJob extends RecommenderJob {

        public CustomJob(Configuration conf) {
            super(conf);
        }

        @Override
        public void saveResult(List<RecommendedItem> recommendedList) throws LibrecException, IOException, ClassNotFoundException {
            super.saveResult(recommendedList);

        }
    }
}
