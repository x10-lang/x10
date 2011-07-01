package x10doc.doc;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.MemberDoc;
import com.sun.javadoc.PackageDoc;
import com.sun.javadoc.ThrowsTag;
import com.sun.javadoc.Type;

public class X10ThrowsTag extends X10Tag implements ThrowsTag{
	String label, itemName;
	X10RootDoc rootDoc;
	PackageDoc pkgDoc;
	X10ClassDoc classDoc;
	MemberDoc memberDoc;
	boolean beenCalled;
	
	public X10ThrowsTag(String name, String text, X10Doc holder) {
		super(name, text, holder);
		rootDoc = X10RootDoc.getRootDoc();
	}
	
	public ClassDoc exception() {
		if(classDoc == null)
		{
			processText();
		}
		
		return classDoc;
	}

	public String exceptionComment() {
		return text;
	}

	public String exceptionName() {
		return (classDoc == null) ? null : classDoc.name();
	}

	public Type exceptionType() {
		return classDoc;
	}
	
	void processText() {
		String tokens[] = text.split("\\s");
		if(tokens.length > 0)
		{
			String classname = tokens[0];
			classDoc = rootDoc.findClass(holder, classname);
			text = text.replace(tokens[0], "").trim();
		}
	}
}
