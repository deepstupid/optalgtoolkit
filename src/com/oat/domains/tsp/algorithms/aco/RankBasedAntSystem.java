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
import java.util.Collections;
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
 * Type: RankBasedAntSystem<br/>
 * Date: 14/12/2006<br/>
 * <br/>
 * Description: Rank-Based Ant Systems
 * Marco Dorigo and Thomas Stützle. Ant Colony Optimization. USA: The MIT Press; 2004.
 * 
 * Uses a ranking method for pheromone updates
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
public class RankBasedAntSystem extends EpochAlgorithm<TSPSolution>
	implements AutomaticallyConfigurableAlgorithm
{        
    // algorithm parameters
    protected long seed;    
    protected double historyContribution; // alpha
    protected double heuristicContribution; // beta
    protected double decayFactor; // rho
    protected int totalAnts; // m
    protected int rankSize; // w
 
    // state
	protected Random rand;
	protected double [][] pheromoneMatrix;
	protected double [][] distanceMatrix;
	protected TSPSolution best;

    /**
     * Automatically configures to m=100
     */
    public RankBasedAntSystem()
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
	public static void automaticConfiguration(RankBasedAntSystem algorithm, int numCities)
	{
		algorithm.setSeed(System.currentTimeMillis());
		algorithm.setHistoryContribution(1.0); // alpha
		algorithm.setHeuristicContribution(2.5); // beta, between 2 and 5
		algorithm.setDecayFactor(0.1); //rho
		algorithm.setTotalAnts(numCities); // m
		algorithm.setRankSize(6); // w=6		
	}
    
    
    @Override
    public String getDetails()
    {
        return  "As described in: Marco Dorigo and Thomas Stützle. Ant Colony Optimization. USA: The MIT Press; 2004.";
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
        // rank size
        if(rankSize>totalAnts)
        {
            throw new InvalidConfigurationException("Invalid rankSize " + rankSize);
        }
        else if(rankSize<=0)
        {
            throw new InvalidConfigurationException("Invalid rankSize " + rankSize);
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
        // update pheromone
        updateAndDecayPheromone(newPopulation, pheromoneMatrix, best);
	}

	/**
     * Perform the decay followed by the pheromone update operations
     * @param ants
     * @param pheromoneMatrix
     */
    protected void updateAndDecayPheromone(LinkedList<TSPSolution> ants, double [][] pheromoneMatrix, TSPSolution best)
    {
        // decay
        decayPheromone(pheromoneMatrix);
        // sory the population by quality, ascending (best-worst)
        Collections.sort(ants);
        // perform - rank based update        
        for(int i = 0; i<rankSize-1; i++)
        {
            double factor = (rankSize-i-1);
            updatePheromone(pheromoneMatrix, ants.get(i).getPermutation(), factor, ants.get(i).getScore());
        }  
        // update for the best ever ant (the zero'th rank)
        updatePheromone(pheromoneMatrix, best.getPermutation(), rankSize, best.getScore());
    }  
    /**
     * Given a single permutation (solution), update the pheromone matrix
     * @param historyMatrix
     * @param permutation
     * @param updateFactor - normally 1, but can be different, say for elitism
     * @param tourLength
     */
    protected void updatePheromone(double [][] historyMatrix, int [] permutation, double updateFactor, double tourLength)
    {
        double d = updateFactor * (1.0/tourLength); 
        
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
     * Create and initialise the pheromone matrix
     * @param p
     * @param nnSolution
     * @return
     */
    protected double [][] initialisePheromoneMatrix(TSPProblem p, Solution nnSolution)
    {
        int totalCities = p.getTotalCities();
        double [][] pheromoneMatrix = new double[totalCities][totalCities];
        // TODO - the specific tau-zero in the book is not clear, where does 'r' come from?
        // just use AS approach
        double v = (totalAnts / nnSolution.getScore());
        
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
        return "Rank-Based Ant System (ASrank)";
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

    public int getRankSize()
    {
        return rankSize;
    }

    public void setRankSize(int rankSize)
    {
        this.rankSize = rankSize;
    }

    
    
}

