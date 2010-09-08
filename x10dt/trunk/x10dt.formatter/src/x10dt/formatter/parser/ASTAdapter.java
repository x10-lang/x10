/*******************************************************************************
* Copyright (c) 2008 IBM Corporation.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*    Robert Fuhrer (rfuhrer@watson.ibm.com) - initial API and implementation

*******************************************************************************/

package x10dt.formatter.parser;

import lpg.runtime.IAst;
import lpg.runtime.IToken;

import org.eclipse.imp.language.ILanguageService;
import org.eclipse.imp.xform.pattern.parser.ASTAdapterBase;

import x10dt.formatter.parser.ast.AbstractASTNodeList;

public class ASTAdapter extends ASTAdapterBase implements ILanguageService,
		X10Parsersym {

	public boolean isList(Object astNode) {
		return astNode instanceof AbstractASTNodeList;
	}

	@Override
	public Object[] getChildren(Object astNode) {
		return ((IAst) astNode).getChildren().toArray();
	}

	@Override
	public int getOffset(Object astNode) {
		return ((IAst) astNode).getLeftIToken().getStartOffset();
	}

	@Override
	public int getLength(Object astNode) {
		IAst ast = (IAst) astNode;
		IToken left = ast.getLeftIToken();
		IToken right = ast.getRightIToken();

		// special case for epsilon trees
		if (left.getTokenIndex() > right.getTokenIndex()) {
			return 0;
		} else {
			int start = left.getStartOffset();
			int end = right.getEndOffset();
			return end - start + 1;
		}
	}

	@Override
	public String getTypeOf(Object astNode) {
		return astNode.getClass().getName();
	}

	@Override
	public boolean isMetaVariable(Object astNode) {
		IAst ast = (IAst) astNode;
		if (ast.getChildren().size() == 0) {
			int k = ast.getLeftIToken().getKind();

			switch (k) {
			case TK_METAVARIABLE_PackageName:
			case TK_METAVARIABLE_X10ClassModifier:
			case TK_METAVARIABLE_X10ClassModifiers:
			case TK_METAVARIABLE_Property:
			case TK_METAVARIABLE_Properties:
			case TK_METAVARIABLE_identifier:
			case TK_METAVARIABLE_Type:
			case TK_METAVARIABLE_ConstExpression:
			case TK_METAVARIABLE_DepParameterExp:
			case TK_METAVARIABLE_MethodModifier:
			case TK_METAVARIABLE_Statement:
			case TK_METAVARIABLE_ClockList:
			case TK_METAVARIABLE_Expression:
			case TK_METAVARIABLE_FieldModifier:
			case TK_METAVARIABLE_ArgumentList:
			case TK_METAVARIABLE_Object:
			case TK_METAVARIABLE_TypeNode:
			case TK_METAVARIABLE_PackageNode:
			case TK_METAVARIABLE_Import:
			case TK_METAVARIABLE_ClassDecl:
			case TK_METAVARIABLE_ClassBodyDeclaration:
			case TK_METAVARIABLE_ClassBodyDeclarations:
			case TK_METAVARIABLE_TypeDeclaration:
			case TK_METAVARIABLE_TypeDeclarations:
			case TK_METAVARIABLE_FieldModifiers:
			case TK_METAVARIABLE_BlockStatement:
			case TK_METAVARIABLE_BlockStatements:
			case TK_METAVARIABLE_ClassName:
			case TK_METAVARIABLE_TypeName:
			case TK_METAVARIABLE_TypeArgument:
			case TK_METAVARIABLE_TypeArgumentList:
			case TK_METAVARIABLE_Primary:
			case TK_METAVARIABLE_AmbiguousName:
			case TK_METAVARIABLE_WhenStatement:
			case TK_METAVARIABLE_FormalParameterList:
			case TK_METAVARIABLE_LastFormalParameter:
			case TK_METAVARIABLE_FormalParameter:
			case TK_METAVARIABLE_FormalParameters:
			case TK_METAVARIABLE_StatementExpression:
			

				return true;
			default:
				return false;
			}
		}

		return false;
	}

}
