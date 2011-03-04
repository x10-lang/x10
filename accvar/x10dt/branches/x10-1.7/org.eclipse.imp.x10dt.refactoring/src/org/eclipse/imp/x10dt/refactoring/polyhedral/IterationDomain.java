package org.eclipse.imp.x10dt.refactoring.polyhedral;

import java.util.List;

public interface IterationDomain {
    List<DomainParameter> getParameters();
    List<InductionVarDomain> getVarDomains();
}
