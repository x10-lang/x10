/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2010.
 */

package x10.util;

/**
 * An Option is a utility struct primarily used to support
 * command line parsing by the OptionsParser class.
 * It encapsulates a long and short form of the option
 * and a description.
 */
public struct Option {
    public short_:String; // underscore is workaround for XTENLANG-623
    public long_:String;
    public description:String;
    public def this(s:String, l:String, d:String) {
        this.short_ = s;
        if (s!=null) {
            if (s.length()!=1) {
                throw new IllegalArgumentException("short options must be one letter only (or null)");
            }
        }
        this.long_ = (l==null) ? null : "--"+l;
        this.description=d;
    }
    public def toString() = description;

    public def equals(other:Any):Boolean {
        if (other instanceof Option) {
            return equals(other as Option);
        } else {
            return false;
        }
    }

    public def equals(that:Option):Boolean {
        if (!description.equals(that.description)) return false;
        if (long_ != null) {
            if (!long_.equals(that.long_)) return false;
        } else {
            if (that.long_ != null) return false;
        }
        if (short_ == null) return that.short_ == null;
        return short_.equals(that.short_);
    }

    public def hashCode():Int {
        var result:int = 1;
        if (short_ != null) {
            result = 8191*result + short_.hashCode();
        }
        if (long_ != null) {
            result = 8191*result + long_.hashCode();
        }
        result = 8191 * result + description.hashCode();
        return result;
    }
}
