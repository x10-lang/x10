package x10dt.ui.typeHierarchy;
/*******************************************************************************
 * Copyright (c) 2000, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/


/**
 * Interface to access the type selection component hosting a
 * type selection extension.
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 *
 * @since 3.2
 *
 * @noimplement This interface is not intended to be implemented by clients.
 * @noextend This interface is not intended to be extended by clients.
 */
public interface ITypeSelectionComponent {

	/**
	 * Triggers a search inside the type component with the
	 * current settings.
	 */
	public void triggerSearch();
}