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

package x10.util;

import x10.compiler.NonEscaping;

/**
 * This parses a reasonably general Unix style of command line argument arrays. 
 * Non-numeric entries that begin with "-" are treated as keys.  The remaining
 * arguments are values whose handling depends on what information is supplied
 * about the valid keys.  The information about a key is supplied as an
 * {@link Option}. There are a variety of constructors that go the spectrum
 * from passing just the argument array to passing the arguments, the options
 * and handlers for any error conditions where the defaults are not what is
 * needed.
 * <p>
 * A common convention that is supported is that one character flags may be
 * grouped, as in  Unix's "<code>ps -aef</code>", which is a valid short-hand
 * "<code>ps -a -e -f</code>.  The downside is if "<code>-aef</code>" is a
 * key as well as "<code>-a</code>", "<code>-e</code>" and "<code>-f</code>",
 * the parser has to decide whether you really meant "<code>-aef</code>" or
 * the three flags.  The answer is that "<code>-aef</code>" wins. If you
 * use "<code>--aef</code>" instead of "<code>-aef</code>", the problem goes
 * away.
 * <p>
 * "<code>-key=value</code>" is treated the same as "<code>-key value</code>",
 * e.g. "<code>-cp=../bin/:/lib/...</code>" for specifying a "class path".  
 * This means that "<code>=</code>" cannot appear in a key.
 * <p>
 * Parsing stops at the first argument that is neither a valid key nor a 
 * value associated with a valid key.  The remaining arguments form the "tail"
 * of the command line, and can be accessed by the methods {@link hasTail} and
 * {@link getTail}.  One can also use "<code>--</code>" by itself as an
 * argument to mark the dividing line between the head and the tail.
 * arguments that belong to the command being invoked.
 * <p>
 * Another well-established convention is using the keys "<code>-?</code>"
 * and "<code>--help</code>" to request an explanation of the command's syntax.
 * The method {@link wantsUsageOnly}, together with the pre-packaged
 * <code>Option</code> "<code>HELP</code>", make this easy for an application
 * to honor.
 * <p>
 * When handed a string <code>s</code> and asked to look
 * it up as an option key, the drill is to see whether it begins with
 * <code>'-'</code>.  If it does, it is looked up "as is".  If it does not,
 * both <code>"-"+s</code> and <code>"--"+s</code> are tried (in that order).
 * <p>
 * No processing is done to the strings that make up an option's value until
 * a request is made to get the value.  Although the vast majority of keys
 * consume either no or one argument for their value, a key, in general,
 * may consume any number of arguments.  Regardless of how many values are 
 * found, the set is kept as an array.  In the case where there is exactly
 * one value, we provide <code>operator</code> methods with syntax 
 * <code>this(key, defaultValue)</code> for getting the value converted from
 * <code>String</code> to the type of <code>defaultValue</code>, which may
 * be any of the numeric types, <code>Boolean</code, or <code>String</code>. 
 * Numeric values follow the usual rules: leading 0x for hex, leading
 * 0 for octal, anything else is decimal.  For Booleans, we allow all of the
 * strings recognized by {@link StringUtil.checkBoolean}.  In the rare case
 * where there may be more than one value, call either {@link get} or
 * {@link getOrElse}.
 * <p>
 * Finally, a few words about exception handling: there are a number of 
 * problems that can occur, and the instance variables named "<code>on...</code>" 
 * hold closures that deal with them.  There are default actions, of course,
 * and one form of the constructor allows you to override some or all of them.
 *
 * @author Jonathan Brezin
 * @author Dave Cunningham
 */ 
public class OptionsParser {
    /**
     * provides a unique subclass for sginalling errors parsing the command line
     * and requesting "the" value of an option when more than one is provided.
     */
    public static final class Err extends Exception {
        public def this (m:String) { super(m); }
    }

    /** maps a key to its array of values */
    public static type OptionsMap = HashMap[String, Rail[String]];

    //
    //======================Error Handler Types=============================
    //
    /**
     * what to do if the key given by its first argument appears to be followed
     * by too many values.  The second argument is the number of arguments we
     * expected to use, and the third argument is the number we actually have
     * available.  The return value is the number of value arguments that
     * should be consumed.
     */
    public static type TooManyHandler = (String, Int, Int) => Int;

    /**
     * what to do if the key given by its first argument is not followed by
     * enough arguments to complete its value--typically expects 1 but sees
     * none.  The second argument is the number expected, and the third
     * argument is the number actually available.  There is normally little
     * to do here other than signal an error, but we allow you to be the
     * judge.  The return value is the array of Strings to use as the value.
     */
    public static type TooFewHandler = (String, Int, Int)=>Rail[String];

