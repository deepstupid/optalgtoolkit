/*
 Optimization Algorithm Toolkit (OAT)
 http://sourceforge.net/projects/optalgtoolkit
 Copyright (C) 2006  Jason Brownlee

 This program is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public License
 as published by the Free Software Foundation; either version 2
 of the License, or (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program; if not, write to the Free Software
 Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
*/
package com.oat.domains.hbs;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.oat.AlgorithmRunException;
import com.oat.InitialisationException;
import com.oat.InvalidConfigurationException;
import com.oat.Problem;
import com.oat.Solution;
import com.oat.SolutionEvaluationException;
import com.oat.domains.cfo.CFOProblemInterface;
import com.oat.domains.cfo.CFOSolution;
import com.oat.utils.AlgorithmUtils;
import com.oat.utils.BinaryDecodeMode;

/**
 * Description: 
 *  
 * Date: 04/09/2007<br/>
 * @author Jason Brownlee 
 *
 * <br/>
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 
 * </pre>
 */
public class HuygensProblem extends Problem implements CFOProblemInterface
{
	/**
	 * Maximum number of points that can be sent to the server at a time
	 */
	public final static int ITERATION_SIZE = 1000;
	
	// user configuration
    protected int series = 20;
    protected int landscape = 1; // specific landscape in series
    protected String email = "";
    protected BinaryDecodeMode decodeMode = BinaryDecodeMode.GrayCode;
    
    // internal state
    protected HuygensWSClient client;    
	protected int dimensions = 2;
	protected int bitPrecision = 64;
    protected boolean toroidal = true;
    
    
    
   
    // normal execution path for a single solution, no need to worry about things
    @Override
    protected double problemSpecificCost(Solution solution)
    {
    	double [] coord = ((CFOSolution)solution).getCoordinate();    	
    	
        Object [] result = null;
        
        try
        {
            result = client.train(new double[][]{coord});
        }
        catch(Exception e)
        {
            throw new AlgorithmRunException("Unable to evaluate solution.\n" + e.getMessage(), e);
        }
        
        if(result!=null && !((Boolean)result[0]))
        {
            throw new AlgorithmRunException("Failure while evaluating solution.\n" + result[1]);
        }
        
        return ((double[])result[2])[0];
    }
    
    // overriden for a batch of points
    // ensures that no more than 1000 points are sent to the server at a time
    @Override
    public <T extends Solution> void cost(LinkedList<T> ss)
    {
        LinkedList<T> toEval = new LinkedList<T>();
        toEval.addAll(ss);
        
        // strip out all those solutions that have already been evaluated
        for (Iterator<T> iterator = toEval.iterator(); iterator.hasNext();)
        {
            T s = iterator.next();
            if(s.isEvaluated())
            {
                iterator.remove();
            }            
        }

        if(!toEval.isEmpty())
        {
            // divide toEval up into digestable chunks
            if(toEval.size() > ITERATION_SIZE)
            {
                do
                {
                    // build a batch of a suitable size
                    LinkedList<T> tmp = new LinkedList<T>();
                    for (int i = 0; i < ITERATION_SIZE && !toEval.isEmpty(); i++)
                    {
                        tmp.add(toEval.removeFirst());
                    }
                    // evaluate
                    batchEvaluate(tmp);
                }
                // continue only while there are solutions left to evaluate
                while(!toEval.isEmpty() && canEvaluate());
            }
            else
            {
                // evaluate all solutions in toEval
                batchEvaluate(toEval);
            }
        }
    }
    
    
    
    protected <T extends Solution> void batchEvaluate(LinkedList<T> pop)
        throws AlgorithmRunException
    {        
        // check safety
        for(Solution s : pop)
        {
            checkSolutionForSafety(((CFOSolution)s).getCoordinate()); // slow but safe
        }
        
        // perform the evaluation
        double [][] coords = new double[pop.size()][];
        for (int i = 0; i < coords.length; i++)
        {
            coords[i] = ((CFOSolution)pop.get(i)).getCoordinate();
        }
        
        Object [] result = null;            
        try
        {
            result = client.train(coords);
        }
        catch(Exception e)
        {
            throw new AlgorithmRunException("Unable to batch evaluate solutions.\n" + e.getMessage(), e);
        }
        
        if(result!=null && !((Boolean)result[0]))
        {
            throw new AlgorithmRunException("Failure while batch evaluating solutions.\n" + result[1]);
        }
        
        double [] scores = (double[]) result[2];
        
        // store scores
        for (int i = 0; i < scores.length && canEvaluate(); i++)
        {
            pop.get(i).evaluated(scores[i]);
            triggerSolutionEvaluationEvent(pop.get(i));
        }
    }
    
