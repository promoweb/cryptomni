package src;

/*-- TabbedPane.java -------------------------------------------------------------

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
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;

public class TabbedPane extends JPanel 
{
	// Class variables:
	private static final long serialVersionUID = 1L;
	private JFileChooser fc;
	private JSpinner spinner;
	private JComboBox sizeList;
	private ImageIcon saveIcon, dirIcon;
	
	// Class variables to keep track of file selections:
	private File encryptFile1, encryptFile2, decryptFile1, decryptFile2;
	private boolean encryptFile1Select, encryptFile2Select, decryptFile1Select, 
					decryptFile2Select;

	public TabbedPane() 
    {
        super(new GridLayout(1, 1));
        
        // Create the tabbed pane.
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
        ImageIcon keyIcon = createImageIcon("/icons/key.gif");
        ImageIcon lockIcon = createImageIcon("/icons/lock.gif");
        ImageIcon openIcon = createImageIcon("/icons/lock_open.gif");
        saveIcon = createImageIcon("/icons/save.gif");
        dirIcon = createImageIcon("/icons/open.gif");
        
        // Create the first panel.
        JComponent panel1 = makeKeyPanel();
        tabbedPane.addTab("Create Key", keyIcon, panel1,"Create a Cryptomni key");
        tabbedPane.setMnemonicAt(0, KeyEvent.VK_1);
        
        // Create the second panel.
        JComponent panel2 = makeEncryptPanel();
        tabbedPane.addTab("Encrypt", lockIcon, panel2, "Encrypt a file");
        tabbedPane.setMnemonicAt(1, KeyEvent.VK_2);
        
        // Create the third panel.
        JComponent panel3 = makeDecryptPanel();
        tabbedPane.addTab("Decrypt", openIcon, panel3, "Decrypt a file");
        tabbedPane.setMnemonicAt(2, KeyEvent.VK_3);
        
        // Add the tabbed pane to this panel.
        add(tabbedPane);
        
        // Enable use of scrolling tabs.
        tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
    }
    
	// Creates a panel which allows the user to create a Cryptomni key.
    protected JComponent makeKeyPanel() 
    {        
        JPanel panel = new JPanel(false);
        panel.setLayout(new BorderLayout());
        
        // Create a file chooser.
        fc = new JFileChooser();
        
        // Create labels.
        JLabel label1 = new JLabel("File size:"); 
        JLabel label2 = new JLabel("Choose a file size larger than the file you" +
        		" plan on encrypting.");
        
        // Create a spinner.
        SpinnerModel sizeModel = new SpinnerNumberModel(1,1,Integer.MAX_VALUE,1);
        spinner = new JSpinner (sizeModel);

        // Create a combo box.
        String[] sizeStrings = {"bytes (B)","kibibytes (KiB)","mebibytes (MiB)"};
        sizeList = new JComboBox(sizeStrings);
        sizeList.setSelectedIndex(1);
        
        // Create the key button.
        JButton keyButton = new JButton("Create Key File...",saveIcon);
        keyButton.addActionListener(new ActionListener() 
        {
        	public void actionPerformed(ActionEvent e) 
        	{
    			// Display the save dialog.
                int returnVal = fc.showSaveDialog(TabbedPane.this);
                if (returnVal == JFileChooser.APPROVE_OPTION) 
                {
                    File file = fc.getSelectedFile();
                    // Get the value of the spinner.
                    int byteSize = (Integer)spinner.getValue();
                    // Check the value of the combo box.
                    if (sizeList.getSelectedItem().equals("kibibytes (KiB)"))
                    {
                    	// Convert kibibytes to bytes.
                    	byteSize *= 1024;
                    }
                    else if (sizeList.getSelectedItem().equals("mebibytes (MiB)"))
                    {
                    	// Convert mebibytes to bytes.
                    	byteSize *= 1048576;
                    }
                    // Attempt to create the key file.
                    if (Cryptomni.createKey(file, byteSize))
                    {
                    	// The key file was successfully created.
                		JOptionPane.showMessageDialog(TabbedPane.this,
                				"Cryptomni key successfully created.","Success", 
                				JOptionPane.INFORMATION_MESSAGE);
                    }
                    else
                    {
                    	// Display an error message.
                		JOptionPane.showMessageDialog(TabbedPane.this,
                				"File IO exception.","Error", 
                				JOptionPane.ERROR_MESSAGE);
                    }
                }
        	}
        });
        
        // For layout purposes, create three separate panels.
        JPanel panel1 = new JPanel();
        panel1.add(label1);
        panel1.add(spinner);
        panel1.add(sizeList);
        JPanel panel2 = new JPanel();
        panel2.add(label2);
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(keyButton);

        // Add the buttons to this panel.
        panel.add(panel1, BorderLayout.PAGE_START);
        panel.add(panel2, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.PAGE_END);
        
        return panel;
    }
    
    // Creates a panel which allows the user to encrypt a file.
    protected JComponent makeEncryptPanel() 
    {
        JPanel panel = new JPanel(false);
        panel.setLayout(new BorderLayout());
        
        // Create a file chooser.
        fc = new JFileChooser();
        
        // Create the source button.
        JButton sourceButton = new JButton("Choose Source File...",dirIcon);
        encryptFile1Select = false;
        sourceButton.addActionListener(new ActionListener() 
        {
        	public void actionPerformed(ActionEvent e) 
        	{
        		// Display the open dialog.
                int returnVal = fc.showOpenDialog(TabbedPane.this);
                if (returnVal == JFileChooser.APPROVE_OPTION) 
                {
                    encryptFile1 = fc.getSelectedFile();
                    encryptFile1Select = true;
                }
            }
        });
        
        // Create the key button.
        JButton keyButton = new JButton("Choose Key File...",dirIcon);
        encryptFile2Select = false;
        keyButton.addActionListener(new ActionListener() 
        {
        	public void actionPerformed(ActionEvent e) 
        	{
        		// Display the open dialog.
                int returnVal = fc.showOpenDialog(TabbedPane.this);
                if (returnVal == JFileChooser.APPROVE_OPTION) 
                {
                    encryptFile2 = fc.getSelectedFile();
                    encryptFile2Select = true;
                }
            }
        });
        
        // Create the destination button.
        JButton destinationButton = new JButton("Create Encrypted File...",
        		saveIcon);
        destinationButton.addActionListener(new ActionListener() 
        {
        	public void actionPerformed(ActionEvent e) 
        	{
        		// Make sure a source file was selected.
        		if (!encryptFile1Select)
        		{
            		JOptionPane.showMessageDialog(TabbedPane.this,"Please select"+
            				" a source file.","Error", JOptionPane.ERROR_MESSAGE);
        		}
        		// Make sure a key was selected.
        		else if (!encryptFile2Select)
        		{
            		JOptionPane.showMessageDialog(TabbedPane.this,"Please select"+
            				" a key file.","Error", JOptionPane.ERROR_MESSAGE);
        		}
        		else
        		{
        			int proceed = 0;
        			// Check whether the key file is smaller than the source file.
        			if (encryptFile1.length()>encryptFile2.length())
        			{
        				// Ask the user if the encryption should proceed.
        				proceed = JOptionPane.showConfirmDialog(
        					  TabbedPane.this,
        					  "The source file is larger than the key file.\n" +
        					  "Are you sure you want to continue the encryption?",
        					  "Warning",
        					  JOptionPane.YES_NO_OPTION);
        			}
        			if (proceed == 0)
        			{
            			// Display the save dialog.
                        int returnVal = fc.showSaveDialog(TabbedPane.this);
                        if (returnVal == JFileChooser.APPROVE_OPTION) 
                        {
                            File file3 = fc.getSelectedFile();
                        	int shrink = 0;
                			if (encryptFile1.length()<encryptFile2.length())
                			{
                				shrink = JOptionPane.showConfirmDialog(
                  					  TabbedPane.this,
                  					  "The key file is larger than the source " +
                  					  "file.\nMultiple files can be encrypted" +
                  					  " using a single\nkey file if the used " +
                  					  "portion of the key is\ndeleted after" +
                  					  " each operation. Would you like\nto " +
                  					  "shrink the key file after the encryption?",
                  					  "Confirm",
                  					  JOptionPane.YES_NO_OPTION);
                			}
                            // Attempt to encrypt the file.
                			boolean result = false;
                			if (shrink == 0)
                			{
                				result = Cryptomni.encryptFile(encryptFile1, 
                						encryptFile2, file3, true);
                			}
                			else
                			{
                				result = Cryptomni.encryptFile(encryptFile1, 
                						encryptFile2, file3, false);
                			}
                            if (result)
                            {
                            	// The file was successfully encrypted.
                        		JOptionPane.showMessageDialog(TabbedPane.this,
                        				"File encrypted successfully.","Success", 
                        				JOptionPane.INFORMATION_MESSAGE);
                            }
                            else
                            {
                            	// Display an error message.
                        		JOptionPane.showMessageDialog(TabbedPane.this,
                        				"File IO exception.","Error", 
                        				JOptionPane.ERROR_MESSAGE);
                            }
                        }
        			}
        		}
            }
        });
        
        // For layout purposes, put the buttons in separate panels.
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(sourceButton);
        buttonPanel.add(keyButton);
        JPanel buttonPanel2 = new JPanel();
        buttonPanel2.add(destinationButton);

        // Add the buttons to this panel.
        panel.add(buttonPanel, BorderLayout.PAGE_START);
        panel.add(buttonPanel2, BorderLayout.CENTER);
        
        return panel;
    }
    
    // Creates a panel which allows the user to decrypt a file.
    protected JComponent makeDecryptPanel() 
    {
        JPanel panel = new JPanel(false);
        panel.setLayout(new BorderLayout());
        
        // Create a file chooser.
        fc = new JFileChooser();
        
        // Create the source button.
        JButton sourceButton = new JButton("Choose Encrypted File...",dirIcon);
        decryptFile1Select = false;
        sourceButton.addActionListener(new ActionListener() 
        {
        	public void actionPerformed(ActionEvent e) 
        	{
        		// Display the open dialog.
                int returnVal = fc.showOpenDialog(TabbedPane.this);
                if (returnVal == JFileChooser.APPROVE_OPTION) 
                {
                    decryptFile1 = fc.getSelectedFile();
                    decryptFile1Select = true;
                }
            }
        });
        
        // Create the key button.
        JButton keyButton = new JButton("Choose Key File...",dirIcon);
        decryptFile2Select = false;
        keyButton.addActionListener(new ActionListener() 
        {
        	public void actionPerformed(ActionEvent e) 
        	{
        		// Display the open dialog.
                int returnVal = fc.showOpenDialog(TabbedPane.this);
                if (returnVal == JFileChooser.APPROVE_OPTION) 
                {
                    decryptFile2 = fc.getSelectedFile();
                    decryptFile2Select = true;
                }
            }
        });
        
        // Create the destination button.
        JButton destinationButton = new JButton("Create Decrypted File...",
        		saveIcon);
        destinationButton.addActionListener(new ActionListener() 
        {
        	public void actionPerformed(ActionEvent e) 
        	{
        		// Make sure a source file was selected.
        		if (!decryptFile1Select)
        		{
            		JOptionPane.showMessageDialog(TabbedPane.this,"Please select"+
            				" an encrypted file.","Error", 
            				JOptionPane.ERROR_MESSAGE);
        		}
        		// Make sure a key was selected.
        		else if (!decryptFile2Select)
        		{
            		JOptionPane.showMessageDialog(TabbedPane.this,"Please select"+
            				" a key file.","Error", JOptionPane.ERROR_MESSAGE);
        		}
        		else
        		{
        			int proceed = 0;
        			// Check if the key is larger than the source file.
        			if (decryptFile1.length()>decryptFile2.length())
        			{
        				// Ask the user if the encryption should proceed.
        				proceed = JOptionPane.showConfirmDialog(
        					  TabbedPane.this,
        					  "The encrypted file is larger than the key file.\n"+
        					  "Are you sure you want to continue the decryption?",
        					  "Warning",
        					  JOptionPane.YES_NO_OPTION);
        			}
        			if (proceed == 0)
        			{
            			// Display the save dialog.
                        int returnVal = fc.showSaveDialog(TabbedPane.this);
                        if (returnVal == JFileChooser.APPROVE_OPTION) 
                        {
                            File file3 = fc.getSelectedFile();
                        	int shrink = 0;
                			if (decryptFile1.length()<decryptFile2.length())
                			{
                				shrink = JOptionPane.showConfirmDialog(
                  					  TabbedPane.this,
                  					  "The key file is larger than the encrypted"+
                  					  " file.\nMultiple files can be decrypted" +
                  					  " using a single\nkey file if the used " +
                  					  "portion of the key is\ndeleted after" +
                  					  " each operation. Would you like\nto " +
                  					  "shrink the key file after the decryption?",
                  					  "Confirm",
                  					  JOptionPane.YES_NO_OPTION);
                			}
                            
                            // Attempt to decrypt the file.
                			boolean result = false;
                			if (shrink == 0)
                			{
                				result = Cryptomni.decryptFile(decryptFile1, 
                						decryptFile2, file3, true);
                			}
                			else
                			{
                				result = Cryptomni.decryptFile(decryptFile1, 
                						decryptFile2, file3, false);
                			}
                            if (result)
                            {
                            	// The file was successfully encrypted.
                        		JOptionPane.showMessageDialog(TabbedPane.this,
                        				"File decrypted successfully.","Success", 
                        				JOptionPane.INFORMATION_MESSAGE);
                            }
                            else
                            {
                            	// Display an error message.
                        		JOptionPane.showMessageDialog(TabbedPane.this,
                        				"File IO exception.","Error", 
                        				JOptionPane.ERROR_MESSAGE);
                            }
                        }
        			}
        		}
            }
        });
        
        // For layout purposes, put the buttons in separate panels.
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(sourceButton);
        buttonPanel.add(keyButton);
        JPanel buttonPanel2 = new JPanel();
        buttonPanel2.add(destinationButton);

        // Add the buttons to this panel.
        panel.add(buttonPanel, BorderLayout.PAGE_START);
        panel.add(buttonPanel2, BorderLayout.CENTER);
        
        return panel;
    }
    
    // Returns an ImageIcon, or null if the path was invalid.
    protected static ImageIcon createImageIcon(String path) 
    {
        java.net.URL imgURL = TabbedPane.class.getResource(path);
        if (imgURL != null) 
        {
            return new ImageIcon(imgURL);
        } 
        else 
        {
            System.err.println("Couldn't find file: " + path);
            return null;
        }
    }
}
