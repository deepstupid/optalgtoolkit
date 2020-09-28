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
package com.oat.domains.cfo.problems.horn;

import java.util.Arrays;

import com.oat.domains.cfo.CFOProblem;


/**
 * Type: HornsFivePeaks<br/>
 * Date: 16/05/2006<br/>
 * <br/>
 * Description: taken from Genetic Algorithm Difficulty and the Modality of Fitness Landscapes - Horn, Goldberg 1994
 * <br/>
 * @author Daniel Angus
 * 
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 
 * </pre>
 */
public class ModifiedHornsFivePeaks extends CFOProblem
{
    
    public final static double[][] G = new double[][]{{7,59},{5,21},{30,7},{62,3},{62,51}};

    @Override
    protected double problemSpecificCost(double[] v)
    {
        double[] costs = new double[5];
        for (int i = 0; i < costs.length; i++)
        {
            costs[i] = Math.sqrt(1.0 + Math.sqrt(Math.pow(G[i][0] - v[X], 2.0) + Math.pow(G[i][1] - v[Y], 2.0)));
        }
        Arrays.sort(costs);
        for (int i = 0; i < G.length; i++)
        {
            if (Math.sqrt(Math.pow((v[X] - G[i][0]), 2.0) + Math.pow((v[Y] - G[i][1]), 2.0)) < 2.0)
            {
                return 10.0 / (costs[0]);
            }
        }
        return costs[0];
    }

    @Override
    public String getName()
    {
        return "Horns 5 Peaks (modified)";
    }

    @Override
    public boolean isMinimization()
    {
        return false;
    }

    @Override
    protected double[][] preapreMinMax()
    {
        return new double[][]{{0.00, +65.00},{0.00, +65.00}};
    }

    @Override
    protected double[][] preapreOptima()
    {      
    	return new double[][]{{7,59},{5,21},{30,7},{62,3},{62,51}};
    }
    
    @Override
    public SUPPORTED_DIMENSIONS [] getSupportDimensionality()
    {
        return new SUPPORTED_DIMENSIONS[]{SUPPORTED_DIMENSIONS.TWO_DIMENSIONAL};
    }
}
