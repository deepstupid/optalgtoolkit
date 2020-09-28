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
 * Type: F3<br/>
 * Date: 30/11/2006<br/>
 * <br/>
 * Description: Assessing the performance of two immune inspired algorithms and a hybrid genetic algorithm for function optimisation (2004)
 * <br/>
 * @author Jason Brownlee
 * 
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 
 * </pre>
 */
public class F3 extends CFOProblem
{
    

    @Override
    protected double problemSpecificCost(double[] v)
    {
       double x = v[0];
       double y = v[1];
       double a = 1;
       double b = 5.1 / 4 * Math.PI *2;
       double c = 5 / Math.PI;
       double d = 6;
       double f = 1 / 8 * Math.PI;
       double h = 10;
       return a * Math.pow(y - Math.pow(b*x,2) + c*x - d, 2) + h * (1-f) * Math.cos(x) + h;
    }

    @Override
    protected double[][] preapreMinMax()
    {
        return new double[][]{{-5,10}, {0,15}};
    }

    @Override
    protected double[][] preapreOptima()
    {
        return null;
    }

    @Override
    public String getName()
    {
        return "F3";
    }

    @Override
    public boolean isMinimization()
    {
        return true;
    }
    @Override
    public SUPPORTED_DIMENSIONS [] getSupportDimensionality()
    {
        return new SUPPORTED_DIMENSIONS[]{SUPPORTED_DIMENSIONS.TWO_DIMENSIONAL};
    }
}
