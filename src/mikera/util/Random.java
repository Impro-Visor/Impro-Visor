package mikera.util;

import mikera.randomz.Hash;
 
/**
 * Implementation of Random class using XORShift RNG
 * 
 * Not thread safe: designed for use in single threaded contexts
 * 
 * @author Mike Anderson
 *
 */
public final class Random extends java.util.Random {
	private static final long serialVersionUID = 6868944865706425166L;

	private long state=ensureState(System.nanoTime());
	
	public Random() {
		
	}
	
	public Random(long state) {
		this.state=ensureState(state);
	}
		
	private static final long ensureState(long l) {
		if (l==0) return 54384849948L;
		return l;
	}

	@Override
	protected int next(int bits) {
		return (int)(nextLong()>>>(64-bits));
	}
	
	@Override
	public long nextLong() {
		long a=state;
		state=Rand.xorShift64(a);
		return a;
	}
	
	@Override
	public void setSeed(long seed) {
		state=ensureState(seed);
	}
	
	public long getSeed() {
		return state;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof Random) {
			return equals((Random)o);
		}
		return super.equals(o);
	}
	
	@Override 
	public Random clone() {
		return new Random(this.state);
	}
	
	public boolean equals(Random o) {
		return state==o.state;
	}
	
	@Override
	public int hashCode() {
		return Hash.hashCode(state);
	}
}