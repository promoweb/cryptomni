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
import javax.swing.SwingWorker;
import java.awt.Cursor;


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
        final JButton keyButton = new JButton("Create Key File...",saveIcon);
        keyButton.addActionListener(new ActionListener() 
        {
        	public void actionPerformed(ActionEvent e) 
        	{
    			// Display the save dialog.
                int returnVal = fc.showSaveDialog(TabbedPane.this);
                if (returnVal == JFileChooser.APPROVE_OPTION) 
                {
                    final File file = fc.getSelectedFile();
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
                    final int finalByteSize = byteSize;

                    setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                    keyButton.setEnabled(false);

                    SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
                        @Override
                        protected Boolean doInBackground() throws Exception {
                            return Cryptomni.createKey(file, finalByteSize);
                        }

                        @Override
                        protected void done() {
                            try {
                                if (get()) {
                                    JOptionPane.showMessageDialog(TabbedPane.this,
                                            "Cryptomni key successfully created.","Success",
                                            JOptionPane.INFORMATION_MESSAGE);
                                } else {
                                    JOptionPane.showMessageDialog(TabbedPane.this,
                                            "Failed to create key file.","Error",
                                            JOptionPane.ERROR_MESSAGE);
                                }
                            } catch (Exception ex) {
                                JOptionPane.showMessageDialog(TabbedPane.this,
                                        "An error occurred: " + ex.getMessage(),"Error",
                                        JOptionPane.ERROR_MESSAGE);
                            } finally {
                                setCursor(Cursor.getDefaultCursor());
                                keyButton.setEnabled(true);
                            }
                        }
                    };
                    worker.execute();
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
        final JButton destinationButton = new JButton("Create Encrypted File...",
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
        			// Check whether the key file is smaller than the source file.
        			if (encryptFile1.length()>encryptFile2.length())
        			{
					// Display an error message and abort.
					JOptionPane.showMessageDialog(TabbedPane.this,
        					  "The source file is larger than the key file.\n" +
							  "Please use a key that is at least as long as the source file.",
						  "Error",
						  JOptionPane.ERROR_MESSAGE);
        			}
				else
        			{
            			// Display the save dialog.
                        int returnVal = fc.showSaveDialog(TabbedPane.this);
                        if (returnVal == JFileChooser.APPROVE_OPTION) 
                        {
                            final File file3 = fc.getSelectedFile();

                            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                            destinationButton.setEnabled(false);

                            SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
                                @Override
                                protected Boolean doInBackground() throws Exception {
                                    return Cryptomni.encryptFile(encryptFile1, encryptFile2, file3);
                                }

                                @Override
                                protected void done() {
                                    try {
                                        if (get()) {
                                            JOptionPane.showMessageDialog(TabbedPane.this,
                                                    "File encrypted successfully.","Success",
                                                    JOptionPane.INFORMATION_MESSAGE);
                                        } else {
                                            JOptionPane.showMessageDialog(TabbedPane.this,
                                                    "File encryption failed.","Error",
                                                    JOptionPane.ERROR_MESSAGE);
                                        }
                                    } catch (Exception ex) {
                                        JOptionPane.showMessageDialog(TabbedPane.this,
                                                "An error occurred: " + ex.getMessage(),"Error",
                                                JOptionPane.ERROR_MESSAGE);
                                    } finally {
                                        setCursor(Cursor.getDefaultCursor());
                                        destinationButton.setEnabled(true);
                                    }
                                }
                            };
                            worker.execute();
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
        final JButton destinationButton = new JButton("Create Decrypted File...",
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
				// Check if the key is smaller than the source file.
        			if (decryptFile1.length()>decryptFile2.length())
        			{
					// Display an error message and abort.
					JOptionPane.showMessageDialog(TabbedPane.this,
        					  "The encrypted file is larger than the key file.\n"+
							  "Please use a key that is at least as long as the encrypted file.",
						  "Error",
						  JOptionPane.ERROR_MESSAGE);
        			}
				else
        			{
            			// Display the save dialog.
                        int returnVal = fc.showSaveDialog(TabbedPane.this);
                        if (returnVal == JFileChooser.APPROVE_OPTION) 
                        {
                            final File file3 = fc.getSelectedFile();
                            
                            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                            destinationButton.setEnabled(false);

                            SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
                                @Override
                                protected Boolean doInBackground() throws Exception {
                                    return Cryptomni.decryptFile(decryptFile1, decryptFile2, file3);
                                }

                                @Override
                                protected void done() {
                                    try {
                                        if (get()) {
                                            JOptionPane.showMessageDialog(TabbedPane.this,
                                                    "File decrypted successfully.","Success",
                                                    JOptionPane.INFORMATION_MESSAGE);
                                        } else {
                                            JOptionPane.showMessageDialog(TabbedPane.this,
                                                    "File decryption failed.","Error",
                                                    JOptionPane.ERROR_MESSAGE);
                                        }
                                    } catch (Exception ex) {
                                        JOptionPane.showMessageDialog(TabbedPane.this,
                                                "An error occurred: " + ex.getMessage(),"Error",
                                                JOptionPane.ERROR_MESSAGE);
                                    } finally {
                                        setCursor(Cursor.getDefaultCursor());
                                        destinationButton.setEnabled(true);
                                    }
                                }
                            };
                            worker.execute();
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
