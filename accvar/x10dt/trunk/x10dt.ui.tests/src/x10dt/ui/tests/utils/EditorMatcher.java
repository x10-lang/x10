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

import org.eclipse.ui.IEditorReference;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

public class EditorMatcher extends BaseMatcher<IEditorReference> {
  private final String fEditorName;

  public EditorMatcher(String editorName) {
    fEditorName = editorName;
  }

  public void describeTo(Description description) {
    description.appendText("EditorMatcher(name == " + fEditorName + ")");
  }

  public boolean matches(Object item) {
    if (item instanceof IEditorReference) {
      IEditorReference er = (IEditorReference) item;
      return er.getName().equals(fEditorName);
    }
    return false;
  }
}