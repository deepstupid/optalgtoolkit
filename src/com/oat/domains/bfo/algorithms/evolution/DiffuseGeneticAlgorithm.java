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
package com.oat.domains.bfo.algorithms.evolution;

import java.util.LinkedList;
import java.util.Random;

import com.oat.Algorithm;
import com.oat.InvalidConfigurationException;
import com.oat.Problem;
import com.oat.domains.bfo.BFOProblemInterface;
import com.oat.domains.bfo.BFOSolution;
import com.oat.domains.bfo.BFOUtils;
import com.oat.utils.EvolutionUtils;


/**
 * Type: DiffuseGeneticAlgorithm<br/>
 * Date: 25/03/2006<br/>
 * <br/>
 * Description: A square population structure, where breeding occurs in local neighbourhood
 * The structure is not a torrid (could be made so though - perhaps a config)
 * 
 * Changes
 * - removed elitism (does not make sense in this approach)
 * - replace lattice each iteration to prevent pop-overlap
 * 
 * <br/>
 * @author Jason Brownlee
 * 
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 22/12/2006   JBrownlee   Random moved to method variable rather than instance variable
 * 20/08/2007	JBrownlee	Moved to binary function optimization domain
 * </pre>
 */
public class DiffuseGeneticAlgorithm extends Algorithm
{
    public final static int TOTAL_NEIGHBOURS = 4;
    
    protected long seed = System.currentTimeMillis();
    protected double crossover = 0.95;
    protected double mutation = 0.005;
    protected int popsize = 100;
    protected int boutSize = 2;
    
    
    @Override
    public String getDetails()
    {
        return 
        "Diffuse (Cellular) Genetic Algorithm. " +
        "As described in Chrisila C. Pettey. Diffusion (cellular) models. Thomas Back; David B Fogel, and Zbigniew Michalwicz. Evolutionary Computation 2 - Advanced Algorithms and Operations. UK: Institute of Physics (IOP) Publishing Ltd.; 2000." +
        "2D lattice where the population is a square (square root of popsize), " +
        "Neighbourhood is taken as N, S, E, W directions on the lattice without toroidal wrapping, " +
        "Cell member is first parent, second parent is taken as a tournament selected neighbour.";
    }
    
    @Override
    protected void internalExecuteAlgorithm(Problem p)
    {
        Random r = new Random(seed);           
        // prepare initial population
        LinkedList<BFOSolution> pop = BFOUtils.getRandomPopulationBinary(r, (BFOProblemInterface)p, popsize);    
        // evaluate
        p.cost(pop);
        
        // build a lattice
        int square = (int) Math.sqrt(popsize);
        BFOSolution [][] lattice = new BFOSolution[square][square];
        int offset = 0;
        for (int i = 0; i < lattice.length; i++)
        {
            for (int j = 0; j < lattice[i].length; j++)
            {
                lattice[i][j] = pop.get(offset++);
            }            
        }
        
        // run algorithm until there are no evaluations left
        while(p.canEvaluate())
        {
            // reproduce
            lattice = reproduce(lattice, p, r);
            // put the lattice in a list so we can do things to it
            pop.clear();
            for (int i = 0; i < lattice.length; i++)
            {
                for (int j = 0; j < lattice[i].length; j++)
                {
                    pop.add(lattice[i][j]);
                }
            }
            
            // evaluate
            p.cost(pop);         
            triggerIterationCompleteEvent(p,pop);
        }
    }    
    
    protected LinkedList<BFOSolution> getNeighbours(BFOSolution [][] lattice, int i, int j)
    {
        // get neighbours (four of them)
        LinkedList<BFOSolution> neighbours = new LinkedList<BFOSolution>();
        if(i > 0) // above
        {
            neighbours.add(lattice[i-1][j]);
        }
        if(i < lattice.length-1) // below
        {
            neighbours.add(lattice[i+1][j]);
        }
        if(j > 0) // left
        {
            neighbours.add(lattice[i][j-1]);
        }
        if(j < lattice[i].length-1) // right
        {
            neighbours.add(lattice[i][j+1]);
        }
        
        return neighbours;
    }

    
    public BFOSolution [][] reproduce(BFOSolution [][] lattice, Problem p, Random r)
    {
    	BFOSolution [][] newLattice = new BFOSolution[lattice.length][lattice[0].length]; 
        
        for (int i = 0; i < lattice.length; i++)
        {
            for (int j = 0; j < lattice[i].length; j++)
            {
            	BFOSolution self = lattice[i][j];
                // get neighbours
                LinkedList<BFOSolution> neighbours = getNeighbours(lattice, i, j);                
                // select other parent
                BFOSolution other = EvolutionUtils.tournamentSelection(neighbours, 1, p, r, boutSize).getFirst();                
                // crossover
                boolean [][] b = EvolutionUtils.onePointBinaryCrossover(self.getBitString(), other.getBitString(), r, crossover);                
                // selection
                boolean [] bitstring = (r.nextBoolean() ? b[0] : b[1]); // randomly select one   
                // mutation
                EvolutionUtils.binaryMutate(bitstring, r, mutation);   
                // creation / replacement
                newLattice[i][j] = new BFOSolution(bitstring);
            }
        }
        return newLattice;
    }
    

    @Override
    public String getName()
    {
        return "Diffuse Genetic Algorithm (Cellular)";
    }

    
    @Override
    public void validateConfiguration() throws InvalidConfigurationException
    {
        // crossover
        if(crossover>1||crossover<0)
        {
            throw new InvalidConfigurationException("Invalid crossover " + crossover);
        }
        // mutation
        if(mutation>1||mutation<0)
        {
            throw new InvalidConfigurationException("Invalid mutation " + mutation);
        }
        // popsize - must be a square
        int a = (int) Math.round(Math.sqrt(popsize));
        popsize = a*a; // must be a square
        if(popsize<TOTAL_NEIGHBOURS)
        {
            throw new InvalidConfigurationException("Invalid popsize (root is taken then squared) " + popsize);
        }
        // bout size
        if(boutSize>TOTAL_NEIGHBOURS||boutSize<0)
        {
            throw new InvalidConfigurationException("Invalid boutSize " + boutSize);
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

    public double getCrossover()
    {
        return crossover;
    }

    public void setCrossover(double crossover)
    {
        this.crossover = crossover;
    }

    public double getMutation()
    {
        return mutation;
    }

    public void setMutation(double mutation)
    {
        this.mutation = mutation;
    }

    public int getPopsize()
    {
        return popsize;
    }

    public void setPopsize(int popsize)
    {
        this.popsize = popsize;
    }

    public int getBoutSize()
    {
        return boutSize;
    }

    public void setBoutSize(int boutSize)
    {
        this.boutSize = boutSize;
    }
}

