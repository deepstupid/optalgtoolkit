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
package com.oat.domains.cfo.problems.optainet;

import com.oat.domains.cfo.CFOProblem;

/**
 * Type: SchaffersFunction<br/>
 * Date: 10/03/2006<br/>
 * <br/>
 * Description: An Artificial Immune Network for Multimodal Function Optimization (2002)
 * <br/>
 * @author Jason Brownlee
 * 
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 
 * </pre>
 */
public class SchaffersFunction extends CFOProblem
{

    @Override
    protected double problemSpecificCost(double[] v)
    {
        double x = v[0];
        double y = v[1];
        
        double root = Math.sqrt((x*x) + (y*y));
        double sin = Math.sin(root);
        double p1 = (sin*sin) - 0.5;
        
        double lower = 1.0 + 0.001 * ((x*x) + (y*y));
        double p2 = lower*lower;
        
        return (0.5 - (p1/p2));
    }

    @Override
    protected double[][] preapreMinMax()
    {
        double [][] d = new double[dimensions][];
        for (int i = 0; i < d.length; i++)
        {
            d[i] = new double[]{-10, +10};
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
    public boolean isMinimization()
    {
        return false;
    }

    @Override
    public String getName()
    {
        return "Schaffer's Function";
    }
    @Override
    public SUPPORTED_DIMENSIONS [] getSupportDimensionality()
    {
        return new SUPPORTED_DIMENSIONS[]{SUPPORTED_DIMENSIONS.TWO_DIMENSIONAL};
    }
}