    /**
     * what to do if the same key appears more than once.  The first argument
     * is the key, the second is map that assigns each key its value array,
     * and the third argument is the (expanded) args array.  The final two
     * arguments delimit the value arguments for the instance of the key that
     * caused this handler to be called: the first Int is the location of the
     * first value, and the second is the number of arguments that make up the
     * value (normally 1). There are basically four choices: first wins (the
     * handler is a no-op), last wins (replace the key's value array with the
     * new one), combine the two (new value array is the concatenation of the
     * current one with new one), and finally, throw an exception, particularly
     * if the two occurences of the keys have different values and there is no
     * reason to prefer one over the other.
     */
    public static type DupHandler = (String, OptionsMap, Rail[String], Int, Int)=>void;

    /**
     * what to do if you ask for a key's value, expecting that its value is 
     * either true or false (it's a flag) or is the argument immediately
     * following the key, only to discover that there is more than one
     * entry in the key's value array.  Normally, there is little to do other
     * than signal an error, but you could return either the first or last
     * entry in the array, or the concatenation of the entries (with a space 
     * character between each?).  The return value should be the String
     * representation of the value to use.
     */
    public static type MoreThanOneHandler = (String, String, Rail[String]) => String;

    /**
     * IGNORE and START_TAIL are the two things you can do when you hit an
     * unrecognized key that is not known to be in the tail.  Unknown key
     * handlers are assumed to return one of these two values.  IGNORE
     * means: don't assume this is part of the tail...keep looking for 
     * more keys.
     */
    public static IGNORE = 0n;

    /** assume that the tail begins at this key, if not before */
    public static START_TAIL = 1n;

    /** 
     * what to do if we encounter a key that is not in the options set.
     * The first argument is the key, the second the key:option map.
     * The return value is one of the two constants above.
     */
    public static type UnknownKeyHandler = (String, HashMap[String, Option])=>Int;

    //
    //======================Error Handler Instance Variables================
    //
    /**
     * this handles the event in which too few values are provided for a key.
     * It is a function of three arguments: the key itself, how many arguments
     * were expected, and how many were actually found.  Nota bene: we are 
     * talking about parsing the command line and not finding a value.  We
     * are NOT talking about asking, once the command line has been read,
     * what value, if any, was supplied for a given option.
     */
    public var onTooFew: TooFewHandler = 
        (key: String, expected: Int, got: Int) => {
           if(key.length()>0) {
                throw new Err("Missing arguments for "+key+": "+expected+" expected, got "+got);
           }
           return new Rail[String](0);
        };

    /**
     * this handles the problem of what to do when too many values are provided
     * for a key.  The default action is just to use the number expected and
     * ignore the rest.  If you want different behavior, you need to supply a
     * function of three arguments: the key itself, how many arguments were
     * expected, and how many were actually found. It should return the number
     * to use.
     */
    public var onTooMany: TooManyHandler = 
        (key: String, expected: Int, got: Int): Int => expected;

    /**
     * this handles the problem of what to do when the same key (or the short
     * and long forms of the same key) appear multiple times in a single command.
     * The default is that the last wins.
     */
    public var onDuplicate: DupHandler = 
        (key:String, props:OptionsMap, args:Rail[String], first:Int, values:Int) => { 
	        val init = (n: Long) => args(first + (n as Int));
	        val valueArray = new Rail[String](values, init);
	        Console.ERR.println("Duplicated "+key+", It's value now: "+valueArray(0));
        	props.put(key, valueArray);      
        };

    /**
     * this handles the problem of what to do when the application asks for the
     * value of an option, expecting that exactly none or one value will be
     * provided.  The default is to throw an exception.  
     */
    public var onMoreThanOne: MoreThanOneHandler =
        (key:String, orElse: String, values: Rail[String]): String => {
            throw new Err("request of value for ("+key+", "+
                    orElse+") failed: values == "+values); 
        };

    /**
     * use this as the value for onMoreThanOne if you want the first value in
     * the values array when there is more than one.
     */
    public static firstOnMoreThanOne = 
        (key:String, orElse:String, values:Rail[String]): String => values(0);

    /**
     * use this as the value for onMoreThanOne if you want the last value in
     * the values array when there is more than one.
     */
    public static lastOnMoreThanOne = 
        (key:String, orElse:String, values:Rail[String]): String => 
    		values(values.size-1);

