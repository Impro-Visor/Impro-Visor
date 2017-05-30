package mikera.randomz;

/**
 * Static method class for random functions
 * 
 * @author Mike
 *
 */
public class Randomz {

	/**
	 * Creates a random number generator
	 * @return
	 */
	public static java.util.Random getGenerator() {
		return new mikera.util.Random();
	}
	
	public static java.util.Random getGenerator(long seed) {
		return new mikera.util.Random(seed);
	}

}
