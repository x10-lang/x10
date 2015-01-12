/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2015.
 */

package x10doc.doc;

import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import polyglot.types.Flags;
import x10.util.CollectionFactory;

import com.sun.javadoc.Doc;
import com.sun.javadoc.ProgramElementDoc;
import com.sun.javadoc.SeeTag;
import com.sun.javadoc.SourcePosition;
import com.sun.javadoc.Tag;

public abstract class X10Doc implements Doc {
    private String rawComment;
    private String comment;
    X10Tag[] firstSentenceTags, inlineTags;
    List<Tag> blockTags = new ArrayList<Tag>();
    List<Tag> paramTags = new ArrayList<Tag>();

    public void processComment(String rawComment) {
        this.rawComment = rawComment;
        procComment(rawCommentToText(rawComment));
    }

    /**
     * Initializes fields comment, inlineTags of the object
     * 
     * @param commentText the processed comment text
     */
    private void procComment(String commentText) {
        // initialize inlineTags
        ArrayList<Tag> result = new ArrayList<Tag>();
        String noInlineTags = replaceAtSigns(commentText);

        /*
         * Pattern p = Pattern.compile("\\{\\s*@[^}]*\\}"); // matches inline
         * tags // Pattern p =
         * Pattern.compile("\\{\\s*@([^\\s\\}]+)\\s*([^\\}]*)\\s*}"); // matches
         * inline tags Matcher m = p.matcher(commentText); int start = 0, end =
         * 0; // create an array of tag objects of kind "Text" and "@link"; as
         * explained in the // doclet API, for a comment // This is a {@link Doc
         * commentlabel} example. // create an array of Tag objects: // *
         * tags[0] is a Tag with name "Text" and text consisting of "This is a "
         * // * tags[1] is a SeeTag with name "@link", and label "commentlabel"
         * // * tags[2] is a Tag with name "Text" and text consisting of
         * " example." while (m.find()) { end = m.start(); String linkText =
         * m.group(); // System.out.print("String = \"" +
         * commentText.substring(start, end)); //
         * System.out.println("\"; linkText = \"" + linkText + "\""); //
         * result.add(new X10Tag("Text", commentText.substring(start, end),
         * this)); result.add(X10Tag.processInlineTag(linkText, this)); //int
         * index = commentText.indexOf(linkText); //commentText =
         * commentText.substring(0, index) + commentText.substring(index +
         * linkText.length()); // result.add(new X10SeeTag(true, linkText,
         * this)); // "true" signifies an @link tag, as opposed to an @see tag
         * start = m.end(); }
         */
        if (!commentText.startsWith("@")) { // make sure that there is a
                                            // beginning paragraph
            // initialize comment
            int blockTagStart = noInlineTags.indexOf("@"); // start of block
                                                           // tags within
                                                           // comment
            blockTagStart = (blockTagStart == -1) ? commentText.length() : blockTagStart;
            this.comment = commentText.substring(0, blockTagStart).trim();
            if (!comment.equals("")) {
                result.addAll(createInlineTags(comment, this));
            }

            // }
            // add constraints, if any
            // String decl = declString();
            // if (decl != null) {
            // result.add(new X10Tag(decl, this));
            // }

            // initialize firstSentenceTags
            BreakIterator b = BreakIterator.getSentenceInstance();
            b.setText(comment);
            int start = 0;
            int end = 0;
            start = b.first();
            end = b.next();
            String firstSentence = ((start <= end) ? comment.substring(start, end).trim() : "");
            // System.out.println("X10Doc.initializeFields(): firstSentence = \""
            // + firstSentence + "\"");
            firstSentenceTags = createInlineTags(firstSentence, this).toArray(new X10Tag[0]);

        } else {
            firstSentenceTags = new X10Tag[0];
        }

        inlineTags = result.toArray(new X10Tag[0]);

        // TODO: creating Tag objects for block tags and storing them in a field
        // of this object
        Pattern blockTagPattern = Pattern.compile("\\s*@[^@]*");
        Matcher blockTagMatcher = blockTagPattern.matcher(noInlineTags);
        while (blockTagMatcher.find()) {
            String tagText = blockTagMatcher.group();
            int start = blockTagMatcher.start();
            processBlockTag(commentText.substring(start, start + tagText.length()));
        }
    }

