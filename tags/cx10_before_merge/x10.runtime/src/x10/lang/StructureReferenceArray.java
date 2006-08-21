package x10.lang;

/**
 * 
 * 
 */

public abstract class StructureReferenceArray extends structureArray {
	
	public StructureReferenceArray( dist D,int elSize) {
		super( D ,elSize);
	}

	abstract public byte    setByte( byte v, point/*(region)*/ p,int offset);
	abstract public char    setChar( char v, point/*(region)*/ p,int offset);
	abstract public boolean setBoolean( boolean v, point/*(region)*/ p,int offset);
	abstract public short   setShort( short v, point/*(region)*/ p,int offset);
	abstract public int     setInt( int v, point/*(region)*/ p,int offset);
	abstract public long    setLong( long v, point/*(region)*/ p,int offset);
	abstract public float   setFloat( float v, point/*(region)*/ p,int offset);
	abstract public double  setDouble( double v, point/*(region)*/ p,int offset);
	
	abstract public byte    setByte( byte v, int p,int offset);
	abstract public char    setChar( char v, int p,int offset);
	abstract public boolean setBoolean( boolean v, int p,int offset);
	abstract public short   setShort( short v, int p,int offset);
	abstract public int     setInt( int v, int p,int offset);
	abstract public long    setLong( long v, int p,int offset);
	abstract public float   setFloat( float v, int p,int offset);
	abstract public double  setDouble( double v, int p,int offset);
	
	
	abstract public byte    setByte( byte v, int p, int q,int offset);
	abstract public char    setChar( char v, int p, int q,int offset);
	abstract public boolean setBoolean( boolean v, int p, int q,int offset);
	abstract public short   setShort( short v, int p, int q,int offset);
	abstract public int     setInt( int v, int p, int q,int offset);
	abstract public long    setLong( long v, int p, int q,int offset);
	abstract public float   setFloat( float v, int p, int q,int offset);
	abstract public double  setDouble( double v, int p, int q,int offset);
	
	
	abstract public byte    setByte( byte v, int p, int q, int r,int offset);
	abstract public char    setChar( char v, int p, int q, int r,int offset);
	abstract public boolean setBoolean( boolean v, int p, int q, int r,int offset);
	abstract public short   setShort( short v, int p, int q, int r,int offset);
	abstract public int     setInt( int v, int p, int q, int r,int offset);
	abstract public long    setLong( long v, int p, int q, int r,int offset);
	abstract public float   setFloat( float v, int p, int q, int r,int offset);
	abstract public double  setDouble( double v, int p, int q, int r,int offset);
	
	
	abstract public byte    setByte( byte v, int p, int q, int r, int s,int offset);
	abstract public char    setChar( char v, int p, int q, int r, int s,int offset);
	abstract public boolean setBoolean( boolean v, int p, int q, int r, int s,int offset);
	abstract public short   setShort( short v, int p, int q, int r, int s,int offset);
	abstract public int     setInt( int v, int p, int q, int r, int s,int offset);
	abstract public long    setLong( long v, int p, int q, int r, int s,int offset);
	abstract public float   setFloat( float v, int p, int q, int r, int s,int offset);
	abstract public double  setDouble( double v, int p, int q, int r, int s,int offset);
	
		
}
