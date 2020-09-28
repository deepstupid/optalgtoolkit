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
package com.oat.domains.tsp.algorithms.aco;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Random;

import com.oat.AlgorithmRunException;
import com.oat.AutomaticallyConfigurableAlgorithm;
import com.oat.EpochAlgorithm;
import com.oat.InvalidConfigurationException;
import com.oat.Problem;
import com.oat.Solution;
import com.oat.domains.tsp.TSPProblem;
import com.oat.domains.tsp.TSPSolution;
import com.oat.domains.tsp.TSPUtils;
import com.oat.utils.AlgorithmUtils;

/**
 * Date: 14/12/2006<br/>
 * <br/>
 * Description: Max-Min Ant Systems (MMAS)
 * As described in: Marco Dorigo and Thomas Stützle. Ant Colony Optimization. USA: The MIT Press; 2004.
 * 
 * Main differences from Ant Systems:
 * - bounded pheromone values
 * - restart after convergence (not implemented, just do it in code)
 * - update for best-ever and iteration best (supports both with updateSelectionFactor)
 *    (randomly selects either, > 0.5 favors bestEver, < 0.5 favors iterationBest, 0,1 are the limits obviously)
 * 
 * Does not appear to perform too well!
 * 
 * <br/>
 * @author Jason Brownlee
 * 
 * 
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 22/12/2006   Jbrownlee   Random moved to method variable rather than instance variable
 * 10/09/2007	JBrownlee	Updated to extend from EpochAlgorithm and implement AutomaticallyConfigurableAlgorithm
 *    
 * </pre>                        
 *
 */
