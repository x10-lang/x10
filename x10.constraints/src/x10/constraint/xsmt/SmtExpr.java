package x10.constraint.xsmt;

import java.util.List;

public interface SmtExpr extends SmtTerm {
	int getNumChildren(); 
	SmtTerm get(int i);
	List<? extends SmtTerm> getChildren(); 
	
}
