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

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sun.javadoc.Doc;
import com.sun.javadoc.SourcePosition;
import com.sun.javadoc.Tag;

public class X10Tag implements Tag {
    public static final String DOCROOT = "docRoot";
    public static final String INHERITDOC = "inheritDoc";
    public static final String CODE = "code";
    public static final String LITERAL = "literal";
    public static final String LINK = "@link";
    public static final String LINKPLAIN = "@linkplain";
    public static final String SEE = "@see";

    public static final String TEXT = "Text";
    public static final String AUTHOR = "author";
    public static final String PARAM = "param";
    public static final String RETURN = "return";
    public static final String DEPRECATED = "deprecated";
    public static final String THROWS = "throws";

    public static final String GUARD = "guard";

    public static final ArrayList<String> inlineTagTypes = new ArrayList<String>();
    static {
        inlineTagTypes.add(DOCROOT);
        inlineTagTypes.add(INHERITDOC);
        inlineTagTypes.add(CODE);
        inlineTagTypes.add(LITERAL);
        inlineTagTypes.add(LINK);
        inlineTagTypes.add(LINKPLAIN);
    }

    String kind, name, text;
    final X10Doc holder;

    public X10Tag(String name, String text, X10Doc holder) {
        if (name.equals(DOCROOT) || name.equals(INHERITDOC)) {
            this.kind = this.name = name;
            this.text = "";
        } else if (name.equals(CODE) || name.equals(LITERAL) || name.equals(AUTHOR) || name.equals(PARAM)
                || name.equals(RETURN) || name.equals(DEPRECATED) || name.equals(THROWS) || name.equals(GUARD)) {
            this.kind = this.name = name;
            this.text = text;
        } else if (name.equals(LINK) || name.equals(LINKPLAIN) || name.equals(SEE)) {
            this.kind = SEE;
            this.name = name;
            this.text = text;
        } else {
            this.kind = this.name = TEXT;
            this.text = text;
        }
        this.holder = holder;

    }

    public void setText(String text) {
        this.text = text;
    }

    public Tag[] firstSentenceTags() {
        System.out.println("Tag.firstSentenceTags() ignored " + name + ", text: " + text);
        return new Tag[0];
    }

    public Doc holder() {
        // System.out.println("Tag.holder() called.");
        return this.holder;
    }

    public Tag[] inlineTags() {
        return X10Doc.createInlineTags(text, holder).toArray(new X10Tag[0]);
    }

    public String kind() {
        return kind;
    }

    public String name() {
        return name;
    }

    public SourcePosition position() {
        // TODO Auto-generated method stub
        // System.out.println("Tag.position() called.");
        return null;
    }

    public String text() {
        return this.text;
    }

    public String toString() {
        System.out.println("Tag.toString() called.");
        return (name + ":" + text);
    }

    public static X10Tag processInlineTag(String text, X10Doc holder) {
        // Pattern p =
        // Pattern.compile("\\{\\s*(@[^\\s\\}]+)\\s*([^\\}]*)\\s*}");
        Pattern p = Pattern.compile("\\{\\s*(@[^\\s]+)\\s*([^}]*)\\s*\\}");

        Matcher m = p.matcher(text);

        if (m.find()) {
            String name = m.group(1);
            String rest = m.group(2);
            // System.out.println("m.group() = \"" + m.group() + "\"");
            // System.out.println("tag = \"" + name + "\", rest = \"" + rest +
            // "\"");
            if (!inlineTagTypes.contains(name)) {
                return new X10Tag(TEXT, text, holder);

            }
            if (name.equals(LINK) || name.equals(LINKPLAIN)) {
                return new X10SeeTag(name, rest, text, holder);
            }

            else {
                return new X10Tag(name, text, holder);
            }
        }
        return new X10Tag(TEXT, text, holder);
    }

}
