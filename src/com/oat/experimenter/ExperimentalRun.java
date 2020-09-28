/*
Optimization Algorithm Toolkit (OAT)
http://sourceforge.net/projects/optalgtoolkit
Copyright (C) 2006-2007  Jason Brownlee

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
package com.oat.experimenter;

import java.util.Date;

import com.oat.Algorithm;
import com.oat.InitialisationException;
import com.oat.InvalidConfigurationException;
import com.oat.Problem;
import com.oat.StopCondition;
import com.oat.utils.AlgorithmUtils;
import com.oat.utils.BeanUtils;

/**
 * Date: 01/08/2007<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 *
 * 
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 
 * </pre>
 *
 */
public class ExperimentalRun 
	implements Comparable<ExperimentalRun>
{
	/**
	 * The distinct name of this run, R# format
	 */
    protected String id;
    /**
     * The algorithm and configuration to execute in this run
     */
    protected Algorithm algorithm;
    /**
     * The problem and configuration to execute in this run
     */
    protected Problem problem;
    /**
     * The number of repeats for the run
     */
    protected int repeats;
    
    
    /**
     * Whether or not the run has been completed
     */
    protected boolean completed;
    /**
     * The date/time the run was completed (if completed)
     */
    protected Date completionDate;
    
    
    /**
     * 
     */
    public ExperimentalRun()    
    {}
    
   
    
    public String getId()
    {
        return id;
    }
    public void setId(String id)
    {
        this.id = id;
    }
    public Algorithm getAlgorithm()
    {
        return algorithm;
    }
    public void setAlgorithm(Algorithm algorithm)
    {
        this.algorithm = algorithm;
    }
    public Problem getProblem()
    {
        return problem;
    }
    public void setProblem(Problem problem)
    {
        this.problem = problem;
    }
    
    
    
    /**
     * Converts this schedule entry to a String line in the following format
     * 
     * id,algorithm,problem,repeats
     * 
     * id,class=value|name=value|name=value,class=value|name=value|name=value,class=value|name=value|name=value
     * 
     * @return
     */    
    public String toStringEntry()
    {
        StringBuffer b = new StringBuffer();
        // id
        b.append(id);
        b.append(",");
        // problem
        b.append(BeanUtils.beanToTokenisedString(problem));      
        b.append(",");
        // algorithm
        b.append(BeanUtils.beanToTokenisedString(algorithm));   
        b.append(",");
        // repeats
        b.append(repeats);   
        
        return b.toString();
    }
    
    /**
     * Populates this object from a String entry in the following format:
     * 
     * @param entry
     */
    public void fromStringEntry(String entry)
    	throws InitialisationException, InvalidConfigurationException
    {
        String [] parts = entry.split(",");
        if(parts.length!=4)
        {
            throw new InitialisationException("Invalid schedule string, expected 4 parts: " + parts.length);
        }
        // id
        id = parts[0].trim();        
        // problem
        problem = (Problem) BeanUtils.beanFromString(parts[1]);               
        // algorithm
        algorithm = (Algorithm) BeanUtils.beanFromString(parts[2]);
        // repeats
        repeats = Integer.parseInt(parts[3]);
    }
    
    
    public void completed(Date aCompletionDate)
    {
    	completed = (aCompletionDate != null);
    	completionDate = aCompletionDate;
    }
    
    
    @Override
    public String toString()
    {
        return id;
    }

    public boolean isCompleted()
    {
        return completed;
    }

    

    public Date getCompletionDate()
    {
        return completionDate;
    }

   

    public int getRepeats()
    {
        return repeats;
    }

    public void setRepeats(int repeats)
    {
        this.repeats = repeats;
    }

    @Override
    public int compareTo(ExperimentalRun o)
    {
        Integer thisId = Integer.parseInt(id.substring(1));
        Integer otherId = Integer.parseInt(o.id.substring(1));
        
        return thisId.compareTo(otherId);
    }

	


	
	public void validateConfiguration(StopCondition stopCondition) 
		throws InvalidConfigurationException
	{
    	// run id
    	if(id == null || id.length()<1)
    	{
    		throw new InvalidConfigurationException("Invalid run id " + id);
    	}
    	// problem
    	if(problem == null)
    	{
    		throw new InvalidConfigurationException("Problem not specified");
    	}
    	
    	problem.addStopCondition(stopCondition);
    	problem.validateConfiguration();
    	problem.removeStopCondition(stopCondition);
    	
    	// algorithm
    	if(algorithm == null)
    	{
    		throw new InvalidConfigurationException("Algorithm not specified");
    	}
        
    	algorithm.validateConfiguration();
        
        // repeats
        if(!AlgorithmUtils.inBounds(repeats, 30, 1000)) 
        {
        	throw new InvalidConfigurationException("Invalid total repeats, expect between 30 and 1000 : " + repeats);
        }		
	}
}
