/**
 * Copyright (c) 2006 IBM Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial Implementation
 *
 */
package x10dt.ui.launch.rms.core.environment;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.ptp.remotetools.environment.control.SSHTargetControl;
import org.eclipse.ptp.remotetools.environment.wizard.AbstractEnvironmentDialogPage;
import org.eclipse.ptp.remotetools.utils.verification.ControlAttributes;
import org.eclipse.ptp.utils.ui.swt.AuthenticationFrame;
import org.eclipse.ptp.utils.ui.swt.AuthenticationFrameMold;
import org.eclipse.ptp.utils.ui.swt.ComboGroup;
import org.eclipse.ptp.utils.ui.swt.ComboGroupItem;
import org.eclipse.ptp.utils.ui.swt.GenericControlMold;
import org.eclipse.ptp.utils.ui.swt.TextGroup;
import org.eclipse.ptp.utils.ui.swt.TextMold;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import x10dt.ui.launch.rms.core.Messages;

final class ConfigurationPage extends AbstractEnvironmentDialogPage {

  ConfigurationPage(final String targetName, final ControlAttributes attributesMap) {
    super(targetName);
    this.fTargetName = targetName;
    this.fConfigFactory = new ConfigFactory(attributesMap);
  }

  ConfigurationPage() {
    super(Messages.ConfigurationPage_DefaultTargetName);
    this.fTargetName = Messages.ConfigurationPage_DefaultTargetName;
    this.fConfigFactory = new ConfigFactory();
  }

  // --- Abstract methods implementation

  public void createControl(final Composite parent) {
    this.setDescription(Messages.ConfigurationPage_DialogDescription);
    this.setTitle(Messages.ConfigurationPage_DialogTitle);
    this.setErrorMessage(null);

    GridLayout topLayout = new GridLayout();
    final Composite topControl = new Composite(parent, SWT.NONE);
    setControl(topControl);
    topControl.setLayout(topLayout);

    TextMold mold = new TextMold(GenericControlMold.GRID_DATA_ALIGNMENT_FILL | GenericControlMold.GRID_DATA_GRAB_EXCESS_SPACE,
                                 Messages.ConfigurationPage_LabelTargetName);
    this.fTargetNameGroup = new TextGroup(topControl, mold);

    createAuthControl(topControl);

    fillControls();
    registerListeners();
  }
  
  public ControlAttributes getAttributes() {
    return this.fConfigFactory.getAttributes();
  }

  public String getName() {
    return this.fTargetName;
  }
  
  public boolean isValid() {
    try {
      this.fRemoteAuthFrame.validateFields();
      this.fConfigFactory.createTargetConfig();
    } catch (CoreException e) {
      setErrorMessage(e.getMessage());
      return false;
    }
    return true;
  }
  
  // --- Private code
  
  private void createAuthControl(final Composite topControl) {
    AuthenticationFrameMold amold = new AuthenticationFrameMold(Messages.ConfigurationPage_ConnectionFrameTitle);
    amold.setBitmask(AuthenticationFrameMold.SHOW_HOST_TYPE_RADIO_BUTTON);
    amold.setLabelLocalhost(Messages.ConfigurationPage_LabelLocalhost);
    amold.setLabelRemoteHost(Messages.ConfigurationPage_LabelRemoteHost);
    amold.setLabelHideAdvancedOptions(Messages.ConfigurationPage_LabelHideAdvancedOptions);
    amold.setLabelHostAddress(Messages.ConfigurationPage_LabelHostAddress);
    amold.setLabelHostPort(Messages.ConfigurationPage_LabelHostPort);
    amold.setLabelIsPasswordBased(Messages.ConfigurationPage_LabelIsPasswordBased);
    amold.setLabelIsPublicKeyBased(Messages.ConfigurationPage_LabelIsPublicKeyBased);
    amold.setLabelPassphrase(Messages.ConfigurationPage_LabelPassphrase);
    amold.setLabelPassword(Messages.ConfigurationPage_LabelPassword);
    amold.setLabelPublicKeyPath(Messages.ConfigurationPage_LabelPublicKeyPath);
    amold.setLabelPublicKeyPathButton(Messages.ConfigurationPage_LabelPublicKeyPathButton);
    amold.setLabelPublicKeyPathTitle(Messages.ConfigurationPage_LabelPublicKeyPathTitle);
    amold.setLabelShowAdvancedOptions(Messages.ConfigurationPage_LabelShowAdvancedOptions);
    amold.setLabelTimeout(Messages.ConfigurationPage_LabelTimeout);
    amold.setLabelCipherType(Messages.ConfigurationPage_CipherType);
    amold.setLabelUserName(Messages.ConfigurationPage_LabelUserName);

    this.fRemoteAuthFrame = new AuthenticationFrame(topControl, amold);
  }

