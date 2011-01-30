package polyglot.frontend;

/**
 * A listener that is notified when a Goal has started executing.
 */
public interface GoalListener {
    /**
     * Invoked when the given goal starts executing.
     * @param goal the goal that started executing
     */
    void taskStarted(Goal goal);
}
