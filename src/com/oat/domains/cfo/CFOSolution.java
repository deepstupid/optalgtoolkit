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

import java.util.Arrays;

import com.oat.Solution;
import com.oat.domains.bfo.BFOSolution;
import com.oat.utils.ArrayUtils;
import com.oat.utils.BitStringUtils;


/**
 * Type: FuncOptSolution<br/>
 * Date: 10/03/2006<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 * 
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 07/08/2007   JBrownlee   Modified to support the embedding of a binary solution
 * </pre>
 */
public class CFOSolution extends Solution
{   
    /**
     * Coordinate for the solution in an N-dimensional Euclidean space
     */
    protected final double [] coordinate;  
    /**
     * Optional binary solution used to seed the creation of the coordinate
     */
    protected final BFOSolution binarySolution;

    
    
    
    
    /**
     * Copy Constructor
     * Creates a new solution instance with a COPY of the provided
     * solutions coordinates
     * @param parent
     */
    public CFOSolution(CFOSolution parent)
    {
        this(ArrayUtils.copyArray(parent.coordinate));
    }
    /**
     * Normal Constructor
     * Creates a new function optimization solution with the provided coordinate 
     * @param aCoord
     */
    public CFOSolution(double [] aCoord)
    {
        coordinate = aCoord;
        binarySolution = null;
    }
    /**
     * Creates a new Function Optimization solution with the provided 
     * binary solution embedded within. The binary string of the provided
     * binary solution is translated into the required coordinates 
     * (according to the problem definitions bitPrecision and numDimensions).
     * The internal binary solution is stored and updated such that when this funcopt
     * solution is modified (specifically evaluated() and setNormalizedRelativeScore() )
     * the internal solution is modified accordingly. This gives the appearance to the problem
     * that the funcopt problem definition and to the binary algorithms that the solution
     * is treated as per normal
     * @param aBinarySolution - a bit string solution to be internalized by this funcopt solution
     * @param aProblem - the problem definition that influences how the bitstring is translated 
     *                  specifically with regard to the number of bits per parameter as well as the number
     *                  of parameters
     */
    public CFOSolution(BFOSolution aBinarySolution, CFOProblem aProblem)
    {
        // translate the binary solution
        coordinate = BitStringUtils.decode(aProblem.getDecodeMode(), aBinarySolution.getBitString(), aProblem.getMinmax());
        // check for things        
        if(aBinarySolution.isEvaluated())
        {
        	evaluated(aBinarySolution.getScore());
        	if(aBinarySolution.hasNormalizedRelativeScore())
        	{
        		setNormalizedRelativeScore(aBinarySolution.getNormalizedRelativeScore());
        	}
        }
        // now we are seeded
        binarySolution = aBinarySolution;        
    }
    
    
    /**
     * Whether or not this coordinate was seed with a binary solution
     * @return
     */
    public boolean isSeededWithBinarySolution()
    {
        return (binarySolution!=null);
    }
    
    @Override
    public void evaluated(double aCost)
    {
       super.evaluated(aCost);
       if(isSeededWithBinarySolution())
       {
           binarySolution.evaluated(aCost);
       }
    }
    @Override
    public void setNormalizedRelativeScore(double n)
    {
        super.setNormalizedRelativeScore(n);
        if(isSeededWithBinarySolution())
        {
            binarySolution.setNormalizedRelativeScore(n);
        }
    }
    
    
    public double[] getCoordinate()
    {
        return coordinate;
    }
    
    @Override
    public boolean equals(Object o)
    {
        CFOSolution s = (CFOSolution) o;
        return Arrays.equals(coordinate, s.coordinate);
    }
    
    @Override
    public String toString()
    {
        return Arrays.toString(coordinate);
    }
}