    @Override
    public void validateConfiguration() throws InvalidConfigurationException
    {         
        // validation
        if(!isValidEmail())
        {
            throw new InvalidConfigurationException("Invalid email address: " + email);
        }
    }
    public boolean isValidEmail()
    {
        if(email==null || email.length()<3)
        {
            return false;
        }
        Pattern p = Pattern.compile(".+@.+\\.[a-z]+");
        Matcher m = p.matcher(email);
        return m.matches();
    }
    
    /**
     * Called right before the problem is used in an algorithm run 
     */
    @Override
    public void initialiseBeforeRun()
    	throws InitialisationException
    {    	
        // create client        
        try
        {
            client = new HuygensWSClient();
        }
        catch(Exception e)
        {
            throw new InitialisationException("Unable to create connection.\n"+e.getMessage(), e);
        }
        Object [] result = null;
        try
        {
            result = client.login(email);
        }
        catch(Exception e)
        {
            throw new InitialisationException("Unable to logon with email: "+email+"\n"+e.getMessage(), e);
        }
        finally
        {
            if(result!=null && !((Boolean)result[0]))
            {
                throw new InitialisationException("Failure while logon with email: "+email+"\n" + result[1]);
            }
        }
        try
        {
            result = client.startTraining(series, landscape);
        }
        catch(Exception e)
        {
            throw new InitialisationException("Unable to start training. series="+series+", landscape="+landscape+"\n"+e.getMessage(), e);
        }
        finally
        {
            if(result!=null && !((Boolean)result[0]))
            {
                throw new InitialisationException("Failure while starting training. series="+series+", landscape="+landscape+"\n" + result[1]);
            }
        }
    }
	

	@Override
	public void checkSolutionForSafety(Solution solution)
			throws SolutionEvaluationException
	{
		checkSolutionForSafety(((CFOSolution)solution).getCoordinate());
	}
	
    public void checkSolutionForSafety(double [] v) 
    	throws AlgorithmRunException
	{
	    if(v.length < dimensions)
	    {
	        throw new AlgorithmRunException("Solution coordinate does cont contain the desired number of dimensions " + dimensions);
	    }
	    double [][] minmax = getMinmax();
	    for (int i = 0; i < dimensions; i++)
	    {
	        if(!AlgorithmUtils.inBounds(v[i], minmax[i][0], minmax[i][1]))
	        {
	            throw new AlgorithmRunException("Unable to evaluate, coordinate is out of function bounds (dimension ["+i+"])" +
	                    " val["+v[i]+"] max["+minmax[i][0]+"], val["+v[i]+"], max["+minmax[i][1]+"].");
	        }
	    }
	}
	

	@Override
	public boolean isMinimization()
	{
		return true;
	}
	
	@Override
	public int getBitPrecision()
	{
		return bitPrecision;
	}

	@Override
	public BinaryDecodeMode getDecodeMode()
	{
		return decodeMode;
	}
	
	public void setDecodeMode(BinaryDecodeMode m)
	{
		decodeMode = m;
	}

	@Override
	public int getDimensions()
	{
		return dimensions;
	}

	@Override
	public CFOSolution [] getGlobalOptima()
	{
		return null;
	}

	@Override
	public double[][] getMinmax()
	{
		return new double[][]{{0,1},{0,1}};
	}

	@Override
	public String getName()
	{
		return "Huygens Suite (Training)";
	}

	@Override
	public boolean isToroidal()
	{
		return toroidal;
	}
	
    @Override
    public String getDetails()
    {
        StringBuffer b = new StringBuffer();
        double [][] minmax = getMinmax();
        
        b.append(super.getDetails()+", ");
        b.append("BitPrecisionPerParameter="+bitPrecision+", ");
        b.append("Torroidal="+isToroidal()+", ");
        b.append("Dimensions="+dimensions+", ");
        b.append("Server="+HuygensWSClient.HUYGENS_WS_ADDRESS+", ");
        b.append("ClientVersion="+HuygensWSClient.VERSION+"");
        
        b.append("Bounds=(");
        for (int i = 0; i < minmax.length; i++)
        {
            b.append(i+"[min="+minmax[i][0]+",max="+minmax[i][1]+"]");
            if(i!=minmax.length-1)
            {
                b.append(",");
            }
        }
        b.append("), ");
        b.append("Optima=(Unknown)");
        
        return b.toString();
    }

	public int getSeries()
	{
		return series;
	}

	public void setSeries(int series)
	{
		this.series = series;
	}

	public int getLandscape()
	{
		return landscape;
	}

	public void setLandscape(int landscape)
	{
		this.landscape = landscape;
	}

	public String getEmail()
	{
		return email;
	}

	public void setEmail(String email)
	{
		this.email = email;
	}
	
	@Override
	public boolean isUserConfigurable()
	{
		return true;
	}
}
