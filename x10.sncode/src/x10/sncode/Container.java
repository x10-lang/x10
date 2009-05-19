/*******************************************************************************
 * Copyright (c) 2002,2006 IBM Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package x10.sncode;

import java.util.ArrayList;
import java.util.List;

public abstract class Container implements SnConstants {
    List<Tree> attributes;

    public Container() {
        attributes = new ArrayList<Tree>();
    }

	public List<Tree> getAttributes() {
		return attributes;
	}

    static <S, T, X extends Exception> List<T> mapList(List<S> ss, Mapper<S, T, X> m) throws X {
        List<T> ts = new ArrayList<T>();
        for (S s : ss) {
            ts.add(m.map(s));
        }
        return ts;
    }

    static abstract class Mapper<S, T, X extends Exception> {
        abstract T map(S s) throws X;
    }

    public abstract void readFrom(SnFile sn, Tree tree) throws InvalidClassFileException;

    public abstract Tree makeTree();
}
