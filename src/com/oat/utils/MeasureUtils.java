/*
Optimization Algorithm Toolkit (OAT)
http://sourceforge.net/projects/optalgtoolkit
Copyright (C) 2007  Jason Brownlee

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
package com.oat.utils;

/**
 * Type: MeasureUtils<br/>
 * Date: 05/07/2007<br/>
 * <br/>
 * Description: Collection of measure utilities
 * <br/>
 * @author Jason Brownlee
 *
 * 
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 
 * </pre>
 *
 */
public class MeasureUtils
{
    /**
     * Calculate the Root Mean Squared Error (RMSE)
     * @param errors
     * @return - the RMS
     */
    public static double calculateRMSE(double [] errors)
    {
        double sum = 0.0;
        for (int i = 0; i < errors.length; i++)
        {
            sum += (errors[i] * errors[i]);
        }
        return Math.sqrt(sum);
    }
    /**
     * Calculate the Average Error 
     * @param errors - presumes all errors a positive [0,infinity]
     * @return - the SE
     */
    public static double calculateAE(double [] errors)
    {
        double sum = 0.0;
        for (int i = 0; i < errors.length; i++)
        {
            sum += errors[i];
        }
        return (sum==0.0) ? 0.0 : sum/errors.length;
    }
}
