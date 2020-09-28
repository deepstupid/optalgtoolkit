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
package com.oat.domains.cfo;

import com.oat.AlgorithmRunException;
import com.oat.InvalidConfigurationException;
import com.oat.Problem;
import com.oat.Solution;
import com.oat.SolutionEvaluationException;
import com.oat.domains.bfo.BFOProblemInterface;
import com.oat.domains.bfo.BFOSolution;
import com.oat.utils.AlgorithmUtils;
import com.oat.utils.BinaryDecodeMode;

/**
 * Type: FuncOptProblem<br/>
 * Date: 10/03/2006<br/>
 * <br/>
 * Description: Base function optimization problem definition.
 * <br/>
 * @author Jason Brownlee
 *
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 22/12/2006   JBrownlee   Validate binary solutions to ensure the binary to double 
 *                          conversion matches before evaluation
 * 09/01/2007   JBrownlee   Added support for 4D only function
 * 06/07/2007   JBrownlee   Added support for GUI configurable (dimensions and precision)
 *                          Also updated getProblemDetails()
 * 07/07/2007   JBrownlee   Does not check dimension validity until configuration validation (not in setDimension())
 * 07/08/2007   JBrownlee   Implemented the BinaryProblemInterface interface to allow the problem to appear
 *                          as a binary problem to binary algorithms
 * 20/08/2007	JBrownlee	Added a wrapper for evaluating the cost of binary solutions as cfo solutions
 * </pre>
 */
