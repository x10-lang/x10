/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 */

package x10.ast;

import polyglot.types.Name;

/**
 * A factored out list of standard operator names (unary and binary).
 */
public class OperatorNames {
    public static final Name PLUS =        Name.make("operator+");
    public static final Name MINUS =       Name.make("operator-");
    public static final Name STAR =        Name.make("operator*");
    public static final Name SLASH =       Name.make("operator/");
    public static final Name PERCENT =     Name.make("operator%");
    public static final Name LT =          Name.make("operator<");
    public static final Name GT =          Name.make("operator>");
    public static final Name LE =          Name.make("operator<=");
    public static final Name GE =          Name.make("operator>=");
    public static final Name LEFT =        Name.make("operator<<");
    public static final Name RIGHT =       Name.make("operator>>");
    public static final Name RRIGHT =      Name.make("operator>>>");
    public static final Name AMPERSAND =   Name.make("operator&");
    public static final Name BAR =         Name.make("operator|");
    public static final Name CARET =       Name.make("operator^");
    public static final Name TILDE =       Name.make("operator~");
    public static final Name NTILDE =      Name.make("operator!~");
    public static final Name AND =         Name.make("operator&&");
    public static final Name OR =          Name.make("operator||");
    public static final Name BANG =        Name.make("operator!");
    public static final Name EQ =          Name.make("operator==");
    public static final Name NE =          Name.make("operator!=");
    public static final Name RANGE =       Name.makeUnchecked("operator..");
    public static final Name ARROW =       Name.make("operator->");
    public static final Name LARROW =      Name.make("operator<-");
    public static final Name FUNNEL =      Name.make("operator-<");
    public static final Name LFUNNEL =     Name.make("operator>-");
    public static final Name DIAMOND =     Name.make("operator<>");
    public static final Name BOWTIE =      Name.make("operator><");
    public static final Name STARSTAR =    Name.make("operator**");
    public static final Name APPLY =       Name.make("operator()");
    public static final Name SET =         Name.make("operator()="); 
    public static final Name AS =          Name.makeUnchecked("operator_as");
    public static final Name IMPLICIT_AS = Name.makeUnchecked("implicit_operator_as");

    public static final String INVERSE_OPERATOR_PREFIX = "inverse_";
    public static Name inverse(Name name) {
        if (name == null) return null;
        return Name.makeUnchecked(INVERSE_OPERATOR_PREFIX + name.toString());
    }
    public static boolean is_inverse(Name name) {
        return name != null && name.toString().startsWith(INVERSE_OPERATOR_PREFIX);
    }
}
