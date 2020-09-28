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
package com.oat.domains.cfo.problems;

import com.oat.domains.cfo.CFOProblem;

/**
 * Type: Hansen<br/>
 * Date: 16/05/2006<br/>
 * <br/>
 * Description: from http://www2.imm.dtu.dk/~km/GlobOpt/testex/testproblems.html#10
 * <br/>
 * @author Daniel Angus
 * 
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 
 * </pre>
 */
public class Hansens extends CFOProblem
{

    @Override
    protected double problemSpecificCost(double[] v)
    {       
        double sum1 = 0.0;
        double sum2 = 0.0;
        
        for (int i = 1; i <= 5 ; i++)
        {
            sum1 += i*Math.cos((i-1)*v[X]+i);
            sum2 += i*Math.cos((i+1)*v[Y]+i);
        }
        
        return sum1*sum2;        
    }

    @Override
    public String getName()
    {
        return "Hansens function";
    }

    @Override
    public boolean isMinimization()
    {
        return false;
    }

    @Override
    protected double[][] preapreMinMax()
    {
        return new double[][]{{-10.00, +10.00},{-10.00, +10.00}};
    }

    @Override
    protected double[][] preapreOptima()
    {      
    	return null;
    }
    
    @Override
    public SUPPORTED_DIMENSIONS [] getSupportDimensionality()
    {
        return new SUPPORTED_DIMENSIONS[]{SUPPORTED_DIMENSIONS.TWO_DIMENSIONAL};
    }
}
