/*******************************************************************************
 * Copyright (c) 2000, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package x10dt.ui.typeHierarchy;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.imp.model.ModelFactory.ModelException;

import x10dt.search.core.elements.IMemberInfo;
import x10dt.search.core.elements.IMethodInfo;
import x10dt.search.core.elements.ITypeInfo;
import x10dt.search.core.engine.ITypeHierarchy;
import x10dt.search.core.pdb.X10FlagsEncoder.X10;



public class MethodOverrideTester {
	private static class Substitutions {

		public static final Substitutions EMPTY_SUBST= new Substitutions();

		private HashMap fMap;

		public Substitutions() {
			fMap= null;
		}

		public void addSubstitution(String typeVariable, String substitution, String erasure) {
			if (fMap == null) {
				fMap= new HashMap(3);
			}
			fMap.put(typeVariable, new String[] { substitution, erasure });
		}

		private String[] getSubstArray(String typeVariable) {
			if (fMap != null) {
				return (String[]) fMap.get(typeVariable);
			}
			return null;
		}

		public String getSubstitution(String typeVariable) {
			String[] subst= getSubstArray(typeVariable);
			if (subst != null) {
				return subst[0];
			}
			return null;
		}

		public String getErasure(String typeVariable) {
			String[] subst= getSubstArray(typeVariable);
			if (subst != null) {
				return subst[1];
			}
			return null;
		}
	}

	private final ITypeInfo fFocusType;
	private final ITypeHierarchy fHierarchy;

	private Map /* <IMethodInfo, Substitutions> */ fMethodSubstitutions;
	private Map /* <ITypeInfo, Substitutions> */ fTypeVariableSubstitutions;

	public MethodOverrideTester(ITypeInfo focusType, ITypeHierarchy hierarchy) {
		if (focusType == null || hierarchy == null) {
			throw new IllegalArgumentException();
		}
		fFocusType= focusType;
		fHierarchy= hierarchy;
		fTypeVariableSubstitutions= null;
		fMethodSubstitutions= null;
	}

	public ITypeInfo getFocusType() {
		return fFocusType;
	}

	public ITypeHierarchy getTypeHierarchy() {
		return fHierarchy;
	}

	/**
	 * Finds the method that declares the given method. A declaring method is the 'original' method declaration that does
	 * not override nor implement a method. <code>null</code> is returned it the given method does not override
	 * a method. When searching, super class are examined before implemented interfaces.
	 * @param overriding the overriding method
	 * @param testVisibility If true the result is tested on visibility. Null is returned if the method is not visible.
	 * @return the declaring method, or <code>null</code>
	 * @throws ModelException
	 */
	public IMethodInfo findDeclaringMethod(IMethodInfo overriding, boolean testVisibility) throws ModelException {
		IMethodInfo result= null;
		IMethodInfo overridden= findOverriddenMethod(overriding, testVisibility);
		while (overridden != null) {
			result= overridden;
			overridden= findOverriddenMethod(result, testVisibility);
		}
		return result;
	}

	/**
	 * Finds the method that is overridden by the given method.
	 * First the super class is examined and then the implemented interfaces.
	 * @param overriding the overriding method
	 * @param testVisibility If true the result is tested on visibility. Null is returned if the method is not visible.
	 * @return a method that is directly overridden by the given method, or <code>null</code>
	 * @throws ModelException
	 */
	public IMethodInfo findOverriddenMethod(IMethodInfo overriding, boolean testVisibility) throws ModelException {
		int flags= overriding.getX10FlagsCode();
		if (SearchUtils.hasFlag(X10.PRIVATE, flags) || SearchUtils.hasFlag(X10.STATIC, flags) || overriding.isConstructor()) {
			return null;
		}

		ITypeInfo type= overriding.getDeclaringType();
		ITypeInfo superClass= fHierarchy.getSuperClass(type);
		if (superClass != null) {
			IMethodInfo res= findOverriddenMethodInHierarchy(superClass, overriding);
			if (res != null && !SearchUtils.hasFlag(X10.PRIVATE, res.getX10FlagsCode())) {
//				if (!testVisibility || ModelUtil.isVisibleInHierarchy(res, type.getPackageFragment())) {
//					return res;
//				}
			}
		}
		if (!overriding.isConstructor()) {
			ITypeInfo[] interfaces= fHierarchy.getAllSuperInterfaces(type);
			for (int i= 0; i < interfaces.length; i++) {
				IMethodInfo res= findOverriddenMethodInHierarchy(interfaces[i], overriding);
				if (res != null) {
					return res; // methods from interfaces are always public and therefore visible
				}
			}
		}
		return null;
	}

	/**
	 * Finds the directly overridden method in a type and its super types. First the super class is examined and then the implemented interfaces.
	 * With generics it is possible that 2 methods in the same type are overidden at the same time. In that case, the first overridden method found is returned.
	 * 	@param type The type to find methods in
	 * @param overriding The overriding method
	 * @return The first overridden method or <code>null</code> if no method is overridden
	 * @throws ModelException
	 */
	public IMethodInfo findOverriddenMethodInHierarchy(ITypeInfo type, IMethodInfo overriding) throws ModelException {
		IMethodInfo method= findOverriddenMethodInType(type, overriding);
		if (method != null) {
			return method;
		}
		ITypeInfo superClass= fHierarchy.getSuperClass(type);
		if (superClass != null) {
			IMethodInfo res=  findOverriddenMethodInHierarchy(superClass, overriding);
			if (res != null) {
				return res;
			}
		}
		if (!overriding.isConstructor()) {
			ITypeInfo[] superInterfaces= fHierarchy.getAllSuperInterfaces(type);
			for (int i= 0; i < superInterfaces.length; i++) {
				IMethodInfo res= findOverriddenMethodInHierarchy(superInterfaces[i], overriding);
				if (res != null) {
					return res;
				}
			}
		}
		return method;
	}

	/**
	 * Finds an overridden method in a type. With generics it is possible that 2 methods in the same type are overridden at the same time.
	 * In that case the first overridden method found is returned.
	 * @param overriddenType The type to find methods in
	 * @param overriding The overriding method
	 * @return The first overridden method or <code>null</code> if no method is overridden
	 * @throws ModelException
	 */
	public IMethodInfo findOverriddenMethodInType(ITypeInfo overriddenType, IMethodInfo overriding) throws ModelException {
		IMethodInfo[] overriddenMethods = SearchUtils.getMethods(overriddenType);
		for (int i= 0; i < overriddenMethods.length; i++) {
			if (isSubsignature(overriding, overriddenMethods[i])) {
				return overriddenMethods[i];
			}
		}
		return null;
	}

	/**
	 * Finds an overriding method in a type.
	 * @param overridingType The type to find methods in
	 * @param overridden The overridden method
	 * @return The overriding method or <code>null</code> if no method is overriding.
	 * @throws ModelException
	 */
	public IMethodInfo findOverridingMethodInType(ITypeInfo overridingType, IMethodInfo overridden) throws ModelException {
		IMethodInfo[] overridingMethods= SearchUtils.getMethods(overridingType);
		for (int i= 0; i < overridingMethods.length; i++) {
			if (isSubsignature(overridingMethods[i], overridden)) {
				return overridingMethods[i];
			}
		}
		return null;
	}

	/**
	 * Tests if a method is a subsignature of another method.
	 * @param overriding overriding method (m1)
	 * @param overridden overridden method (m2)
	 * @return <code>true</code> iff the method <code>m1</code> is a subsignature of the method <code>m2</code>.
	 * 		This is one of the requirements for m1 to override m2.
	 * 		Accessibility and return types are not taken into account.
	 * 		Note that subsignature is <em>not</em> symmetric!
	 * @throws ModelException
	 */
	public boolean isSubsignature(IMethodInfo overriding, IMethodInfo overridden) throws ModelException {
		if (!overridden.getName().equals(overriding.getName())) {
			return false;
		}
		int nParameters= overridden.getParameters().length;
		if (nParameters != overriding.getParameters().length) {
			return false;
		}

		if (!hasCompatibleTypeParameters(overriding, overridden)) {
			return false;
		}

		return nParameters == 0 || hasCompatibleParameterTypes(overriding, overridden);
	}

	private boolean hasCompatibleTypeParameters(IMethodInfo overriding, IMethodInfo overridden) throws ModelException {
		ITypeInfo[] overriddenTypeParameters= overridden.getParameters();
		ITypeInfo[] overridingTypeParameters= overriding.getParameters();
		int nOverridingTypeParameters= overridingTypeParameters.length;
		if (overriddenTypeParameters.length != nOverridingTypeParameters) {
			return nOverridingTypeParameters == 0;
		}
		Substitutions overriddenSubst= getMethodSubstitions(overridden);
		Substitutions overridingSubst= getMethodSubstitions(overriding);
		for (int i= 0; i < nOverridingTypeParameters; i++) {
			String erasure1= overriddenSubst.getErasure(overriddenTypeParameters[i].getName());
			String erasure2= overridingSubst.getErasure(overridingTypeParameters[i].getName());
			if (erasure1 == null || !erasure1.equals(erasure2)) {
				return false;
			}
			// comparing only the erasure is not really correct: Need to compare all bounds, that can be in different order
//			int nBounds= overriddenTypeParameters[i].getBounds().length;
//			if (nBounds > 1 && nBounds != overridingTypeParameters[i].getBounds().length) {
//				return false;
//			}
		}
		return true;
	}

	private boolean hasCompatibleParameterTypes(IMethodInfo overriding, IMethodInfo overridden) throws ModelException {
		ITypeInfo[] overriddenParamTypes= overridden.getParameters();
		ITypeInfo[] overridingParamTypes= overriding.getParameters();

		String[] substitutedOverriding= new String[overridingParamTypes.length];
		boolean testErasure= false;

		for (int i= 0; i < overridingParamTypes.length; i++) {
			ITypeInfo overriddenParamSig= overriddenParamTypes[i];
			String overriddenParamName= getSubstitutedTypeName(overriddenParamSig.getName(), overridden);
			String overridingParamName= getSubstitutedTypeName(overridingParamTypes[i].getName(), overriding);
			substitutedOverriding[i]= overridingParamName;
			if (!overriddenParamName.equals(overridingParamName)) {
				testErasure= true;
				break;
			}
		}
		if (testErasure) {
			for (int i= 0; i < overridingParamTypes.length; i++) {
				ITypeInfo overriddenParamSig= overriddenParamTypes[i];
				String overriddenParamName= getErasedTypeName(overriddenParamSig.getName(), overridden);
				String overridingParamName= substitutedOverriding[i];
				if (overridingParamName == null)
					overridingParamName= getSubstitutedTypeName(overridingParamTypes[i].getName(), overriding);
				if (!overriddenParamName.equals(overridingParamName)) {
					return false;
				}
			}
		}
		return true;
	}

	private String getVariableSubstitution(IMemberInfo context, String variableName) throws ModelException {
		ITypeInfo type;
		if (context instanceof IMethodInfo) {
			String subst= getMethodSubstitions((IMethodInfo) context).getSubstitution(variableName);
			if (subst != null) {
				return subst;
			}
			type= context.getDeclaringType();
		} else {
			type= (ITypeInfo) context;
		}
		String subst= getTypeSubstitions(type).getSubstitution(variableName);
		if (subst != null) {
			return subst;
		}
		return variableName; // not a type variable
	}

	private String getVariableErasure(IMemberInfo context, String variableName) throws ModelException {
		ITypeInfo type;
		if (context instanceof IMethodInfo) {
			String subst= getMethodSubstitions((IMethodInfo) context).getErasure(variableName);
			if (subst != null) {
				return subst;
			}
			type= context.getDeclaringType();
		} else {
			type= (ITypeInfo) context;
		}
		String subst= getTypeSubstitions(type).getErasure(variableName);
		if (subst != null) {
			return subst;
		}
		return variableName; // not a type variable
	}

	/*
	 * Returns the substitutions for a method's type parameters
	 */
	private Substitutions getMethodSubstitions(IMethodInfo method) throws ModelException {
		if (fMethodSubstitutions == null) {
			fMethodSubstitutions= new LinkedHashMap(3);
		}

		Substitutions s= (Substitutions) fMethodSubstitutions.get(method);
		if (s == null) {
			ITypeInfo[] typeParameters= method.getParameters();
			if (typeParameters.length == 0) {
				s= Substitutions.EMPTY_SUBST;
			} else {
				ITypeInfo instantiatedType= method.getDeclaringType();
				s= new Substitutions();
				for (int i= 0; i < typeParameters.length; i++) {
					ITypeInfo curr= typeParameters[i];
					s.addSubstitution(curr.getName(), '+' + String.valueOf(i), getTypeParameterErasure(curr, instantiatedType));
				}
			}
			fMethodSubstitutions.put(method, s);
		}
		return s;
	}

	/*
	 * Returns the substitutions for a type's type parameters
	 */
	private Substitutions getTypeSubstitions(ITypeInfo type) throws ModelException {
		if (fTypeVariableSubstitutions == null) {
			fTypeVariableSubstitutions= new HashMap();
			computeSubstitutions(fFocusType, null, null);
		}
		Substitutions subst= (Substitutions) fTypeVariableSubstitutions.get(type);
		if (subst == null) {
			return Substitutions.EMPTY_SUBST;
		}
		return subst;
	}

	private void computeSubstitutions(ITypeInfo instantiatedType, ITypeInfo instantiatingType, String[] typeArguments) throws ModelException {
		Substitutions s= new Substitutions();
		fTypeVariableSubstitutions.put(instantiatedType, s);

		ITypeInfo[] typeParameters= new ITypeInfo[0]/*instantiatedType.getParameters()*/;

		if (instantiatingType == null) { // the focus type
			for (int i= 0; i < typeParameters.length; i++) {
				ITypeInfo curr= typeParameters[i];
				// use star to make type variables different from type refs
				s.addSubstitution(curr.getName(), '*' + curr.getName(), getTypeParameterErasure(curr, instantiatedType));
			}
		} else {
			if (typeParameters.length == typeArguments.length) {
				for (int i= 0; i < typeParameters.length; i++) {
					ITypeInfo curr= typeParameters[i];
					String substString= getSubstitutedTypeName(typeArguments[i], instantiatingType); // substitute in the context of the instantiatingType
					String erasure= getErasedTypeName(typeArguments[i], instantiatingType); // get the erasure from the type argument
					s.addSubstitution(curr.getName(), substString, erasure);
				}
			} else if (typeArguments.length == 0) { // raw type reference
				for (int i= 0; i < typeParameters.length; i++) {
					ITypeInfo curr= typeParameters[i];
					String erasure= getTypeParameterErasure(curr, instantiatedType);
					s.addSubstitution(curr.getName(), erasure, erasure);
				}
			} else {
				// code with errors
			}
		}
		ITypeInfo superclassTypeSignature= fHierarchy.getSuperClass(instantiatedType);
		if (superclassTypeSignature != null) {
			String superTypeArguments= superclassTypeSignature.getName();
			ITypeInfo superclass= fHierarchy.getSuperClass(instantiatedType);
			if (superclass != null && !fTypeVariableSubstitutions.containsKey(superclass)) {
				computeSubstitutions(superclass, instantiatedType, new String[]{superTypeArguments});
			}
		}
		ITypeInfo[] superInterfacesTypeSignature= fHierarchy.getAllSuperInterfaces(instantiatedType);
		int nInterfaces= superInterfacesTypeSignature.length;
		if (nInterfaces > 0) {
			ITypeInfo[] superInterfaces= fHierarchy.getAllSuperInterfaces(instantiatedType);
			if (superInterfaces.length == nInterfaces) {
				for (int i= 0; i < nInterfaces; i++) {
					String superTypeArguments= superInterfacesTypeSignature[i].getName();
					ITypeInfo superInterface= superInterfaces[i];
					if (!fTypeVariableSubstitutions.containsKey(superInterface)) {
						computeSubstitutions(superInterface, instantiatedType, new String[]{superTypeArguments});
					}
				}
			}
		}
	}

	private String getTypeParameterErasure(ITypeInfo typeParameter, ITypeInfo context) throws ModelException {
//		String[] bounds= typeParameter.getBounds();
//		if (bounds.length > 0) {
//			return getSubstitutedTypeName(Signature.createTypeSignature(bounds[0], false), context);
//		}
		return "Object"; //$NON-NLS-1$
	}


	/**
	 * Translates the type signature to a 'normalized' type name where all variables are substituted for the given type or method context.
	 * The returned name contains only simple names and can be used to compare against other substituted type names
	 * @param typeSig The type signature to translate
	 * @param context The context for the substitution
	 * @return a type name
	 * @throws ModelException
	 */
	private String getSubstitutedTypeName(String typeSig, IMemberInfo context) throws ModelException {
		return internalGetSubstitutedTypeName(typeSig, context, false, new StringBuffer()).toString();
	}

	private String getErasedTypeName(String typeSig, IMemberInfo context) throws ModelException {
		return internalGetSubstitutedTypeName(typeSig, context, true, new StringBuffer()).toString();
	}

	private StringBuffer internalGetSubstitutedTypeName(String typeSig, IMemberInfo context, boolean erasure, StringBuffer buf) throws ModelException {
		return buf.append(typeSig);
	}
}
