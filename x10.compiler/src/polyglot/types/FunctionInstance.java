/*
 * This file is part of the Polyglot extensible compiler framework.
 *
 * Copyright (c) 2000-2006 Polyglot project group, Cornell University
 * Copyright (c) 2007 IBM Corporation
 * 
 */
package polyglot.types;


/**
 * A <code>ProcedureInstance</code> contains the type information for a Java
 * procedure (either a method or a constructor).
 */
public interface FunctionInstance<T extends ProcedureDef> extends ProcedureInstance<T> {
    Type returnType();

    public FunctionInstance<T> returnTypeRef(Ref<? extends Type> returnType);

    public Ref<? extends Type> returnTypeRef();
}
