package src;

/*-- Cryptomni.java --------------------------------------------------------------

  Cryptomni Version 1.1

  Copyright (C) 2007 Byron Knoll

  This program is free software; you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation; either version 2 of the License, or
  (at your option) any later version.

  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.

  You should have received a copy of the GNU General Public License
  along with this program; if not, write to the Free Software
  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA

--------------------------------------------------------------------------------*/

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.SecureRandom;

/** 
 * Cryptomni is a program which can encrypt and decrypt files using the one-time 
 * pad cipher. A key file is created using the cryptographically strong random 
 * number generator SecureRandom. If a key is truly random, kept secret, and never
 * reused, this encryption algorithm can be proven to be unbreakable.
 * @author  Byron Knoll
 * @version 1.1, 02/25/2015
 */
public class Cryptomni
{
	public static void main(String[] args)
	{
		// Display program information:
		System.out.println("Cryptomni Version 1.1");
		System.out.println("Copyright (C) 2015 Byron Knoll\n");
		
		// The displayHelp variable will be set to true if invalid command-line 
		// arguments are used.
		boolean displayHelp = false;
		
		if (args.length == 0)
		{
			new CryptomniGUI(); // Open a graphical user interface.
		}
		else if (args.length == 1)
		{
			if (args[0].equals("-i")) 
			{
        		String aboutString =
            	"Cryptomni is a program which can encrypt and decrypt files"+
            	"\nusing the one-time pad cipher. A key file is created using\n"+
            	"the cryptographically strong random number generator \n" +
            	"SecureRandom. If a key is truly random, kept secret, and\n" +
            	"never reused, this encryption algorithm can be proven to\n" +
            	"be unbreakable.\n\n"+
            	"This program is free software; you can redistribute it and/or\n"+
            	"modify it under the terms of the GNU General Public License\n"+
            	"as published by the Free Software Foundation; either version\n"+
            	"2 of the License, or (at your option) any later version. \n\n"+
            	"This program is distributed in the hope that it will be useful,"+
            	"\nbut WITHOUT ANY WARRANTY; without even the implied \n" +
            	"warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR\n" +
            	"PURPOSE. See the GNU General Public License for more details.";
        		System.out.println(aboutString);
			}
			else
			{
				displayHelp = true;
			}
		}
		else if (args.length == 3)
		{
			if (args[0].equals("-c")) 
			{
			    try 
			    {
			    	// Attempt to convert the third argument to an integer.
			    	int size = Integer.parseInt(args[2]);
			    	// Attempt to create the Cryptomni key.
			    	if (createKey(new File(args[1]), size))
			    	{
			    		System.out.println("Cryptomni key successfully created.");
			    	}
			    	else
			    	{
			    		System.out.println("File IO exception.\n");
			    		displayHelp = true;
			    	}
			    } 
			    catch (NumberFormatException e) 
			    {
			    	// The third argument must be an integer.
			        displayHelp = true;
			    }
			}
			else displayHelp = true;
		}
		else if (args.length == 4)
		{
			if (args[0].equals("-e1"))
			{
				// Attempt to encrypt the file.
				if (encryptFile(new File(args[1]), new File (args[2]), 
						new File (args[3]), false))
		    	{
		    		System.out.println("File encrypted successfully.");
		    	}
		    	else
		    	{
		    		System.out.println("File IO exception.\n");
		    		displayHelp = true;
		    	}
			}
			else if (args[0].equals("-e2"))
			{
				// Attempt to encrypt the file.
				if (encryptFile(new File(args[1]), new File (args[2]), 
						new File (args[3]), true))
		    	{
		    		System.out.println("File encrypted successfully.");
		    	}
		    	else
		    	{
		    		System.out.println("File IO exception.\n");
		    		displayHelp = true;
		    	}
			}
			else if (args[0].equals("-d1"))
			{
				// Attempt to decrypt the file.
				if (decryptFile(new File(args[1]), new File (args[2]), 
						new File (args[3]), false))
		    	{
		    		System.out.println("File decrypted successfully.");
		    	}
		    	else
		    	{
		    		System.out.println("File IO exception.\n");
		    		displayHelp = true;
		    	}
			}
			else if (args[0].equals("-d2"))
			{
				// Attempt to decrypt the file.
				if (decryptFile(new File(args[1]), new File (args[2]), 
						new File (args[3]), true))
		    	{
		    		System.out.println("File decrypted successfully.");
		    	}
		    	else
		    	{
		    		System.out.println("File IO exception.\n");
		    		displayHelp = true;
		    	}
			}
			else displayHelp = true;
		}
		else // Invalid number of arguments.
		{
			System.out.println("Invalid number of command-line arguments.\n");
			displayHelp = true;
		}
		
		if (displayHelp) // Display usage instructions.
		{
			System.out.println("Usage:\n");
			System.out.println("A graphical user interface will open if no " +
							   "command-line arguments are provided.\n");
			System.out.println("-i");
			System.out.println("Display program information.\n");
			System.out.println("-c <filename> <size>");
			System.out.println("Create a Cryptomni key with the specified " +
							   "number of bytes.\n");
			System.out.println("-e1 <source file> <key file> <destination file>");
			System.out.println("Encrypt a file using the specified Cryptomni " +
					           "key (without modifying the key).\n");
			System.out.println("-e2 <source file> <key file> <destination file>");
			System.out.println("Encrypt a file using the specified Cryptomni " +
					           "key (deleting the used portion of the key " +
					           "file).\n");
			System.out.println("-d1 <encrypted file> <key file> " +
							   "<destination file>");
			System.out.println("Decrypt a file using the specified Cryptomni " +
	           					"key (without modifying the key).\n");	
			System.out.println("-d2 <encrypted file> <key file> " +
			   "<destination file>");
			System.out.println("Decrypt a file using the specified Cryptomni " +
					           "key (deleting the used portion of the key " +
					           "file).\n");
		}
	}
	
