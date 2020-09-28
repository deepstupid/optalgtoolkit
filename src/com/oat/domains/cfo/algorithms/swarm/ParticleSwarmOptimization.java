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
package com.oat.domains.cfo.algorithms.swarm;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.Random;

import com.oat.Algorithm;
import com.oat.InvalidConfigurationException;
import com.oat.Problem;
import com.oat.domains.cfo.CFOProblemInterface;
import com.oat.domains.cfo.CFOSolution;
import com.oat.domains.cfo.CFOUtils;
import com.oat.utils.AlgorithmUtils;
import com.oat.utils.ArrayUtils;
import com.oat.utils.RandomUtils;



/**
 * Type: ParticleSwarmOptimization<br/>
 * Date: 13/03/2006<br/>
 * <br/>
 * Description: PSO Algorithm
 * <br/>
 * @author Jason Brownlee
 * 
 * 
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 22/12/2006   JBrownlee   Random moved to method variable rather than instance variable
 *                          Put a hack in to prevent parent, child comparisons when evaluations have run out
 * 09/01/2007   JBrownlee   Cleaned up implementation
 * </pre>
 */
public class ParticleSwarmOptimization extends Algorithm
{       
    protected DistanceComparator comparator;
    
    protected long seed = System.currentTimeMillis();
    protected int popsize = 100;
    protected double vMax = 1.0;
    protected double c1 = 2.0;
    protected double c2 = 2.0;
    protected double momentum = 0.5;
    protected int neighbourhoodSize = 20;
    
    
    public ParticleSwarmOptimization()
    {
        comparator = new DistanceComparator();
    }    
    
    @Override
    public String getDetails()
    {
        return "Particle Swarm Optimization (PSO) (g-best & l-best): " +
                "as described in the PSO Tutorial: http://www.swarmintelligence.org/tutorials.php, " +
                "added momentum that specifies the ratio of previous velocity to use, and the inverse ratio for the amount of new velocity to contribute to the new velocity, " +
                "the vMax parameter is a ratio of the objective function range in each dimension, " +
                "set neighbourhood size to popsize to get g-best behaviour."; 
    }    
    
    protected class PSOSolution extends CFOSolution
    {
        private final double [] velocity;
        private final double [] pbestcoord;
        private double pbestScore;
        
        /**
         * Temporary variable used to define particle neighbourhoods 
         */
        protected double distance;
        
        /**
         * Create a duplicate particle, suitable for updating
         * @param parent
         */
        public PSOSolution(PSOSolution parent)
        {
            super(ArrayUtils.copyArray(parent.getCoordinate()));
            velocity = ArrayUtils.copyArray(parent.velocity);
            pbestcoord = ArrayUtils.copyArray(parent.pbestcoord);
            pbestScore = parent.pbestScore;
        }
        
        /**
         * Create a new virgin particle in the problem space
         * Must be followed up with a call to prepareVirginParticle()
         * @param aCoord
         */
        public PSOSolution(double [] aCoord)
        {
            // create random coordinate
            super(aCoord);
            velocity = new double[coordinate.length];
            pbestcoord = new double[coordinate.length];
            pbestScore = Double.NaN;
        }
        
        /**
         * only needed for initial random pop
         * @param p
         */
        protected void prepareVirginParticle(CFOProblemInterface p, Random r)
        { 
            double [][] minmax = p.getMinmax();
            for (int i = 0; i < velocity.length; i++)
            {
                // randomised initial velocity (until replaced)
                double v = (minmax[i][1] - minmax[i][0]) * vMax;
                velocity[i] = (r.nextDouble() * (v*0.5));
                velocity[i] *= (r.nextBoolean() ? -1 : 1);
            }
            // assume evaluated
            pbestScore = getScore();
            System.arraycopy(coordinate, 0, pbestcoord, 0, coordinate.length);
        }
    }
    
    
    

