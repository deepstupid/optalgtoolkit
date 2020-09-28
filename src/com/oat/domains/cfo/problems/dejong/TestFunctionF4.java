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

import java.util.Random;

import com.oat.domains.cfo.CFOProblem;


/**
 * Type: TestFunctionF4<br/>
 * Date: 10/03/2006<br/>
 * <br/>
 * Description: An analysis of the behavior of a class of genetic adaptive sysetms (De Jong) (1975)
 * <br/>
 * @author Jason Brownlee
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 
 * </pre>
 */
public class TestFunctionF4 extends CFOProblem
{
    protected Random r;
    
    public TestFunctionF4()
    {
        r = new Random();
    }
    

    @Override
    protected double problemSpecificCost(double[] v)
    {
        double sum = 0.0;
        
        for (int i = 0; i < dimensions; i++)
        {
            for (int j = 0; j < 30; j++)
            {
                sum += (i * Math.pow(v[i], 4) + r.nextGaussian());
            }
        }
        
        return sum;
    }

    @Override
    protected double[][] preapreMinMax()
    {
        double [][] d = new double[dimensions][];
        for (int i = 0; i < d.length; i++)
        {
            d[i] = new double[]{-1.28, +1.28};
        }
        return d;
    }

    @Override
    protected double[][] preapreOptima()
    {
        // TODO Auto-generated method stub
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
        return "Test Function F4 (Quartic Function)";
    }
    @Override
    public SUPPORTED_DIMENSIONS [] getSupportDimensionality()
    {
        return new SUPPORTED_DIMENSIONS[]{SUPPORTED_DIMENSIONS.ANY};
    }
}
