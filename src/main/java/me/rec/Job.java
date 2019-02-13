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

    @Deprecated
    private class CustomJob extends RecommenderJob {
        private final Configuration conf;

        public CustomJob(Configuration conf) {
            super(conf);
            this.conf = conf;
        }

        @Override
        public void saveResult(List<RecommendedItem> recommendedList) throws LibrecException, IOException, ClassNotFoundException {
            super.saveResult(recommendedList);
//            if (recommendedList != null && recommendedList.size() > 0) {
//                // make output path
//                String algoSimpleName = DriverClassUtil.getDriverName(getRecommenderClass());
//                String outputPath = conf.get("dfs.result.dir") + "/" + conf.get("data.input.path") + "-" + algoSimpleName + "-output/" + algoSimpleName;
//                if (null != dataModel && (dataModel.getDataSplitter() instanceof KCVDataSplitter || dataModel.getDataSplitter() instanceof LOOCVDataSplitter) && null != conf.getInt("data.splitter.cv.index")) {
//                    outputPath = outputPath + "-" + String.valueOf(conf.getInt("data.splitter.cv.index"));
//                }
//                LOG.info("Result path is " + outputPath);
//                // convert itemList to string
//                StringBuilder sb = new StringBuilder();
//                for (RecommendedItem recItem : recommendedList) {
//                    String userId = recItem.getUserId();
//                    String itemId = recItem.getItemId();
//                    String value = String.valueOf(recItem.getValue());
//                    sb.append(userId).append(",").append(itemId).append(",").append(value).append("\n");
//                }
//                String resultData = sb.toString();
//                // save resultData
//                try {
//                    FileUtil.writeString(outputPath, resultData);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }

        }
    }
}
