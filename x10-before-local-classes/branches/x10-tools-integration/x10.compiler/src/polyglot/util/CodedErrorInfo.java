package polyglot.util;

import java.util.Map;

public class CodedErrorInfo extends ErrorInfo {
	int errorCode;
	Map<String, Object> attributes;

	public CodedErrorInfo(int kind, String message, Position position,
			Map<String, Object> attributes) {
		super(kind, message, position);
		this.attributes = attributes;
	}

	public int getErrorCode() {
		return (Integer) attributes.get("ERROR_CODE");
	}

	public Map<String, Object> getAttributes() {
		return attributes;
	}
}
