/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package org.eclipse.imp.x10dt.ui.launch.core.utils;

import java.util.Collection;

import org.eclipse.imp.utils.Pair;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;

/**
 * Utility methods for creating SWT controls in forms.
 * 
 * @author egeay
 */
public class SWTFormUtils {
  
  /**
   * Calls {@link #createLabelAndText(Composite, String, FormToolkit, Collection, int)} with no container to collect the
   * widgets and with the default text widget height.
   * 
   * @param parent The parent composite to consider.
   * @param labelText The text for the label.
   * @param toolkit The toolkit to use for creating the label and text widgets.
   * @return The non-null Text widget created.
   */
  public static Text createLabelAndText(final Composite parent, final String labelText, final FormToolkit toolkit) {
    return createLabelAndText(parent, labelText, toolkit, null, 1);
  }
  
  /**
   * Calls {@link #createLabelAndText(Composite, String, FormToolkit, Collection, int)} with the default text widget height.
   * 
   * @param parent The parent composite to consider.
   * @param labelText The text for the label.
   * @param toolkit The toolkit to use for creating the label and text widgets.
   * @param controlContainer A container that, if it is not <b>null</b> will contain after the call the label and text 
   * controls. Can be useful for enabling/disabling of groups of controls easily.
   * @return The non-null Text widget created.
   */
  public static Text createLabelAndText(final Composite parent, final String labelText, final FormToolkit toolkit,
                                        final Collection<Control> controlContainer) {
    return createLabelAndText(parent, labelText, toolkit, controlContainer, 1);
  }
  
  /**
   * Calls {@link #createLabelAndText(Composite, String, FormToolkit, Collection, int)} with no container to collect the
   * widgets.
   * 
   * @param parent The parent composite to consider.
   * @param labelText The text for the label.
   * @param toolkit The toolkit to use for creating the label and text widgets.
   * @param textHeightFactor The height factor to consider for the text widget. A value of 1 will create the default height
   * value based on the font size. A value of 2 will multiply this default value by 2, and so on.
   * @return The non-null Text widget created.
   */
  public static Text createLabelAndText(final Composite parent, final String labelText, final FormToolkit toolkit,
                                        final int textHeightFactor) {
    return createLabelAndText(parent, labelText, toolkit, null, textHeightFactor);
  }
  
  /**
   * Creates a label and text widgets on the same row in a SWT form.
   * 
   * @param parent The parent composite to consider.
   * @param labelText The text for the label.
   * @param toolkit The toolkit to use for creating the label and text widgets.
   * @param controlContainer A container that, if it is not <b>null</b> will contain after the call the label and text 
   * controls. Can be useful for enabling/disabling of groups of controls easily.
   * @param textHeightFactor The height factor to consider for the text widget. A value of 1 will create the default height
   * value based on the font size. A value of 2 will multiply this default value by 2, and so on.
   * @return The non-null Text widget created.
   */
  public static Text createLabelAndText(final Composite parent, final String labelText, final FormToolkit toolkit,
                                        final Collection<Control> controlContainer, final int textHeightFactor) {
    final Composite composite = toolkit.createComposite(parent);
    final boolean isTableWrapLayout = parent.getLayout() instanceof TableWrapLayout;
    composite.setFont(parent.getFont());
    if (isTableWrapLayout) {
      final TableWrapLayout tableWrapLayout = new TableWrapLayout();
      tableWrapLayout.numColumns = 2;
      composite.setLayout(tableWrapLayout);
      composite.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
    } else {
      composite.setLayout(new GridLayout(2, false));
      composite.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
    }
    
    final Label label = toolkit.createLabel(composite, labelText, SWT.WRAP);
    if (isTableWrapLayout) {
      label.setLayoutData(new TableWrapData(TableWrapData.FILL, TableWrapData.MIDDLE));
    } else {
      label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
    }
    if (controlContainer != null) {
      controlContainer.add(label);
    }
    final int style = (textHeightFactor == 1) ? SWT.NONE : SWT.WRAP;
    final Text text = toolkit.createText(composite, null /* value */, style);
    if (isTableWrapLayout) {
      final TableWrapData gd = new TableWrapData(TableWrapData.FILL_GRAB);
      if (textHeightFactor > 1) {
        gd.heightHint = text.getLineHeight() * textHeightFactor;
      }
      text.setLayoutData(gd);
    } else {
      final GridData gd = new GridData(SWT.FILL, SWT.NONE, true, false);
      if (textHeightFactor > 1) {
        gd.heightHint = text.getLineHeight() * textHeightFactor;
      }
      text.setLayoutData(gd);
    }
    if (controlContainer != null) {
      controlContainer.add(text);
    }
    return text;
  }
  
