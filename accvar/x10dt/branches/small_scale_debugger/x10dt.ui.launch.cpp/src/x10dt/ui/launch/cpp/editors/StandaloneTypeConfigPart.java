/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.ui.launch.cpp.editors;

import x10dt.ui.launch.core.utils.PTPConstants;



final class StandaloneTypeConfigPart extends AbstractShallowTypeConfigPart implements ICITypeConfigurationPart {

  // --- Interface methods implementation

  public String getServiceProviderId() {
    return PTPConstants.STANDALONE_SERVICE_PROVIDER_ID;
  }

}
