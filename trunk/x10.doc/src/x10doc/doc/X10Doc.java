package x10doc.doc;

import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import polyglot.types.Flags;

import com.sun.javadoc.Doc;
import com.sun.javadoc.SeeTag;
import com.sun.javadoc.SourcePosition;
import com.sun.javadoc.Tag;

public class X10Doc implements Doc {
	private String rawComment;
	private String comment;
	X10Tag[] firstSentenceTags, inlineTags;
	
	public X10Doc(String rawComment) {
		this.rawComment = rawComment;
		setComment(rawCommentToText(rawComment));
	}

	/**
	 * Initializes fields comment, inlineTags of the object
	 * @param commentText the processed comment text
	 */
	public void setComment(String commentText) {
		// initialize inlineTags
		ArrayList<Tag> result = new ArrayList<Tag>();
        Pattern p = Pattern.compile("\\{\\s*@[^\\}]*\\}"); // matches inline tags
        // Pattern p = Pattern.compile("\\{\\s*@([^\\s\\}]+)\\s*([^\\}]*)\\s*}"); // matches inline tags
        Matcher m = p.matcher(commentText);
        int start = 0, end = 0;
        // create an array of tag objects of kind "Text" and "@link"; as explained in the
        // doclet API, for a comment
        //   This is a {@link Doc commentlabel} example.
        // create an array of Tag objects:
        //  * tags[0] is a Tag with name "Text" and text consisting of "This is a "
        //  * tags[1] is a SeeTag with name "@link", and label "commentlabel"
        //  * tags[2] is a Tag with name "Text" and text consisting of " example." 
        while (m.find()) {
            end = m.start();
            String linkText = m.group();
            // System.out.print("String = \"" + commentText.substring(start, end));
            // System.out.println("\"; linkText = \"" + linkText + "\"");
            result.add(new X10Tag("Text", commentText.substring(start, end), this));
            result.add(X10Tag.processInlineTag(linkText, this));
            // result.add(new X10SeeTag(true, linkText, this)); 
            // "true" signifies an @link tag, as opposed to an @see tag 
            start = m.end();
        }

        // initialize comment
        int blockTagStart = commentText.indexOf("@", start); // start of block tags within comment
        blockTagStart = (blockTagStart == -1) ? commentText.length() : blockTagStart;
        this.comment = commentText.substring(0, blockTagStart).trim();
        result.add(new X10Tag("Text", commentText.substring(start, blockTagStart), this));
        // System.out.println("X10Doc.initializeFields(): Rest = \"" + commentText.substring(start, blockTagStart) + "\"");
        
        // add constraints, if any
        // String decl = declString();
        // if (decl != null) {
        //	result.add(new X10Tag(decl, this));
        // }
        
        inlineTags = result.toArray(new X10Tag[0]);

        // initialize firstSentenceTags
		BreakIterator b = BreakIterator.getSentenceInstance();
		b.setText(comment);
		start = end = 0;
		start = b.first(); end = b.next();
		String firstSentence = ((start <= end) ? comment.substring(start, end).trim() : "");
		// System.out.println("X10Doc.initializeFields(): firstSentence = \"" + firstSentence + "\"");
		firstSentenceTags = createInlineTags(firstSentence);
		
		// TODO: creating Tag objects for block tags and storing them in a field of this object
	}
	
	// used to add comment lines displaying X10 class declarations, specifically the class constraints in 
	// the declarations; the declaration string argument contains @link tags to entities in the constraint
	// TODO: update this.rawComment, this.comment appropriately
	public void addDeclTag(String declString) {
		if (declString == null) {
			return;
		}
		X10Tag[] declTags = createInlineTags(declString);

		// firstSentenceTags = concat(declTags, firstSentenceTags);

		// place declaration before the first sentence of the existing comment but do not add
		// it to firstSentenceTags; this ensures that the declaration string is not displayed 
		// in tables such as the "Class Summary" table
		inlineTags = concat(declTags, inlineTags);
		
//		int len = inlineTags.length + declTags.length;
//		X10Tag[] newTags = new X10Tag[len];
//		int i;
//		for (i = 0; i < inlineTags.length; i++) {
//			newTags[i] = inlineTags[i];
//		}
//		for (int j = 0 ; i < len; i++, j++) {
//			newTags[i] = declTags[j];
//		}
//		inlineTags = newTags;
	}

