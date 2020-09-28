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
 * Type: MichalewiczsFunction<br/>
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
public class MichalewiczsFunction extends CFOProblem
{

    @Override
    protected double problemSpecificCost(double[] v)
    {
        // f12(x)=-sum(sin(x(i))·(sin(i·x(i)^2/pi))^(2·m)), i=1:n, m=10
        // 0<=x(i)<=pi.
        
        double m = 10;
        double sum = 0;
        for (int i = 0; i < dimensions; i++)
        {
            double inner = (i * Math.pow(v[i],2)) / Math.PI;             
            sum += Math.sin(v[i]) * Math.pow(Math.sin(inner), 2*m);
        }
        return -sum;
    }

    @Override
    protected double[][] preapreMinMax()
    {
        double [][] d = new double[dimensions][];
        for (int i = 0; i < d.length; i++)
        {
            d[i] = new double[]{0, Math.PI};
        }
        return d;
    }

    @Override
    protected double[][] preapreOptima()
    {
        // f(x)=-4.687 (n=5); x(i)=???, i=1:n.
        // f(x)=-9.66 (n=10); x(i)=???, i=1:n.
        
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
        return "Michalewicz's Function 12";
    }
    @Override
    public SUPPORTED_DIMENSIONS [] getSupportDimensionality()
    {
        return new SUPPORTED_DIMENSIONS[]{SUPPORTED_DIMENSIONS.ANY};
    }
}
