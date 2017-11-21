package Client;

public class Terrain {
	public final int COMPRESSION;
	public final int[][] data;

	public Terrain(int[][] data, int compression) {
		this.data = data;
		COMPRESSION = compression;
	}
}
