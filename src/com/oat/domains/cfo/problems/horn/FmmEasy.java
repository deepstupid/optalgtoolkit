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

import com.oat.domains.cfo.CFOProblem;

/**
 * Type: FmmEasy<br/>
 * Date: 05/12/2006<br/>
 * <br/>
 * Description: As specified in: Genetic Algorithm Difficulty and the Modality of Fitness Landscapes (1994)
 * <br/>
 * @author Jason Brownlee
 * 
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 
 * </pre>
 */
public class FmmEasy extends CFOProblem
{

    @Override
    protected double problemSpecificCost(double[] v)
    {
        double sum = v[0] + v[1];
        if((sum%2.0) == 0.0)
        {
            sum += 10.0;
        }
        return sum;
    }

    @Override
    public SUPPORTED_DIMENSIONS[] getSupportDimensionality()
    {
        return new SUPPORTED_DIMENSIONS[]{SUPPORTED_DIMENSIONS.TWO_DIMENSIONAL};
    }

    @Override
    protected double[][] preapreMinMax()
    {
        return new double[][]{{0,20},{0,20}};
    }

    @Override
    protected double[][] preapreOptima()
    {
        return new double[][]{{20,20}};
    }

    @Override
    public String getName()
    {
        return "Horn's fmmEasy";
    }

    @Override
    public boolean isMinimization()
    {
        return false;
    }
}
