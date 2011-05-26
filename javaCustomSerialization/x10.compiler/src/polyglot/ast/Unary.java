/*
 * This file is part of the Polyglot extensible compiler framework.
 *
 * Copyright (c) 2000-2006 Polyglot project group, Cornell University
 * 
 */

package polyglot.ast;

/**
 * A <code>Unary</code> represents a Java unary expression, an
 * immutable pair of an expression and an an operator.
 */
public interface Unary extends Expr 
{
    /** Unary expression operator. */
    public static enum Operator {
        BIT_NOT  ("~", true),
        NEG      ("-", true),
        POST_INC ("++", false),
        POST_DEC ("--", false),
        PRE_INC  ("++", true),
        PRE_DEC  ("--", true),
        POS      ("+", true),
        NOT      ("!", true),
        CARET    ("^", true),
        BAR      ("|", true),
        AMPERSAND("&", true),
        STAR     ("*", true),
        SLASH    ("/", true),
        PERCENT  ("%", true);

        protected boolean prefix;
        protected String name;

        private Operator(String name, boolean prefix) {
            this.name = name;
            this.prefix = prefix;
        }

        /** Returns true of the operator is a prefix operator, false if
         * postfix. */
        public boolean isPrefix() { return prefix; }

        @Override public String toString() { return name; }
    }

    public static final Operator BIT_NOT   = Operator.BIT_NOT;
    public static final Operator NEG       = Operator.NEG;
    public static final Operator POST_INC  = Operator.POST_INC;
    public static final Operator POST_DEC  = Operator.POST_DEC;
    public static final Operator PRE_INC   = Operator.PRE_INC;
    public static final Operator PRE_DEC   = Operator.PRE_DEC;
    public static final Operator POS       = Operator.POS;
    public static final Operator NOT       = Operator.NOT;
    public static final Operator CARET     = Operator.CARET;
    public static final Operator BAR       = Operator.BAR;
    public static final Operator AMPERSAND = Operator.AMPERSAND;
    public static final Operator STAR      = Operator.STAR;
    public static final Operator SLASH     = Operator.SLASH;
    public static final Operator PERCENT   = Operator.PERCENT;

    /** The sub-expression on that to apply the operator. */
    Expr expr();
    /** Set the sub-expression on that to apply the operator. */
    Unary expr(Expr e);

    /** The operator to apply on the sub-expression. */
    Operator operator();
    /** Set the operator to apply on the sub-expression. */
    Unary operator(Operator o);
}
