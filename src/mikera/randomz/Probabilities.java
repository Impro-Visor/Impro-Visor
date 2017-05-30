package mikera.randomz;

public class Probabilities {
	
	/** Calculate chance of lp0 being chosen vs lp1
	 *  both expressed in log probabilities
	 * @param lp0 log probability of outcome 0
	 * @param lp1 log probability of outcome 1
	 * @return Chance of outcome 0 vs. outcome 1
	 */
	public  static double logProbabilityChance(double lp0, double lp1) {
		if (lp0>lp1) {
			double p1=Math.exp(lp1-lp0);
			return 1/(1+p1);
		}
		
		double p0=Math.exp(lp0-lp1);
		return p0/(1+p0);
	}

}