	/** 
	 * Attempts to create a Cryptomni key with the specified number of bytes.
	 * 
	 * @param  filename the location to create the Cryptomni key file.
	 * @param  byteSize the number of bytes of the Cryptomni key file.
	 * @return 			a boolean that is true if the Cryptomni key was 
	 * 					successfully created, or false if an IOException occurred.
	 */
	public static boolean createKey (File filename, int byteSize)
	{
		// Create random bytes using a cryptographically strong random 
		// number generator.
		SecureRandom random  = new SecureRandom();
		byte[] pseudoRandom = new byte [byteSize];
		random.nextBytes(pseudoRandom);
		
        FileOutputStream out = null;
        try 
        {
        	// Create a file output stream.
            out = new FileOutputStream(filename, false);
            // Iterate through each byte in the file.
            for (byte currentByte: pseudoRandom)
            {
            	// Write a random byte to the file.
                out.write(currentByte);
            }
            // Close the output stream.
            out.close();
        }
        catch (IOException e1)
        {
            if (out != null) // Check if the output stream was created.
            {
            	try
            	{
                	// Close the output stream.
            		out.close();
            	}
            	catch (IOException e2) {}
            }
            // Return false because of the IOException.
            return false;
        }
        // Return true since the operation was successful.
		return true;
	}
	
	/** 
	 * Attempts to encrypt a file using the specified Cryptomni key.
	 * 
	 * @param  sourceFile 		the location of the file to encrypt.
	 * @param  keyFile			the location of the Cryptomni key file.
	 * @param  destinationFile	the location to create the new encrypted file.
	 * @param  shrink			a boolean which determines whether the used 
	 * 							portion of the key file should be deleted.
	 * @return 					a boolean that is true if the file was 
	 * 							successfully encrypted, or false if an 
	 * 							IOException occurred.
	 */
	public static boolean encryptFile (File sourceFile, File keyFile, 
			File destinationFile, boolean shrink)
	{
        FileInputStream in = null;
        FileInputStream key = null;
        FileOutputStream out = null;
        try 
        {
    		// Create file IO streams.
            in = new FileInputStream(sourceFile);
            key = new FileInputStream(keyFile);
            out = new FileOutputStream(destinationFile, false);
            int currentByte, keyByte, newByte;

            // Iterate through the bytes in the original file.
            while ((currentByte = in.read()) != -1) 
            {
            	keyByte = key.read();
            	// Calculate the value of the encrypted byte.
            	newByte = (currentByte+keyByte)%256;
            	// Write the byte to the encrypted file.
                out.write(newByte);
            }
            
            // Check whether the key file needs to be changed.
            if (shrink && (sourceFile.length()<keyFile.length()))
            {
                // Create a temporary file.
            	File temp = File.createTempFile("crypt", null);
                // Delete temp file when program exits.
                temp.deleteOnExit();
                // Create a new output stream.
            	out.close();
                out = new FileOutputStream(temp, false);
                // Iterate through the remaining bytes in the key file.
                while ((currentByte = key.read()) != -1) 
                {
                	// Write the byte to the temp file.
                    out.write(currentByte);
                }
                // Close the IO streams.
                in.close();
                key.close();
                out.close();
                // Create new file IO streams to write the new key file.
                in = new FileInputStream(temp);
                out = new FileOutputStream(keyFile, false);
                // Write the new key file.
                while ((currentByte = in.read()) != -1) 
                {
                	// Write the byte to the key file.
                    out.write(currentByte);
                }
                // Close the remaining IO streams.
                in.close();
                out.close();
            }
            else
            {            
            	// Close the IO streams.
                in.close();
                key.close();
                out.close();
            }
        } 
        catch (IOException e1) 
        {
        	try
        	{
                if (in != null) // Check if the input stream was created.
                {
                	// Close the input stream.
                    in.close();
                }
                if (key != null) // Check if the input stream was created.
                {
                	// Close the input stream.
                    key.close();
                }
                if (out != null) // Check if the output stream was created.
                {
                	// Close the output stream.
                    out.close();
                }
        	}
        	catch (IOException e2) {}
            // Return false because of the IOException.
        	return false;
        }
        // Return true since the operation was successful.
		return true;
	}
	
