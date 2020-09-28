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

/**
 * Date: 08/12/2006<br/>
 * <br/>
 * Description: Contains the version of the software, for use in UI's
 * <br/>
 * @author Jason Brownlee
 * 
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 11/07/2007   JBrownlee   Added names and web        
 *                
 * </pre>
 */
public interface Version
{
	// TODO : move these strings to a resource bundle
	
    /**
     * A long name for the software
     */
    String NAME_LONG = "Optimization Algorithm Toolkit";
    /**
     * A short name for the software
     */
    String NAME_SHORT = "OAT";
    /**
     * web URL
     */
    String WEB = "http://sourceforge.net/projects/optalgtoolkit";
    /**
     * Current release version of the software
     */
    String VERSION = "1.4";
}
