/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.operation.imports;
import java.io.IOException;
import java.io.StringReader;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
/**
 * Test FixedReader.
 *
 * @author $Author: gonnot $
 * @version $Revision: 1.1.1.1 $
 */
public class FixedReaderTest extends TestCase {
    /**
     * Constructor for the FixedReaderTest object
     *
     * @param name Description of Parameter
     */
    public FixedReaderTest(String name) {
        super(name);
    }

    /**
     * A unit test suite for JUnit
     *
     * @return The test suite
     */
    public static Test suite() {
        return new TestSuite(FixedReaderTest.class);
    }


    /**
     * Constructor for the test_readLine object
     *
     * @exception Exception Description of Exception
     */
    public void test_readLine() throws Exception {
        FixedReader reader = new FixedReader(new StringReader("aabbcc"), 2);
        assertEquals(reader.readLine(), "aa");
        assertEquals(reader.readLine(), "bb");
        assertEquals(reader.readLine(), "cc");
        assertNull(reader.readLine());
    }


    /**
     * A unit test for JUnit
     *
     * @exception Exception Description of Exception
     */
    public void test_readLine_SautDeLigne() throws Exception {
        FixedReader reader = new FixedReader(new StringReader("aab\ncc"), 2);
        assertEquals(reader.readLine(), "aa");
        assertEquals(reader.readLine(), "b\n");
        assertEquals(reader.readLine(), "cc");
    }


    /**
     * A unit test for JUnit
     *
     * @exception Exception Description of Exception
     */
    public void test_readLine_Error() throws Exception {
        FixedReader reader = new FixedReader(new StringReader("aab"), 2);
        assertEquals(reader.readLine(), "aa");
        try {
            reader.readLine();
            fail("La ligne est incomplete");
        }
        catch (IOException e) {}
    }
}