    /**
     * this handles the problem of what to do if an unexpected key is found while
     * parsing the command line.  The default action on reading an unrecognized
     * key is to add it as a flag and mark it as unexpected.  This means that
     * the tail will only be correctly recognized as such if either the unknown
     * key is not the last key, or the unexpected key really is a flag, or you
     * explicitly insert "--" to separate the tail.
     */
    public var onUnknownKey: UnknownKeyHandler = 
        (key: String, keyMap: HashMap[String, Option]) => {
            Console.ERR.println("Unknown key, '"+key+"' will be treated as flag.");
            var shortKey: String = "", longKey: String = "";
            if (key.indexOf("--")==0n) longKey = key;
            else if (key.indexOf("-") == 0n) shortKey = key;
            else { longKey = "--"+key; shortKey = "-"+key; }
            if (shortKey.length() > 0 || longKey.length() > 0) {
                val added = new Option(shortKey, longKey, "???", 0n, true, false, true);
                if (shortKey.length() > 0 ) keyMap.put(shortKey, added);
                if (longKey.length() > 0 ) keyMap.put(longKey, added);
            }
            return IGNORE;
        };

    /** the other common thing to do on seeing an unknown key is to start the tail */
    public static startTailOnUnknownKey =
        (key: String, keyMap: HashMap[String, Option]) => START_TAIL;
        
    @NonEscaping
    protected final def setHandlers(hndlrs: Rail[Any]) {
       for(n in hndlrs.range()) {
           val hndlr = hndlrs(n);
           if (hndlr instanceof TooManyHandler) onTooMany = hndlr as TooManyHandler;
           else if (hndlr instanceof TooFewHandler) onTooFew = hndlr as TooFewHandler;
           else if (hndlr instanceof DupHandler) onDuplicate = hndlr as DupHandler;
           else if (hndlr instanceof MoreThanOneHandler) onMoreThanOne = hndlr as MoreThanOneHandler;
           else if (hndlr instanceof UnknownKeyHandler) onUnknownKey = hndlr as UnknownKeyHandler;
       }
    }

    //
    //======================= Protected Instance Members =======================
    //
    protected var options: Rail[Option];       // our copy of the Options to look for
    protected var keyMap: HashMap[String, Option]; // maps keys to Options
    protected var args: Rail[String] = null;   // expanded copy: look at expandArgs
    protected var properties: OptionsMap = null;   // maps key to value array
    protected var tailStart: Int = -1n;             // index into args of first tail argument
    protected var required: HashSet[Option];       // keys for a which a value is required
    protected var oneCharFlags: HashSet[String];   // keys that are one character long and
    											   // are used as flags
    //
    //======================= Constructors and Initialization =======================
    //
    /**
     * to be used when you don't want to supply your own options array: we treat
     * all "-" and "--" prefixed args as option keys, and starts the tail after
     * "--", if it appears by itself this way.  All option keys are allowed to
     * be followed by 0 or more values.  Keys are assumed not to contain 
     * '=' and not to begin with a number.
     * @param args the command line argument array to be parsed
     */
    public def this(args: Rail[String]) {
        parse(args, makeOptionArray(args));
    }
    protected static def makeOptionArray(args: Rail[String]) {
        val builder = new RailBuilder[Option](args.size);
        val lastArg = args.size - 1;
        for(n in 0..lastArg) {
            var argN: String = args(n);
            if(argN(0n) == '-') {
                if(argN.length() > 1 && argN(1n).isDigit()) continue;
                else if (argN.equals("--")) break; // no keys for this cmd follow
                val firstEq = argN.indexOf("=");   // allow key=value syntax
                if (firstEq > 0n) argN = argN.substring(0n, firstEq);
                if (argN(1n)=='-') { // key is a "long form"
                    builder.add(Option("",argN,"no description given", 0n, true));
                }
                else builder.add(Option(argN,"","no description given", 0n, true));
            }
        }
        return builder.result();
    }

    /**
     * This is one of the two constructors that one normally calls.  You supply
     * a single, complete list of valid options and parse the args array against
     * that list.
     * @param options the whole list of valid options
     * @param args the command line argument array to be parsed
     */
    public def this(args: Rail[String], options: Rail[Option]) {
        parse(args, new Rail[Option](options));
    }

    /**
     * This is the second of the constructors that one normally calls.  It is
     * for the common case in which there are two sets of options: flags
     * (that is no value to follow) and simple parameters where the key
     * is followed by exactly one value.
     * <p>
     * This call allows you to write the flags and simple parms as two separate
     * arrays.  You may specify both the flags <i>and</i> the parms as
     * <code>Option(shortKey, longKey, description)</code>.  All of the fields
     * in the <code>Option</code>, except those dealing with how many arguments
     * it consumes, are honored "as is".
     * @param args: the command line arguments array
     * @param flags: the array of <code>Option</code>s for the flag keys
     * @param others: the array of <code>Option</code>s for the simple
     * parameter keys
     */
    public def this(args: Rail[String], flags: Rail[Option], others: Rail[Option]) {
        parse(args, merge(flags, others));
    }
    protected static def merge(flags: Rail[Option], parms: Rail[Option]) {
        val builder = new RailBuilder[Option](flags.size + parms.size);
        for(m in flags.range()) builder.add(flags(m).asFlag());
        for(n in parms.range()) builder.add(parms(n).asSimpleParm());
        return builder.result();
    }