    // replaces @ signs of inline tags in the comment with another char ("A")
    private String replaceAtSigns(String comment) {
        String result = comment;
        Pattern p = Pattern.compile("\\{\\s*@[^}]*\\}"); // matches inline tags
        Matcher m = p.matcher(comment);
        while (m.find()) {
            String linkText = m.group();
            int atIndex = linkText.indexOf("@") + m.start();
            result = result.substring(0, atIndex) + "A" + result.substring(atIndex + 1);
        }
        return result;
    }

    private void processBlockTag(String tagText) {
        String kind = null;
        String text = null;
        Pattern p = Pattern.compile("@[^\\s]*");
        Matcher m = p.matcher(tagText);
        if (m.find()) {
            kind = m.group().substring(1);
            text = tagText.substring(m.end()).trim();
        }
        if (kind.equals(X10Tag.PARAM)) {
            Pattern p1 = Pattern.compile("[^\\s]*");
            Matcher m1 = p1.matcher(text);
            if (m1.find()) {
                String name = m1.group();
                String paramComment = text.substring(m1.end()).trim();
                X10Tag[] inTags = createInlineTags(paramComment, this).toArray(new X10Tag[0]);
                X10ParamTag t = new X10ParamTag(false, name, inTags, paramComment, text, this);
                blockTags.add(t);
                paramTags.add(t);
            }

        }

        else if (kind.equals("see") || kind.equals("link") || kind.equals("linkplain")) {
            blockTags.add(new X10SeeTag("@" + kind, text, text, this));
        }

        else if (kind.equals(X10Tag.THROWS)) {
            blockTags.add(new X10ThrowsTag(kind, text, this));
        }

        else {
            blockTags.add(new X10Tag(kind, text, this));
        }
    }

    public X10Tag[] getX10Tags() {
        return new X10Tag[0];
    }

    public void addGuardTags(List<X10Tag> list) {
        StringBuilder sb = new StringBuilder();
        Tag[] tags = tags(X10Tag.GUARD);
        if (tags.length > 0) {
            sb.append("<DL><DT><B>Guard:</B>");
            for (Tag tag : tags) {
                sb.append("<DD><CODE>");
                String code = tag.text();
                String tokens[] = code.split("\\s");

                if (tokens.length > 1) {
                    sb.append(tokens[0]);
                    sb.append("</CODE> - ");
                    sb.append(code.replace(tokens[0], "").trim());
                }

                else {
                    sb.append("</CODE>");
                }

            }
            sb.append("</DL><P>");
        }

        list.addAll(createInlineTags(sb.toString(), this));
    }

    // used to add comment lines displaying X10 class declarations, specifically
    // the class constraints in
    // the declarations; the declaration string argument contains @link tags to
    // entities in the constraint
    // TODO: update this.rawComment, this.comment appropriately
    public void addDeclTag(String declString) {
        if (declString == null) {
            return;
        }
        X10Tag[] declTags = createInlineTags(declString, this).toArray(new X10Tag[0]);

        // firstSentenceTags = concat(declTags, firstSentenceTags);

        // place declaration before the first sentence of the existing comment
        // but do not add
        // it to firstSentenceTags; this ensures that the declaration string is
        // not displayed
        // in tables such as the "Class Summary" table
        inlineTags = concat(declTags, inlineTags);

        // int len = inlineTags.length + declTags.length;
        // X10Tag[] newTags = new X10Tag[len];
        // int i;
        // for (i = 0; i < inlineTags.length; i++) {
        // newTags[i] = inlineTags[i];
        // }
        // for (int j = 0 ; i < len; i++, j++) {
        // newTags[i] = declTags[j];
        // }
        // inlineTags = newTags;
    }

