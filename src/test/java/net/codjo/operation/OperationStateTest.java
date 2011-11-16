/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.operation;
import java.util.Date;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
/**
 * Overview.
 *
 * @author $Author: gonnot $
 * @version $Revision: 1.1.1.1 $
 */
public class OperationStateTest extends TestCase {
    /**
     * Constructor for the OperationStateTest object
     *
     * @param name Description of Parameter
     */
    public OperationStateTest(String name) {
        super(name);
    }

    /**
     * A unit test suite for JUnit
     *
     * @return The test suite
     */
    public static Test suite() {
        return new TestSuite(OperationStateTest.class);
    }


    /**
     * A unit test for JUnit
     */
    public void test_constructor() {
        OperationState opeState = new OperationState();
        assertEquals(opeState.getState(), OperationState.TO_DO);
        assertEquals(opeState.getDate(), null);
    }


    /**
     * A unit test for JUnit
     */
    public void test_setState_DONE() {
        OperationState opeState;
        opeState = new OperationState(new Date(), OperationState.DONE);
        assertEquals(opeState.getState(), OperationState.DONE);
        assertEquals(opeState.getDate(), new Date());
    }


    /**
     * A unit test for JUnit
     */
    public void test_setState_FAILED() {
        OperationState opeState;
        opeState = new OperationState(new Date(), OperationState.FAILED);
        assertEquals(opeState.getState(), OperationState.FAILED);
        assertEquals(opeState.getDate(), new Date());
    }


    /**
     * A unit test for JUnit
     */
    public void test_setState_TO_DO() {
        OperationState opeState;

        opeState = new OperationState(new Date(), OperationState.FAILED);
        assertEquals(opeState.getState(), OperationState.FAILED);

        opeState = new OperationState(null, OperationState.TO_DO);
        assertEquals(opeState.getState(), OperationState.TO_DO);
        assertEquals(opeState.getDate(), null);
    }


    /**
     * A unit test for JUnit
     */
    public void test_setState_TO_DO_bad() {
        OperationState opeState;
        try {
            opeState = new OperationState(new Date(), OperationState.TO_DO);
            fail("should throw illegalArgument");
        }
        catch (IllegalArgumentException e) {}
    }


    /**
     * A unit test for JUnit
     */
    public void test_setState_badDate() {
        OperationState opeState;
        try {
            opeState = new OperationState(null, OperationState.DONE);
            fail("should throw illegalArgument");
        }
        catch (IllegalArgumentException e) {}
    }


    /**
     * A unit test for JUnit
     */
    public void test_setState_unknown() {
        OperationState opeState;
        try {
            opeState = new OperationState(new Date(), 258);
            fail("should throw illegalArgument");
        }
        catch (IllegalArgumentException e) {}
    }
}
