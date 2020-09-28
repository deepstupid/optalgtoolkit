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
package com.oat.domains.cfo.problems.optainet;

import com.oat.domains.cfo.CFOProblem;
import com.oat.utils.Complex;

/**
 * Type: Roots<br/>
 * Date: 16/05/2006<br/>
 * <br/>
 * Description: taken from Petrowski - A New Selection Operator Dedicated to Speciation (1997)
 * <br/>
 * @author Jason Brownlee
 * 
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 
 * </pre>
 */
public class Roots extends CFOProblem
{

    @Override
    protected double problemSpecificCost(double[] v)
    {
       double x = v[0];
       double y = v[1];
       Complex z1 = new Complex(1,0);
       Complex z2 = new Complex(x,y);
       Complex z = new Complex(x,y);
       for (int i = 0; i <= 4; i++) {
    	 z2 = z2.times(z);  
       }
       z2 = z2.minus(z1);      
       return 1.0 / (1.0 + z2.mod());
    }

    @Override
    public String getName()
    {
        return "Roots";
    }

    @Override
    public boolean isMinimization()
    {
        return false;
    }

    @Override
    protected double[][] preapreMinMax()
    {
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
//        double [][] d = new double[1][dimensions];
//        for (int i = 0; i < d[0].length; i++)
//        {
//            d[0][i] = 0;
//        }
//        return d;
        
        return null;
    }
    @Override
    public SUPPORTED_DIMENSIONS [] getSupportDimensionality()
    {
        return new SUPPORTED_DIMENSIONS[]{SUPPORTED_DIMENSIONS.TWO_DIMENSIONAL};
    }
}
