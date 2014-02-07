/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 * This file was originally derived from the Polyglot extensible compiler framework.
 *
 *  (C) Copyright 2000-2007 Polyglot project group, Cornell University
 *  (C) Copyright IBM Corporation 2007-2014.
 */

package polyglot.ast;

/**
 * Constants defining the precedence of an expression.  Lower
 * values denote higher precedence (i.e., tighter binding).
 */
public enum Precedence {

    /** The precedence of a literal */
    LITERAL     ("literal", 0),
    /** The precedence of a unary expression. */
    UNARY       ("unary", 10),
    /** The precedence of a cast expression. */
    CAST        ("cast", 10),
    /** The precedence of a <code>*</code>, <code>/</code>, or <code>%</code> expression. */
    MUL         ("*", 20),
    /** The precedence of a <code>+</code> when applied to Strings.  This is of higher precedence than <code>+</code> applied to numbers. */
    STRING_ADD  ("string+", 30),
    /** The precedence of a <code>+</code> when applied to numbers, and the precedence of <code>-</code>. */
    ADD         ("+", 40),
    /** The precedence of the shift expressions <code>&lt,&lt,</code>, <code>&gt,&gt,</code>, and <code>&gt,&gt,&gt,</code>. */
    SHIFT       ("<<", 50),
    /** The precedence of the relational expressions <code>&lt,</code>, <code>&gt,</code>, <code>&lt,=</code>, and <code>&gt,=</code>. */
    RELATIONAL  ("<", 60),
    /** The precedence of <code>instanceof</code> expressions. */
    INSTANCEOF  ("isa", 70),
    /** The precedence of equality operators.  That is, precedence of <code>==</code> and <code>!=</code> expressions. */
    EQUAL       ("==", 80),
    /** The precedence of bitwise AND (<code>&amp,<code>) expressions. */
    BIT_AND     ("&", 90),
    /** The precedence of bitwise XOR (<code>^<code>) expressions. */
    BIT_XOR     ("^", 100),
    /** The precedence of bitwise OR (<code>|<code>) expressions. */
    BIT_OR      ("|", 110),
    /** The precedence of IN (<code>in<code>) expressions. */
    IN          ("in", 115),
    /** The precedence of conditional AND (<code>&&<code>) expressions. */
    COND_AND    ("&&", 120),
    /** The precedence of conditional OR (<code>||<code>) expressions. */
    COND_OR     ("||", 130),
    /** The precedence of ternary conditional expressions. */
    CONDITIONAL ("?:", 140),
    /** The precedence of assignment expressions. */
    ASSIGN      ("=", 150),
    /** The precedence of all other expressions. This has the lowest precedence to ensure the expression is parenthesized on output. */
    UNKNOWN     ("unknown", 999);

    private int value;
    public final String name;

    private Precedence(String name, int value) {
	assert(value >= 0);
	this.value = value;
        this.name = name;
    }


    /** Returns true if this and p have the same precedence. */
    public boolean equalsPrecedence(Precedence p) {
	return value == p.value;
    }

    /** Returns true if this binds tighter than p. */
    public boolean isTighter(Precedence p) {
	return value < p.value;
    }
}
