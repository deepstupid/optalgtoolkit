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
 * Type: MovedAxisParalleHyper_EllipsoidFunction<br/>
 * Date: 30/11/2006<br/>
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
public class MovedAxisParalleHyper_EllipsoidFunction extends CFOProblem
{

    @Override
    protected double problemSpecificCost(double[] v)
    {
        // f1c(x)=sum(5*i·x(i)^2), i=1:n, -5.12<=x(i)<=5.12.
        
        double sum = 0.0;
        
        for (int i = 0; i < dimensions; i++)
        {
            sum += 5.0 * i *Math.pow(v[i], 2);
        }
        
        return sum;
    }

    @Override
    protected double[][] preapreMinMax()
    {
        double [][] d = new double[dimensions][];
        for (int i = 0; i < d.length; i++)
        {
            d[i] = new double[]{-5.12, +5.12};
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
        return "Moved axis parallel hyper-ellipsoid function";
    }
    @Override
    public SUPPORTED_DIMENSIONS [] getSupportDimensionality()
    {
        return new SUPPORTED_DIMENSIONS[]{SUPPORTED_DIMENSIONS.ANY};
    }
}
