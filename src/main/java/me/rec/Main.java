package me.rec;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Properties;

/**
 * @author Chenglong Ma
 */
public class Main {
    private static final Logger LOG = LogManager.getLogger(Main.class);

    /**
     * The main entry of the program
     *
     * @param args the options specified by user.
     */
    public static void main(String[] args) throws IOException {
        Command command = new Command();
        Properties cli = command.buildCli(args);
        Job job = new Job(cli);
        job.run();
        LOG.info("All done");
    }
}
