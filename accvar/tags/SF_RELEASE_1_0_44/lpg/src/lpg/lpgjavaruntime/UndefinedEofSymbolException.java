package lpg.lpgjavaruntime;

public class UndefinedEofSymbolException extends Exception
{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private String str;
    
    public UndefinedEofSymbolException()
    {
        str = "UndefinedEofSymbolException";
    }
    public UndefinedEofSymbolException(String str)
    {
        this.str = str;
    }
    public String toString() { return str; }
}
