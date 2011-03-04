package org.eclipse.imp.x10dt.ui.editor;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.imp.editor.HoverHelper;
import org.eclipse.imp.x10dt.tests.services.swbot.utils.FileUtils;
import org.eclipse.imp.x10dt.tests.services.swbot.utils.ProjectUtils;
import org.eclipse.imp.x10dt.ui.X10DTUIPlugin;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Shell;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@SuppressWarnings( { "unused" })
@RunWith(Parameterized.class)
public class X10DocProviderTest_Parameterized {
	private static X10DocProvider dp;
	private static String BOLD;
	private static String UNBOLD;
	private static String NEWLINE;
	private static String PARA;

	private static IProject project;
	private static TestEditor editor;

	private IFile file;
	private HoverHelper hh;

	public X10DocProviderTest_Parameterized(IFile file) throws Exception {
		super();

		this.file = file;
		editor = new TestEditor(file);
		editor.getAst();

		hh = new HoverHelper(editor.language);
	}

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {

	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	// private Object getTarget(TestEditor editor, int line, int col)
	// throws BadLocationException {
	// return getTarget(editor, editor.getOffset(line - 1, col));
	// }
	//
	// private Object getTarget(TestEditor editor, int offset)
	// throws BadLocationException {
	// IReferenceResolver refResolver = ServiceFactory.getInstance()
	// .getReferenceResolver(editor.language);
	//
	// ISourcePositionLocator nodeLocator = editor.parseController
	// .getSourcePositionLocator();
	//
	// Object root = editor.getAst();
	// Object selNode = nodeLocator.findNode(root, offset);
	// Object target = (refResolver != null) ? refResolver.getLinkTarget(
	// selNode, editor.parseController) : selNode;
	// if (target == null)
	// target = selNode;
	//
	// return target;
	// }

	@Parameters
	public static Collection<Object[]> params() throws Exception {
		project = ProjectUtils.copyProjectIntoWorkspace(X10DTUIPlugin
				.getInstance().getBundle(), new Path("data/X10DocProvider"));

		IFolder folder = project.getFolder("src");

		// ProjectUtils.copyFolderIntoContainer(X10DTUIPlugin.getInstance().getBundle(),
		// folder, new Path("data/X10DocProvider"));

		final List<Object[]> list = new ArrayList<Object[]>();

		folder.accept(new IResourceVisitor() {
			public boolean visit(IResource resource) throws CoreException {
				if (resource instanceof IFile) {
					list.add(new Object[] { resource });
					return false;
				}
				return true;
			}
		});

		return list;
	}

	@Test
	public void testGetDocumentation() throws Exception {
		File actual = new File(project.getFile(
				new Path("actual").append(
						file.getProjectRelativePath().removeFirstSegments(1))
						.removeFileExtension().addFileExtension("txt"))
				.getLocation().toPortableString());

		actual.getParentFile().mkdirs();

		FileChannel fchan = new FileOutputStream(actual).getChannel();
		BufferedWriter bf = new BufferedWriter(Channels.newWriter(fchan,
				"UTF-8"));
		try {
			for (int offset = 0; offset < editor.document.getLength(); offset++) {
				Point loc = editor.getLocation(offset);

				Shell shell = new Shell();
				SourceViewer viewer = new SourceViewer(shell, null, 0);
				viewer.setDocument(editor.document);
				String doc = hh.getHoverHelpAt(editor.parseController, viewer,
						offset);

				if (doc != null && doc.length() > 0) {
					loc = editor.getLocation(offset);
					bf.write("" + loc.x + "," + loc.y + ": ");
					bf.write(doc);
					bf.newLine();
				}
			}

		} finally {
			bf.close();
			fchan.close();
		}

		File expected = new File(project.getFile(
				new Path("expected").append(
						file.getProjectRelativePath().removeFirstSegments(1))
						.removeFileExtension().addFileExtension("txt"))
				.getLocation().toPortableString());

		// expected won't exist on first run of test (e.g. when adding a new
		// x10
		// file to
		// the suite)
		Assert
				.assertTrue(
						"The data file containing the expected results for "
								+ file.getLocation()
								+ " does not exist.  If you just added this file to the test suite be sure to verify the contents of "
								+ actual.getPath()
								+ " and copy it into the expected folder in the test suite.",
						expected.exists());

		int result = FileUtils.compareLinesOf(actual, expected);
		Assert.assertTrue("Actual differs from expected at line " + result,
				result == -1);

	}
}
