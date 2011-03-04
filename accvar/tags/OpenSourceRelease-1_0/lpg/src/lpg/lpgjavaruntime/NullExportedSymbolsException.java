package lpg.lpgjavaruntime;

public class NullExportedSymbolsException extends Exception
{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private String str;
    
    public NullExportedSymbolsException()
    {
        str = "NullExportedSymbolsException";
    }
    public NullExportedSymbolsException(String str)
    {
        this.str = str;
    }
    public String toString() { return str; }
}
