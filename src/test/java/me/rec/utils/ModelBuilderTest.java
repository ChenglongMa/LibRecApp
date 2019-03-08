package me.rec.utils;

import org.junit.Test;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

/**
 * @author Chenglong Ma
 */
public class ModelBuilderTest {

    @Test
    public void toText() {
    }

    @Test
    public void toArff() throws IOException, NoSuchFieldException {
        Properties properties = new Properties();
        properties.load(new FileReader(new File("E:\\Projects\\LibRecApp\\conf\\dataset\\cm100k-test.properties")));
        Config config = new Config(properties);
        config.set(Config.MODEL_FORMAT, Config.ModelFormat.ARFF);
        ModelBuilder.build(config, Config.ModelFormat.ARFF);
    }
}