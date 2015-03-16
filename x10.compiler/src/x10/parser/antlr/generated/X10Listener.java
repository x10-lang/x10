// Generated from /Users/lmandel/x10/x10-parser/x10.compiler/src/x10/parser/antlr/X10.g4 by ANTLR 4.5

  package x10.parser.antlr.generated;
  
  import x10.parser.antlr.ASTBuilder.Modifier;
  import polyglot.parse.*;
  import polyglot.ast.*;
  import x10.ast.*;

import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link X10Parser}.
 */
public interface X10Listener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link X10Parser#modifiersopt}.
	 * @param ctx the parse tree
	 */
	void enterModifiersopt(X10Parser.ModifiersoptContext ctx);
	/**
	 * Exit a parse tree produced by {@link X10Parser#modifiersopt}.
	 * @param ctx the parse tree
	 */
	void exitModifiersopt(X10Parser.ModifiersoptContext ctx);
	/**
	 * Enter a parse tree produced by the {@code modifierAbstract}
	 * labeled alternative in {@link X10Parser#modifier}.
	 * @param ctx the parse tree
	 */
	void enterModifierAbstract(X10Parser.ModifierAbstractContext ctx);
	/**
	 * Exit a parse tree produced by the {@code modifierAbstract}
	 * labeled alternative in {@link X10Parser#modifier}.
	 * @param ctx the parse tree
	 */
	void exitModifierAbstract(X10Parser.ModifierAbstractContext ctx);
	/**
	 * Enter a parse tree produced by the {@code modifierAnnotation}
	 * labeled alternative in {@link X10Parser#modifier}.
	 * @param ctx the parse tree
	 */
	void enterModifierAnnotation(X10Parser.ModifierAnnotationContext ctx);
	/**
	 * Exit a parse tree produced by the {@code modifierAnnotation}
	 * labeled alternative in {@link X10Parser#modifier}.
	 * @param ctx the parse tree
	 */
	void exitModifierAnnotation(X10Parser.ModifierAnnotationContext ctx);
	/**
	 * Enter a parse tree produced by the {@code modifierAtomic}
	 * labeled alternative in {@link X10Parser#modifier}.
	 * @param ctx the parse tree
	 */
	void enterModifierAtomic(X10Parser.ModifierAtomicContext ctx);
	/**
	 * Exit a parse tree produced by the {@code modifierAtomic}
	 * labeled alternative in {@link X10Parser#modifier}.
	 * @param ctx the parse tree
	 */
	void exitModifierAtomic(X10Parser.ModifierAtomicContext ctx);
	/**
	 * Enter a parse tree produced by the {@code modifierFinal}
	 * labeled alternative in {@link X10Parser#modifier}.
	 * @param ctx the parse tree
	 */
	void enterModifierFinal(X10Parser.ModifierFinalContext ctx);
	/**
	 * Exit a parse tree produced by the {@code modifierFinal}
	 * labeled alternative in {@link X10Parser#modifier}.
	 * @param ctx the parse tree
	 */
	void exitModifierFinal(X10Parser.ModifierFinalContext ctx);
	/**
	 * Enter a parse tree produced by the {@code modifierNative}
	 * labeled alternative in {@link X10Parser#modifier}.
	 * @param ctx the parse tree
	 */
	void enterModifierNative(X10Parser.ModifierNativeContext ctx);
	/**
	 * Exit a parse tree produced by the {@code modifierNative}
	 * labeled alternative in {@link X10Parser#modifier}.
	 * @param ctx the parse tree
	 */
	void exitModifierNative(X10Parser.ModifierNativeContext ctx);
	/**
	 * Enter a parse tree produced by the {@code modifierPrivate}
	 * labeled alternative in {@link X10Parser#modifier}.
	 * @param ctx the parse tree
	 */
	void enterModifierPrivate(X10Parser.ModifierPrivateContext ctx);
	/**
	 * Exit a parse tree produced by the {@code modifierPrivate}
	 * labeled alternative in {@link X10Parser#modifier}.
	 * @param ctx the parse tree
	 */
	void exitModifierPrivate(X10Parser.ModifierPrivateContext ctx);
	/**
	 * Enter a parse tree produced by the {@code modifierProtected}
	 * labeled alternative in {@link X10Parser#modifier}.
	 * @param ctx the parse tree
	 */
	void enterModifierProtected(X10Parser.ModifierProtectedContext ctx);
	/**
	 * Exit a parse tree produced by the {@code modifierProtected}
	 * labeled alternative in {@link X10Parser#modifier}.
	 * @param ctx the parse tree
	 */
	void exitModifierProtected(X10Parser.ModifierProtectedContext ctx);
	/**
	 * Enter a parse tree produced by the {@code modifierPublic}
	 * labeled alternative in {@link X10Parser#modifier}.
	 * @param ctx the parse tree
	 */
	void enterModifierPublic(X10Parser.ModifierPublicContext ctx);
	/**
	 * Exit a parse tree produced by the {@code modifierPublic}
	 * labeled alternative in {@link X10Parser#modifier}.
	 * @param ctx the parse tree
	 */
	void exitModifierPublic(X10Parser.ModifierPublicContext ctx);
	/**
	 * Enter a parse tree produced by the {@code modifierStatic}
	 * labeled alternative in {@link X10Parser#modifier}.
	 * @param ctx the parse tree
	 */
	void enterModifierStatic(X10Parser.ModifierStaticContext ctx);
	/**
	 * Exit a parse tree produced by the {@code modifierStatic}
	 * labeled alternative in {@link X10Parser#modifier}.
	 * @param ctx the parse tree
	 */
	void exitModifierStatic(X10Parser.ModifierStaticContext ctx);
	/**
	 * Enter a parse tree produced by the {@code modifierTransient}
	 * labeled alternative in {@link X10Parser#modifier}.
	 * @param ctx the parse tree
	 */
	void enterModifierTransient(X10Parser.ModifierTransientContext ctx);
	/**
	 * Exit a parse tree produced by the {@code modifierTransient}
	 * labeled alternative in {@link X10Parser#modifier}.
	 * @param ctx the parse tree
	 */
	void exitModifierTransient(X10Parser.ModifierTransientContext ctx);
	/**
	 * Enter a parse tree produced by the {@code modifierClocked}
	 * labeled alternative in {@link X10Parser#modifier}.
	 * @param ctx the parse tree
	 */
	void enterModifierClocked(X10Parser.ModifierClockedContext ctx);
	/**
	 * Exit a parse tree produced by the {@code modifierClocked}
	 * labeled alternative in {@link X10Parser#modifier}.
	 * @param ctx the parse tree
	 */
	void exitModifierClocked(X10Parser.ModifierClockedContext ctx);
	/**
	 * Enter a parse tree produced by {@link X10Parser#methodModifiersopt}.
	 * @param ctx the parse tree
	 */
	void enterMethodModifiersopt(X10Parser.MethodModifiersoptContext ctx);
	/**
	 * Exit a parse tree produced by {@link X10Parser#methodModifiersopt}.
	 * @param ctx the parse tree
	 */
	void exitMethodModifiersopt(X10Parser.MethodModifiersoptContext ctx);
	/**
	 * Enter a parse tree produced by the {@code methodModifierModifier}
	 * labeled alternative in {@link X10Parser#methodModifier}.
	 * @param ctx the parse tree
	 */
	void enterMethodModifierModifier(X10Parser.MethodModifierModifierContext ctx);
	/**
	 * Exit a parse tree produced by the {@code methodModifierModifier}
	 * labeled alternative in {@link X10Parser#methodModifier}.
	 * @param ctx the parse tree
	 */
	void exitMethodModifierModifier(X10Parser.MethodModifierModifierContext ctx);
	/**
	 * Enter a parse tree produced by the {@code methodModifierProperty}
	 * labeled alternative in {@link X10Parser#methodModifier}.
	 * @param ctx the parse tree
	 */
	void enterMethodModifierProperty(X10Parser.MethodModifierPropertyContext ctx);
	/**
	 * Exit a parse tree produced by the {@code methodModifierProperty}
	 * labeled alternative in {@link X10Parser#methodModifier}.
	 * @param ctx the parse tree
	 */
	void exitMethodModifierProperty(X10Parser.MethodModifierPropertyContext ctx);
	/**
	 * Enter a parse tree produced by {@link X10Parser#typeDefDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterTypeDefDeclaration(X10Parser.TypeDefDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link X10Parser#typeDefDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitTypeDefDeclaration(X10Parser.TypeDefDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link X10Parser#propertiesopt}.
	 * @param ctx the parse tree
	 */
	void enterPropertiesopt(X10Parser.PropertiesoptContext ctx);
	/**
	 * Exit a parse tree produced by {@link X10Parser#propertiesopt}.
	 * @param ctx the parse tree
	 */
	void exitPropertiesopt(X10Parser.PropertiesoptContext ctx);
	/**
	 * Enter a parse tree produced by {@link X10Parser#property}.
	 * @param ctx the parse tree
	 */
	void enterProperty(X10Parser.PropertyContext ctx);
	/**
	 * Exit a parse tree produced by {@link X10Parser#property}.
	 * @param ctx the parse tree
	 */
	void exitProperty(X10Parser.PropertyContext ctx);
	/**
	 * Enter a parse tree produced by the {@code methodDeclarationMethod}
	 * labeled alternative in {@link X10Parser#methodDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterMethodDeclarationMethod(X10Parser.MethodDeclarationMethodContext ctx);
	/**
	 * Exit a parse tree produced by the {@code methodDeclarationMethod}
	 * labeled alternative in {@link X10Parser#methodDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitMethodDeclarationMethod(X10Parser.MethodDeclarationMethodContext ctx);
	/**
	 * Enter a parse tree produced by the {@code methodDeclarationBinaryOp}
	 * labeled alternative in {@link X10Parser#methodDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterMethodDeclarationBinaryOp(X10Parser.MethodDeclarationBinaryOpContext ctx);
	/**
	 * Exit a parse tree produced by the {@code methodDeclarationBinaryOp}
	 * labeled alternative in {@link X10Parser#methodDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitMethodDeclarationBinaryOp(X10Parser.MethodDeclarationBinaryOpContext ctx);
	/**
	 * Enter a parse tree produced by the {@code methodDeclarationPrefixOp}
	 * labeled alternative in {@link X10Parser#methodDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterMethodDeclarationPrefixOp(X10Parser.MethodDeclarationPrefixOpContext ctx);
	/**
	 * Exit a parse tree produced by the {@code methodDeclarationPrefixOp}
	 * labeled alternative in {@link X10Parser#methodDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitMethodDeclarationPrefixOp(X10Parser.MethodDeclarationPrefixOpContext ctx);
	/**
	 * Enter a parse tree produced by the {@code methodDeclarationApplyOp}
	 * labeled alternative in {@link X10Parser#methodDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterMethodDeclarationApplyOp(X10Parser.MethodDeclarationApplyOpContext ctx);
	/**
	 * Exit a parse tree produced by the {@code methodDeclarationApplyOp}
	 * labeled alternative in {@link X10Parser#methodDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitMethodDeclarationApplyOp(X10Parser.MethodDeclarationApplyOpContext ctx);
	/**
	 * Enter a parse tree produced by the {@code methodDeclarationSetOp}
	 * labeled alternative in {@link X10Parser#methodDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterMethodDeclarationSetOp(X10Parser.MethodDeclarationSetOpContext ctx);
	/**
	 * Exit a parse tree produced by the {@code methodDeclarationSetOp}
	 * labeled alternative in {@link X10Parser#methodDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitMethodDeclarationSetOp(X10Parser.MethodDeclarationSetOpContext ctx);
	/**
	 * Enter a parse tree produced by the {@code methodDeclarationConversionOp}
	 * labeled alternative in {@link X10Parser#methodDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterMethodDeclarationConversionOp(X10Parser.MethodDeclarationConversionOpContext ctx);
	/**
	 * Exit a parse tree produced by the {@code methodDeclarationConversionOp}
	 * labeled alternative in {@link X10Parser#methodDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitMethodDeclarationConversionOp(X10Parser.MethodDeclarationConversionOpContext ctx);
	/**
	 * Enter a parse tree produced by the {@code binaryOperatorDecl}
	 * labeled alternative in {@link X10Parser#binaryOperatorDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterBinaryOperatorDecl(X10Parser.BinaryOperatorDeclContext ctx);
	/**
	 * Exit a parse tree produced by the {@code binaryOperatorDecl}
	 * labeled alternative in {@link X10Parser#binaryOperatorDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitBinaryOperatorDecl(X10Parser.BinaryOperatorDeclContext ctx);
	/**
	 * Enter a parse tree produced by the {@code binaryOperatorDeclThisLeft}
	 * labeled alternative in {@link X10Parser#binaryOperatorDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterBinaryOperatorDeclThisLeft(X10Parser.BinaryOperatorDeclThisLeftContext ctx);
	/**
	 * Exit a parse tree produced by the {@code binaryOperatorDeclThisLeft}
	 * labeled alternative in {@link X10Parser#binaryOperatorDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitBinaryOperatorDeclThisLeft(X10Parser.BinaryOperatorDeclThisLeftContext ctx);
	/**
	 * Enter a parse tree produced by the {@code binaryOperatorDeclThisRight}
	 * labeled alternative in {@link X10Parser#binaryOperatorDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterBinaryOperatorDeclThisRight(X10Parser.BinaryOperatorDeclThisRightContext ctx);
	/**
	 * Exit a parse tree produced by the {@code binaryOperatorDeclThisRight}
	 * labeled alternative in {@link X10Parser#binaryOperatorDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitBinaryOperatorDeclThisRight(X10Parser.BinaryOperatorDeclThisRightContext ctx);
	/**
	 * Enter a parse tree produced by the {@code prefixOperatorDecl}
	 * labeled alternative in {@link X10Parser#prefixOperatorDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterPrefixOperatorDecl(X10Parser.PrefixOperatorDeclContext ctx);
	/**
	 * Exit a parse tree produced by the {@code prefixOperatorDecl}
	 * labeled alternative in {@link X10Parser#prefixOperatorDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitPrefixOperatorDecl(X10Parser.PrefixOperatorDeclContext ctx);
	/**
	 * Enter a parse tree produced by the {@code prefixOperatorDeclThis}
	 * labeled alternative in {@link X10Parser#prefixOperatorDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterPrefixOperatorDeclThis(X10Parser.PrefixOperatorDeclThisContext ctx);
	/**
	 * Exit a parse tree produced by the {@code prefixOperatorDeclThis}
	 * labeled alternative in {@link X10Parser#prefixOperatorDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitPrefixOperatorDeclThis(X10Parser.PrefixOperatorDeclThisContext ctx);
	/**
	 * Enter a parse tree produced by {@link X10Parser#applyOperatorDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterApplyOperatorDeclaration(X10Parser.ApplyOperatorDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link X10Parser#applyOperatorDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitApplyOperatorDeclaration(X10Parser.ApplyOperatorDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link X10Parser#setOperatorDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterSetOperatorDeclaration(X10Parser.SetOperatorDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link X10Parser#setOperatorDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitSetOperatorDeclaration(X10Parser.SetOperatorDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by the {@code conversionOperatorDeclarationExplicit}
	 * labeled alternative in {@link X10Parser#conversionOperatorDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterConversionOperatorDeclarationExplicit(X10Parser.ConversionOperatorDeclarationExplicitContext ctx);
	/**
	 * Exit a parse tree produced by the {@code conversionOperatorDeclarationExplicit}
	 * labeled alternative in {@link X10Parser#conversionOperatorDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitConversionOperatorDeclarationExplicit(X10Parser.ConversionOperatorDeclarationExplicitContext ctx);
	/**
	 * Enter a parse tree produced by the {@code conversionOperatorDeclarationImplicit}
	 * labeled alternative in {@link X10Parser#conversionOperatorDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterConversionOperatorDeclarationImplicit(X10Parser.ConversionOperatorDeclarationImplicitContext ctx);
	/**
	 * Exit a parse tree produced by the {@code conversionOperatorDeclarationImplicit}
	 * labeled alternative in {@link X10Parser#conversionOperatorDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitConversionOperatorDeclarationImplicit(X10Parser.ConversionOperatorDeclarationImplicitContext ctx);
	/**
	 * Enter a parse tree produced by the {@code explicitConversionOperatorDecl0}
	 * labeled alternative in {@link X10Parser#explicitConversionOperatorDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterExplicitConversionOperatorDecl0(X10Parser.ExplicitConversionOperatorDecl0Context ctx);
	/**
	 * Exit a parse tree produced by the {@code explicitConversionOperatorDecl0}
	 * labeled alternative in {@link X10Parser#explicitConversionOperatorDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitExplicitConversionOperatorDecl0(X10Parser.ExplicitConversionOperatorDecl0Context ctx);
	/**
	 * Enter a parse tree produced by the {@code explicitConversionOperatorDecl1}
	 * labeled alternative in {@link X10Parser#explicitConversionOperatorDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterExplicitConversionOperatorDecl1(X10Parser.ExplicitConversionOperatorDecl1Context ctx);
	/**
	 * Exit a parse tree produced by the {@code explicitConversionOperatorDecl1}
	 * labeled alternative in {@link X10Parser#explicitConversionOperatorDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitExplicitConversionOperatorDecl1(X10Parser.ExplicitConversionOperatorDecl1Context ctx);
	/**
	 * Enter a parse tree produced by {@link X10Parser#implicitConversionOperatorDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterImplicitConversionOperatorDeclaration(X10Parser.ImplicitConversionOperatorDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link X10Parser#implicitConversionOperatorDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitImplicitConversionOperatorDeclaration(X10Parser.ImplicitConversionOperatorDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by the {@code propertyMethodDecl0}
	 * labeled alternative in {@link X10Parser#propertyMethodDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterPropertyMethodDecl0(X10Parser.PropertyMethodDecl0Context ctx);
	/**
	 * Exit a parse tree produced by the {@code propertyMethodDecl0}
	 * labeled alternative in {@link X10Parser#propertyMethodDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitPropertyMethodDecl0(X10Parser.PropertyMethodDecl0Context ctx);
	/**
	 * Enter a parse tree produced by the {@code propertyMethodDecl1}
	 * labeled alternative in {@link X10Parser#propertyMethodDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterPropertyMethodDecl1(X10Parser.PropertyMethodDecl1Context ctx);
	/**
	 * Exit a parse tree produced by the {@code propertyMethodDecl1}
	 * labeled alternative in {@link X10Parser#propertyMethodDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitPropertyMethodDecl1(X10Parser.PropertyMethodDecl1Context ctx);
	/**
	 * Enter a parse tree produced by the {@code explicitConstructorInvocationThis}
	 * labeled alternative in {@link X10Parser#explicitConstructorInvocation}.
	 * @param ctx the parse tree
	 */
	void enterExplicitConstructorInvocationThis(X10Parser.ExplicitConstructorInvocationThisContext ctx);
	/**
	 * Exit a parse tree produced by the {@code explicitConstructorInvocationThis}
	 * labeled alternative in {@link X10Parser#explicitConstructorInvocation}.
	 * @param ctx the parse tree
	 */
	void exitExplicitConstructorInvocationThis(X10Parser.ExplicitConstructorInvocationThisContext ctx);
	/**
	 * Enter a parse tree produced by the {@code explicitConstructorInvocationSuper}
	 * labeled alternative in {@link X10Parser#explicitConstructorInvocation}.
	 * @param ctx the parse tree
	 */
	void enterExplicitConstructorInvocationSuper(X10Parser.ExplicitConstructorInvocationSuperContext ctx);
	/**
	 * Exit a parse tree produced by the {@code explicitConstructorInvocationSuper}
	 * labeled alternative in {@link X10Parser#explicitConstructorInvocation}.
	 * @param ctx the parse tree
	 */
	void exitExplicitConstructorInvocationSuper(X10Parser.ExplicitConstructorInvocationSuperContext ctx);
	/**
	 * Enter a parse tree produced by the {@code explicitConstructorInvocationPrimaryThis}
	 * labeled alternative in {@link X10Parser#explicitConstructorInvocation}.
	 * @param ctx the parse tree
	 */
	void enterExplicitConstructorInvocationPrimaryThis(X10Parser.ExplicitConstructorInvocationPrimaryThisContext ctx);
	/**
	 * Exit a parse tree produced by the {@code explicitConstructorInvocationPrimaryThis}
	 * labeled alternative in {@link X10Parser#explicitConstructorInvocation}.
	 * @param ctx the parse tree
	 */
	void exitExplicitConstructorInvocationPrimaryThis(X10Parser.ExplicitConstructorInvocationPrimaryThisContext ctx);
	/**
	 * Enter a parse tree produced by the {@code explicitConstructorInvocationPrimarySuper}
	 * labeled alternative in {@link X10Parser#explicitConstructorInvocation}.
	 * @param ctx the parse tree
	 */
	void enterExplicitConstructorInvocationPrimarySuper(X10Parser.ExplicitConstructorInvocationPrimarySuperContext ctx);
	/**
	 * Exit a parse tree produced by the {@code explicitConstructorInvocationPrimarySuper}
	 * labeled alternative in {@link X10Parser#explicitConstructorInvocation}.
	 * @param ctx the parse tree
	 */
	void exitExplicitConstructorInvocationPrimarySuper(X10Parser.ExplicitConstructorInvocationPrimarySuperContext ctx);
	/**
	 * Enter a parse tree produced by {@link X10Parser#interfaceDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterInterfaceDeclaration(X10Parser.InterfaceDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link X10Parser#interfaceDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitInterfaceDeclaration(X10Parser.InterfaceDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link X10Parser#assignPropertyCall}.
	 * @param ctx the parse tree
	 */
	void enterAssignPropertyCall(X10Parser.AssignPropertyCallContext ctx);
	/**
	 * Exit a parse tree produced by {@link X10Parser#assignPropertyCall}.
	 * @param ctx the parse tree
	 */
	void exitAssignPropertyCall(X10Parser.AssignPropertyCallContext ctx);
	/**
	 * Enter a parse tree produced by the {@code typeConstrainedType}
	 * labeled alternative in {@link X10Parser#type}.
	 * @param ctx the parse tree
	 */
	void enterTypeConstrainedType(X10Parser.TypeConstrainedTypeContext ctx);
	/**
	 * Exit a parse tree produced by the {@code typeConstrainedType}
	 * labeled alternative in {@link X10Parser#type}.
	 * @param ctx the parse tree
	 */
	void exitTypeConstrainedType(X10Parser.TypeConstrainedTypeContext ctx);
	/**
	 * Enter a parse tree produced by the {@code typeFunctionType}
	 * labeled alternative in {@link X10Parser#type}.
	 * @param ctx the parse tree
	 */
	void enterTypeFunctionType(X10Parser.TypeFunctionTypeContext ctx);
	/**
	 * Exit a parse tree produced by the {@code typeFunctionType}
	 * labeled alternative in {@link X10Parser#type}.
	 * @param ctx the parse tree
	 */
	void exitTypeFunctionType(X10Parser.TypeFunctionTypeContext ctx);
	/**
	 * Enter a parse tree produced by the {@code typeVoid}
	 * labeled alternative in {@link X10Parser#type}.
	 * @param ctx the parse tree
	 */
	void enterTypeVoid(X10Parser.TypeVoidContext ctx);
	/**
	 * Exit a parse tree produced by the {@code typeVoid}
	 * labeled alternative in {@link X10Parser#type}.
	 * @param ctx the parse tree
	 */
	void exitTypeVoid(X10Parser.TypeVoidContext ctx);
	/**
	 * Enter a parse tree produced by the {@code typeAnnotations}
	 * labeled alternative in {@link X10Parser#type}.
	 * @param ctx the parse tree
	 */
	void enterTypeAnnotations(X10Parser.TypeAnnotationsContext ctx);
	/**
	 * Exit a parse tree produced by the {@code typeAnnotations}
	 * labeled alternative in {@link X10Parser#type}.
	 * @param ctx the parse tree
	 */
	void exitTypeAnnotations(X10Parser.TypeAnnotationsContext ctx);
	/**
	 * Enter a parse tree produced by {@link X10Parser#functionType}.
	 * @param ctx the parse tree
	 */
	void enterFunctionType(X10Parser.FunctionTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link X10Parser#functionType}.
	 * @param ctx the parse tree
	 */
	void exitFunctionType(X10Parser.FunctionTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link X10Parser#classType}.
	 * @param ctx the parse tree
	 */
	void enterClassType(X10Parser.ClassTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link X10Parser#classType}.
	 * @param ctx the parse tree
	 */
	void exitClassType(X10Parser.ClassTypeContext ctx);
	/**
	 * Enter a parse tree produced by the {@code simpleNamedType0}
	 * labeled alternative in {@link X10Parser#simpleNamedType}.
	 * @param ctx the parse tree
	 */
	void enterSimpleNamedType0(X10Parser.SimpleNamedType0Context ctx);
	/**
	 * Exit a parse tree produced by the {@code simpleNamedType0}
	 * labeled alternative in {@link X10Parser#simpleNamedType}.
	 * @param ctx the parse tree
	 */
	void exitSimpleNamedType0(X10Parser.SimpleNamedType0Context ctx);
	/**
	 * Enter a parse tree produced by the {@code simpleNamedType2}
	 * labeled alternative in {@link X10Parser#simpleNamedType}.
	 * @param ctx the parse tree
	 */
	void enterSimpleNamedType2(X10Parser.SimpleNamedType2Context ctx);
	/**
	 * Exit a parse tree produced by the {@code simpleNamedType2}
	 * labeled alternative in {@link X10Parser#simpleNamedType}.
	 * @param ctx the parse tree
	 */
	void exitSimpleNamedType2(X10Parser.SimpleNamedType2Context ctx);
	/**
	 * Enter a parse tree produced by the {@code simpleNamedType1}
	 * labeled alternative in {@link X10Parser#simpleNamedType}.
	 * @param ctx the parse tree
	 */
	void enterSimpleNamedType1(X10Parser.SimpleNamedType1Context ctx);
	/**
	 * Exit a parse tree produced by the {@code simpleNamedType1}
	 * labeled alternative in {@link X10Parser#simpleNamedType}.
	 * @param ctx the parse tree
	 */
	void exitSimpleNamedType1(X10Parser.SimpleNamedType1Context ctx);
	/**
	 * Enter a parse tree produced by {@link X10Parser#namedTypeNoConstraints}.
	 * @param ctx the parse tree
	 */
	void enterNamedTypeNoConstraints(X10Parser.NamedTypeNoConstraintsContext ctx);
	/**
	 * Exit a parse tree produced by {@link X10Parser#namedTypeNoConstraints}.
	 * @param ctx the parse tree
	 */
	void exitNamedTypeNoConstraints(X10Parser.NamedTypeNoConstraintsContext ctx);
	/**
	 * Enter a parse tree produced by {@link X10Parser#namedType}.
	 * @param ctx the parse tree
	 */
	void enterNamedType(X10Parser.NamedTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link X10Parser#namedType}.
	 * @param ctx the parse tree
	 */
	void exitNamedType(X10Parser.NamedTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link X10Parser#depParameters}.
	 * @param ctx the parse tree
	 */
	void enterDepParameters(X10Parser.DepParametersContext ctx);
	/**
	 * Exit a parse tree produced by {@link X10Parser#depParameters}.
	 * @param ctx the parse tree
	 */
	void exitDepParameters(X10Parser.DepParametersContext ctx);
	/**
	 * Enter a parse tree produced by {@link X10Parser#typeParamsWithVarianceopt}.
	 * @param ctx the parse tree
	 */
	void enterTypeParamsWithVarianceopt(X10Parser.TypeParamsWithVarianceoptContext ctx);
	/**
	 * Exit a parse tree produced by {@link X10Parser#typeParamsWithVarianceopt}.
	 * @param ctx the parse tree
	 */
	void exitTypeParamsWithVarianceopt(X10Parser.TypeParamsWithVarianceoptContext ctx);
	/**
	 * Enter a parse tree produced by {@link X10Parser#typeParametersopt}.
	 * @param ctx the parse tree
	 */
	void enterTypeParametersopt(X10Parser.TypeParametersoptContext ctx);
	/**
	 * Exit a parse tree produced by {@link X10Parser#typeParametersopt}.
	 * @param ctx the parse tree
	 */
	void exitTypeParametersopt(X10Parser.TypeParametersoptContext ctx);
	/**
	 * Enter a parse tree produced by {@link X10Parser#formalParameters}.
	 * @param ctx the parse tree
	 */
	void enterFormalParameters(X10Parser.FormalParametersContext ctx);
	/**
	 * Exit a parse tree produced by {@link X10Parser#formalParameters}.
	 * @param ctx the parse tree
	 */
	void exitFormalParameters(X10Parser.FormalParametersContext ctx);
	/**
	 * Enter a parse tree produced by {@link X10Parser#constraintConjunctionopt}.
	 * @param ctx the parse tree
	 */
	void enterConstraintConjunctionopt(X10Parser.ConstraintConjunctionoptContext ctx);
	/**
	 * Exit a parse tree produced by {@link X10Parser#constraintConjunctionopt}.
	 * @param ctx the parse tree
	 */
	void exitConstraintConjunctionopt(X10Parser.ConstraintConjunctionoptContext ctx);
	/**
	 * Enter a parse tree produced by {@link X10Parser#hasZeroConstraint}.
	 * @param ctx the parse tree
	 */
	void enterHasZeroConstraint(X10Parser.HasZeroConstraintContext ctx);
	/**
	 * Exit a parse tree produced by {@link X10Parser#hasZeroConstraint}.
	 * @param ctx the parse tree
	 */
	void exitHasZeroConstraint(X10Parser.HasZeroConstraintContext ctx);
	/**
	 * Enter a parse tree produced by {@link X10Parser#whereClauseopt}.
	 * @param ctx the parse tree
	 */
	void enterWhereClauseopt(X10Parser.WhereClauseoptContext ctx);
	/**
	 * Exit a parse tree produced by {@link X10Parser#whereClauseopt}.
	 * @param ctx the parse tree
	 */
	void exitWhereClauseopt(X10Parser.WhereClauseoptContext ctx);
	/**
	 * Enter a parse tree produced by {@link X10Parser#classDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterClassDeclaration(X10Parser.ClassDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link X10Parser#classDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitClassDeclaration(X10Parser.ClassDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link X10Parser#structDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterStructDeclaration(X10Parser.StructDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link X10Parser#structDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitStructDeclaration(X10Parser.StructDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link X10Parser#constructorDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterConstructorDeclaration(X10Parser.ConstructorDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link X10Parser#constructorDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitConstructorDeclaration(X10Parser.ConstructorDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link X10Parser#superExtendsopt}.
	 * @param ctx the parse tree
	 */
	void enterSuperExtendsopt(X10Parser.SuperExtendsoptContext ctx);
	/**
	 * Exit a parse tree produced by {@link X10Parser#superExtendsopt}.
	 * @param ctx the parse tree
	 */
	void exitSuperExtendsopt(X10Parser.SuperExtendsoptContext ctx);
	/**
	 * Enter a parse tree produced by the {@code varKeyword0}
	 * labeled alternative in {@link X10Parser#varKeyword}.
	 * @param ctx the parse tree
	 */
	void enterVarKeyword0(X10Parser.VarKeyword0Context ctx);
	/**
	 * Exit a parse tree produced by the {@code varKeyword0}
	 * labeled alternative in {@link X10Parser#varKeyword}.
	 * @param ctx the parse tree
	 */
	void exitVarKeyword0(X10Parser.VarKeyword0Context ctx);
	/**
	 * Enter a parse tree produced by the {@code varKeyword1}
	 * labeled alternative in {@link X10Parser#varKeyword}.
	 * @param ctx the parse tree
	 */
	void enterVarKeyword1(X10Parser.VarKeyword1Context ctx);
	/**
	 * Exit a parse tree produced by the {@code varKeyword1}
	 * labeled alternative in {@link X10Parser#varKeyword}.
	 * @param ctx the parse tree
	 */
	void exitVarKeyword1(X10Parser.VarKeyword1Context ctx);
	/**
	 * Enter a parse tree produced by {@link X10Parser#fieldDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterFieldDeclaration(X10Parser.FieldDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link X10Parser#fieldDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitFieldDeclaration(X10Parser.FieldDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by the {@code statement0}
	 * labeled alternative in {@link X10Parser#statement}.
	 * @param ctx the parse tree
	 */
	void enterStatement0(X10Parser.Statement0Context ctx);
	/**
	 * Exit a parse tree produced by the {@code statement0}
	 * labeled alternative in {@link X10Parser#statement}.
	 * @param ctx the parse tree
	 */
	void exitStatement0(X10Parser.Statement0Context ctx);
	/**
	 * Enter a parse tree produced by the {@code statement1}
	 * labeled alternative in {@link X10Parser#statement}.
	 * @param ctx the parse tree
	 */
	void enterStatement1(X10Parser.Statement1Context ctx);
	/**
	 * Exit a parse tree produced by the {@code statement1}
	 * labeled alternative in {@link X10Parser#statement}.
	 * @param ctx the parse tree
	 */
	void exitStatement1(X10Parser.Statement1Context ctx);
	/**
	 * Enter a parse tree produced by {@link X10Parser#annotationStatement}.
	 * @param ctx the parse tree
	 */
	void enterAnnotationStatement(X10Parser.AnnotationStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link X10Parser#annotationStatement}.
	 * @param ctx the parse tree
	 */
	void exitAnnotationStatement(X10Parser.AnnotationStatementContext ctx);
	/**
	 * Enter a parse tree produced by the {@code nonExpressionStatemen0}
	 * labeled alternative in {@link X10Parser#nonExpressionStatement}.
	 * @param ctx the parse tree
	 */
	void enterNonExpressionStatemen0(X10Parser.NonExpressionStatemen0Context ctx);
	/**
	 * Exit a parse tree produced by the {@code nonExpressionStatemen0}
	 * labeled alternative in {@link X10Parser#nonExpressionStatement}.
	 * @param ctx the parse tree
	 */
	void exitNonExpressionStatemen0(X10Parser.NonExpressionStatemen0Context ctx);
	/**
	 * Enter a parse tree produced by the {@code nonExpressionStatemen1}
	 * labeled alternative in {@link X10Parser#nonExpressionStatement}.
	 * @param ctx the parse tree
	 */
	void enterNonExpressionStatemen1(X10Parser.NonExpressionStatemen1Context ctx);
	/**
	 * Exit a parse tree produced by the {@code nonExpressionStatemen1}
	 * labeled alternative in {@link X10Parser#nonExpressionStatement}.
	 * @param ctx the parse tree
	 */
	void exitNonExpressionStatemen1(X10Parser.NonExpressionStatemen1Context ctx);
	/**
	 * Enter a parse tree produced by the {@code nonExpressionStatemen2}
	 * labeled alternative in {@link X10Parser#nonExpressionStatement}.
	 * @param ctx the parse tree
	 */
	void enterNonExpressionStatemen2(X10Parser.NonExpressionStatemen2Context ctx);
	/**
	 * Exit a parse tree produced by the {@code nonExpressionStatemen2}
	 * labeled alternative in {@link X10Parser#nonExpressionStatement}.
	 * @param ctx the parse tree
	 */
	void exitNonExpressionStatemen2(X10Parser.NonExpressionStatemen2Context ctx);
	/**
	 * Enter a parse tree produced by the {@code nonExpressionStatemen3}
	 * labeled alternative in {@link X10Parser#nonExpressionStatement}.
	 * @param ctx the parse tree
	 */
	void enterNonExpressionStatemen3(X10Parser.NonExpressionStatemen3Context ctx);
	/**
	 * Exit a parse tree produced by the {@code nonExpressionStatemen3}
	 * labeled alternative in {@link X10Parser#nonExpressionStatement}.
	 * @param ctx the parse tree
	 */
	void exitNonExpressionStatemen3(X10Parser.NonExpressionStatemen3Context ctx);
	/**
	 * Enter a parse tree produced by the {@code nonExpressionStatemen4}
	 * labeled alternative in {@link X10Parser#nonExpressionStatement}.
	 * @param ctx the parse tree
	 */
	void enterNonExpressionStatemen4(X10Parser.NonExpressionStatemen4Context ctx);
	/**
	 * Exit a parse tree produced by the {@code nonExpressionStatemen4}
	 * labeled alternative in {@link X10Parser#nonExpressionStatement}.
	 * @param ctx the parse tree
	 */
	void exitNonExpressionStatemen4(X10Parser.NonExpressionStatemen4Context ctx);
	/**
	 * Enter a parse tree produced by the {@code nonExpressionStatemen5}
	 * labeled alternative in {@link X10Parser#nonExpressionStatement}.
	 * @param ctx the parse tree
	 */
	void enterNonExpressionStatemen5(X10Parser.NonExpressionStatemen5Context ctx);
	/**
	 * Exit a parse tree produced by the {@code nonExpressionStatemen5}
	 * labeled alternative in {@link X10Parser#nonExpressionStatement}.
	 * @param ctx the parse tree
	 */
	void exitNonExpressionStatemen5(X10Parser.NonExpressionStatemen5Context ctx);
	/**
	 * Enter a parse tree produced by the {@code nonExpressionStatemen6}
	 * labeled alternative in {@link X10Parser#nonExpressionStatement}.
	 * @param ctx the parse tree
	 */
	void enterNonExpressionStatemen6(X10Parser.NonExpressionStatemen6Context ctx);
	/**
	 * Exit a parse tree produced by the {@code nonExpressionStatemen6}
	 * labeled alternative in {@link X10Parser#nonExpressionStatement}.
	 * @param ctx the parse tree
	 */
	void exitNonExpressionStatemen6(X10Parser.NonExpressionStatemen6Context ctx);
	/**
	 * Enter a parse tree produced by the {@code nonExpressionStatemen7}
	 * labeled alternative in {@link X10Parser#nonExpressionStatement}.
	 * @param ctx the parse tree
	 */
	void enterNonExpressionStatemen7(X10Parser.NonExpressionStatemen7Context ctx);
	/**
	 * Exit a parse tree produced by the {@code nonExpressionStatemen7}
	 * labeled alternative in {@link X10Parser#nonExpressionStatement}.
	 * @param ctx the parse tree
	 */
	void exitNonExpressionStatemen7(X10Parser.NonExpressionStatemen7Context ctx);
	/**
	 * Enter a parse tree produced by the {@code nonExpressionStatemen8}
	 * labeled alternative in {@link X10Parser#nonExpressionStatement}.
	 * @param ctx the parse tree
	 */
	void enterNonExpressionStatemen8(X10Parser.NonExpressionStatemen8Context ctx);
	/**
	 * Exit a parse tree produced by the {@code nonExpressionStatemen8}
	 * labeled alternative in {@link X10Parser#nonExpressionStatement}.
	 * @param ctx the parse tree
	 */
	void exitNonExpressionStatemen8(X10Parser.NonExpressionStatemen8Context ctx);
	/**
	 * Enter a parse tree produced by the {@code nonExpressionStatemen9}
	 * labeled alternative in {@link X10Parser#nonExpressionStatement}.
	 * @param ctx the parse tree
	 */
	void enterNonExpressionStatemen9(X10Parser.NonExpressionStatemen9Context ctx);
	/**
	 * Exit a parse tree produced by the {@code nonExpressionStatemen9}
	 * labeled alternative in {@link X10Parser#nonExpressionStatement}.
	 * @param ctx the parse tree
	 */
	void exitNonExpressionStatemen9(X10Parser.NonExpressionStatemen9Context ctx);
	/**
	 * Enter a parse tree produced by the {@code nonExpressionStatemen10}
	 * labeled alternative in {@link X10Parser#nonExpressionStatement}.
	 * @param ctx the parse tree
	 */
	void enterNonExpressionStatemen10(X10Parser.NonExpressionStatemen10Context ctx);
	/**
	 * Exit a parse tree produced by the {@code nonExpressionStatemen10}
	 * labeled alternative in {@link X10Parser#nonExpressionStatement}.
	 * @param ctx the parse tree
	 */
	void exitNonExpressionStatemen10(X10Parser.NonExpressionStatemen10Context ctx);
	/**
	 * Enter a parse tree produced by the {@code nonExpressionStatemen11}
	 * labeled alternative in {@link X10Parser#nonExpressionStatement}.
	 * @param ctx the parse tree
	 */
	void enterNonExpressionStatemen11(X10Parser.NonExpressionStatemen11Context ctx);
	/**
	 * Exit a parse tree produced by the {@code nonExpressionStatemen11}
	 * labeled alternative in {@link X10Parser#nonExpressionStatement}.
	 * @param ctx the parse tree
	 */
	void exitNonExpressionStatemen11(X10Parser.NonExpressionStatemen11Context ctx);
	/**
	 * Enter a parse tree produced by the {@code nonExpressionStatemen13}
	 * labeled alternative in {@link X10Parser#nonExpressionStatement}.
	 * @param ctx the parse tree
	 */
	void enterNonExpressionStatemen13(X10Parser.NonExpressionStatemen13Context ctx);
	/**
	 * Exit a parse tree produced by the {@code nonExpressionStatemen13}
	 * labeled alternative in {@link X10Parser#nonExpressionStatement}.
	 * @param ctx the parse tree
	 */
	void exitNonExpressionStatemen13(X10Parser.NonExpressionStatemen13Context ctx);
	/**
	 * Enter a parse tree produced by the {@code nonExpressionStatemen14}
	 * labeled alternative in {@link X10Parser#nonExpressionStatement}.
	 * @param ctx the parse tree
	 */
	void enterNonExpressionStatemen14(X10Parser.NonExpressionStatemen14Context ctx);
	/**
	 * Exit a parse tree produced by the {@code nonExpressionStatemen14}
	 * labeled alternative in {@link X10Parser#nonExpressionStatement}.
	 * @param ctx the parse tree
	 */
	void exitNonExpressionStatemen14(X10Parser.NonExpressionStatemen14Context ctx);
	/**
	 * Enter a parse tree produced by the {@code nonExpressionStatemen15}
	 * labeled alternative in {@link X10Parser#nonExpressionStatement}.
	 * @param ctx the parse tree
	 */
	void enterNonExpressionStatemen15(X10Parser.NonExpressionStatemen15Context ctx);
	/**
	 * Exit a parse tree produced by the {@code nonExpressionStatemen15}
	 * labeled alternative in {@link X10Parser#nonExpressionStatement}.
	 * @param ctx the parse tree
	 */
	void exitNonExpressionStatemen15(X10Parser.NonExpressionStatemen15Context ctx);
	/**
	 * Enter a parse tree produced by the {@code nonExpressionStatemen16}
	 * labeled alternative in {@link X10Parser#nonExpressionStatement}.
	 * @param ctx the parse tree
	 */
	void enterNonExpressionStatemen16(X10Parser.NonExpressionStatemen16Context ctx);
	/**
	 * Exit a parse tree produced by the {@code nonExpressionStatemen16}
	 * labeled alternative in {@link X10Parser#nonExpressionStatement}.
	 * @param ctx the parse tree
	 */
	void exitNonExpressionStatemen16(X10Parser.NonExpressionStatemen16Context ctx);
	/**
	 * Enter a parse tree produced by the {@code nonExpressionStatemen17}
	 * labeled alternative in {@link X10Parser#nonExpressionStatement}.
	 * @param ctx the parse tree
	 */
	void enterNonExpressionStatemen17(X10Parser.NonExpressionStatemen17Context ctx);
	/**
	 * Exit a parse tree produced by the {@code nonExpressionStatemen17}
	 * labeled alternative in {@link X10Parser#nonExpressionStatement}.
	 * @param ctx the parse tree
	 */
	void exitNonExpressionStatemen17(X10Parser.NonExpressionStatemen17Context ctx);
	/**
	 * Enter a parse tree produced by the {@code nonExpressionStatemen18}
	 * labeled alternative in {@link X10Parser#nonExpressionStatement}.
	 * @param ctx the parse tree
	 */
	void enterNonExpressionStatemen18(X10Parser.NonExpressionStatemen18Context ctx);
	/**
	 * Exit a parse tree produced by the {@code nonExpressionStatemen18}
	 * labeled alternative in {@link X10Parser#nonExpressionStatement}.
	 * @param ctx the parse tree
	 */
	void exitNonExpressionStatemen18(X10Parser.NonExpressionStatemen18Context ctx);
	/**
	 * Enter a parse tree produced by the {@code nonExpressionStatemen19}
	 * labeled alternative in {@link X10Parser#nonExpressionStatement}.
	 * @param ctx the parse tree
	 */
	void enterNonExpressionStatemen19(X10Parser.NonExpressionStatemen19Context ctx);
	/**
	 * Exit a parse tree produced by the {@code nonExpressionStatemen19}
	 * labeled alternative in {@link X10Parser#nonExpressionStatement}.
	 * @param ctx the parse tree
	 */
	void exitNonExpressionStatemen19(X10Parser.NonExpressionStatemen19Context ctx);
	/**
	 * Enter a parse tree produced by the {@code nonExpressionStatemen20}
	 * labeled alternative in {@link X10Parser#nonExpressionStatement}.
	 * @param ctx the parse tree
	 */
	void enterNonExpressionStatemen20(X10Parser.NonExpressionStatemen20Context ctx);
	/**
	 * Exit a parse tree produced by the {@code nonExpressionStatemen20}
	 * labeled alternative in {@link X10Parser#nonExpressionStatement}.
	 * @param ctx the parse tree
	 */
	void exitNonExpressionStatemen20(X10Parser.NonExpressionStatemen20Context ctx);
	/**
	 * Enter a parse tree produced by the {@code nonExpressionStatemen21}
	 * labeled alternative in {@link X10Parser#nonExpressionStatement}.
	 * @param ctx the parse tree
	 */
	void enterNonExpressionStatemen21(X10Parser.NonExpressionStatemen21Context ctx);
	/**
	 * Exit a parse tree produced by the {@code nonExpressionStatemen21}
	 * labeled alternative in {@link X10Parser#nonExpressionStatement}.
	 * @param ctx the parse tree
	 */
	void exitNonExpressionStatemen21(X10Parser.NonExpressionStatemen21Context ctx);
	/**
	 * Enter a parse tree produced by the {@code nonExpressionStatemen22}
	 * labeled alternative in {@link X10Parser#nonExpressionStatement}.
	 * @param ctx the parse tree
	 */
	void enterNonExpressionStatemen22(X10Parser.NonExpressionStatemen22Context ctx);
	/**
	 * Exit a parse tree produced by the {@code nonExpressionStatemen22}
	 * labeled alternative in {@link X10Parser#nonExpressionStatement}.
	 * @param ctx the parse tree
	 */
	void exitNonExpressionStatemen22(X10Parser.NonExpressionStatemen22Context ctx);
	/**
	 * Enter a parse tree produced by {@link X10Parser#oBSOLETE_OfferStatement}.
	 * @param ctx the parse tree
	 */
	void enterOBSOLETE_OfferStatement(X10Parser.OBSOLETE_OfferStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link X10Parser#oBSOLETE_OfferStatement}.
	 * @param ctx the parse tree
	 */
	void exitOBSOLETE_OfferStatement(X10Parser.OBSOLETE_OfferStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link X10Parser#ifThenStatement}.
	 * @param ctx the parse tree
	 */
	void enterIfThenStatement(X10Parser.IfThenStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link X10Parser#ifThenStatement}.
	 * @param ctx the parse tree
	 */
	void exitIfThenStatement(X10Parser.IfThenStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link X10Parser#emptyStatement}.
	 * @param ctx the parse tree
	 */
	void enterEmptyStatement(X10Parser.EmptyStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link X10Parser#emptyStatement}.
	 * @param ctx the parse tree
	 */
	void exitEmptyStatement(X10Parser.EmptyStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link X10Parser#labeledStatement}.
	 * @param ctx the parse tree
	 */
	void enterLabeledStatement(X10Parser.LabeledStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link X10Parser#labeledStatement}.
	 * @param ctx the parse tree
	 */
	void exitLabeledStatement(X10Parser.LabeledStatementContext ctx);
	/**
	 * Enter a parse tree produced by the {@code loopStatement0}
	 * labeled alternative in {@link X10Parser#loopStatement}.
	 * @param ctx the parse tree
	 */
	void enterLoopStatement0(X10Parser.LoopStatement0Context ctx);
	/**
	 * Exit a parse tree produced by the {@code loopStatement0}
	 * labeled alternative in {@link X10Parser#loopStatement}.
	 * @param ctx the parse tree
	 */
	void exitLoopStatement0(X10Parser.LoopStatement0Context ctx);
	/**
	 * Enter a parse tree produced by the {@code loopStatement1}
	 * labeled alternative in {@link X10Parser#loopStatement}.
	 * @param ctx the parse tree
	 */
	void enterLoopStatement1(X10Parser.LoopStatement1Context ctx);
	/**
	 * Exit a parse tree produced by the {@code loopStatement1}
	 * labeled alternative in {@link X10Parser#loopStatement}.
	 * @param ctx the parse tree
	 */
	void exitLoopStatement1(X10Parser.LoopStatement1Context ctx);
	/**
	 * Enter a parse tree produced by the {@code loopStatement2}
	 * labeled alternative in {@link X10Parser#loopStatement}.
	 * @param ctx the parse tree
	 */
	void enterLoopStatement2(X10Parser.LoopStatement2Context ctx);
	/**
	 * Exit a parse tree produced by the {@code loopStatement2}
	 * labeled alternative in {@link X10Parser#loopStatement}.
	 * @param ctx the parse tree
	 */
	void exitLoopStatement2(X10Parser.LoopStatement2Context ctx);
	/**
	 * Enter a parse tree produced by the {@code loopStatement3}
	 * labeled alternative in {@link X10Parser#loopStatement}.
	 * @param ctx the parse tree
	 */
	void enterLoopStatement3(X10Parser.LoopStatement3Context ctx);
	/**
	 * Exit a parse tree produced by the {@code loopStatement3}
	 * labeled alternative in {@link X10Parser#loopStatement}.
	 * @param ctx the parse tree
	 */
	void exitLoopStatement3(X10Parser.LoopStatement3Context ctx);
	/**
	 * Enter a parse tree produced by {@link X10Parser#expressionStatement}.
	 * @param ctx the parse tree
	 */
	void enterExpressionStatement(X10Parser.ExpressionStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link X10Parser#expressionStatement}.
	 * @param ctx the parse tree
	 */
	void exitExpressionStatement(X10Parser.ExpressionStatementContext ctx);
	/**
	 * Enter a parse tree produced by the {@code assertStatement0}
	 * labeled alternative in {@link X10Parser#assertStatement}.
	 * @param ctx the parse tree
	 */
	void enterAssertStatement0(X10Parser.AssertStatement0Context ctx);
	/**
	 * Exit a parse tree produced by the {@code assertStatement0}
	 * labeled alternative in {@link X10Parser#assertStatement}.
	 * @param ctx the parse tree
	 */
	void exitAssertStatement0(X10Parser.AssertStatement0Context ctx);
	/**
	 * Enter a parse tree produced by the {@code assertStatement1}
	 * labeled alternative in {@link X10Parser#assertStatement}.
	 * @param ctx the parse tree
	 */
	void enterAssertStatement1(X10Parser.AssertStatement1Context ctx);
	/**
	 * Exit a parse tree produced by the {@code assertStatement1}
	 * labeled alternative in {@link X10Parser#assertStatement}.
	 * @param ctx the parse tree
	 */
	void exitAssertStatement1(X10Parser.AssertStatement1Context ctx);
	/**
	 * Enter a parse tree produced by {@link X10Parser#switchStatement}.
	 * @param ctx the parse tree
	 */
	void enterSwitchStatement(X10Parser.SwitchStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link X10Parser#switchStatement}.
	 * @param ctx the parse tree
	 */
	void exitSwitchStatement(X10Parser.SwitchStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link X10Parser#switchBlock}.
	 * @param ctx the parse tree
	 */
	void enterSwitchBlock(X10Parser.SwitchBlockContext ctx);
	/**
	 * Exit a parse tree produced by {@link X10Parser#switchBlock}.
	 * @param ctx the parse tree
	 */
	void exitSwitchBlock(X10Parser.SwitchBlockContext ctx);
	/**
	 * Enter a parse tree produced by {@link X10Parser#switchBlockStatementGroupsopt}.
	 * @param ctx the parse tree
	 */
	void enterSwitchBlockStatementGroupsopt(X10Parser.SwitchBlockStatementGroupsoptContext ctx);
	/**
	 * Exit a parse tree produced by {@link X10Parser#switchBlockStatementGroupsopt}.
	 * @param ctx the parse tree
	 */
	void exitSwitchBlockStatementGroupsopt(X10Parser.SwitchBlockStatementGroupsoptContext ctx);
	/**
	 * Enter a parse tree produced by {@link X10Parser#switchBlockStatementGroup}.
	 * @param ctx the parse tree
	 */
	void enterSwitchBlockStatementGroup(X10Parser.SwitchBlockStatementGroupContext ctx);
	/**
	 * Exit a parse tree produced by {@link X10Parser#switchBlockStatementGroup}.
	 * @param ctx the parse tree
	 */
	void exitSwitchBlockStatementGroup(X10Parser.SwitchBlockStatementGroupContext ctx);
	/**
	 * Enter a parse tree produced by {@link X10Parser#switchLabelsopt}.
	 * @param ctx the parse tree
	 */
	void enterSwitchLabelsopt(X10Parser.SwitchLabelsoptContext ctx);
	/**
	 * Exit a parse tree produced by {@link X10Parser#switchLabelsopt}.
	 * @param ctx the parse tree
	 */
	void exitSwitchLabelsopt(X10Parser.SwitchLabelsoptContext ctx);
	/**
	 * Enter a parse tree produced by {@link X10Parser#switchLabels}.
	 * @param ctx the parse tree
	 */
	void enterSwitchLabels(X10Parser.SwitchLabelsContext ctx);
	/**
	 * Exit a parse tree produced by {@link X10Parser#switchLabels}.
	 * @param ctx the parse tree
	 */
	void exitSwitchLabels(X10Parser.SwitchLabelsContext ctx);
	/**
	 * Enter a parse tree produced by the {@code switchLabel0}
	 * labeled alternative in {@link X10Parser#switchLabel}.
	 * @param ctx the parse tree
	 */
	void enterSwitchLabel0(X10Parser.SwitchLabel0Context ctx);
	/**
	 * Exit a parse tree produced by the {@code switchLabel0}
	 * labeled alternative in {@link X10Parser#switchLabel}.
	 * @param ctx the parse tree
	 */
	void exitSwitchLabel0(X10Parser.SwitchLabel0Context ctx);
	/**
	 * Enter a parse tree produced by the {@code switchLabel1}
	 * labeled alternative in {@link X10Parser#switchLabel}.
	 * @param ctx the parse tree
	 */
	void enterSwitchLabel1(X10Parser.SwitchLabel1Context ctx);
	/**
	 * Exit a parse tree produced by the {@code switchLabel1}
	 * labeled alternative in {@link X10Parser#switchLabel}.
	 * @param ctx the parse tree
	 */
	void exitSwitchLabel1(X10Parser.SwitchLabel1Context ctx);
	/**
	 * Enter a parse tree produced by {@link X10Parser#whileStatement}.
	 * @param ctx the parse tree
	 */
	void enterWhileStatement(X10Parser.WhileStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link X10Parser#whileStatement}.
	 * @param ctx the parse tree
	 */
	void exitWhileStatement(X10Parser.WhileStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link X10Parser#doStatement}.
	 * @param ctx the parse tree
	 */
	void enterDoStatement(X10Parser.DoStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link X10Parser#doStatement}.
	 * @param ctx the parse tree
	 */
	void exitDoStatement(X10Parser.DoStatementContext ctx);
	/**
	 * Enter a parse tree produced by the {@code forStatement0}
	 * labeled alternative in {@link X10Parser#forStatement}.
	 * @param ctx the parse tree
	 */
	void enterForStatement0(X10Parser.ForStatement0Context ctx);
	/**
	 * Exit a parse tree produced by the {@code forStatement0}
	 * labeled alternative in {@link X10Parser#forStatement}.
	 * @param ctx the parse tree
	 */
	void exitForStatement0(X10Parser.ForStatement0Context ctx);
	/**
	 * Enter a parse tree produced by the {@code forStatement1}
	 * labeled alternative in {@link X10Parser#forStatement}.
	 * @param ctx the parse tree
	 */
	void enterForStatement1(X10Parser.ForStatement1Context ctx);
	/**
	 * Exit a parse tree produced by the {@code forStatement1}
	 * labeled alternative in {@link X10Parser#forStatement}.
	 * @param ctx the parse tree
	 */
	void exitForStatement1(X10Parser.ForStatement1Context ctx);
	/**
	 * Enter a parse tree produced by {@link X10Parser#basicForStatement}.
	 * @param ctx the parse tree
	 */
	void enterBasicForStatement(X10Parser.BasicForStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link X10Parser#basicForStatement}.
	 * @param ctx the parse tree
	 */
	void exitBasicForStatement(X10Parser.BasicForStatementContext ctx);
	/**
	 * Enter a parse tree produced by the {@code forInit0}
	 * labeled alternative in {@link X10Parser#forInit}.
	 * @param ctx the parse tree
	 */
	void enterForInit0(X10Parser.ForInit0Context ctx);
	/**
	 * Exit a parse tree produced by the {@code forInit0}
	 * labeled alternative in {@link X10Parser#forInit}.
	 * @param ctx the parse tree
	 */
	void exitForInit0(X10Parser.ForInit0Context ctx);
	/**
	 * Enter a parse tree produced by the {@code forInit1}
	 * labeled alternative in {@link X10Parser#forInit}.
	 * @param ctx the parse tree
	 */
	void enterForInit1(X10Parser.ForInit1Context ctx);
	/**
	 * Exit a parse tree produced by the {@code forInit1}
	 * labeled alternative in {@link X10Parser#forInit}.
	 * @param ctx the parse tree
	 */
	void exitForInit1(X10Parser.ForInit1Context ctx);
	/**
	 * Enter a parse tree produced by {@link X10Parser#forUpdate}.
	 * @param ctx the parse tree
	 */
	void enterForUpdate(X10Parser.ForUpdateContext ctx);
	/**
	 * Exit a parse tree produced by {@link X10Parser#forUpdate}.
	 * @param ctx the parse tree
	 */
	void exitForUpdate(X10Parser.ForUpdateContext ctx);
	/**
	 * Enter a parse tree produced by {@link X10Parser#statementExpressionList}.
	 * @param ctx the parse tree
	 */
	void enterStatementExpressionList(X10Parser.StatementExpressionListContext ctx);
	/**
	 * Exit a parse tree produced by {@link X10Parser#statementExpressionList}.
	 * @param ctx the parse tree
	 */
	void exitStatementExpressionList(X10Parser.StatementExpressionListContext ctx);
	/**
	 * Enter a parse tree produced by {@link X10Parser#breakStatement}.
	 * @param ctx the parse tree
	 */
	void enterBreakStatement(X10Parser.BreakStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link X10Parser#breakStatement}.
	 * @param ctx the parse tree
	 */
	void exitBreakStatement(X10Parser.BreakStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link X10Parser#continueStatement}.
	 * @param ctx the parse tree
	 */
	void enterContinueStatement(X10Parser.ContinueStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link X10Parser#continueStatement}.
	 * @param ctx the parse tree
	 */
	void exitContinueStatement(X10Parser.ContinueStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link X10Parser#returnStatement}.
	 * @param ctx the parse tree
	 */
	void enterReturnStatement(X10Parser.ReturnStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link X10Parser#returnStatement}.
	 * @param ctx the parse tree
	 */
	void exitReturnStatement(X10Parser.ReturnStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link X10Parser#throwStatement}.
	 * @param ctx the parse tree
	 */
	void enterThrowStatement(X10Parser.ThrowStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link X10Parser#throwStatement}.
	 * @param ctx the parse tree
	 */
	void exitThrowStatement(X10Parser.ThrowStatementContext ctx);
	/**
	 * Enter a parse tree produced by the {@code tryStatement0}
	 * labeled alternative in {@link X10Parser#tryStatement}.
	 * @param ctx the parse tree
	 */
	void enterTryStatement0(X10Parser.TryStatement0Context ctx);
	/**
	 * Exit a parse tree produced by the {@code tryStatement0}
	 * labeled alternative in {@link X10Parser#tryStatement}.
	 * @param ctx the parse tree
	 */
	void exitTryStatement0(X10Parser.TryStatement0Context ctx);
	/**
	 * Enter a parse tree produced by the {@code tryStatement1}
	 * labeled alternative in {@link X10Parser#tryStatement}.
	 * @param ctx the parse tree
	 */
	void enterTryStatement1(X10Parser.TryStatement1Context ctx);
	/**
	 * Exit a parse tree produced by the {@code tryStatement1}
	 * labeled alternative in {@link X10Parser#tryStatement}.
	 * @param ctx the parse tree
	 */
	void exitTryStatement1(X10Parser.TryStatement1Context ctx);
	/**
	 * Enter a parse tree produced by {@link X10Parser#catches}.
	 * @param ctx the parse tree
	 */
	void enterCatches(X10Parser.CatchesContext ctx);
	/**
	 * Exit a parse tree produced by {@link X10Parser#catches}.
	 * @param ctx the parse tree
	 */
	void exitCatches(X10Parser.CatchesContext ctx);
	/**
	 * Enter a parse tree produced by {@link X10Parser#catchClause}.
	 * @param ctx the parse tree
	 */
	void enterCatchClause(X10Parser.CatchClauseContext ctx);
	/**
	 * Exit a parse tree produced by {@link X10Parser#catchClause}.
	 * @param ctx the parse tree
	 */
	void exitCatchClause(X10Parser.CatchClauseContext ctx);
	/**
	 * Enter a parse tree produced by {@link X10Parser#finallyBlock}.
	 * @param ctx the parse tree
	 */
	void enterFinallyBlock(X10Parser.FinallyBlockContext ctx);
	/**
	 * Exit a parse tree produced by {@link X10Parser#finallyBlock}.
	 * @param ctx the parse tree
	 */
	void exitFinallyBlock(X10Parser.FinallyBlockContext ctx);
	/**
	 * Enter a parse tree produced by {@link X10Parser#clockedClauseopt}.
	 * @param ctx the parse tree
	 */
	void enterClockedClauseopt(X10Parser.ClockedClauseoptContext ctx);
	/**
	 * Exit a parse tree produced by {@link X10Parser#clockedClauseopt}.
	 * @param ctx the parse tree
	 */
	void exitClockedClauseopt(X10Parser.ClockedClauseoptContext ctx);
	/**
	 * Enter a parse tree produced by the {@code asyncStatement0}
	 * labeled alternative in {@link X10Parser#asyncStatement}.
	 * @param ctx the parse tree
	 */
	void enterAsyncStatement0(X10Parser.AsyncStatement0Context ctx);
	/**
	 * Exit a parse tree produced by the {@code asyncStatement0}
	 * labeled alternative in {@link X10Parser#asyncStatement}.
	 * @param ctx the parse tree
	 */
	void exitAsyncStatement0(X10Parser.AsyncStatement0Context ctx);
	/**
	 * Enter a parse tree produced by the {@code asyncStatement1}
	 * labeled alternative in {@link X10Parser#asyncStatement}.
	 * @param ctx the parse tree
	 */
	void enterAsyncStatement1(X10Parser.AsyncStatement1Context ctx);
	/**
	 * Exit a parse tree produced by the {@code asyncStatement1}
	 * labeled alternative in {@link X10Parser#asyncStatement}.
	 * @param ctx the parse tree
	 */
	void exitAsyncStatement1(X10Parser.AsyncStatement1Context ctx);
	/**
	 * Enter a parse tree produced by {@link X10Parser#atStatement}.
	 * @param ctx the parse tree
	 */
	void enterAtStatement(X10Parser.AtStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link X10Parser#atStatement}.
	 * @param ctx the parse tree
	 */
	void exitAtStatement(X10Parser.AtStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link X10Parser#atomicStatement}.
	 * @param ctx the parse tree
	 */
	void enterAtomicStatement(X10Parser.AtomicStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link X10Parser#atomicStatement}.
	 * @param ctx the parse tree
	 */
	void exitAtomicStatement(X10Parser.AtomicStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link X10Parser#whenStatement}.
	 * @param ctx the parse tree
	 */
	void enterWhenStatement(X10Parser.WhenStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link X10Parser#whenStatement}.
	 * @param ctx the parse tree
	 */
	void exitWhenStatement(X10Parser.WhenStatementContext ctx);
	/**
	 * Enter a parse tree produced by the {@code atEachStatement0}
	 * labeled alternative in {@link X10Parser#atEachStatement}.
	 * @param ctx the parse tree
	 */
	void enterAtEachStatement0(X10Parser.AtEachStatement0Context ctx);
	/**
	 * Exit a parse tree produced by the {@code atEachStatement0}
	 * labeled alternative in {@link X10Parser#atEachStatement}.
	 * @param ctx the parse tree
	 */
	void exitAtEachStatement0(X10Parser.AtEachStatement0Context ctx);
	/**
	 * Enter a parse tree produced by the {@code atEachStatement1}
	 * labeled alternative in {@link X10Parser#atEachStatement}.
	 * @param ctx the parse tree
	 */
	void enterAtEachStatement1(X10Parser.AtEachStatement1Context ctx);
	/**
	 * Exit a parse tree produced by the {@code atEachStatement1}
	 * labeled alternative in {@link X10Parser#atEachStatement}.
	 * @param ctx the parse tree
	 */
	void exitAtEachStatement1(X10Parser.AtEachStatement1Context ctx);
	/**
	 * Enter a parse tree produced by the {@code enhancedForStatement0}
	 * labeled alternative in {@link X10Parser#enhancedForStatement}.
	 * @param ctx the parse tree
	 */
	void enterEnhancedForStatement0(X10Parser.EnhancedForStatement0Context ctx);
	/**
	 * Exit a parse tree produced by the {@code enhancedForStatement0}
	 * labeled alternative in {@link X10Parser#enhancedForStatement}.
	 * @param ctx the parse tree
	 */
	void exitEnhancedForStatement0(X10Parser.EnhancedForStatement0Context ctx);
	/**
	 * Enter a parse tree produced by the {@code enhancedForStatement1}
	 * labeled alternative in {@link X10Parser#enhancedForStatement}.
	 * @param ctx the parse tree
	 */
	void enterEnhancedForStatement1(X10Parser.EnhancedForStatement1Context ctx);
	/**
	 * Exit a parse tree produced by the {@code enhancedForStatement1}
	 * labeled alternative in {@link X10Parser#enhancedForStatement}.
	 * @param ctx the parse tree
	 */
	void exitEnhancedForStatement1(X10Parser.EnhancedForStatement1Context ctx);
	/**
	 * Enter a parse tree produced by the {@code finishStatement0}
	 * labeled alternative in {@link X10Parser#finishStatement}.
	 * @param ctx the parse tree
	 */
	void enterFinishStatement0(X10Parser.FinishStatement0Context ctx);
	/**
	 * Exit a parse tree produced by the {@code finishStatement0}
	 * labeled alternative in {@link X10Parser#finishStatement}.
	 * @param ctx the parse tree
	 */
	void exitFinishStatement0(X10Parser.FinishStatement0Context ctx);
	/**
	 * Enter a parse tree produced by the {@code finishStatement1}
	 * labeled alternative in {@link X10Parser#finishStatement}.
	 * @param ctx the parse tree
	 */
	void enterFinishStatement1(X10Parser.FinishStatement1Context ctx);
	/**
	 * Exit a parse tree produced by the {@code finishStatement1}
	 * labeled alternative in {@link X10Parser#finishStatement}.
	 * @param ctx the parse tree
	 */
	void exitFinishStatement1(X10Parser.FinishStatement1Context ctx);
	/**
	 * Enter a parse tree produced by the {@code castExpression0}
	 * labeled alternative in {@link X10Parser#castExpression}.
	 * @param ctx the parse tree
	 */
	void enterCastExpression0(X10Parser.CastExpression0Context ctx);
	/**
	 * Exit a parse tree produced by the {@code castExpression0}
	 * labeled alternative in {@link X10Parser#castExpression}.
	 * @param ctx the parse tree
	 */
	void exitCastExpression0(X10Parser.CastExpression0Context ctx);
	/**
	 * Enter a parse tree produced by the {@code castExpression2}
	 * labeled alternative in {@link X10Parser#castExpression}.
	 * @param ctx the parse tree
	 */
	void enterCastExpression2(X10Parser.CastExpression2Context ctx);
	/**
	 * Exit a parse tree produced by the {@code castExpression2}
	 * labeled alternative in {@link X10Parser#castExpression}.
	 * @param ctx the parse tree
	 */
	void exitCastExpression2(X10Parser.CastExpression2Context ctx);
	/**
	 * Enter a parse tree produced by the {@code castExpression1}
	 * labeled alternative in {@link X10Parser#castExpression}.
	 * @param ctx the parse tree
	 */
	void enterCastExpression1(X10Parser.CastExpression1Context ctx);
	/**
	 * Exit a parse tree produced by the {@code castExpression1}
	 * labeled alternative in {@link X10Parser#castExpression}.
	 * @param ctx the parse tree
	 */
	void exitCastExpression1(X10Parser.CastExpression1Context ctx);
	/**
	 * Enter a parse tree produced by the {@code typeParamWithVarianceList1}
	 * labeled alternative in {@link X10Parser#typeParamWithVarianceList}.
	 * @param ctx the parse tree
	 */
	void enterTypeParamWithVarianceList1(X10Parser.TypeParamWithVarianceList1Context ctx);
	/**
	 * Exit a parse tree produced by the {@code typeParamWithVarianceList1}
	 * labeled alternative in {@link X10Parser#typeParamWithVarianceList}.
	 * @param ctx the parse tree
	 */
	void exitTypeParamWithVarianceList1(X10Parser.TypeParamWithVarianceList1Context ctx);
	/**
	 * Enter a parse tree produced by the {@code typeParamWithVarianceList0}
	 * labeled alternative in {@link X10Parser#typeParamWithVarianceList}.
	 * @param ctx the parse tree
	 */
	void enterTypeParamWithVarianceList0(X10Parser.TypeParamWithVarianceList0Context ctx);
	/**
	 * Exit a parse tree produced by the {@code typeParamWithVarianceList0}
	 * labeled alternative in {@link X10Parser#typeParamWithVarianceList}.
	 * @param ctx the parse tree
	 */
	void exitTypeParamWithVarianceList0(X10Parser.TypeParamWithVarianceList0Context ctx);
	/**
	 * Enter a parse tree produced by the {@code typeParamWithVarianceList3}
	 * labeled alternative in {@link X10Parser#typeParamWithVarianceList}.
	 * @param ctx the parse tree
	 */
	void enterTypeParamWithVarianceList3(X10Parser.TypeParamWithVarianceList3Context ctx);
	/**
	 * Exit a parse tree produced by the {@code typeParamWithVarianceList3}
	 * labeled alternative in {@link X10Parser#typeParamWithVarianceList}.
	 * @param ctx the parse tree
	 */
	void exitTypeParamWithVarianceList3(X10Parser.TypeParamWithVarianceList3Context ctx);
	/**
	 * Enter a parse tree produced by the {@code typeParamWithVarianceList2}
	 * labeled alternative in {@link X10Parser#typeParamWithVarianceList}.
	 * @param ctx the parse tree
	 */
	void enterTypeParamWithVarianceList2(X10Parser.TypeParamWithVarianceList2Context ctx);
	/**
	 * Exit a parse tree produced by the {@code typeParamWithVarianceList2}
	 * labeled alternative in {@link X10Parser#typeParamWithVarianceList}.
	 * @param ctx the parse tree
	 */
	void exitTypeParamWithVarianceList2(X10Parser.TypeParamWithVarianceList2Context ctx);
	/**
	 * Enter a parse tree produced by {@link X10Parser#typeParameterList}.
	 * @param ctx the parse tree
	 */
	void enterTypeParameterList(X10Parser.TypeParameterListContext ctx);
	/**
	 * Exit a parse tree produced by {@link X10Parser#typeParameterList}.
	 * @param ctx the parse tree
	 */
	void exitTypeParameterList(X10Parser.TypeParameterListContext ctx);
	/**
	 * Enter a parse tree produced by the {@code oBSOLETE_TypeParamWithVariance0}
	 * labeled alternative in {@link X10Parser#oBSOLETE_TypeParamWithVariance}.
	 * @param ctx the parse tree
	 */
	void enterOBSOLETE_TypeParamWithVariance0(X10Parser.OBSOLETE_TypeParamWithVariance0Context ctx);
	/**
	 * Exit a parse tree produced by the {@code oBSOLETE_TypeParamWithVariance0}
	 * labeled alternative in {@link X10Parser#oBSOLETE_TypeParamWithVariance}.
	 * @param ctx the parse tree
	 */
	void exitOBSOLETE_TypeParamWithVariance0(X10Parser.OBSOLETE_TypeParamWithVariance0Context ctx);
	/**
	 * Enter a parse tree produced by the {@code oBSOLETE_TypeParamWithVariance1}
	 * labeled alternative in {@link X10Parser#oBSOLETE_TypeParamWithVariance}.
	 * @param ctx the parse tree
	 */
	void enterOBSOLETE_TypeParamWithVariance1(X10Parser.OBSOLETE_TypeParamWithVariance1Context ctx);
	/**
	 * Exit a parse tree produced by the {@code oBSOLETE_TypeParamWithVariance1}
	 * labeled alternative in {@link X10Parser#oBSOLETE_TypeParamWithVariance}.
	 * @param ctx the parse tree
	 */
	void exitOBSOLETE_TypeParamWithVariance1(X10Parser.OBSOLETE_TypeParamWithVariance1Context ctx);
	/**
	 * Enter a parse tree produced by {@link X10Parser#typeParameter}.
	 * @param ctx the parse tree
	 */
	void enterTypeParameter(X10Parser.TypeParameterContext ctx);
	/**
	 * Exit a parse tree produced by {@link X10Parser#typeParameter}.
	 * @param ctx the parse tree
	 */
	void exitTypeParameter(X10Parser.TypeParameterContext ctx);
	/**
	 * Enter a parse tree produced by {@link X10Parser#closureExpression}.
	 * @param ctx the parse tree
	 */
	void enterClosureExpression(X10Parser.ClosureExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link X10Parser#closureExpression}.
	 * @param ctx the parse tree
	 */
	void exitClosureExpression(X10Parser.ClosureExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link X10Parser#lastExpression}.
	 * @param ctx the parse tree
	 */
	void enterLastExpression(X10Parser.LastExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link X10Parser#lastExpression}.
	 * @param ctx the parse tree
	 */
	void exitLastExpression(X10Parser.LastExpressionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code closureBody0}
	 * labeled alternative in {@link X10Parser#closureBody}.
	 * @param ctx the parse tree
	 */
	void enterClosureBody0(X10Parser.ClosureBody0Context ctx);
	/**
	 * Exit a parse tree produced by the {@code closureBody0}
	 * labeled alternative in {@link X10Parser#closureBody}.
	 * @param ctx the parse tree
	 */
	void exitClosureBody0(X10Parser.ClosureBody0Context ctx);
	/**
	 * Enter a parse tree produced by the {@code closureBody2}
	 * labeled alternative in {@link X10Parser#closureBody}.
	 * @param ctx the parse tree
	 */
	void enterClosureBody2(X10Parser.ClosureBody2Context ctx);
	/**
	 * Exit a parse tree produced by the {@code closureBody2}
	 * labeled alternative in {@link X10Parser#closureBody}.
	 * @param ctx the parse tree
	 */
	void exitClosureBody2(X10Parser.ClosureBody2Context ctx);
	/**
	 * Enter a parse tree produced by the {@code closureBody1}
	 * labeled alternative in {@link X10Parser#closureBody}.
	 * @param ctx the parse tree
	 */
	void enterClosureBody1(X10Parser.ClosureBody1Context ctx);
	/**
	 * Exit a parse tree produced by the {@code closureBody1}
	 * labeled alternative in {@link X10Parser#closureBody}.
	 * @param ctx the parse tree
	 */
	void exitClosureBody1(X10Parser.ClosureBody1Context ctx);
	/**
	 * Enter a parse tree produced by {@link X10Parser#atExpression}.
	 * @param ctx the parse tree
	 */
	void enterAtExpression(X10Parser.AtExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link X10Parser#atExpression}.
	 * @param ctx the parse tree
	 */
	void exitAtExpression(X10Parser.AtExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link X10Parser#oBSOLETE_FinishExpression}.
	 * @param ctx the parse tree
	 */
	void enterOBSOLETE_FinishExpression(X10Parser.OBSOLETE_FinishExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link X10Parser#oBSOLETE_FinishExpression}.
	 * @param ctx the parse tree
	 */
	void exitOBSOLETE_FinishExpression(X10Parser.OBSOLETE_FinishExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link X10Parser#typeName}.
	 * @param ctx the parse tree
	 */
	void enterTypeName(X10Parser.TypeNameContext ctx);
	/**
	 * Exit a parse tree produced by {@link X10Parser#typeName}.
	 * @param ctx the parse tree
	 */
	void exitTypeName(X10Parser.TypeNameContext ctx);
	/**
	 * Enter a parse tree produced by {@link X10Parser#className}.
	 * @param ctx the parse tree
	 */
	void enterClassName(X10Parser.ClassNameContext ctx);
	/**
	 * Exit a parse tree produced by {@link X10Parser#className}.
	 * @param ctx the parse tree
	 */
	void exitClassName(X10Parser.ClassNameContext ctx);
	/**
	 * Enter a parse tree produced by {@link X10Parser#typeArguments}.
	 * @param ctx the parse tree
	 */
	void enterTypeArguments(X10Parser.TypeArgumentsContext ctx);
	/**
	 * Exit a parse tree produced by {@link X10Parser#typeArguments}.
	 * @param ctx the parse tree
	 */
	void exitTypeArguments(X10Parser.TypeArgumentsContext ctx);
	/**
	 * Enter a parse tree produced by {@link X10Parser#packageName}.
	 * @param ctx the parse tree
	 */
	void enterPackageName(X10Parser.PackageNameContext ctx);
	/**
	 * Exit a parse tree produced by {@link X10Parser#packageName}.
	 * @param ctx the parse tree
	 */
	void exitPackageName(X10Parser.PackageNameContext ctx);
	/**
	 * Enter a parse tree produced by {@link X10Parser#expressionName}.
	 * @param ctx the parse tree
	 */
	void enterExpressionName(X10Parser.ExpressionNameContext ctx);
	/**
	 * Exit a parse tree produced by {@link X10Parser#expressionName}.
	 * @param ctx the parse tree
	 */
	void exitExpressionName(X10Parser.ExpressionNameContext ctx);
	/**
	 * Enter a parse tree produced by {@link X10Parser#methodName}.
	 * @param ctx the parse tree
	 */
	void enterMethodName(X10Parser.MethodNameContext ctx);
	/**
	 * Exit a parse tree produced by {@link X10Parser#methodName}.
	 * @param ctx the parse tree
	 */
	void exitMethodName(X10Parser.MethodNameContext ctx);
	/**
	 * Enter a parse tree produced by {@link X10Parser#packageOrTypeName}.
	 * @param ctx the parse tree
	 */
	void enterPackageOrTypeName(X10Parser.PackageOrTypeNameContext ctx);
	/**
	 * Exit a parse tree produced by {@link X10Parser#packageOrTypeName}.
	 * @param ctx the parse tree
	 */
	void exitPackageOrTypeName(X10Parser.PackageOrTypeNameContext ctx);
	/**
	 * Enter a parse tree produced by {@link X10Parser#fullyQualifiedName}.
	 * @param ctx the parse tree
	 */
	void enterFullyQualifiedName(X10Parser.FullyQualifiedNameContext ctx);
	/**
	 * Exit a parse tree produced by {@link X10Parser#fullyQualifiedName}.
	 * @param ctx the parse tree
	 */
	void exitFullyQualifiedName(X10Parser.FullyQualifiedNameContext ctx);
	/**
	 * Enter a parse tree produced by {@link X10Parser#compilationUnit}.
	 * @param ctx the parse tree
	 */
	void enterCompilationUnit(X10Parser.CompilationUnitContext ctx);
	/**
	 * Exit a parse tree produced by {@link X10Parser#compilationUnit}.
	 * @param ctx the parse tree
	 */
	void exitCompilationUnit(X10Parser.CompilationUnitContext ctx);
	/**
	 * Enter a parse tree produced by {@link X10Parser#packageDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterPackageDeclaration(X10Parser.PackageDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link X10Parser#packageDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitPackageDeclaration(X10Parser.PackageDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link X10Parser#importDeclarationsopt}.
	 * @param ctx the parse tree
	 */
	void enterImportDeclarationsopt(X10Parser.ImportDeclarationsoptContext ctx);
	/**
	 * Exit a parse tree produced by {@link X10Parser#importDeclarationsopt}.
	 * @param ctx the parse tree
	 */
	void exitImportDeclarationsopt(X10Parser.ImportDeclarationsoptContext ctx);
	/**
	 * Enter a parse tree produced by the {@code importDeclaration0}
	 * labeled alternative in {@link X10Parser#importDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterImportDeclaration0(X10Parser.ImportDeclaration0Context ctx);
	/**
	 * Exit a parse tree produced by the {@code importDeclaration0}
	 * labeled alternative in {@link X10Parser#importDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitImportDeclaration0(X10Parser.ImportDeclaration0Context ctx);
	/**
	 * Enter a parse tree produced by the {@code importDeclaration1}
	 * labeled alternative in {@link X10Parser#importDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterImportDeclaration1(X10Parser.ImportDeclaration1Context ctx);
	/**
	 * Exit a parse tree produced by the {@code importDeclaration1}
	 * labeled alternative in {@link X10Parser#importDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitImportDeclaration1(X10Parser.ImportDeclaration1Context ctx);
	/**
	 * Enter a parse tree produced by {@link X10Parser#typeDeclarationsopt}.
	 * @param ctx the parse tree
	 */
	void enterTypeDeclarationsopt(X10Parser.TypeDeclarationsoptContext ctx);
	/**
	 * Exit a parse tree produced by {@link X10Parser#typeDeclarationsopt}.
	 * @param ctx the parse tree
	 */
	void exitTypeDeclarationsopt(X10Parser.TypeDeclarationsoptContext ctx);
	/**
	 * Enter a parse tree produced by the {@code typeDeclaration0}
	 * labeled alternative in {@link X10Parser#typeDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterTypeDeclaration0(X10Parser.TypeDeclaration0Context ctx);
	/**
	 * Exit a parse tree produced by the {@code typeDeclaration0}
	 * labeled alternative in {@link X10Parser#typeDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitTypeDeclaration0(X10Parser.TypeDeclaration0Context ctx);
	/**
	 * Enter a parse tree produced by the {@code typeDeclaration1}
	 * labeled alternative in {@link X10Parser#typeDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterTypeDeclaration1(X10Parser.TypeDeclaration1Context ctx);
	/**
	 * Exit a parse tree produced by the {@code typeDeclaration1}
	 * labeled alternative in {@link X10Parser#typeDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitTypeDeclaration1(X10Parser.TypeDeclaration1Context ctx);
	/**
	 * Enter a parse tree produced by the {@code typeDeclaration2}
	 * labeled alternative in {@link X10Parser#typeDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterTypeDeclaration2(X10Parser.TypeDeclaration2Context ctx);
	/**
	 * Exit a parse tree produced by the {@code typeDeclaration2}
	 * labeled alternative in {@link X10Parser#typeDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitTypeDeclaration2(X10Parser.TypeDeclaration2Context ctx);
	/**
	 * Enter a parse tree produced by the {@code typeDeclaration3}
	 * labeled alternative in {@link X10Parser#typeDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterTypeDeclaration3(X10Parser.TypeDeclaration3Context ctx);
	/**
	 * Exit a parse tree produced by the {@code typeDeclaration3}
	 * labeled alternative in {@link X10Parser#typeDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitTypeDeclaration3(X10Parser.TypeDeclaration3Context ctx);
	/**
	 * Enter a parse tree produced by the {@code typeDeclaration4}
	 * labeled alternative in {@link X10Parser#typeDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterTypeDeclaration4(X10Parser.TypeDeclaration4Context ctx);
	/**
	 * Exit a parse tree produced by the {@code typeDeclaration4}
	 * labeled alternative in {@link X10Parser#typeDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitTypeDeclaration4(X10Parser.TypeDeclaration4Context ctx);
	/**
	 * Enter a parse tree produced by {@link X10Parser#interfacesopt}.
	 * @param ctx the parse tree
	 */
	void enterInterfacesopt(X10Parser.InterfacesoptContext ctx);
	/**
	 * Exit a parse tree produced by {@link X10Parser#interfacesopt}.
	 * @param ctx the parse tree
	 */
	void exitInterfacesopt(X10Parser.InterfacesoptContext ctx);
	/**
	 * Enter a parse tree produced by {@link X10Parser#classBody}.
	 * @param ctx the parse tree
	 */
	void enterClassBody(X10Parser.ClassBodyContext ctx);
	/**
	 * Exit a parse tree produced by {@link X10Parser#classBody}.
	 * @param ctx the parse tree
	 */
	void exitClassBody(X10Parser.ClassBodyContext ctx);
	/**
	 * Enter a parse tree produced by the {@code classMemberDeclaration0}
	 * labeled alternative in {@link X10Parser#classMemberDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterClassMemberDeclaration0(X10Parser.ClassMemberDeclaration0Context ctx);
	/**
	 * Exit a parse tree produced by the {@code classMemberDeclaration0}
	 * labeled alternative in {@link X10Parser#classMemberDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitClassMemberDeclaration0(X10Parser.ClassMemberDeclaration0Context ctx);
	/**
	 * Enter a parse tree produced by the {@code classMemberDeclaration1}
	 * labeled alternative in {@link X10Parser#classMemberDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterClassMemberDeclaration1(X10Parser.ClassMemberDeclaration1Context ctx);
	/**
	 * Exit a parse tree produced by the {@code classMemberDeclaration1}
	 * labeled alternative in {@link X10Parser#classMemberDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitClassMemberDeclaration1(X10Parser.ClassMemberDeclaration1Context ctx);
	/**
	 * Enter a parse tree produced by {@link X10Parser#formalDeclarators}.
	 * @param ctx the parse tree
	 */
	void enterFormalDeclarators(X10Parser.FormalDeclaratorsContext ctx);
	/**
	 * Exit a parse tree produced by {@link X10Parser#formalDeclarators}.
	 * @param ctx the parse tree
	 */
	void exitFormalDeclarators(X10Parser.FormalDeclaratorsContext ctx);
	/**
	 * Enter a parse tree produced by {@link X10Parser#fieldDeclarators}.
	 * @param ctx the parse tree
	 */
	void enterFieldDeclarators(X10Parser.FieldDeclaratorsContext ctx);
	/**
	 * Exit a parse tree produced by {@link X10Parser#fieldDeclarators}.
	 * @param ctx the parse tree
	 */
	void exitFieldDeclarators(X10Parser.FieldDeclaratorsContext ctx);
	/**
	 * Enter a parse tree produced by {@link X10Parser#variableDeclaratorsWithType}.
	 * @param ctx the parse tree
	 */
	void enterVariableDeclaratorsWithType(X10Parser.VariableDeclaratorsWithTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link X10Parser#variableDeclaratorsWithType}.
	 * @param ctx the parse tree
	 */
	void exitVariableDeclaratorsWithType(X10Parser.VariableDeclaratorsWithTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link X10Parser#variableDeclarators}.
	 * @param ctx the parse tree
	 */
	void enterVariableDeclarators(X10Parser.VariableDeclaratorsContext ctx);
	/**
	 * Exit a parse tree produced by {@link X10Parser#variableDeclarators}.
	 * @param ctx the parse tree
	 */
	void exitVariableDeclarators(X10Parser.VariableDeclaratorsContext ctx);
	/**
	 * Enter a parse tree produced by {@link X10Parser#variableInitializer}.
	 * @param ctx the parse tree
	 */
	void enterVariableInitializer(X10Parser.VariableInitializerContext ctx);
	/**
	 * Exit a parse tree produced by {@link X10Parser#variableInitializer}.
	 * @param ctx the parse tree
	 */
	void exitVariableInitializer(X10Parser.VariableInitializerContext ctx);
	/**
	 * Enter a parse tree produced by {@link X10Parser#resultType}.
	 * @param ctx the parse tree
	 */
	void enterResultType(X10Parser.ResultTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link X10Parser#resultType}.
	 * @param ctx the parse tree
	 */
	void exitResultType(X10Parser.ResultTypeContext ctx);
	/**
	 * Enter a parse tree produced by the {@code hasResultType0}
	 * labeled alternative in {@link X10Parser#hasResultType}.
	 * @param ctx the parse tree
	 */
	void enterHasResultType0(X10Parser.HasResultType0Context ctx);
	/**
	 * Exit a parse tree produced by the {@code hasResultType0}
	 * labeled alternative in {@link X10Parser#hasResultType}.
	 * @param ctx the parse tree
	 */
	void exitHasResultType0(X10Parser.HasResultType0Context ctx);
	/**
	 * Enter a parse tree produced by the {@code hasResultType1}
	 * labeled alternative in {@link X10Parser#hasResultType}.
	 * @param ctx the parse tree
	 */
	void enterHasResultType1(X10Parser.HasResultType1Context ctx);
	/**
	 * Exit a parse tree produced by the {@code hasResultType1}
	 * labeled alternative in {@link X10Parser#hasResultType}.
	 * @param ctx the parse tree
	 */
	void exitHasResultType1(X10Parser.HasResultType1Context ctx);
	/**
	 * Enter a parse tree produced by {@link X10Parser#formalParameterList}.
	 * @param ctx the parse tree
	 */
	void enterFormalParameterList(X10Parser.FormalParameterListContext ctx);
	/**
	 * Exit a parse tree produced by {@link X10Parser#formalParameterList}.
	 * @param ctx the parse tree
	 */
	void exitFormalParameterList(X10Parser.FormalParameterListContext ctx);
	/**
	 * Enter a parse tree produced by the {@code loopIndexDeclarator0}
	 * labeled alternative in {@link X10Parser#loopIndexDeclarator}.
	 * @param ctx the parse tree
	 */
	void enterLoopIndexDeclarator0(X10Parser.LoopIndexDeclarator0Context ctx);
	/**
	 * Exit a parse tree produced by the {@code loopIndexDeclarator0}
	 * labeled alternative in {@link X10Parser#loopIndexDeclarator}.
	 * @param ctx the parse tree
	 */
	void exitLoopIndexDeclarator0(X10Parser.LoopIndexDeclarator0Context ctx);
	/**
	 * Enter a parse tree produced by the {@code loopIndexDeclarator1}
	 * labeled alternative in {@link X10Parser#loopIndexDeclarator}.
	 * @param ctx the parse tree
	 */
	void enterLoopIndexDeclarator1(X10Parser.LoopIndexDeclarator1Context ctx);
	/**
	 * Exit a parse tree produced by the {@code loopIndexDeclarator1}
	 * labeled alternative in {@link X10Parser#loopIndexDeclarator}.
	 * @param ctx the parse tree
	 */
	void exitLoopIndexDeclarator1(X10Parser.LoopIndexDeclarator1Context ctx);
	/**
	 * Enter a parse tree produced by the {@code loopIndexDeclarator2}
	 * labeled alternative in {@link X10Parser#loopIndexDeclarator}.
	 * @param ctx the parse tree
	 */
	void enterLoopIndexDeclarator2(X10Parser.LoopIndexDeclarator2Context ctx);
	/**
	 * Exit a parse tree produced by the {@code loopIndexDeclarator2}
	 * labeled alternative in {@link X10Parser#loopIndexDeclarator}.
	 * @param ctx the parse tree
	 */
	void exitLoopIndexDeclarator2(X10Parser.LoopIndexDeclarator2Context ctx);
	/**
	 * Enter a parse tree produced by the {@code loopIndex0}
	 * labeled alternative in {@link X10Parser#loopIndex}.
	 * @param ctx the parse tree
	 */
	void enterLoopIndex0(X10Parser.LoopIndex0Context ctx);
	/**
	 * Exit a parse tree produced by the {@code loopIndex0}
	 * labeled alternative in {@link X10Parser#loopIndex}.
	 * @param ctx the parse tree
	 */
	void exitLoopIndex0(X10Parser.LoopIndex0Context ctx);
	/**
	 * Enter a parse tree produced by the {@code loopIndex1}
	 * labeled alternative in {@link X10Parser#loopIndex}.
	 * @param ctx the parse tree
	 */
	void enterLoopIndex1(X10Parser.LoopIndex1Context ctx);
	/**
	 * Exit a parse tree produced by the {@code loopIndex1}
	 * labeled alternative in {@link X10Parser#loopIndex}.
	 * @param ctx the parse tree
	 */
	void exitLoopIndex1(X10Parser.LoopIndex1Context ctx);
	/**
	 * Enter a parse tree produced by the {@code formalParameter0}
	 * labeled alternative in {@link X10Parser#formalParameter}.
	 * @param ctx the parse tree
	 */
	void enterFormalParameter0(X10Parser.FormalParameter0Context ctx);
	/**
	 * Exit a parse tree produced by the {@code formalParameter0}
	 * labeled alternative in {@link X10Parser#formalParameter}.
	 * @param ctx the parse tree
	 */
	void exitFormalParameter0(X10Parser.FormalParameter0Context ctx);
	/**
	 * Enter a parse tree produced by the {@code formalParameter1}
	 * labeled alternative in {@link X10Parser#formalParameter}.
	 * @param ctx the parse tree
	 */
	void enterFormalParameter1(X10Parser.FormalParameter1Context ctx);
	/**
	 * Exit a parse tree produced by the {@code formalParameter1}
	 * labeled alternative in {@link X10Parser#formalParameter}.
	 * @param ctx the parse tree
	 */
	void exitFormalParameter1(X10Parser.FormalParameter1Context ctx);
	/**
	 * Enter a parse tree produced by the {@code formalParameter2}
	 * labeled alternative in {@link X10Parser#formalParameter}.
	 * @param ctx the parse tree
	 */
	void enterFormalParameter2(X10Parser.FormalParameter2Context ctx);
	/**
	 * Exit a parse tree produced by the {@code formalParameter2}
	 * labeled alternative in {@link X10Parser#formalParameter}.
	 * @param ctx the parse tree
	 */
	void exitFormalParameter2(X10Parser.FormalParameter2Context ctx);
	/**
	 * Enter a parse tree produced by {@link X10Parser#oBSOLETE_Offersopt}.
	 * @param ctx the parse tree
	 */
	void enterOBSOLETE_Offersopt(X10Parser.OBSOLETE_OffersoptContext ctx);
	/**
	 * Exit a parse tree produced by {@link X10Parser#oBSOLETE_Offersopt}.
	 * @param ctx the parse tree
	 */
	void exitOBSOLETE_Offersopt(X10Parser.OBSOLETE_OffersoptContext ctx);
	/**
	 * Enter a parse tree produced by {@link X10Parser#throwsopt}.
	 * @param ctx the parse tree
	 */
	void enterThrowsopt(X10Parser.ThrowsoptContext ctx);
	/**
	 * Exit a parse tree produced by {@link X10Parser#throwsopt}.
	 * @param ctx the parse tree
	 */
	void exitThrowsopt(X10Parser.ThrowsoptContext ctx);
	/**
	 * Enter a parse tree produced by the {@code methodBody0}
	 * labeled alternative in {@link X10Parser#methodBody}.
	 * @param ctx the parse tree
	 */
	void enterMethodBody0(X10Parser.MethodBody0Context ctx);
	/**
	 * Exit a parse tree produced by the {@code methodBody0}
	 * labeled alternative in {@link X10Parser#methodBody}.
	 * @param ctx the parse tree
	 */
	void exitMethodBody0(X10Parser.MethodBody0Context ctx);
	/**
	 * Enter a parse tree produced by the {@code methodBody2}
	 * labeled alternative in {@link X10Parser#methodBody}.
	 * @param ctx the parse tree
	 */
	void enterMethodBody2(X10Parser.MethodBody2Context ctx);
	/**
	 * Exit a parse tree produced by the {@code methodBody2}
	 * labeled alternative in {@link X10Parser#methodBody}.
	 * @param ctx the parse tree
	 */
	void exitMethodBody2(X10Parser.MethodBody2Context ctx);
	/**
	 * Enter a parse tree produced by the {@code methodBody1}
	 * labeled alternative in {@link X10Parser#methodBody}.
	 * @param ctx the parse tree
	 */
	void enterMethodBody1(X10Parser.MethodBody1Context ctx);
	/**
	 * Exit a parse tree produced by the {@code methodBody1}
	 * labeled alternative in {@link X10Parser#methodBody}.
	 * @param ctx the parse tree
	 */
	void exitMethodBody1(X10Parser.MethodBody1Context ctx);
	/**
	 * Enter a parse tree produced by the {@code methodBody3}
	 * labeled alternative in {@link X10Parser#methodBody}.
	 * @param ctx the parse tree
	 */
	void enterMethodBody3(X10Parser.MethodBody3Context ctx);
	/**
	 * Exit a parse tree produced by the {@code methodBody3}
	 * labeled alternative in {@link X10Parser#methodBody}.
	 * @param ctx the parse tree
	 */
	void exitMethodBody3(X10Parser.MethodBody3Context ctx);
	/**
	 * Enter a parse tree produced by the {@code constructorBody0}
	 * labeled alternative in {@link X10Parser#constructorBody}.
	 * @param ctx the parse tree
	 */
	void enterConstructorBody0(X10Parser.ConstructorBody0Context ctx);
	/**
	 * Exit a parse tree produced by the {@code constructorBody0}
	 * labeled alternative in {@link X10Parser#constructorBody}.
	 * @param ctx the parse tree
	 */
	void exitConstructorBody0(X10Parser.ConstructorBody0Context ctx);
	/**
	 * Enter a parse tree produced by the {@code constructorBody1}
	 * labeled alternative in {@link X10Parser#constructorBody}.
	 * @param ctx the parse tree
	 */
	void enterConstructorBody1(X10Parser.ConstructorBody1Context ctx);
	/**
	 * Exit a parse tree produced by the {@code constructorBody1}
	 * labeled alternative in {@link X10Parser#constructorBody}.
	 * @param ctx the parse tree
	 */
	void exitConstructorBody1(X10Parser.ConstructorBody1Context ctx);
	/**
	 * Enter a parse tree produced by the {@code constructorBody2}
	 * labeled alternative in {@link X10Parser#constructorBody}.
	 * @param ctx the parse tree
	 */
	void enterConstructorBody2(X10Parser.ConstructorBody2Context ctx);
	/**
	 * Exit a parse tree produced by the {@code constructorBody2}
	 * labeled alternative in {@link X10Parser#constructorBody}.
	 * @param ctx the parse tree
	 */
	void exitConstructorBody2(X10Parser.ConstructorBody2Context ctx);
	/**
	 * Enter a parse tree produced by the {@code constructorBody3}
	 * labeled alternative in {@link X10Parser#constructorBody}.
	 * @param ctx the parse tree
	 */
	void enterConstructorBody3(X10Parser.ConstructorBody3Context ctx);
	/**
	 * Exit a parse tree produced by the {@code constructorBody3}
	 * labeled alternative in {@link X10Parser#constructorBody}.
	 * @param ctx the parse tree
	 */
	void exitConstructorBody3(X10Parser.ConstructorBody3Context ctx);
	/**
	 * Enter a parse tree produced by {@link X10Parser#constructorBlock}.
	 * @param ctx the parse tree
	 */
	void enterConstructorBlock(X10Parser.ConstructorBlockContext ctx);
	/**
	 * Exit a parse tree produced by {@link X10Parser#constructorBlock}.
	 * @param ctx the parse tree
	 */
	void exitConstructorBlock(X10Parser.ConstructorBlockContext ctx);
	/**
	 * Enter a parse tree produced by {@link X10Parser#arguments}.
	 * @param ctx the parse tree
	 */
	void enterArguments(X10Parser.ArgumentsContext ctx);
	/**
	 * Exit a parse tree produced by {@link X10Parser#arguments}.
	 * @param ctx the parse tree
	 */
	void exitArguments(X10Parser.ArgumentsContext ctx);
	/**
	 * Enter a parse tree produced by {@link X10Parser#extendsInterfacesopt}.
	 * @param ctx the parse tree
	 */
	void enterExtendsInterfacesopt(X10Parser.ExtendsInterfacesoptContext ctx);
	/**
	 * Exit a parse tree produced by {@link X10Parser#extendsInterfacesopt}.
	 * @param ctx the parse tree
	 */
	void exitExtendsInterfacesopt(X10Parser.ExtendsInterfacesoptContext ctx);
	/**
	 * Enter a parse tree produced by {@link X10Parser#interfaceBody}.
	 * @param ctx the parse tree
	 */
	void enterInterfaceBody(X10Parser.InterfaceBodyContext ctx);
	/**
	 * Exit a parse tree produced by {@link X10Parser#interfaceBody}.
	 * @param ctx the parse tree
	 */
	void exitInterfaceBody(X10Parser.InterfaceBodyContext ctx);
	/**
	 * Enter a parse tree produced by {@link X10Parser#interfaceMemberDeclarationsopt}.
	 * @param ctx the parse tree
	 */
	void enterInterfaceMemberDeclarationsopt(X10Parser.InterfaceMemberDeclarationsoptContext ctx);
	/**
	 * Exit a parse tree produced by {@link X10Parser#interfaceMemberDeclarationsopt}.
	 * @param ctx the parse tree
	 */
	void exitInterfaceMemberDeclarationsopt(X10Parser.InterfaceMemberDeclarationsoptContext ctx);
	/**
	 * Enter a parse tree produced by the {@code interfaceMemberDeclaration0}
	 * labeled alternative in {@link X10Parser#interfaceMemberDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterInterfaceMemberDeclaration0(X10Parser.InterfaceMemberDeclaration0Context ctx);
	/**
	 * Exit a parse tree produced by the {@code interfaceMemberDeclaration0}
	 * labeled alternative in {@link X10Parser#interfaceMemberDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitInterfaceMemberDeclaration0(X10Parser.InterfaceMemberDeclaration0Context ctx);
	/**
	 * Enter a parse tree produced by the {@code interfaceMemberDeclaration2}
	 * labeled alternative in {@link X10Parser#interfaceMemberDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterInterfaceMemberDeclaration2(X10Parser.InterfaceMemberDeclaration2Context ctx);
	/**
	 * Exit a parse tree produced by the {@code interfaceMemberDeclaration2}
	 * labeled alternative in {@link X10Parser#interfaceMemberDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitInterfaceMemberDeclaration2(X10Parser.InterfaceMemberDeclaration2Context ctx);
	/**
	 * Enter a parse tree produced by the {@code interfaceMemberDeclaration1}
	 * labeled alternative in {@link X10Parser#interfaceMemberDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterInterfaceMemberDeclaration1(X10Parser.InterfaceMemberDeclaration1Context ctx);
	/**
	 * Exit a parse tree produced by the {@code interfaceMemberDeclaration1}
	 * labeled alternative in {@link X10Parser#interfaceMemberDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitInterfaceMemberDeclaration1(X10Parser.InterfaceMemberDeclaration1Context ctx);
	/**
	 * Enter a parse tree produced by the {@code interfaceMemberDeclaration3}
	 * labeled alternative in {@link X10Parser#interfaceMemberDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterInterfaceMemberDeclaration3(X10Parser.InterfaceMemberDeclaration3Context ctx);
	/**
	 * Exit a parse tree produced by the {@code interfaceMemberDeclaration3}
	 * labeled alternative in {@link X10Parser#interfaceMemberDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitInterfaceMemberDeclaration3(X10Parser.InterfaceMemberDeclaration3Context ctx);
	/**
	 * Enter a parse tree produced by {@link X10Parser#annotationsopt}.
	 * @param ctx the parse tree
	 */
	void enterAnnotationsopt(X10Parser.AnnotationsoptContext ctx);
	/**
	 * Exit a parse tree produced by {@link X10Parser#annotationsopt}.
	 * @param ctx the parse tree
	 */
	void exitAnnotationsopt(X10Parser.AnnotationsoptContext ctx);
	/**
	 * Enter a parse tree produced by {@link X10Parser#annotations}.
	 * @param ctx the parse tree
	 */
	void enterAnnotations(X10Parser.AnnotationsContext ctx);
	/**
	 * Exit a parse tree produced by {@link X10Parser#annotations}.
	 * @param ctx the parse tree
	 */
	void exitAnnotations(X10Parser.AnnotationsContext ctx);
	/**
	 * Enter a parse tree produced by {@link X10Parser#annotation}.
	 * @param ctx the parse tree
	 */
	void enterAnnotation(X10Parser.AnnotationContext ctx);
	/**
	 * Exit a parse tree produced by {@link X10Parser#annotation}.
	 * @param ctx the parse tree
	 */
	void exitAnnotation(X10Parser.AnnotationContext ctx);
	/**
	 * Enter a parse tree produced by {@link X10Parser#identifier}.
	 * @param ctx the parse tree
	 */
	void enterIdentifier(X10Parser.IdentifierContext ctx);
	/**
	 * Exit a parse tree produced by {@link X10Parser#identifier}.
	 * @param ctx the parse tree
	 */
	void exitIdentifier(X10Parser.IdentifierContext ctx);
	/**
	 * Enter a parse tree produced by {@link X10Parser#block}.
	 * @param ctx the parse tree
	 */
	void enterBlock(X10Parser.BlockContext ctx);
	/**
	 * Exit a parse tree produced by {@link X10Parser#block}.
	 * @param ctx the parse tree
	 */
	void exitBlock(X10Parser.BlockContext ctx);
	/**
	 * Enter a parse tree produced by {@link X10Parser#blockStatements}.
	 * @param ctx the parse tree
	 */
	void enterBlockStatements(X10Parser.BlockStatementsContext ctx);
	/**
	 * Exit a parse tree produced by {@link X10Parser#blockStatements}.
	 * @param ctx the parse tree
	 */
	void exitBlockStatements(X10Parser.BlockStatementsContext ctx);
	/**
	 * Enter a parse tree produced by the {@code blockInteriorStatement0}
	 * labeled alternative in {@link X10Parser#blockInteriorStatement}.
	 * @param ctx the parse tree
	 */
	void enterBlockInteriorStatement0(X10Parser.BlockInteriorStatement0Context ctx);
	/**
	 * Exit a parse tree produced by the {@code blockInteriorStatement0}
	 * labeled alternative in {@link X10Parser#blockInteriorStatement}.
	 * @param ctx the parse tree
	 */
	void exitBlockInteriorStatement0(X10Parser.BlockInteriorStatement0Context ctx);
	/**
	 * Enter a parse tree produced by the {@code blockInteriorStatement1}
	 * labeled alternative in {@link X10Parser#blockInteriorStatement}.
	 * @param ctx the parse tree
	 */
	void enterBlockInteriorStatement1(X10Parser.BlockInteriorStatement1Context ctx);
	/**
	 * Exit a parse tree produced by the {@code blockInteriorStatement1}
	 * labeled alternative in {@link X10Parser#blockInteriorStatement}.
	 * @param ctx the parse tree
	 */
	void exitBlockInteriorStatement1(X10Parser.BlockInteriorStatement1Context ctx);
	/**
	 * Enter a parse tree produced by the {@code blockInteriorStatement2}
	 * labeled alternative in {@link X10Parser#blockInteriorStatement}.
	 * @param ctx the parse tree
	 */
	void enterBlockInteriorStatement2(X10Parser.BlockInteriorStatement2Context ctx);
	/**
	 * Exit a parse tree produced by the {@code blockInteriorStatement2}
	 * labeled alternative in {@link X10Parser#blockInteriorStatement}.
	 * @param ctx the parse tree
	 */
	void exitBlockInteriorStatement2(X10Parser.BlockInteriorStatement2Context ctx);
	/**
	 * Enter a parse tree produced by the {@code blockInteriorStatement3}
	 * labeled alternative in {@link X10Parser#blockInteriorStatement}.
	 * @param ctx the parse tree
	 */
	void enterBlockInteriorStatement3(X10Parser.BlockInteriorStatement3Context ctx);
	/**
	 * Exit a parse tree produced by the {@code blockInteriorStatement3}
	 * labeled alternative in {@link X10Parser#blockInteriorStatement}.
	 * @param ctx the parse tree
	 */
	void exitBlockInteriorStatement3(X10Parser.BlockInteriorStatement3Context ctx);
	/**
	 * Enter a parse tree produced by the {@code blockInteriorStatement4}
	 * labeled alternative in {@link X10Parser#blockInteriorStatement}.
	 * @param ctx the parse tree
	 */
	void enterBlockInteriorStatement4(X10Parser.BlockInteriorStatement4Context ctx);
	/**
	 * Exit a parse tree produced by the {@code blockInteriorStatement4}
	 * labeled alternative in {@link X10Parser#blockInteriorStatement}.
	 * @param ctx the parse tree
	 */
	void exitBlockInteriorStatement4(X10Parser.BlockInteriorStatement4Context ctx);
	/**
	 * Enter a parse tree produced by {@link X10Parser#identifierList}.
	 * @param ctx the parse tree
	 */
	void enterIdentifierList(X10Parser.IdentifierListContext ctx);
	/**
	 * Exit a parse tree produced by {@link X10Parser#identifierList}.
	 * @param ctx the parse tree
	 */
	void exitIdentifierList(X10Parser.IdentifierListContext ctx);
	/**
	 * Enter a parse tree produced by the {@code formalDeclarator0}
	 * labeled alternative in {@link X10Parser#formalDeclarator}.
	 * @param ctx the parse tree
	 */
	void enterFormalDeclarator0(X10Parser.FormalDeclarator0Context ctx);
	/**
	 * Exit a parse tree produced by the {@code formalDeclarator0}
	 * labeled alternative in {@link X10Parser#formalDeclarator}.
	 * @param ctx the parse tree
	 */
	void exitFormalDeclarator0(X10Parser.FormalDeclarator0Context ctx);
	/**
	 * Enter a parse tree produced by the {@code formalDeclarator1}
	 * labeled alternative in {@link X10Parser#formalDeclarator}.
	 * @param ctx the parse tree
	 */
	void enterFormalDeclarator1(X10Parser.FormalDeclarator1Context ctx);
	/**
	 * Exit a parse tree produced by the {@code formalDeclarator1}
	 * labeled alternative in {@link X10Parser#formalDeclarator}.
	 * @param ctx the parse tree
	 */
	void exitFormalDeclarator1(X10Parser.FormalDeclarator1Context ctx);
	/**
	 * Enter a parse tree produced by the {@code formalDeclarator2}
	 * labeled alternative in {@link X10Parser#formalDeclarator}.
	 * @param ctx the parse tree
	 */
	void enterFormalDeclarator2(X10Parser.FormalDeclarator2Context ctx);
	/**
	 * Exit a parse tree produced by the {@code formalDeclarator2}
	 * labeled alternative in {@link X10Parser#formalDeclarator}.
	 * @param ctx the parse tree
	 */
	void exitFormalDeclarator2(X10Parser.FormalDeclarator2Context ctx);
	/**
	 * Enter a parse tree produced by the {@code fieldDeclarator0}
	 * labeled alternative in {@link X10Parser#fieldDeclarator}.
	 * @param ctx the parse tree
	 */
	void enterFieldDeclarator0(X10Parser.FieldDeclarator0Context ctx);
	/**
	 * Exit a parse tree produced by the {@code fieldDeclarator0}
	 * labeled alternative in {@link X10Parser#fieldDeclarator}.
	 * @param ctx the parse tree
	 */
	void exitFieldDeclarator0(X10Parser.FieldDeclarator0Context ctx);
	/**
	 * Enter a parse tree produced by the {@code fieldDeclarator1}
	 * labeled alternative in {@link X10Parser#fieldDeclarator}.
	 * @param ctx the parse tree
	 */
	void enterFieldDeclarator1(X10Parser.FieldDeclarator1Context ctx);
	/**
	 * Exit a parse tree produced by the {@code fieldDeclarator1}
	 * labeled alternative in {@link X10Parser#fieldDeclarator}.
	 * @param ctx the parse tree
	 */
	void exitFieldDeclarator1(X10Parser.FieldDeclarator1Context ctx);
	/**
	 * Enter a parse tree produced by the {@code variableDeclarator0}
	 * labeled alternative in {@link X10Parser#variableDeclarator}.
	 * @param ctx the parse tree
	 */
	void enterVariableDeclarator0(X10Parser.VariableDeclarator0Context ctx);
	/**
	 * Exit a parse tree produced by the {@code variableDeclarator0}
	 * labeled alternative in {@link X10Parser#variableDeclarator}.
	 * @param ctx the parse tree
	 */
	void exitVariableDeclarator0(X10Parser.VariableDeclarator0Context ctx);
	/**
	 * Enter a parse tree produced by the {@code variableDeclarator1}
	 * labeled alternative in {@link X10Parser#variableDeclarator}.
	 * @param ctx the parse tree
	 */
	void enterVariableDeclarator1(X10Parser.VariableDeclarator1Context ctx);
	/**
	 * Exit a parse tree produced by the {@code variableDeclarator1}
	 * labeled alternative in {@link X10Parser#variableDeclarator}.
	 * @param ctx the parse tree
	 */
	void exitVariableDeclarator1(X10Parser.VariableDeclarator1Context ctx);
	/**
	 * Enter a parse tree produced by the {@code variableDeclarator2}
	 * labeled alternative in {@link X10Parser#variableDeclarator}.
	 * @param ctx the parse tree
	 */
	void enterVariableDeclarator2(X10Parser.VariableDeclarator2Context ctx);
	/**
	 * Exit a parse tree produced by the {@code variableDeclarator2}
	 * labeled alternative in {@link X10Parser#variableDeclarator}.
	 * @param ctx the parse tree
	 */
	void exitVariableDeclarator2(X10Parser.VariableDeclarator2Context ctx);
	/**
	 * Enter a parse tree produced by the {@code variableDeclaratorWithType0}
	 * labeled alternative in {@link X10Parser#variableDeclaratorWithType}.
	 * @param ctx the parse tree
	 */
	void enterVariableDeclaratorWithType0(X10Parser.VariableDeclaratorWithType0Context ctx);
	/**
	 * Exit a parse tree produced by the {@code variableDeclaratorWithType0}
	 * labeled alternative in {@link X10Parser#variableDeclaratorWithType}.
	 * @param ctx the parse tree
	 */
	void exitVariableDeclaratorWithType0(X10Parser.VariableDeclaratorWithType0Context ctx);
	/**
	 * Enter a parse tree produced by the {@code variableDeclaratorWithType1}
	 * labeled alternative in {@link X10Parser#variableDeclaratorWithType}.
	 * @param ctx the parse tree
	 */
	void enterVariableDeclaratorWithType1(X10Parser.VariableDeclaratorWithType1Context ctx);
	/**
	 * Exit a parse tree produced by the {@code variableDeclaratorWithType1}
	 * labeled alternative in {@link X10Parser#variableDeclaratorWithType}.
	 * @param ctx the parse tree
	 */
	void exitVariableDeclaratorWithType1(X10Parser.VariableDeclaratorWithType1Context ctx);
	/**
	 * Enter a parse tree produced by the {@code variableDeclaratorWithType2}
	 * labeled alternative in {@link X10Parser#variableDeclaratorWithType}.
	 * @param ctx the parse tree
	 */
	void enterVariableDeclaratorWithType2(X10Parser.VariableDeclaratorWithType2Context ctx);
	/**
	 * Exit a parse tree produced by the {@code variableDeclaratorWithType2}
	 * labeled alternative in {@link X10Parser#variableDeclaratorWithType}.
	 * @param ctx the parse tree
	 */
	void exitVariableDeclaratorWithType2(X10Parser.VariableDeclaratorWithType2Context ctx);
	/**
	 * Enter a parse tree produced by {@link X10Parser#localVariableDeclarationStatement}.
	 * @param ctx the parse tree
	 */
	void enterLocalVariableDeclarationStatement(X10Parser.LocalVariableDeclarationStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link X10Parser#localVariableDeclarationStatement}.
	 * @param ctx the parse tree
	 */
	void exitLocalVariableDeclarationStatement(X10Parser.LocalVariableDeclarationStatementContext ctx);
	/**
	 * Enter a parse tree produced by the {@code localVariableDeclaration0}
	 * labeled alternative in {@link X10Parser#localVariableDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterLocalVariableDeclaration0(X10Parser.LocalVariableDeclaration0Context ctx);
	/**
	 * Exit a parse tree produced by the {@code localVariableDeclaration0}
	 * labeled alternative in {@link X10Parser#localVariableDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitLocalVariableDeclaration0(X10Parser.LocalVariableDeclaration0Context ctx);
	/**
	 * Enter a parse tree produced by the {@code localVariableDeclaration1}
	 * labeled alternative in {@link X10Parser#localVariableDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterLocalVariableDeclaration1(X10Parser.LocalVariableDeclaration1Context ctx);
	/**
	 * Exit a parse tree produced by the {@code localVariableDeclaration1}
	 * labeled alternative in {@link X10Parser#localVariableDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitLocalVariableDeclaration1(X10Parser.LocalVariableDeclaration1Context ctx);
	/**
	 * Enter a parse tree produced by the {@code localVariableDeclaration2}
	 * labeled alternative in {@link X10Parser#localVariableDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterLocalVariableDeclaration2(X10Parser.LocalVariableDeclaration2Context ctx);
	/**
	 * Exit a parse tree produced by the {@code localVariableDeclaration2}
	 * labeled alternative in {@link X10Parser#localVariableDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitLocalVariableDeclaration2(X10Parser.LocalVariableDeclaration2Context ctx);
	/**
	 * Enter a parse tree produced by the {@code primary26}
	 * labeled alternative in {@link X10Parser#primary}.
	 * @param ctx the parse tree
	 */
	void enterPrimary26(X10Parser.Primary26Context ctx);
	/**
	 * Exit a parse tree produced by the {@code primary26}
	 * labeled alternative in {@link X10Parser#primary}.
	 * @param ctx the parse tree
	 */
	void exitPrimary26(X10Parser.Primary26Context ctx);
	/**
	 * Enter a parse tree produced by the {@code primary27}
	 * labeled alternative in {@link X10Parser#primary}.
	 * @param ctx the parse tree
	 */
	void enterPrimary27(X10Parser.Primary27Context ctx);
	/**
	 * Exit a parse tree produced by the {@code primary27}
	 * labeled alternative in {@link X10Parser#primary}.
	 * @param ctx the parse tree
	 */
	void exitPrimary27(X10Parser.Primary27Context ctx);
	/**
	 * Enter a parse tree produced by the {@code primary24}
	 * labeled alternative in {@link X10Parser#primary}.
	 * @param ctx the parse tree
	 */
	void enterPrimary24(X10Parser.Primary24Context ctx);
	/**
	 * Exit a parse tree produced by the {@code primary24}
	 * labeled alternative in {@link X10Parser#primary}.
	 * @param ctx the parse tree
	 */
	void exitPrimary24(X10Parser.Primary24Context ctx);
	/**
	 * Enter a parse tree produced by the {@code primary25}
	 * labeled alternative in {@link X10Parser#primary}.
	 * @param ctx the parse tree
	 */
	void enterPrimary25(X10Parser.Primary25Context ctx);
	/**
	 * Exit a parse tree produced by the {@code primary25}
	 * labeled alternative in {@link X10Parser#primary}.
	 * @param ctx the parse tree
	 */
	void exitPrimary25(X10Parser.Primary25Context ctx);
	/**
	 * Enter a parse tree produced by the {@code primary22}
	 * labeled alternative in {@link X10Parser#primary}.
	 * @param ctx the parse tree
	 */
	void enterPrimary22(X10Parser.Primary22Context ctx);
	/**
	 * Exit a parse tree produced by the {@code primary22}
	 * labeled alternative in {@link X10Parser#primary}.
	 * @param ctx the parse tree
	 */
	void exitPrimary22(X10Parser.Primary22Context ctx);
	/**
	 * Enter a parse tree produced by the {@code primary0}
	 * labeled alternative in {@link X10Parser#primary}.
	 * @param ctx the parse tree
	 */
	void enterPrimary0(X10Parser.Primary0Context ctx);
	/**
	 * Exit a parse tree produced by the {@code primary0}
	 * labeled alternative in {@link X10Parser#primary}.
	 * @param ctx the parse tree
	 */
	void exitPrimary0(X10Parser.Primary0Context ctx);
	/**
	 * Enter a parse tree produced by the {@code primary1}
	 * labeled alternative in {@link X10Parser#primary}.
	 * @param ctx the parse tree
	 */
	void enterPrimary1(X10Parser.Primary1Context ctx);
	/**
	 * Exit a parse tree produced by the {@code primary1}
	 * labeled alternative in {@link X10Parser#primary}.
	 * @param ctx the parse tree
	 */
	void exitPrimary1(X10Parser.Primary1Context ctx);
	/**
	 * Enter a parse tree produced by the {@code primary23}
	 * labeled alternative in {@link X10Parser#primary}.
	 * @param ctx the parse tree
	 */
	void enterPrimary23(X10Parser.Primary23Context ctx);
	/**
	 * Exit a parse tree produced by the {@code primary23}
	 * labeled alternative in {@link X10Parser#primary}.
	 * @param ctx the parse tree
	 */
	void exitPrimary23(X10Parser.Primary23Context ctx);
	/**
	 * Enter a parse tree produced by the {@code primary20}
	 * labeled alternative in {@link X10Parser#primary}.
	 * @param ctx the parse tree
	 */
	void enterPrimary20(X10Parser.Primary20Context ctx);
	/**
	 * Exit a parse tree produced by the {@code primary20}
	 * labeled alternative in {@link X10Parser#primary}.
	 * @param ctx the parse tree
	 */
	void exitPrimary20(X10Parser.Primary20Context ctx);
	/**
	 * Enter a parse tree produced by the {@code primary21}
	 * labeled alternative in {@link X10Parser#primary}.
	 * @param ctx the parse tree
	 */
	void enterPrimary21(X10Parser.Primary21Context ctx);
	/**
	 * Exit a parse tree produced by the {@code primary21}
	 * labeled alternative in {@link X10Parser#primary}.
	 * @param ctx the parse tree
	 */
	void exitPrimary21(X10Parser.Primary21Context ctx);
	/**
	 * Enter a parse tree produced by the {@code primary28}
	 * labeled alternative in {@link X10Parser#primary}.
	 * @param ctx the parse tree
	 */
	void enterPrimary28(X10Parser.Primary28Context ctx);
	/**
	 * Exit a parse tree produced by the {@code primary28}
	 * labeled alternative in {@link X10Parser#primary}.
	 * @param ctx the parse tree
	 */
	void exitPrimary28(X10Parser.Primary28Context ctx);
	/**
	 * Enter a parse tree produced by the {@code primary29}
	 * labeled alternative in {@link X10Parser#primary}.
	 * @param ctx the parse tree
	 */
	void enterPrimary29(X10Parser.Primary29Context ctx);
	/**
	 * Exit a parse tree produced by the {@code primary29}
	 * labeled alternative in {@link X10Parser#primary}.
	 * @param ctx the parse tree
	 */
	void exitPrimary29(X10Parser.Primary29Context ctx);
	/**
	 * Enter a parse tree produced by the {@code primaryError0}
	 * labeled alternative in {@link X10Parser#primary}.
	 * @param ctx the parse tree
	 */
	void enterPrimaryError0(X10Parser.PrimaryError0Context ctx);
	/**
	 * Exit a parse tree produced by the {@code primaryError0}
	 * labeled alternative in {@link X10Parser#primary}.
	 * @param ctx the parse tree
	 */
	void exitPrimaryError0(X10Parser.PrimaryError0Context ctx);
	/**
	 * Enter a parse tree produced by the {@code primary5}
	 * labeled alternative in {@link X10Parser#primary}.
	 * @param ctx the parse tree
	 */
	void enterPrimary5(X10Parser.Primary5Context ctx);
	/**
	 * Exit a parse tree produced by the {@code primary5}
	 * labeled alternative in {@link X10Parser#primary}.
	 * @param ctx the parse tree
	 */
	void exitPrimary5(X10Parser.Primary5Context ctx);
	/**
	 * Enter a parse tree produced by the {@code primary30}
	 * labeled alternative in {@link X10Parser#primary}.
	 * @param ctx the parse tree
	 */
	void enterPrimary30(X10Parser.Primary30Context ctx);
	/**
	 * Exit a parse tree produced by the {@code primary30}
	 * labeled alternative in {@link X10Parser#primary}.
	 * @param ctx the parse tree
	 */
	void exitPrimary30(X10Parser.Primary30Context ctx);
	/**
	 * Enter a parse tree produced by the {@code primaryError1}
	 * labeled alternative in {@link X10Parser#primary}.
	 * @param ctx the parse tree
	 */
	void enterPrimaryError1(X10Parser.PrimaryError1Context ctx);
	/**
	 * Exit a parse tree produced by the {@code primaryError1}
	 * labeled alternative in {@link X10Parser#primary}.
	 * @param ctx the parse tree
	 */
	void exitPrimaryError1(X10Parser.PrimaryError1Context ctx);
	/**
	 * Enter a parse tree produced by the {@code primary4}
	 * labeled alternative in {@link X10Parser#primary}.
	 * @param ctx the parse tree
	 */
	void enterPrimary4(X10Parser.Primary4Context ctx);
	/**
	 * Exit a parse tree produced by the {@code primary4}
	 * labeled alternative in {@link X10Parser#primary}.
	 * @param ctx the parse tree
	 */
	void exitPrimary4(X10Parser.Primary4Context ctx);
	/**
	 * Enter a parse tree produced by the {@code primary3}
	 * labeled alternative in {@link X10Parser#primary}.
	 * @param ctx the parse tree
	 */
	void enterPrimary3(X10Parser.Primary3Context ctx);
	/**
	 * Exit a parse tree produced by the {@code primary3}
	 * labeled alternative in {@link X10Parser#primary}.
	 * @param ctx the parse tree
	 */
	void exitPrimary3(X10Parser.Primary3Context ctx);
	/**
	 * Enter a parse tree produced by the {@code primary2}
	 * labeled alternative in {@link X10Parser#primary}.
	 * @param ctx the parse tree
	 */
	void enterPrimary2(X10Parser.Primary2Context ctx);
	/**
	 * Exit a parse tree produced by the {@code primary2}
	 * labeled alternative in {@link X10Parser#primary}.
	 * @param ctx the parse tree
	 */
	void exitPrimary2(X10Parser.Primary2Context ctx);
	/**
	 * Enter a parse tree produced by the {@code primary9}
	 * labeled alternative in {@link X10Parser#primary}.
	 * @param ctx the parse tree
	 */
	void enterPrimary9(X10Parser.Primary9Context ctx);
	/**
	 * Exit a parse tree produced by the {@code primary9}
	 * labeled alternative in {@link X10Parser#primary}.
	 * @param ctx the parse tree
	 */
	void exitPrimary9(X10Parser.Primary9Context ctx);
	/**
	 * Enter a parse tree produced by the {@code primary8}
	 * labeled alternative in {@link X10Parser#primary}.
	 * @param ctx the parse tree
	 */
	void enterPrimary8(X10Parser.Primary8Context ctx);
	/**
	 * Exit a parse tree produced by the {@code primary8}
	 * labeled alternative in {@link X10Parser#primary}.
	 * @param ctx the parse tree
	 */
	void exitPrimary8(X10Parser.Primary8Context ctx);
	/**
	 * Enter a parse tree produced by the {@code primaryError2}
	 * labeled alternative in {@link X10Parser#primary}.
	 * @param ctx the parse tree
	 */
	void enterPrimaryError2(X10Parser.PrimaryError2Context ctx);
	/**
	 * Exit a parse tree produced by the {@code primaryError2}
	 * labeled alternative in {@link X10Parser#primary}.
	 * @param ctx the parse tree
	 */
	void exitPrimaryError2(X10Parser.PrimaryError2Context ctx);
	/**
	 * Enter a parse tree produced by the {@code primary7}
	 * labeled alternative in {@link X10Parser#primary}.
	 * @param ctx the parse tree
	 */
	void enterPrimary7(X10Parser.Primary7Context ctx);
	/**
	 * Exit a parse tree produced by the {@code primary7}
	 * labeled alternative in {@link X10Parser#primary}.
	 * @param ctx the parse tree
	 */
	void exitPrimary7(X10Parser.Primary7Context ctx);
	/**
	 * Enter a parse tree produced by the {@code primaryError3}
	 * labeled alternative in {@link X10Parser#primary}.
	 * @param ctx the parse tree
	 */
	void enterPrimaryError3(X10Parser.PrimaryError3Context ctx);
	/**
	 * Exit a parse tree produced by the {@code primaryError3}
	 * labeled alternative in {@link X10Parser#primary}.
	 * @param ctx the parse tree
	 */
	void exitPrimaryError3(X10Parser.PrimaryError3Context ctx);
	/**
	 * Enter a parse tree produced by the {@code primary6}
	 * labeled alternative in {@link X10Parser#primary}.
	 * @param ctx the parse tree
	 */
	void enterPrimary6(X10Parser.Primary6Context ctx);
	/**
	 * Exit a parse tree produced by the {@code primary6}
	 * labeled alternative in {@link X10Parser#primary}.
	 * @param ctx the parse tree
	 */
	void exitPrimary6(X10Parser.Primary6Context ctx);
	/**
	 * Enter a parse tree produced by the {@code primary35}
	 * labeled alternative in {@link X10Parser#primary}.
	 * @param ctx the parse tree
	 */
	void enterPrimary35(X10Parser.Primary35Context ctx);
	/**
	 * Exit a parse tree produced by the {@code primary35}
	 * labeled alternative in {@link X10Parser#primary}.
	 * @param ctx the parse tree
	 */
	void exitPrimary35(X10Parser.Primary35Context ctx);
	/**
	 * Enter a parse tree produced by the {@code primary10}
	 * labeled alternative in {@link X10Parser#primary}.
	 * @param ctx the parse tree
	 */
	void enterPrimary10(X10Parser.Primary10Context ctx);
	/**
	 * Exit a parse tree produced by the {@code primary10}
	 * labeled alternative in {@link X10Parser#primary}.
	 * @param ctx the parse tree
	 */
	void exitPrimary10(X10Parser.Primary10Context ctx);
	/**
	 * Enter a parse tree produced by the {@code primary36}
	 * labeled alternative in {@link X10Parser#primary}.
	 * @param ctx the parse tree
	 */
	void enterPrimary36(X10Parser.Primary36Context ctx);
	/**
	 * Exit a parse tree produced by the {@code primary36}
	 * labeled alternative in {@link X10Parser#primary}.
	 * @param ctx the parse tree
	 */
	void exitPrimary36(X10Parser.Primary36Context ctx);
	/**
	 * Enter a parse tree produced by the {@code primary37}
	 * labeled alternative in {@link X10Parser#primary}.
	 * @param ctx the parse tree
	 */
	void enterPrimary37(X10Parser.Primary37Context ctx);
	/**
	 * Exit a parse tree produced by the {@code primary37}
	 * labeled alternative in {@link X10Parser#primary}.
	 * @param ctx the parse tree
	 */
	void exitPrimary37(X10Parser.Primary37Context ctx);
	/**
	 * Enter a parse tree produced by the {@code primary11}
	 * labeled alternative in {@link X10Parser#primary}.
	 * @param ctx the parse tree
	 */
	void enterPrimary11(X10Parser.Primary11Context ctx);
	/**
	 * Exit a parse tree produced by the {@code primary11}
	 * labeled alternative in {@link X10Parser#primary}.
	 * @param ctx the parse tree
	 */
	void exitPrimary11(X10Parser.Primary11Context ctx);
	/**
	 * Enter a parse tree produced by the {@code primary38}
	 * labeled alternative in {@link X10Parser#primary}.
	 * @param ctx the parse tree
	 */
	void enterPrimary38(X10Parser.Primary38Context ctx);
	/**
	 * Exit a parse tree produced by the {@code primary38}
	 * labeled alternative in {@link X10Parser#primary}.
	 * @param ctx the parse tree
	 */
	void exitPrimary38(X10Parser.Primary38Context ctx);
	/**
	 * Enter a parse tree produced by the {@code primary12}
	 * labeled alternative in {@link X10Parser#primary}.
	 * @param ctx the parse tree
	 */
	void enterPrimary12(X10Parser.Primary12Context ctx);
	/**
	 * Exit a parse tree produced by the {@code primary12}
	 * labeled alternative in {@link X10Parser#primary}.
	 * @param ctx the parse tree
	 */
	void exitPrimary12(X10Parser.Primary12Context ctx);
	/**
	 * Enter a parse tree produced by the {@code primary13}
	 * labeled alternative in {@link X10Parser#primary}.
	 * @param ctx the parse tree
	 */
	void enterPrimary13(X10Parser.Primary13Context ctx);
	/**
	 * Exit a parse tree produced by the {@code primary13}
	 * labeled alternative in {@link X10Parser#primary}.
	 * @param ctx the parse tree
	 */
	void exitPrimary13(X10Parser.Primary13Context ctx);
	/**
	 * Enter a parse tree produced by the {@code primary31}
	 * labeled alternative in {@link X10Parser#primary}.
	 * @param ctx the parse tree
	 */
	void enterPrimary31(X10Parser.Primary31Context ctx);
	/**
	 * Exit a parse tree produced by the {@code primary31}
	 * labeled alternative in {@link X10Parser#primary}.
	 * @param ctx the parse tree
	 */
	void exitPrimary31(X10Parser.Primary31Context ctx);
	/**
	 * Enter a parse tree produced by the {@code primary32}
	 * labeled alternative in {@link X10Parser#primary}.
	 * @param ctx the parse tree
	 */
	void enterPrimary32(X10Parser.Primary32Context ctx);
	/**
	 * Exit a parse tree produced by the {@code primary32}
	 * labeled alternative in {@link X10Parser#primary}.
	 * @param ctx the parse tree
	 */
	void exitPrimary32(X10Parser.Primary32Context ctx);
	/**
	 * Enter a parse tree produced by the {@code primary33}
	 * labeled alternative in {@link X10Parser#primary}.
	 * @param ctx the parse tree
	 */
	void enterPrimary33(X10Parser.Primary33Context ctx);
	/**
	 * Exit a parse tree produced by the {@code primary33}
	 * labeled alternative in {@link X10Parser#primary}.
	 * @param ctx the parse tree
	 */
	void exitPrimary33(X10Parser.Primary33Context ctx);
	/**
	 * Enter a parse tree produced by the {@code primary34}
	 * labeled alternative in {@link X10Parser#primary}.
	 * @param ctx the parse tree
	 */
	void enterPrimary34(X10Parser.Primary34Context ctx);
	/**
	 * Exit a parse tree produced by the {@code primary34}
	 * labeled alternative in {@link X10Parser#primary}.
	 * @param ctx the parse tree
	 */
	void exitPrimary34(X10Parser.Primary34Context ctx);
	/**
	 * Enter a parse tree produced by the {@code primary17}
	 * labeled alternative in {@link X10Parser#primary}.
	 * @param ctx the parse tree
	 */
	void enterPrimary17(X10Parser.Primary17Context ctx);
	/**
	 * Exit a parse tree produced by the {@code primary17}
	 * labeled alternative in {@link X10Parser#primary}.
	 * @param ctx the parse tree
	 */
	void exitPrimary17(X10Parser.Primary17Context ctx);
	/**
	 * Enter a parse tree produced by the {@code primary18}
	 * labeled alternative in {@link X10Parser#primary}.
	 * @param ctx the parse tree
	 */
	void enterPrimary18(X10Parser.Primary18Context ctx);
	/**
	 * Exit a parse tree produced by the {@code primary18}
	 * labeled alternative in {@link X10Parser#primary}.
	 * @param ctx the parse tree
	 */
	void exitPrimary18(X10Parser.Primary18Context ctx);
	/**
	 * Enter a parse tree produced by the {@code primary19}
	 * labeled alternative in {@link X10Parser#primary}.
	 * @param ctx the parse tree
	 */
	void enterPrimary19(X10Parser.Primary19Context ctx);
	/**
	 * Exit a parse tree produced by the {@code primary19}
	 * labeled alternative in {@link X10Parser#primary}.
	 * @param ctx the parse tree
	 */
	void exitPrimary19(X10Parser.Primary19Context ctx);
	/**
	 * Enter a parse tree produced by the {@code primary39}
	 * labeled alternative in {@link X10Parser#primary}.
	 * @param ctx the parse tree
	 */
	void enterPrimary39(X10Parser.Primary39Context ctx);
	/**
	 * Exit a parse tree produced by the {@code primary39}
	 * labeled alternative in {@link X10Parser#primary}.
	 * @param ctx the parse tree
	 */
	void exitPrimary39(X10Parser.Primary39Context ctx);
	/**
	 * Enter a parse tree produced by the {@code IntLiteral}
	 * labeled alternative in {@link X10Parser#literal}.
	 * @param ctx the parse tree
	 */
	void enterIntLiteral(X10Parser.IntLiteralContext ctx);
	/**
	 * Exit a parse tree produced by the {@code IntLiteral}
	 * labeled alternative in {@link X10Parser#literal}.
	 * @param ctx the parse tree
	 */
	void exitIntLiteral(X10Parser.IntLiteralContext ctx);
	/**
	 * Enter a parse tree produced by the {@code LongLiteral}
	 * labeled alternative in {@link X10Parser#literal}.
	 * @param ctx the parse tree
	 */
	void enterLongLiteral(X10Parser.LongLiteralContext ctx);
	/**
	 * Exit a parse tree produced by the {@code LongLiteral}
	 * labeled alternative in {@link X10Parser#literal}.
	 * @param ctx the parse tree
	 */
	void exitLongLiteral(X10Parser.LongLiteralContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ByteLiteral}
	 * labeled alternative in {@link X10Parser#literal}.
	 * @param ctx the parse tree
	 */
	void enterByteLiteral(X10Parser.ByteLiteralContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ByteLiteral}
	 * labeled alternative in {@link X10Parser#literal}.
	 * @param ctx the parse tree
	 */
	void exitByteLiteral(X10Parser.ByteLiteralContext ctx);
	/**
	 * Enter a parse tree produced by the {@code UnsignedByteLiteral}
	 * labeled alternative in {@link X10Parser#literal}.
	 * @param ctx the parse tree
	 */
	void enterUnsignedByteLiteral(X10Parser.UnsignedByteLiteralContext ctx);
	/**
	 * Exit a parse tree produced by the {@code UnsignedByteLiteral}
	 * labeled alternative in {@link X10Parser#literal}.
	 * @param ctx the parse tree
	 */
	void exitUnsignedByteLiteral(X10Parser.UnsignedByteLiteralContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ShortLiteral}
	 * labeled alternative in {@link X10Parser#literal}.
	 * @param ctx the parse tree
	 */
	void enterShortLiteral(X10Parser.ShortLiteralContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ShortLiteral}
	 * labeled alternative in {@link X10Parser#literal}.
	 * @param ctx the parse tree
	 */
	void exitShortLiteral(X10Parser.ShortLiteralContext ctx);
	/**
	 * Enter a parse tree produced by the {@code UnsignedShortLiteral}
	 * labeled alternative in {@link X10Parser#literal}.
	 * @param ctx the parse tree
	 */
	void enterUnsignedShortLiteral(X10Parser.UnsignedShortLiteralContext ctx);
	/**
	 * Exit a parse tree produced by the {@code UnsignedShortLiteral}
	 * labeled alternative in {@link X10Parser#literal}.
	 * @param ctx the parse tree
	 */
	void exitUnsignedShortLiteral(X10Parser.UnsignedShortLiteralContext ctx);
	/**
	 * Enter a parse tree produced by the {@code UnsignedIntLiteral}
	 * labeled alternative in {@link X10Parser#literal}.
	 * @param ctx the parse tree
	 */
	void enterUnsignedIntLiteral(X10Parser.UnsignedIntLiteralContext ctx);
	/**
	 * Exit a parse tree produced by the {@code UnsignedIntLiteral}
	 * labeled alternative in {@link X10Parser#literal}.
	 * @param ctx the parse tree
	 */
	void exitUnsignedIntLiteral(X10Parser.UnsignedIntLiteralContext ctx);
	/**
	 * Enter a parse tree produced by the {@code UnsignedLongLiteral}
	 * labeled alternative in {@link X10Parser#literal}.
	 * @param ctx the parse tree
	 */
	void enterUnsignedLongLiteral(X10Parser.UnsignedLongLiteralContext ctx);
	/**
	 * Exit a parse tree produced by the {@code UnsignedLongLiteral}
	 * labeled alternative in {@link X10Parser#literal}.
	 * @param ctx the parse tree
	 */
	void exitUnsignedLongLiteral(X10Parser.UnsignedLongLiteralContext ctx);
	/**
	 * Enter a parse tree produced by the {@code FloatingPointLiteral}
	 * labeled alternative in {@link X10Parser#literal}.
	 * @param ctx the parse tree
	 */
	void enterFloatingPointLiteral(X10Parser.FloatingPointLiteralContext ctx);
	/**
	 * Exit a parse tree produced by the {@code FloatingPointLiteral}
	 * labeled alternative in {@link X10Parser#literal}.
	 * @param ctx the parse tree
	 */
	void exitFloatingPointLiteral(X10Parser.FloatingPointLiteralContext ctx);
	/**
	 * Enter a parse tree produced by the {@code DoubleLiteral}
	 * labeled alternative in {@link X10Parser#literal}.
	 * @param ctx the parse tree
	 */
	void enterDoubleLiteral(X10Parser.DoubleLiteralContext ctx);
	/**
	 * Exit a parse tree produced by the {@code DoubleLiteral}
	 * labeled alternative in {@link X10Parser#literal}.
	 * @param ctx the parse tree
	 */
	void exitDoubleLiteral(X10Parser.DoubleLiteralContext ctx);
	/**
	 * Enter a parse tree produced by the {@code Literal10}
	 * labeled alternative in {@link X10Parser#literal}.
	 * @param ctx the parse tree
	 */
	void enterLiteral10(X10Parser.Literal10Context ctx);
	/**
	 * Exit a parse tree produced by the {@code Literal10}
	 * labeled alternative in {@link X10Parser#literal}.
	 * @param ctx the parse tree
	 */
	void exitLiteral10(X10Parser.Literal10Context ctx);
	/**
	 * Enter a parse tree produced by the {@code CharacterLiteral}
	 * labeled alternative in {@link X10Parser#literal}.
	 * @param ctx the parse tree
	 */
	void enterCharacterLiteral(X10Parser.CharacterLiteralContext ctx);
	/**
	 * Exit a parse tree produced by the {@code CharacterLiteral}
	 * labeled alternative in {@link X10Parser#literal}.
	 * @param ctx the parse tree
	 */
	void exitCharacterLiteral(X10Parser.CharacterLiteralContext ctx);
	/**
	 * Enter a parse tree produced by the {@code StringLiteral}
	 * labeled alternative in {@link X10Parser#literal}.
	 * @param ctx the parse tree
	 */
	void enterStringLiteral(X10Parser.StringLiteralContext ctx);
	/**
	 * Exit a parse tree produced by the {@code StringLiteral}
	 * labeled alternative in {@link X10Parser#literal}.
	 * @param ctx the parse tree
	 */
	void exitStringLiteral(X10Parser.StringLiteralContext ctx);
	/**
	 * Enter a parse tree produced by the {@code NullLiteral}
	 * labeled alternative in {@link X10Parser#literal}.
	 * @param ctx the parse tree
	 */
	void enterNullLiteral(X10Parser.NullLiteralContext ctx);
	/**
	 * Exit a parse tree produced by the {@code NullLiteral}
	 * labeled alternative in {@link X10Parser#literal}.
	 * @param ctx the parse tree
	 */
	void exitNullLiteral(X10Parser.NullLiteralContext ctx);
	/**
	 * Enter a parse tree produced by {@link X10Parser#booleanLiteral}.
	 * @param ctx the parse tree
	 */
	void enterBooleanLiteral(X10Parser.BooleanLiteralContext ctx);
	/**
	 * Exit a parse tree produced by {@link X10Parser#booleanLiteral}.
	 * @param ctx the parse tree
	 */
	void exitBooleanLiteral(X10Parser.BooleanLiteralContext ctx);
	/**
	 * Enter a parse tree produced by {@link X10Parser#argumentList}.
	 * @param ctx the parse tree
	 */
	void enterArgumentList(X10Parser.ArgumentListContext ctx);
	/**
	 * Exit a parse tree produced by {@link X10Parser#argumentList}.
	 * @param ctx the parse tree
	 */
	void exitArgumentList(X10Parser.ArgumentListContext ctx);
	/**
	 * Enter a parse tree produced by the {@code fieldAccess0}
	 * labeled alternative in {@link X10Parser#fieldAccess}.
	 * @param ctx the parse tree
	 */
	void enterFieldAccess0(X10Parser.FieldAccess0Context ctx);
	/**
	 * Exit a parse tree produced by the {@code fieldAccess0}
	 * labeled alternative in {@link X10Parser#fieldAccess}.
	 * @param ctx the parse tree
	 */
	void exitFieldAccess0(X10Parser.FieldAccess0Context ctx);
	/**
	 * Enter a parse tree produced by the {@code fieldAccess1}
	 * labeled alternative in {@link X10Parser#fieldAccess}.
	 * @param ctx the parse tree
	 */
	void enterFieldAccess1(X10Parser.FieldAccess1Context ctx);
	/**
	 * Exit a parse tree produced by the {@code fieldAccess1}
	 * labeled alternative in {@link X10Parser#fieldAccess}.
	 * @param ctx the parse tree
	 */
	void exitFieldAccess1(X10Parser.FieldAccess1Context ctx);
	/**
	 * Enter a parse tree produced by the {@code fieldAccess2}
	 * labeled alternative in {@link X10Parser#fieldAccess}.
	 * @param ctx the parse tree
	 */
	void enterFieldAccess2(X10Parser.FieldAccess2Context ctx);
	/**
	 * Exit a parse tree produced by the {@code fieldAccess2}
	 * labeled alternative in {@link X10Parser#fieldAccess}.
	 * @param ctx the parse tree
	 */
	void exitFieldAccess2(X10Parser.FieldAccess2Context ctx);
	/**
	 * Enter a parse tree produced by the {@code conditionalExpression0}
	 * labeled alternative in {@link X10Parser#conditionalExpression}.
	 * @param ctx the parse tree
	 */
	void enterConditionalExpression0(X10Parser.ConditionalExpression0Context ctx);
	/**
	 * Exit a parse tree produced by the {@code conditionalExpression0}
	 * labeled alternative in {@link X10Parser#conditionalExpression}.
	 * @param ctx the parse tree
	 */
	void exitConditionalExpression0(X10Parser.ConditionalExpression0Context ctx);
	/**
	 * Enter a parse tree produced by the {@code conditionalExpression1}
	 * labeled alternative in {@link X10Parser#conditionalExpression}.
	 * @param ctx the parse tree
	 */
	void enterConditionalExpression1(X10Parser.ConditionalExpression1Context ctx);
	/**
	 * Exit a parse tree produced by the {@code conditionalExpression1}
	 * labeled alternative in {@link X10Parser#conditionalExpression}.
	 * @param ctx the parse tree
	 */
	void exitConditionalExpression1(X10Parser.ConditionalExpression1Context ctx);
	/**
	 * Enter a parse tree produced by the {@code conditionalExpression4}
	 * labeled alternative in {@link X10Parser#conditionalExpression}.
	 * @param ctx the parse tree
	 */
	void enterConditionalExpression4(X10Parser.ConditionalExpression4Context ctx);
	/**
	 * Exit a parse tree produced by the {@code conditionalExpression4}
	 * labeled alternative in {@link X10Parser#conditionalExpression}.
	 * @param ctx the parse tree
	 */
	void exitConditionalExpression4(X10Parser.ConditionalExpression4Context ctx);
	/**
	 * Enter a parse tree produced by the {@code conditionalExpression5}
	 * labeled alternative in {@link X10Parser#conditionalExpression}.
	 * @param ctx the parse tree
	 */
	void enterConditionalExpression5(X10Parser.ConditionalExpression5Context ctx);
	/**
	 * Exit a parse tree produced by the {@code conditionalExpression5}
	 * labeled alternative in {@link X10Parser#conditionalExpression}.
	 * @param ctx the parse tree
	 */
	void exitConditionalExpression5(X10Parser.ConditionalExpression5Context ctx);
	/**
	 * Enter a parse tree produced by the {@code conditionalExpression2}
	 * labeled alternative in {@link X10Parser#conditionalExpression}.
	 * @param ctx the parse tree
	 */
	void enterConditionalExpression2(X10Parser.ConditionalExpression2Context ctx);
	/**
	 * Exit a parse tree produced by the {@code conditionalExpression2}
	 * labeled alternative in {@link X10Parser#conditionalExpression}.
	 * @param ctx the parse tree
	 */
	void exitConditionalExpression2(X10Parser.ConditionalExpression2Context ctx);
	/**
	 * Enter a parse tree produced by the {@code conditionalExpression3}
	 * labeled alternative in {@link X10Parser#conditionalExpression}.
	 * @param ctx the parse tree
	 */
	void enterConditionalExpression3(X10Parser.ConditionalExpression3Context ctx);
	/**
	 * Exit a parse tree produced by the {@code conditionalExpression3}
	 * labeled alternative in {@link X10Parser#conditionalExpression}.
	 * @param ctx the parse tree
	 */
	void exitConditionalExpression3(X10Parser.ConditionalExpression3Context ctx);
	/**
	 * Enter a parse tree produced by the {@code conditionalExpression8}
	 * labeled alternative in {@link X10Parser#conditionalExpression}.
	 * @param ctx the parse tree
	 */
	void enterConditionalExpression8(X10Parser.ConditionalExpression8Context ctx);
	/**
	 * Exit a parse tree produced by the {@code conditionalExpression8}
	 * labeled alternative in {@link X10Parser#conditionalExpression}.
	 * @param ctx the parse tree
	 */
	void exitConditionalExpression8(X10Parser.ConditionalExpression8Context ctx);
	/**
	 * Enter a parse tree produced by the {@code conditionalExpression20}
	 * labeled alternative in {@link X10Parser#conditionalExpression}.
	 * @param ctx the parse tree
	 */
	void enterConditionalExpression20(X10Parser.ConditionalExpression20Context ctx);
	/**
	 * Exit a parse tree produced by the {@code conditionalExpression20}
	 * labeled alternative in {@link X10Parser#conditionalExpression}.
	 * @param ctx the parse tree
	 */
	void exitConditionalExpression20(X10Parser.ConditionalExpression20Context ctx);
	/**
	 * Enter a parse tree produced by the {@code conditionalExpression11}
	 * labeled alternative in {@link X10Parser#conditionalExpression}.
	 * @param ctx the parse tree
	 */
	void enterConditionalExpression11(X10Parser.ConditionalExpression11Context ctx);
	/**
	 * Exit a parse tree produced by the {@code conditionalExpression11}
	 * labeled alternative in {@link X10Parser#conditionalExpression}.
	 * @param ctx the parse tree
	 */
	void exitConditionalExpression11(X10Parser.ConditionalExpression11Context ctx);
	/**
	 * Enter a parse tree produced by the {@code conditionalExpression12}
	 * labeled alternative in {@link X10Parser#conditionalExpression}.
	 * @param ctx the parse tree
	 */
	void enterConditionalExpression12(X10Parser.ConditionalExpression12Context ctx);
	/**
	 * Exit a parse tree produced by the {@code conditionalExpression12}
	 * labeled alternative in {@link X10Parser#conditionalExpression}.
	 * @param ctx the parse tree
	 */
	void exitConditionalExpression12(X10Parser.ConditionalExpression12Context ctx);
	/**
	 * Enter a parse tree produced by the {@code conditionalExpression21}
	 * labeled alternative in {@link X10Parser#conditionalExpression}.
	 * @param ctx the parse tree
	 */
	void enterConditionalExpression21(X10Parser.ConditionalExpression21Context ctx);
	/**
	 * Exit a parse tree produced by the {@code conditionalExpression21}
	 * labeled alternative in {@link X10Parser#conditionalExpression}.
	 * @param ctx the parse tree
	 */
	void exitConditionalExpression21(X10Parser.ConditionalExpression21Context ctx);
	/**
	 * Enter a parse tree produced by the {@code conditionalExpression6}
	 * labeled alternative in {@link X10Parser#conditionalExpression}.
	 * @param ctx the parse tree
	 */
	void enterConditionalExpression6(X10Parser.ConditionalExpression6Context ctx);
	/**
	 * Exit a parse tree produced by the {@code conditionalExpression6}
	 * labeled alternative in {@link X10Parser#conditionalExpression}.
	 * @param ctx the parse tree
	 */
	void exitConditionalExpression6(X10Parser.ConditionalExpression6Context ctx);
	/**
	 * Enter a parse tree produced by the {@code conditionalExpression7}
	 * labeled alternative in {@link X10Parser#conditionalExpression}.
	 * @param ctx the parse tree
	 */
	void enterConditionalExpression7(X10Parser.ConditionalExpression7Context ctx);
	/**
	 * Exit a parse tree produced by the {@code conditionalExpression7}
	 * labeled alternative in {@link X10Parser#conditionalExpression}.
	 * @param ctx the parse tree
	 */
	void exitConditionalExpression7(X10Parser.ConditionalExpression7Context ctx);
	/**
	 * Enter a parse tree produced by the {@code conditionalExpression10}
	 * labeled alternative in {@link X10Parser#conditionalExpression}.
	 * @param ctx the parse tree
	 */
	void enterConditionalExpression10(X10Parser.ConditionalExpression10Context ctx);
	/**
	 * Exit a parse tree produced by the {@code conditionalExpression10}
	 * labeled alternative in {@link X10Parser#conditionalExpression}.
	 * @param ctx the parse tree
	 */
	void exitConditionalExpression10(X10Parser.ConditionalExpression10Context ctx);
	/**
	 * Enter a parse tree produced by the {@code conditionalExpression18}
	 * labeled alternative in {@link X10Parser#conditionalExpression}.
	 * @param ctx the parse tree
	 */
	void enterConditionalExpression18(X10Parser.ConditionalExpression18Context ctx);
	/**
	 * Exit a parse tree produced by the {@code conditionalExpression18}
	 * labeled alternative in {@link X10Parser#conditionalExpression}.
	 * @param ctx the parse tree
	 */
	void exitConditionalExpression18(X10Parser.ConditionalExpression18Context ctx);
	/**
	 * Enter a parse tree produced by the {@code conditionalExpression17}
	 * labeled alternative in {@link X10Parser#conditionalExpression}.
	 * @param ctx the parse tree
	 */
	void enterConditionalExpression17(X10Parser.ConditionalExpression17Context ctx);
	/**
	 * Exit a parse tree produced by the {@code conditionalExpression17}
	 * labeled alternative in {@link X10Parser#conditionalExpression}.
	 * @param ctx the parse tree
	 */
	void exitConditionalExpression17(X10Parser.ConditionalExpression17Context ctx);
	/**
	 * Enter a parse tree produced by the {@code conditionalExpression19}
	 * labeled alternative in {@link X10Parser#conditionalExpression}.
	 * @param ctx the parse tree
	 */
	void enterConditionalExpression19(X10Parser.ConditionalExpression19Context ctx);
	/**
	 * Exit a parse tree produced by the {@code conditionalExpression19}
	 * labeled alternative in {@link X10Parser#conditionalExpression}.
	 * @param ctx the parse tree
	 */
	void exitConditionalExpression19(X10Parser.ConditionalExpression19Context ctx);
	/**
	 * Enter a parse tree produced by the {@code conditionalExpression14}
	 * labeled alternative in {@link X10Parser#conditionalExpression}.
	 * @param ctx the parse tree
	 */
	void enterConditionalExpression14(X10Parser.ConditionalExpression14Context ctx);
	/**
	 * Exit a parse tree produced by the {@code conditionalExpression14}
	 * labeled alternative in {@link X10Parser#conditionalExpression}.
	 * @param ctx the parse tree
	 */
	void exitConditionalExpression14(X10Parser.ConditionalExpression14Context ctx);
	/**
	 * Enter a parse tree produced by the {@code conditionalExpression13}
	 * labeled alternative in {@link X10Parser#conditionalExpression}.
	 * @param ctx the parse tree
	 */
	void enterConditionalExpression13(X10Parser.ConditionalExpression13Context ctx);
	/**
	 * Exit a parse tree produced by the {@code conditionalExpression13}
	 * labeled alternative in {@link X10Parser#conditionalExpression}.
	 * @param ctx the parse tree
	 */
	void exitConditionalExpression13(X10Parser.ConditionalExpression13Context ctx);
	/**
	 * Enter a parse tree produced by the {@code conditionalExpression16}
	 * labeled alternative in {@link X10Parser#conditionalExpression}.
	 * @param ctx the parse tree
	 */
	void enterConditionalExpression16(X10Parser.ConditionalExpression16Context ctx);
	/**
	 * Exit a parse tree produced by the {@code conditionalExpression16}
	 * labeled alternative in {@link X10Parser#conditionalExpression}.
	 * @param ctx the parse tree
	 */
	void exitConditionalExpression16(X10Parser.ConditionalExpression16Context ctx);
	/**
	 * Enter a parse tree produced by the {@code conditionalExpression26}
	 * labeled alternative in {@link X10Parser#conditionalExpression}.
	 * @param ctx the parse tree
	 */
	void enterConditionalExpression26(X10Parser.ConditionalExpression26Context ctx);
	/**
	 * Exit a parse tree produced by the {@code conditionalExpression26}
	 * labeled alternative in {@link X10Parser#conditionalExpression}.
	 * @param ctx the parse tree
	 */
	void exitConditionalExpression26(X10Parser.ConditionalExpression26Context ctx);
	/**
	 * Enter a parse tree produced by the {@code conditionalExpression25}
	 * labeled alternative in {@link X10Parser#conditionalExpression}.
	 * @param ctx the parse tree
	 */
	void enterConditionalExpression25(X10Parser.ConditionalExpression25Context ctx);
	/**
	 * Exit a parse tree produced by the {@code conditionalExpression25}
	 * labeled alternative in {@link X10Parser#conditionalExpression}.
	 * @param ctx the parse tree
	 */
	void exitConditionalExpression25(X10Parser.ConditionalExpression25Context ctx);
	/**
	 * Enter a parse tree produced by the {@code nonAssignmentExpression1}
	 * labeled alternative in {@link X10Parser#nonAssignmentExpression}.
	 * @param ctx the parse tree
	 */
	void enterNonAssignmentExpression1(X10Parser.NonAssignmentExpression1Context ctx);
	/**
	 * Exit a parse tree produced by the {@code nonAssignmentExpression1}
	 * labeled alternative in {@link X10Parser#nonAssignmentExpression}.
	 * @param ctx the parse tree
	 */
	void exitNonAssignmentExpression1(X10Parser.NonAssignmentExpression1Context ctx);
	/**
	 * Enter a parse tree produced by the {@code nonAssignmentExpression2}
	 * labeled alternative in {@link X10Parser#nonAssignmentExpression}.
	 * @param ctx the parse tree
	 */
	void enterNonAssignmentExpression2(X10Parser.NonAssignmentExpression2Context ctx);
	/**
	 * Exit a parse tree produced by the {@code nonAssignmentExpression2}
	 * labeled alternative in {@link X10Parser#nonAssignmentExpression}.
	 * @param ctx the parse tree
	 */
	void exitNonAssignmentExpression2(X10Parser.NonAssignmentExpression2Context ctx);
	/**
	 * Enter a parse tree produced by the {@code nonAssignmentExpression3}
	 * labeled alternative in {@link X10Parser#nonAssignmentExpression}.
	 * @param ctx the parse tree
	 */
	void enterNonAssignmentExpression3(X10Parser.NonAssignmentExpression3Context ctx);
	/**
	 * Exit a parse tree produced by the {@code nonAssignmentExpression3}
	 * labeled alternative in {@link X10Parser#nonAssignmentExpression}.
	 * @param ctx the parse tree
	 */
	void exitNonAssignmentExpression3(X10Parser.NonAssignmentExpression3Context ctx);
	/**
	 * Enter a parse tree produced by the {@code nonAssignmentExpression4}
	 * labeled alternative in {@link X10Parser#nonAssignmentExpression}.
	 * @param ctx the parse tree
	 */
	void enterNonAssignmentExpression4(X10Parser.NonAssignmentExpression4Context ctx);
	/**
	 * Exit a parse tree produced by the {@code nonAssignmentExpression4}
	 * labeled alternative in {@link X10Parser#nonAssignmentExpression}.
	 * @param ctx the parse tree
	 */
	void exitNonAssignmentExpression4(X10Parser.NonAssignmentExpression4Context ctx);
	/**
	 * Enter a parse tree produced by the {@code assignmentExpression0}
	 * labeled alternative in {@link X10Parser#assignmentExpression}.
	 * @param ctx the parse tree
	 */
	void enterAssignmentExpression0(X10Parser.AssignmentExpression0Context ctx);
	/**
	 * Exit a parse tree produced by the {@code assignmentExpression0}
	 * labeled alternative in {@link X10Parser#assignmentExpression}.
	 * @param ctx the parse tree
	 */
	void exitAssignmentExpression0(X10Parser.AssignmentExpression0Context ctx);
	/**
	 * Enter a parse tree produced by the {@code assignmentExpression1}
	 * labeled alternative in {@link X10Parser#assignmentExpression}.
	 * @param ctx the parse tree
	 */
	void enterAssignmentExpression1(X10Parser.AssignmentExpression1Context ctx);
	/**
	 * Exit a parse tree produced by the {@code assignmentExpression1}
	 * labeled alternative in {@link X10Parser#assignmentExpression}.
	 * @param ctx the parse tree
	 */
	void exitAssignmentExpression1(X10Parser.AssignmentExpression1Context ctx);
	/**
	 * Enter a parse tree produced by the {@code assignment0}
	 * labeled alternative in {@link X10Parser#assignment}.
	 * @param ctx the parse tree
	 */
	void enterAssignment0(X10Parser.Assignment0Context ctx);
	/**
	 * Exit a parse tree produced by the {@code assignment0}
	 * labeled alternative in {@link X10Parser#assignment}.
	 * @param ctx the parse tree
	 */
	void exitAssignment0(X10Parser.Assignment0Context ctx);
	/**
	 * Enter a parse tree produced by the {@code assignment1}
	 * labeled alternative in {@link X10Parser#assignment}.
	 * @param ctx the parse tree
	 */
	void enterAssignment1(X10Parser.Assignment1Context ctx);
	/**
	 * Exit a parse tree produced by the {@code assignment1}
	 * labeled alternative in {@link X10Parser#assignment}.
	 * @param ctx the parse tree
	 */
	void exitAssignment1(X10Parser.Assignment1Context ctx);
	/**
	 * Enter a parse tree produced by the {@code assignment2}
	 * labeled alternative in {@link X10Parser#assignment}.
	 * @param ctx the parse tree
	 */
	void enterAssignment2(X10Parser.Assignment2Context ctx);
	/**
	 * Exit a parse tree produced by the {@code assignment2}
	 * labeled alternative in {@link X10Parser#assignment}.
	 * @param ctx the parse tree
	 */
	void exitAssignment2(X10Parser.Assignment2Context ctx);
	/**
	 * Enter a parse tree produced by the {@code leftHandSide0}
	 * labeled alternative in {@link X10Parser#leftHandSide}.
	 * @param ctx the parse tree
	 */
	void enterLeftHandSide0(X10Parser.LeftHandSide0Context ctx);
	/**
	 * Exit a parse tree produced by the {@code leftHandSide0}
	 * labeled alternative in {@link X10Parser#leftHandSide}.
	 * @param ctx the parse tree
	 */
	void exitLeftHandSide0(X10Parser.LeftHandSide0Context ctx);
	/**
	 * Enter a parse tree produced by the {@code leftHandSide1}
	 * labeled alternative in {@link X10Parser#leftHandSide}.
	 * @param ctx the parse tree
	 */
	void enterLeftHandSide1(X10Parser.LeftHandSide1Context ctx);
	/**
	 * Exit a parse tree produced by the {@code leftHandSide1}
	 * labeled alternative in {@link X10Parser#leftHandSide}.
	 * @param ctx the parse tree
	 */
	void exitLeftHandSide1(X10Parser.LeftHandSide1Context ctx);
	/**
	 * Enter a parse tree produced by {@link X10Parser#expression}.
	 * @param ctx the parse tree
	 */
	void enterExpression(X10Parser.ExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link X10Parser#expression}.
	 * @param ctx the parse tree
	 */
	void exitExpression(X10Parser.ExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link X10Parser#constantExpression}.
	 * @param ctx the parse tree
	 */
	void enterConstantExpression(X10Parser.ConstantExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link X10Parser#constantExpression}.
	 * @param ctx the parse tree
	 */
	void exitConstantExpression(X10Parser.ConstantExpressionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code assignmentOperator0}
	 * labeled alternative in {@link X10Parser#assignmentOperator}.
	 * @param ctx the parse tree
	 */
	void enterAssignmentOperator0(X10Parser.AssignmentOperator0Context ctx);
	/**
	 * Exit a parse tree produced by the {@code assignmentOperator0}
	 * labeled alternative in {@link X10Parser#assignmentOperator}.
	 * @param ctx the parse tree
	 */
	void exitAssignmentOperator0(X10Parser.AssignmentOperator0Context ctx);
	/**
	 * Enter a parse tree produced by the {@code assignmentOperator1}
	 * labeled alternative in {@link X10Parser#assignmentOperator}.
	 * @param ctx the parse tree
	 */
	void enterAssignmentOperator1(X10Parser.AssignmentOperator1Context ctx);
	/**
	 * Exit a parse tree produced by the {@code assignmentOperator1}
	 * labeled alternative in {@link X10Parser#assignmentOperator}.
	 * @param ctx the parse tree
	 */
	void exitAssignmentOperator1(X10Parser.AssignmentOperator1Context ctx);
	/**
	 * Enter a parse tree produced by the {@code assignmentOperator2}
	 * labeled alternative in {@link X10Parser#assignmentOperator}.
	 * @param ctx the parse tree
	 */
	void enterAssignmentOperator2(X10Parser.AssignmentOperator2Context ctx);
	/**
	 * Exit a parse tree produced by the {@code assignmentOperator2}
	 * labeled alternative in {@link X10Parser#assignmentOperator}.
	 * @param ctx the parse tree
	 */
	void exitAssignmentOperator2(X10Parser.AssignmentOperator2Context ctx);
	/**
	 * Enter a parse tree produced by the {@code assignmentOperator3}
	 * labeled alternative in {@link X10Parser#assignmentOperator}.
	 * @param ctx the parse tree
	 */
	void enterAssignmentOperator3(X10Parser.AssignmentOperator3Context ctx);
	/**
	 * Exit a parse tree produced by the {@code assignmentOperator3}
	 * labeled alternative in {@link X10Parser#assignmentOperator}.
	 * @param ctx the parse tree
	 */
	void exitAssignmentOperator3(X10Parser.AssignmentOperator3Context ctx);
	/**
	 * Enter a parse tree produced by the {@code assignmentOperator4}
	 * labeled alternative in {@link X10Parser#assignmentOperator}.
	 * @param ctx the parse tree
	 */
	void enterAssignmentOperator4(X10Parser.AssignmentOperator4Context ctx);
	/**
	 * Exit a parse tree produced by the {@code assignmentOperator4}
	 * labeled alternative in {@link X10Parser#assignmentOperator}.
	 * @param ctx the parse tree
	 */
	void exitAssignmentOperator4(X10Parser.AssignmentOperator4Context ctx);
	/**
	 * Enter a parse tree produced by the {@code assignmentOperator5}
	 * labeled alternative in {@link X10Parser#assignmentOperator}.
	 * @param ctx the parse tree
	 */
	void enterAssignmentOperator5(X10Parser.AssignmentOperator5Context ctx);
	/**
	 * Exit a parse tree produced by the {@code assignmentOperator5}
	 * labeled alternative in {@link X10Parser#assignmentOperator}.
	 * @param ctx the parse tree
	 */
	void exitAssignmentOperator5(X10Parser.AssignmentOperator5Context ctx);
	/**
	 * Enter a parse tree produced by the {@code assignmentOperator6}
	 * labeled alternative in {@link X10Parser#assignmentOperator}.
	 * @param ctx the parse tree
	 */
	void enterAssignmentOperator6(X10Parser.AssignmentOperator6Context ctx);
	/**
	 * Exit a parse tree produced by the {@code assignmentOperator6}
	 * labeled alternative in {@link X10Parser#assignmentOperator}.
	 * @param ctx the parse tree
	 */
	void exitAssignmentOperator6(X10Parser.AssignmentOperator6Context ctx);
	/**
	 * Enter a parse tree produced by the {@code assignmentOperator7}
	 * labeled alternative in {@link X10Parser#assignmentOperator}.
	 * @param ctx the parse tree
	 */
	void enterAssignmentOperator7(X10Parser.AssignmentOperator7Context ctx);
	/**
	 * Exit a parse tree produced by the {@code assignmentOperator7}
	 * labeled alternative in {@link X10Parser#assignmentOperator}.
	 * @param ctx the parse tree
	 */
	void exitAssignmentOperator7(X10Parser.AssignmentOperator7Context ctx);
	/**
	 * Enter a parse tree produced by the {@code assignmentOperator8}
	 * labeled alternative in {@link X10Parser#assignmentOperator}.
	 * @param ctx the parse tree
	 */
	void enterAssignmentOperator8(X10Parser.AssignmentOperator8Context ctx);
	/**
	 * Exit a parse tree produced by the {@code assignmentOperator8}
	 * labeled alternative in {@link X10Parser#assignmentOperator}.
	 * @param ctx the parse tree
	 */
	void exitAssignmentOperator8(X10Parser.AssignmentOperator8Context ctx);
	/**
	 * Enter a parse tree produced by the {@code assignmentOperator9}
	 * labeled alternative in {@link X10Parser#assignmentOperator}.
	 * @param ctx the parse tree
	 */
	void enterAssignmentOperator9(X10Parser.AssignmentOperator9Context ctx);
	/**
	 * Exit a parse tree produced by the {@code assignmentOperator9}
	 * labeled alternative in {@link X10Parser#assignmentOperator}.
	 * @param ctx the parse tree
	 */
	void exitAssignmentOperator9(X10Parser.AssignmentOperator9Context ctx);
	/**
	 * Enter a parse tree produced by the {@code assignmentOperator10}
	 * labeled alternative in {@link X10Parser#assignmentOperator}.
	 * @param ctx the parse tree
	 */
	void enterAssignmentOperator10(X10Parser.AssignmentOperator10Context ctx);
	/**
	 * Exit a parse tree produced by the {@code assignmentOperator10}
	 * labeled alternative in {@link X10Parser#assignmentOperator}.
	 * @param ctx the parse tree
	 */
	void exitAssignmentOperator10(X10Parser.AssignmentOperator10Context ctx);
	/**
	 * Enter a parse tree produced by the {@code assignmentOperator11}
	 * labeled alternative in {@link X10Parser#assignmentOperator}.
	 * @param ctx the parse tree
	 */
	void enterAssignmentOperator11(X10Parser.AssignmentOperator11Context ctx);
	/**
	 * Exit a parse tree produced by the {@code assignmentOperator11}
	 * labeled alternative in {@link X10Parser#assignmentOperator}.
	 * @param ctx the parse tree
	 */
	void exitAssignmentOperator11(X10Parser.AssignmentOperator11Context ctx);
	/**
	 * Enter a parse tree produced by the {@code assignmentOperator12}
	 * labeled alternative in {@link X10Parser#assignmentOperator}.
	 * @param ctx the parse tree
	 */
	void enterAssignmentOperator12(X10Parser.AssignmentOperator12Context ctx);
	/**
	 * Exit a parse tree produced by the {@code assignmentOperator12}
	 * labeled alternative in {@link X10Parser#assignmentOperator}.
	 * @param ctx the parse tree
	 */
	void exitAssignmentOperator12(X10Parser.AssignmentOperator12Context ctx);
	/**
	 * Enter a parse tree produced by the {@code assignmentOperator13}
	 * labeled alternative in {@link X10Parser#assignmentOperator}.
	 * @param ctx the parse tree
	 */
	void enterAssignmentOperator13(X10Parser.AssignmentOperator13Context ctx);
	/**
	 * Exit a parse tree produced by the {@code assignmentOperator13}
	 * labeled alternative in {@link X10Parser#assignmentOperator}.
	 * @param ctx the parse tree
	 */
	void exitAssignmentOperator13(X10Parser.AssignmentOperator13Context ctx);
	/**
	 * Enter a parse tree produced by the {@code assignmentOperator14}
	 * labeled alternative in {@link X10Parser#assignmentOperator}.
	 * @param ctx the parse tree
	 */
	void enterAssignmentOperator14(X10Parser.AssignmentOperator14Context ctx);
	/**
	 * Exit a parse tree produced by the {@code assignmentOperator14}
	 * labeled alternative in {@link X10Parser#assignmentOperator}.
	 * @param ctx the parse tree
	 */
	void exitAssignmentOperator14(X10Parser.AssignmentOperator14Context ctx);
	/**
	 * Enter a parse tree produced by the {@code assignmentOperator15}
	 * labeled alternative in {@link X10Parser#assignmentOperator}.
	 * @param ctx the parse tree
	 */
	void enterAssignmentOperator15(X10Parser.AssignmentOperator15Context ctx);
	/**
	 * Exit a parse tree produced by the {@code assignmentOperator15}
	 * labeled alternative in {@link X10Parser#assignmentOperator}.
	 * @param ctx the parse tree
	 */
	void exitAssignmentOperator15(X10Parser.AssignmentOperator15Context ctx);
	/**
	 * Enter a parse tree produced by the {@code assignmentOperator16}
	 * labeled alternative in {@link X10Parser#assignmentOperator}.
	 * @param ctx the parse tree
	 */
	void enterAssignmentOperator16(X10Parser.AssignmentOperator16Context ctx);
	/**
	 * Exit a parse tree produced by the {@code assignmentOperator16}
	 * labeled alternative in {@link X10Parser#assignmentOperator}.
	 * @param ctx the parse tree
	 */
	void exitAssignmentOperator16(X10Parser.AssignmentOperator16Context ctx);
	/**
	 * Enter a parse tree produced by the {@code assignmentOperator17}
	 * labeled alternative in {@link X10Parser#assignmentOperator}.
	 * @param ctx the parse tree
	 */
	void enterAssignmentOperator17(X10Parser.AssignmentOperator17Context ctx);
	/**
	 * Exit a parse tree produced by the {@code assignmentOperator17}
	 * labeled alternative in {@link X10Parser#assignmentOperator}.
	 * @param ctx the parse tree
	 */
	void exitAssignmentOperator17(X10Parser.AssignmentOperator17Context ctx);
	/**
	 * Enter a parse tree produced by the {@code assignmentOperator18}
	 * labeled alternative in {@link X10Parser#assignmentOperator}.
	 * @param ctx the parse tree
	 */
	void enterAssignmentOperator18(X10Parser.AssignmentOperator18Context ctx);
	/**
	 * Exit a parse tree produced by the {@code assignmentOperator18}
	 * labeled alternative in {@link X10Parser#assignmentOperator}.
	 * @param ctx the parse tree
	 */
	void exitAssignmentOperator18(X10Parser.AssignmentOperator18Context ctx);
	/**
	 * Enter a parse tree produced by the {@code assignmentOperator19}
	 * labeled alternative in {@link X10Parser#assignmentOperator}.
	 * @param ctx the parse tree
	 */
	void enterAssignmentOperator19(X10Parser.AssignmentOperator19Context ctx);
	/**
	 * Exit a parse tree produced by the {@code assignmentOperator19}
	 * labeled alternative in {@link X10Parser#assignmentOperator}.
	 * @param ctx the parse tree
	 */
	void exitAssignmentOperator19(X10Parser.AssignmentOperator19Context ctx);
	/**
	 * Enter a parse tree produced by the {@code assignmentOperator20}
	 * labeled alternative in {@link X10Parser#assignmentOperator}.
	 * @param ctx the parse tree
	 */
	void enterAssignmentOperator20(X10Parser.AssignmentOperator20Context ctx);
	/**
	 * Exit a parse tree produced by the {@code assignmentOperator20}
	 * labeled alternative in {@link X10Parser#assignmentOperator}.
	 * @param ctx the parse tree
	 */
	void exitAssignmentOperator20(X10Parser.AssignmentOperator20Context ctx);
	/**
	 * Enter a parse tree produced by the {@code prefixOp0}
	 * labeled alternative in {@link X10Parser#prefixOp}.
	 * @param ctx the parse tree
	 */
	void enterPrefixOp0(X10Parser.PrefixOp0Context ctx);
	/**
	 * Exit a parse tree produced by the {@code prefixOp0}
	 * labeled alternative in {@link X10Parser#prefixOp}.
	 * @param ctx the parse tree
	 */
	void exitPrefixOp0(X10Parser.PrefixOp0Context ctx);
	/**
	 * Enter a parse tree produced by the {@code prefixOp1}
	 * labeled alternative in {@link X10Parser#prefixOp}.
	 * @param ctx the parse tree
	 */
	void enterPrefixOp1(X10Parser.PrefixOp1Context ctx);
	/**
	 * Exit a parse tree produced by the {@code prefixOp1}
	 * labeled alternative in {@link X10Parser#prefixOp}.
	 * @param ctx the parse tree
	 */
	void exitPrefixOp1(X10Parser.PrefixOp1Context ctx);
	/**
	 * Enter a parse tree produced by the {@code prefixOp2}
	 * labeled alternative in {@link X10Parser#prefixOp}.
	 * @param ctx the parse tree
	 */
	void enterPrefixOp2(X10Parser.PrefixOp2Context ctx);
	/**
	 * Exit a parse tree produced by the {@code prefixOp2}
	 * labeled alternative in {@link X10Parser#prefixOp}.
	 * @param ctx the parse tree
	 */
	void exitPrefixOp2(X10Parser.PrefixOp2Context ctx);
	/**
	 * Enter a parse tree produced by the {@code prefixOp3}
	 * labeled alternative in {@link X10Parser#prefixOp}.
	 * @param ctx the parse tree
	 */
	void enterPrefixOp3(X10Parser.PrefixOp3Context ctx);
	/**
	 * Exit a parse tree produced by the {@code prefixOp3}
	 * labeled alternative in {@link X10Parser#prefixOp}.
	 * @param ctx the parse tree
	 */
	void exitPrefixOp3(X10Parser.PrefixOp3Context ctx);
	/**
	 * Enter a parse tree produced by the {@code prefixOp4}
	 * labeled alternative in {@link X10Parser#prefixOp}.
	 * @param ctx the parse tree
	 */
	void enterPrefixOp4(X10Parser.PrefixOp4Context ctx);
	/**
	 * Exit a parse tree produced by the {@code prefixOp4}
	 * labeled alternative in {@link X10Parser#prefixOp}.
	 * @param ctx the parse tree
	 */
	void exitPrefixOp4(X10Parser.PrefixOp4Context ctx);
	/**
	 * Enter a parse tree produced by the {@code prefixOp5}
	 * labeled alternative in {@link X10Parser#prefixOp}.
	 * @param ctx the parse tree
	 */
	void enterPrefixOp5(X10Parser.PrefixOp5Context ctx);
	/**
	 * Exit a parse tree produced by the {@code prefixOp5}
	 * labeled alternative in {@link X10Parser#prefixOp}.
	 * @param ctx the parse tree
	 */
	void exitPrefixOp5(X10Parser.PrefixOp5Context ctx);
	/**
	 * Enter a parse tree produced by the {@code prefixOp6}
	 * labeled alternative in {@link X10Parser#prefixOp}.
	 * @param ctx the parse tree
	 */
	void enterPrefixOp6(X10Parser.PrefixOp6Context ctx);
	/**
	 * Exit a parse tree produced by the {@code prefixOp6}
	 * labeled alternative in {@link X10Parser#prefixOp}.
	 * @param ctx the parse tree
	 */
	void exitPrefixOp6(X10Parser.PrefixOp6Context ctx);
	/**
	 * Enter a parse tree produced by the {@code prefixOp7}
	 * labeled alternative in {@link X10Parser#prefixOp}.
	 * @param ctx the parse tree
	 */
	void enterPrefixOp7(X10Parser.PrefixOp7Context ctx);
	/**
	 * Exit a parse tree produced by the {@code prefixOp7}
	 * labeled alternative in {@link X10Parser#prefixOp}.
	 * @param ctx the parse tree
	 */
	void exitPrefixOp7(X10Parser.PrefixOp7Context ctx);
	/**
	 * Enter a parse tree produced by the {@code prefixOp8}
	 * labeled alternative in {@link X10Parser#prefixOp}.
	 * @param ctx the parse tree
	 */
	void enterPrefixOp8(X10Parser.PrefixOp8Context ctx);
	/**
	 * Exit a parse tree produced by the {@code prefixOp8}
	 * labeled alternative in {@link X10Parser#prefixOp}.
	 * @param ctx the parse tree
	 */
	void exitPrefixOp8(X10Parser.PrefixOp8Context ctx);
	/**
	 * Enter a parse tree produced by the {@code prefixOp9}
	 * labeled alternative in {@link X10Parser#prefixOp}.
	 * @param ctx the parse tree
	 */
	void enterPrefixOp9(X10Parser.PrefixOp9Context ctx);
	/**
	 * Exit a parse tree produced by the {@code prefixOp9}
	 * labeled alternative in {@link X10Parser#prefixOp}.
	 * @param ctx the parse tree
	 */
	void exitPrefixOp9(X10Parser.PrefixOp9Context ctx);
	/**
	 * Enter a parse tree produced by the {@code binOp0}
	 * labeled alternative in {@link X10Parser#binOp}.
	 * @param ctx the parse tree
	 */
	void enterBinOp0(X10Parser.BinOp0Context ctx);
	/**
	 * Exit a parse tree produced by the {@code binOp0}
	 * labeled alternative in {@link X10Parser#binOp}.
	 * @param ctx the parse tree
	 */
	void exitBinOp0(X10Parser.BinOp0Context ctx);
	/**
	 * Enter a parse tree produced by the {@code binOp1}
	 * labeled alternative in {@link X10Parser#binOp}.
	 * @param ctx the parse tree
	 */
	void enterBinOp1(X10Parser.BinOp1Context ctx);
	/**
	 * Exit a parse tree produced by the {@code binOp1}
	 * labeled alternative in {@link X10Parser#binOp}.
	 * @param ctx the parse tree
	 */
	void exitBinOp1(X10Parser.BinOp1Context ctx);
	/**
	 * Enter a parse tree produced by the {@code binOp2}
	 * labeled alternative in {@link X10Parser#binOp}.
	 * @param ctx the parse tree
	 */
	void enterBinOp2(X10Parser.BinOp2Context ctx);
	/**
	 * Exit a parse tree produced by the {@code binOp2}
	 * labeled alternative in {@link X10Parser#binOp}.
	 * @param ctx the parse tree
	 */
	void exitBinOp2(X10Parser.BinOp2Context ctx);
	/**
	 * Enter a parse tree produced by the {@code binOp3}
	 * labeled alternative in {@link X10Parser#binOp}.
	 * @param ctx the parse tree
	 */
	void enterBinOp3(X10Parser.BinOp3Context ctx);
	/**
	 * Exit a parse tree produced by the {@code binOp3}
	 * labeled alternative in {@link X10Parser#binOp}.
	 * @param ctx the parse tree
	 */
	void exitBinOp3(X10Parser.BinOp3Context ctx);
	/**
	 * Enter a parse tree produced by the {@code binOp4}
	 * labeled alternative in {@link X10Parser#binOp}.
	 * @param ctx the parse tree
	 */
	void enterBinOp4(X10Parser.BinOp4Context ctx);
	/**
	 * Exit a parse tree produced by the {@code binOp4}
	 * labeled alternative in {@link X10Parser#binOp}.
	 * @param ctx the parse tree
	 */
	void exitBinOp4(X10Parser.BinOp4Context ctx);
	/**
	 * Enter a parse tree produced by the {@code binOp5}
	 * labeled alternative in {@link X10Parser#binOp}.
	 * @param ctx the parse tree
	 */
	void enterBinOp5(X10Parser.BinOp5Context ctx);
	/**
	 * Exit a parse tree produced by the {@code binOp5}
	 * labeled alternative in {@link X10Parser#binOp}.
	 * @param ctx the parse tree
	 */
	void exitBinOp5(X10Parser.BinOp5Context ctx);
	/**
	 * Enter a parse tree produced by the {@code binOp6}
	 * labeled alternative in {@link X10Parser#binOp}.
	 * @param ctx the parse tree
	 */
	void enterBinOp6(X10Parser.BinOp6Context ctx);
	/**
	 * Exit a parse tree produced by the {@code binOp6}
	 * labeled alternative in {@link X10Parser#binOp}.
	 * @param ctx the parse tree
	 */
	void exitBinOp6(X10Parser.BinOp6Context ctx);
	/**
	 * Enter a parse tree produced by the {@code binOp7}
	 * labeled alternative in {@link X10Parser#binOp}.
	 * @param ctx the parse tree
	 */
	void enterBinOp7(X10Parser.BinOp7Context ctx);
	/**
	 * Exit a parse tree produced by the {@code binOp7}
	 * labeled alternative in {@link X10Parser#binOp}.
	 * @param ctx the parse tree
	 */
	void exitBinOp7(X10Parser.BinOp7Context ctx);
	/**
	 * Enter a parse tree produced by the {@code binOp8}
	 * labeled alternative in {@link X10Parser#binOp}.
	 * @param ctx the parse tree
	 */
	void enterBinOp8(X10Parser.BinOp8Context ctx);
	/**
	 * Exit a parse tree produced by the {@code binOp8}
	 * labeled alternative in {@link X10Parser#binOp}.
	 * @param ctx the parse tree
	 */
	void exitBinOp8(X10Parser.BinOp8Context ctx);
	/**
	 * Enter a parse tree produced by the {@code binOp9}
	 * labeled alternative in {@link X10Parser#binOp}.
	 * @param ctx the parse tree
	 */
	void enterBinOp9(X10Parser.BinOp9Context ctx);
	/**
	 * Exit a parse tree produced by the {@code binOp9}
	 * labeled alternative in {@link X10Parser#binOp}.
	 * @param ctx the parse tree
	 */
	void exitBinOp9(X10Parser.BinOp9Context ctx);
	/**
	 * Enter a parse tree produced by the {@code binOp10}
	 * labeled alternative in {@link X10Parser#binOp}.
	 * @param ctx the parse tree
	 */
	void enterBinOp10(X10Parser.BinOp10Context ctx);
	/**
	 * Exit a parse tree produced by the {@code binOp10}
	 * labeled alternative in {@link X10Parser#binOp}.
	 * @param ctx the parse tree
	 */
	void exitBinOp10(X10Parser.BinOp10Context ctx);
	/**
	 * Enter a parse tree produced by the {@code binOp11}
	 * labeled alternative in {@link X10Parser#binOp}.
	 * @param ctx the parse tree
	 */
	void enterBinOp11(X10Parser.BinOp11Context ctx);
	/**
	 * Exit a parse tree produced by the {@code binOp11}
	 * labeled alternative in {@link X10Parser#binOp}.
	 * @param ctx the parse tree
	 */
	void exitBinOp11(X10Parser.BinOp11Context ctx);
	/**
	 * Enter a parse tree produced by the {@code binOp12}
	 * labeled alternative in {@link X10Parser#binOp}.
	 * @param ctx the parse tree
	 */
	void enterBinOp12(X10Parser.BinOp12Context ctx);
	/**
	 * Exit a parse tree produced by the {@code binOp12}
	 * labeled alternative in {@link X10Parser#binOp}.
	 * @param ctx the parse tree
	 */
	void exitBinOp12(X10Parser.BinOp12Context ctx);
	/**
	 * Enter a parse tree produced by the {@code binOp13}
	 * labeled alternative in {@link X10Parser#binOp}.
	 * @param ctx the parse tree
	 */
	void enterBinOp13(X10Parser.BinOp13Context ctx);
	/**
	 * Exit a parse tree produced by the {@code binOp13}
	 * labeled alternative in {@link X10Parser#binOp}.
	 * @param ctx the parse tree
	 */
	void exitBinOp13(X10Parser.BinOp13Context ctx);
	/**
	 * Enter a parse tree produced by the {@code binOp14}
	 * labeled alternative in {@link X10Parser#binOp}.
	 * @param ctx the parse tree
	 */
	void enterBinOp14(X10Parser.BinOp14Context ctx);
	/**
	 * Exit a parse tree produced by the {@code binOp14}
	 * labeled alternative in {@link X10Parser#binOp}.
	 * @param ctx the parse tree
	 */
	void exitBinOp14(X10Parser.BinOp14Context ctx);
	/**
	 * Enter a parse tree produced by the {@code binOp15}
	 * labeled alternative in {@link X10Parser#binOp}.
	 * @param ctx the parse tree
	 */
	void enterBinOp15(X10Parser.BinOp15Context ctx);
	/**
	 * Exit a parse tree produced by the {@code binOp15}
	 * labeled alternative in {@link X10Parser#binOp}.
	 * @param ctx the parse tree
	 */
	void exitBinOp15(X10Parser.BinOp15Context ctx);
	/**
	 * Enter a parse tree produced by the {@code binOp16}
	 * labeled alternative in {@link X10Parser#binOp}.
	 * @param ctx the parse tree
	 */
	void enterBinOp16(X10Parser.BinOp16Context ctx);
	/**
	 * Exit a parse tree produced by the {@code binOp16}
	 * labeled alternative in {@link X10Parser#binOp}.
	 * @param ctx the parse tree
	 */
	void exitBinOp16(X10Parser.BinOp16Context ctx);
	/**
	 * Enter a parse tree produced by the {@code binOp17}
	 * labeled alternative in {@link X10Parser#binOp}.
	 * @param ctx the parse tree
	 */
	void enterBinOp17(X10Parser.BinOp17Context ctx);
	/**
	 * Exit a parse tree produced by the {@code binOp17}
	 * labeled alternative in {@link X10Parser#binOp}.
	 * @param ctx the parse tree
	 */
	void exitBinOp17(X10Parser.BinOp17Context ctx);
	/**
	 * Enter a parse tree produced by the {@code binOp18}
	 * labeled alternative in {@link X10Parser#binOp}.
	 * @param ctx the parse tree
	 */
	void enterBinOp18(X10Parser.BinOp18Context ctx);
	/**
	 * Exit a parse tree produced by the {@code binOp18}
	 * labeled alternative in {@link X10Parser#binOp}.
	 * @param ctx the parse tree
	 */
	void exitBinOp18(X10Parser.BinOp18Context ctx);
	/**
	 * Enter a parse tree produced by the {@code binOp19}
	 * labeled alternative in {@link X10Parser#binOp}.
	 * @param ctx the parse tree
	 */
	void enterBinOp19(X10Parser.BinOp19Context ctx);
	/**
	 * Exit a parse tree produced by the {@code binOp19}
	 * labeled alternative in {@link X10Parser#binOp}.
	 * @param ctx the parse tree
	 */
	void exitBinOp19(X10Parser.BinOp19Context ctx);
	/**
	 * Enter a parse tree produced by the {@code binOp20}
	 * labeled alternative in {@link X10Parser#binOp}.
	 * @param ctx the parse tree
	 */
	void enterBinOp20(X10Parser.BinOp20Context ctx);
	/**
	 * Exit a parse tree produced by the {@code binOp20}
	 * labeled alternative in {@link X10Parser#binOp}.
	 * @param ctx the parse tree
	 */
	void exitBinOp20(X10Parser.BinOp20Context ctx);
	/**
	 * Enter a parse tree produced by the {@code binOp21}
	 * labeled alternative in {@link X10Parser#binOp}.
	 * @param ctx the parse tree
	 */
	void enterBinOp21(X10Parser.BinOp21Context ctx);
	/**
	 * Exit a parse tree produced by the {@code binOp21}
	 * labeled alternative in {@link X10Parser#binOp}.
	 * @param ctx the parse tree
	 */
	void exitBinOp21(X10Parser.BinOp21Context ctx);
	/**
	 * Enter a parse tree produced by the {@code binOp22}
	 * labeled alternative in {@link X10Parser#binOp}.
	 * @param ctx the parse tree
	 */
	void enterBinOp22(X10Parser.BinOp22Context ctx);
	/**
	 * Exit a parse tree produced by the {@code binOp22}
	 * labeled alternative in {@link X10Parser#binOp}.
	 * @param ctx the parse tree
	 */
	void exitBinOp22(X10Parser.BinOp22Context ctx);
	/**
	 * Enter a parse tree produced by the {@code binOp23}
	 * labeled alternative in {@link X10Parser#binOp}.
	 * @param ctx the parse tree
	 */
	void enterBinOp23(X10Parser.BinOp23Context ctx);
	/**
	 * Exit a parse tree produced by the {@code binOp23}
	 * labeled alternative in {@link X10Parser#binOp}.
	 * @param ctx the parse tree
	 */
	void exitBinOp23(X10Parser.BinOp23Context ctx);
	/**
	 * Enter a parse tree produced by the {@code binOp24}
	 * labeled alternative in {@link X10Parser#binOp}.
	 * @param ctx the parse tree
	 */
	void enterBinOp24(X10Parser.BinOp24Context ctx);
	/**
	 * Exit a parse tree produced by the {@code binOp24}
	 * labeled alternative in {@link X10Parser#binOp}.
	 * @param ctx the parse tree
	 */
	void exitBinOp24(X10Parser.BinOp24Context ctx);
	/**
	 * Enter a parse tree produced by the {@code binOp25}
	 * labeled alternative in {@link X10Parser#binOp}.
	 * @param ctx the parse tree
	 */
	void enterBinOp25(X10Parser.BinOp25Context ctx);
	/**
	 * Exit a parse tree produced by the {@code binOp25}
	 * labeled alternative in {@link X10Parser#binOp}.
	 * @param ctx the parse tree
	 */
	void exitBinOp25(X10Parser.BinOp25Context ctx);
	/**
	 * Enter a parse tree produced by the {@code binOp26}
	 * labeled alternative in {@link X10Parser#binOp}.
	 * @param ctx the parse tree
	 */
	void enterBinOp26(X10Parser.BinOp26Context ctx);
	/**
	 * Exit a parse tree produced by the {@code binOp26}
	 * labeled alternative in {@link X10Parser#binOp}.
	 * @param ctx the parse tree
	 */
	void exitBinOp26(X10Parser.BinOp26Context ctx);
	/**
	 * Enter a parse tree produced by the {@code binOp27}
	 * labeled alternative in {@link X10Parser#binOp}.
	 * @param ctx the parse tree
	 */
	void enterBinOp27(X10Parser.BinOp27Context ctx);
	/**
	 * Exit a parse tree produced by the {@code binOp27}
	 * labeled alternative in {@link X10Parser#binOp}.
	 * @param ctx the parse tree
	 */
	void exitBinOp27(X10Parser.BinOp27Context ctx);
	/**
	 * Enter a parse tree produced by the {@code binOp28}
	 * labeled alternative in {@link X10Parser#binOp}.
	 * @param ctx the parse tree
	 */
	void enterBinOp28(X10Parser.BinOp28Context ctx);
	/**
	 * Exit a parse tree produced by the {@code binOp28}
	 * labeled alternative in {@link X10Parser#binOp}.
	 * @param ctx the parse tree
	 */
	void exitBinOp28(X10Parser.BinOp28Context ctx);
	/**
	 * Enter a parse tree produced by the {@code binOp29}
	 * labeled alternative in {@link X10Parser#binOp}.
	 * @param ctx the parse tree
	 */
	void enterBinOp29(X10Parser.BinOp29Context ctx);
	/**
	 * Exit a parse tree produced by the {@code binOp29}
	 * labeled alternative in {@link X10Parser#binOp}.
	 * @param ctx the parse tree
	 */
	void exitBinOp29(X10Parser.BinOp29Context ctx);
	/**
	 * Enter a parse tree produced by {@link X10Parser#parenthesisOp}.
	 * @param ctx the parse tree
	 */
	void enterParenthesisOp(X10Parser.ParenthesisOpContext ctx);
	/**
	 * Exit a parse tree produced by {@link X10Parser#parenthesisOp}.
	 * @param ctx the parse tree
	 */
	void exitParenthesisOp(X10Parser.ParenthesisOpContext ctx);
	/**
	 * Enter a parse tree produced by {@link X10Parser#hasResultTypeopt}.
	 * @param ctx the parse tree
	 */
	void enterHasResultTypeopt(X10Parser.HasResultTypeoptContext ctx);
	/**
	 * Exit a parse tree produced by {@link X10Parser#hasResultTypeopt}.
	 * @param ctx the parse tree
	 */
	void exitHasResultTypeopt(X10Parser.HasResultTypeoptContext ctx);
	/**
	 * Enter a parse tree produced by {@link X10Parser#typeArgumentsopt}.
	 * @param ctx the parse tree
	 */
	void enterTypeArgumentsopt(X10Parser.TypeArgumentsoptContext ctx);
	/**
	 * Exit a parse tree produced by {@link X10Parser#typeArgumentsopt}.
	 * @param ctx the parse tree
	 */
	void exitTypeArgumentsopt(X10Parser.TypeArgumentsoptContext ctx);
	/**
	 * Enter a parse tree produced by {@link X10Parser#argumentListopt}.
	 * @param ctx the parse tree
	 */
	void enterArgumentListopt(X10Parser.ArgumentListoptContext ctx);
	/**
	 * Exit a parse tree produced by {@link X10Parser#argumentListopt}.
	 * @param ctx the parse tree
	 */
	void exitArgumentListopt(X10Parser.ArgumentListoptContext ctx);
	/**
	 * Enter a parse tree produced by {@link X10Parser#argumentsopt}.
	 * @param ctx the parse tree
	 */
	void enterArgumentsopt(X10Parser.ArgumentsoptContext ctx);
	/**
	 * Exit a parse tree produced by {@link X10Parser#argumentsopt}.
	 * @param ctx the parse tree
	 */
	void exitArgumentsopt(X10Parser.ArgumentsoptContext ctx);
	/**
	 * Enter a parse tree produced by {@link X10Parser#identifieropt}.
	 * @param ctx the parse tree
	 */
	void enterIdentifieropt(X10Parser.IdentifieroptContext ctx);
	/**
	 * Exit a parse tree produced by {@link X10Parser#identifieropt}.
	 * @param ctx the parse tree
	 */
	void exitIdentifieropt(X10Parser.IdentifieroptContext ctx);
	/**
	 * Enter a parse tree produced by {@link X10Parser#forInitopt}.
	 * @param ctx the parse tree
	 */
	void enterForInitopt(X10Parser.ForInitoptContext ctx);
	/**
	 * Exit a parse tree produced by {@link X10Parser#forInitopt}.
	 * @param ctx the parse tree
	 */
	void exitForInitopt(X10Parser.ForInitoptContext ctx);
	/**
	 * Enter a parse tree produced by {@link X10Parser#forUpdateopt}.
	 * @param ctx the parse tree
	 */
	void enterForUpdateopt(X10Parser.ForUpdateoptContext ctx);
	/**
	 * Exit a parse tree produced by {@link X10Parser#forUpdateopt}.
	 * @param ctx the parse tree
	 */
	void exitForUpdateopt(X10Parser.ForUpdateoptContext ctx);
	/**
	 * Enter a parse tree produced by {@link X10Parser#expressionopt}.
	 * @param ctx the parse tree
	 */
	void enterExpressionopt(X10Parser.ExpressionoptContext ctx);
	/**
	 * Exit a parse tree produced by {@link X10Parser#expressionopt}.
	 * @param ctx the parse tree
	 */
	void exitExpressionopt(X10Parser.ExpressionoptContext ctx);
	/**
	 * Enter a parse tree produced by {@link X10Parser#catchesopt}.
	 * @param ctx the parse tree
	 */
	void enterCatchesopt(X10Parser.CatchesoptContext ctx);
	/**
	 * Exit a parse tree produced by {@link X10Parser#catchesopt}.
	 * @param ctx the parse tree
	 */
	void exitCatchesopt(X10Parser.CatchesoptContext ctx);
	/**
	 * Enter a parse tree produced by {@link X10Parser#blockStatementsopt}.
	 * @param ctx the parse tree
	 */
	void enterBlockStatementsopt(X10Parser.BlockStatementsoptContext ctx);
	/**
	 * Exit a parse tree produced by {@link X10Parser#blockStatementsopt}.
	 * @param ctx the parse tree
	 */
	void exitBlockStatementsopt(X10Parser.BlockStatementsoptContext ctx);
	/**
	 * Enter a parse tree produced by {@link X10Parser#classBodyopt}.
	 * @param ctx the parse tree
	 */
	void enterClassBodyopt(X10Parser.ClassBodyoptContext ctx);
	/**
	 * Exit a parse tree produced by {@link X10Parser#classBodyopt}.
	 * @param ctx the parse tree
	 */
	void exitClassBodyopt(X10Parser.ClassBodyoptContext ctx);
	/**
	 * Enter a parse tree produced by {@link X10Parser#formalParameterListopt}.
	 * @param ctx the parse tree
	 */
	void enterFormalParameterListopt(X10Parser.FormalParameterListoptContext ctx);
	/**
	 * Exit a parse tree produced by {@link X10Parser#formalParameterListopt}.
	 * @param ctx the parse tree
	 */
	void exitFormalParameterListopt(X10Parser.FormalParameterListoptContext ctx);
}