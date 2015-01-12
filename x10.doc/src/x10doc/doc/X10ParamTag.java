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

import com.sun.javadoc.ParamTag;
import com.sun.javadoc.Tag;

public class X10ParamTag extends X10Tag implements ParamTag {
    private boolean isTypeParameter;
    private String paramName, paramComment;
    private X10Tag[] inTags;

    public X10ParamTag(boolean isTypeParameter, String name, X10Tag[] inTags, String paramComment, String text,
            X10Doc holder) {
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

    public Tag[] inlineTags() {
        return inTags;
    }
}