    /**
     * In those (rare, we hope) occasions when the default error handlers are
     * not what you need, this constructor is designed to give you a way
     * to provide an array of the handlers you want.  The array entries are
     * closures of one of the five handler types: TooManyHandler, etc.
     * Here is an example of the intended use where you want to signal an
     * error when there are too many values for an option and start the
     * tail on or before the first unrecognized key:<pre>
     *    static ourTooMany: TooManyHandler = 
     *       (key: String, expected: Int, got: Int): Int => {
     *          throw new Err("Too many arguments for "+key+": "+expected+
     *                       " expected, got "+got);
     *    };
     *    static val HANDLERS = [ourTooMany,OptionsParser.startTailOnUnknownKey];
     *    this(args, OPTIONS, HANDLERS);
     * </pre>
     * @param optionsArray the whole list of valid options
     * @param args the command line argument array to be parsed
     * @param handlers the closures to use as handlers.
     */
    public def this(args: Rail[String], options: Rail[Option], handlers: Rail[Any]) {
        setHandlers(handlers);
        parse(args, new Rail[Option](options));
    }

    //
    //=============== private methods used by the constructor ==================
    //
    @NonEscaping
    private def parse(args: Rail[String], options: Rail[Option]) {
        if (properties != null) {
            Console.ERR.println("Command line already parsed!");
            return;
        }
        this.options = options;
        initializeKeyMap(options);
        val keyIndex = makeKeyIndex(args, options); // n-th entry is index of n-th key
        val props = new OptionsMap();               // maps keys to their value arrays
        var keyInArgs: Rail[Int] = keyIndex;        // may need to compress it a bit
        var newTailStart: Int = 0n;                  // we'll use options to get a better value
        var lastIndex: Int = keyInArgs.size as Int -2n;      // may get smaller if we compress
        this.properties = props;
        for(var k:Int =0n; k<=lastIndex; k++) { // find the values (if any) for each key
            val key = this.args(keyInArgs(k));
            val option = keyMap.getOrElse(key, Option.BAD);
            if (option == Option.BAD) break;
            val first = keyInArgs(k)+1n;
            var valueCount: Int = keyInArgs(k+1n) - first;
            val expected = option.minFollow;
            var values: Rail[String] = null;
            if(expected > valueCount) {
                values = onTooFew(key, expected, valueCount);
            }
            else if (expected < valueCount) {
                if (!option.arbFollow) { // start the tail, compress the key index
                    valueCount = onTooMany(key, expected, valueCount);
                    val newTail = first + valueCount;
                    val thisK = lastIndex = k; 
                    val init = (n:Long)=>(n<=thisK ? keyIndex(n) : newTail);
                    keyInArgs = new Rail[Int](k+2n, init);
                    // setting lastIndex above means the loop will terminate
                    // on the next iteration.  We don't break out here because
                    // we still need to update "properties" for this key.
                }
            }
            newTailStart = first+valueCount;
            if (properties.containsKey(key)) {
                if (valueCount > 0n) {
                    onDuplicate(key, props, this.args, first, valueCount);
                }
            }
            else {
                val whatToPut: Rail[String];
                if (valueCount == 0n) {
                    if (option.arbFollow) {
                        whatToPut = new Rail[String]();
                    }
                    else whatToPut = new Rail[String](1, "true"); // it is a flag!
                }
                else if (values != null) whatToPut = values;
                else {
                    val thisArgs = this.args;
                    val init = (n: Long) => thisArgs(first + n);
                    whatToPut = new Rail[String](valueCount, init);
                }
                properties.put(key, whatToPut);
                val otherForm = option.otherForm(key);
                if(otherForm.length()>0n) properties.put(otherForm, whatToPut);
            }
        }
        this.tailStart = keyInArgs(keyInArgs.size-1) = newTailStart;
    }

    @NonEscaping
    private def initializeKeyMap(options: Rail[Option]) {
        val km = new HashMap[String, Option]((options.size*2) as Int);
        val req = new HashSet[Option](options.size as Int);
        val ocf = new HashSet[String](options.size as Int);
        for(ox in options.range()) {
            val o = options(ox);
            if (o.shortForm.length() > 0) {
                km.put(o.shortForm, o);
                if (o.shortForm.length() == 2n && o.minFollow == 0n && !o.arbFollow) {
                    ocf.add(o.shortForm); 
                }               
            }
            if (o.longForm.length() > 0n) km.put(o.longForm, o);
            if (o.required) req.add(o);
        }
        keyMap = km;
        required = req;
        oneCharFlags = ocf;
    }

