package com.ibm.wala.cast.x10.analysis.util;

import x10.finish.table.CallSite;
import x10.finish.table.CallTableVal;

public class CommunicationLabel {
	public final CallTableVal.Arity arity;
	public final CallSite cs;
	public final boolean isLocal;
	public final boolean isLast;

	public CommunicationLabel(CallTableVal.Arity a, CallSite c, boolean local,
			boolean last) {
		arity = a;
		cs = c;
		isLocal = local;
		isLast = last;
	}

	public CommunicationLabel(CallTableVal.Arity a, boolean last) {
		arity = a;
		isLast = last;
		// only method has a callsite
		cs = new CallSite("", "", 0, 0);// dummy cs
		isLocal = false;
	}

	@Deprecated
	public CommunicationLabel() {
		arity = CallTableVal.Arity.One;
		cs = new CallSite("", "", 0, 0);// dummy cs
		isLocal = false;
		isLast = false;
	}

	/*
	 * public boolean equals(Object o) { each label is different! }
	 */
	public String toString() {
		return arity.toString() + "-" + (isLocal == true ? "local" : "remote")
				+ (isLast == true ? "-last" : "");
	}
}
