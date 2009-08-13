/*
 *
 * (C) Copyright IBM Corporation 2006-2008.
 *
 *  This file is part of X10 Language.
 *
 */

package x10.compiler;

/**
 * This interface exists solely to document the identifier names that
 * correspond to various X10 language operators. It's unlikely that
 * any class would implement this interface. Some restricted subsets
 * have been identified that might be useful sets of operators for
 * implementation: ArithmeticOps, ComparisonOps, SetOps.
 */

interface Ops[B,C] {

    def $plus(): C;              // +a
    def $minus(): C;             // -a

    def $plus(that: B): C;       // a+b
    def $minus(that: B): C;      // a-b
    def $times(that: B): C;      // a*b
    def $over(that: B): C;       // a/b
    def $percent(that: B): C;    // a%b

    def $eq(that: B): boolean;   // value comparison op TBD
    def $ne(that: B): boolean;   // value comparison op TBD
    def $lt(that: B): boolean;   // a<b
    def $gt(that: B): boolean;   // a>b
    def $le(that: B): boolean;   // a<=b
    def $ge(that: B): boolean;   // a>=b

    def $not(): C;               // !a
    def $and(that: B): C;        // a&&b
    def $or(that: B): C;         // a||c

    def $tilde(): C;             // ~a
    def $ampersand(that: B): C;  // a&b
    def $bar(that: B): C;        // a|b
    def $caret(that: B): C;      // a^b
    def $left(that: B): C;       // a<<b
    def $right(that: B): C;      // a>>c

    def $in(): C;                // a in b
    
    def $instanceof(that: B): C; // a instanceof b
    def $cast(that: B): C;       // (b)a
}

