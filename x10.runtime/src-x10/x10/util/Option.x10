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

package x10.util;

/**
 * This is a minimal set of properties for parameters specified in the command
 * line.  An option may have two keys.  The first is its short form, which has
 * a single leading '-'.  The other, the long form, starts with "--".  For
 * Unix systems this convention is well established, the alternate long form
 * being, we believe, started by GNU.
 * <p>
 * The term "flag" is often used for an option that stands alone, which, in
 * terms of Option's instance fields corresponds to minFollow==0 && !arbfollow.
 * That is the default: if you only provide the short and/or long forms of the
 * key and a description, a flag is what you get.  If <code>options</code>
 * is an instance of <code>OptionsParser</code>, and <code>flagName</code> is
 * either long or short key for a flag, then you can test the flag in either
 * of two ways: <code>options(flagName)</code> and
 * <code>options(flagName, false)</code> both return true if 
 * <code>flagName</code> appears as a key in the arguments array; otherwise,
 * both return false.
 * <p>
 * A non-flag option typically consumes exactly one argument immediately
 * following the option itself, ie. minFollow == 1 && !arbFollow.  That is
 * what you get when you call the constructor that also allows you to specify
 * "minFollow".  A call like <code>Option("-foo","","turn on foolery",3)</code>
 * will tell the parser to look for exactly 3 arguments following the key.
 * There is no implication that "foo" must appear, but if it does, it must
 * have 3 arguments.  If what you need is 3 OR MORE arguments, the call you
 * want is <code>Option("-foo","","turn on foolery",3,true)</code>.  Finally, if
 * the command line <i>must</i> specify values for the option "-foo", you need
 * one more argument: 
 *    <code>Option("-foo","","turn on foolery",3,true,true)</code>.
 * <p>
 * If you don't want both a short form and long form for the option key,
 * supply the empty string in the constructor calls for whichever you don't
 * need. For example, the calls for "-foo" in the previous paragraph all say
 * that there is no long form for that option.
 */
public struct Option {
    /** a string of the form "-key" that is the short name for the option */
    public val shortForm: String; 

    /** a string of the form "--key" that is the long name for the option */
    public val longForm: String;

    /** what you want to appear in help messages */
    public val description: String;

    /** 
     * the minimum number of arguments following the option that it consumes,
     * the default being 0.
     */ 
    public val minFollow: Int;

    /** 
     * if true, any number greater than the minimum may be consumed, but the
     * default is false.
     */
    public val arbFollow: Boolean;

    /**
     * if true, this option has values and must be specified in the command line.
     * Of course, flags are NEVER required.  The default is false--that is, 
     * unless you say otherwise, an option is, as the name suggests, optional.
     */
    public val required: Boolean;

    /**
     * if true, this option was added "on the fly" to the list of valid options,
     * rather than having been specified as part of the OptionsParser's initial
     * construction.  This is useful if you just want to ignore unknown option
     * keys, but still keep a record of what you've seen.
     */
    public val onTheFly: Boolean;

    /**
     * constructs an option that is a flag: no value following the option.  You
     * may omit the leading "-" from the short form for the key, or the "--"
     * from the long.
     * @param s the short form for the key
     * @param l the long form for the key
     * @param d the description.
     */
    public def this(s:String, l:String, d:String) {
        this(s,l,d,0n,false,false,false);
    }     

    /**
     * constructs an option that consumes a fixed number of following arguments
     * as values.
     * @param s the short form for the key
     * @param l the long form for the key
     * @param d the description
     * @param followers the number of values to expect
     */
    public def this(s:String, l:String, d:String, followers: Int) {
        this(s,l,d,followers,false,false,false);
    }

    /**
     * options consuming either a fixed or a variable number of arguments for
     * their values
     * @param s the short form for the key
     * @param l the long form for the key
     * @param d the description
     * @param followers the MINIMUM number of values to expect
     * @param mayBeMore if true, allow more than the minimum number of values
     */
    public def this(s:String, l:String, d:String, followers: Int, mayBeMore: Boolean) {
        this(s,l,d,followers,mayBeMore,false,false);
    }

