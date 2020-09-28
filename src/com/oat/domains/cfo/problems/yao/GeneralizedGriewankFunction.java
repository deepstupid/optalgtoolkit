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
package com.oat.domains.cfo.problems.yao;

import com.oat.domains.cfo.CFOProblem;

/**
 * Type: GeneralizedGriewankFunction<br/>
 * Date: 13/11/2006<br/>
 * <br/>
 * Description:
 * 
 * As specified in: Evolutionary programming made faster (1999)
 * 
 * <br/>
 * @author Jason Brownlee
 * 
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 
 * </pre>
 */
public class GeneralizedGriewankFunction extends CFOProblem
{

    @Override
    protected double problemSpecificCost(double[] v)
    {
        double s1 = 0.0;
        for (int i = 0; i < dimensions; i++)
        {
            s1 += v[i]*v[i];
        }
        double p1 = 0.0;
        for (int i = 0; i < dimensions; i++)
        {
            double a = Math.cos(v[i] / Math.sqrt(i+1.0));
                      
            if(i == 0)
            {
                p1 = a;
            }
            else
            {
                p1 *= a;
            }
             
        }        
        return (1.0/4000.0) * s1 - p1 + 1.0;
    }

    @Override
    public String getName()
    {
        return "Generalized Griewank Function";
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
            d[i] = new double[]{-600, +600};
        }
        return d;
    }

    @Override
    protected double[][] preapreOptima()
    {
        return new double[1][dimensions]; // single optima at zero for all x
    }
    @Override
    public SUPPORTED_DIMENSIONS [] getSupportDimensionality()
    {
        return new SUPPORTED_DIMENSIONS[]{SUPPORTED_DIMENSIONS.ANY};
    }
}
