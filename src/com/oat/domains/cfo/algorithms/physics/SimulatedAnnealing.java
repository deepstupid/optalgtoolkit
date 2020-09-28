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
package com.oat.domains.cfo.algorithms.physics;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Random;

import com.oat.Algorithm;
import com.oat.InvalidConfigurationException;
import com.oat.Problem;
import com.oat.domains.cfo.CFOProblemInterface;
import com.oat.domains.cfo.CFOSolution;
import com.oat.utils.AlgorithmUtils;
import com.oat.utils.RandomUtils;


/**
 * Type: SimulatedAnnealing<br/>
 * Date: 14/03/2006<br/>
 * <br/>
 * Description: Simulated Annealing (SA) algorithm
 * <br/>
 * @author Jason Brownlee
 * 
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 22/12/2006   JBrownlee   Random moved to method variable rather than instance variable
 * 
 * </pre>
 */
public class SimulatedAnnealing extends Algorithm
{     
    // TODO - massive updating required
    
    protected double [] v; // step size vector
    
    // parameters
    protected long seed = System.currentTimeMillis();
    protected int initialpopsize = 100;          
    // TODO what is a good initial temp
    protected double temp = 5; // current temperature
    protected double Ns = 20; // number of steps before changing step (step window)
    protected double c = 2; // step adjustment parameter
    // TODO assumes 2D
    protected double Nt = Math.max(100, 5 * 2); // temperature step size (Ns*Nt)
    protected double rT = 0.85; // temp adjustement coefficient

    
    @Override
    public String getDetails()
    {
        return "Simulated Annealing (SA): " +
                "as described in: A. Corana; M. Marchesi; C. Martini, and S. Ridella. Minimizing multimodal functions of continuous variables with the \"simulated annealing\" algorithm. ACM Transactions on Mathematical Software (TOMS). 1987; 13(3):262-280. ISSN: 0098-3500. " +
                        " uses an initial random sample to determine the starting point of the search";
    }
    
    @Override
    protected void internalExecuteAlgorithm(Problem p)
    {
        Random r = new Random(seed);
        
        // prepare step size
        v = new double[((CFOProblemInterface)p).getDimensions()]; // step size vector
        for (int i = 0; i < v.length; i++)
        {
            v[i] = r.nextDouble(); // TODO - what value to initialise???
        }
        
        LinkedList<CFOSolution> pop = new LinkedList<CFOSolution>();        
        // prepare initial population
        while(pop.size() < initialpopsize)
        {
            pop.add(new CFOSolution(RandomUtils.randomPointInRange(r, ((CFOProblemInterface)p).getMinmax())));
        }        
        // evaluate
        p.cost(pop);        
        CFOSolution current = AlgorithmUtils.getBest(pop, p);
        
        // run algorithm until there are no evaluations left
        int h = 0;
        int j = 0;
        int m = 0;
        int [] accepted = new int[((CFOProblemInterface)p).getDimensions()];
        while(p.canEvaluate())
        {
            // generate sample
            CFOSolution newCurrent = generateNextSample(current, (CFOProblemInterface)p, h, r);
            // evaluate
            p.cost(newCurrent);            
            // check for acceptance (better or Metropolis)
            if(p.isBetter(newCurrent, current) || shouldAcceptMetropolis(current, newCurrent, r))
            {
                current = newCurrent;                
                accepted[h]++;
            }
            if(++h >= ((CFOProblemInterface)p).getDimensions())
            {
                h = 0; // reset
                j++;
                // check for step variation
                if(j >= Ns)
                {
                    // update step size
                    updateStepSize(accepted, (CFOProblemInterface)p);
                    j = 0; // reset
                    Arrays.fill(accepted, 0); // reset
                    // check for temp adjustment
                    if(++m >= Nt)
                    {
                        // update temperature
                        updateTemperature();
                        m = 0; // reset
                        
                        // no termination criterion - using all the evals
                    }
                }
            }
        }
    }

    
    protected CFOSolution generateNextSample(CFOSolution aCurrentSample, CFOProblemInterface p, int axis, Random r)
    {
        // duplicate the current coordinate
        double [] parent = aCurrentSample.getCoordinate(); 
        double [] coord = new double[parent.length];
        System.arraycopy(parent, 0, coord, 0, parent.length);        
        
        // generate for a single axis
        coord[axis] = coord[axis] + r.nextGaussian() * v[axis];
        // reflect
        AlgorithmUtils.fixCoordBounds(coord, p.getMinmax(), p.isToroidal());
        
        return new CFOSolution(coord);
    }
    
    protected void updateStepSize(int [] n, CFOProblemInterface p)
    {
        for (int i = 0; i < v.length; i++)
        {
            if(n[i] > 0.6*Ns)
            {
                v[i] = v[i] * (1.0 + c * (((n[i]/Ns)-0.6)/0.4));
            }
            else if(n[i] < 0.4*Ns)
            {
                v[i] = v[i] / (1.0 + c * ((0.4-(n[i]/Ns))/0.4));
            }
            // otherwise no change
        }
        // ensure stepsize is not too large
        double [][] minmax = p.getMinmax();
        for (int i = 0; i < v.length; i++)
        {
            if(v[i] > minmax[i][1])
            {
                v[i] = minmax[i][1];
            }
            else if(v[i] < minmax[i][0])
            {
                v[i] = minmax[i][0];
            }
        }
        
    }
    
    protected void updateTemperature()
    {
        temp = rT * temp;
    }
    
    protected boolean shouldAcceptMetropolis(CFOSolution current, CFOSolution newCurrent, Random r)
    {
        // exp(-delta f / T)
        double diff = Math.abs(current.getScore() - newCurrent.getScore());
        double p = Math.exp(-diff / temp);
        if(r.nextDouble() < p)
        {
            return true;
        }        
        
        return false;
    }
    
    
    
    @Override
    public String getName()
    {
        return "Simulated Annealing (SA)";
    }


    
    @Override
    public void validateConfiguration()
        throws InvalidConfigurationException
    {
        // popsize
        if(initialpopsize<=0)
        {
            throw new InvalidConfigurationException("Invalid initialpopsize " + initialpopsize);
        }
        // temp
        if(temp<0)
        {
            throw new InvalidConfigurationException("Invalid temp " + temp);
        }
        // ns
        if(Ns<0)
        {
            throw new InvalidConfigurationException("Invalid Ns " + Ns);
        }
        // c
        if(c<0)
        {
            throw new InvalidConfigurationException("Invalid c " + c);
        }
        // nt
        if(Nt<0)
        {
            throw new InvalidConfigurationException("Invalid Nt " + Nt);
        }
        // rt
        if(rT<0 || rT>1)
        {
            throw new InvalidConfigurationException("Invalid rT " + rT);
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

    public int getInitialpopsize()
    {
        return initialpopsize;
    }

    public void setInitialpopsize(int initialpopsize)
    {
        this.initialpopsize = initialpopsize;
    }

    public double getTemp()
    {
        return temp;
    }

    public void setTemp(double temp)
    {
        this.temp = temp;
    }

    public double getNs()
    {
        return Ns;
    }

    public void setNs(double ns)
    {
        Ns = ns;
    }

    public double getC()
    {
        return c;
    }

    public void setC(double c)
    {
        this.c = c;
    }

    public double getNt()
    {
        return Nt;
    }

    public void setNt(double nt)
    {
        Nt = nt;
    }

    public double getRT()
    {
        return rT;
    }

    public void setRT(double rt)
    {
        rT = rt;
    }
}
