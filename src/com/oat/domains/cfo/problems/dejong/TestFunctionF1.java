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
package com.oat.domains.cfo.problems.dejong;

import com.oat.domains.cfo.CFOProblem;

/**
 * Type: TestFunctionF1<br/>
 * Date: 10/03/2006<br/>
 * <br/>
 * Description: An analysis of the behavior of a class of genetic adaptive sysetms (De Jong) (1975)
 * <br/>
 * @author Jason Brownlee
 * 
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 
 * </pre>
 */
public class TestFunctionF1 extends CFOProblem
{

    @Override
    protected double problemSpecificCost(double[] v)
    {
        // f1(x)=sum(x(i)^2), i=1:n, -5.12<=x(i)<=5.12
        
        double sum = 0.0;
        
        for (int i = 0; i < dimensions; i++)
        {
            sum += Math.pow(v[i], 2);
        }
        
        return sum;        
    }

    @Override
    public String getName()
    {
        return "Test Function F1";
    }

    @Override
    public boolean isMinimization()
    {
        return true;
    }

    @Override
    protected double[][] preapreMinMax()
    {
        double [][] d = new double[dimensions][];
        for (int i = 0; i < d.length; i++)
        {
            d[i] = new double[]{-5.12, +5.12};
        }
        return d;
    }

    @Override
    protected double[][] preapreOptima()
    {
        // f(x)=0, x(i)=0, i=1:n.
        
        double [][] d = new double[1][dimensions];
        for (int i = 0; i < d[0].length; i++)
        {
            d[0][i] = 0;
        }
        return d;
    }
    @Override
    public SUPPORTED_DIMENSIONS [] getSupportDimensionality()
    {
        return new SUPPORTED_DIMENSIONS[]{SUPPORTED_DIMENSIONS.ANY};
    }
}
