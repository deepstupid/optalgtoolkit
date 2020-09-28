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
package com.oat.utils;

/**
 * Type: ArrayUtils<br/>
 * Date: 15/12/2006<br/>
 * <br/>
 * Description:
 * <br/>
 * @author Jason Brownlee
 * 
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 *                          
 * </pre>
 */
public class ArrayUtils
{

    public final static void normalise(double [] d)
    {
        double min = min(d);
        double max = max(d);
        double range = max-min;
        for (int i = 0; i < d.length; i++)
        {
            if(range == 0)
            {
                d[i] = 0;
            }
            else
            {
                d[i] = (d[i]-min) / range;
            }
            
            if(d[i]<0||d[i]>1)
            {
                throw new RuntimeException("Invalid normalization: " + d[i]);
            }
        }
        
        
    }
   
    public final static double min(double [] v)
    {
        double min = v[0];
        for (int i = 1; i < v.length; i++)
        {
            if(v[i] < min)
            {
                min = v[i];
            }
        }
        return min;
    }
    public final static double max(double [] v)
    {
        double max = v[0];
        for (int i = 1; i < v.length; i++)
        {
            if(v[i] > max)
            {
                max = v[i];
            }
        }
        return max;
    }

    

   
    public final static void swap(int i, int j, int [] d)
    {
        int t = d[i];
        d[i] = d[j];
        d[j] = t;
    }
    public final static void swap(int i, int j, byte [] d)
    {
        byte t = d[i];
        d[i] = d[j];
        d[j] = t;
    }
    public final static <T> void swap(int i, int j, T[] d)
    {
        T t = d[i];
        d[i] = d[j];
        d[j] = t;
    }

    
    
    public final static boolean [] copyArray(boolean [] b)
    {
        boolean [] n = new boolean[b.length];
        System.arraycopy(b, 0, n, 0, b.length);
        return n;
    }
    public final static int [] copyArray(int [] b)
    {
        int [] n = new int[b.length];
        System.arraycopy(b, 0, n, 0, b.length);
        return n;
    }
    public final static double [] copyArray(double [] d)
    {
        double [] n = new double[d.length];
        System.arraycopy(d, 0, n, 0, n.length);
        return n;
    }

    
    public final static boolean permutationContainsValue(int [] perm, int length, int val)
    {
        for (int i = 0; i < length; i++)
        {
            if(perm[i] == val)
            {
                return true;
            }
        }
        
        return false;
    }

}

