package x10.refactorings;

import com.ibm.wala.cast.tree.CAstSourcePositionMap;

public class RefactoringPosition {
    public int line;

    public int col;

    public int endLine;

    public int endCol;

    private boolean checkMode = false;
    private static boolean containsMode = false;

    public RefactoringPosition(int line, int col, int endLine, int endCol) {
        this.line = line;
        this.col = col;
        this.endLine = endLine;
        this.endCol = endCol;
    }

    public RefactoringPosition(polyglot.util.Position p) {
        this(p.line(), p.column(), p.endLine(), p.endColumn());
    }

    public RefactoringPosition(CAstSourcePositionMap.Position p) {
        this(p.getFirstLine(), p.getFirstCol(), p.getLastLine(), p.getLastCol());
    }

    /**
     * Determines whether object equality for this RefactoringPosition object is determined by reference or by
     * standard/contains equality. Setting checkMode true allows multiple RefactoringPositions to be put into a HashMap,
     * even if they represent the same source position.
     */
    public void setCheckMode() {
        checkMode = !checkMode;
    }

    /**
     * Turns on the contains mode of checking for RefactoringPosition objects. Does not affect objects in checkMode.
     */
    public static void setContainsMode() {
        containsMode = true;
    }

    /**
     * Turns off the contains mode of checking for RefactoringPosition objects. Does not affect objects in checkMode.
     */
    public static void unsetContainsMode() {
        containsMode = false;
    }

    /**
     * The equals method for a RefactoringPosition has three behaviors, depending on the mode of checking: 1. object
     * reference equality [checkMode] 2. containment (this contains compared object) [containsMode] 3. "loose" source
     * position equality (same lines, but possibly different column positions) [standard]
     */
    public boolean equals(Object o) {
        if (checkMode)
            return this == o;
        else if (o instanceof RefactoringPosition) {
            RefactoringPosition rp = (RefactoringPosition) o;
            if (containsMode)
                return this.contains(rp); // || */rp.contains(this);
            else
                return (this.line == rp.line)
                        && (this.endLine == rp.endLine)
                        && (((this.col <= rp.col) && (this.endCol >= rp.col)) || ((this.col <= rp.endCol) && (this.endCol >= rp.endCol)))
                        && ((this.endCol - this.col) == (rp.endCol - rp.col));
            // ||((this.col >= rp.col) && (this.endCol <= rp.endCol)));
        } else
            return false;

    }

    /**
     * Returns true if "this" position contains the position in the argument. Containment is not well defined for
     * positions that span multiple lines.
     * 
     * @param rp
     * @return
     */
    public boolean contains(RefactoringPosition rp) {
        return (this.line == rp.line) && (this.endLine == rp.endLine) && (this.col <= rp.col)
                && (this.endCol >= rp.endCol);
        // && (((this.col <= rp.col) && (this.endCol >= rp.col)) || ((this.col <= rp.endCol) && (this.endCol >=
        // rp.endCol)));
    }

    public String toString() {
        return "(" + line + "," + col + ")-(" + endLine + "," + endCol + ")";
    }

    public int hashCode() {
        return (this.line << 4) + this.endLine;
    }
}
