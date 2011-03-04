/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.search.core.facts;

import static x10dt.search.core.pdb.X10FactTypeNames.X10_FieldName;
import static x10dt.search.core.pdb.X10FactTypeNames.X10_MethodName;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.imp.pdb.facts.ISourceLocation;
import org.eclipse.imp.pdb.facts.ITuple;
import org.eclipse.imp.pdb.facts.IValue;
import org.eclipse.imp.pdb.facts.IValueFactory;
import org.eclipse.imp.pdb.facts.impl.fast.ValueFactory;
import org.eclipse.imp.pdb.facts.type.Type;
import org.eclipse.imp.pdb.facts.type.TypeFactory;
import org.eclipse.osgi.util.NLS;

import polyglot.types.ClassType;
import polyglot.types.ConstructorDef;
import polyglot.types.FieldDef;
import polyglot.types.Flags;
import polyglot.types.MethodDef;
import polyglot.types.Ref;
import polyglot.types.Type_c;
import polyglot.types.UnknownType;
import polyglot.util.Position;
import x10.types.ConstrainedType;
import x10.types.FunctionType;
import x10.types.ParameterType;
import x10dt.search.core.Messages;
import x10dt.search.core.pdb.ITypeManager;
import x10dt.search.core.pdb.SearchDBTypes;
import x10dt.search.core.pdb.X10FactTypeNames;
import x10dt.search.core.pdb.X10FlagsEncoder;



abstract class AbstractFactWriter implements IFactWriter {

  protected AbstractFactWriter(final String scopeTypeName) {
    this.fValueFactory = ValueFactory.getInstance();
    this.fTypeName = SearchDBTypes.getInstance().getType(X10FactTypeNames.X10_TypeName);
    this.fMethodName = SearchDBTypes.getInstance().getType(X10_MethodName);
    this.fFieldName = SearchDBTypes.getInstance().getType(X10_FieldName);
    this.fIntType = TypeFactory.getInstance().integerType();
    this.fScopeTypeName = scopeTypeName;
    
    this.fVoidType = this.fValueFactory.tuple(createTypeName("void")); //$NON-NLS-1$
    this.fThisMethodName = this.fMethodName.make(this.fValueFactory, "this"); //$NON-NLS-1$
    
    this.fTypeToValue = new HashMap<polyglot.types.Type, ITuple>(128);
    this.fMethodToValue = new HashMap<MethodDef, IValue>(512);
    this.fCtorToValue = new HashMap<ConstructorDef, IValue>(128);
    this.fFieldToValue = new HashMap<FieldDef, IValue>(256);
  }
  
  // --- Code for sub-classes
  
