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
package com.oat.domains.cfo.problems.timmis;

import com.oat.domains.cfo.CFOProblem;

/**
 * Type: F1<br/>
 * Date: 16/05/2006<br/>
 * <br/>
 * Description: Assessing the performance of two immune inspired algorithms and a hybrid genetic algorithm for function optimisation (2004)
 * 
 * <br/> 
 * @author Jason Brownlee
 * 
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 
 * </pre>
 */
public class F1 extends CFOProblem
{
    public F1()
    {
        setDimensions(1);
    }    
    
    @Override
    protected double problemSpecificCost(double[] v)
    {        
        double s = (5*Math.PI*v[0] == 0.4*Math.PI) ? 1 : 0;
        return 2*Math.pow(v[0]-0.75,2.0)+Math.sin(s)-0.125;        
    }

    @Override
    public String getName()
    {
        return "F1";
    }

    @Override
    public boolean isMinimization()
    {
        return true;
    }

    @Override
    protected double[][] preapreMinMax()
    {
        return new double[][]{{0,1}};
    }

    @Override
    protected double[][] preapreOptima()
    {
        return null;
    }    
    @Override
    public SUPPORTED_DIMENSIONS [] getSupportDimensionality()
    {
        return new SUPPORTED_DIMENSIONS[]{SUPPORTED_DIMENSIONS.ONE_DIMENSIONAL};
    }
}
