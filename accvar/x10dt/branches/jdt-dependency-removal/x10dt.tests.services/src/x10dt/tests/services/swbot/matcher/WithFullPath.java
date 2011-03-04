/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.tests.services.swbot.matcher;

import org.eclipse.core.resources.IResource;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

/**
 * Tries to match a resource full path to a string provided.
 * 
 * @author egeay
 */
public final class WithFullPath extends TypeSafeMatcher<IResource> implements Matcher<IResource> {
  
  /**
   * Constructs the matcher with the path to use as reference.
   * 
   * @param path The path to use for comparison with all the resources found.
   */
  public WithFullPath(final String path) {
    this.fPath = path;
  }

  // --- SelfDescribing's interface methods implementation
  
  public void describeTo(final Description description) {
    description.appendText(" with resource path '").appendText(this.fPath).appendText("'"); //$NON-NLS-1$ //$NON-NLS-2$
  }
  
  // --- Abstract methods implementation

  public boolean matchesSafely(final IResource item) {
    return this.fPath.equals(item.getFullPath().toString());
  }
  
  // --- Fields
  
  private final String fPath;

}
