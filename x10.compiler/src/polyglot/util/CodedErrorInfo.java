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
