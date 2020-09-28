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

import java.util.LinkedList;
import java.util.Random;

import com.oat.Algorithm;
import com.oat.AlgorithmRunException;
import com.oat.InvalidConfigurationException;
import com.oat.Problem;
import com.oat.domains.cfo.CFOProblemInterface;
import com.oat.domains.cfo.CFOSolution;
import com.oat.domains.cfo.CFOUtils;
import com.oat.utils.AlgorithmUtils;
import com.oat.utils.EvolutionUtils;


/**
 * Type: DifferentialEvolution<br/>
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
 *                          Re-arranged replacement call, such that it only gets executed if there
 *                          are remaining evaluations
 * 06/01/2007   JBrownlee   Moved algorithm main loop into single method
 * 09/01/2007   JBrownlee   Use generic replacement strategy, cleaned up 
 * 
 * </pre>
 */
public class DifferentialEvolution extends Algorithm
{    
    public static enum Mode {
        DE_RAND_1_BIN, 
        DE_CURRENT_TO_RAND, 
        DE_RAND_1_EXP, 
        DE_CURRENT_TO_RAND_1_BIN}
    
    // user parameters
    protected long seed = System.currentTimeMillis();
    protected int popsize = 100;
    protected double CR = 0.8;
    protected double F = 0.8;
    protected Mode mode = Mode.DE_RAND_1_BIN;    
    
    
    @Override
    public String getDetails()
    {
        return
        "Differential Evolution (DE): " +
        "As described in Kenneth V. Price. Chapter Six: An Introduction to Differential Evolution. D. Corne; M. Dorigo, and F. Glover, Editors. New Ideas in Optimization. England: McGraw-Hill; 1999; pp. 79-108." +
        "Supports a number of standard combination modes, " +
        "uses a random \"K\" value where appropriate.";
    }
    
    @Override
    public void internalExecuteAlgorithm(Problem p)
    {
        Random r = new Random(seed);        
        
        // prepare initial population
        LinkedList<CFOSolution> pop = CFOUtils.getRandomPopulationReal(r, (CFOProblemInterface)p, popsize);   
        // evaluate
        p.cost(pop);
        
        // run algorithm until there are no evaluations left
        while(p.canEvaluate())
        {
            triggerIterationCompleteEvent(p,pop);
            // reproduce
            LinkedList<CFOSolution> children = generateChildren(pop, (CFOProblemInterface)p, r);
            // evaluate
            p.cost(children);    
            // perform replacement
            if(p.canEvaluate())
            {
                pop = EvolutionUtils.elitistReplacement(pop, children, p);   
            }
        }
    }
  
    
    public LinkedList<CFOSolution> generateChildren(LinkedList<CFOSolution> pop, CFOProblemInterface p, Random r)
    {
        int NP = pop.size();
        int D = p.getDimensions();
        LinkedList<CFOSolution> children = new LinkedList<CFOSolution>();
        
        for (int i = 0; i < NP; i++)
        {
            int r1, r2, r3 = -1;
            do{r1=r.nextInt(NP);}while(r1==i);
            do{r2=r.nextInt(NP);}while(r2==i||r2==r1);
            do{r3=r.nextInt(NP);}while(r3==i||r3==r1||r3==r2);
            
            double [] p0 = pop.get(i).getCoordinate();
            double [] p1 = pop.get(r1).getCoordinate();
            double [] p2 = pop.get(r2).getCoordinate();
            double [] p3 = pop.get(r3).getCoordinate();     
            double [] child = null;
            
            switch(mode)
            {
                case DE_RAND_1_BIN:
                {
                    child = DE_RAND_1_BIN(p0, p1, p2, p3, D, r);
                    break;
                }        
                case DE_CURRENT_TO_RAND:
                {
                    child = DE_CURRENT_TO_RAND(p0, p1, p2, p3, D, r);
                    break;
                }
                case DE_RAND_1_EXP:
                {
                    child = DE_RAND_1_EXP(p0, p1, p2, p3, D, r);
                    break;
                }
                case DE_CURRENT_TO_RAND_1_BIN:
                {
                    child = DE_CURRENT_TO_RAND_1_BIN(p0, p1, p2, p3, D, r);
                    break;
                }
                default:
                {
                    throw new AlgorithmRunException("Unknown mode: " + mode);
                }
            }
            
            // add child to list
            AlgorithmUtils.fixCoordBounds(child, p.getMinmax(), p.isToroidal());
            CFOSolution s = new CFOSolution(child);
            children.add(s);
        }
        
        return children;
    }
    
    protected double [] DE_RAND_1_BIN(double [] p0, double [] p1, double [] p2, double [] p3, int D, Random r)
    {
        double [] child = new double[D];
        
        int j = (int) (r.nextDouble() * D); // random starting point
        for (int k = 1; k <= D; k++)
        {
            if(r.nextDouble() < CR || k == D)
            {
                child[j] = p3[j] + F * (p1[j] - p2[j]);
            }
            else
            {
                child[j] = p0[j];
            }               
            
            // check bounds
            j = (j + 1) % D; // wrap
        }
        
        return child;
    }
    
    protected double [] DE_CURRENT_TO_RAND(double [] p0, double [] p1, double [] p2, double [] p3, int D, Random r)
    {
        double [] child = new double[D];
        
        for (int j = 0; j < D; j++)
        {
            // randomise K
            double K = r.nextDouble();            
            child[j] = p0[j] + (K * (p3[j] - p0[j])) + (F * (p1[j] - p2[j]));
        }
        
        return child;
    }
    
    protected double [] DE_RAND_1_EXP(double [] p0, double [] p1, double [] p2, double [] p3, int D, Random r)
    {
        double [] child = new double[D];   
        
        int j = (int) (r.nextDouble() * D); // random starting point
        int flag = 0;
        for (int k = 1; k <= D; k++)
        {
            if(r.nextDouble() < CR || k == D)
            {
                flag = 1;
            }
            if(flag == 1)
            {
                child[j] = p3[j] + F * (p1[j] - p2[j]);
            }
            else
            {
                child[j] = p0[j];
            }             
            
            // check bounds
            j = (j + 1) % D; // wrap
        }
        
        return child;
    }
    
    
    protected double [] DE_CURRENT_TO_RAND_1_BIN(double [] p0, double [] p1, double [] p2, double [] p3, int D, Random r)
    {
        double [] child = new double[D];
        
        int j = (int) (r.nextDouble() * D); // random starting point
        for (int k = 1; k <= D; k++)
        {
            if(r.nextDouble() < CR || k == D)
            {
                // randomise K
                double K = r.nextDouble();                
                child[j] = p0[j] +  (K * (p3[j] - p0[j])) + (F * (p1[j] - p2[j]));
            }
            else
            {
                child[j] = p0[j];
            }  
            
            // check bounds
            j = (j + 1) % D; // wrap                
        }
        
        return child;
    }
    
    
    @Override
    public String getName()
    {
        return "Differential Evolution (DE)";
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
        // crossover
        if(CR>1||CR<0)
        {
            throw new InvalidConfigurationException("Invalid CR " + CR);
        }
        // scale factor
        if(F<0)
        {
            throw new InvalidConfigurationException("Invalid F " + F);
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

    public double getCR()
    {
        return CR;
    }

    public void setCR(double cr)
    {
        CR = cr;
    }

    public double getF()
    {
        return F;
    }

    public void setF(double f)
    {
        F = f;
    }

    public Mode getMode()
    {
        return mode;
    }

    public void setMode(Mode mode)
    {
        this.mode = mode;
    }

    
}
