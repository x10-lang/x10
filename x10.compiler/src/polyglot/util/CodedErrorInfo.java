/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 * This file was originally derived from the Polyglot extensible compiler framework.
 *
 *  (C) Copyright 2000-2007 Polyglot project group, Cornell University
 *  (C) Copyright IBM Corporation 2007-2015.
 */

package polyglot.util;

import java.util.Map;

public class CodedErrorInfo extends ErrorInfo {
	
	public static final int ERROR_CODE_SURROUND_THROW = 1001;
	public static final int ERROR_CODE_METHOD_NOT_FOUND = 1002;
	public static final int ERROR_CODE_CONSTRUCTOR_NOT_FOUND  = 1003;
	public static final int ERROR_CODE_METHOD_NOT_IMPLEMENTED = 1004;
	public static final int ERROR_CODE_TYPE_NOT_FOUND = 1005;
	public static final int ERROR_CODE_WRONG_PACKAGE = 1006;
	
	int errorCode;
	public static final String ERROR_CODE_KEY= "errorCode";
	Map<String, Object> attributes;

	public CodedErrorInfo(int kind, String message, Position position,
			Map<String, Object> attributes) {
		super(kind, message, position);
		this.attributes = attributes;
	}

	public int getErrorCode() {
		return (Integer) attributes.get(ERROR_CODE_KEY);
	}

	public Map<String, Object> getAttributes() {
		return attributes;
	}
}
