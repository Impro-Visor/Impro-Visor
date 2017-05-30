package mikera.randomz;

import mikera.util.Rand;

public final class ProbabilityPicker<O> {
	private static final double[] NULLCHANCES=new double[0];
	private static final Object[] NULLOBJECTS=new Object[0];
	
	private int count=0;
	private double[] chances=NULLCHANCES;
	private double[] belows=NULLCHANCES;
	private Object[] objects=NULLOBJECTS;
	private double total=0.0;
	
	/**
	 * Picks a random object
	 * 
	 * @return Random object chosen with associated relative probability
	 */
	@SuppressWarnings("unchecked")
	public O pick() {
		double r=Rand.nextDouble()*total;
		
		int i=0;
		int ir=1;
		for (;;) {
			double ci=chances[i];
			if (r<ci) return (O)objects[i];
			r-=ci;
			
			double bi=belows[i];
			if (r<bi) {
				i=childIndex(i,ir,0);
			} else {
				i=childIndex(i,ir,1);
				r-=bi;
			}
			
			ir*=2;
		}
	}
	
	private void ensureSize(int n) {
		if (n<=count) return;
		if (n>chances.length) {
			int nn=Math.max(n,(chances.length*2));
			double[] newChances=new double[nn];
			double[] newBelows=new double[nn];
			Object[] newObjects=new Object[nn];

			System.arraycopy(chances, 0, newChances,0,count);
			System.arraycopy(belows, 0, newBelows,0, count);
			System.arraycopy(objects, 0, newObjects,0,count);
			
			chances=newChances;
			objects=newObjects;
			belows=newBelows;
		}
	}
	
	
	public void add(O object, double probability) {
		int i=getIndex(object);
		if (i<0) {
			addNew(object,probability);
			return;
		}
		setChance(i,probability+chances[i]);
	}
	
	private void addNew(O object, double probability) {
		if (probability<0) return;
		
		ensureSize(count+1);
		
		int i=count;
		count++;
		
		objects[i]=object;

		setChance(i,probability);		
	}
	
	public int getIndex(O o) {
		for (int i=0; i<count; i++) {
			if (objects[i].equals(o)) {
				return i;
			}
		}		
		return -1;
	}
	
	public double get(O o) {
		int i=getIndex(o);
		if (i<0) return 0.0;
		return chances[i];
	}
	
	protected void update(O o, double p) {
		for (int i=0; i<count; i++) {
			if (objects[i].equals(o)) {
				setChance(i,p);
				return;
			}
		}
	}
	
	private void setChance(int i, double p) {
		double d=p-chances[i];
		total+=d;
		chances[i]=p;
		
		int r=order(i);
		while (r>1) {
			int pi=parentIndex(i,r);
			int pr=r/2; // parent order
			
			if (((i+1)&pr)>0) {
				// top branch
			} else {
				// update below
				belows[pi]+=d;
			}
			
			r=pr;
			i=pi;
		}
	}
	
	public double getTotal() {
		return total;
	}
	
	public int getCount() {
		return count;
	}
	
	@SuppressWarnings("unchecked")
	public void remove(Object object) {
		O o=(O)object;
		int i=getIndex(o);
		if (i<0) throw new Error("Object not found: "+object.toString());
		remove(i);
	}
		
	private void remove(int i) {
		setChance(i,0);
		int last=count-1;
		
		if (i<last) {
			//move object from end
			double cd=chances[last];
			setChance(last,0);
			objects[i]=objects[last];
			setChance(i,cd);
			objects[last]=null;
		}
	
		count--;
		return;
	}
	
	@SuppressWarnings("unchecked")
	protected void swap(int a, int b) {
		double ca=chances[a];
		double cb=chances[b];
		O oa=(O)objects[a];
		setChance(a,cb);
		setChance(b,ca);
		objects[a]=objects[b];
		objects[b]=oa;	
	}
	
	/*
	 * Branching logic is as follows:
	 * 
	 * For index i:
	 *   branch 0 child is i + order(i)
	 *   branch 1 child is i + 2*order(i)
	 *   
	 * Where order(0)=1
	 * And order of any child is order of parent times two
	 */
	
	public static int parentIndex(int i) {
		int po=order(i);
		return parentIndex(i,po);
	}
	
	private static int parentIndex(int i, int po) {
		return ( ((i+1)&(~po))|(po>>1) ) -1;
	}
	
	public static int childIndex(int i, int branch) {
		return childIndex(i,order(i),branch);
	}
	
	private static int childIndex(int i, int ir, int branch) {
		return i+(1+branch)*ir;
	}
	
	private static int fillBitsRight(int n) {
		// TODO: use bitwise ops from mikera
		n = n | (n >> 1);
		n = n | (n >> 2);
		n = n | (n >> 4);
		n = n | (n >> 8);
		n = n | (n >> 16);
		return n;
	}
	
	private static int roundUpToPowerOfTwo(int n) {
		// TODO: use bitwise ops from mikera
		n = n - 1;
		n = fillBitsRight(n);
		n = n + 1;
		return n;
	}
	
	public static int order(int i) {
		return roundUpToPowerOfTwo(i+2)/2;
	}
}
