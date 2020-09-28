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
package com.oat.domains.psp;

/**
 * Type: InvalidModelException<br/>
 * Date: 21/11/2006<br/>
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
public class InvalidModelException extends Exception
{
    protected int collisionLength;
    protected byte [][] lattice;
    
    /**
     * 
     * @param msg
     * @param aLength
     * @param aModel
     */
    public InvalidModelException(
            String msg, 
            int aLength, 
            byte [][] aLattice)
    {
        super(msg);
        collisionLength = aLength;
        lattice = aLattice;
    }

    public int getCollisionLength()
    {
        return collisionLength;
    }

    public byte[][] getLattice()
    {
        return lattice;
    }
}