    // make a run over the actual args to find the location of the keys.
    // Computes two indices: the keyIndex, which is a table of contents
    // for args whose n-th entry is the location of the n-th key, and
    // the expanded args array, which takes care of expanding multi-flag
    // keys like "ps -aef" into [-a, -e, -f] and key=value entries into
    // [key, value].
    @NonEscaping 
    private def makeKeyIndex(args:Rail[String], options:Rail[Option]) {
        val keyIndex = new RailBuilder[Int](args.size);
        var keyCount: Int = 0n;
        var helpOnly: Boolean = false;
        val expanded = expandArgs(args);
        var tailStart: Int = expanded.size as Int;
        val lastArg: Int = expanded.size as Int - 1n;
        val reqNotSeen = required.clone();
        for(i in 0n .. lastArg) {
            val s = expanded(i);
            if (s.length() > 0n && s(0n) == '-') {
                if (s.length() > 1 && s(1n).isDigit()) continue;
                else if (keyMap.containsKey(s)) {
                    keyIndex.add(i);
                    val optForS = keyMap.getOrElse(s, Option.BAD);
                    if (optForS.required) reqNotSeen.remove(optForS);
                    else if (optForS == Option.HELP) {
                        helpOnly = true;
                    }
                }
                else if (s.equals("--") || onUnknownKey(s, keyMap) == START_TAIL) {
                    tailStart = i; break;                  
                }
                else keyIndex.add(i); // may be unknown, but is not previous key's value
             }
        }
        keyIndex.add(tailStart); // a sentinel: may be a bit low, we'll reset later
        val howManyMissing = reqNotSeen.size();
        if (helpOnly || howManyMissing == 0) {
            this.args = expanded;
            return keyIndex.result();
        }
        else {
            val sb = new StringBuilder();
            sb.add("The following ");
            if (howManyMissing == 1) sb.add("option is missing:\n\t");
            else sb.add(howManyMissing+" options are missing\n\t");
            for(o in reqNotSeen) {
                sb.add(o.usage());
                sb.add("\n\t");
            }
            throw new Err(sb.result()+"\n");
        }
    }

    // We expand arguments like "-aef" that are recognized as being a collection
    // flags.  We also break into two parts arguments that look like "key=value".
    // We stop expanding at the first argument that begins with "-" and is 
    // neither a number nor a key (even after trying to expand the argument).
    // The expanded array is assigned here to this.args.
    @NonEscaping 
    private def expandArgs(args: Rail[String]): Rail[String] {
        val builder = new RailBuilder[String](Math.min(8,2*args.size));
        var inTail: Boolean = false; // we have seen a key that is not recognized
        for(n in args.range()) {
            val argN = args(n);
            if (argN.length() == 0n || argN(0n) != '-' || inTail) builder.add(argN);
            else if (argN.equals("--")) { inTail = true; builder.add(argN); }
            else if (argN.length() < 2n) builder.add(argN);
            else if(argN(1n).isDigit()) builder.add(argN);
            else if (keyMap.containsKey(argN)) builder.add(argN);
            else { // expand one-char flags and key=value args
                var isFlags: Boolean = true;
                val count = argN.length() - 1n;
                val exploded = new Rail[String](count, (k:Long)=>"-"+argN((k as Int)+1n));
                for(k in exploded.range()) { // is the arg a concatenation of one char flags?
                    if (!oneCharFlags.contains(exploded(k))) {
                        isFlags = false; break; // No! It is not!
                    }
                }
                if (isFlags) for(k in exploded.range()) builder.add(exploded(k));
                else { // maybe it has the form -key=value:
                    val firstEq = argN.indexOf("=");
                    if (firstEq > 0n && keyMap.containsKey(argN.substring(0n,firstEq))) {
                        builder.add(argN.substring(0n, firstEq));
                        builder.add(argN.substring(firstEq+1n));
                    }
                    else { // the key is not one of ours
                        builder.add(argN);
                        try { 
                            val where = onUnknownKey(argN, keyMap);
                            if (where == START_TAIL) inTail = true;
                        }
                        catch(e: Err) {}
                   }
                }
            }
        }
        return this.args = builder.result();
    }

