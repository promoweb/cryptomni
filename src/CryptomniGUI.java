package src;

/*-- CryptomniGUI.java -----------------------------------------------------------

Copyright (C) 2007 Byron Knoll

This file is part of Cryptomni.

Cryptomni is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

Cryptomni is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with Cryptomni; if not, write to the Free Software
Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA

--------------------------------------------------------------------------------*/

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

public class CryptomniGUI
{
	private static JFrame frame; // The Cryptomni window.
	
	public CryptomniGUI ()
	{
        // Schedule a job for the event-dispatching thread:
        // creating and showing Cryptomni's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() 
        {
            public void run() 
            {
                createAndShowGUI();
            }
        });
	}
	
    private static void createAndShowGUI() 
    {
        // Create and set up the window.
        frame = new JFrame("Cryptomni");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);

        // Create and set up the content pane.
        JComponent newContentPane = new TabbedPane();
        newContentPane.setOpaque(true);
        frame.getContentPane().add(newContentPane, BorderLayout.CENTER);
        
        // Create the menu bar.
        initializeMenu ();

        // Display the window.
        frame.pack();
        frame.setVisible(true);
    }
    
    private static void initializeMenu ()
    {
        // Create the menu bar.
        JMenuBar menuBar = new JMenuBar();

        // Build the first menu.
        JMenu menu = new JMenu("File");
        menu.setMnemonic(KeyEvent.VK_F);
        menu.getAccessibleContext().setAccessibleDescription("File");
        menuBar.add(menu);

        // Create a JMenuItem.
        JMenuItem menuItem = new JMenuItem("Quit", KeyEvent.VK_Q);
        // Add an action listener.
        menuItem.addActionListener(new ActionListener() 
        {
        	public void actionPerformed(ActionEvent e) 
        	{
        		// Exit Cryptomni.
                frame.setVisible(false);
                frame.dispose();
                System.exit(0);
            }
        });
        menu.add(menuItem);
        
        // Build the second menu.
        JMenu menu2 = new JMenu("Help");
        menu2.setMnemonic(KeyEvent.VK_H);
        menu2.getAccessibleContext().setAccessibleDescription("File");
        menuBar.add(menu2);
        
        // Create a JMenuItem.
        JMenuItem menu2Item = new JMenuItem("About", KeyEvent.VK_A);
        // Add an action listener.
        menu2Item.addActionListener(new ActionListener() 
        {
        	public void actionPerformed(ActionEvent e) 
        	{
        		String aboutString =
        		"Cryptomni is a program which can encrypt and decrypt files"+
        		"\nusing the one-time pad cipher. A key file is created using\n"+
        		"the cryptographically strong random number generator \n" +
        		"SecureRandom. If a key is truly random, kept secret, and\n" +
        		"never reused, this encryption algorithm can be proven to\n" +
        		"be unbreakable.\n\n"+
        		"Copyright (C) 2007 Byron Knoll\n\n"+
        		"This program is free software; you can redistribute it and/or\n"+
        		"modify it under the terms of the GNU General Public License\n"+
        		"as published by the Free Software Foundation; either version\n"+
        		"2 of the License, or (at your option) any later version. \n\n"+
        		"This program is distributed in the hope that it will be useful,"+
        		"\nbut WITHOUT ANY WARRANTY; without even the implied \n" +
        		"warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR\n" +
        		"PURPOSE. See the GNU General Public License for more details.";
        		// Create the About dialog.
        		JOptionPane.showMessageDialog(frame, aboutString,
        				"Cryptomni Version 1.1", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        menu2.add(menu2Item);
        
        // Set the menu bar.
        frame.setJMenuBar(menuBar);
    }
}