    @Override
    protected void internalExecuteAlgorithm(Problem p)
    {
        Random r = new Random(seed);
        
        LinkedList<PSOSolution> pop = new LinkedList<PSOSolution>();        
        // prepare initial population
        while(pop.size() < popsize)
        {
            pop.add(new PSOSolution(RandomUtils.randomPointInRange(r, ((CFOProblemInterface)p).getMinmax())));            
        }        
        // evaluate
        p.cost(pop);        
        // set best positions and initial velocities
        if(p.canEvaluate())
        {
            for(PSOSolution ss : pop)
            {
                ss.prepareVirginParticle((CFOProblemInterface)p, r);
            }
        }
        
        // run algorithm until there are no evaluations left
        PSOSolution best = AlgorithmUtils.getBest(pop, p);
        while(p.canEvaluate())
        {
            triggerIterationCompleteEvent(p,pop);
            // create new particle positions
            pop = createNewParticlePositions(pop, p, best, r);
            // evaluate new positions
            p.cost(pop);    
            if(p.canEvaluate())
            {
                // update personal best positions
                updatePersonalBestPositions(pop, p);
                // locate new global best particle position
                PSOSolution c = AlgorithmUtils.getBest(pop, p);
                best = (p.isBetter(c,best)) ? c : best; 
            }
        }
    }
    
    /**
     * Check each particles position and see if it is a better personal
     * best particle position, if so, remember it as such
     * 
     * @param pop
     * @param p
     */
    protected void updatePersonalBestPositions(LinkedList<PSOSolution> pop, Problem p)
    {
        for (int i = 0; i < pop.size(); i++)
        {
            PSOSolution particle = pop.get(i);
            // see if the position is the best position ever visited by this particle
            if (p.isBetter(particle.getScore(), particle.pbestScore))
            {
                // take new position as personal best
                particle.pbestScore = particle.getScore();
                System.arraycopy(particle.getCoordinate(), 0, particle.pbestcoord, 0, particle.pbestcoord.length);
            }
        }
    }
    
 
    /**
     * Returns a collection of neighbourhoodSize closest particles 
     * using euclidean distance. Distance is calculated and the provided 
     * population is sorted according to the distance. The specified particle
     * is never a member of its own neighbourhood. 
     * 
     * @param pop
     * @param particle
     * @return
     */
    protected LinkedList<PSOSolution> getNeighbours(LinkedList<PSOSolution> pop, PSOSolution particle)
    {
        LinkedList<PSOSolution> neighbours = new LinkedList<PSOSolution>();        
        
        // calculate distances
        for(PSOSolution s : pop)
        {
            if(s != particle)
            {
                s.distance = CFOUtils.euclideanDistance(s, particle);                
            }
        }
        // sort by distance asc
        Collections.sort(pop, comparator);
        // get the best n
        int i = 0;
        do
        {
            neighbours.add(pop.get(i++));
        }
        while(neighbours.size() < neighbourhoodSize);
        
        return neighbours;
    }
    
    protected class DistanceComparator implements Comparator<PSOSolution>
    {
        /**
         * Compares its two arguments for order.  Returns a negative integer,
         * zero, or a positive integer as the first argument is less than, equal
         * to, or greater than the second.<p>
         */
        public int compare(PSOSolution p1, PSOSolution p2)
        {            
            if(p1.distance < p2.distance)
            {
                return -1;
            }
            else if(p1.distance > p2.distance)
            {
                return +1;
            }
            return 0; // same
        }
        
    }
    
    
    /**
     * Provides support for global-best and local-best depending on
     * the setting of the neighbourhood size parameter.
     * 
     * @param pop
     * @param best
     * @param self
     * @param p
     * @return
     */
    protected double [] getBestCoord(
            LinkedList<PSOSolution> pop,
            PSOSolution best, 
            PSOSolution self, 
            Problem p)
    {
        double [] bestPos = null;
        
        // check for gbest
        if(neighbourhoodSize == popsize)
        {
            bestPos = best.getCoordinate();
        }
        // best
        else
        {
            // get neighbours
            LinkedList<PSOSolution> neighbours = getNeighbours(pop, self);
            // locate the best neighbourhood pbestpos
            // has to be better than own pbestscore
            double score = self.pbestScore;
            bestPos = self.pbestcoord;
            for(CFOSolution s : neighbours)
            {
                PSOSolution p1 = (PSOSolution) s;                
                if(p.isBetter(p1.pbestScore, score))
                {
                    score = p1.pbestScore;
                    bestPos = p1.pbestcoord;
                }
            }
        }
        
        return bestPos;
    }
    
