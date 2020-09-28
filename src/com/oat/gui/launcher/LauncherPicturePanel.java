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
package com.oat.gui.launcher;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.Transparency;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.oat.gui.GUIException;

/**
 * Description: 
 *  
 * Date: 21/08/2007<br/>
 * @author Jason Brownlee 
 *
 * <br/>
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 
 * </pre>
 */
public class LauncherPicturePanel extends JPanel
{
	public LauncherPicturePanel()
	{
		// load image
		ImageIcon imageIcon = createImageIcon("/splash.jpg");
		// create buffered image
		Image image = imageIcon.getImage();
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gs = ge.getDefaultScreenDevice();
        GraphicsConfiguration gc = gs.getDefaultConfiguration();
        BufferedImage bimage = gc.createCompatibleImage(image.getWidth(null), image.getHeight(null), Transparency.OPAQUE);
        Graphics g = bimage.createGraphics();
        g.drawImage(image, 0, 0, null);
        g.dispose();
		// scale
		AffineTransform tx = new AffineTransform();
		tx.scale(0.75, 0.75);
		AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);
		bimage = op.filter(bimage, null);
		// convert back to an image
		image = Toolkit.getDefaultToolkit().createImage(bimage.getSource());
		imageIcon = new ImageIcon(image);
		// display		
		setLayout(new BorderLayout());
		add(new JLabel(imageIcon), BorderLayout.CENTER);
		
	}
	
	protected ImageIcon createImageIcon(String path) 
	{
	    URL imgURL = LauncherPicturePanel.class.getResource(path);
	    if(imgURL == null)
	    {
	    	throw new GUIException("Unable to load image: " + path);
	    }
	    return new ImageIcon(imgURL);
	}
}
