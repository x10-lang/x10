package lpg.lpgjavaruntime;

public class Adjunct extends AbstractToken implements IToken
{
    public Adjunct() {}
    public Adjunct(PrsStream prsStream, int startOffset, int endOffset, int kind)
    {
        super(prsStream, startOffset, endOffset, kind);
    }
    public IToken[] getFollowingAdjuncts() { return null; }
    public IToken[] getPrecedingAdjuncts() { return null; }
}