    //
    //============================= Getting Values =============================
    //
    /**
     * creates and returns a copy of the tail array
     * @return a possibly empty array of Strings
     */
    public def getTail() {
        if (tailStart >= args.size) return new Rail[String](0, "");
        val first = args(tailStart).equals("--") ? tailStart+1n : tailStart;
        val init = (n:Long)=>args(n+first);
        return new Rail[String](args.size-first, init);
    }

    /**
     * returns a Boolean: true means the tail array has positive size
     * @return true if the args array has non-empty tail, false otherwise.
     */
    public def hasTail() = (tailStart < args.size);
    
    ////////////////////////////////////////////////////////////////////////
    //
    // The operator methods that follow are non-escaping so that subclasses
    // can use them in their constructors to initialize variables that hold
    // command-line parameters.  The same is true of the two lower-level
    // methods get() and getOrElse() on which the operators depend. 
    //
    // The idea is to a allow an application specific Main class that
    // extends OptionsParser and contains instance variables that can
    // be initialized from the command line.  For example:
    //      val: srcPath: String;
    //      public def this(args: Rail[String], opts: Rail[Option]) {
    //         super(args, opts);
    //         srcPath = this("src", ".");
    //         ...
    //      }
    //
    ////////////////////////////////////////////////////////////////////////
    
    /**
     * returns an array of the values saved for a given key, which are the
     * arguments following the option up to either the next option, the
     * number of values expected has been seen, the beginning of the tail
     * is reached, or the end of the array is reached.
     * @param key a String that names an option that might possibly appear
     *    in the command line.
     * @return a possibly empty array of Strings if the option is a valid one;
     *   null otherwise.
     */
    @NonEscaping 
    public final def get(val key: String): Rail[String] {
        var winner: String = key;
        var isValid: Boolean = true;
        if (key(0n) == '-') {
            if (keyMap.getOrElse(key, Option.BAD) == Option.BAD) isValid = false;  
        }
        else { // add dashes as needed in front of key to get a string we recognize
            winner =  "-"+key;  
            if (keyMap.getOrElse(winner, Option.BAD) == Option.BAD) { 
                winner = "--"+key;
                if (keyMap.getOrElse(winner, Option.BAD) == Option.BAD) isValid = false;   
            }
        }
        if (isValid) return properties.getOrElse(winner, new Rail[String](0));
        else {
            onUnknownKey(key, keyMap);
            return null;
        }
    }
    /**
     * returns the array of Strings that are "key"'s value, or "orElse" if
     * "key" is not present in the command line.
     */
    @NonEscaping 
    public final def getOrElse(var key: String, orElse: Rail[String]): Rail[String] {
        val found = get(key);
        return found == null || found.size == 0 ? orElse : found;
    }
    
    /**
     * looks up the number of values for the option named by "key"
     * @param key a String that is a possible option name.
     * @return the number of values found, -1 if the option is not a valid one.
     * @see #OptionParser.get(String)
     */
    public def howManyValues(key: String) {
        val values = get(key);
        return values == null ? -1 : (values.size as Int);
    }
    
    /**
     * returns true if the key is among the options found, false otherwise.
     * @param key the String that possibly names an option in the command
     * @return true if the key is an option found in the command line.
     */
    @NonEscaping public final operator this(key:String): Boolean {
        if (key(0n) == '-') {
            if (keyMap.getOrElse(key, Option.BAD) == Option.BAD) return false;
            else return properties.containsKey(key);
        }
        else { // add dashes as needed in front of key to get a string we recognize
            var winner: String =  "-"+key;  
            if (keyMap.getOrElse(winner, Option.BAD) == Option.BAD) { 
                winner = "--"+key;
                if (keyMap.getOrElse(winner, Option.BAD) == Option.BAD) return false;   
            }
            return properties.containsKey(winner);
        }
    }

    /**
     * for options that are required to have exactly one value, return the value
     * if the option is present, and if not (or if the key is not recognized),
     * return the default value provided.
     * @param key a String naming a key that might appear in the args array
     * @param d a String to be used as the default value
     * @return the value String found.
     * @throws OptionParser.Err if the option is present and there is not precisely
     *    one value.
     */
    @NonEscaping public final operator this(key:String, d:String): String {
        val raw = get(key);
        if(raw == null || raw.size == 0) return d; 
        else if (raw.size == 1) return raw(0);
        else return onMoreThanOne(key, d, raw);
    }

    /**
     * for options that must have a Byte as value, return that value if the
     * option is present, and if it is not, return the default provided here.
     * @param key a String naming a key that might appear in the args array
     * @param d a Byte to be used as the default value 
     * @return the value as a Byte
     * @throws OptionParser.Err if the option is present and there is not precisely
     *    one value, or is not convertible to Byte.
     */
    @NonEscaping public final operator this(key:String, d:Byte):Byte { 
        val v = this(key, "");
        if (v.length() == 0n) return d;
        try { return StringUtil.parseByte(v); }
        catch (e:NumberFormatException) {
            throw new Err("Expected a byte, got: \""+v+"\"");
        }
    }

