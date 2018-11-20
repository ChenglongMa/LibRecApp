import org.apache.commons.cli.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class Main {
    private static final Log LOG = LogFactory.getLog(Main.class);
    private static String dataName = Config.getDatasets().get(0);
    private static int cv = 10;
    private static boolean split = false;

    /**
     * The usage of command line options
     *
     * @param options
     */
    private static void help(Options options) {
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
    private static Options buildOpts() {
        //build options
        Options options = new Options();
        // example: using "ml-1m" dataset
        // -d ml-1m
        String datasetDesc = String.format("The dataset to be processed,\n" +
                "choose from: <%s>\n" +
                "default:<%s>", Config.getDatasetsStr(), dataName);
        options.addOption(Opt.DATASET, "dataset", true, datasetDesc);

        // example: 5-folds cross validation
        // -cv 5
        options.addOption(Opt.CV, true,
                "The number of folds for cross validation, default: " + cv);

        // example: to re-split the dataset
        // -s
        String splitDesc = "Whether to re-split the dataset, " +
                "would split if there is no such subsets";
        options.addOption(Opt.SPLIT, "split", false,
                splitDesc);
        return options;
    }

    /**
     * Build Command Line Interface
     *
     * @param args
     */
    private static void buildCli(String[] args) {
        LOG.info("reading command line options");
        Options options = buildOpts();

        // build the parser
        CommandLineParser parser = new DefaultParser();
        CommandLine cmd;
        try {
            // verify the input commands
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            // print usage and exit
            help(options);
            return;
        }

        // read commands
        if (cmd.hasOption(Opt.DATASET)) {
            dataName = cmd.getOptionValue(Opt.DATASET);
            if (!Config.getDatasets().contains(dataName)) {
                help(options);
            }
            LOG.info("The processed dataset: " + dataName);
            switch (dataName) {
                //todo
            }
        }
        if (cmd.hasOption(Opt.CV)) {
            cv = Integer.parseInt(cmd.getOptionValue(Opt.CV));
            LOG.info("cv: " + cv);
        }

        split = cmd.hasOption(Opt.SPLIT);
    }

    public static void main(String[] args) {
        buildCli(args);
        System.out.println(Config.getDatasets().get(0));
        LOG.info("starting server");
    }

    private static class Opt {
        static final String SPLIT = "s";
        static final String DATASET = "d";
        static final String CV = "cv";

    }
}