  protected final ISourceLocation createSourceLocation(final Position position) {
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
  
  protected final IValueFactory getValueFactory() {
    return this.fValueFactory;
  }
  
  protected final IValue findOrCreateField(final FieldDef fieldDef) {
    final IValue value = this.fFieldToValue.get(fieldDef);
    if (value == null) {
      final IValue newValue = createFieldValue(fieldDef);
      this.fFieldToValue.put(fieldDef, newValue);
      return newValue;
    } else {
      return value;
    }
  }
  
  protected final IValue findOrCreateConstructor(final ConstructorDef ctorDef) {
    final IValue value = this.fCtorToValue.get(ctorDef);
    if (value == null) {
      final IValue newValue = createConstructorValue(ctorDef);
      this.fCtorToValue.put(ctorDef, newValue);
      return newValue;
    } else {
      return value;
    }
  }
  
  protected final IValue findOrCreateMethod(final MethodDef methodDef) {
    final IValue value = this.fMethodToValue.get(methodDef);
    if (value == null) {
      final IValue newValue = createMethodValue(methodDef);
      this.fMethodToValue.put(methodDef, newValue);
      return newValue;
    } else {
      return value;
    }
  }
  
  protected final ITuple findOrCreateType(final polyglot.types.Type type) {
    final ITuple tuple = this.fTypeToValue.get(type);
    if (tuple == null) {
      final ITuple newTuple = createType(type);
      this.fTypeToValue.put(type, newTuple);
      return newTuple;
    } else {
      return tuple;
    }
  }
  
  protected final void insertValue(final String typeName, final IValue ... values) {
    getTypeManager(typeName).getWriter().insert(values);
  }
  
  // --- Private code
  
  private IValue createConstructorValue(final ConstructorDef methodDef) {
    final List<Ref<? extends polyglot.types.Type>> formalTypes = methodDef.formalTypes();
    final IValue[] args = new IValue[formalTypes.size()];
    int i = -1;
    for (final Ref<? extends polyglot.types.Type> formalType : formalTypes) {
      args[++i] = createType(formalType.get());
    }
    return getValueFactory().tuple(createSourceLocation(methodDef.position()), this.fThisMethodName, this.fVoidType,  
                                   getValueFactory().list(args), createModifiersCodeValue(methodDef.flags()));
  }
  
  private IValue createFieldValue(final FieldDef fieldDef) {
    return getValueFactory().tuple(createSourceLocation(fieldDef.position()), 
                                   this.fFieldName.make(getValueFactory(), fieldDef.name().toString()),
                                   createType(fieldDef.asInstance().type()), createModifiersCodeValue(fieldDef.flags()));
  }
  
  private IValue createMethodValue(final MethodDef methodDef) {
    final List<Ref<? extends polyglot.types.Type>> formalTypes = methodDef.formalTypes();
    final IValue[] args = new IValue[formalTypes.size()];
    int i = -1;
    for (final Ref<? extends polyglot.types.Type> formalType : formalTypes) {
      args[++i] = createType(formalType.get());
    }
    return getValueFactory().tuple(createSourceLocation(methodDef.position()),
                                   this.fMethodName.make(getValueFactory(), methodDef.name().toString()),
                                   createType(methodDef.returnType().get()), getValueFactory().list(args),
                                   createModifiersCodeValue(methodDef.flags()));
  }
  
  private IValue createModifiersCodeValue(final Flags flags) {
    return this.fIntType.make(this.fValueFactory, new X10FlagsEncoder(flags).getCode());
  }
  
  private ITuple createType(final polyglot.types.Type type) {
    if (type instanceof FunctionType) {
      if (type.position().isCompilerGenerated()) {
        return getValueFactory().tuple(createTypeName(type.toString()));
      } else {
        return getValueFactory().tuple(createTypeName(type.toString()), createSourceLocation(type.position()),
                                       this.fIntType.make(this.fValueFactory, -1));
      }
    } else if (type instanceof ClassType) {
      final ClassType classType = (ClassType) type;
      if (classType.outer() == null) {
        return getValueFactory().tuple(createTypeName(type.fullName().toString()), createSourceLocation(type.position()),
                                       createModifiersCodeValue(classType.flags()));
      } else {
        final IValue outerTypeNameValue = createTypeName(classType.outer().fullName().toString());
        return getValueFactory().tuple(createTypeName(type.fullName().toString()), createSourceLocation(type.position()),
                                       createModifiersCodeValue(classType.flags()), outerTypeNameValue);
      }
    } else if (type instanceof ConstrainedType) {
      return createType(((ConstrainedType) type).baseType().get());
    } else if (type instanceof ParameterType) {
      return getValueFactory().tuple(createTypeName('[' + type.fullName().toString()), createSourceLocation(type.position()), 
                                     this.fIntType.make(this.fValueFactory, -1));
    } else {
      if (type.position() == null) {
        if (type instanceof UnknownType) {
          return getValueFactory().tuple(createTypeName(((Type_c) type).typeToString()));
        } else {
          return getValueFactory().tuple(createTypeName(type.fullName().toString()));
        }
      } else {
        return getValueFactory().tuple(createTypeName(type.fullName().toString()), createSourceLocation(type.position()), 
                                       this.fIntType.make(this.fValueFactory, -1));
      }
    }
  }
  
  private IValue createTypeName(final String typeName) {
    return this.fTypeName.make(this.fValueFactory, typeName);
  }
  
  private ITypeManager getTypeManager(final String typeName) {
    return SearchDBTypes.getInstance().getTypeManager(typeName, this.fScopeTypeName);
  }
  
  // --- Fields
  
  private final IValueFactory fValueFactory;
  
  private final Type fTypeName;
  
  private final Type fMethodName;
  
  private final Type fFieldName;
  
  private final Type fIntType;
  
  private final IValue fVoidType;
  
  private final IValue fThisMethodName;
  
  private final Map<polyglot.types.Type, ITuple> fTypeToValue;
  
  private final Map<ConstructorDef, IValue> fCtorToValue;
  
  private final Map<MethodDef, IValue> fMethodToValue;
  
  private final Map<FieldDef, IValue> fFieldToValue;
  
  
  private String fScopeTypeName;

}
