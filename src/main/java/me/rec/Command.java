package me.rec;

import net.librec.util.StringUtil;
import org.apache.commons.cli.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.Properties;

/**
 * @author Chenglong Ma
 */
public class Command {
    private static final Logger LOG = LogManager.getLogger(Command.class);
    private final String[] defDatasets;
    private final String[] defRecommenders;
    private int cv;
    private boolean split;

    public Command() {
        Config defConf = new Config();
        defDatasets = defConf.getDatasets();
        defRecommenders = defConf.getRecommenders();
        cv = defConf.getInt(Config.CV_NUM);
        split = defConf.getLong(Config.RANDOM_SEED) == 1L;
    }

    /**
     * The usage of command line options
     *
     * @param options
     */
    private void help(Options options) {
        String header = "An application based on LibRec\n\n";
        String footer = "\nHave fun:)";
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("LibRecApp", header, options, footer, true);
        System.exit(-1);
    }

    /**
     * Build options based on requirements
     *
     * @return
     */
    private Options buildOptions() {
        //build options
        Options options = new Options();
        // example: using dataset "ml-1m" and "ml-100k"
        // -d ml-1m ml-100k
        String datasetDesc = String.format("The dataset(s) to be processed, " +
                        "choose from:\n %s\n(All of them will be applied if not specified)\n---"
                , Arrays.toString(defDatasets));
        Option.Builder datasetOpt = Option.builder(Opt.DATASET)
                .longOpt("dataset")
                .hasArgs()
                .desc(datasetDesc);
        options.addOption(datasetOpt.build());

        // example: 5-folds cross validation
        // -cv 5
        String cvDesc = String.format("The number of folds for cross validation, default: %d\n---", cv);
        options.addOption(Opt.CV, true, cvDesc);

        // example: to re-split the dataset
        // -s
        String splitDesc = "Whether to re-split the dataset, default: false\n" +
                "(Would split if there is no such subsets)\n---";
        options.addOption(Opt.SPLIT, "split", false,
                splitDesc);

        // example: using recommender "userknn", "itemknn" and "slopeone"
        // -r userknn itemknn slopeone
        String recDesc = String.format("The recommender(s) to be applied, " +
                        "choose from:\n %s\n(All of them will be applied if not specified)\n---"
                , Arrays.toString(defRecommenders));
        Option.Builder recOpt = Option.builder(Opt.RECOMMENDER)
                .longOpt("recommender")
                .hasArgs()
                .desc(recDesc);
        options.addOption(recOpt.build());
        return options;
    }

    /**
     * Build Command Line Interface
     *
     * @param args
     * @return the properties read from cli
     */
    public Properties buildCli(String[] args) {
        Properties prop = null;
        LOG.info("reading command line options");
        Options options = buildOptions();

        try {
            prop = new Properties();
            // build the parser
            CommandLineParser parser = new DefaultParser();
            // verify the input commands
            CommandLine cmd = parser.parse(options, args);

            // read commands
            String[] datasets = defDatasets.clone();
            if (cmd.hasOption(Opt.DATASET)) {
                datasets = cmd.getOptionValues(Opt.DATASET);
                if (!Arrays.asList(defDatasets).containsAll(Arrays.asList(datasets))) {
                    throw new IllegalArgumentException("Invalid datasets");
                }
                prop.setProperty(Config.DATASETS, StringUtil.arrayToString(datasets));
            }
            LOG.info("The processed datasets: " + Arrays.toString(datasets));
            if (cmd.hasOption(Opt.CV)) {
                cv = Integer.parseInt(cmd.getOptionValue(Opt.CV));
                prop.setProperty(Config.CV_NUM, String.valueOf(cv));
            }
            LOG.info("The number of folds for cross validation: " + cv);

            split = cmd.hasOption(Opt.SPLIT);
            long other = System.currentTimeMillis();//TODO: to be specified
            prop.setProperty(Config.RANDOM_SEED, String.valueOf(split ? other : 1));
            LOG.info("(re-)Split? :" + split);

            String[] recs = defRecommenders.clone();
            if (cmd.hasOption(Opt.RECOMMENDER)) {
                recs = cmd.getOptionValues(Opt.RECOMMENDER);
                if (!Arrays.asList(defRecommenders).containsAll(Arrays.asList(recs))) {
                    throw new IllegalArgumentException("Invalid recommenders");
                }
                prop.setProperty(Config.RECOMMENDERS, StringUtil.arrayToString(recs));
            }
            LOG.info("The applied recommenders: " + Arrays.toString(recs));
        } catch (Exception e) {
            LOG.error(e);
            // print usage and exit
            help(options);
        }
        return prop;
    }

    /**
     * The abbr options used for cli
     */
    private static class Opt {
        static final String RECOMMENDER = "r";
        static final String SPLIT = "s";
        static final String DATASET = "d";
        static final String CV = "cv";
    }
}
