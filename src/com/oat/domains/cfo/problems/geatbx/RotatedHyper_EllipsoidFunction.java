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
package com.oat.domains.cfo.problems.geatbx;

import com.oat.domains.cfo.CFOProblem;

/**
 * Type: RotatedHyper_EllipsoidFunction<br/>
 * Date: 11/03/2006<br/>
 * <br/>
 * Description: geatbx (http://www.geatbx.com/docu/fcnindex-01.html)
 * <br/>
 * @author Jason Brownlee
 * 
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 
 * </pre>
 */
public class RotatedHyper_EllipsoidFunction extends CFOProblem
{

    @Override
    protected double problemSpecificCost(double[] v)
    {
        //f1b(x)=sum(sum(x(j)^2), j=1:i), i=1:n, -65.536<=x(i)<=65.536.
        
        double sum = 0.0;
        
        for (int i = 0; i < dimensions; i++)
        {
            for (int j = 0; j < i; j++)
            {
                sum += Math.pow(v[j], 2);
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
            d[i] = new double[]{-65.536, +65.536};
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
        return true;
    }

    @Override
    public String getName()
    {
        return "Rotated hyper-ellipsoid function";
    }
    @Override
    public SUPPORTED_DIMENSIONS [] getSupportDimensionality()
    {
        return new SUPPORTED_DIMENSIONS[]{SUPPORTED_DIMENSIONS.ANY};
    }
}