public abstract class CFOProblem extends Problem
    implements BFOProblemInterface, CFOProblemInterface
{    
		
    /**
     * Indexes on the problem bounds
     */
    public final static int MIN = 0, MAX = 1;
    /**
     * Indexes for a solution coordinate
     */
    public final static int X = 0, Y = 1;
    
    /**
     * Supported dimensionality
     */
    public static enum SUPPORTED_DIMENSIONS {
        ONE_DIMENSIONAL, 
        TWO_DIMENSIONAL, 
        THREE_DIMENSIONAL, 
        FOUR_DIMENSIONAL, 
        ANY}
        
    /**
     * bit precision used for parameters when converting binary to continuous, defaults to 64
     */
    protected int bitPrecision = 64;
    /**
     * binary decode mode when converting binary strings to continuous parameters 
     */
    protected BinaryDecodeMode decodeMode = BinaryDecodeMode.GrayCode;
    /**
     * dimensionality used (number of parameters), defaults to 2
     */
    protected int dimensions = 2;
    
    
    /**
     * Default Constructor
     */
    public CFOProblem()
    {
    }
    
    
    
       
    /**
     * Default Constructor
     */
    public CFOProblem(int numDimensions)
    {
    	this();
        setDimensions(numDimensions);
    }
    
    
    public CFOProblem getCreateNewInstance(int numDimensions)
    {
        CFOProblem f = null;
        try
        {
            f = this.getClass().newInstance();
            
        }
        catch (Exception e)
        {
            throw new AlgorithmRunException("Unable to prepare new instance with specified dimensionality.", e);
        }
        f.setDimensions(numDimensions);
        return f;
    }
    
    
    /**
     * Support for variable dimensionality
     * @return
     */
    public abstract SUPPORTED_DIMENSIONS [] getSupportDimensionality();
    
    
    public boolean isDimensionalitySupported(SUPPORTED_DIMENSIONS d)
    {
        SUPPORTED_DIMENSIONS [] dd = getSupportDimensionality();
        for (int i = 0; i < dd.length; i++)
        {
            if(dd[i] == d)
            {
                return true;
            }
        }
        return false;
    }
    public boolean isDimensionalitySupported(int d)
    {
        if(d <= 0)
        {
            return false;
        }
        else if(isDimensionalitySupported(SUPPORTED_DIMENSIONS.ANY))
        {
            return true;
        }
        else if(d == 1 && isDimensionalitySupported(SUPPORTED_DIMENSIONS.ONE_DIMENSIONAL))
        {
            return true;
        }
        else if(d == 2 && isDimensionalitySupported(SUPPORTED_DIMENSIONS.TWO_DIMENSIONAL))
        {
            return true;
        }
        else if(d == 3 && isDimensionalitySupported(SUPPORTED_DIMENSIONS.THREE_DIMENSIONAL))
        {
            return true;
        }
        return false;
    }
    
    @Override
    public String getDetails()
    {
        StringBuffer b = new StringBuffer();
        double [][] minmax = getMinmax();        
        CFOSolution [] globalOptima = getGlobalOptima();
        
        b.append(super.getDetails()+", ");
        b.append("BitPrecisionPerParameter="+bitPrecision+", ");
        b.append("Torroidal="+isToroidal()+", ");
        b.append("Dimensions="+dimensions+", ");
        b.append("Bounds=(");
        for (int i = 0; i < minmax.length; i++)
        {
            b.append(i+"[min="+minmax[i][MIN]+",max="+minmax[i][MAX]+"]");
            if(i!=minmax.length-1)
            {
                b.append(",");
            }
        }
        b.append("), ");
        b.append("Optima=(");
        if(globalOptima==null)
        {
            b.append("Unknown");
        }
        else
        {
            for (int i = 0; i < globalOptima.length; i++)
            {
            	// coordinate in [0,0,0,0](Cost=0) format
                b.append(globalOptima[i].toString());
                b.append("{Score="+globalOptima[i].getScore()+"}");
            }
        }
        b.append(")");
        
        return b.toString();
    }
    
    
    protected abstract double [][] preapreMinMax();
    protected abstract double [][] preapreOptima();   
    
    protected abstract double problemSpecificCost(double [] v);
    
    @Override
    protected double problemSpecificCost(Solution solution)
    {        
        return problemSpecificCost(((CFOSolution)solution).getCoordinate());
    }
    
    @Override
    public void cost(Solution solution)
        throws SolutionEvaluationException
    {
    	// intercept's all evaluations
        if(solution instanceof BFOSolution)
        {
            solution = new CFOSolution((BFOSolution)solution, this);
        }
        super.cost(solution);
    }
    
    @Override
    public void checkSolutionForSafety(Solution solution) 
        throws AlgorithmRunException
    {        
        checkSolutionForSafety(((CFOSolution)solution).getCoordinate());
    }
    

    /**
     * Performs a function evaluations without the overhead of the problem cost() function
     * @return
     */
    public double directFunctionEvaluation(double [] coord)
    {
    	return problemSpecificCost(coord);
    }
         
    public void checkSolutionForSafety(double [] v) 
        throws AlgorithmRunException
    {
        if(v.length < dimensions)
        {
            throw new AlgorithmRunException("Solution coordinate does cont contain the desired number of dimensions " + dimensions);
        }
        double [][] minmax = getMinmax();
        for (int i = 0; i < dimensions; i++)
        {
            if(!AlgorithmUtils.inBounds(v[i], minmax[i][0], minmax[i][1]))
            {
                throw new AlgorithmRunException("Unable to evaluate, coordinate is out of function bounds (dimension ["+i+"])" +
                        " val["+v[i]+"] max["+minmax[i][0]+"], val["+v[i]+"], max["+minmax[i][1]+"].");
            }
        }
    }
    
    /**
     * Binary problem interface implementation where the number of bits
     * used to represent the problem is specified by the problem bit precision
     * multiplied by the number of dimensions
     * 
     *  @return - bitPrecision * dimensions
     */
    @Override
    public int getBinaryStringLength()
    {
        return bitPrecision * dimensions;
    }
    
    public int getBitPrecision()
    {
        return bitPrecision;
    }
    public void setBitPrecision(int b)
    {
        bitPrecision = b;
    }

    public int getDimensions()
    {
        return dimensions;
    }

    
    
    public CFOSolution [] getGlobalOptima()
    {
    	double [][] coords = preapreOptima();
    	if(coords == null)
    	{
    		return null;
    	}
    	
    	CFOSolution [] globalOptima = new CFOSolution[coords.length];
		for (int i = 0; i < globalOptima.length; i++)
		{
			// ensure a valid coordinate
			checkSolutionForSafety(coords[i]);			
			// create
			globalOptima[i] = new CFOSolution(coords[i]);
			// set cost directly
			double score = directFunctionEvaluation(coords[i]);
			globalOptima[i].evaluated(score);

		}
    	return globalOptima;
    }

    public double[][] getMinmax()
    {
        return preapreMinMax();
    }
        
    @Override
    public boolean isToroidal()
    {
        return false;
    }

    public void setDimensions(int d)
    {        
        this.dimensions = d;
    }
    
    @Override
    public boolean isUserConfigurable()
    {
        return true;
    }
    
    @Override
    public void validateConfiguration() throws InvalidConfigurationException
    {         
        // validation
        if(!isDimensionalitySupported(dimensions))
        {
            throw new InvalidConfigurationException("Dimensionality not supported " + dimensions);
        }
        else if(bitPrecision<1||bitPrecision>64)
        {
            throw new InvalidConfigurationException("Unsupported bit precision " + bitPrecision);
        }
    }

	public BinaryDecodeMode getDecodeMode()
	{
		return decodeMode;
	}

	public void setDecodeMode(BinaryDecodeMode decodeMode)
	{
		this.decodeMode = decodeMode;
	}
}
