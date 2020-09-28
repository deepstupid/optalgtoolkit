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
 * Type: GoldsteinPricesFunction<br/>
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
public class GoldsteinPricesFunction extends CFOProblem
{

    @Override
    protected double problemSpecificCost(double[] v)
    {
        //   fGold(x1,x2)=[1+(x1+x2+1)^2·(19-14·x1+3·x1^2-14·x2+6·x1·x2+3·x2^2)]·
        //[30+(2·x1-3·x2)^2·(18-32·x1+12·x1^2+48·x2-36·x1·x2+27·x2^2)]
        
        return (1 + Math.pow(v[0]+v[1]+1,2) * (19-14*v[0]+3*Math.pow(v[0],2)-14*v[1]+6*v[0]*v[1]+3*Math.pow(v[1],2)))
        * Math.pow(30 + (2*v[0]-3*v[1]), 2)*(18-32*v[1]+12*Math.pow(v[0],2)+48*v[1]-36*v[0]*v[1]+27*Math.pow(v[1],2));
    }

    @Override
    protected double[][] preapreMinMax()
    {
        // -2<=x(i)<=2, i=1:2.
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
        // f(x1,x2)=3; (x1,x2)=(0,-1).
        
        // wrong for sure!
        //return new double[][]{{0,-1}};
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
        return "Goldstein-Price's function";
    }
    @Override
    public SUPPORTED_DIMENSIONS [] getSupportDimensionality()
    {
        return new SUPPORTED_DIMENSIONS[]{SUPPORTED_DIMENSIONS.TWO_DIMENSIONAL};
    }
}