  /**
   * Creates a label, text (with the default text widget height) and button widgets on the same row in a SWT form.
   * 
   * @param parent The parent composite to consider.
   * @param labelText The text for the label.
   * @param buttonText The text for the button.
   * @param toolkit The toolkit to use for creating the label and text widgets.
   * @param controlContainer A container that, if it is not <b>null</b> will contain after the call the label and text 
   * controls. Can be useful for enabling/disabling of groups of controls easily.
   * @param textHeightFactor The height factor to consider for the text widget. A value of 1 will create the default height
   * value based on the font size. A value of 2 will multiply this default value by 2, and so on.
   * @return The non-null Text and Button widgets created.
   */
  public static Pair<Text,Button> createLabelTextButton(final Composite parent, final String labelText, 
                                                        final String buttonText, final FormToolkit toolkit, 
                                                        final Collection<Control> controlContainer) {
    return createLabelTextButton(parent, labelText, buttonText, toolkit, controlContainer, 1);
  }
  
  /**
   * Creates a label, text and button widgets on the same row in a SWT form.
   * 
   * @param parent The parent composite to consider.
   * @param labelText The text for the label.
   * @param buttonText The text for the button.
   * @param toolkit The toolkit to use for creating the label and text widgets.
   * @param controlContainer A container that, if it is not <b>null</b> will contain after the call the label and text 
   * controls. Can be useful for enabling/disabling of groups of controls easily.
   * @param textHeightFactor The height factor to consider for the text widget. A value of 1 will create the default height
   * value based on the font size. A value of 2 will multiply this default value by 2, and so on.
   * @return The non-null Text and Button widgets created.
   */
  public static Pair<Text,Button> createLabelTextButton(final Composite parent, final String labelText, 
                                                        final String buttonText, final FormToolkit toolkit, 
                                                        final Collection<Control> controlContainer,
                                                        final int textHeightFactor) {
    final Composite composite = toolkit.createComposite(parent);
    composite.setFont(parent.getFont());
    final boolean isTableWrapLayout = parent.getLayout() instanceof TableWrapLayout;
    if (isTableWrapLayout) {
      final TableWrapLayout tableWrapLayout = new TableWrapLayout();
      tableWrapLayout.numColumns = 3;
      composite.setLayout(tableWrapLayout);
      composite.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
    } else {
      composite.setLayout(new GridLayout(3, false));
      composite.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
    }
    
    final Label label = toolkit.createLabel(composite, labelText, SWT.WRAP);
    if (isTableWrapLayout) {
      label.setLayoutData(new TableWrapData(TableWrapData.FILL, TableWrapData.MIDDLE));
    } else {
      label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
    }
    if (controlContainer != null) {
      controlContainer.add(label);
    }
    final int style = (textHeightFactor == 1) ? SWT.NONE : SWT.WRAP;
    final Text text = toolkit.createText(composite, null /* value */, style);
    if (isTableWrapLayout) {
      final TableWrapData twData = new TableWrapData(TableWrapData.FILL_GRAB, TableWrapData.MIDDLE);
      if (textHeightFactor > 1) {
        twData.heightHint = text.getLineHeight() * textHeightFactor;
      }
      text.setLayoutData(twData);
    } else {
      final GridData gd = new GridData(SWT.FILL, SWT.CENTER, true, false);
      if (textHeightFactor > 1) {
        gd.heightHint = text.getLineHeight() * textHeightFactor;
      }
      text.setLayoutData(gd);
    }
    if (controlContainer != null) {
      controlContainer.add(text);
    }
    final Button button = toolkit.createButton(composite, buttonText, SWT.PUSH);
    if (isTableWrapLayout) {
      button.setLayoutData(new TableWrapData(TableWrapData.FILL));
    } else {
      button.setLayoutData(new GridData(SWT.FILL, SWT.NONE, false, false));
    }
    button.setEnabled(false);
    if (controlContainer != null) {
      controlContainer.add(button);
    }
    return new Pair<Text,Button>(text, button);
  }
  
  // --- Private code
  
  private SWTFormUtils() {}

}
