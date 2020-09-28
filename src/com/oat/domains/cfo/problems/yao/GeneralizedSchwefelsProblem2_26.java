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
 * Type: GeneralizedSchwefelsProblem2_26<br/>
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
public class GeneralizedSchwefelsProblem2_26 extends CFOProblem
{

    @Override
    protected double problemSpecificCost(double[] v)
    {
        double sum = 0.0;
        for (int i = 0; i < dimensions; i++)
        {
            sum += v[i] * Math.sin(Math.sqrt(Math.abs(v[i])));
        }
        return -sum;
    }

    @Override
    public String getName()
    {
        return "Generalized Schwefels Problem 2.26";
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
            d[i] = new double[]{-500, +500};
        }
        return d;
    }

    @Override
    protected double[][] preapreOptima()
    {
        double [][] d = new double[1][dimensions];
        for (int i = 0; i < dimensions; i++)
        {
            d[0][i] = 420.9687;
        }
        return d;
    }
    @Override
    public SUPPORTED_DIMENSIONS [] getSupportDimensionality()
    {
        return new SUPPORTED_DIMENSIONS[]{SUPPORTED_DIMENSIONS.ANY};
    }
}
