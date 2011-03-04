package x10dt.ui.editor;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import junit.framework.Assert;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import x10dt.ui.editor.X10DocProvider;

@SuppressWarnings( { "unused" })
public class X10DocProviderTest {
	private static X10DocProvider dp;
	private static String BOLD;
	private static String UNBOLD;
	private static String NEWLINE;
	private static String PARA;

	public static Object getPrivateField(Object o, String fieldName) throws Exception {
	    Field f= o.getClass().getDeclaredField(fieldName);
	    f.setAccessible(true);
	    return f.get(o);
	}

	public static Object invokePrivate(Object o, String methodName, Class<?>[] formalTypes, Object[] actualParms) throws Exception {
	    Method m= o.getClass().getDeclaredMethod(methodName, formalTypes);
	    m.setAccessible(true);
	    return m.invoke(o, actualParms);
	}

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		dp = new X10DocProvider();
		BOLD = (String) getPrivateField(dp, "BOLD");
		UNBOLD = (String) getPrivateField(dp, "UNBOLD");
		NEWLINE = (String) getPrivateField(dp, "NEWLINE");
		PARA = (String) getPrivateField(dp, "PARA");
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Test
	public void testAddNameToDoc() throws Throwable {

		String doc = (String) invokePrivate(dp, "addNameToDoc",
				new Class[] { String.class, String.class }, new Object[] {
						new String("name"), new String("doc") });

		Assert.assertEquals(
				"Incorrect format string produced for non-null doc", BOLD
						+ "name" + UNBOLD + PARA + "doc" + PARA, doc);

		String emptyDoc = (String) invokePrivate(dp, "addNameToDoc",
				new Class[] { String.class, String.class }, new Object[] {
						new String("name"), null });

		Assert.assertEquals("Incorrect format string produced for null doc",
				BOLD + "name" + UNBOLD + PARA + PARA, emptyDoc);

	}
}