    public static X10Tag[] concat(X10Tag[] orig, X10Tag[] newTags) {
    	int len = orig.length + newTags.length;
    	X10Tag[] result = new X10Tag[len];
    	int i;
		for (i = 0; i < orig.length; i++) {
			result[i] = orig[i];
		}
		for (int j = 0 ; i < len; i++, j++) {
			result[i] = newTags[j];
		}
		return result;
    }
	
	// method to be overriden in sub-classes such as X10FieldDoc, X10MethodDoc, X10ConstructorDoc, 
	// X10TypeDefDoc, etc; the return string may be a description of the field type or declaration of
	// the method etc., and is expected to be added to the comments of the associated Doc object, by 
	// a method such as X10Doc.addDeclTag(...)
	public String declString() {
		return "";
	}
	
	// used to add a comment line displaying X10 type of a field, method/constructor return value, 
	// and method/constructor parameter, specifically the constraints in these X10 types; argument str
	// cannot have any inline (or block) tags
	// TODO: will not work if comment contains block tags
	public void addNewLineToComment(String str) {
		if (str == null || str.equals("")) {
			return;
		}
		String newLine = "<PRE>\n</PRE>" + str;
		this.rawComment = this.rawComment + newLine;
		setComment(comment + newLine);
	}
	
	/**
	 * Creates an array of Tag objects of kind "Text" and "@link" for the specified string.
	 * @param text processed comment text which may contain inline tags, but not block tags
	 */
	public X10Tag[] createInlineTags(String text) {
		ArrayList<X10Tag> result = new ArrayList<X10Tag>();
        Pattern p = Pattern.compile("\\{\\s*@[^\\}]*\\}");
        Matcher m = p.matcher(text);
        int start = 0, end = 0;
        while (m.find()) {
            end = m.start();
            String linkText = m.group();
            System.out.print("String = \"" + text.substring(start, end));
            System.out.println("\"; linkText = \"" + linkText + "\"");
            result.add(new X10Tag("Text", text.substring(start, end), this));
            result.add(X10Tag.processInlineTag(linkText, this));
            // result.add(new X10Tag("Text", text.substring(start, end), this));
            // result.add(new X10SeeTag(true, linkText, this)); 
              // "true" signifies an @link tag, as opposed to an @see tag 
            start = m.end();
        }
        result.add(new X10Tag("Text", text.substring(start, text.length()), this));
        // System.out.println("Rest = \"" + text.substring(start, text.length()) + "\"");
        
        return result.toArray(new X10Tag[0]);
	}

	public String commentText() {
		if (X10RootDoc.printSwitch)
			System.out.println("Doc.commentText() called for "+name());
		// new Exception().printStackTrace();
		return comment;
	}