    /**
     * for options that must have a Short as value, return that value if the
     * option is present, and if it is not, return the default provided here.
     * @param key a String naming a key that might appear in the args array
     * @param d a Short to be used as the default value 
     * @return the value as a Short
     * @throws OptionParser.Err if the option is present and there is not precisely
     *    one value, or is not convertible to Short.
     */
    @NonEscaping public final operator this (key:String, d:Short):Short { 
        val v = this(key, "");
        if (v.length() == 0n) return d;
        try { return StringUtil.parseShort(v); } 
        catch (e:NumberFormatException) {
            throw new Err("Expected a Short, got: \""+v+"\"");
        }
    }

    /**
     * for options that must have a Int as value, return that value if the
     * option is present, and if it is not, return the default provided here.
     * @param key a String naming a key that might appear in the args array
     * @param d a Int to be used as the default value 
     * @return the value as a Int
     * @throws OptionParser.Err if the option is present and there is not precisely
     *    one value, or is not convertible to Int.
     */
    @NonEscaping public final operator this(key:String, d:Int): Int { 
        val v = this(key, "");
        if (v.length() == 0n) return d;
        try { return StringUtil.parseInt(v); } 
        catch (e:NumberFormatException) {
            throw new Err("Expected an Int, got: \""+v+"\"");
        }
    }

    /**
     * for options that must have a Long as value, return that value if the
     * option is present, and if it is not, return the default provided here.
     * @param key a String naming a key that might appear in the args array
     * @param d a Long to be used as the default value 
     * @return the value as a Long
     * @throws OptionParser.Err if the option is present and there is not precisely
     *    one value, or is not convertible to Long.
     */
    @NonEscaping public final operator this(key:String, d:Long): Long {
        val v = this(key, "");
        if (v.length() == 0n) return d;
        try { return StringUtil.parseLong(v); }
        catch (e:NumberFormatException) {
            throw new Err("Expected a Long, got: \""+v+"\"");
        }
    }

    /**
     * for options that must have a UByte as value, return that value if the
     * option is present, and if it is not, return the default provided here.
     * @param key a String naming a key that might appear in the args array
     * @param d a UByte to be used as the default value 
     * @return the value as a UByte
     * @throws OptionParser.Err if the option is present and there is not precisely
     *    one value, or is not convertible to UByte.
     */
    @NonEscaping public final operator this(key:String, d:UByte):UByte { 
        val v = this(key, "");
        if (v.length() == 0n) return d;
        try { return StringUtil.parseUByte(v); }
        catch (e:NumberFormatException) {
            throw new Err("Expected a UByte, got: \""+v+"\"");
        }
    }

    /**
     * for options that must have a   as value, return that value if the
     * option is present, and if it is not, return the default provided here.
     * @param key a String naming a key that might appear in the args array
     * @param d a UShort to be used as the default value 
     * @return the value as a UShort
     * @throws OptionParser.Err if the option is present and there is not precisely
     *    one value, or is not convertible to UShort.
     */
    @NonEscaping public final operator this (key:String, d:UShort):UShort { //throws Err {
        val v = this(key, "");
        if (v.length() == 0n) return d;
        try { return StringUtil.parseUShort(v); }
        catch (e:NumberFormatException) {
            throw new Err("Expected a UShort, got: \""+v+"\"");
        }
    }

    /**
     * for options that must have a UInt as value, return that value if the
     * option is present, and if it is not, return the default provided here.
     * @param key a String naming a key that might appear in the args array
     * @param d a UInt to be used as the default value 
     * @return the value as a UInt
     * @throws OptionParser.Err if the option is present and there is not precisely
     *    one value, or is not convertible to UInt.
     */
    @NonEscaping public final operator this(key:String, d:UInt): UInt { 
        val v = this(key, "");
        if (v.length() == 0n) return d;
        try { return StringUtil.parseUInt(v); }
        catch (e:NumberFormatException) {
            throw new Err("Expected a UInt, got: \""+v+"\"");
        }
    }

    /**
     * for options that must have a ULong as value, return that value if the
     * option is present, and if it is not, return the default provided here.
     * @param key a String naming a key that might appear in the args array
     * @param d a ULong to be used as the default value 
     * @return the value as a ULong
     * @throws OptionParser.Err if the option is present and there is not precisely
     *    one value, or is not convertible to ULong.
     */
    @NonEscaping public final operator this(key:String, d:ULong): ULong {
        val v = this(key, "");
        if (v.length() == 0n) return d;
        try { return StringUtil.parseULong(v); }
        catch (e:NumberFormatException) {
            throw new Err("Expected a ULong, got: \""+v+"\"");
        }
    }

