package x10dt.ui.editor;

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
import org.eclipse.jface.text.source.AnnotationModel;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEclipseEditor;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import x10dt.tests.services.swbot.utils.FileUtils;
import x10dt.tests.services.swbot.utils.ProjectUtils;
import x10dt.ui.X10DTUIPlugin;

@SuppressWarnings({ "unused" })
@RunWith(Parameterized.class)
public class X10DocProviderTest_Parameterized {
  private static X10DocProvider dp;

  private static String BOLD;

  private static String UNBOLD;

  private static String NEWLINE;

  private static String PARA;

  private static IProject project;

  private static TestEditor editor;

  protected SWTBotEclipseEditor fSrcEditor;

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

  @Parameters
  public static Collection<Object[]> params() throws Exception {
    project = ProjectUtils.copyProjectIntoWorkspace(X10DTUIPlugin.getInstance().getBundle(), new Path("data/X10DocProvider"));

    IFolder folder = project.getFolder("src");

    final List<Object[]> list = new ArrayList<Object[]>();

    folder.accept(new IResourceVisitor() {
      public boolean visit(IResource resource) throws CoreException {
        if (resource instanceof IFile) {
          if (!resource.toString().contains(".svn/")) {
            list.add(new Object[] { resource });

          }

          return false;
        }

        return true;

      }
    });

    return list;
  }

  @Test
  public void testGetDocumentation() throws Exception {

    File actual = new File(project.getFile(new Path("actual").append(file.getProjectRelativePath().removeFirstSegments(1))
                                                             .removeFileExtension().addFileExtension("txt")).getLocation()
                                  .toPortableString());

    actual.getParentFile().mkdirs();

    FileChannel fchan = new FileOutputStream(actual).getChannel();
    BufferedWriter bf = new BufferedWriter(Channels.newWriter(fchan, "UTF-8"));

    try {
      for (int offset = 0; offset < editor.document.getLength(); offset++) {

        Point loc = editor.getLocation(offset);

        Shell shell = new Shell();

        SourceViewer viewer = new SourceViewer(shell, null, 0);
        IAnnotationModel model = new AnnotationModel();
        model.connect(editor.document);
        viewer.setDocument(editor.document, model);
        String doc = hh.getHoverHelpAt(editor.parseController, viewer, offset);

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
    File expected = new File(project.getFile(new Path("expected").append(file.getProjectRelativePath().removeFirstSegments(1))
                                                                 .removeFileExtension().addFileExtension("txt")).getLocation()
                                    .toPortableString());

    Assert.assertTrue("The data file containing the expected results for " + file.getLocation() +
                      " does not exist.  If you just added this file to the test suite be sure to verify the contents of " +
                      actual.getPath() + " and copy it into the expected folder in the test suite.", expected.exists());
    int result = FileUtils.compareLinesOf(actual, expected);
    Assert.assertTrue("Actual differs from expected at line " + result, result == -1);

  }
}
