package org.eclipse.imp.x10dt.refactoring.polyhedral;

import java.util.List;

public interface StaticControlPart {
    List<DomainParameter> getParameters();
    List<SCoPStatement> getStatements();
    int[][] getInitialSchedule();
}
