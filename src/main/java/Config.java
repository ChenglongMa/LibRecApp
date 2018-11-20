import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Config {
    private static final String CONFIG_NAME = "/config.properties";
    private static final URL DATASETS = Config.class.getResource("datasets");

    public static URL getPATH() {
        return DATASETS;
    }

    public static List<String> getDatasets() {
        List<String> datasets = new ArrayList<>();
        File file = new File(DATASETS.getFile());
        if (file.isDirectory()) {
            for (File subFile : Objects.requireNonNull(file.listFiles())) {
                if (subFile.isDirectory()) {
                    datasets.add(subFile.getName());
                }
            }
        }
        return datasets;
    }

    public static String getDatasetsStr() {
        return String.join(", ", getDatasets());
    }
}
