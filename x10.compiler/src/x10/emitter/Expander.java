/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 */

package x10.emitter;

import polyglot.visit.Translator;

/**
 * An abstract class for sub-template expansion.
 */
public abstract class Expander {
	public final Emitter er;

	public Expander(Emitter er) {
		this.er = er;
	}

	public final void expand() {
		expand(er.tr);
	}

	public abstract void expand(Translator tr);

}