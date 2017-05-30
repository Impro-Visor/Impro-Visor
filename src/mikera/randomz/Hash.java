package mikera.randomz;

import java.util.List;

/**
 * Static pseudo-random hash functions
 * 
 * @author Mike
 */
public class Hash {
	public static final long longHash(long a) {
		a ^= (a << 21);
		a ^= (a >>> 35);
		a ^= (a << 4);
		return a;
	}
	
	public static final long hash (double x) {
		return longHash(longHash(
				0x8000+Long.rotateLeft( longHash(Double.doubleToRawLongBits(x)),17)));
	}
	
	public static final long hash (double x, double y) {
		return longHash(longHash(
				hash(x)+Long.rotateLeft(longHash(Double.doubleToRawLongBits(y)),17)));
	}
	
	public static final long hash (double x, double y, double z) {
		return longHash(longHash(
				hash(x,y)+Long.rotateLeft(longHash(Double.doubleToRawLongBits(z)),17)));
	}
	
	public static final long hash (double x, double y, double z, double t) {		
		return longHash(longHash(
				hash(x,y,z)+Long.rotateLeft(longHash(Double.doubleToRawLongBits(t)),17)));
	}
	
	private static final double LONG_SCALE_FACTOR=1.0/(Long.MAX_VALUE+1.0);
	
	public static final double dhash(double x) {
		long h = hash(x);
		return (h&Long.MAX_VALUE)*LONG_SCALE_FACTOR;
	}
	
	public static final double dhash(double x, double y) {
		long h = hash(x,y);
		return (h&Long.MAX_VALUE)*LONG_SCALE_FACTOR;
	}
	
	public static final double dhash(double x, double y, double z) {
		long h = hash(x,y,z);
		return (h&Long.MAX_VALUE)*LONG_SCALE_FACTOR;
	}
	
	public static final double dhash(double x, double y , double z, double t) {
		long h = hash(x,y,z,t);
		return (h&Long.MAX_VALUE)*LONG_SCALE_FACTOR;
	}

	/**
	 * Hashcode for an int, defined as the value of the int itself for consistency with java.lang.Integer
	 * 
	 * @param value
	 * @return
	 */
	public static final int hashCode(int value) {
		return value;
	}
	
	/** 
	 * Hashcode for a double primitive. Matches Java hashCode.
	 * 
	 * @param d
	 * @return
	 */
	public static final int hashCode(double d) {
		return hashCode(Double.doubleToLongBits(d));
	}
	
	/**
	 * Hashcode for a long primitive Matches Java hashCode.
	 * @param l
	 * @return
	 */
	public static final int hashCode(long l) {
		return (int) (l ^ (l >>> 32));
	}
	
	/**
	 * Hashcode for  list of items. Matches Java hashcode
	 * @param list
	 * @return
	 */
	public static <T> int hashCode(List<T> list) {
		int length=list.size();
		int hashCode = 1;
		for (int i = 0; i < length; i++) {
			hashCode = 31 * hashCode + (list.get(i).hashCode());
		}
		return hashCode;
	}
	
	private static final int[] ZERO_HASHES=new int[20];
	static {
		int hashCode=1;
		for (int i=0; i<ZERO_HASHES.length; i++) {
			ZERO_HASHES[i]=hashCode;
			hashCode = 31 * hashCode;
		}
	}
	/**
	 * Return the hashCode for a vector of zeros
	 * @param length
	 * @return
	 */
	public static int zeroVectorHash(int length) {
		// TODO: when updating to latest mathz version use 
		// Maths.modPower32Bit(31,length); 
		
		if (length<ZERO_HASHES.length) return ZERO_HASHES[length];
		
		int hashCode=ZERO_HASHES[ZERO_HASHES.length-1];
		for (int i=0; i<=(length-ZERO_HASHES.length); i++) {
			hashCode = 31 * hashCode;
		}
		return hashCode;
	}
}
