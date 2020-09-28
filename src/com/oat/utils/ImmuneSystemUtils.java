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

/**
 * Type: ImmuneSystemUtils<br/>
 * Date: 07/12/2006<br/>
 * <br/>
 * Description: Algorithm functions common to immune system algorithms
 * <br/>
 * @author Jason Brownlee
 * 
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 *                          
 * </pre>
 */
public class ImmuneSystemUtils
{
    /**
     * The CLONALG mutation probability equation a=exp(-rho*f)
     * Specified in: Leandro N. de Castro and Fernando J. Von Zuben. Learning and optimization using the clonal selection principle. IEEE Transactions on Evolutionary Computation. 2002 Jun; 6(3):239-251. ISSN: 1089-778X.
     * 
     * @param normalizedFitness - fitness of parent normalized [0,1], where the 
     *  larger the value, the better the fitness (maximising normalized fitness)
     * @param rho - the mutation configuration parameter >1 
     */
    public final static double mutationProbabilityCLONALG(double normalizedFitness, double rho)
    {
        double prob = Math.exp(-rho * normalizedFitness);
        // safety
        if(!AlgorithmUtils.inBounds(prob, 0, 1))
        {
            throw new AlgorithmRunException("Invalid CLONALG mutation probability f["+normalizedFitness+"], rho["+rho+"], prob["+prob+"].");
        }
        return prob;
    }
    
    /**
     * The OPAINET mutation probability equation: a=(1/Beta) * exp(-f)
     * As specified in: An artificial immune network for multimodal function optimization (2002)
     * 
     * @param normalizedFitness - fitness of parent normalized [0,1], where the 
     *  larger the value, the better the fitness (maximising normalized fitness)
     * @param beta - the mutation configuration parameter >1, defaults to 100
     */
    public final static double mutationProbabilityOPAINET(double normalizedFitness, double beta)
    {
        double prob = (1.0/beta) *Math.exp(-normalizedFitness);
        // safety
        if(!AlgorithmUtils.inBounds(prob, 0, 1))
        {
            throw new AlgorithmRunException("Invalid OPT-aiNET mutation probability f["+normalizedFitness+"], rho["+beta+"], prob["+prob+"].");
        }
        return prob;
    }
    
    
    /**
     * Calculate the number of clones to create using the CLONALG method (optimization)
     * round(Beta * N)
     * 
     * @return
     */
    public final static int numClonesCLONALG_OPT(double beta, int N)
    {
        int Nc = (int) Math.round(beta * N);
        // safety
        if(Nc < 0 || AlgorithmUtils.isInvalidNumber(Nc))
        {
            throw new AlgorithmRunException("Invalid CLONALG number of clones beta["+beta+"], N["+N+"], Nc["+Nc+"].");
        }
        
        return Nc;
    }
    
    /**
     * Calculate the number of clones to create using the CLONALG method (classification)
     * round((Beta * N) / i)
     * 
     * @return
     */
    public final static int numClonesCLONALG_CLAS(double beta, int N, int rank)
    {
        int Nc = (int) Math.round((beta * N) / rank);
        // safety
        if(Nc < 0 || AlgorithmUtils.isInvalidNumber(Nc))
        {
            throw new AlgorithmRunException("Invalid CLONALG number of clones beta["+beta+"], N["+N+"], rank["+rank+"] Nc["+Nc+"].");
        }
        
        return Nc;
    }
    
    
    /**
     * The cloning potential used in IA and CLIGA. Specified in the following references:
     * IA: Vincenzo Cutello; Giuseppe Nicosia, and Mario Pavone. A Hybrid Immune Algorithm with Information Gain for the Graph Coloring Problem. Proceedings, Part I:  Genetic and Evolutionary Computation Conference (GECCO 2003); Chicago, IL, USA. Berlin / Heidelberg: Springer; 2003: 171-182. Lecture Notes in Computer Science . v. 2723 ). ISBN: 3-540-40602-6.
     * CLIGA: Vincenzo Cutello and Giuseppe Nicosia. Chapter VI. The Clonal Selection Principle for In Silico and In Vivo Computing. Leandro Nunes de Castro and Fernando J. Von Zuben, Editor. Recent Developments in Biologically Inspired Computing. Hershey, London, Melbourne, Singapore: Idea Group Publishing; 2005; pp. 104-146.
     * @param normalizedFitness
     * @param length - length of the solution
     * @param k
     * @return
     */
    public final static double cloningPotentialCLIGA(double normalizedFitness, int length, double k)
    {
        double m = Math.exp(-k * (length - normalizedFitness));
        // safety
        if(!AlgorithmUtils.inBounds(m, 0, 1))
        {
            throw new AlgorithmRunException("Unexpected cloning potential k["+k+"], length["+length+"] " + m);
        }
        return m;
    }
    
    /**
     * Calculate the probability of a B-cell being deleted as specified in IA and CLIGA
     * IA: Vincenzo Cutello; Giuseppe Nicosia, and Mario Pavone. A Hybrid Immune Algorithm with Information Gain for the Graph Coloring Problem. Proceedings, Part I:  Genetic and Evolutionary Computation Conference (GECCO 2003); Chicago, IL, USA. Berlin / Heidelberg: Springer; 2003: 171-182. Lecture Notes in Computer Science . v. 2723 ). ISBN: 3-540-40602-6.
     * CLIGA: Vincenzo Cutello and Giuseppe Nicosia. Chapter VI. The Clonal Selection Principle for In Silico and In Vivo Computing. Leandro Nunes de Castro and Fernando J. Von Zuben, Editor. Recent Developments in Biologically Inspired Computing. Hershey, London, Melbourne, Singapore: Idea Group Publishing; 2005; pp. 104-146.
     * 
     * @param maxAge (tau-Beta)
     * @return
     */
    public final static double calculateDeletionPotentialCLIGA(double maxAge)
    {
        double m = 1.0 - Math.exp(-Math.log(2) / maxAge);
        // safety
        if(!AlgorithmUtils.inBounds(m, 0, 1))
        {
            throw new AlgorithmRunException("Unexpected deletion potential value tauBeta["+maxAge+"] " + m);
        }
        return m;
    }
    
}
