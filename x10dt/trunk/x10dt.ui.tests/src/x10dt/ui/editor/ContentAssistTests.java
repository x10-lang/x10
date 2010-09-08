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

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.imp.editor.UniversalEditor;
import org.eclipse.imp.utils.StreamUtils;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.waits.Conditions;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEclipseEditor;
import org.eclipse.swtbot.swt.finder.utils.SWTBotPreferences;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.texteditor.AbstractTextEditor;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.osgi.framework.Bundle;

import polyglot.ast.ClassMember;
import polyglot.ast.Node;
import polyglot.ast.SourceFile;
import polyglot.ast.Stmt;
import polyglot.util.Position;
import polyglot.visit.NodeVisitor;
import x10dt.tests.services.swbot.utils.ProjectUtils;
import x10dt.tests.services.swbot.utils.SWTBotUtils;
import x10dt.ui.parser.ParseController;
import x10dt.ui.tests.X10DTTestBase;
import x10dt.ui.tests.utils.EditorMatcher;

/**
 * @author rfuhrer@watson.ibm.com
 *
 */
public class ContentAssistTests extends X10DTTestBase {
    private static final String PROJECT_NAME= "ContentAssistProject"; //$NON-NLS-1$

    private static final String CLASS_NAME= "MyClass"; //$NON-NLS-1$

    private static final String CLASS_SRCFILE_NAME= CLASS_NAME + ".x10"; //$NON-NLS-1$

    private static final String[] STATEMENT_CONTEXT_PROPOSALS= {
            "array new - ",
            "array access - ",
            "async - ",
            "at - ",
            "ateach - ",
            "atomic - ",
            "clocked - ",
            "constraint - ",
            "finish - ",
            "foreach - ",
            "function - ",
            "instanceof - ",
            "printing - ",
            "type - ",
            "val - ",
            "var - ",
            "when - "
    };

    private static final String[] MEMBER_CONTEXT_PROPOSALS= {
        "val - ",
        "var - ",
        "property - ",
        "const - ",
        "def - ",
        "this - ",
        "class - ",
        "struct - ",
        "dependent type -"
    };

    /**
     * The bot for the editor used to exercise the outline view
     */
    protected static SWTBotEclipseEditor fSrcEditor;

    @BeforeClass
    public static void beforeClass() throws Exception {
        SWTBotPreferences.KEYBOARD_STRATEGY= "org.eclipse.swtbot.swt.finder.keyboard.SWTKeyboardStrategy"; //$NON-NLS-1$
        topLevelBot= new SWTWorkbenchBot();
        SWTBotPreferences.TIMEOUT= 15000; // Long timeout needed for first project creation
        SWTBotUtils.closeWelcomeViewIfNeeded(topLevelBot);

        createJavaBackEndProject(PROJECT_NAME, false);
        topLevelBot.shells()[0].activate(); // HACK - make sure the main window's shell is active, in case we ran after other tests
        ProjectUtils.createClass(topLevelBot, CLASS_NAME);

        topLevelBot.waitUntil(Conditions.waitForEditor(new EditorMatcher(CLASS_SRCFILE_NAME)));

        fSrcEditor= topLevelBot.editorByTitle(CLASS_SRCFILE_NAME).toTextEditor();
    }

    @AfterClass
    public static void afterClass() throws Exception {
        SWTBotUtils.saveAllDirtyEditors(topLevelBot);
    }

    private static class LinePos {
        int line, col;
        public LinePos(int line, int col) {
            this.line= line;
            this.col= col;
        }
    }

    @Test
    public void test1() {
        getTestSource(fSrcEditor, "data/Hello.x10"); //$NON-NLS-1$

        runStatementContextTest();
    }

    private void runStatementContextTest() {
        IDocument doc= getEditorDocument(fSrcEditor);
        SourceFile srcFileRoot= getASTRoot(fSrcEditor);

        List<LinePos> allContextPositions= findAllStatementContextLineEnds(srcFileRoot, doc);

        fSrcEditor.setFocus();

        runContextContextAssistTest(allContextPositions, STATEMENT_CONTEXT_PROPOSALS, "statement");
    }

    @Test
    public void test2() {
        getTestSource(fSrcEditor, "data/Hello.x10"); //$NON-NLS-1$

        runMemberContextTest();
    }

    private void runMemberContextTest() {
        IDocument doc= getEditorDocument(fSrcEditor);
        SourceFile srcFileRoot= getASTRoot(fSrcEditor);

        List<LinePos> allContextPositions= findAllMemberContextLineEnds(srcFileRoot, doc);

        fSrcEditor.setFocus();

        runContextContextAssistTest(allContextPositions, MEMBER_CONTEXT_PROPOSALS, "member");
    }

