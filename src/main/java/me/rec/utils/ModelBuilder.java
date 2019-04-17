package me.rec.utils;

import me.tongfei.progressbar.ProgressBar;
import net.librec.conf.Configured;
import net.librec.util.FileUtil;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.Arrays;
import java.util.regex.Pattern;

/**
 * @author Chenglong Ma
 */
public class ModelBuilder {
    private static final Logger LOG = LogManager.getLogger(ModelBuilder.class);

    public static void build(Config config, String modelFormat) throws IOException {
        String[] paths = getPaths(config, modelFormat);
        String src = paths[0];
        String res = paths[1];
        if (FileUtil.exist(res)) {
            LOG.info("Such result file has existed: " + res);
            return;
        }
        if (!FileUtil.exist(src)) {
            throw new FileNotFoundException(src);
        }
        LOG.info("To convert to " + modelFormat + " format.");
        BufferedReader br = new BufferedReader(new FileReader(src));
        BufferedWriter bw = new BufferedWriter(new FileWriter(res));
        switch (modelFormat) {
            case Config.ModelFormat.ARFF:
                toArff(config, br, bw);
                break;
            case Config.ModelFormat.TEXT:
                toText(config, br, bw);
                break;
        }
        LOG.info("Converting Done. Result file: " + res);
    }

    private static void toText(Config config, BufferedReader br, BufferedWriter bw) throws IOException {
        String row;
        String colFormat = config.get(Configured.CONF_DATA_COLUMN_FORMAT, "UIR");
        while (StringUtils.isNotBlank(row = br.readLine())) {
            String upperRow = row.toUpperCase().trim();
            if (startsWith(upperRow, "%", "@RELATION", "@DATA", "@ATTRIBUTE")) {
                continue;
            }
            String[] cols = row.split(",");
            String[] new_cols = Arrays.copyOfRange(cols, 0, colFormat.length());
            bw.write(String.join(",", new_cols));
            bw.newLine();
        }
    }

    private static void toArff(Config config, BufferedReader br, BufferedWriter bw) throws IOException {
        String sep = config.getSeparator();
        Pattern pattern = Pattern.compile(sep);
        String row;
        String colFormat = config.get(Configured.CONF_DATA_COLUMN_FORMAT, "UIR");
        bw.write("@RELATION user-item\n\n");
        writeAttributes(bw, colFormat);
        bw.write("@DATA\n");
        try (ProgressBar pb = new ProgressBar("To Arff", 100000)) {
            while (StringUtils.isNotBlank(row = br.readLine())) {

                String[] cols = pattern.split(row);
                if (cols.length < colFormat.length()) {
                    continue;
                }
                pb.step();
                String[] new_cols = Arrays.copyOfRange(cols, 0, colFormat.length());
                bw.write(String.join(",", new_cols));
                bw.newLine();
            }
        }
    }

    private static void writeAttributes(BufferedWriter bw, String colFormat) throws IOException {

        for (char c : colFormat.toLowerCase().toCharArray()) {
            String atrr = null, type = null;
            switch (c) {
                case 'u':
                    atrr = "user";
                    type = "STRING";
                    break;
                case 'i':
                    atrr = "item";
                    type = "STRING";
                    break;
                case 'r':
                    atrr = "rating";
                    type = "NUMERIC";
                    break;
                case 't':
                    atrr = "time";
                    type = "STRING";
                    break;
            }
            bw.write(String.format("@ATTRIBUTE %s %s\n", atrr, type));
        }
        bw.newLine();
        bw.newLine();
    }

    /**
     * Returns source path and result path
     *
     * @param config
     * @param resFormat
     * @return
     */
    private static String[] getPaths(Config config, String resFormat) throws FileNotFoundException {
        String inKey, outKey;
        switch (resFormat) {
            default:
            case Config.ModelFormat.ARFF:
                inKey = Config.TEXT_INPUT_PATH;
                outKey = Config.ARFF_INPUT_PATH;
                break;
            case Config.ModelFormat.TEXT:
                inKey = Config.ARFF_INPUT_PATH;
                outKey = Config.TEXT_INPUT_PATH;
        }
        String extension = "." + resFormat;
        String srcPath = config.getFullPath(inKey);
        String defResPath = FilenameUtils.removeExtension(srcPath) + extension;
        return new String[]{
                srcPath,
                config.getFullPath(outKey, defResPath)
        };
    }

    private static boolean startsWith(String src, String... prefixs) {
        for (String prefix : prefixs) {
            if (src.startsWith(prefix)) {
                return true;
            }
        }
        return false;
    }
}
