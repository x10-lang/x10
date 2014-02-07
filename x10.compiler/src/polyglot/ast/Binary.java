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

import x10.types.MethodInstance;

/**
 * A <code>Binary</code> represents a Java binary expression, an
 * immutable pair of expressions combined with an operator.
 */
public interface Binary extends Expr
{
    /** Binary expression operator. */
    public static enum Operator {
        GT      (">", Precedence.RELATIONAL),
        LT      ("<", Precedence.RELATIONAL),
        EQ      ("==", Precedence.EQUAL),
        LE      ("<=", Precedence.RELATIONAL),
        GE      (">=", Precedence.RELATIONAL),
        NE      ("!=", Precedence.EQUAL),
        COND_OR ("||", Precedence.COND_OR),
        COND_AND("&&", Precedence.COND_AND),
        ADD     ("+", Precedence.ADD),
        SUB     ("-", Precedence.ADD),
        MUL     ("*", Precedence.MUL),
        DIV     ("/", Precedence.MUL),
        MOD     ("%", Precedence.MUL),
        BIT_OR  ("|", Precedence.BIT_OR),
        BIT_AND ("&", Precedence.BIT_AND),
        BIT_XOR ("^", Precedence.BIT_XOR),
        SHL     ("<<", Precedence.SHIFT),
        SHR     (">>", Precedence.SHIFT),
        USHR    (">>>", Precedence.SHIFT),
        DOT_DOT ("..", Precedence.CAST),
        ARROW   ("->", Precedence.SHIFT),
        LARROW  ("<-", Precedence.SHIFT),
        FUNNEL  ("-<", Precedence.SHIFT),
        LFUNNEL (">-", Precedence.SHIFT),
        DIAMOND ("<>", Precedence.SHIFT),
        BOWTIE  ("><", Precedence.SHIFT),
        STARSTAR("**", Precedence.MUL),
        TWIDDLE ("~", Precedence.EQUAL),
        NTWIDDLE("!~", Precedence.EQUAL),
        BANG    ("!", Precedence.SHIFT);

	    protected final Precedence prec;
        public final String name;

        Operator(String name, Precedence prec) {
            this.name = name;
            this.prec = prec;
        }

        /** Returns the precedence of the operator. */
        public Precedence precedence() { return prec; }

        @Override public String toString() {
            return name;
        }
        public static Operator fromOp(String opName) {
            for (Operator op : values())
                if (op.name.equals(opName)) return op;
            return null;
        }
    }

    public static final Operator GT = Operator.GT;
    public static final Operator LT = Operator.LT;
    public static final Operator EQ = Operator.EQ;
    public static final Operator LE = Operator.LE;
    public static final Operator GE = Operator.GE;
    public static final Operator NE = Operator.NE;
    public static final Operator COND_OR = Operator.COND_OR;
    public static final Operator COND_AND = Operator.COND_AND;
    public static final Operator ADD = Operator.ADD;
    public static final Operator SUB = Operator.SUB;
    public static final Operator MUL = Operator.MUL;
    public static final Operator DIV = Operator.DIV;
    public static final Operator MOD = Operator.MOD;
    public static final Operator BIT_OR = Operator.BIT_OR;
    public static final Operator BIT_AND = Operator.BIT_AND;
    public static final Operator BIT_XOR = Operator.BIT_XOR;
    public static final Operator SHL = Operator.SHL;
    public static final Operator SHR = Operator.SHR;
    public static final Operator USHR = Operator.USHR;
    public static final Operator DOT_DOT = Operator.DOT_DOT;
    public static final Operator ARROW = Operator.ARROW;
    public static final Operator LARROW = Operator.LARROW;
    public static final Operator FUNNEL = Operator.FUNNEL;
    public static final Operator LFUNNEL = Operator.LFUNNEL;
    public static final Operator DIAMOND = Operator.DIAMOND;
    public static final Operator BOWTIE = Operator.BOWTIE;
    public static final Operator STARSTAR = Operator.STARSTAR;
    public static final Operator TWIDDLE = Operator.TWIDDLE;
    public static final Operator NTWIDDLE = Operator.NTWIDDLE;
    public static final Operator BANG = Operator.BANG;


    /**
     * Left child of the binary.
     */
    Expr left();

    /**
     * Set the left child of the binary.
     */
    Binary left(Expr left);

    /**
     * The binary's operator.
     */
    Operator operator();

    /**
     * Set the binary's operator.
     */
    Binary operator(Operator op);

    /**
     * Right child of the binary.
     */
    Expr right();

    /**
     * Set the right child of the binary.
     */
    Binary right(Expr right);

    /**
     * Returns true if the binary might throw an arithmetic exception,
     * such as division by zero.
     */
    boolean throwsArithmeticException();

    /**
     * Set the precedence of the expression.
     */
    Binary precedence(Precedence precedence);
    
    boolean invert();
    Binary invert(boolean invert);
    MethodInstance methodInstance();
    Binary methodInstance(MethodInstance mi);
}
