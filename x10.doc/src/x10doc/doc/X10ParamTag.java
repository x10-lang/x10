package x10doc.doc;

import com.sun.javadoc.ParamTag;
import com.sun.javadoc.Tag;

public class X10ParamTag extends X10Tag implements ParamTag {
	private boolean isTypeParameter;
	private String paramName, paramComment;
	private X10Tag[] inTags;
	
	public X10ParamTag(boolean isTypeParameter, String name, X10Tag[] inTags, String paramComment, String text, X10Doc holder){
		super(X10Tag.PARAM, text, holder);
		this.isTypeParameter = isTypeParameter;
		this.paramName = name;
		this.inTags = inTags;
	}
	
	public boolean isTypeParameter() {
		return isTypeParameter;
	}

	public String parameterComment() {
		return paramComment;
	}

	public String parameterName() {
		return paramName;
	}
	
	public Tag[] inlineTags(){
		return inTags;
	}
}
