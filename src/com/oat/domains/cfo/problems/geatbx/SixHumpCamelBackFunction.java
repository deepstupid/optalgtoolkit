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
 * Type: SixHumpCamelBackFunction<br/>
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
public class SixHumpCamelBackFunction extends CFOProblem
{

    @Override
    protected double problemSpecificCost(double[] v)
    {
        //fSixh(x1,x2)=(4-2.1·x1^2+x1^4/3)·x1^2+x1·x2+(-4+4·x2^2)·x2^2
        
        double x = v[0];
        double y = v[1];
        
        double a = (4.0-2.1*(x*x)+(x*x*x*x)/3.0) * (x*x)+x*y + (-4+4*(y*y)) * (y*y);
        return a;
    }

    @Override
    protected double[][] preapreMinMax()
    {
        // -3<=x1<=3, -2<=x2<=2.
        return new double[][]{{-3, +3}, {-2, +2}};
    }

    @Override
    protected double[][] preapreOptima()
    {
        // f(x1,x2)=-1.0316; (x1,x2)=(-0.0898,0.7126), (0.0898,-0.7126).
        
        return new double[][]
        {
                {-0.0898,0.7126},
                {0.0898,-0.7126}
        };
    }

    @Override
    public boolean isMinimization()
    {
        return true;
    }

    @Override
    public String getName()
    {
        return "Six-Hump Camel Back Function";
    }
    @Override
    public SUPPORTED_DIMENSIONS [] getSupportDimensionality()
    {
        return new SUPPORTED_DIMENSIONS[]{SUPPORTED_DIMENSIONS.TWO_DIMENSIONAL};
    }
}
