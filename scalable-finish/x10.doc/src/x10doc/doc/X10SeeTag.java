package x10doc.doc;

import java.text.BreakIterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.Doc;
import com.sun.javadoc.MemberDoc;
import com.sun.javadoc.PackageDoc;
import com.sun.javadoc.SeeTag;
import com.sun.javadoc.Tag;

public class X10SeeTag extends X10Tag implements SeeTag {
	String label, itemName;
	X10RootDoc rootDoc;
	PackageDoc pkgDoc;
	X10ClassDoc classDoc;
	MemberDoc memberDoc;

	public X10SeeTag(String name, String label, String text, X10Doc holder) {
		super(name, text, holder);
		rootDoc = X10RootDoc.getRootDoc();
		processText(label);
//		pkgDoc = null;
//		classDoc = null;
//		memberDoc = null;
		this.label = itemName = label;
//		int i = text.indexOf(tag);
//		assert (i > 0) : "X10SeeTag constructor: " + text + "does not contain " + tag;
//		BreakIterator b = BreakIterator.getWordInstance();
//		String bText = text.substring(i+5, text.length()).trim(); 
//		b.setText(bText);
//		int start = b.first(), end = b.next();
//		this.itemName = bText.substring(start, end).trim();
//		start = end;
//		end = bText.indexOf('}', start);
//		if (start <= end) {
//			this.label = bText.substring(start, end).trim();
//		}
	}
	
	private void processText(String text){
		int index = text.indexOf("#");
		String classname = null;
		String member = null;
		if (index == -1) {
			classname = text;
		} else {
			classname = text.substring(0,index);
            if (classDoc != null) {
                member = text.substring(index+1);
                memberDoc = classDoc.getMemberDoc(member);
            }
		}
		if (classname.equals("")) {
			classname = X10RootDoc.getContainingClass(holder);
		}
		classDoc = (X10ClassDoc) rootDoc.classNamed(classname);
		index = classname.lastIndexOf(".");
		if (index != -1) {
			String pkgname = classname.substring(0,index);
			pkgDoc = rootDoc.packageNamed(pkgname);
		}
	}
	
	public X10ClassDoc holderClass() {
		if (holder instanceof X10ClassDoc) {
			return ((X10ClassDoc) holder);
		}
		else if (holder instanceof MemberDoc) {
			return ((X10ClassDoc) ((MemberDoc) holder).containingClass());
		}
		else {
			return null;
		}
	}
	
