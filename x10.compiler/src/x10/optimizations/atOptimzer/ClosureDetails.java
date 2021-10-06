package x10.optimizations.atOptimzer;

import java.util.*;

public class ClosureDetails {
	public String name;
	public int fromClass = 0;
	public int fromMethod = 0;
	public int fromplace = 0;
	public int toClass = 0;
	public int toMethod = 0;
	public int toplace = 0;
	public int countCRSWS = -1;

	public ClosureDetails(String closureName, int fromClass, int fromMethod, int fromplace ) {
		this.name = closureName;
		this.fromClass = fromClass;
		this.fromMethod = fromMethod;
		this.fromplace = fromplace;
	}

}

