package lpg.lpgjavaruntime;

//
//
//
public class ConfigurationStack
{
    private final static int TABLE_SIZE = 1021; // 1021 is a prime
    private ConfigurationElement table[] = new ConfigurationElement[TABLE_SIZE];
    private ObjectTuple configuration_stack = new ObjectTuple(1 << 12);
    private StateElement state_root;
    private int configuration_size,
                stacks_size,
                state_element_size;

    ParseTable prs;

    public ConfigurationStack(ParseTable prs)
    {
        this.prs = prs;

        state_element_size++;

        state_root = new StateElement();
        state_root.parent = null;
        state_root.siblings = null;
        state_root.children = null;
        state_root.number = prs.getStartState();
    }

    StateElement makeStateList(StateElement parent, int stack[], int index, int stack_top)
    {
        for (int i = index; i <= stack_top; i++)
        {
            state_element_size++;

            StateElement state = new StateElement();
            state.number = stack[i];
            state.parent = parent;
            state.children = null;
            state.siblings = null;

            parent.children = state;
            parent = state;
        }

        return parent;
    }

    StateElement findOrInsertStack(StateElement root, int stack[], int index, int stack_top)
    {
        int state_number = stack[index];
        for (StateElement p = root; p != null; p = p.siblings)
        {
            if (p.number == state_number)
                return (index == stack_top
                               ? p
                               : p.children == null
                                                ? makeStateList(p, stack, index + 1, stack_top)
                                                : findOrInsertStack(p.children, stack, index + 1, stack_top));
        }

        state_element_size++;

        StateElement node = new StateElement();
        node.number = state_number;
        node.parent = root.parent;
        node.children = null;
        node.siblings = root.siblings;
        root.siblings = node;

        return (index == stack_top ? node : makeStateList(node, stack, index + 1, stack_top));
    }

    public boolean findConfiguration(int stack[], int stack_top, int curtok)
    {
        StateElement last_element = findOrInsertStack(state_root, stack, 0, stack_top);
        int hash_address = curtok % TABLE_SIZE;
        for (ConfigurationElement configuration = table[hash_address]; configuration != null; configuration = configuration.next)
        {
            if (configuration.curtok == curtok && last_element == configuration.last_element)
                return true;
        }

        return false;
    }

    //
    //
    //
    public void push(int stack[], int stack_top, int conflict_index, int curtok, int action_length)
    {
        ConfigurationElement configuration = new ConfigurationElement();
        int hash_address = curtok % TABLE_SIZE;
        configuration.next = table[hash_address];
        table[hash_address] = configuration;
        configuration_size++; // keep track of number of configurations

        configuration.stack_top = stack_top;
        stacks_size += (stack_top + 1);  // keep track of number of stack elements processed
        configuration.last_element = findOrInsertStack(state_root, stack, 0, stack_top);
        configuration.conflict_index = conflict_index;
        configuration.curtok = curtok;
        configuration.action_length = action_length;

        configuration_stack.add(configuration);

        return;
    }

    //
    //
    //
    public ConfigurationElement pop()
    {
        ConfigurationElement configuration = null;
        if (configuration_stack.size() > 0)
        {
            int index = configuration_stack.size() - 1;
            configuration = (ConfigurationElement) configuration_stack.get(index);
            configuration.act = prs.baseAction(configuration.conflict_index++);
            if (prs.baseAction(configuration.conflict_index) == 0)
                configuration_stack.reset(index);
        }

        return configuration;
    }

    //
    //
    //
    public ConfigurationElement top()
    {
        ConfigurationElement configuration = null;
        if (configuration_stack.size() > 0)
        {
            int index = configuration_stack.size() - 1;
            configuration = (ConfigurationElement) configuration_stack.get(index);
            configuration.act = prs.baseAction(configuration.conflict_index);
        }

        return configuration;
    }

    public int size() { return configuration_stack.size(); }

    public int configurationSize() { return configuration_size; }

    int numStateElements() { return state_element_size; }

    int stacksSize()
    {
        return stacks_size;
    }
};