    /**
     * Create new particle positions from existing particle positions.
     * Adjust velicity of particles accordingly.
     * 
     * @param pop
     * @param p
     * @param best
     * @param r
     * @return
     */
    protected LinkedList<PSOSolution> createNewParticlePositions(
            LinkedList<PSOSolution> pop, 
            Problem p, 
            PSOSolution best,
            Random r)
    {        
        double [][] minmax = ((CFOProblemInterface)p).getMinmax();
        LinkedList<PSOSolution> children = new LinkedList<PSOSolution>();
        
        // create children one at a time
        // basically copy parents, then update position and velocity of child particle
        for (int k = 0; k < pop.size(); k++)
        {
            PSOSolution parent = pop.get(k);
            PSOSolution child = new PSOSolution(parent);            
            double [] position = child.getCoordinate();
            double [] bestPos = getBestCoord(pop, best, parent, p);
            children.add(child);
            
            // update velocity
            for (int i = 0; i < child.velocity.length; i++)
            {
                // update velocity
                child.velocity[i] = 
                  (momentum*child.velocity[i]) + // how much of the previous velocity
                  (1-momentum)* 
                  (
                    + (c1 * r.nextDouble() * (child.pbestcoord[i] - position[i])) 
                    + (c2 * r.nextDouble() * (bestPos[i] - position[i]))
                  );
                
                // bound velocity
                double v = (minmax[i][1] - minmax[i][0]) * vMax;
                if(child.velocity[i] > v)
                {
                    child.velocity[i] = v;
                }
                else if(child.velocity[i] < -v)
                {
                    child.velocity[i] = -v;
                }                
            }
            
            // update position
            for (int i = 0; i < position.length; i++)
            {                
                // update position
                position[i] = position[i] + child.velocity[i];
            }            
            // ensure particle is within the problem bounds
            boundPosition(position, child.velocity, minmax, ((CFOProblemInterface)p).isToroidal());            
        }
        
        return children;
    }
    
    
    protected void boundPosition(
            double [] childCoord, 
            double [] velocity, 
            double [][] minmax, 
            boolean isToroidal)    
    {
        // bounce off problem bounds
        // doing it here instead of in AlgorithmUtils
        // because of velocity reflection!!!!
        for (int i = 0; i < childCoord.length; i++)
        {            
            // a bounce could bounce beyond the opposite end of the domain
            while(childCoord[i] > minmax[i][1] || childCoord[i] < minmax[i][0])
            {                
                // too large
                while(childCoord[i] > minmax[i][1])
                {
                    if(isToroidal)
                    {
                        childCoord[i] -= minmax[i][1]; // wrap
                    }
                    else
                    {
                        // subtract the difference
                        double diff = Math.abs(childCoord[i] - minmax[i][1]);
                        // always smaller
                        childCoord[i] = (minmax[i][1] - diff);
                    }
                    // invert velocity            
                    velocity[i] *= -1.0;
                }
                // too small
                while(childCoord[i] < minmax[i][0])
                {  
                    if(isToroidal)
                    {
                        childCoord[i] += minmax[i][1]; // wrap
                    }
                    else
                    {
                        double diff = Math.abs(childCoord[i] - minmax[i][0]);
                        // always larger
                        childCoord[i] = (minmax[i][0] + diff);
                    }
                    // invert velocity            
                    velocity[i] *= -1.0;
                } 
            }
        }
    }
    
    

    @Override
    public String getName()
    {
        return "Particle Swarm Optimization (PSO)";
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
        // vmax
        if(vMax>1 || vMax<0)
        {
            throw new InvalidConfigurationException("Invalid vMax " + vMax);
        }
        // momentum
        if(momentum>1 || momentum<0)
        {
            throw new InvalidConfigurationException("Invalid momentum " + momentum);
        }
        // neighoburhood size
        // must be less than or equal to the popsize and non zero
        if(neighbourhoodSize>popsize || neighbourhoodSize<=0)
        {
            throw new InvalidConfigurationException("Invalid neighbourhoodSize " + neighbourhoodSize);
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

    public double getVMax()
    {
        return vMax;
    }

    public void setVMax(double max)
    {
        vMax = max;
    }

    public double getC1()
    {
        return c1;
    }

    public void setC1(double c1)
    {
        this.c1 = c1;
    }

    public double getC2()
    {
        return c2;
    }

    public void setC2(double c2)
    {
        this.c2 = c2;
    }

    public double getMomentum()
    {
        return momentum;
    }

    public void setMomentum(double momentum)
    {
        this.momentum = momentum;
    }

    public int getNeighbourhoodSize()
    {
        return neighbourhoodSize;
    }

    public void setNeighbourhoodSize(int neighbourhoodSize)
    {
        this.neighbourhoodSize = neighbourhoodSize;
    }
}
