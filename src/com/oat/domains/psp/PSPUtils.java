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

import java.util.Random;

import com.oat.AlgorithmRunException;

/**
 * Type: ProtFoldUtils<br/>
 * Date: 07/12/2006<br/>
 * <br/>
 * Description: Utility functions relevant to the protein folding domain
 * <br/>
 * @author Jason Brownlee
 * 
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 
 * </pre>
 */
public class PSPUtils
{    
    public final static int NORTH = 1, SOUTH = 2, EAST = 3, WEST = 4;
    
    
    public final static PSPSolutionAbsolute generateRandomAbsSolution(PSPProblem p, Random r)
    {
        byte [] perm = new byte[p.getDataset().length - 1];
        for (int i = 0; i < perm.length; i++)
        {
            perm[i] = (byte) (r.nextInt(4)+1);
        }
        return new PSPSolutionAbsolute(perm);
    }
    
    /**
     * Convert a permutation of absolute lattice directions into a conformation on a 2D lattice.
     * 
     * @param aPermutation
     * @param dataset
     * @return
     * @throws InvalidModelException
     */
    public final static byte [][] absolutePermutationToLattice(byte [] aPermutation, boolean [] dataset)
        throws InvalidModelException
    {
        int latticeWidth = 2*(((aPermutation.length%2)==0) ? aPermutation.length : aPermutation.length + 1); 
        byte [][] lattice = new byte [latticeWidth][latticeWidth]; // square lattice
        int [] coord = {latticeWidth/2,latticeWidth/2};        
        // lay down the first value
        lattice[coord[0]][coord[1]] = PSPProblem.val(dataset[0]);
        // step through the path
        for (int i = 0; i < aPermutation.length; i++)
        {
            // update the coordinate
            updateCoord(coord, aPermutation[i]);
            // check for collision
            if(lattice[coord[0]][coord[1]] != PSPProblem.EMPTY)
            {
                throw new InvalidModelException("Collision", i, lattice);
            }
            lattice[coord[0]][coord[1]] = PSPProblem.val(dataset[i+1]);
        }        
        return lattice;
    }
    
    
    /**
     * Check that the absolute permutation is valid
     * @param aPermutation
     * @param dataset
     */
    public final static void isValidAbsolutePermutation(byte [] aPermutation, boolean [] dataset)
    {
        if(aPermutation.length != dataset.length-1)
        {
            throw new AlgorithmRunException("Permutation length "+aPermutation.length+" does not match expected " + (dataset.length-1));
        }
        for (int i = 0; i < aPermutation.length; i++)
        {
            if(aPermutation[i]<NORTH||aPermutation[i]>WEST)
            {
                throw new AlgorithmRunException("Invalid permutation value " + aPermutation[i]);
            }
        }
    }
    
    /**
     * Convert an absolute permutation to a human-readable string
     * @param permutation
     * @return
     */
    public static String absolutePermutationToString(byte [] permutation)
    {
        char [] c = new char[permutation.length];
        
        for (int i = 0; i < c.length; i++)
        {
            switch(permutation[i])
            {
                case NORTH:                
                {
                    c[i] = 'N';
                    break;
                }
                case SOUTH:
                {
                    c[i] = 'S';
                    break;
                }
                case EAST:
                {
                    c[i] = 'E';
                    break;
                }
                case WEST:
                {
                    c[i] = 'W';
                    break;
                }
                default:
                {
                    throw new AlgorithmRunException("Invalid permutation value " + permutation[i]);
                }                    
            }
        }
        
        return new String(c);
    }
    
    
    public final static int FOWARD = 1, LEFT = 2, RIGHT = 3;
    
    /**
     * Generate a random relative solution
     * @param p
     * @param r
     * @return
     */
    public final static PSPSolutionRelative generateRandomRelSolution(PSPProblem p, Random r)
    {
        byte [] perm = new byte[p.getDataset().length - 1];
        for (int i = 0; i < perm.length; i++)
        {
            perm[i] = (byte) (r.nextInt(3)+1);
        }
        return new PSPSolutionRelative(perm);
    }
    
