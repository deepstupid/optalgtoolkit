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
 * Type: EasomsFunction<br/>
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
public class EasomsFunction extends CFOProblem
{

    @Override
    protected double problemSpecificCost(double[] v)
    {
        // fEaso(x1,x2)=-cos(x1)·cos(x2)·exp(-((x1-pi)^2+(x2-pi)^2))
        
        return -Math.cos(v[0])*Math.cos(v[1])*Math.exp(-(Math.pow(v[0]-Math.PI,2)+Math.pow(v[1]-Math.PI,2)));
    }

    @Override
    protected double[][] preapreMinMax()
    {
        // -100<=x(i)<=100, i=1:2.
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
        // f(x1,x2)=-1; (x1,x2)=(pi,pi).
        return new double[][]{{Math.PI,Math.PI}};
    }

    @Override
    public boolean isMinimization()
    {
        return true;
    }

    @Override
    public String getName()
    {
        return "Easom's function";
    }
    @Override
    public SUPPORTED_DIMENSIONS [] getSupportDimensionality()
    {
        return new SUPPORTED_DIMENSIONS[]{SUPPORTED_DIMENSIONS.TWO_DIMENSIONAL};
    }
}
