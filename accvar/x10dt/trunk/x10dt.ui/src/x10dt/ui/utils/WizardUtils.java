/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.ui.utils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * Utility methods for the various X10 wizards.
 * 
 * @author egeay
 */
public final class WizardUtils {
  
  /**
   * Creates a label with a push button on the right.
   * 
   * @param parent The composite parent to use.
   * @param labelText The text for the label.
   * @param buttonText The text for the push button.
   * @param selectionListener The selection listener to use if necessary. A null value is allowed if one does not want to
   * listen to selection event.
   * @return The button created. A non-null value in all cases.
   */
  public static Button createLabelAndPushButton(final Composite parent, final String labelText, final String buttonText, 
                                                final SelectionListener selectionListener) {
    final Label label = new Label(parent, SWT.NONE);
    label.setText(labelText);
    label.setFont(parent.getFont());
    label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
    
    final Button button = new Button(parent, SWT.PUSH);
    button.setFont(parent.getFont());
    button.setText(buttonText);
    button.setLayoutData(new GridData(SWT.RIGHT, SWT.NONE, false, false));
    if (selectionListener != null) {
      button.addSelectionListener(selectionListener);
    }
    return button;
  }
  
  /**
   * Creates a label with a text widget on the right.
   * 
   * @param parent The composite parent to use.
   * @param labelText The text for the label.
   * @param modifyListener The modification listener to use if necessary. A null value is allowed if one does not want to
   * listen to modification event.
   * @return The text widget created. A non-null value in all cases.
   */
  public static Text createLabelAndText(final Composite parent, final String labelText, final ModifyListener modifyListener) {
    final Label label = new Label(parent, SWT.NONE);
    label.setText(labelText);
    label.setFont(parent.getFont());
    label.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
    
    final Text text = new Text(parent, SWT.MULTI | SWT.BORDER | SWT.WRAP);
    text.setFont(parent.getFont());
    text.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
    if (modifyListener != null) {
      text.addModifyListener(modifyListener);
    }
    return text;
  }
  
  /**
   * Initializes the given file's contents with "Hello World" source code in X10.
   * 
   * @param packageName The package name to use.
   * @param typeName The type name that will contain the code.
   * @return A non-null input stream encapsulating the sample code.
   */
  public static InputStream createSampleContentStream(final String packageName, final String typeName) {
    final StringBuilder sb = new StringBuilder();

    if ((packageName != null) && (packageName.trim().length() > 0)) {
      sb.append("package " + packageName + ";\n\n"); //$NON-NLS-1$ //$NON-NLS-2$
    }
    sb.append("/**\n");
    sb.append(" * The canonical \"Hello, World\" demo class expressed in X10\n");
    sb.append(" */\n");
    sb.append("public class " + typeName + " {\n\n"); //$NON-NLS-1$ //$NON-NLS-2$
    sb.append("    /**\n");
    sb.append("     * The main method for the Hello class\n");
    sb.append("     */\n");
    sb.append("    public static def main(Array[String]) {\n"); //$NON-NLS-1$
    sb.append("        Console.OUT.println(\"Hello, World!\");\n"); //$NON-NLS-1$
    sb.append("    }\n\n"); //$NON-NLS-1$
    sb.append("}"); //$NON-NLS-1$

    return new ByteArrayInputStream(sb.toString().getBytes());
  }

}
