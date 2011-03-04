/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.search.core.pdb;

import static x10dt.search.core.pdb.X10FactTypeNames.X10_FieldName;
import static x10dt.search.core.pdb.X10FactTypeNames.X10_MethodName;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import org.eclipse.imp.pdb.facts.ISourceLocation;
import org.eclipse.imp.pdb.facts.IValue;
import org.eclipse.imp.pdb.facts.IValueFactory;
import org.eclipse.imp.pdb.facts.impl.fast.ValueFactory;
import org.eclipse.imp.pdb.facts.type.Type;
import org.eclipse.imp.pdb.facts.type.TypeFactory;
import org.eclipse.osgi.util.NLS;

import polyglot.types.ConstructorDef;
import polyglot.types.FieldDef;
import polyglot.types.Flags;
import polyglot.types.MethodDef;
import polyglot.types.Ref;
import polyglot.util.Position;
import polyglot.visit.NodeVisitor;
import x10dt.search.core.Messages;

/**
 * Provides services for facts writing and defines a base for all fact writers that need to browse final AST nodes in order
 * to create the appropriate indexing information.
 * 
 * @author egeay
 */
public class FactWriterVisitor extends NodeVisitor {
  
  protected FactWriterVisitor() {
    this.fValueFactory = ValueFactory.getInstance();
    this.fTypeName = SearchDBTypes.getInstance().getType(X10FactTypeNames.X10_TypeName);
    this.fMethodName = SearchDBTypes.getInstance().getType(X10_MethodName);
    this.fFieldName = SearchDBTypes.getInstance().getType(X10_FieldName);
    this.fIntType = TypeFactory.getInstance().integerType();
    
    this.fVoidType = createTypeName("x10.lang.Void"); //$NON-NLS-1$
    this.fThisMethodName = this.fMethodName.make(this.fValueFactory, "this"); //$NON-NLS-1$
  }
  
  // --- Public services
  
  /**
   * Defines the current scope type.
   * 
   * @param scopeTypeName The name defining the scope type.
   */
  public final void setScopeType(final String scopeTypeName) {
    this.fScopeTypeName = scopeTypeName;
  }
  
  // --- Code for implementers
  
  protected final IValue createConstructorValue(final ConstructorDef methodDef) {
    final List<Ref<? extends polyglot.types.Type>> formalTypes = methodDef.formalTypes();
    final IValue[] args = new IValue[formalTypes.size()];
    int i = -1;
    for (final Ref<? extends polyglot.types.Type> formalType : formalTypes) {
      args[++i] = createTypeName(formalType.get().toString());
    }
    return getValueFactory().tuple(getSourceLocation(methodDef.position()), this.fThisMethodName, this.fVoidType,  
                                   getValueFactory().list(args), createModifiersCodeValue(methodDef.flags()));
  }
  
  protected final IValue createFieldValue(final FieldDef fieldDef) {
    return getValueFactory().tuple(getSourceLocation(fieldDef.position()), 
                                   this.fFieldName.make(getValueFactory(), fieldDef.name().toString()),
                                   createTypeName(fieldDef.asInstance().type().toString()),
                                   createModifiersCodeValue(fieldDef.flags()));
  }
  
  protected final IValue createMethodValue(final MethodDef methodDef) {
    final List<Ref<? extends polyglot.types.Type>> formalTypes = methodDef.formalTypes();
    final IValue[] args = new IValue[formalTypes.size()];
    int i = -1;
    for (final Ref<? extends polyglot.types.Type> formalType : formalTypes) {
      args[++i] = createTypeName(formalType.get().toString());
    }
    return getValueFactory().tuple(getSourceLocation(methodDef.position()),
                                   this.fMethodName.make(getValueFactory(), methodDef.name().toString()),
                                   createTypeName(methodDef.returnType().get().toString()), getValueFactory().list(args),
                                   createModifiersCodeValue(methodDef.flags()));
  }
  
  protected final IValue createModifiersCodeValue(final Flags flags) {
    return this.fIntType.make(this.fValueFactory, new X10FlagsEncoder(flags).getCode());
  }
  
  protected final IValue createTypeName(final String typeName) {
    return this.fTypeName.make(this.fValueFactory, typeName);
  }

  protected final ISourceLocation getSourceLocation(final Position position) {
    final StringBuilder scheme = new StringBuilder();
    if (position.file().contains(".jar:")) { //$NON-NLS-1$
      scheme.append("jar:"); //$NON-NLS-1$
    }
    scheme.append("file"); //$NON-NLS-1$
    try {
      String path = position.file();
      final String osName = System.getProperty("os.name"); //$NON-NLS-1$
      if (osName.startsWith("Windows")) { //$NON-NLS-1$
        path = '/' + path.replace('\\', '/');
      }
      final URI uri = new URI(scheme.toString(), null /* host */, path, null /* fragment */);
      return this.fValueFactory.sourceLocation(uri, position.offset(), 
                                               position.endOffset() - position.offset(), position.line(), position.endLine(),
                                               position.column(), position.endColumn());
    } catch (URISyntaxException except) {
      throw new RuntimeException(NLS.bind(Messages.FWV_URICreationError, position.file()), except);
    }
  }
  
  protected final ITypeManager getTypeManager(final String typeName) {
    return SearchDBTypes.getInstance().getTypeManager(typeName, this.fScopeTypeName);
  }
  
  protected final IValueFactory getValueFactory() {
    return this.fValueFactory;
  }
  
  protected final void insertValue(final String typeName, final IValue ... values) {
    getTypeManager(typeName).getWriter().insert(values);
  }
  
  // --- Fields
  
  private final IValueFactory fValueFactory;
  
  private final Type fTypeName;
  
  private final Type fMethodName;
  
  private final Type fFieldName;
  
  private final Type fIntType;
  
  private final IValue fVoidType;
  
  private final IValue fThisMethodName;
  
  
  private String fScopeTypeName;

}
