package x10.network;

public interface NetworkTransportCallbacks {
	
	public void runPlaceAddedHandler(int placeId);
	public void runPlaceRemovedHandler(int placeId);

	public void initDataStore(String connectTo);
	
	public long getEpoch();
	public void setEpoch(long epoch);
	
	static enum compressionCodec {NONE, SNAPPY};
	public compressionCodec useCompressionCodec();
}
