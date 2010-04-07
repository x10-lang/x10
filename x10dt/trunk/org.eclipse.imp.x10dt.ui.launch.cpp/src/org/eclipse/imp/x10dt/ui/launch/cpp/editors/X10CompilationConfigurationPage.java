package org.eclipse.imp.x10dt.ui.launch.cpp.editors;

import org.eclipse.imp.x10dt.ui.launch.cpp.LaunchMessages;
import org.eclipse.imp.x10dt.ui.launch.cpp.platform_conf.IX10PlatformConfWorkCopy;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.Form;


final class X10CompilationConfigurationPage extends X10FormPage {

  X10CompilationConfigurationPage(final X10PlatformConfFormEditor editor, final IX10PlatformConfWorkCopy x10PlatformConf) {
    super(x10PlatformConf, editor, X10_COMPILATION_CONF_PAGE_ID, LaunchMessages.XPCP_X10PConfPageName);
    
    addCompletePageChangedListener(editor);
  }
  
  // --- Overridden methods

  protected void createFormContent(final IManagedForm managedForm) {
    final Form form = managedForm.getForm().getForm();
    form.getBody().setLayout(new GridLayout(2, true));
    form.getBody().setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
    
    final Composite left = managedForm.getToolkit().createComposite(form.getBody());
    left.setFont(form.getBody().getFont());
    left.setLayout(new GridLayout(1,false));
    final GridData gd1 = new GridData(SWT.FILL, SWT.FILL, true, true);
    gd1.widthHint = 0;
    gd1.heightHint = 0;
    left.setLayoutData(gd1);
    managedForm.addPart(new CompilationAndLinkingSectionPart(left, this, getPlatformConf()));
    
    final Composite right = managedForm.getToolkit().createComposite(form.getBody());
    right.setFont(form.getBody().getFont());
    right.setLayout(new GridLayout(1, false));
    final GridData gd2 = new GridData(SWT.FILL, SWT.FILL, true, true);
    gd2.widthHint = 0;
    gd2.heightHint = 0;
    right.setLayoutData(gd2);
    managedForm.addPart(new RemoteOutputFolderSectionPart(right, this, getPlatformConf()));
    managedForm.addPart(new X10DistributionSectionPart(right, this, getPlatformConf()));
  }
  
  public void dispose() {
    removeCompletePageChangedListener((X10PlatformConfFormEditor) getEditor());
    super.dispose();
  }

  // --- Fields
  
  static final String X10_COMPILATION_CONF_PAGE_ID = "x10.compilation.conf.page"; //$NON-NLS-1$

}
