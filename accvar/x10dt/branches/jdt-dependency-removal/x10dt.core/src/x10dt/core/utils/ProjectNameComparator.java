/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/

package x10dt.core.utils;

import java.util.Comparator;

import org.eclipse.core.resources.IProject;

public final class ProjectNameComparator implements Comparator<IProject> {
	public int compare(IProject o1, IProject o2) {
		return o1.getName().compareTo(o2.getName());
	}
}