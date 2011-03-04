/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Robert Fuhrer (rfuhrer@watson.ibm.com) - initial API and implementation
 *******************************************************************************/

package x10dt.ui.editor;

import java.util.List;

import junit.framework.Assert;
import lpg.runtime.IPrsStream;
import lpg.runtime.IToken;

import org.eclipse.imp.editor.UniversalEditor;
import org.eclipse.imp.services.IReferenceResolver;
import org.eclipse.swtbot.eclipse.finder.waits.Conditions;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.ui.IEditorPart;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import polyglot.ast.ClassDecl;
import polyglot.ast.FieldDecl;
import polyglot.ast.Id;
import polyglot.ast.Import;
import polyglot.ast.MethodDecl;
import polyglot.ast.Node;
import polyglot.ast.PackageNode;
import polyglot.ast.VarDecl;
import x10.ast.PropertyDecl;
import x10.ast.TypeDecl;
import x10.parser.X10Parsersym;
import x10dt.tests.services.swbot.utils.ProjectUtils;
import x10dt.ui.parser.ParseController;
import x10dt.ui.parser.PolyglotNodeLocator;
import x10dt.ui.tests.utils.EditorMatcher;

@RunWith(SWTBotJunit4ClassRunner.class)
public class ResolverTests extends X10DTEditorTestBase {
    private static final String PROJECT_NAME = "TestResolver";

    @BeforeClass
    public static void beforeClass() throws Exception {
        X10DTEditorTestBase.BeforeClass();
        createJavaBackEndProject(PROJECT_NAME, false);
        topLevelBot.shells()[0].activate();
    }

    @AfterClass
    public static void afterClass() throws Exception {
        X10DTEditorTestBase.AfterClass();
    }

    @After
    public void after() throws Exception {
        afterTest();
    }

    @Test
    public void testGCSpheres() throws Exception {
        createSourceFileCheckResolution("GCSpheres");
    }

    @Test
    public void testHistogram() throws Exception {
        createSourceFileCheckResolution("Histogram");
    }

    @Test
    public void testKMeans() throws Exception {
        createSourceFileCheckResolution("KMeans");
    }

    @Test
    public void testMontyPi() throws Exception {
        createSourceFileCheckResolution("MontyPi");
    }

    @Test
    public void testNQueensDist() throws Exception {
        createSourceFileCheckResolution("NQueensDist");
    }

    private void createSourceFileCheckResolution(String className) throws Exception {
        final String srcFileName = className + ".x10";

        ProjectUtils.createClass(topLevelBot, className, PROJECT_NAME + "/src", false);
        topLevelBot.waitUntil(Conditions.waitForEditor(new EditorMatcher(srcFileName)));

        fSrcEditor = topLevelBot.activeEditor().toTextEditor();
        junit.framework.Assert.assertEquals(srcFileName, fSrcEditor.getTitle());

        final IEditorPart editorPart = fSrcEditor.getReference().getEditor(false);
        final UniversalEditor univEditor = (UniversalEditor) editorPart;
        final String resPath = "data/resolver/" + srcFileName;

        getTestSource(fSrcEditor, resPath, className);
        waitForParser();

        ParseController pc= (ParseController) univEditor.getParseController();
        Node root= (Node) pc.getCurrentAst();
        IPrsStream ps= pc.getParseStream();
        List<IToken> tokens = ps.getTokens();
        PolyglotNodeLocator locator = (PolyglotNodeLocator) pc.getSourcePositionLocator();
        IReferenceResolver resolver= univEditor.getLanguageServiceManager().getResolver();

        for(IToken token: tokens) {
            if (token.getKind() == X10Parsersym.TK_IDENTIFIER) {
                System.out.println("Found identifier " + token.toString());

                Node idNode= (Node) locator.findNode(root, token.getStartOffset(), token.getEndOffset());
                Node contextNode= (idNode instanceof Id) ? (Node) locator.getParentNodeOf(idNode, root) : idNode;

                System.out.println("  Node type: " + idNode.getClass().getCanonicalName());
                System.out.println("  Context node type: " + contextNode.getClass().getCanonicalName());

                if (idNode != null && !isDeclaration(contextNode)) {
                    System.out.println("Checking identifier " + token.toString());

                    Object target= resolver.getLinkTarget(idNode, pc);

                    Assert.assertNotNull("Null resolution target for identifier '" + token.toString() + "' [line " + token.getLine() + ", column " + token.getColumn() + "] in context node of type " + contextNode.getClass().getCanonicalName(), target);
                }
            }
        }
        fSrcEditor.close();
    }

    private boolean isDeclaration(Node node) {
        return node instanceof ClassDecl || node instanceof TypeDecl || node instanceof MethodDecl || node instanceof VarDecl ||
               node instanceof FieldDecl || node instanceof PackageNode || node instanceof Import || node instanceof PropertyDecl;
    }
}
