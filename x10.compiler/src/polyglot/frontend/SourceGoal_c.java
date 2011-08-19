package polyglot.frontend;



public abstract class SourceGoal_c extends AbstractGoal_c implements SourceGoal {
    private static final long serialVersionUID = -2494790171806247993L;

    protected Job job;

    public SourceGoal_c(String name, Job job) {
        super(name);
        assert job != null;
        this.job = job;
    }
    
    public SourceGoal_c(Job job) {
        assert job != null;
        this.job = job;
    }

    public Job job() {
        return job;
    }

    public int hashCode() {
        return job().hashCode() + name().hashCode();
    }

    public boolean equals(Object o) {
        if (o instanceof SourceGoal) {
            SourceGoal g = (SourceGoal) o;
            return job().equals(g.job()) && name().equals(g.name());
        }
        return false;
    }

    public String toString() {
        return job() + ":" + job().extensionInfo() + ":"
        + name() + " (" + stateString() + ")";
    }
}