    /**
     * Convert a permutation of relative lattice directions into a conformation on a 2D lattice.
     * 
     * @param aPermutation
     * @param dataset
     * @return
     * @throws InvalidModelException
     */
    public final static byte [][] relativePermutationToLattice(byte [] aPermutation, boolean [] dataset)
        throws InvalidModelException
    {
        int latticeWidth = 2*(((aPermutation.length%2)==0) ? aPermutation.length : aPermutation.length + 1); 
        byte [][] lattice = new byte [latticeWidth][latticeWidth]; // square lattice        
        int [] coord = {latticeWidth/2,latticeWidth/2};        
        // lay down the first value
        lattice[coord[0]][coord[1]] = PSPProblem.val(dataset[0]);
        // step through the path
        int currDir = NORTH;
        for (int i = 0; i < aPermutation.length; i++)
        {
            switch(currDir)
            {
                case NORTH:
                {
                    switch(aPermutation[i])
                    {
                        case FOWARD:
                        {
                            updateCoord(coord, NORTH);
                            currDir = NORTH;
                            break;
                        }
                        case LEFT:
                        {
                            updateCoord(coord, WEST);
                            currDir = WEST;
                            break;
                        }
                        case RIGHT:
                        {
                            updateCoord(coord, EAST);
                            currDir = EAST;
                            break;
                        }
                    }
                    break;
                }
                case SOUTH:
                {
                    switch(aPermutation[i])
                    {
                        case FOWARD:
                        {
                            updateCoord(coord, SOUTH);
                            currDir = SOUTH;
                            break;
                        }
                        case LEFT:
                        {
                            updateCoord(coord, EAST);
                            currDir = EAST;
                            break;
                        }
                        case RIGHT:
                        {
                            updateCoord(coord, WEST);
                            currDir = WEST;
                            break;
                        }
                    }
                    break;
                }
                case EAST:
                {
                    switch(aPermutation[i])
                    {
                        case FOWARD:
                        {
                            updateCoord(coord, EAST);
                            currDir = EAST;
                            break;
                        }
                        case LEFT:
                        {
                            updateCoord(coord, NORTH);
                            currDir = NORTH;
                            break;
                        }
                        case RIGHT:
                        {
                            updateCoord(coord, SOUTH);
                            currDir = SOUTH;
                            break;
                        }
                    }
                    break;
                }
                case WEST:
                {
                    switch(aPermutation[i])
                    {
                        case FOWARD:
                        {
                            updateCoord(coord, WEST);
                            currDir = WEST;
                            break;
                        }
                        case LEFT:
                        {
                            updateCoord(coord, SOUTH);
                            currDir = SOUTH;
                            break;
                        }
                        case RIGHT:
                        {
                            updateCoord(coord, NORTH);
                            currDir = NORTH;
                            break;
                        }
                    }
                    break;
                }                
            }
            
            if(lattice[coord[0]][coord[1]] != PSPProblem.EMPTY)
            {
                throw new InvalidModelException("Collision", i, lattice);
            }
            lattice[coord[0]][coord[1]] = PSPProblem.val(dataset[i+1]);
        }        
        return lattice;
    }
    
    
    public final static void updateCoord(int [] coord, int dir)
    {
        switch(dir)
        {
            case WEST: // right
            {
                coord[0] -= 1;
                break;
            }       
            case SOUTH: // down
            {
                coord[1] += 1;
                break;
            }  
            case EAST: // left
            {
                coord[0] += 1;                  
                break;
            }
            case NORTH: // up
            {
                coord[1] -= 1;
                break;
            }
            default:
            {
                throw new AlgorithmRunException("Invalid absolute permutation value " + dir);
            }   
        }
    }
    
    
    /**
     * Check that the relative permutation is valid
     * @param aPermutation
     * @param dataset
     */
    public final static void isValidRelativePermutation(byte [] aPermutation, boolean [] dataset)
    {
        if(aPermutation.length != dataset.length-1)
        {
            throw new AlgorithmRunException("Permutation length "+aPermutation.length+" does not match expected " + (dataset.length-1));
        }
        for (int i = 0; i < aPermutation.length; i++)
        {
            if(aPermutation[i]<FOWARD||aPermutation[i]>RIGHT)
            {
                throw new AlgorithmRunException("Invalid permutation value " + aPermutation[i]);
            }
        }
    }
    
    /**
     * Convert an relative permutation to a human-readable string
     * @param permutation
     * @return
     */
    public static String relativePermutationToString(byte [] permutation)
    {
        char [] c = new char[permutation.length];
        
        for (int i = 0; i < c.length; i++)
        {
            switch(permutation[i])
            {
                case FOWARD:                
                {
                    c[i] = 'F';
                    break;
                }
                case LEFT:
                {
                    c[i] = 'L';
                    break;
                }
                case RIGHT:
                {
                    c[i] = 'R';
                    break;
                }
                default:
                {
                    throw new AlgorithmRunException("Invalid permutation value " + permutation[i]);
                }                    
            }
        }
        
        return new String(c);
    }
}
