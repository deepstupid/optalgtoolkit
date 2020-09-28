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
package com.oat;

import java.util.LinkedList;

/**
 * Type: AlgorithmIterationCompleteListener<br/>
 * Date: 10/03/2006<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 * 
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 27/12/2006   JBrownlee   Refactored the method name
 * 06/01/2007   JBrownlee   Changed to not accept the best solution ever as a parameter
 * 
 * </pre> 
 */
public interface AlgorithmEpochCompleteListener
{
   
    /**
     * Notify a listener that an algorithm iteration has completed
     * Uses type erasure to ensure all solutions are of the same type
     * @param <T>
     * @param p
     * @param currentPop
     */
    <T extends Solution> void epochCompleteEvent(Problem p, LinkedList<T> currentPop);
}
