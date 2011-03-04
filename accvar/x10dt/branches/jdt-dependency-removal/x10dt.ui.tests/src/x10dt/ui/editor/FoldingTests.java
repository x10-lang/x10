package x10dt.ui.editor;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Iterator;

import lpg.runtime.ILexStream;

import org.eclipse.imp.editor.UniversalEditor;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.text.source.projection.ProjectionAnnotation;
import org.eclipse.swtbot.eclipse.finder.waits.Conditions;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.ui.IEditorPart;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import polyglot.ast.MethodDecl;
import polyglot.ast.Node;
import polyglot.visit.NodeVisitor;
import x10dt.tests.services.swbot.utils.ProjectUtils;
import x10dt.ui.parser.ParseController;
import x10dt.ui.tests.utils.EditorMatcher;

@RunWith(SWTBotJunit4ClassRunner.class)
public class FoldingTests extends X10DTEditorTestBase {
    private static final String PROJECT_NAME = "TestOutline";

    private static final String CLASS_NAME = "Hello";

    private static final String SRC_FILE_NAME = CLASS_NAME + ".x10";

    @BeforeClass
    public static void beforeClass() throws Exception {
        X10DTEditorTestBase.BeforeClass();
        createJavaBackEndProject(PROJECT_NAME, false);
        topLevelBot.shells()[0].activate();
    }

    @After
    public void after() throws Exception {
        afterTest();
    }

    @AfterClass
    public static void afterClass() throws Exception {
        X10DTEditorTestBase.AfterClass();
    }

    @Test
    public void test1() throws Exception {
        ProjectUtils.createClass(topLevelBot, CLASS_NAME, PROJECT_NAME + "/src", true);
        topLevelBot.waitUntil(Conditions.waitForEditor(new EditorMatcher(SRC_FILE_NAME)));

        fSrcEditor = topLevelBot.activeEditor().toTextEditor();
        junit.framework.Assert.assertEquals(SRC_FILE_NAME, fSrcEditor.getTitle());

        final IEditorPart editorPart = fSrcEditor.getReference().getEditor(false);
        final UniversalEditor univEditor = (UniversalEditor) editorPart;

        univEditor.addModelListener(fUpdateListener);

        waitForParser();

        IAnnotationModel annModel = (IAnnotationModel) univEditor.getAdapter(IAnnotationModel.class);
        Node root= (Node) univEditor.getParseController().getCurrentAst();

        checkFoldable(findMethod("main", root), annModel, univEditor);
//      checkFoldable(findMethod("this", root), annModel, univEditor);
    }

    private void checkFoldable(Node node, IAnnotationModel annModel, UniversalEditor univEditor) {
        ParseController parseCtrlr= (ParseController) univEditor.getParseController();
        ILexStream lexStream= parseCtrlr.getLexStream();
        IDocument doc= univEditor.getDocumentProvider().getDocument(univEditor.getEditorInput());

        polyglot.util.Position pos= node.position();
        int start= pos.offset();
        int end= pos.endOffset();

        int startLine= lexStream.getLineNumberOfCharAt(start);
        int endLine= lexStream.getLineNumberOfCharAt(end);

        checkFoldable(startLine, endLine, annModel, doc);
    }

    private void checkFoldable(int startLine, int endLine, IAnnotationModel annModel, IDocument doc) {
        boolean found= false;
        for(Iterator<Annotation> iter= annModel.getAnnotationIterator(); iter.hasNext() && !found; ) {
            Annotation a= iter.next();
            Position p= annModel.getPosition(a);

            if (ProjectionAnnotation.TYPE.equals(a.getType())) {
                try {
                    int foldRegionStartLine= doc.getLineOfOffset(p.offset) + 1;
                    int foldRegionEndLine= doc.getLineOfOffset(p.offset + p.length - 1) + 1;

                    if (startLine == foldRegionStartLine && (endLine + 1) == foldRegionEndLine) {
                        found= true;
                    }
                } catch (BadLocationException e) {
                    e.printStackTrace();
                    fail("BadLocationException caught when querying position of folding annotation.");
                }
            }
        }
        assertTrue("Expected foldable region for lines " + startLine + ":" + endLine + " not found.", found);
    }

    private MethodDecl findMethod(final String methodName, Node astRoot) {
        final MethodDecl[] mds= new MethodDecl[1];

        astRoot.visit(new NodeVisitor() {
            @Override
            public NodeVisitor enter(Node n) {
                if (n instanceof MethodDecl) {
                    MethodDecl md= (MethodDecl) n;
                    if (md.name().toString().equals(methodName)) {
                        mds[0]= md;
                    }
                }
                return this;
            }
        });
        return mds[0];
    }
}
