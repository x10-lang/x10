/*
 * This file is part of the Polyglot extensible compiler framework.
 *
 * Copyright (c) 2000-2006 Polyglot project group, Cornell University
 * 
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
        USHR_ASSIGN    (">>>=", Binary.USHR);

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

    public static final Operator ASSIGN         = Operator.ASSIGN;
    public static final Operator ADD_ASSIGN     = Operator.ADD_ASSIGN;
    public static final Operator SUB_ASSIGN     = Operator.SUB_ASSIGN;
    public static final Operator MUL_ASSIGN     = Operator.MUL_ASSIGN;
    public static final Operator DIV_ASSIGN     = Operator.DIV_ASSIGN;
    public static final Operator MOD_ASSIGN     = Operator.MOD_ASSIGN;
    public static final Operator BIT_AND_ASSIGN = Operator.BIT_AND_ASSIGN;
    public static final Operator BIT_OR_ASSIGN  = Operator.BIT_OR_ASSIGN;
    public static final Operator BIT_XOR_ASSIGN = Operator.BIT_XOR_ASSIGN;
    public static final Operator SHL_ASSIGN     = Operator.SHL_ASSIGN;
    public static final Operator SHR_ASSIGN     = Operator.SHR_ASSIGN;
    public static final Operator USHR_ASSIGN    = Operator.USHR_ASSIGN;

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
