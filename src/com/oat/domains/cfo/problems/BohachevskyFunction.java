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
 * 
 * Type: BohachevskyFunction<br/>
 * Date: 16/05/2006<br/>
 * <br/>
 * Description: Evaluating the CMA Evolution Strategy on Multimodal Test Functions (2004)
 * <br/>
 * @author Jason Brownlee
 * 
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 
 * </pre>
 */
public class BohachevskyFunction extends CFOProblem
{

    @Override
    protected double problemSpecificCost(double[] v)
    {
        double sum = 0.0;
        for (int i = 0; i < dimensions-1; i++)
        {
            sum += Math.pow(v[i],2) + 2.0*Math.pow(v[i+1],2) -0.3*Math.cos(3.0*Math.PI*v[i])-0.4*Math.cos(4.0*Math.PI*v[i+1])+0.7;
        }        
        return sum;
    }

    @Override
    public String getName()
    {
        return "Bohachevsky's Function";
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
            d[i] = new double[]{-100, +100};
        }
        return d;
    }

    @Override
    protected double[][] preapreOptima()
    {
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
