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
package com.oat.domains.cfo.problems.clonalg;

import com.oat.domains.cfo.CFOProblem;

/**
 * Type: G3<br/>
 * Date: 14/03/2006<br/>
 * <br/>
 * Description:
 * 
 * From: Learning and optimization using the clonal selection principle (2002)
 * - added the optima, mentioned in the tech report:
 * Leandro N. de Castro and Fernando José Von Zuben. Artificial Immune Systems - Part I: Basic Theory and Applications [Technical Report].  Brazil: Department of Computer Engineering and Industrial Automation, School of Electrical and Computer Engineering, State University of Campinas; 1999 Dec; TR DCA 01/99. 
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
public class G3 extends CFOProblem
{

    @Override
    protected double problemSpecificCost(double[] v)
    {
        return v[X] * Math.sin(4*Math.PI*v[X]) - v[Y]*Math.sin(4*Math.PI*v[Y]+Math.PI) + 1;
    }

    @Override
    protected double[][] preapreMinMax()
    {
        return new double[][]                            
        {
                {-1, +2},
                {-1, +2}
        };
    }

    @Override
    protected double[][] preapreOptima()
    {
        return new double[][]{{1.63,1.63}};
    }

    @Override
    public boolean isMinimization()
    {
        return false;
    }

    @Override
    public String getName()
    {
        return "G3";
    }
    
    @Override
    public SUPPORTED_DIMENSIONS [] getSupportDimensionality()
    {
        return new SUPPORTED_DIMENSIONS[]{SUPPORTED_DIMENSIONS.TWO_DIMENSIONAL};
    }
}
