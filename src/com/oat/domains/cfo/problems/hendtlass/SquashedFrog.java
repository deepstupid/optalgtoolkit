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
 * Type: SquashedFrog<br/>
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
public class SquashedFrog extends CFOProblem
{

    @Override
    protected double problemSpecificCost(double[] v)
    {
        double x = v[0];
        double y = v[1];
        
        double t = Math.pow((x-0.1),2.0) + Math.pow((y-0.2), 2.0);        
        double result =  1.0 + Math.pow(t, 0.25) - Math.cos(5.0 * Math.PI * Math.sqrt(t));
        return result;
    }

    @Override
    protected double[][] preapreMinMax()
    {
        return new double[][]{{-2, +2},{-2, +2}};
    }

    @Override
    protected double[][] preapreOptima()
    {
        return new double[][]{{0.1,0.2}};
    }

    @Override
    public boolean isMinimization()
    {
        return true;
    }

    @Override
    public String getName()
    {
        return "Squashed Frog Function (Timbo)";
    }
    @Override
    public SUPPORTED_DIMENSIONS [] getSupportDimensionality()
    {
        return new SUPPORTED_DIMENSIONS[]{SUPPORTED_DIMENSIONS.TWO_DIMENSIONAL};
    }
}