  private void fillControls() {
    ControlAttributes attributes = this.fConfigFactory.getAttributes();
    this.fTargetNameGroup.setString(this.fTargetName);
    this.fRemoteAuthFrame.setLocalhostSelected(attributes.getBoolean(ConfigFactory.ATTR_LOCALHOST_SELECTION));
    this.fRemoteAuthFrame.setHostPort(attributes.getInt(ConfigFactory.ATTR_CONNECTION_PORT));
    this.fRemoteAuthFrame.setHostAddress(attributes.getString(ConfigFactory.ATTR_CONNECTION_ADDRESS));
    this.fRemoteAuthFrame.setUserName(attributes.getString(ConfigFactory.ATTR_LOGIN_USERNAME));
    this.fRemoteAuthFrame.setPassword(attributes.getString(ConfigFactory.ATTR_LOGIN_PASSWORD));
    this.fRemoteAuthFrame.setPublicKeyPath(attributes.getString(ConfigFactory.ATTR_KEY_PATH));
    this.fRemoteAuthFrame.setPassphrase(attributes.getString(ConfigFactory.ATTR_KEY_PASSPHRASE));
    this.fRemoteAuthFrame.setTimeout(attributes.getInt(ConfigFactory.ATTR_CONNECTION_TIMEOUT));
    this.fRemoteAuthFrame.setPasswordBased(attributes.getBoolean(ConfigFactory.ATTR_IS_PASSWORD_AUTH));

    // Fill the combobox with available cipher types
    Map<String, String> cipherMap = SSHTargetControl.getCipherTypesMap();
    Set<String> cKeySet = cipherMap.keySet();
    ComboGroup cipherGroup = this.fRemoteAuthFrame.getCipherTypeGroup();
    for (Iterator<String> it = cKeySet.iterator(); it.hasNext();) {
      String key = it.next();
      String value = cipherMap.get(key);

      cipherGroup.add(new ComboGroupItem(key, value));
    }
    // Select the cipher type based on the attributes map.
    cipherGroup.selectIndexUsingID(attributes.getString(ConfigFactory.ATTR_CIPHER_TYPE));

    // org.eclipse.ptp.remotetools.internal.ssh.systemWorkspaceGroup.setString(attributes.getString(ConfigFactory.ATTR_SYSTEM_WORKSPACE));
  }

  private void readControls() {
    ControlAttributes attributes = this.fConfigFactory.getAttributes();
    this.fTargetName = this.fTargetNameGroup.getString();
    attributes.setBoolean(ConfigFactory.ATTR_LOCALHOST_SELECTION, this.fRemoteAuthFrame.isLocalhostSelected());
    attributes.setString(ConfigFactory.ATTR_LOGIN_USERNAME, this.fRemoteAuthFrame.getUserName());
    attributes.setString(ConfigFactory.ATTR_LOGIN_PASSWORD, this.fRemoteAuthFrame.getPassword());
    attributes.setString(ConfigFactory.ATTR_CONNECTION_ADDRESS, this.fRemoteAuthFrame.getHostAddress());
    attributes.setString(ConfigFactory.ATTR_CONNECTION_PORT, Integer.toString(this.fRemoteAuthFrame.getHostPort()));
    attributes.setString(ConfigFactory.ATTR_KEY_PATH, this.fRemoteAuthFrame.getPublicKeyPath());
    attributes.setString(ConfigFactory.ATTR_KEY_PASSPHRASE, this.fRemoteAuthFrame.getPassphrase());
    attributes.setString(ConfigFactory.ATTR_CONNECTION_TIMEOUT, Integer.toString(this.fRemoteAuthFrame.getTimeout()));
    attributes.setBoolean(ConfigFactory.ATTR_IS_PASSWORD_AUTH, this.fRemoteAuthFrame.isPasswordBased());
    attributes.setString(ConfigFactory.ATTR_CIPHER_TYPE, this.fRemoteAuthFrame.getSelectedCipherType().getId());
  }
  
  private void registerListeners() {
    this.fDataModifyListener = new DataModifyListener();
    this.fTargetNameGroup.addModifyListener(this.fDataModifyListener);
    this.fRemoteAuthFrame.addModifyListener(this.fDataModifyListener);
  }
  
  // --- Private classes
  
  private class DataModifyListener implements ModifyListener {

    // --- Interface methods implementation

    public synchronized void modifyText(final ModifyEvent event) {
      readControls();
      getContainer().updateButtons();
    }
    
  }

  // --- Fields
  
  private final ConfigFactory fConfigFactory;
  
  private String fTargetName;

  private TextGroup fTargetNameGroup;

  private AuthenticationFrame fRemoteAuthFrame;
  
  private DataModifyListener fDataModifyListener;

}
