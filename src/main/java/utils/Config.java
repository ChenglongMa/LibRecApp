package utils;

import net.librec.conf.Configuration;
import net.librec.conf.Configured;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * @author Chenglong Ma
 */
public class Config extends Configuration {
    public static final String CV_NUM = "data.splitter.cv.number";
    public static final String IS_RANKING = "rec.recommender.isranking";
    public static final String TOPN = "rec.recommender.ranking.topn";
    public static final String RANDOM_SEED = "rec.random.seed";
    public static final String DATA_INPUT_PATH = Configured.CONF_DATA_INPUT_PATH;
    public static final String TEXT_INPUT_PATH = "data.input.text.path";
    public static final String ARFF_INPUT_PATH = "data.input.arff.path";
    public static final String DATA_SEP = "data.convert.sep";
    public static final String DEFAULT_SEP = "[\t;, ]";
    public static final String DATASETS = "datasets";
    public static final String RECOMMENDERS = "recommenders";
    public static final String MODEL_FORMAT = "data.model.format";
    public static final String TEST_DATA_PATH = "data.testset.path";
    public static final String TRAIN_SUFFIXES = "data.input.path.suffixes";
    public static final String TEST_SUFFIXES = "data.testset.path.suffixes";

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

    public Config(Config other) throws IOException {
        this(other.getProps());
    }

    /**
     * @return the recommender array in the configuration.
     */
    public String[] getRecommenders() {
        return getStrings(RECOMMENDERS);
    }

    public String[] getTrainSetSuffixes() {
        return getStrings(TRAIN_SUFFIXES);
    }

    public String[] getTestSetSuffixes() {
        return getStrings(TEST_SUFFIXES);
    }

    /**
     * @return the datasets in the configuration.
     */
    public String[] getDatasets() {
        return getStrings(DATASETS);
    }

    public String getSeparator() {
        return get(Config.DATA_SEP, Config.DEFAULT_SEP);
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

    public String getFullPath(String subKey) {
        String subPath = get(subKey);
        return get(Configured.CONF_DFS_DATA_DIR) + "/" + subPath;
    }

    public String getFullPath(String subKey, String defaultSubPath) {
        String subPath = get(subKey);
        if (StringUtils.isBlank(subPath)) {
            subPath = defaultSubPath;
        }
        return get(Configured.CONF_DFS_DATA_DIR) + "/" + subPath;
    }

    public List<String> getFullPaths(String subKey) {
        return getFullPaths(subKey, null);
    }

    public List<String> getFullPaths(String subKey, String defaultSubPath) {
        String subPath = get(subKey);
        if (StringUtils.isBlank(subPath)) {
            subPath = defaultSubPath;
        }
        String[] subPaths = subPath.trim().split("[,:]");
        return Arrays.stream(subPaths).map(path -> get(Configured.CONF_DFS_DATA_DIR) + "/" + path).collect(Collectors.toList());
    }

    public static class ModelFormat {
        public static final String TEXT = "text";
        public static final String ARFF = "arff";
    }

    @Override
    public String toString() {
        return getProps().toString();
    }
}
