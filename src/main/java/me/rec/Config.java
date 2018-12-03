package me.rec;

import net.librec.conf.Configuration;

import java.net.URL;
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
    private static final String CONF_PATH = "default.properties";

    public Config() {
        super();
        Configuration.addDefaultResource(CONF_PATH);
    }

    public Config(Properties cmd) {
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
     * URL ml_1m_URL = getPropertyURL("ml-1m");
     * </p>
     *
     * @param resName the resource name
     * @return the custom setting path
     */
    public URL getPropertyURL(String resName) {
        String propKey = resName + ".properties";
        String propValue = get(propKey);
        return getResource(propValue);
    }

}