    public static X10Tag[] concat(X10Tag[] orig, X10Tag[] newTags) {
        int len = orig.length + newTags.length;
        X10Tag[] result = new X10Tag[len];
        int i;
        for (i = 0; i < orig.length; i++) {
            result[i] = orig[i];
        }
        for (int j = 0; i < len; i++, j++) {
            result[i] = newTags[j];
        }
        return result;
    }

    // method to be overriden in sub-classes such as X10FieldDoc, X10MethodDoc,
    // X10ConstructorDoc,
    // X10TypeDefDoc, etc; the return string may be a description of the field
    // type or declaration of
    // the method etc., and is expected to be added to the comments of the
    // associated Doc object, by
    // a method such as X10Doc.addDeclTag(...)
    public String declString() {
        return "";
    }

    // used to add a comment line displaying X10 type of a field,
    // method/constructor return value,
    // and method/constructor parameter, specifically the constraints in these
    // X10 types; argument str
    // cannot have any inline (or block) tags
    // TODO: will not work if comment contains block tags
    public void addNewLineToComment(String str) {
        if (str == null || str.equals("")) {
            return;
        }
        String newLine = "<PRE>\n</PRE>" + str;
        this.rawComment = this.rawComment + newLine;
        processComment(comment + newLine);
    }

    /**
     * Creates an array of Tag objects of kind "Text" and "@link" for the
     * specified string.
     * 
     * @param text processed comment text which may contain inline tags, but not
     *            block tags
     */
    public static List<X10Tag> createInlineTags(String text, X10Doc holder) {
        ArrayList<X10Tag> result = new ArrayList<X10Tag>();
        Pattern p = Pattern.compile("\\{\\s*@[^}]*\\}");
        Matcher m = p.matcher(text);
        int start = 0, end = 0;
        while (m.find()) {
            end = m.start();
            String linkText = m.group();
            // System.out.print("String = \"" + text.substring(start, end));
            // System.out.println("\"; linkText = \"" + linkText + "\"");
            result.add(new X10Tag("Text", text.substring(start, end), holder));
            result.add(X10Tag.processInlineTag(linkText, holder));
            // result.add(new X10Tag("Text", text.substring(start, end), this));
            // result.add(new X10SeeTag(true, linkText, this));
            // "true" signifies an @link tag, as opposed to an @see tag
            start = m.end();
        }
        result.add(new X10Tag("Text", text.substring(start, text.length()), holder));

        // System.out.println("Rest = \"" + text.substring(start, text.length())
        // + "\"");
        if (result.isEmpty()) {
            result.add(new X10Tag("Text", text, holder));
        }
        return result;
    }

    public String commentText() {
        if (X10RootDoc.printSwitch) System.out.println("Doc.commentText() called for " + name());
        return comment;
    }
    
    public Tag[] firstSentenceTags() {
        if (X10RootDoc.printSwitch) System.out.println("Doc.firstSentenceTags() called for " + name());
        return firstSentenceTags;
    }

    public String getRawCommentText() {
        if (X10RootDoc.printSwitch) System.out.println("Doc.getRawCommentText() called for " + name());
        new Exception().printStackTrace();
        return rawComment;
    }

    /**
     * Return comment as an array of tags. Includes inline tags (i.e.
     * {@link reference} tags) but not block tags.
     */
    public Tag[] inlineTags() {
        if (X10RootDoc.printSwitch) System.out.println("Doc.inlineTags() called for " + name());
        return inlineTags;
    }

    public boolean isAnnotationType() {
        return false;
    }

