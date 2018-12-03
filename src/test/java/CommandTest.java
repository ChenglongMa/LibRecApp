import net.librec.util.StringUtil;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

/**
 * @author Chenglong Ma
 */
public class CommandTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void buildCli() {
    }

    @Test
    public void testCopy() {
        String[] a = {"a", "b", "c", "d"};
//        String[] b = a;
        String[] b = a.clone();
        b[0] = "123";
        System.out.printf("a=%s\n",Arrays.toString(a));
        System.out.printf("b=%s\n",Arrays.toString(b));
        Assert.assertNotEquals(a,b);
    }
}