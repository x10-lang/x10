/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/

package x10dt.ui.tests;

import java.util.regex.Pattern;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEclipseEditor;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotMenu;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import x10dt.tests.services.swbot.constants.LaunchConstants;
import x10dt.tests.services.swbot.constants.ViewConstants;
import x10dt.tests.services.swbot.matcher.EditorPathMatcher;
import x10dt.tests.services.swbot.utils.SWTBotUtils;

/**
 * This test checks that the correct "Run As..." context menu choices
 * appear, dependent upon which X10 project nature the element's containing
 * project has (either the Java back-end or the C++ back-end nature).
 * @author rfuhrer
 */
@RunWith(SWTBotJunit4ClassRunner.class)
public class RTC753Test extends X10DTTestBase {
    private static final String JAVA_PROJECT1_NAME = "HelloJava1"; //$NON-NLS-1$

    private static final String CPP_PROJECT1_NAME = "HelloCpp1"; //$NON-NLS-1$

    private static final String JAVA_PROJECT2_NAME = "HelloJava2"; //$NON-NLS-1$

    private static final String CPP_PROJECT2_NAME = "HelloCpp2"; //$NON-NLS-1$

    private static final String CLASS_NAME = "Hello"; //$NON-NLS-1$

    private static final String SRC_FOLDER_NAME= "src"; //$NON-NLS-1$

    private static final String SRC_FOLDER_PREFIX= SRC_FOLDER_NAME + "/"; //$NON-NLS-1$

    private static final String X10_FILE_EXTENSION= ".x10"; //$NON-NLS-1$

    @BeforeClass
    public static void beforeClass() throws Exception {
      BeforeClass();
    }

    @After
    public void after() throws Exception {
      afterTest();
    }

    @AfterClass
    public static void afterClass() throws Exception {
      AfterClass();
    }
    
    @Test
    public void basicLaunchTest() throws Exception {
      createJavaBackEndProject(JAVA_PROJECT1_NAME, true);
      createCPPBackEndProject(CPP_PROJECT1_NAME, true);
      createJavaBackEndProject(JAVA_PROJECT2_NAME, true);
      createCPPBackEndProject(CPP_PROJECT2_NAME, true);

      SWTBot pkgExplorerBot= topLevelBot.viewByTitle(ViewConstants.PACKAGE_EXPLORER_VIEW_NAME).bot();

      checkForProjJavaRunItem(JAVA_PROJECT1_NAME, pkgExplorerBot);
      checkForProjCppRunItem(CPP_PROJECT1_NAME, pkgExplorerBot);
      checkForProjJavaRunItem(JAVA_PROJECT2_NAME, pkgExplorerBot);
      checkForProjCppRunItem(CPP_PROJECT2_NAME, pkgExplorerBot);

      checkForSrcJavaRunItem(JAVA_PROJECT1_NAME, pkgExplorerBot);
      checkForSrcCppRunItem(CPP_PROJECT1_NAME, pkgExplorerBot);
      checkForSrcJavaRunItem(JAVA_PROJECT2_NAME, pkgExplorerBot);
      checkForSrcCppRunItem(CPP_PROJECT2_NAME, pkgExplorerBot);

      checkForEditorRunItem(JAVA_PROJECT1_NAME, SRC_FOLDER_PREFIX + CLASS_NAME + X10_FILE_EXTENSION, LaunchConstants.RUN_AS_X10_APPLICATION_JAVA_BACK_END_PAT);
      checkForEditorRunItem(CPP_PROJECT1_NAME, SRC_FOLDER_PREFIX + CLASS_NAME + X10_FILE_EXTENSION, LaunchConstants.RUN_AS_X10_APPLICATION_CPP_BACK_END_PAT);
      checkForEditorRunItem(JAVA_PROJECT2_NAME, SRC_FOLDER_PREFIX + CLASS_NAME + X10_FILE_EXTENSION, LaunchConstants.RUN_AS_X10_APPLICATION_JAVA_BACK_END_PAT);
      checkForEditorRunItem(CPP_PROJECT2_NAME, SRC_FOLDER_PREFIX + CLASS_NAME + X10_FILE_EXTENSION, LaunchConstants.RUN_AS_X10_APPLICATION_CPP_BACK_END_PAT);
    }

    private void checkForEditorRunItem(String projectName, String srcFilePath, Pattern runItemPat) {
        // Find the editor for the "Hello.x10" source file in the given project.
        // Get the context menu, look for "Run As", and see whether the right kind
        // of app run is offered.
        final IPath srcPath = new Path("/" + projectName + "/" + srcFilePath); //$NON-NLS-1$ //$NON-NLS-2$
        final SWTBotEclipseEditor editor = topLevelBot.editor(new EditorPathMatcher(srcPath)).toTextEditor();
        editor.show();
        editor.setFocus();
        SWTBotUtils.findSubMenu(editor.contextMenu(LaunchConstants.RUN_AS_MENU), runItemPat);
    }

    private void checkForProjJavaRunItem(String projName, SWTBot pkgExplorerBot) {
        checkForProjRunItem(LaunchConstants.RUN_AS_X10_APPLICATION_JAVA_BACK_END_PAT, projName, pkgExplorerBot);
    }

    private void checkForProjCppRunItem(String projName, SWTBot pkgExplorerBot) {
        checkForProjRunItem(LaunchConstants.RUN_AS_X10_APPLICATION_CPP_BACK_END_PAT, projName, pkgExplorerBot);
    }

    private void checkForProjRunItem(Pattern runItemPat, String projName, SWTBot pkgExplorerBot) {
        SWTBotTree pkgTree= pkgExplorerBot.tree();

        pkgTree.setFocus();

        SWTBotTreeItem projItem= pkgExplorerBot.tree().getTreeItem(projName);

        @SuppressWarnings("unused")
        SWTBotMenu runItem= SWTBotUtils.findSubMenu(projItem.contextMenu(LaunchConstants.RUN_AS_MENU), runItemPat);

//      System.out.println("Got run menu item: " + runItem.getText());
//      runItem.click();
    }

    private void checkForSrcJavaRunItem(String projName, SWTBot pkgExplorerBot) {
        checkForSrcRunItem(LaunchConstants.RUN_AS_X10_APPLICATION_JAVA_BACK_END_PAT, projName, pkgExplorerBot);
    }

    private void checkForSrcCppRunItem(String projName, SWTBot pkgExplorerBot) {
        checkForSrcRunItem(LaunchConstants.RUN_AS_X10_APPLICATION_CPP_BACK_END_PAT, projName, pkgExplorerBot);
    }

    private void checkForSrcRunItem(Pattern runItemPat, String projName, SWTBot pkgExplorerBot) {
        SWTBotTree pkgTree= pkgExplorerBot.tree();

        pkgTree.setFocus();

        SWTBotTreeItem classItem= pkgTree.expandNode(projName, SRC_FOLDER_NAME, CLASS_NAME + X10_FILE_EXTENSION);

        @SuppressWarnings("unused")
        SWTBotMenu runItem= SWTBotUtils.findSubMenu(classItem.contextMenu(LaunchConstants.RUN_AS_MENU), runItemPat);

//      System.out.println("Got run menu item: " + runItem.getText());
//      runItem.click();
    }
}
