/*
 * This file is part of the Polyglot extensible compiler framework.
 *
 * Copyright (c) 2000-2006 Polyglot project group, Cornell University
 * 
 */

package polyglot.types;

import polyglot.types.VarDef_c.ConstantValue;

/**
 * A <code>VarInstance</code> contains type information for a variable.  It may
 * be either a local or a field.
 */
public interface VarDef extends TypeObject, Def
{
    /**
     * The flags of the variable.
     */
    Flags flags();

    /**
     * The name of the variable.
     */
    Name name();
    void setName(Name name);

    /**
     * The type of the variable.
     */
    Ref<? extends Type> type();

    Ref<ConstantValue> constantValueRef();
    
    /**
     * The variable's constant value, or null.
     */
    x10.types.constants.ConstantValue constantValue();

    /**
     * Destructively set the constant value of the field.
     * @param value the constant value.  Should be an instance of String,
     * Boolean, Byte, Short, Character, Integer, Long, Float, Double, or null.
     */
    void setConstantValue(x10.types.constants.ConstantValue value);
    
    /**
     * Mark the variable as not a compile time constant.
     */
    void setNotConstant();
    
    /**
     * Whether the variable has a constant value.
     */
    boolean isConstant();

    /**
     * Destructively set the type of the variable.
     * This method should be deprecated.
     */
    void setType(Ref<? extends Type> type); //destructive update   
    
    /**
     * Destructively set the flags of the variable.
     */
    void setFlags(Flags flags);

    /**
     * Return the Use for the current Def.
     */
    VarInstance<? extends VarDef> asInstance();

}
