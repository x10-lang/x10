package lpg.lpgjavaruntime;

public class ConfigurationElement
{
    ConfigurationElement next;

    StateElement last_element;
    public int stack_top,
               action_length,
               conflict_index,
               curtok,
               act;

    public void retrieveStack(int stack[])
    {
        StateElement tail = last_element;
        for (int i = stack_top; i >= 0; i--)
        {
            stack[i] = tail.number;
            tail = tail.parent;
        }
        return;
    }
};