    public void processLinkTag(String text) {
        // Pattern p = Pattern.compile("\\{\\s*(@[^\\s]*)\\s*(([^\\s]*)\\.)?(([^\\s\\.#\\(]*)#)?([^\\s\\(]*)(\\([^\\)]*\\))?\\s*([^\\}]*)\\}");
        Pattern p = Pattern.compile("\\{\\s*(@[^\\s]+)\\s*(([^\\s]+)\\.)?(([^\\s\\.#\\(]*)#)?([^\\s\\(]+)(\\([^\\)]*\\))?\\s*([^\\}]*)\\}");

        // explanation of pattern: 3 parts of pattern for 3 entities of tag
        //   {@----    package.class#member(...)    label?}
        //   ---------|----------------------------|-------
        //   part1     part2                        part3

        // [part1] \\{\\s*(@[^\\s]*)\\s*

        // [part2] (([^\\s]+)\\.)?(([^\\s\\.#\\(]*)#)?([^\\s\\(]+)(\\([^\\)]*\\))?\\s*
        // [further split into subparts and explained]

        // (([^\\s]+)\\.)?: any seq of chars other than whitespace (may
        // include period) followed by a period, 0 or 1 occurrence of such a
        // seq; contains groups 2 and 3; 3 might be the package name

        // (([^\\s\\.#\\(]*)#)? : any seq of chars other than whitespace,
        // period, #, and (, followed by #, 0 or 1 occurrence of such a seq;
        // contains groups 4 and 5; 5 might be the class name

        // ([^\\s\\(]+): any seq of chars other than whitespace and (;
        // contains group 6; notice that such a seq must occur in the input
        // string but because of the "*" this pattern matches the empty
        // string also; might be the class or the member name

        // (\\([^\\)]*\\))?: seq containing '(', followed by any string of
        // chars other than ')', followed by ')', 0 or 1 occurrence of such a
        // seq; contains group 7; m.group(7) is the list of param types

        // [part3] ([^\\}]*)\\}
        // ([^\\}]*)\\}: any seq of chars other than '}', followed by '}'; contains group 8; label = m.group(8)

        Matcher m = p.matcher(text);
        String pkgName, className, memberName;
        String g3, g4, g5, g6, g7, g8; 
        X10ClassDoc cd;
        if (m.find()) {
            g3 = m.group(3); // (([^\\s]+)\\.)? contains 2 and 3 
            g4 = m.group(4); // (([^\\s\\.#\\(]*)#)? contains 4 and 5
            g5 = m.group(5); // (([^\\s\\.#\\(]*)#)? contains 4 and 5
            g6 = m.group(6); // ([^\\s\\(]+) contains 6
            g7 = m.group(7); // (\\([^\\)]*\\))? contains 7 
            g8 = m.group(8); // ([^\\}]*)\\} contains 8
            this.label = g8.trim();

            System.out.println("m.group() = \"" + m.group() + "\"");
            System.out.println("tag = m.group(1) = \"" + m.group(1) + "\"");
            System.out.println("m.group(2) = \"" + m.group(2) + "\"");
            System.out.println("m.group(3) = \"" + g3 + "\"");
            System.out.println("m.group(4) = \"" + g4 + "\"");
            System.out.println("m.group(5) = \"" + g5 + "\"");
            System.out.println("m.group(6) = \"" + g6 + "\"");
            System.out.println("m.group(7) = \"" + g7 + "\"");
            System.out.println("label = m.group(8).trim() = \"" + this.label + "\"");

            if (g4 != null) {
            	// text contains #; so, item is a class member (field, constructor, method); note
            	// that class.nestedClass#member is not allowed, although class.nestedClass is
            	if (g3 != null) { // g3 is the package name
            		className = g3 + "." + g5; // if g3 is non-null, then g5 must be non-null
            		cd = (X10ClassDoc) rootDoc.classNamed(className);
            	}
            	else if (g5 != null) { // null package name, and non-null class name
            		pkgName = holderClass().containingPackage().name();
            		className = pkgName + "." + g5; // g5 is a class in the holding class's package
            		cd = (X10ClassDoc) rootDoc.classNamed(className);
            	}
            	else { // null package name, null class name
            		cd = holderClass(); // g6 is a member of the current class
            	}
            	if (g7 == null) { // null list of parameters
            		memberDoc = cd.getField(g6);
            	}
            	else {
            		// TODO: needs to be modified to get the correct signature with fully qualified types, and
            		// remove arg names if present
            		memberDoc = cd.getConstructor(g6 + g7);
            		if (memberDoc == null) {
                		memberDoc = cd.getMethod(g6 + g7);
            		}
            	}
            	
            }
            else {
            	// item is a package, class, or nested class
            	String pgmElem = ((g3 == null) ? "" : g3) + ((g5 == null) ? "" : g5);
            	pkgDoc = (X10PackageDoc) rootDoc.packageNamed(pgmElem);
            	if (pkgDoc == null) {
            		classDoc = (X10ClassDoc) rootDoc.classNamed(pgmElem);
            	}
            }
            
//            if (listParams != null || m.group(4).endsWith("#")) {
//            	memberName = m.group(6);
//            	assert(className != null && memberName != null) : 
//            		"processLinkTag: unable to find class/method name for tag.";
//            	if (packageName != null) {
//            		cd = (X10ClassDoc) rootDoc.classNamed(packageName + "." + className);
//            	}
//            	else if (className != null) {
//            	}
//            	else {
//            		cd = (X10ClassDoc) holder; 
//            	}
//        		// lookup cd for method/constructor/field
//            }
        }
    }

    @Override
	public Tag[] firstSentenceTags() {
		return new Tag[0];
	}

	@Override
	public Tag[] inlineTags() {
		X10Tag[] result = new X10Tag[1];
		result[0] = new X10Tag("Text", text, holder);
		return result;
	}

	public String label() {
		return label;
	}

	public ClassDoc referencedClass() {
		// ClassDoc cd = X10RootDoc.getRootDoc().classNamed(itemName); 
		// return cd;
		return classDoc;
	}

	public String referencedClassName() {
		// return itemName;
		return ((classDoc == null) ? null : classDoc.qualifiedName());
	}

	public MemberDoc referencedMember() {
		return memberDoc;
	}

	public String referencedMemberName() {
		// return itemName;
		return ((memberDoc == null) ? null : memberDoc.qualifiedName());
	}

	public PackageDoc referencedPackage() {
		// PackageDoc p = referencedClass().containingPackage();
		return pkgDoc;
	}
}
