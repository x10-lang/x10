/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.ui.launch.rms.core.environment;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.ptp.remotetools.environment.control.ITargetConfig;
import org.eclipse.ptp.remotetools.environment.control.ITargetControl;
import org.eclipse.ptp.remotetools.environment.core.ITargetElement;
import org.eclipse.ptp.remotetools.environment.extension.ITargetTypeExtension;
import org.eclipse.ptp.remotetools.environment.wizard.AbstractEnvironmentDialogPage;

/**
 * 
 * @author egeay
 */
public final class RemoteHostEnvironment implements ITargetTypeExtension {
  
  // --- Interface methods implementation

  public ITargetControl controlFactory(final ITargetElement element) throws CoreException {
    final ConfigFactory factory = new ConfigFactory(element.getAttributes());
    final ITargetConfig targetConfig = factory.createTargetConfig();
    return new TargetControl(targetConfig, new AuthInfo(targetConfig));
  }

  public String[] getControlAttributeNames() {
    return ConfigFactory.KEY_ARRAY;
  }

  public AbstractEnvironmentDialogPage dialogPageFactory(final ITargetElement targetElement) {
    return new ConfigurationPage(targetElement.getName(), targetElement.getAttributes());
  }

  public AbstractEnvironmentDialogPage dialogPageFactory() {
    return new ConfigurationPage();
  }

  public String[] getControlAttributeNamesForCipheredKeys() {
    return ConfigFactory.KEY_CIPHERED_ARRAY;
  }

}