	/** 
	 * Attempts to decrypt a file using the specified Cryptomni key.
	 * 
	 * @param  encryptedFile	the location of the file to decrypt.
	 * @param  keyFile			the location of the Cryptomni key file.
	 * @param  destinationFile	the location to create the new decrypted file.
	 * @param  shrink			a boolean which determines whether the used 
	 * 							portion of the key file should be deleted.
	 * @return 					a boolean that is true if the file was 
	 * 							successfully decrypted, or false if an 
	 * 							IOException occurred.
	 */
	public static boolean decryptFile (File encryptedFile, File keyFile, 
			File destinationFile, boolean shrink)
	{
        FileInputStream in = null;
        FileInputStream key = null;
        FileOutputStream out = null;   
        try 
        {
    		// Create file IO streams.
            in = new FileInputStream(encryptedFile);
            key = new FileInputStream(keyFile);
            out = new FileOutputStream(destinationFile, false);
            int currentByte, keyByte, newByte;

            // Iterate through the bytes in the encrypted file.
            while ((currentByte = in.read()) != -1) 
            {
            	keyByte = key.read();
            	// Calculate the value of the decrypted byte.
            	newByte = currentByte-keyByte;
            	if (newByte < 0) newByte+=256;
            	// Write the byte to the decrypted file.
                out.write(newByte);
            }
            
            // Check whether the key file needs to be changed.
            if (shrink && (encryptedFile.length()<keyFile.length()))
            {
                // Create a temporary file.
            	File temp = File.createTempFile("crypt", null);
                // Delete temp file when program exits.
                temp.deleteOnExit();
                // Create a new output stream.
            	out.close();
                out = new FileOutputStream(temp, false);
                // Iterate through the remaining bytes in the key file.
                while ((currentByte = key.read()) != -1) 
                {
                	// Write the byte to the temp file.
                    out.write(currentByte);
                }
                // Close the IO streams.
                in.close();
                key.close();
                out.close();
                // Create new file IO streams to write the new key file.
                in = new FileInputStream(temp);
                out = new FileOutputStream(keyFile, false);
                // Write the new key file.
                while ((currentByte = in.read()) != -1) 
                {
                	// Write the byte to the key file.
                    out.write(currentByte);
                }
                // Close the remaining IO streams.
                in.close();
                out.close();
            }
            else
            {            
            	// Close the IO streams.
                in.close();
                key.close();
                out.close();
            }
        } 
        catch (IOException e1) 
        {
        	try
        	{
                if (in != null) // Check if the input stream was created.
                {
                	// Close the input stream.
                    in.close();
                }
                if (key != null) // Check if the input stream was created.
                {
                	// Close the input stream.
                    key.close();
                }
                if (out != null) // Check if the output stream was created.
                {
                	// Close the output stream.
                    out.close();
                }
        	}
        	catch (IOException e2) {}
            // Return false because of the IOException.
        	return false;
        }
        // Return true since the operation was successful.
		return true;
	}
}
