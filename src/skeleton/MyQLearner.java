package skeleton;


import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
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
    private static final boolean DEBUG = false;
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
    	
    }
    
    @Override
    protected double explorationFunction(State state, String action)
    {
        return Double.NEGATIVE_INFINITY;
    }
    
    
    protected double f(Double u, Double n) 
    {
		if (Double.isNaN(u) || n < NE) {
			return Double.POSITIVE_INFINITY;
		}
		return u;		
	}
    
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
    
  
    
    
    protected double alpha(double Nsa, double gamma, double s_reward)
    {
    	
    	return 1/Nsa;	  	
    }
    
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
    	double reward = percept.score();
    	
    	
    	if (s_prime.isTerminal())
    	{   
    		if (q.containsKey(s_prime) == false)
    		{
    			HashMap<String, Double> action_for_s = new HashMap<String, Double>();
        		action_for_s.put("None", reward);
        		q.put(s_prime, action_for_s);
    		}
    		else
    		{
    			HashMap<String, Double> action_for_s = q.get(s_prime);
        		action_for_s.put("None", reward);
        		q.put(s_prime, action_for_s);
    		}
    	}
    	
    	if (s != null)
    	{
    		HashMap<String, Double> val =  n.get(s);
    		
    		if (null == val) {
    			val = new HashMap<String, Double>();
    			val.put(a, 1.0);
			}
    		else
    		{
    			if (val.containsKey(a) == false)
    			{
    				val.put(a, 1.0);
    			}
    			else
    			{
    				val.put(a, val.get(a) + 1);
    			}
    		}
    		
    		n.put(s, val);
    		
    		double max_a = get_max(s_prime, percept);
    		
    		Double s_reward;
    		if (q.containsKey(s) == false) {
    			s_reward = 0.0;
			}
    		else
    		{
    			if (q.get(s).containsKey(a) == false)
    				s_reward = 0.0;
    			else
    			    s_reward = q.get(s).get(a);
    		}
    		double Nsa = n.get(s).get(a);
    		double Al = alpha(Nsa, percept.gamma(), s_reward);
    		double new_reward = s_reward + Al * (r + (percept.gamma() * max_a) - s_reward);  
    		if (reward > 0)
    		{
    			
    			System.out.printf("\t\t\t nop %f\n", reward);
    		}
    		if (q.containsKey(s) == false)
    		{
    			HashMap<String, Double> action_for_s = new HashMap<String, Double>();
        		action_for_s.put(a, new_reward);
        		q.put(s, action_for_s);
    		}
    		else
    		{
    			HashMap<String, Double> action_for_s = q.get(s);
        		action_for_s.put(a, new_reward);
        		q.put(s, action_for_s);
    		}
    		
    	}
    	
    	
			s = s_prime;
		    a = myExplorationFunction(s_prime, percept);
		    r = reward;
            return a;
    }

}
