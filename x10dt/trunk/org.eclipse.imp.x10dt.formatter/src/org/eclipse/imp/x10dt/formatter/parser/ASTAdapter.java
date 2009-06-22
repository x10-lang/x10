package org.eclipse.imp.x10dt.formatter.parser;

import lpg.runtime.IAst;
import lpg.runtime.IToken;

import org.eclipse.imp.language.ILanguageService;
import org.eclipse.imp.x10dt.formatter.parser.ast.AbstractASTNodeList;
import org.eclipse.imp.xform.pattern.parser.ASTAdapterBase;

public class ASTAdapter extends ASTAdapterBase implements ILanguageService,
		PatternX10Parsersym {
	
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
			switch (ast.getLeftIToken().getKind()) {
			default:
				return false;
			}
		}

		return false;
	}

}
