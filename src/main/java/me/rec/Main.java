package me.rec;

import me.rec.utils.Command;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Properties;

/**
 * @author Chenglong Ma
 */
public class Main {
    public static final String REC_PREFIX = "rec_prefix";


    /**
     * The main entry of the program
     *
     * @param args the options specified by user.
     */
    public static void main(String[] args) throws IOException {
        System.setProperty(REC_PREFIX, "default");
        Logger LOG = LogManager.getLogger(Main.class);
        Command command = new Command();
        Properties cli = command.buildCli(args);
        Job job = new Job(cli);
        job.run();
        LOG.info("All done");
    }
}
