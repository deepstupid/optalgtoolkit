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
package com.oat.domains.cfo.problems.timmis;

import com.oat.domains.cfo.CFOProblem;

/**
 * Type: F6<br/>
 * Date: 16/05/2006<br/>
 * <br/>
 * Description: Assessing the performance of two immune inspired algorithms and a hybrid genetic algorithm for function optimisation (2004)
 * Verified with: An improvement of the standard genetic algorithm fighting premature convergence in continuous optimisation (2001)
 * <br/> 
 * @author Jason Brownlee
 * 
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 
 * </pre>
 */
public class F6 extends CFOProblem
{

    @Override
    protected double problemSpecificCost(double[] v)
    {
        double x = v[0];
        double y = v[1];  
        return (x*x*x*x)/4 - (x*x)/2 + x/10 + (y*y)/2;
    }

    @Override
    public String getName()
    {
        return "F6 (Quartic)";
    }

    @Override
    public boolean isMinimization()
    {
        return true;
    }

    @Override
    protected double[][] preapreMinMax()
    {
        return new double[][]{{-10,10}, {-10,10}};
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
