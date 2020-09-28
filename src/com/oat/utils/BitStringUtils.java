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

import com.oat.AlgorithmRunException;
import com.oat.domains.bfo.BFOSolution;



/**
 * Type: BitStringUtils<br/>
 * Date: 24/03/2006<br/>
 * <br/>
 * Description: Utilities related to bit strings and binary representations
 * <br/>
 * @author Jason Brownlee
 * 
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 08/07/2007   JBrownlee   Added function to convert a collection of bit strings to a string 
 * </pre>
 */
public class BitStringUtils
{
    
  
    
    /**
     * Convert the provided binary vector to a string in the format
     * 01010100101011
     * @param b
     * @return
     */
    public final static String toString(boolean [] b)
    {        
        char [] c = new char[b.length];
        for (int i = 0; i < c.length; i++)
        {
            c[i] = (b[i]) ? '1' : '0';
        }
        return new String(c);
    }
    
    /**
     * Converts the provided collection of bit strings to string
     * [###,###,...]
     * @param b
     * @return
     */
    public final static String toString(boolean [][] b)
    {
        StringBuffer buffer = new StringBuffer(b.length*b[0].length);
        buffer.append("[");
        for (int i = 0; i < b.length; i++)
        {
            buffer.append(toString(b[i]));
            if(i!=b.length-1)
            {
                buffer.append(",");
            }
        }
        buffer.append("]");
        return buffer.toString();
    }
    
    /**
     * Converts the provided collection of bit strings to string
     * {[###,###,...],[...]}
     * @param b
     * @return
     */
    public final static String toString(boolean [][][] b)
    {
        StringBuffer buffer = new StringBuffer(b.length*b[0].length*b[0][0].length);
        buffer.append("{");
        for (int i = 0; i < b.length; i++)
        {
            buffer.append(toString(b[i]));
            if(i!=b.length-1)
            {
                buffer.append(",");
            }
        }
        buffer.append("}");
        return buffer.toString();
    }
    
    
    /**
     * Decode the provided binary vector into a double coordinate in the specified domain
     * 
     * @param mode - the manner in which to conver the string into doubles
     * @param b - the string to decode
     * @param minmax - bounds of the coordinate space [] = each dimension [i][2] = {min,max} format
     * @return double [] coordinate within the bounds of the domain
     */
    public final static double [] decode(BinaryDecodeMode mode, boolean [] b, double [][] minmax)
    {
        int d = minmax.length;                        
        if(d>1 && (b.length % d) != 0)
        {
            throw new AlgorithmRunException("Unable to evenly divide "+b.length+" bits into "+ d);
        }
        
        // determine the even division of bits
        double [] coord = new double[d];
        int bitsPerCoord = b.length / coord.length;        
        
        // process each phenotypic value
        for (int i = 0, offset = 0; i < coord.length; i++, offset+=bitsPerCoord)
        {
            switch(mode)
            {
                case Binary:
                {
                    coord[i] = binaryBitsToDouble(b, offset, bitsPerCoord, minmax[i][0], minmax[i][1]);
                    break;
                }
                case GrayCode:
                {
                    coord[i] = grayBitsToDouble(b, offset, bitsPerCoord, minmax[i][0], minmax[i][1]);
                    break;
                }
                default:
                {
                    throw new AlgorithmRunException("Invalid decode mode!");
                }
            }
        }
        
        return coord;
    }
    
    /**
     * decode a single binary bitstring
     * @param b
     * @param offset
     * @param length
     * @param min
     * @param max
     * @return
     */
    public static double binaryBitsToDouble(boolean [] b, int offset, int length, double min, double max)
    {
        // do the sum
        double sum = 0.0;
        for (int i = 0, o = offset; i < length; i++, o++)
        {
            sum += (b[o] ? 1.0 : 0.0) * Math.pow(2.0, i);
        }
        // do the division
        double div = (max-min) / (Math.pow(2, length) - 1);
        return min + div * sum;
    }
    
    /**
     * Decode a single gray code string
     * @param b
     * @param offset
     * @param length
     * @param min
     * @param max
     * @return
     */
    public static double grayBitsToDouble(boolean [] b, int offset, int length, double min, double max)
    {        
        // do the sum
        double sum = 0.0;
        for (int i = 0; i < length; i++)        
        {
            // modulo addition to current position
            double mod = 0.0;
            for (int j = 0, o = offset; j<=i; j++, o++)
            {
                mod += (b[o] ? 1.0 : 0.0);
            }
            
            sum += (mod%2) * Math.pow(2.0, i);            
        }
        // do the division
        double div = (max-min) / (Math.pow(2, length) - 1);
        return min + div * sum;
    }    

	/**
     * Testing 
	 */
	public static void main(String[] args)
	{
        // for testing
        /**
            Dec  Gray   Binary
             0   000    000
             1   001    001
             2   011    010
             3   010    011
             4   110    100
             5   111    101
             6   101    110
             7   100    111
         */        
        
	    // 7
        boolean [] bc7 = {true,true,true};
        System.out.println(binaryBitsToDouble(bc7, 0, bc7.length, 0, Math.pow(2,bc7.length)-1));
        
        // 7
        boolean [] gc7 = {true,false,false};
        System.out.println(grayBitsToDouble(gc7, 0, gc7.length, 0, Math.pow(2,gc7.length)-1));        
	}


    /**
     * Calculate the hamming distance between the two binary strings and return 
     * the result as a ratio of the string length: (distance / length)
     * @param b1
     * @param b2
     * @return
     */
    public final static double hammingDistanceRatio(boolean [] b1, boolean [] b2)
    {         
        return (BitStringUtils.hammingDistance(b1,b2) / b1.length);
    }

    /**
     * Calculate the hamming distance between the two binary strings
     * @param b1
     * @param b2
     * @return
     */
    public final static double hammingDistance(boolean [] b1, boolean [] b2)
    {        
        return hammingDistance(b1, 0, b1.length, b2, 0, b2.length);
    }
    
    /**
     * Calculate the hamming distance between the two binary solutions
     * @param b1
     * @param b2
     * @return
     */
    public final static double hammingDistance(BFOSolution b1, BFOSolution b2)
    {        
        return hammingDistance(b1.getBitString(), b2.getBitString());
    }
    
    /**
     * Calculate the hamming distance between the two binary strings
     * using the specified offsets and lengths, assumes the strings are of equal length
     * Number of differences
     * 
     * @param b1
     * @param s1
     * @param l1
     * @param b2
     * @param s2
     * @param l2
     * @return
     */
    public final static double hammingDistance(
            boolean [] b1,
            int s1,
            int l1,
            boolean [] b2,
            int s2, 
            int l2)
    {                
        int diff = 0;        
        for (int i = s1, j=s2; i<(s1+l1) && j<(s2+l2); i++, j++)
        {
            if(b1[i] != b2[j])
            {
                diff++;
            }
        }
        
        return diff;
    }


    /**
     * Calculates the unitation (number of 1's) of the provided bit string
     * @param b
     * @return
     */
    public final static int unitation(boolean [] b)
    {
        return BitStringUtils.unitation(b,0,b.length);
    }

    /**
     * Calculates the unitation (number of 1's) of the provided bit string
     * with specified start and length
     * @param b
     * @param start
     * @param length
     * @return
     */
    public final static int unitation(boolean [] b, int start, int length)
    {
        int count = 0;
        for (int i = start; i < start+length; i++)
        {
            if(b[i])
            {
                count++;
            }
        }
        return count;
    }
    
    
    
}