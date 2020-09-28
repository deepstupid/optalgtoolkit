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
package com.oat.domains.cfo.algorithms.evolution;

import java.util.Collections;
import java.util.LinkedList;
import java.util.Random;

import com.oat.Algorithm;
import com.oat.AlgorithmRunException;
import com.oat.AutomaticallyConfigurableAlgorithm;
import com.oat.InvalidConfigurationException;
import com.oat.Problem;
import com.oat.domains.cfo.CFOProblemInterface;
import com.oat.domains.cfo.CFOSolution;
import com.oat.utils.AlgorithmUtils;
import com.oat.utils.EvolutionUtils;
import com.oat.utils.RandomUtils;


/**
 * Type: EvolutionStrategies<br/>
 * Date: 13/03/2006<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 * 
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 22/12/2006   JBrownlee   Random moved to method variable rather than instance variable
 *                          Uses evolution utils for elitist selection
 * 24/07/2007   JBrownlee   Added support for automatic configuration
 * </pre>
 */
public class EvolutionStrategies extends Algorithm
	implements AutomaticallyConfigurableAlgorithm
{
    public static enum Mode {MU_PLUS_LAMBDA, MU_COMMA_LAMBDA} 
    
    // configuration
    protected long seed;
    protected int popsize;
    protected double tau;
    protected double eta;
    protected double rho;
    protected Mode mode;
    protected double crossover;
    
    
    public EvolutionStrategies()
    {
    	automaticConfiguration(2, this);
    }
   
    
    @Override
    public void automaticallyConfigure(Problem problem)
    {
    	automaticConfiguration(((CFOProblemInterface)problem).getDimensions(), this);
    }
    
    public static void automaticConfiguration(int numDimensions, EvolutionStrategies algorithm)
    {
    	algorithm.setSeed(System.currentTimeMillis());
    	algorithm.setCrossover(0.7); // ?
    	algorithm.setPopsize(20); // ?
    	algorithm.setTau(defaultTau(numDimensions));
    	algorithm.setEta(defaultEta(numDimensions));
    	algorithm.setRho((5*Math.PI)/180.0);
    	algorithm.setMode(Mode.MU_PLUS_LAMBDA);
    }
    
    
    @Override
    public String getDetails()
    {
        return
        "Evolution Strategies (ES) (mu+lambda & mu,lamda): " +
        "As described in: Hunter Rudolph. Evolution strategies. Thomas Back; David B Fogel, and Zbigniew Michalwicz. Evolutionary Computation 1 - Basic Algorithms and Operations. UK: Institute of Physics Publishing; 2000; pp. 81-88. " +
        "uses uniform crossover.";
    }
    
    public static double defaultTau(double numDimensions)
    {
        return Math.pow(2.0*numDimensions, (-1.0/2.0));
    }
    public static double defaultEta(double numDimensions)
    {
        return Math.pow(4.0*numDimensions, (-1.0/4.0));
    }
    
    protected class ESSolution extends CFOSolution
    {
        protected final double [] stdevs;
        protected final double [] directions;
        
        public ESSolution(double [] aCoord)
        {
            super(aCoord);
            stdevs = new double[aCoord.length];
            directions = new double[aCoord.length];
        }
        
        /**
         * only needed for initial random pop
         * @param p
         */
        protected void prepare(CFOProblemInterface p, Random r)
        {
            double [][] minmax = p.getMinmax();
            
            for (int i = 0; i < p.getDimensions(); i++)
            {
                stdevs[i] = (minmax[i][1]-minmax[i][0]) * r.nextDouble();
                directions[i] = (2*Math.PI) * r.nextDouble();
            }
        }

        public double[] getDirections()
        {
            return directions;
        }

        public double[] getStdevs()
        {
            return stdevs;
        }
    }
    
    @Override
    protected void internalExecuteAlgorithm(Problem p)
    {
        Random r = new Random(seed);
        
        LinkedList<ESSolution> pop = new LinkedList<ESSolution>();        
        // prepare initial population
        while(pop.size() < popsize)
        {
            ESSolution s = new ESSolution(RandomUtils.randomPointInRange(r, ((CFOProblemInterface)p).getMinmax()));
            s.prepare((CFOProblemInterface)p, r);
            pop.add(s);
        }        
        // evaluate
        p.cost(pop);        
        
        // run algorithm until there are no evaluations left
        while(p.canEvaluate())
        {
            triggerIterationCompleteEvent(p,pop);
            // reproduce
            LinkedList<ESSolution> children = generateChildren(pop, (CFOProblemInterface)p, r);
            // evaluate
            p.cost(children);
            if(p.canEvaluate())
            {
                // finalise the population            
                pop = finalisePopulation(pop, children, p);  
            }
        }
    }
    
    
    protected LinkedList<ESSolution> finalisePopulation(
            LinkedList<ESSolution> pop,
            LinkedList<ESSolution> children, 
            Problem p)
    {
        LinkedList<ESSolution> finalPop = new LinkedList<ESSolution>();
        
        switch(mode)
        {
            case MU_PLUS_LAMBDA:
            {
                finalPop.addAll(pop);
                finalPop.addAll(children);    
                // elitest selection
                EvolutionUtils.elitistSelectionStrategy(finalPop, popsize, p);
                break;
            }
            case MU_COMMA_LAMBDA:
            {
                finalPop.addAll(children);
                break;
            }
            default:
            {
                throw new AlgorithmRunException("Invalid mode");
            }
        }
        
        return finalPop;
    }
    
    public LinkedList<ESSolution> generateChildren(LinkedList<ESSolution> pop, CFOProblemInterface p, Random r)
    {        
        LinkedList<ESSolution> cs = new LinkedList<ESSolution>();
        // randomise parents
        Collections.shuffle(pop, r);
        // recombine        
        for (int i = 0; i < pop.size(); i+=2)
        {
            ESSolution p1 = pop.get(i);
            ESSolution p2 = pop.get(i+1);            
            // recombination
            ESSolution s1 = recombine(p1, p2, r);
            ESSolution s2 = recombine(p2, p1, r);            
            // mutation            
            mutate(s1, p, r);
            mutate(s2, p, r);
            // add
            cs.add(s1);
            cs.add(s2);
        }
        
        return cs;
    }
    
    protected void mutate(ESSolution s, CFOProblemInterface p, Random r)
    {        
        // mutate angles
        for (int i = 0; i < s.directions.length; i++)
        {
            s.directions[i] = (s.directions[i] + (rho*r.nextGaussian())) % (2.0*Math.PI);
        }
        
        // mutate stdev's
        double ztau = r.nextGaussian();
        for (int i = 0; i < s.stdevs.length; i++)
        {
            s.stdevs[i] = s.stdevs[i] * Math.exp((tau * ztau) + (eta*r.nextGaussian()));
        }        
        
        // mutate coords        
        double [] coord = s.getCoordinate();
        for (int i = 0; i < coord.length; i++)
        {
            coord[i] = coord[i] + s.directions[i] * s.stdevs[i] * r.nextGaussian();                        
        }
        
        AlgorithmUtils.fixCoordBounds(coord, p.getMinmax(), p.isToroidal());
    }
    
    
    /**
     * Uniform crossover
     * 
     * @param p1
     * @param p2
     * @return
     */
    protected ESSolution recombine(ESSolution p1, ESSolution p2, Random r)
    {
        ESSolution s = new ESSolution(new double[p1.getCoordinate().length]);
        
        if(r.nextDouble() <= crossover)
        {
            // coord
            for (int i = 0; i < p1.getCoordinate().length; i++)
            {
                s.getCoordinate()[i] = (r.nextBoolean()) ? p1.getCoordinate()[i] : p2.getCoordinate()[i];
            }
            // stdev
            for (int i = 0; i < p1.stdevs.length; i++)
            {
                s.stdevs[i] = (r.nextBoolean()) ? p1.stdevs[i] : p2.stdevs[i];
            }
            // angles
            for (int i = 0; i < p1.directions.length; i++)
            {
                s.directions[i] = (r.nextBoolean()) ? p1.directions[i] : p2.directions[i];
            }
        }
        // copy
        else
        {
            // copy the first parent
            System.arraycopy(p1.getCoordinate(), 0, s.getCoordinate(), 0, p1.getCoordinate().length);
            System.arraycopy(p1.stdevs, 0, s.stdevs, 0, p1.stdevs.length);
            System.arraycopy(p1.directions, 0, s.directions, 0, p1.directions.length);
        }
        
        return s;
    }

    @Override
    public String getName()
    {
        return "Evolution Strategies (ES)";
    }
    

    
    @Override
    public void validateConfiguration()
        throws InvalidConfigurationException
    {
        // pop size
        if((popsize%2)!=0)
        {
            throw new InvalidConfigurationException("Invalid popsize, must be even " + popsize);
        }
        if(popsize<=0)
        {
            throw new InvalidConfigurationException("Invalid popsize " + popsize);
        }
        // crossover
        if(crossover>1||crossover<0)
        {
            throw new InvalidConfigurationException("Invalid crossover " + crossover);
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

    public double getTau()
    {
        return tau;
    }

    public void setTau(double tau)
    {
        this.tau = tau;
    }

    public double getEta()
    {
        return eta;
    }

    public void setEta(double eta)
    {
        this.eta = eta;
    }

    public double getRho()
    {
        return rho;
    }

    public void setRho(double rho)
    {
        this.rho = rho;
    }

    public Mode getMode()
    {
        return mode;
    }

    public void setMode(Mode mode)
    {
        this.mode = mode;
    }

    public double getCrossover()
    {
        return crossover;
    }

    public void setCrossover(double crossover)
    {
        this.crossover = crossover;
    }

    
}
