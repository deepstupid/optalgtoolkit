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
package com.oat.domains.cfo.problems.hendtlass;

import com.oat.domains.cfo.CFOProblem;

/**
 * Type: ThreePotHoles<br/>
 * Date: 10/03/2006<br/>
 * <br/>
 * Description: Evolutionary Computation Using Island Populations in Time (2004)
 * <br/>
 * @author Jason Brownlee
 * 
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 
 * </pre>
 */
public class ThreePotHoles extends CFOProblem
{
    @Override
    protected double problemSpecificCost(double[] v)
    {
        double p1 = 0.0;
        for (int i = 0; i < dimensions; i++)
        {
            p1 += ((v[i] + 8.0) * (v[i] + 8.0)) + 0.1;
        }
        
        double p2 = 0.0;
        for (int i = 0; i < dimensions; i++)
        {
            p2 += ((v[i] + 2.0) * (v[i] + 2.0)) + 0.2;
        }
        
        double p3 = 0.0;
        for (int i = 0; i < dimensions; i++)
        {
            p3 += (v[i] - 3.0) * (v[i] - 3.0);
        } 
        
        double result = Math.sqrt(Math.sqrt(p1) * Math.sqrt(p2) * Math.sqrt(p3));       
        return result;
    }

    @Override
    protected double[][] preapreMinMax()
    {
        return new double[][]{{-10, +10},{-10, +10}};
    }

    @Override
    protected double[][] preapreOptima()
    {
        return new double[][]{
        		{3, 3},
        		{-2,-2},
        		{-8,-8}
        		};
    }

    @Override
    public boolean isMinimization()
    {
        return true;
    }

    @Override
    public String getName()
    {
        return "3 Pot Holes";
    }
    @Override
    public SUPPORTED_DIMENSIONS [] getSupportDimensionality()
    {
        return new SUPPORTED_DIMENSIONS[]{SUPPORTED_DIMENSIONS.TWO_DIMENSIONAL};
    }
}
