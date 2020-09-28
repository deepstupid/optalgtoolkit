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
package com.oat;


/**
 * Date: 17/11/2006<br/>
 * <br/>
 * Description: Generic ancestor solution to a problem instance
 * Solutions are immutable, once evaluated, they cannot be re-assessed (by default). 
 * <br/>
 * @author Jason Brownlee
 * 
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 *                          
 * </pre>
 */
public abstract class Solution implements Comparable<Solution>
{
    /**
     * The score or cost associated with this solution
     */
    private double score = Double.NaN;
    /**
     * Whether or not this solution has been evaluated and assigned a score
     */
    private boolean isEvaluated = false;
    /**
     * An assigned scoring normalized to a collection of other scorings,
     * such as against a population of solutions
     */
    private double normalizedRelativeScore = Double.NaN;   
    /**
     * Whether or not this solution has been assigned a normalized relative score
     */
    private boolean hasNormalizedRelativeScore = false;
    
    
    /**
     * Called when this solution is assigned a score.
     * Solution may only be evaluated once, followup calls result in an exception being called.
     * @param aCost
     * @throws SolutionEvaluationException
     */
    public void evaluated(double aCost)
    	throws SolutionEvaluationException
    {
        if(isEvaluated())
        {
            throw new SolutionEvaluationException("Solution is already evaluated, unable to re-evaluate because solutions are immutable!");
        }
        score = aCost;
        isEvaluated = true;
    }    
    

    @Override
    public int compareTo(Solution o)
    {
        if(score < o.score)
        {
            return -1;
        }
        else if(score > o.score)
        {
            return +1;
        }
        return 0; // same
    }
    
    /**
     * Returns the score of the solution
     * @return - evaluated score
     * @throws SolutionEvaluationException - if unevaluated
     */
    public double getScore()
    	throws SolutionEvaluationException
    {
        if(!isEvaluated())
        {
            throw new SolutionEvaluationException("Unable to access solution scoring, solution is unevaluated.");
        }
        
        return score;
    } 
    
    /**
     * Whether or not the solution is evaluated
     * @return - true if the solution has been evaluated and assigned a score.
     */
    public boolean isEvaluated()
    {
        return isEvaluated;
    }
    
    @Override
    public String toString()
    {
        return "score["+score+"]";
    }

    /**
     * Returns the normalized relative score
     * @return - normalized relative score
     * @throws SolutionEvaluationException
     */
    public double getNormalizedRelativeScore()
    	throws SolutionEvaluationException
    {
        if(!hasNormalizedRelativeScore())
        {
            throw new SolutionEvaluationException("Unable to access normalized relative solution scoring, solution is unevaluated.");
        }
        
        return normalizedRelativeScore;
    }
    
    /**
     * Assigns the normalized relative score if the solution is evaluated. may be set multiple times.
     * @param n
     * @throws SolutionEvaluationException
     */
    public void setNormalizedRelativeScore(double n)
    	throws SolutionEvaluationException
    {
        if(!isEvaluated())
        {
            throw new SolutionEvaluationException("Unable to assign normalized relative solution scoring, solution is unevaluated.");
        }
        
        hasNormalizedRelativeScore = true;
        this.normalizedRelativeScore = n;
    }
    
    /**
     * Whether or not this solution has been assigned a normalized relative score.
     * @return - true if normalized relative score is assigned
     */
    public boolean hasNormalizedRelativeScore()
    {
        return hasNormalizedRelativeScore;
    }
    
    @Override
    public abstract boolean equals(Object o);
}
