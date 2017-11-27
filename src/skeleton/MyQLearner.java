package skeleton;

import util.Percept;
import util.QLearner;
import util.State;



/**
 * An agent that uses value iteration to play the game.
 * 
 * @author Mitch Parry
 * @version 2014-03-28
 * 
 */
public class MyQLearner extends QLearner
{
    //private static final boolean DEBUG = false;
    private static final double NE = 100.0;
    private State s;
    private String a;
    private double r;

    /**
     * The constructor takes the name.
     * 
     * @param name
     *            the name of the player.
     */
    public MyQLearner(String name)
    {
        super(name);
        s = null;
        a = null;
        r = Double.NEGATIVE_INFINITY;
    }
    /*
    protected Double get_max(State state, Percept percept)
    {
    	double max = Double.NEGATIVE_INFINITY;
		if (state.isTerminal()) 
		{
			max = q.get(state).get("None");
		} 
		else 
		{
			for (String actions : percept.actions()) 
			{
				HashMap<String, Double>  aux = q.get(state);
				if (aux != null)
				{
					Double reward = aux.get(actions);
					if (null != reward && reward > max) 
					{
						max = reward;
					}
				}
			}
		}
		if (max == Double.NEGATIVE_INFINITY)
		{
			max = 0.0;
		}
		return max;
    	
    }*/
    
    @Override
    protected double explorationFunction(State state, String action)
    {
    	Double u = value(getQ(),state,action);
    	Double n = value(getN(),state,action);
    	Double reward = f(u, n);
        return reward;
    }
    
    
    protected double f(Double u, Double n) 
    {
		if (n < NE) 
		{
			return Double.POSITIVE_INFINITY;
		}
		else
		{
			return u;	
		}
	}
    
    /*
    protected String myExplorationFunction(State state, Percept percept)
    {
    	List<String> actions = percept.actions();
    	double max = Double.NEGATIVE_INFINITY;
    	String action = null;
    	for (String a : actions)
    	{
    		double Q,N;
    		if (q.containsKey(state) == false)
    		{
    			 Q = Double.NaN;
    			 N = 0;
    		}
    		else
    		{	
    			if (q.get(state).containsKey(a) == false)
    			{
    				Q = Double.NaN;
       			 	N = 0;
    			}
    			else 
    			{
    			 Q = q.get(state).get(a);
    			 N = n.get(state).get(a);
    			}
    		}
    		double reward = f(Q, N);
    		if (reward > max) 
    		{
				max = reward;
				action = a;
			}
    	}
    	return action;
    }
    */
    
    /**
     * Plays the game using a Q-Learning agent.
     * 
     * @param percept
     *            the percept.
     * @return the desired action.
     */
    public String play(Percept percept)
    {
    	
    	State s_prime = new MyState(percept);
    	double rewardPrime = percept.current().reward();
    	double Gamma = percept.gamma();
    	
    	
    	if (s_prime.isTerminal())
    	{   for (String action : percept.actions())
        	{
    			putValue(getQ(), s_prime, action, rewardPrime);
        	}
    	}
    	
    	if (s != null)
    	{
    		addValue(getN(),s,a,1.0);  		
    		double s_reward = value(getQ(),s ,a);
    		double Nsa = value(getN(),s,a); 
    		double maxPrimeReward =  maxValue(s_prime, percept.actions());
    		double new_reward = s_reward + (1/Nsa) * (r + (Gamma * maxPrimeReward) - s_reward);  
    		putValue(getQ(),s,a,new_reward);		
    	}
    	
    	if (s_prime.isTerminal())
    	{
    		s = null;
		    a = null;
		    r = Double.NaN;  
    	}
    	else
    	{
			s = s_prime;
		    a = maxExplorationAction(s_prime, percept.actions());
		    r = rewardPrime;      
    	}
    	
    	return a;
    }

}
