/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.search.core.engine.scope;

/**
 * Defines the different scope levels of X10 search activities.
 * 
 * @author egeay
 */
public final class X10SearchScope {
  
  /**
   * Search at the application level of a given project, i.e. code under source folders.
   */
  public static final int APPLICATION = 1;
  
  /**
   * Search at the level of the project library requirements.
   */
  public static final int LIBRARY = 1 << 1;
  
  /**
   * Search at the level of the project runtime.
   */
  public static final int RUNTIME = 1 << 2;
  
  /**
   * Search at the level of application, library and runtime scopes.
   */
  public static final int ALL = APPLICATION | LIBRARY | RUNTIME;
  
  // --- Private code
  
  private X10SearchScope() {}

}
