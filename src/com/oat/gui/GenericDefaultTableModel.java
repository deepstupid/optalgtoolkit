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
package com.oat.gui;

import javax.swing.table.DefaultTableModel;

/**
 * Description: Suitable to extending to get JTables to jump through hoops
 *  
 * Date: 30/08/2007<br/>
 * @author Jason Brownlee 
 *
 * <br/>
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 
 * </pre>
 */
public class GenericDefaultTableModel extends DefaultTableModel
{
	@Override
    public boolean isCellEditable(int rowIndex, int mColIndex) 
	{
        return false;
    }
	
    // This method returns the Class object of the first
    // cell in specified column in the table model.
    // Unless this method is overridden, all values are
    // assumed to be the type Object.
	@Override
    public Class getColumnClass(int columnIndex)
	{
		Object o = getValueAt(0, columnIndex);
		if (o == null)
		{
			return Object.class;
		} 
		else
		{
			// make use of built-in cell renderers  
			return o.getClass();
		}
	}
}
