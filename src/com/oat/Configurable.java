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
 * Description: A configurable object
 *  
 * Date: 03/09/2007<br/>
 * @author Jason Brownlee 
 *
 * <br/>
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 
 * </pre>
 */
public interface Configurable
{
	/**
	 * Validate the configuration of the object, may throw an exception if the configuration of the object is invalid
	 * @throws InvalidConfigurationException
	 */
	void validateConfiguration() throws InvalidConfigurationException;
	
	/**
	 * Returns all configuration information about the object as a string, preferably in name value pairs,
	 * for example name=value,name=value,...
	 * @return configuration as a string
	 */
	String getConfigurationDetails();
	
	/**
	 * Whether or not the configuration of the object is suitable to be configured by a user, if not, the configuration
	 * may be computer generated only.
	 * @return - true if the object can be configured by a human
	 */
	boolean isUserConfigurable();
}
