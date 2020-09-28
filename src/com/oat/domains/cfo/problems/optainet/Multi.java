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
 * Type: Multi<br/>
 * Date: 16/05/2006<br/>
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
public class Multi extends CFOProblem
{

    @Override
    protected double problemSpecificCost(double[] v)
    {
        // g(x,y) = x.sin(4?x) ? y.sin(4?y+?) + 1, x,y ? [?2,2]        
        double x = v[0];
        double y = v[1];       
        return x * Math.sin(4*Math.PI*x) - y * Math.sin(4*Math.PI*y+Math.PI) + 1;        
    }

    @Override
    public String getName()
    {
        return "Multi Function";
    }

    @Override
    public boolean isMinimization()
    {
        return false;
    }

    @Override
    protected double[][] preapreMinMax()
    {
        double [][] d = new double[dimensions][];
        for (int i = 0; i < d.length; i++)
        {
            d[i] = new double[]{-2, +2};
        }
        return d;
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