    /**
     * for options that must have a Double as value, return that value if the
     * option is present, and if it is not, return the default provided here.
     * @param key a String naming a key that might appear in the args array
     * @param d a Double to be used as the default value 
     * @return the value as a Double
     * @throws OptionParser.Err if the option is present and there is not precisely
     *    one value, or is not convertible to Double.
     */
    @NonEscaping public final operator this(key:String, d:Double):Double {
        val v = this(key, "");
        //Console.OUT.println("v is '"+v+"' for '"+key+"'");
        if (v.length() == 0n) return d;
        try { return Double.parse(v); }
        catch (e:NumberFormatException) {
            throw new Err("Expected a Double, got: \""+v+"\"");
        }
    }

    /**
     * for options that must have a Float as value, return that value if the
     * option is present, and if it is not, return the default provided here.
     * @param key a String naming a key that might appear in the args array
     * @param d a Float to be used as the default value 
     * @return the value as a Float
     * @throws OptionParser.Err if the option is present and there is not precisely
     *    one value, or is not convertible to Float.
     */
    @NonEscaping public final operator this(key:String, d:Float): Float { 
        val v =this(key, "");
        if (v.length() == 0n) return d;
        try { return Float.parse(v); }
        catch (e:NumberFormatException) {
            throw new Err("Expected a Float, got: \""+v+"\"");
        }
    }

    /**
     * This method can be used for both flags and for options that require a 
     * single value. 
     * <p>
     * If the option, if present, must have a value, return that
     * value translated to a Boolean.  If it is not present, return the default
     * value provided as the second argument.  We look for the usual string
     * representations of a Boolean: "true", "yes", "ok", "1" etc for true,
     * and "no", "false", "0" for false.  See <link>StringUtil</link>
     * <p>
     * If the key names a flag (ie no value expected) and the key is present
     * in the args, true will be returned.  If the flag is NOT present, the
     * default argument's value (which in this case should be the Boolean
     * false) will be returned.
     * @param key a String naming a key that might appear in the args array
     * @param d a Boolean to be used as the default value 
     * @return the value, if present, as a Boolean; otherwise return the default.
     * @throws OptionParser.Err if the option is present and there is not precisely
     *    one value, or is not convertible to a Boolean.
     */
    @NonEscaping public final operator this(key:String, d:Boolean): Boolean { 
        val v = this(key, "");
        return v.length() == 0n ? d : StringUtil.checkBoolean(v);      
    }

    /**
     * bundles the option descriptions, one per line indented a few spaces,
     * and returns the leader concatenated with those lines and the trailer,
     * all followed by a newline.
     */
    public def usage(leader: String) {
        val details = new StringBuilder();
        details.add(leader); 
        for(n in options.range()) {
            details.add("   ");
            details.add(options(n).usage());
            details.add("\n");
        }
        details.add("\n");
        return details.result();
    }
    public static HELP = Option.HELP;    
    public def showHELP() {
        Console.ERR.println("Use "+HELP.shortForm+" or "
                + HELP.longForm+" to see the correct usage.");
    }

    /**
     * Implements the convention that "?" and "help" are just requesting
     * information about the command and its options.  The usage is
     * written to the standard error stream.
     * @returns true if either of the two keys is found, false otherwise.
     */
    public def wantsUsageOnly(leader: String) {
        if ( this(HELP.shortForm) || this(HELP.longForm) ) {
            Console.ERR.print(usage(leader));
            return true;
        }
        else return false;
    }

    /**
     * filters out the option keys that were unexpected, but were found
     * "on the fly"while reading the args array.
     * @return an array (normally size 0) of the keys found.
     */
    public def filteredArgs() {
        val builder = new RailBuilder[String]();
        for(o in options) {
            if (o.onTheFly) {
                val key = o.shortForm.length() > 0 ? o.shortForm : o.longForm;	           
                builder.add(key);
            }
        }
        return builder.result();
    }

    /**
     * checks to see whether any unexpected args were found and, if
     * desired, writes them out to the standard error stream.
     */
    public def hasUnexpectedArgs(show: Boolean) {
        val badArgs = this.filteredArgs();
        val rc = (badArgs.size > 0);
        if(rc && show) {
            val badArgsString = StringUtil.formatArray[String](badArgs);
            Console.ERR.println("Unexpected arguments:\n\t"+badArgsString+"\n");
            showHELP();
        }
        return rc;
    }
}