    /**
     * options consuming either a fixed or a variable number of arguments for
     * their values and may be required to appear
     * @param s the short form for the key
     * @param l the long form for the key
     * @param d the description
     * @param followers the MINIMUM number of values to expect
     * @param mayBeMore if true, allow more than 'followers' arguments as values
     * @param mustSee if true, this option MUST be specified in the command line
     */
    public def this(s:String, l:String, d:String, followers:Int,
                    mayBeMore:Boolean, mustSee:Boolean) {
        this(s,l,d,followers,mayBeMore,mustSee,false);
    }

    /**
     * the most general form: options consuming either a fixed or a variable
     * number of arguments for their values and may be required to appear, but
     * may have been added "on the fly" because they were not specified before
     * the command line parse began.
     * @param s the short form for the key
     * @param l the long form for the key
     * @param d the description
     * @param followers the MINIMUM number of values to expect
     * @param mayBeMore if true, allow more than 'followers' arguments as values
     * @param mustSee if true, this option must be specified in the command line
     * @param otf if true, this is an option that was unexpected: it was added
     * 			"on the fly" by the OptionsParser.
     */
    public def this(s:String, l:String, d:String, followers:Int,
                    mayBeMore:Boolean, mustSee:Boolean, otf:Boolean) {
        shortForm   = s.length()==0n ? s : (s(0n)== '-' ? s : "-"+s); 
        longForm    = l.length()==0n ? l : (l.startsWith("--") ? l : "--"+l); 
        description = d;
        minFollow   = followers;
        arbFollow   = mayBeMore;
        required    = mustSee;
        onTheFly    = otf;
    }

    /**
     * converts the option, if need be, so that it is a flag (ie. minFollow==0, 
     * arbFollow==false), but is otherwise the same as <code>this</code>.
     * @param opt an Option that may or may not be a flag--we'll clone the
     * the fields other than minFollow and arbFollow if need be.
     * @return a flag with the given keys and description, etc.
     */
    public def asFlag(): Option {
        if (minFollow == 0n && !arbFollow) return this;
        else return Option(shortForm,longForm,description,0n,false,false,onTheFly);
    }

    /**
     * converts the option, if need be, so that is a simple parameter (ie.
     * minFollow==1, arbFollow==false), but that is otherwise the same as
     * <code>this</code>.
     * @param opt an Option that may or may not have minFollow and arbFollow
     * set correctly for a simple parameter--we'll get them right and clone the
     * rest of the fields.
     * @return a simple key/value Option with the given keys and description
     * that expects one command line argument to follow the key as its value.
     */
    public def asSimpleParm(): Option {
        if (minFollow == 1n && !arbFollow) return this;
        else {
            return Option(shortForm,longForm,description,1n,false,required,onTheFly);
        }
    }

    /**
     * meant not to error check, but simply to give you the long form if you
     * have the short, and vice-versa.
     * @param key either the short form or the long form of an option's keys
     * (including the leading '-' or '--').
     * @returns the other form for the key, null if the <code>key</code>
     * is neither form.
     */
    public def otherForm(key: String): String = 
        key.equals(shortForm) ? longForm : 
            (key.equals(longForm) ? shortForm : null);

    /**
     * string form for an <code>Option</code> aimed at code developers
     * @return a JSON style account of the fields
     */
    public def toString() = "{\n   short: \""+shortForm
    + "\",\n   long: \"" + longForm
    + "\",\n   minFollow: "+ minFollow
    + ",\n   arbFollow: "+ arbFollow
    + ",\n   required: "+ required
    + ",\n   description: \""+description+"\"\n}";

    /**
     * provides a user-friendly description of the option,
     * as opposed to toString(), which is developer-friendly.
     * @return a string to be added the command's help message
     */
    public def usage() {
        var keys: String = shortForm;
        if (longForm.length() > 0) {
            if (shortForm.length() > 0) keys += " or "+longForm;
            else keys = longForm;
        }
        var values: String = ": ";
        if (arbFollow) {
            values = " ("+minFollow+" or more values): ";
        }
        else if (minFollow == 1n) values = " ("+minFollow+" value): ";
        else if (minFollow > 1n) values = " ("+minFollow+" values): ";
        val req = required ? " (required)" : "";
        return keys+values+description+req;
    }

    /**
     * This is the value that the OptionsParser uses for the option that
     * says the caller just wants to see the correct usage for the command.
     */
    public static HELP = Option("?", "help", "displays this message and exits");

    /**
     * This is a value you can use if you need an Option that is definitely
     * illegal
     */
    public static BAD = Option("","","",0n,true,true);
}