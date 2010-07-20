package org.eclipse.imp.x10dt.ui.editor;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;

import junit.framework.Assert;
import junitx.util.PrivateAccessor;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.imp.language.ServiceFactory;
import org.eclipse.imp.parser.ISourcePositionLocator;
import org.eclipse.imp.services.IReferenceResolver;
import org.eclipse.imp.x10dt.ui.X10DTUIPlugin;
import org.eclipse.jface.text.BadLocationException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.osgi.framework.Bundle;

@SuppressWarnings( { "unused" })
public class X10DocProviderTest {
	private static X10DocProvider dp;
	private static String BOLD;
	private static String UNBOLD;
	private static String NEWLINE;
	private static String PARA;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		dp = new X10DocProvider();
		BOLD = (String) PrivateAccessor.getField(dp, "BOLD");
		UNBOLD = (String) PrivateAccessor.getField(dp, "UNBOLD");
		NEWLINE = (String) PrivateAccessor.getField(dp, "NEWLINE");
		PARA = (String) PrivateAccessor.getField(dp, "PARA");
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Test
	public void testAddNameToDoc() throws Throwable {

		String doc = (String) PrivateAccessor.invoke(dp, "addNameToDoc",
				new Class[] { String.class, String.class }, new Object[] {
						new String("name"), new String("doc") });

		Assert.assertEquals(
				"Incorrect format string produced for non-null doc", BOLD
						+ "name" + UNBOLD + PARA + "doc" + PARA, doc);

		String emptyDoc = (String) PrivateAccessor.invoke(dp, "addNameToDoc",
				new Class[] { String.class, String.class }, new Object[] {
						new String("name"), null });

		Assert.assertEquals("Incorrect format string produced for null doc",
				BOLD + "name" + UNBOLD + PARA + PARA, emptyDoc);

	}
}
