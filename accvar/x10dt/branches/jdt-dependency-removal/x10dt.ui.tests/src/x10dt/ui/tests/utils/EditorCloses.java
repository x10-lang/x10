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

package x10dt.ui.tests.utils;

import java.util.List;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEditor;
import org.eclipse.swtbot.swt.finder.finders.UIThreadRunnable;
import org.eclipse.swtbot.swt.finder.results.BoolResult;
import org.eclipse.swtbot.swt.finder.utils.internal.Assert;
import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;

public class EditorCloses extends DefaultCondition {
  private final SWTBotEditor fEditor;

  /**
   * Creates a condition that evaluates to false until the editor is disposed.
   * 
   * @param editor
   *          the editor to be monitored.
   */
  public EditorCloses(SWTBotEditor editor) {
    Assert.isNotNull(editor, "The editor was null"); //$NON-NLS-1$
    this.fEditor = editor;
  }

  public String getFailureMessage() {
    return "The editor " + fEditor + " did not close."; //$NON-NLS-1$ //$NON-NLS-2$
  }

  public boolean test() throws Exception {
    return UIThreadRunnable.syncExec(new BoolResult() {
      public Boolean run() {
        // The following doesn't have the desired effect due to a "bug" in SWTBot -
        // getWidget() calls show(), so getWidget().isDisposed() will never return
        // true. See https://bugs.eclipse.org/bugs/show_bug.cgi?id=265543
        // return fEditor.getWidget() == null || fEditor.getWidget().isDisposed();
        // Recommendation is instead to use bot.editors() and check for an editor open on the same file...
        final List<? extends SWTBotEditor> editors = ((SWTWorkbenchBot) fEditor.bot()).editors();
        for (SWTBotEditor ed : editors) {
          if (ed.getTitle().equals(fEditor.getTitle())) {
            return false;
          }
        }
        return true;
      }
    });
  }
}