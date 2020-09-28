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
 * Type: BraninssRcosFunction<br/>
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
public class BraninssRcosFunction extends CFOProblem
{

    @Override
    protected double problemSpecificCost(double[] v)
    {
        //   fBran(x1,x2)=a·(x2-b·x1^2+c·x1-d)^2+e·(1-f)·cos(x1)+e
        // a=1, b=5.1/(4·pi^2), c=5/pi, d=6, e=10, f=1/(8·pi)
        // -5<=x1<=10, 0<=x2<=15.

        double a = 1;
        double b = 5.1 / (4*Math.pow(Math.PI, 2));
        double c = 5 / Math.PI;
        double d = 6;
        double e = 10;
        double f = 1 / (8 * Math.PI);
        
        return a*Math.pow(v[1]-b*Math.pow(v[0],2)+c*v[0]-d,2)+e*(1-f)*Math.cos(v[0])+e;
    }

    @Override
    protected double[][] preapreMinMax()
    {
        // -5<=x1<=10, 0<=x2<=15
        return new double[][]{{-5, +10}, {0, +15}};
    }

    @Override
    protected double[][] preapreOptima()
    {
        // f(x1,x2)=0.397887; (x1,x2)=(-pi,12.275), (pi,2.275), (9.42478,2.475).
        
        return new double[][]
        {
                {-Math.PI,12.275},
                {Math.PI,2.275},
                {9.42478,2.475}
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
        return "Branins's Rcos Function";
    }
    
    @Override
    public SUPPORTED_DIMENSIONS [] getSupportDimensionality()
    {
        return new SUPPORTED_DIMENSIONS[]{SUPPORTED_DIMENSIONS.TWO_DIMENSIONAL};
    }
}
