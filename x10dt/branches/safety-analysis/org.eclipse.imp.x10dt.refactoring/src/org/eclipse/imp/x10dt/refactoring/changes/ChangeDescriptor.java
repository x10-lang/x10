/*******************************************************************************
* Copyright (c) 2009 IBM Corporation.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*    Robert Fuhrer (rfuhrer@watson.ibm.com) - initial API and implementation
*******************************************************************************/

package org.eclipse.imp.x10dt.refactoring.changes;

/**
 * Describes a transformation as an abstract entity, including all parameters,
 * such that the transformation can be instantiated against another body of source,
 * and have its preconditions verified, and the transformation effected.
 */
public abstract class ChangeDescriptor {
    public abstract Object instantiate();
}
