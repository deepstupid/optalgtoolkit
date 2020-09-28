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
package com.oat.domains.cfo.problems.clonalg;

import com.oat.domains.cfo.CFOProblem;

/**
 * Type: G1<br/>
 * Date: 11/12/2006<br/>
 * <br/>
 * Description: As described in: Learning and optimization using the clonal selection principle (2002)
 * <br/>
 * @author Jason Brownlee
 * 
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 
 * </pre>
 */
public class G1 extends CFOProblem
{
    public G1()
    {
        setDimensions(1);
    }

    @Override
    protected double problemSpecificCost(double[] v)
    { 
        return Math.pow(Math.sin(5.0*Math.PI*v[X]), 6.0);
    }

    @Override
    public String getName()
    {
        return "G1";
    }

    @Override
    public boolean isMinimization()
    {
        return false;
    }

    @Override
    protected double[][] preapreMinMax()
    {
       return new double[][]{{0.0,1.0}};
    }

    @Override
    protected double[][] preapreOptima()
    {      
    	return new double[][]{{0.1}, {0.3}, {0.5}, {0.7}, {0.9}};
    }
    
    @Override
    public SUPPORTED_DIMENSIONS [] getSupportDimensionality()
    {
        return new SUPPORTED_DIMENSIONS[]{SUPPORTED_DIMENSIONS.ONE_DIMENSIONAL};
    }
}