	public int compareTo(Object arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	public Tag[] firstSentenceTags() {
		// TODO Auto-generated method stub
		if (X10RootDoc.printSwitch)
			System.out.println("Doc.firstSentenceTags() called for "+name());
		return firstSentenceTags;
	}

	public String getRawCommentText() {
		if (X10RootDoc.printSwitch)
			System.out.println("Doc.getRawCommentText() called for "+name());
		new Exception().printStackTrace();
		return rawComment;
	}

	/**
	 * Return comment as an array of tags. Includes inline tags (i.e. {@link reference} tags) 
	 * but not block tags.
	 */
	public Tag[] inlineTags() {
		if (X10RootDoc.printSwitch)
			System.out.println("Doc.inlineTags() called for "+name());
		return inlineTags;
	}

	public boolean isAnnotationType() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isAnnotationTypeElement() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isClass() {
		return false;
	}

	public boolean isConstructor() {
		return false;
	}

	public boolean isEnum() {
		return false;
	}

	public boolean isEnumConstant() {
		return false;
	}

	public boolean isError() {
		return false;
	}

	public boolean isException() {
		return false;
	}

	public boolean isField() {
		return false;
	}

	public boolean isIncluded() {
		return false;
	}

	public boolean isInterface() {
		return false;
	}

	public boolean isMethod() {
		return false;
	}

	public boolean isOrdinaryClass() {
		return false;
	}

	public String name() {
		if (X10RootDoc.printSwitch)
			System.out.println("Doc.name() called for "+this);
		return "Dummy Doc Name";
	}

	public SourcePosition position() {
		if (X10RootDoc.printSwitch)
			System.out.println("Doc.position() called for "+name());
		return null;
	}

	/**
	 * Return the see also tags in this Doc item. 
	 */
	public SeeTag[] seeTags() {
		if (X10RootDoc.printSwitch)
			System.out.println("Doc.seeTags() called for "+name());
		return new SeeTag[0];
	}

	public void setRawCommentText(String arg0) {
		if (X10RootDoc.printSwitch)
			System.out.println("Doc.setRawCommentText(String) called for "+name());
		this.rawComment = arg0;
		setComment(rawCommentToText(rawComment));
	}
	
	/**
	 * Return all tags in this Doc item.
	 */
	public Tag[] tags() {
		if (X10RootDoc.printSwitch)
			System.out.println("Doc.tags() called for "+name());
		return inlineTags();
	}

	/**
	 * Return tags of the specified kind in this Doc item.
	 */
	public Tag[] tags(String arg0) {
		// TODO Auto-generated method stub
		if (X10RootDoc.printSwitch)
			System.out.println("Doc.tags(" + arg0 + ") called for "+name());
		if (arg0.equals("Text")) { 
			return inlineTags();
		}
		else return new Tag[0];
	}
	
	public static String rawCommentToText(String rawComment) {
		if (rawComment == null || rawComment.length() == 0) {
			return "";
		}
		assert (rawComment.startsWith("/**")) : "Non-javadoc comment: "+rawComment;
		assert (rawComment.endsWith("*/")) : "No comment terminator: "+rawComment;
		String result = rawComment;
		result = result.replaceFirst("^/\\*\\*\\s*", "");
		result = result.replaceFirst("\\s*\\*/$", "");
		result = result.replaceAll("\\n*\\s*(\\*)+\\s*", " "); // prev: \n*\\s*\\*\\s*, diff: \\n was \n. (\\*)+ was \\*
		result = result.replace('\n', ' ');
		return result.trim();
	}

	private static final HashMap<String, Integer> flagsToHex = new HashMap<String, Integer>();
	static {
		flagsToHex.put(Flags.PUBLIC.toString(),    0x0001);
		flagsToHex.put(Flags.PRIVATE.toString(),   0x0002);
		flagsToHex.put(Flags.PROTECTED.toString(), 0x0004);
		flagsToHex.put(Flags.STATIC.toString(),    0x0008);
		flagsToHex.put(Flags.FINAL.toString(),     0x0010);
		//flagsToHex.put(Flags.SYNCHRONIZED.toString(), 0x0020);
		flagsToHex.put(Flags.NATIVE.toString(), 0x0100);
		flagsToHex.put(Flags.ABSTRACT.toString(), 0x0400);
		//flagsToHex.put(Flags.STRICTFP.toString(), 0x0800);
	}

	public static int flagsToModifierSpecifier(Set flags) {
		int r = 0;
		for (Object flag : flags) {
			// flag could be "property" which is not in flagsToHex (and not recognized by 
			// the standard doclet)
			if (flagsToHex.containsKey((String)flag)) {
				r |= flagsToHex.get((String)flag);
			}
		}
		return r;
	}
}
