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

import polyglot.types.Type;

/**
 * An <code>Assign</code> represents a Java assignment expression.
 */
public interface Assign extends Expr
{
    /** Assignment operator. */
    public static enum Operator {
        ASSIGN         ("=", null),
        ADD_ASSIGN     ("+=", Binary.ADD),
        SUB_ASSIGN     ("-=", Binary.SUB),
        MUL_ASSIGN     ("*=", Binary.MUL),
        DIV_ASSIGN     ("/=", Binary.DIV),
        MOD_ASSIGN     ("%=", Binary.MOD),
        BIT_AND_ASSIGN ("&=", Binary.BIT_AND),
        BIT_OR_ASSIGN  ("|=", Binary.BIT_OR),
        BIT_XOR_ASSIGN ("^=", Binary.BIT_XOR),
        SHL_ASSIGN     ("<<=", Binary.SHL),
        SHR_ASSIGN     (">>=", Binary.SHR),
        USHR_ASSIGN    (">>>=", Binary.USHR),
        DOT_DOT_ASSIGN ("..=", Binary.DOT_DOT),
        ARROW_ASSIGN   ("->=", Binary.ARROW),
        LARROW_ASSIGN  ("<-=", Binary.LARROW),
        FUNNEL_ASSIGN  ("-<=", Binary.FUNNEL),
        LFUNNEL_ASSIGN (">-=", Binary.LFUNNEL),
        DIAMOND_ASSIGN ("<>=", Binary.DIAMOND),
        BOWTIE_ASSIGN  ("><=", Binary.BOWTIE),
        STARSTAR_ASSIGN("**=", Binary.STARSTAR),
        TWIDDLE_ASSIGN ("~=", Binary.TWIDDLE);

        private final Binary.Operator binOp;
        public final String name;
        private Operator(String name, Binary.Operator binOp) {
            this.name = name;
            this.binOp = binOp;
        }
        public Binary.Operator binaryOperator() {
            return binOp;
        }
        @Override public String toString() {
            return name;
        }
    }

    public static final Operator ASSIGN          = Operator.ASSIGN;
    public static final Operator ADD_ASSIGN      = Operator.ADD_ASSIGN;
    public static final Operator SUB_ASSIGN      = Operator.SUB_ASSIGN;
    public static final Operator MUL_ASSIGN      = Operator.MUL_ASSIGN;
    public static final Operator DIV_ASSIGN      = Operator.DIV_ASSIGN;
    public static final Operator MOD_ASSIGN      = Operator.MOD_ASSIGN;
    public static final Operator BIT_AND_ASSIGN  = Operator.BIT_AND_ASSIGN;
    public static final Operator BIT_OR_ASSIGN   = Operator.BIT_OR_ASSIGN;
    public static final Operator BIT_XOR_ASSIGN  = Operator.BIT_XOR_ASSIGN;
    public static final Operator SHL_ASSIGN      = Operator.SHL_ASSIGN;
    public static final Operator SHR_ASSIGN      = Operator.SHR_ASSIGN;
    public static final Operator USHR_ASSIGN     = Operator.USHR_ASSIGN;
    public static final Operator DOT_DOT_ASSIGN  = Operator.DOT_DOT_ASSIGN;
    public static final Operator ARROW_ASSIGN    = Operator.ARROW_ASSIGN;
    public static final Operator LARROW_ASSIGN   = Operator.LARROW_ASSIGN;
    public static final Operator FUNNEL_ASSIGN   = Operator.FUNNEL_ASSIGN;
    public static final Operator LFUNNEL_ASSIGN  = Operator.LFUNNEL_ASSIGN;
    public static final Operator DIAMOND_ASSIGN  = Operator.DIAMOND_ASSIGN;
    public static final Operator BOWTIE_ASSIGN   = Operator.BOWTIE_ASSIGN;
    public static final Operator STARSTAR_ASSIGN = Operator.STARSTAR_ASSIGN;
    public static final Operator TWIDDLE_ASSIGN  = Operator.TWIDDLE_ASSIGN;

    /**
     * Left child (target) of the assignment.
     * The target must be a Variable, but this is not enforced
     * statically to keep Polyglot backward compatible.
     */
    Expr left();

    Type leftType();	

    /**
     * The assignment's operator.
     */
    Operator operator();

    /**
     * Set the assignment's operator.
     */
    Assign operator(Operator op);

    /**
     * Right child (source) of the assignment.
     */
    Expr right();

    /**
     * Set the right child (source) of the assignment.
     */
    Assign right(Expr right);
    
    boolean throwsArithmeticException();
}