    public boolean isAnnotationTypeElement() {
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

    
    public int compareTo(Object other) {
        return name().compareTo(((X10Doc)other).name());
    }
    
    public String name() {
        if (X10RootDoc.printSwitch) System.out.println("Doc.name() called for " + this);
        return "Dummy Doc Name";
    }

    public SourcePosition position() {
        if (X10RootDoc.printSwitch) System.out.println("Doc.position() called for " + name());
        return null;
    }

    /**
     * Return the see also tags in this Doc item.
     */
    public SeeTag[] seeTags() {
        if (X10RootDoc.printSwitch) System.out.println("Doc.seeTags() called for " + name());

        Tag[] tags = tags(X10Tag.SEE);
        SeeTag[] newTags = new SeeTag[tags.length];
        System.arraycopy(tags, 0, newTags, 0, tags.length);
        return newTags;
    }

    public void setRawCommentText(String arg0) {
        if (X10RootDoc.printSwitch) System.out.println("Doc.setRawCommentText(String) called for " + name());
        this.rawComment = arg0;
        processComment(rawComment);
    }

    /**
     * Return all tags in this Doc item.
     */
    public Tag[] tags() {
        if (X10RootDoc.printSwitch) System.out.println("Doc.tags() called for " + name());
        Tag[] result = new Tag[blockTags.size() + inlineTags.length];
        if (blockTags.size() > 0) System.arraycopy(blockTags.toArray(new Tag[0]), 0, result, 0, blockTags.size());
        if (inlineTags.length > 0) System.arraycopy(inlineTags, 0, result, blockTags.size(), inlineTags.length);
        return result;
        // return inlineTags();
    }

    /**
     * Return tags of the specified kind in this Doc item.
     */
    public Tag[] tags(String kind) {
        // TODO Auto-generated method stub
        if (X10RootDoc.printSwitch) System.out.println("Doc.tags(" + kind + ") called for " + name());
        if (kind.equals("Text")) {
            return inlineTags();
        } else
            return getTags(kind);
    }

    private Tag[] getTags(String kind) {
        List<Tag> result = new ArrayList<Tag>();
        for (Tag t : blockTags) {
            if (t.kind().equals(kind)) {
                result.add(t);
            }
        }
        return result.toArray(new X10Tag[0]);
    }

    public static boolean isIncluded(String accessModFilter, ProgramElementDoc pd) {
        boolean isPublic = pd.isPublic();
        if (accessModFilter.equals("-public")) {
            return isPublic;
        }
        boolean isProtected = pd.isProtected();
        if (accessModFilter.equals("-protected")) {
            return (isPublic || isProtected);
        }
        boolean isPackage = pd.isPackagePrivate();
        if (accessModFilter.equals("-package")) {
            return (isPublic || isProtected || isPackage);
        }
        return true;
    }

    public static String rawCommentToText(String rawComment) {
        if (rawComment == null || rawComment.length() == 0) {
            return "";
        }
        assert (rawComment.startsWith("/**")) : "Non-javadoc comment: " + rawComment;
        assert (rawComment.endsWith("*/")) : "No comment terminator: " + rawComment;
        String result = rawComment;
        result = result.replaceFirst("^/\\*\\*\\s*", "");
        result = result.replaceFirst("\\s*\\*/$", "");
        result = result.replaceAll("\\n*\\s*(\\*)+\\s?", "\n");
        return result.trim();
    }

    private static final Map<String, Integer> flagsToHex = CollectionFactory.newHashMap();
    static {
        flagsToHex.put(Flags.PUBLIC.toString(), 0x0001);
        flagsToHex.put(Flags.PRIVATE.toString(), 0x0002);
        flagsToHex.put(Flags.PROTECTED.toString(), 0x0004);
        flagsToHex.put(Flags.STATIC.toString(), 0x0008);
        flagsToHex.put(Flags.FINAL.toString(), 0x0010);
        // flagsToHex.put(Flags.SYNCHRONIZED.toString(), 0x0020);
        flagsToHex.put(Flags.NATIVE.toString(), 0x0100);
        flagsToHex.put(Flags.ABSTRACT.toString(), 0x0400);
        // flagsToHex.put(Flags.STRICTFP.toString(), 0x0800);
    }

    public static int flagsToModifierSpecifier(Set<String> flags) {
        int r = 0;
        for (String flag : flags) {
            // flag could be "property" which is not in flagsToHex (and not
            // recognized by
            // the standard doclet)
            if (flagsToHex.containsKey(flag)) {
                r |= flagsToHex.get(flag);
            }
        }
        return r;
    }

}