/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.ui.utils;

import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import x10dt.ui.X10DTUIPlugin;

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
	 * Returns a section in the Java plugin's dialog settings. If the section doesn't exist yet, it is created.
	 *
	 * @param name the name of the section
	 * @return the section of the given name
	 * @since 3.2
	 */
	public static IDialogSettings getDialogSettingsSection(String name) {
		IDialogSettings dialogSettings= X10DTUIPlugin.getInstance().getDialogSettings();
		IDialogSettings section= dialogSettings.getSection(name);
		if (section == null) {
			section= dialogSettings.addNewSection(name);
		}
		return section;
	}
}
