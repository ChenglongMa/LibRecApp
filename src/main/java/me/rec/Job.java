package me.rec;

import net.librec.common.LibrecException;
import net.librec.conf.Configuration;
import net.librec.job.RecommenderJob;
import net.librec.recommender.item.RecommendedItem;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URL;
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

        try {
            Config config = new Config(cmd);
            String[] datasets = config.getDatasets();
            for (String dataset : datasets) {
                URL dataURL = config.getPropertyURL(dataset);
                Properties dataProp = new Properties();
                dataProp.putAll(cmd);
                dataProp.load(dataURL.openStream());
                String[] recs = config.getRecommenders();
                for (String rec : recs) {
                    Properties recProp = new Properties();
                    URL recURL = config.getPropertyURL(rec);
                    recProp.putAll(dataProp);
                    recProp.load(recURL.openStream());
                    config = new Config(recProp);
                    RecommenderJob job = new RecommenderJob(config);
                    LOG.info(String.format("Running <%s> job for <%s>", rec, dataset));
                    job.runJob();
                }
            }
        } catch (Exception e) {
            LOG.error(e);
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
