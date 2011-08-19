/*
 * This file is part of the Polyglot extensible compiler framework.
 *
 * Copyright (c) 2000-2006 Polyglot project group, Cornell University
 * 
 */

package polyglot.util;

/** A two-element tuple.
 */
public class Pair<S,T>
{
	protected S fst;
	protected T snd;

	public Pair(S p1, T p2) {
		this.fst = p1;
		this.snd = p2;
	}

	public S fst() {
		return fst;
	}

	public T snd() {
		return snd;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Pair<?, ?>) {
			Pair<?,?> p = (Pair<?,?>) obj;
			boolean r1 = fst == null ? p.fst == null : fst.equals(p.fst);
			boolean r2 = snd == null ? p.snd == null : snd.equals(p.snd);
			return r1 && r2;
		}
		return false;
	}

	@Override
	public int hashCode() {
		return (fst == null ? 0 : fst.hashCode()) + (snd == null ? 0 : snd.hashCode());
	}

	public String toString() {
		return "<" + fst + ", " + snd + ">";
	}
}