public class MaxMinAntSystem extends EpochAlgorithm<TSPSolution>
	implements AutomaticallyConfigurableAlgorithm
{    
    // automatic parameters
    protected double tmax;
    protected double tmin;    
    
    // algorithm parameters
    protected long seed;
    protected double historyContribution; // alpha
    protected double heuristicContribution; // beta
    protected double decayFactor; // rho
    protected int totalAnts;  //m=n
    protected double updateSelectionFactor;     
    
    // state
	protected Random rand;
	protected double [][] pheromoneMatrix;
	protected double [][] distanceMatrix;
	protected TSPSolution best;

    /**
     * Automatically configures to m=100
     */
    public MaxMinAntSystem()
    {
    	automaticConfiguration(this, 100);
    }
    
    
	@Override
	public void automaticallyConfigure(Problem problem)
	{
		TSPProblem p = (TSPProblem) problem;
		automaticConfiguration(this, p.getTotalCities());		
	}
	
	/**
	 * Configuration according to 'Ant Colony Optimization', page 71 
	 * @param algorithm
	 * @param numCities
	 */
	public static void automaticConfiguration(MaxMinAntSystem algorithm, int numCities)
	{
		algorithm.setSeed(System.currentTimeMillis());
		algorithm.setHistoryContribution(1.0); // alpha
		algorithm.setHeuristicContribution(2.5); // beta, between 2 and 5
		algorithm.setDecayFactor(0.02); //rho
		algorithm.setTotalAnts(numCities); // m
		algorithm.setUpdateSelectionFactor(0.7); // higher		
	}
    
    
    
    
    

    @Override
    public String getDetails()
    {
        return 
        "As described in: Marco Dorigo and Thomas Stützle. Ant Colony Optimization. USA: The MIT Press; 2004. " +
        "Supports an update selection factor which determines the average ratio of updates shared between best-found so-far and best-of-iteration tours. >0.5 bias best-found so-far, < bias towards best-of-iteration. " +
        "Does not support algorithm restart after convergence, it is suggested that you do this manually in the code.";
    }
    
    
    
	@Override
    public void validateConfiguration()
    	throws InvalidConfigurationException
    {
        // total ants
        if(totalAnts<=0)
        {
            throw new InvalidConfigurationException("Invalid totalAnts " + totalAnts);
        }
        // update selection factor
        if(updateSelectionFactor<0||updateSelectionFactor>1)
        {
            throw new InvalidConfigurationException("Invalid updateSelectionFactor " + updateSelectionFactor);
        }
    }
	
    @Override
	protected LinkedList<TSPSolution> internalExecuteEpoch(Problem problem,
			LinkedList<TSPSolution> population)
	{
        LinkedList<TSPSolution> ants = new LinkedList<TSPSolution>(); 
        while(ants.size() < totalAnts)
        {            
            int [] perm = TSPUtils.probabilisticStepwiseConstruction(distanceMatrix, heuristicContribution, pheromoneMatrix, historyContribution, rand);
            TSPSolution s = new TSPSolution(perm);
            ants.add(s);
        }
        return ants;
	}


	@Override
	protected LinkedList<TSPSolution> internalInitialiseBeforeRun(
			Problem problem)
	{        
        rand = new Random(seed);
        TSPProblem p = (TSPProblem) problem;        
        best = TSPUtils.generateNearestNeighbourSolution(p, rand);
        p.cost(best);
        // prepare the pheromone matrix
        pheromoneMatrix = initialisePheromoneMatrix(p, best);
        distanceMatrix = p.getDistanceMatrix();
        return null;
	}


	@Override
	protected void internalPostEvaluation(Problem problem,
			LinkedList<TSPSolution> oldPopulation,
			LinkedList<TSPSolution> newPopulation)
	{
		TSPProblem p = (TSPProblem) problem;   
        TSPSolution b = AlgorithmUtils.getBest(newPopulation, p);
        if(p.isBetter(b, best))
        {
            best = b;                    
        }
        
        triggerIterationCompleteEvent(p,newPopulation);                
        // update tmin and tmax each iteration
        updatePheromoneBounds(best.getScore(), p);                                       
        // update and decay pheromone
        updateAndDecayPheromone(newPopulation, pheromoneMatrix, p, rand, best);
	}    
    
    protected void updatePheromoneBounds(double bestScore, TSPProblem p)
    {
        // prepare tmax
        tmax = (1.0 / (decayFactor*bestScore));
        // prepare tmin
        tmin = tmax * (Math.pow(0.05, 1.0/p.getTotalCities())) / ((((p.getTotalCities()-1.0) / 2.0)-1) * (Math.pow(0.05, 1.0/p.getTotalCities())));
        
        // safety
        if(tmin>=tmax)
        {
            throw new AlgorithmRunException("Invalid tmin value: tmax["+tmax+"], tmin["+tmin+"]");
        }
    }
    
    /**
     * Perform the decay followed by the pheromone update operations
     * @param ants
     * @param pheromoneMatrix
     */
    protected void updateAndDecayPheromone(LinkedList<TSPSolution> ants, double [][] pheromoneMatrix, Problem p, Random r, TSPSolution best)
    {
        // decay
        decayPheromone(pheromoneMatrix);        
        // select the solution to use for updates
        TSPSolution b = (r.nextDouble() > 0.5) ? best : AlgorithmUtils.getBest(ants, p);
        // perform the update
        updatePheromone(pheromoneMatrix, b.getPermutation(), b.getScore());        
        // bound check all pheromone
        for (int i = 0; i < pheromoneMatrix.length; i++)
        {
            for (int j = 0; j < pheromoneMatrix[i].length; j++)
            {
                if(pheromoneMatrix[i][j] < tmin)
                {
                    pheromoneMatrix[i][j] = tmin;
                }
                if(pheromoneMatrix[i][j] > tmax)
                {
                    pheromoneMatrix[i][j] = tmax;
                }
            }
        }
    }    
    
    /**
     * Decay the pheromone matrix
     * @param pheromoneMatrix
     */
    protected void decayPheromone(double [][] pheromoneMatrix)
    {
        for (int i = 0; i < pheromoneMatrix.length; i++)
        {
            for (int j = 0; j < pheromoneMatrix[i].length; j++)
            {
                double n = (1.0 - decayFactor) * pheromoneMatrix[i][j];
                // safety
                if(AlgorithmUtils.isInvalidNumber(n))
                {
                    throw new AlgorithmRunException("Attempting to decay pheromone matrix to invalid state ["+n+"], old["+pheromoneMatrix[i][j]+"]");
                }
                pheromoneMatrix[i][j] = n;
            }
        } 
    }
    
    /**
     * Given a single permutation (solution), update the pheromone matrix
     * @param historyMatrix
     * @param permutation
     * @param tourLength
     */
    protected void updatePheromone(double [][] historyMatrix, int [] permutation, double tourLength)
    {
        double d = (1.0/tourLength); 
        
        // safety
        if(AlgorithmUtils.isInvalidNumber(d))
        {
            throw new RuntimeException("Invalid update delta " + d);
        }
        
        for (int i = 0; i < permutation.length; i++)
        {
            int x = permutation[i];
            int y = -1;
            if(i == permutation.length-1)
            {
                y = permutation[0]; // wrap
            }
            else
            {
                y = permutation[i + 1];
            }
            historyMatrix[x][y] += d;
            historyMatrix[y][x] += d;
        }
    }    
    
    /**
     * Create and initialise the pheromone matrix
     * @param p
     * @param nnSolution
     * @return
     */
    protected double [][] initialisePheromoneMatrix(TSPProblem p, Solution nnSolution)
    {
        int totalCities = p.getTotalCities();
        double [][] pheromoneMatrix = new double[totalCities][totalCities];
        // specific tau-zero specification
        double v = (1.0 / (decayFactor*nnSolution.getScore()));
        
        // safety
        if(AlgorithmUtils.isInvalidNumber(v))
        {
            throw new AlgorithmRunException("Attempting to initialise pheromone matrix with invalid number: " + v);
        }
        
        for (int i = 0; i < pheromoneMatrix.length; i++)
        {
            Arrays.fill(pheromoneMatrix[i], v);
        }
        
        return pheromoneMatrix;
    }    

    @Override
    public String getName()
    {
        return "Max-Min Ant System (MMAS)";
    }


    public long getSeed()
    {
        return seed;
    }


    public void setSeed(long seed)
    {
        this.seed = seed;
    }


    public double getHistoryContribution()
    {
        return historyContribution;
    }


    public void setHistoryContribution(double historyContribution)
    {
        this.historyContribution = historyContribution;
    }


    public double getHeuristicContribution()
    {
        return heuristicContribution;
    }


    public void setHeuristicContribution(double heuristicContribution)
    {
        this.heuristicContribution = heuristicContribution;
    }


    public double getDecayFactor()
    {
        return decayFactor;
    }


    public void setDecayFactor(double decayFactor)
    {
        this.decayFactor = decayFactor;
    }


    public int getTotalAnts()
    {
        return totalAnts;
    }


    public void setTotalAnts(int totalAnts)
    {
        this.totalAnts = totalAnts;
    }


    public double getUpdateSelectionFactor()
    {
        return updateSelectionFactor;
    }


    public void setUpdateSelectionFactor(double updateSelectionFactor)
    {
        this.updateSelectionFactor = updateSelectionFactor;
    }
}

