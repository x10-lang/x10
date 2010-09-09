package lpg.lpgjavaruntime;

public class NullTerminalSymbolsException extends Exception
{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private String str;
    
    public NullTerminalSymbolsException()
    {
        str = "NullTerminalSymbolsException";
    }
    public NullTerminalSymbolsException(String str)
    {
        this.str = str;
    }
    public String toString() { return str; }
}
