package org.eclipse.imp.x10dt.ui.launch.cpp.editors;

import org.eclipse.imp.x10dt.ui.launch.cpp.LaunchMessages;
import org.eclipse.imp.x10dt.ui.launch.cpp.platform_conf.IX10PlatformConfWorkCopy;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;


final class X10CompilationConfigurationPage extends X10FormPage {

  X10CompilationConfigurationPage(final X10PlatformConfFormEditor editor, final IX10PlatformConfWorkCopy x10PlatformConf) {
    super(x10PlatformConf, editor, X10_COMPILATION_CONF_PAGE_ID, LaunchMessages.XPCP_X10PConfPageName);
    
    addCompletePageChangedListener(editor);
  }
  
  // --- Overridden methods

  protected void createFormContent(final IManagedForm managedForm) {
    final Form form = managedForm.getForm().getForm();
    final TableWrapLayout bodyLayout = new TableWrapLayout();
    bodyLayout.numColumns = 2;
    bodyLayout.makeColumnsEqualWidth = true;
    form.getBody().setLayout(bodyLayout);
    form.getBody().setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
    
    managedForm.addPart(new CompilationAndLinkingSectionPart(form.getBody(), this, getPlatformConf()));
    
    managedForm.addPart(new RemoteOutputFolderSectionPart(form.getBody(), this, getPlatformConf()));
    managedForm.addPart(new X10DistributionSectionPart(form.getBody(), this, getPlatformConf()));
  }
  
  public void dispose() {
    removeCompletePageChangedListener((X10PlatformConfFormEditor) getEditor());
    super.dispose();
  }

  // --- Fields
  
  static final String X10_COMPILATION_CONF_PAGE_ID = "x10.compilation.conf.page"; //$NON-NLS-1$

}
