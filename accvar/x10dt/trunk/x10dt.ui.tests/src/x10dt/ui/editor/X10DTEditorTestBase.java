/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/

package x10dt.ui.editor;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.imp.editor.UniversalEditor;
import org.eclipse.imp.parser.IModelListener;
import org.eclipse.imp.parser.IParseController;
import org.eclipse.imp.utils.StreamUtils;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEclipseEditor;
import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;
import org.eclipse.ui.IEditorPart;
import org.osgi.framework.Bundle;

import x10dt.ui.tests.X10DTTestBase;

/**
 * @author rfuhrer
 */
public class X10DTEditorTestBase extends X10DTTestBase {
    protected class UpdateListener implements IModelListener {
        private boolean fUpdated = false;

        public void reset() {
            fUpdated = false;
        }

        public boolean updateFinished() {
            return fUpdated;
        }

        public AnalysisRequired getAnalysisRequired() {
            return AnalysisRequired.NONE; // this posts the error annotations!
        }

        public void update(IParseController parseController, IProgressMonitor monitor) {
            fUpdated = true;
        }
    }

    /**
       * The bot for the editor used to test the quick outline view.
       */
    protected SWTBotEclipseEditor fSrcEditor;
    protected UpdateListener fUpdateListener = new UpdateListener();

    protected void waitForParser() throws Exception {
        topLevelBot.waitUntil(new DefaultCondition() {
    
          public boolean test() throws Exception {
            return fUpdateListener.updateFinished();
          }
    
          public String getFailureMessage() {
            return "Some Failure Message";
          }
    
        });
    }

    protected void getTestSource(final SWTBotEclipseEditor srcEditor, final String resPath, final String className) {
        final Bundle bundle = Platform.getBundle("x10dt.ui.tests"); //$NON-NLS-1$
        final URL resURL = bundle.getEntry(resPath);
        junit.framework.Assert.assertNotNull("Unable to find test source: " + resPath, resURL); //$NON-NLS-1$
        try {
          final InputStream resStream = FileLocator.openStream(bundle, new Path(resURL.getPath()), false);
          final String contents = StreamUtils.readStreamContents(resStream);
    
          final IEditorPart editorPart = srcEditor.getReference().getEditor(false);
          final UniversalEditor univEditor = (UniversalEditor) editorPart;
          univEditor.addModelListener(fUpdateListener);
    
          srcEditor.setText(contents);
        } catch (final IOException e) {
          junit.framework.Assert.fail(e.getMessage());
        }
      }
}
