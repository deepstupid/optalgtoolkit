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
package com.oat.domains.cfo.algorithms;

import java.util.LinkedList;
import java.util.Random;

import com.oat.Algorithm;
import com.oat.InitialisationException;
import com.oat.InvalidConfigurationException;
import com.oat.Problem;
import com.oat.domains.cfo.CFOProblemInterface;
import com.oat.domains.cfo.CFOSolution;
import com.oat.utils.AlgorithmUtils;
import com.oat.utils.RandomUtils;


/**
 * Type: ParallelHillclimbingAlgorithm<br/>
 * Date: 15/03/2006<br/>
 * <br/>
 * Description: A Comparison of Parallel and Sequential Niching Methods (1995)
 * <br/>
 * @author Jason Brownlee
 * 
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 22/12/2006   JBrownlee   Random moved to method variable rather than instance variable
 *                          Fixed bug after children evaluation where children population
 *                          was not getting trimmed of un-evaluated solutions
 * 
 * </pre>
 */
public class ParallelHillclimbingAlgorithm extends Algorithm
{     
    protected double [] stepSize;
    
    // user parameters
    protected long seed = System.currentTimeMillis();
    protected int popsize = 100;
    protected double initialStepSizeRatio = 0.1;
    

    @Override
    public String getDetails()
    {
        return "Parallel Hillclimbing Algorithm: " +
                "as described in: Samir W. Mahfoud. A Comparison of Parallel and Sequential Niching Methods. Larry Eshelman. Proceedings of the Sixth International Conference on Genetic Algorithms San Francisco, CA, USA: Morgan Kaufmann Publishers Inc.; 1995: 136-143. " +
                "the initialStepSizeRatio parameter is a ratio of the objective function range in each dimension.";
    }
    
    @Override
    protected void internalExecuteAlgorithm(Problem p)
    {
        Random r = new Random(seed);
        
        LinkedList<CFOSolution> pop = new LinkedList<CFOSolution>();        
        // prepare initial population
        while(pop.size() < popsize)
        {
            pop.add(new CFOSolution(RandomUtils.randomPointInRange(r, ((CFOProblemInterface)p).getMinmax())));
        }         
        // evaluate
        p.cost(pop);        
        
        // run algorithm until there are no evaluations left
        int j = 0;
        int changes = 0;
        int direction = 0;
        while(p.canEvaluate())
        {
            triggerIterationCompleteEvent(p,pop);
            LinkedList<CFOSolution> children = generate(pop, direction, j, (CFOProblemInterface)p);
            p.cost(children);
            if(!p.canEvaluate())
            {
                continue;
            }
            LinkedList<CFOSolution> n = new LinkedList<CFOSolution>();
            for (int i = 0; i < children.size(); i++)
            {
                if(p.isBetter(children.get(i), pop.get(i)))
                {
                    // only accept improvements
                    n.add(children.get(i));
                    changes++;
                }
                else
                {
                    n.add(pop.get(i));
                }
            }
            pop = n;            
            
            // positive and negative for this axis
            if(++direction >= 2)
            {
                // both directions have been done for this axis
                direction = 0;
                // check if all axis have been processed
                if(++j >= ((CFOProblemInterface)p).getDimensions())
                {
                    // check for any changes
                    if(changes == 0)
                    {
                        // adjust step sizes - current has reached its usefulness
                        for (int i = 0; i < stepSize.length; i++)
                        {
                            stepSize[i] /= 2.0;
                        }
                    }
                    j = 0;
                    changes = 0;
                }
            }
        }
    }
    
    
    protected LinkedList<CFOSolution> generate(LinkedList<CFOSolution> pop, int dir, int axis, CFOProblemInterface p)
    {
        LinkedList<CFOSolution> n = new LinkedList<CFOSolution>();
        for(CFOSolution s : pop)
        {
            double [] coord = s.getCoordinate();
            double [] nCoord = new double[coord.length];
            System.arraycopy(coord, 0, nCoord, 0, nCoord.length);
            if(dir == 0)
            {
                nCoord[axis] += stepSize[axis];
            }
            else
            {
                nCoord[axis] -= stepSize[axis];
            }
            AlgorithmUtils.fixCoordBounds(nCoord, p.getMinmax(), p.isToroidal());
            CFOSolution ns = new CFOSolution(nCoord);
            n.add(ns);
        }
        
        return n;
    }
    
    
    protected void prepareStepSize(CFOProblemInterface p)
    {
        stepSize = new double[p.getDimensions()];
        double [][] minmax = p.getMinmax();
        for (int i = 0; i < stepSize.length; i++)
        {
            stepSize[i] = (minmax[i][0]-minmax[i][1]) * initialStepSizeRatio;
        }
    }

    @Override
    public String getName()
    {
        return "Parallel Hillclimbing";
    }
        
    @Override
	public void initialiseBeforeRun(Problem p)
		throws InitialisationException
	{
        // prepare the step size
        prepareStepSize((CFOProblemInterface)p);
	}
    
    @Override
    public void validateConfiguration()
        throws InvalidConfigurationException
    {
        // popsize
        if(popsize<=0)
        {
            throw new InvalidConfigurationException("Invalid popsize " + popsize);
        }
        // step size
        if(initialStepSizeRatio>1||initialStepSizeRatio<0)
        {
            throw new InvalidConfigurationException("Invalid initialStepSizeRatio " + initialStepSizeRatio);
        }            

    }



    public long getSeed()
    {
        return seed;
    }


    public void setSeed(long seed)
    {
        this.seed = seed;
    }


    public int getPopsize()
    {
        return popsize;
    }


    public void setPopsize(int popsize)
    {
        this.popsize = popsize;
    }


    public double getInitialStepSizeRatio()
    {
        return initialStepSizeRatio;
    }


    public void setInitialStepSizeRatio(double initialStepSizeRatio)
    {
        this.initialStepSizeRatio = initialStepSizeRatio;
    }

}
