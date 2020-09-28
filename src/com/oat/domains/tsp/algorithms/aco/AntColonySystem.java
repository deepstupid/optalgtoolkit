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
import com.oat.domains.tsp.TSPProblem;
import com.oat.domains.tsp.TSPSolution;
import com.oat.domains.tsp.TSPUtils;
import com.oat.utils.AlgorithmUtils;

/**
 * Type: AntColonySystem<br/>
 * Date: 14/12/2006<br/>
 * <br/>
 * Description: Ant Colony System (ACS)
 * As described in: Marco Dorigo and Thomas Stützle. Ant Colony Optimization. USA: The MIT Press; 2004.
 * 
 * Changes from AS
 * - evaporation after each tour generation
 * - only update/decay pheromone for best tour found so far each iteration
 * - local pheromone update for each new solution created
 * 
 * <br/>
 * @author Jason Brownlee
 * 
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 22/12/2006   JBrownlee   Random moved to method variable rather than instance variable
 * 10/09/2007	JBrownlee	Updated to extend from EpochAlgorithm and implement AutomaticallyConfigurableAlgorithm
 *       
 * </pre>                       
 *  
 */
public class AntColonySystem extends EpochAlgorithm<TSPSolution>
	implements AutomaticallyConfigurableAlgorithm
{
    // non-user parameters
    protected double tau0; // initial pheromone value
    
    // parameters    
    protected long seed;    
    protected double heuristicContribution; // beta
    protected double decayFactor; // rho
    protected int totalAnts;    
    protected double localPheromoneFactor; // sigma
    protected double greedynessFactor; // q0    
    
    // state
	protected Random rand;
	protected double [][] pheromoneMatrix;
	protected double [][] distanceMatrix;
	protected TSPSolution best;

    /**
     * Automatically configures to m=100
     */
    public AntColonySystem()
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
	public static void automaticConfiguration(AntColonySystem algorithm, int numCities)
	{
		algorithm.setSeed(System.currentTimeMillis());
		algorithm.setHeuristicContribution(2.5); // beta, between 2 and 5
		algorithm.setDecayFactor(0.1); //rho=0.1
		algorithm.setTotalAnts(10); // m=10
		algorithm.setLocalPheromoneFactor(0.1); // sigma=0.1
		algorithm.setGreedynessFactor(0.9); //q0=0.9
	}
   
   
    @Override
    public String getDetails()
    {
        return "As described in: Marco Dorigo and Thomas Stützle. Ant Colony Optimization. USA: The MIT Press; 2004.";
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
        // sigma
        if(!AlgorithmUtils.inBounds(localPheromoneFactor, 0, 1))
        {
            throw new InvalidConfigurationException("Invalid sigma " + localPheromoneFactor);
        }
        // greedynessFactor
        if(!AlgorithmUtils.inBounds(greedynessFactor, 0, 1))
        {
            throw new InvalidConfigurationException("Invalid greedynessFactor " + greedynessFactor);
        }
    }    
    
    
    @Override
	protected LinkedList<TSPSolution> internalExecuteEpoch(Problem problem,
			LinkedList<TSPSolution> population)
	{
        LinkedList<TSPSolution> ants = new LinkedList<TSPSolution>(); 
        while(ants.size() < totalAnts)
        {            
            // uses an alpha of 1.0 and greedyness factor
            int [] perm = TSPUtils.probabilisticStepwiseConstruction(distanceMatrix, heuristicContribution, pheromoneMatrix, 1.0, greedynessFactor, rand);
            TSPSolution s = new TSPSolution(perm);
            ants.add(s);
            // local pheromone update
            localPheromoneUpdate(pheromoneMatrix, perm);
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
        // update pheromone for best so far only (decay and update combined)
        globalPheromoneUpdate(pheromoneMatrix, best.getPermutation(), best.getScore()); 		
	}
    

    /**
     * Create and initialise the pheromone matrix
     * @param p
     * @param nnSolution
     * @return
     */
    protected double [][] initialisePheromoneMatrix(TSPProblem p, TSPSolution nnSolution)
    {
        int totalCities = p.getTotalCities();
        double [][] pheromoneMatrix = new double[totalCities][totalCities];
        double v = (1.0 / totalCities * nnSolution.getScore());
        
        // safety
        if(AlgorithmUtils.isInvalidNumber(v))
        {
            throw new AlgorithmRunException("Attempting to initialise pheromone matrix with invalid number: " + v);
        }
        
        for (int i = 0; i < pheromoneMatrix.length; i++)
        {
            Arrays.fill(pheromoneMatrix[i], v);
        }
        
        // needed for local pheromone update in ACS
        tau0 = v;
        
        return pheromoneMatrix;
    }
    
    /**
     * Given a single permutation (solution), update the pheromone matrix
     * Uses ACS global pheromone method
     * @param historyMatrix
     * @param permutation
     * @param tourLength
     */
    protected void globalPheromoneUpdate(double [][] historyMatrix, int [] permutation, double tourLength)
    {        
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
            historyMatrix[x][y] = (1.0-decayFactor)*historyMatrix[x][y] + decayFactor*(1.0/tourLength);
            historyMatrix[y][x] = (1.0-decayFactor)*historyMatrix[y][x] + decayFactor*(1.0/tourLength);
            
            // safety for each change - only need to check one, both the same
            if (AlgorithmUtils.isInvalidNumber(historyMatrix[x][y]))
            {
                throw new RuntimeException("Invalid updated global pheromone value " + historyMatrix[x][y]);
            }
        }
    }    
    
    /**
     * Given a single permutation (solution), update the pheromone matrix
     * Uses ACS local pheromone method
     * @param historyMatrix
     * @param permutation
     */
    protected void localPheromoneUpdate(double [][] historyMatrix, int [] permutation)
    {                
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
            
            historyMatrix[x][y] = (1.0-localPheromoneFactor)*historyMatrix[x][y] + localPheromoneFactor * tau0;
            historyMatrix[y][x] = (1.0-localPheromoneFactor)*historyMatrix[y][x] + localPheromoneFactor * tau0;
            
            // safety for each change - only need to check one, both the same
            if (AlgorithmUtils.isInvalidNumber(historyMatrix[x][y]))
            {
                throw new RuntimeException("Invalid updated local pheromone value " + historyMatrix[x][y]);
            }
        }
    } 

    @Override
    public String getName()
    {
        return "Ant Colony System (ACS)";
    }

    public long getSeed()
    {
        return seed;
    }

    public void setSeed(long seed)
    {
        this.seed = seed;
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

    public double getLocalPheromoneFactor()
    {
        return localPheromoneFactor;
    }

    public void setLocalPheromoneFactor(double localPheromoneFactor)
    {
        this.localPheromoneFactor = localPheromoneFactor;
    }

    public double getGreedynessFactor()
    {
        return greedynessFactor;
    }

    public void setGreedynessFactor(double greedynessFactor)
    {
        this.greedynessFactor = greedynessFactor;
    }
}

