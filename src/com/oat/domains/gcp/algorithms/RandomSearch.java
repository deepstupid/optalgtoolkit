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
package com.oat.domains.gcp.algorithms;

import java.util.Random;

import com.oat.Problem;
import com.oat.algorithms.GenericRandomSearchAlgorithm;
import com.oat.domains.gcp.GCPSolution;
import com.oat.domains.gcp.GCPUtils;
import com.oat.domains.gcp.GCProblem;

/**
 * Type: RandomSearch<br/>
 * Date: 11/12/2006<br/>
 * <br/>
 * Description: Random solution generation for GCP
 * <br/>
 * @author Jason Brownlee
 * 
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 22/12/2006   JBrownlee   Random moved to method variable rather than instance variable
 * 07/09/2007	JBrownlee	Updated to use a common ancestor
 * </pre>
 */
public class RandomSearch extends GenericRandomSearchAlgorithm<GCPSolution>
{    	
    @Override
	protected GCPSolution generateRandomSolution(Random rand, Problem problem)
	{
    	return new GCPSolution(GCPUtils.generateRandomSolution((GCProblem)problem, rand));
	}
}
