package net.codjo.utils;
import net.codjo.gui.utils.DisplayInternalFrameAction.WindowFactory;
import junit.framework.TestCase;
/**
 *
 */
public class DisplayInternalFrameActionTest extends TestCase {

    ClassTestDisplayInternalFrame classTestDisplayInternalFrame;
    WindowFactory testWindowFactory;


    public void test_getConstructor() throws Exception {
    }


    @Override
    protected void setUp() throws java.lang.Exception {
        testWindowFactory = new WindowFactory(ClassTestDisplayInternalFrame.class, new Object[]{
              "TestString1", "TestString2"
        }, new Class[]{String.class, String.class});
        classTestDisplayInternalFrame = (ClassTestDisplayInternalFrame)testWindowFactory.buildWindow();
        assertEquals(classTestDisplayInternalFrame.getParametreTest(), 2);
    }


    /**
     * The teardown method for JUnit
     *
     * @throws java.lang.Exception Description of Exception
     */
    @Override
    protected void tearDown() throws java.lang.Exception {
    }
}
