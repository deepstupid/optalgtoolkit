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
 * 
 * Type: AckleysPathFunction10<br/>
 * Date: 10/03/2006<br/>
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
public class AckleysPathFunction10 extends CFOProblem
{

    @Override
    protected double problemSpecificCost(double[] v)
    {
        //   f10(x)=-a·exp(-b·sqrt(1/n·sum(x(i)^2)))-exp(1/n·sum(cos(c·x(i))))+a+exp(1)
        // a=20; b=0.2; c=2·pi; i=1:n; -32.768<=x(i)<=32.768.
        
        double sum1 = 0.0;
        for (int i = 0; i < dimensions; i++)
        {
            sum1 += (v[i] * v[i]);
        }        
        double p1 = Math.exp(-0.2 * Math.sqrt( (1.0/dimensions) * sum1));
       
        double sum2 = 0.0;
        for (int i = 0; i < dimensions; i++)
        {
            sum2 += Math.cos(2.0*Math.PI*v[i]);
        }
        double p2 = Math.exp((1.0/dimensions) * sum2);
        
        double result = -20.0 * p1 - p2 + 20.0 + Math.E;
        return result;
    }

    @Override
    public String getName()
    {
        return "Ackley's Path Function 10";
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
            d[i] = new double[]{-32.768, +32.768};
        }
        return d;
    }

    @Override
    protected double[][] preapreOptima()
    {
        // f(x)=0; x(i)=0, i=1:n.
        
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
