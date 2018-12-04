package me.rec;

import net.librec.conf.Configuration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

/**
 * @author Chenglong Ma
 */
public class Config extends Configuration {
    public static final String CV_NUM = "data.splitter.cv.number";
    public static final String RANDOM_SEED = "rec.random.seed";
    public static final String DATA_INPUT_PATH = "data.input.path";
    public static final String DATASETS = "datasets";
    public static final String RECOMMENDERS = "recommenders";
    private static final Logger LOG = LogManager.getLogger(Config.class);
    private static final String ROOT = System.getProperty("user.dir") + File.separator;
    private static final String CONF_PATH = //"conf/default.properties";
            ROOT + "conf" + File.separator + "default.properties";

    public Config() throws IOException {
        super();
        if (new File(CONF_PATH).exists()) {
            Properties prop = new Properties();
            prop.load(new FileReader(CONF_PATH));
            addResource(new Resource(prop));
            LOG.info("Load default config file: " + CONF_PATH);
        } else throw new FileNotFoundException(CONF_PATH);
    }

    public Config(Properties cmd) throws IOException {
        this();
        if (cmd != null) {
            addResource(new Resource(cmd, "cmd"));
        }
    }

    /**
     * @return the recommender array in the configuration.
     */
    public String[] getRecommenders() {
        return getStrings(RECOMMENDERS);
    }

    /**
     * @return the datasets in the configuration.
     */
    public String[] getDatasets() {
        return getStrings(DATASETS);
    }

    /**
     * Returns the custom setting by the specified resource name.
     * <p>
     * example:
     * FileReader ml_1m_file = getPropertyFile("ml-1m");
     * </p>
     *
     * @param resName the resource name
     * @return the custom setting path
     */
    public FileReader getPropertyFile(String resName) throws FileNotFoundException {
        String propKey = resName + ".properties";
        String propValue = ROOT + get(propKey);
        return new FileReader(propValue);
    }

}
