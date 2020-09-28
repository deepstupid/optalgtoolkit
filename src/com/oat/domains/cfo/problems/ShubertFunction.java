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
 * Type: ShubertFunction<br/>
 * Date: 11/04/2006<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 * 
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 
 * </pre>
 */
public class ShubertFunction extends CFOProblem
{

    @Override
    protected double problemSpecificCost(double[] v)
    {
        double result = 1;

        for (int index = 0; index < dimensions; index++)
        {
            double sum = 0;

            for (int i = 1; i <= 5; i++)
            {
                sum += i * Math.cos((i + 1) * v[index] + i);
            }

            result *= sum;
        }

        return -result;
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
        return null;
    }

    @Override
    public boolean isMinimization()
    {
        return true;
    }

    @Override
    public String getName()
    {
        return "Shubert Function";
    }
    
    @Override
    public SUPPORTED_DIMENSIONS [] getSupportDimensionality()
    {
        return new SUPPORTED_DIMENSIONS[]{SUPPORTED_DIMENSIONS.ANY};
    }
}