    private void runContextContextAssistTest(List<LinePos> allContextPositions, String[] expectedProposals, String contextName) {
        for(LinePos lp: allContextPositions) {
            System.out.println("Context position at: <line: " + lp.line + ", col: " + lp.col + ">");
        }

        for(LinePos lp: allContextPositions) {
            fSrcEditor.selectRange(lp.line, lp.col, 0); // go to that position
            fSrcEditor.insertText("\n");

            List<String> proposals= fSrcEditor.getAutoCompleteProposals("");

            junit.framework.Assert.assertNotNull("The proposals result is null", proposals);
            junit.framework.Assert.assertTrue("There are no proposals", proposals.size() > 0);

            for(String proposal: expectedProposals) {
                junit.framework.Assert.assertTrue("Expected " + contextName + " proposal is missing: " + proposal + " at location " + toString(lp), containsMatchingProposal(proposals, proposal));
            }
        }
    }

    private String toString(LinePos lp) {
        return "<line: " + lp.line + ", col: " + lp.col + ">";
    }

    private List<LinePos> findAllMemberContextLineEnds(SourceFile srcFileRoot, final IDocument doc) {
        final List<LinePos> result= new LinkedList<LinePos>();

        srcFileRoot.visit(new NodeVisitor() {
            @Override
            public NodeVisitor enter(Node n) {
                if (n instanceof ClassMember && !n.position().isCompilerGenerated()) {
                    try {
                        Position pos= n.position();
                        int endOffset= pos.endOffset();

                        // TODO fix front-end so that ClassMember extent covers ';' as well
                        if (doc.get(endOffset, 1) == ";") {
                            endOffset++;
                        }

                        int line= doc.getLineOfOffset(endOffset);
                        int col= endOffset - doc.getLineOffset(line) + 1;
                        LinePos lp= new LinePos(line, col);

                        result.add(lp);
                    } catch (BadLocationException e) {
                    }
                }
                return this;
            }
        });
        return result;
    }

    private List<LinePos> findAllStatementContextLineEnds(SourceFile srcFileRoot, final IDocument doc) {
        final List<LinePos> result= new LinkedList<LinePos>();
        srcFileRoot.visit(new NodeVisitor() {
            @Override
            public NodeVisitor enter(Node n) {
                if (n instanceof Stmt && !n.position().isCompilerGenerated()) {
                    try {
                        Position pos= n.position();
                        int endOffset= pos.endOffset();

                        // TODO fix front-end so that Stmt extent covers ';' as well
                        if (doc.get(endOffset, 1) == ";") {
                            endOffset++;
                        }

                        int line= doc.getLineOfOffset(endOffset);
                        int col= endOffset - doc.getLineOffset(line) + 1;
                        LinePos lp= new LinePos(line, col);

                        result.add(lp);
                    } catch (BadLocationException e) {
                    }
                }
                return this;
            }
        });
        return result;
    }

    private static SourceFile getASTRoot(SWTBotEclipseEditor fSrcEditor2) {
        final IEditorPart editorPart= fSrcEditor2.getReference().getEditor(false);
        final UniversalEditor univEditor= (UniversalEditor) editorPart;
        final ParseController pc= (ParseController) univEditor.getParseController();

        return (SourceFile) pc.getCurrentAst();
    }

    private static IDocument getEditorDocument(SWTBotEclipseEditor fSrcEditor2) {
        final IEditorPart editorPart= fSrcEditor2.getReference().getEditor(false);
        final IEditorInput input= editorPart.getEditorInput();
        final IDocument doc= ((AbstractTextEditor) editorPart).getDocumentProvider().getDocument(input);

        return doc;
    }

    private boolean containsMatchingProposal(List<String> actualProposals, String proposalPrefix) {
        for(String actualProp: actualProposals) {
            if (actualProp.startsWith(proposalPrefix)) {
                return true;
            }
        }
        return false;
    }

    private void getTestSource(final SWTBotEclipseEditor srcEditor, final String resPath) {
        final Bundle bundle= Platform.getBundle("x10dt.ui.tests"); //$NON-NLS-1$
        final URL resURL= bundle.getEntry(resPath);
        junit.framework.Assert.assertNotNull("Unable to find test source: " + resPath, resURL); //$NON-NLS-1$
        try {
            final InputStream resStream= FileLocator.openStream(bundle, new Path(resURL.getPath()), false);
            final String contents= StreamUtils.readStreamContents(resStream);
            
            srcEditor.setText(contents);
            topLevelBot.sleep(7000); // give parser a chance to catch up...
        } catch (final IOException e) {
            junit.framework.Assert.fail(e.getMessage());
        }
    }
}
