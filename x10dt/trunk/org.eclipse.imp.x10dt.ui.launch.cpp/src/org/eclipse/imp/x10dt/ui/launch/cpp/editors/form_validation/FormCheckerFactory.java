/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package org.eclipse.imp.x10dt.ui.launch.cpp.editors.form_validation;

import org.eclipse.imp.x10dt.ui.launch.core.builder.target_op.ITargetOpHelper;
import org.eclipse.imp.x10dt.ui.launch.core.platform_conf.EArchitecture;
import org.eclipse.imp.x10dt.ui.launch.core.platform_conf.ETargetOS;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.forms.editor.IFormPage;

/**
 * Factory method(s) to create implementation(s) of {@link IFormControlChecker}.
 * 
 * @author egeay
 */
public final class FormCheckerFactory {
  
  /**
   * Creates a checker that is responsible for validating the C++ compiler version that is provided by the end-user.
   * 
   * @param targetOpHelper Helper class for file operations.
   * @param targetOS The current target operating system.
   * @param architecture The current architecture.
   * @param formPage The form page to get access to the form message managers.
   * @param control The control that contains the compiler name.
   * @return A non-null instance of {@link IFormControlChecker}.
   */
  public static IFormControlChecker createCPPCompilerVersionChecker(final ITargetOpHelper targetOpHelper, 
                                                                    final ETargetOS targetOS, final EArchitecture architecture,
                                                                    final IFormPage formPage, final Control control) {
    return new CppCompilerVersionCheker(targetOpHelper, targetOS, architecture, formPage, control);
  }
  
  /**
   * Creates a checker that is responsible for validating that a given text control (Text, Combo, etc...) is not empty.
   * 
   * @param formPage The form page to get access to the form message managers.
   * @param control The control that contains the text in question.
   * @param controlInfo The information that defines the control content.
   * @return A non-null instance of {@link IFormControlChecker}.
   */
  public static IFormControlChecker createEmptyControlChecker(final IFormPage formPage, final Control control, 
                                                              final String controlInfo) {
    return new EmptyControlChecker(formPage, control, controlInfo);
  }
  
  // --- Private code
  
  private FormCheckerFactory() {}

}
