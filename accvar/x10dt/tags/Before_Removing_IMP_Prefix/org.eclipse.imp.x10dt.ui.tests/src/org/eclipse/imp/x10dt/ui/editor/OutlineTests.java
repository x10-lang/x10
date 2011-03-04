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

package org.eclipse.imp.x10dt.ui.editor;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.imp.editor.UniversalEditor;
import org.eclipse.imp.utils.StreamUtils;
import org.eclipse.imp.x10dt.tests.services.swbot.utils.ProjectUtils;
import org.eclipse.imp.x10dt.tests.services.swbot.utils.SWTBotUtils;
import org.eclipse.imp.x10dt.ui.parser.ParseController;
import org.eclipse.imp.x10dt.ui.tests.X10DTTestBase;
import org.eclipse.imp.x10dt.ui.tests.utils.EditorMatcher;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.waits.Conditions;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEclipseEditor;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.utils.SWTBotPreferences;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.eclipse.ui.IEditorPart;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.osgi.framework.Bundle;

import polyglot.ast.ClassDecl;
import polyglot.ast.FieldDecl;
import polyglot.ast.MethodDecl;
import polyglot.ast.Node;
import polyglot.ast.SourceFile;
import polyglot.visit.NodeVisitor;

/**
 * @author rfuhrer@watson.ibm.com
 */
@RunWith(SWTBotJunit4ClassRunner.class)
public class OutlineTests extends X10DTTestBase {
    private static final String PROJECT_NAME= "OutlineProject"; //$NON-NLS-1$

    private static final String CLASS_NAME= "MyClass"; //$NON-NLS-1$

    private static final String CLASS_SRCFILE_NAME= CLASS_NAME + ".x10"; //$NON-NLS-1$

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

    @Test
    public void test1() {
        runTest("data/Hello.x10"); //$NON-NLS-1$
    }

    @Test
    public void testKMeansSPMD() {
        runTest("data/KMeansSPMD.x10"); //$NON-NLS-1$
    }

    @Test
    public void test2() {
        runTest("data/FRASimpleDist.x10"); //$NON-NLS-1$
    }

    @Test
    public void test3() {
        runTest("data/FSSimpleDist.x10"); //$NON-NLS-1$
    }

    @Test
    public void test4() {
        runTest("data/GCSpheres.x10"); //$NON-NLS-1$
    }

    @Test
    public void test5() {
        runTest("data/Histogram.x10"); //$NON-NLS-1$
    }

    @Test
    public void test6() {
        runTest("data/MontyPi.x10"); //$NON-NLS-1$
    }

    @Test
    public void test7() {
        runTest("data/NQueensDist.x10"); //$NON-NLS-1$
    }

    @Test
    public void test8() {
        runTest("data/NQueensPar.x10"); //$NON-NLS-1$
    }

    @Test
    public void test9() {
        runTest("data/StructSpheres.x10"); //$NON-NLS-1$
    }

    private void runTest(String srcPath) {
        getTestSource(fSrcEditor, srcPath);
        verifyOutline(fSrcEditor);
    }

    private void getTestSource(final SWTBotEclipseEditor srcEditor, final String resPath) {
        final Bundle bundle= Platform.getBundle("org.eclipse.imp.x10dt.ui.tests"); //$NON-NLS-1$
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

    /**
     * Verifies the contents of the "Outline" view against the X10 source in the given editor
     */
    private void verifyOutline(final SWTBotEclipseEditor editor) {
        SWTBotView outlineView= topLevelBot.viewByTitle("Outline"); //$NON-NLS-1$

        final IEditorPart editorPart= editor.getReference().getEditor(false);
        final UniversalEditor univEditor= (UniversalEditor) editorPart;
        final ParseController pc= (ParseController) univEditor.getParseController();
        SourceFile srcFile= (SourceFile) pc.getCurrentAst();

        verifyOutlineItems(outlineView, srcFile, ClassDecl.class);
        verifyOutlineItems(outlineView, srcFile, MethodDecl.class);
        verifyOutlineItems(outlineView, srcFile, FieldDecl.class);
    }

    /**
     * Cheap outline contents verification: check that all instances of the given Node sub-class
     * appear in the outline tree (excluding synthetic/compiler-generated instances).
     */
    private void verifyOutlineItems(SWTBotView outlineView, SourceFile srcFile, final Class<? extends Node> clazz) {
        final Set<Node> items= new HashSet<Node>();
        srcFile.visit(new NodeVisitor() {
            @Override
            public NodeVisitor enter(Node n) {
                if (clazz.isInstance(n) && !n.position().isCompilerGenerated()) {
                    items.add(n);
                }
                return this;
            }
        });
        SWTBotTree outlineTree= outlineView.bot().tree();
        SWTBotTreeItem[] treeItems= outlineTree.getAllItems();

        X10LabelProvider labelProvider= new X10LabelProvider();

        for(Node n: items) {
            String label= labelProvider.getText(n);
            junit.framework.Assert.assertTrue("Missing outline item: " + label, find(label, treeItems)); //$NON-NLS-1$
        }
    }

    // TODO "labels" aren't specific enough - there might be more than one item in a given outline view with the same label
    private boolean find(String label, SWTBotTreeItem[] treeItems) {
        for(SWTBotTreeItem item: treeItems) {
            if (item.getText().equals(label)) {
                return true;
            }
            item.expand();
            SWTBotTreeItem[] children= item.getItems();
            if (children.length > 0 && find(label, children)) {
                return true;
            }
        }
        return false;
    }
}
