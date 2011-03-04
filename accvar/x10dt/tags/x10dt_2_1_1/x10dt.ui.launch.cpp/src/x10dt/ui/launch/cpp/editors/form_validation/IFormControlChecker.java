/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.ui.launch.cpp.editors.form_validation;


/**
 * Provides services to validate some form information (like text field) as they are entered before any final validation or
 * saving action occur.
 * 
 * <p>See {@link FormCheckerFactory} to get implementation(s) of this interface.
 * 
 * @author egeay
 */
public interface IFormControlChecker {
  
  /**
   * Validates the text information provided and returns a flag to indicate if it succeeded or not.
   * 
   * @param text The text to validate.
   * @return True if the validation succeeded, false otherwise.
   */
  public boolean validate(final String text);

}
