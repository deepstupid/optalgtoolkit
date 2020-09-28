/*
Optimization Algorithm Toolkit (OAT)
http://sourceforge.net/projects/optalgtoolkit
Copyright (C) 2007  Jason Brownlee

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

import java.util.Arrays;

import com.oat.domains.cfo.CFOProblem;

/**
 * Type: GeneralizedPenalizedFunction2<br/>
 * Date: 09/01/2007<br/>
 * <br/>
 * Description: As specified in: Evolutionary programming made faster (1999)
 * <br/>
 * @author Jason Brownlee
 *
 * 
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 
 * </pre>
 *
 */
public class GeneralizedPenalizedFunction2 extends CFOProblem
{

    @Override
    public SUPPORTED_DIMENSIONS[] getSupportDimensionality()
    {
        return new SUPPORTED_DIMENSIONS[]{SUPPORTED_DIMENSIONS.ANY};
    }

    @Override
    protected double[][] preapreMinMax()
    {
        double [][] d = new double[dimensions][];
        for (int i = 0; i < d.length; i++)
        {
            d[i] = new double[]{-50, +50};
        }
        return d;
    }

    @Override
    protected double[][] preapreOptima()
    {
        double[][] o = new double[1][dimensions]; // single optima at 1 for all x
        Arrays.fill(o[0], 1);
        return o;
    }

    @Override
    protected double problemSpecificCost(double[] v)
    {
        double p1 = 0.1;
        double p2 = Math.pow(Math.sin(Math.PI*3*v[0]), 2);
        double p3 = 0.0;
        for (int i = 0; i < dimensions-1; i++)
        {
            p3 += Math.pow(y(v[i]) - 1, 2) * (1 + Math.pow(Math.sin(3*Math.PI*v[i+1]),2)) + Math.pow(v[dimensions-1]-1, 2) * (1 + Math.pow(Math.sin(2*Math.PI*v[dimensions-1]),2));
        }
        double p4 = 0.0;
        for (int i = 0; i < dimensions; i++)
        {
            p4 += u(v[i],5,100,4);
        }
        return p1 * (p2 + p3) + p4;
    }

    public double u(double x, double a, double k, double m)
    {
        if(x > a)
        {
            return k * Math.pow(x-a, m);
        }
        else if(x < -a)
        {
            return k * Math.pow(-x-a, m);
        }
        return 0; // -a <= x <= a
    }
    
    public double y(double x)
    {
        return 1 + 1/4 * (x + 1);
    }
    
    @Override
    public String getName()
    {
        return "Generalized Penalized Function 2";
    }

    @Override
    public boolean isMinimization()
    {
        return true;
    }
}
