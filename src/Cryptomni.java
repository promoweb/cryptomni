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
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
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
			if (args[0].equals("-e"))
			{
				// Attempt to encrypt the file.
				if (encryptFile(new File(args[1]), new File (args[2]), 
						new File (args[3])))
		    	{
		    		System.out.println("File encrypted successfully.");
		    	}
		    	else
		    	{
				System.out.println("File encryption failed.\n");
		    		displayHelp = true;
		    	}
			}
			else if (args[0].equals("-d"))
			{
				// Attempt to decrypt the file.
				if (decryptFile(new File(args[1]), new File (args[2]), 
						new File (args[3])))
		    	{
		    		System.out.println("File decrypted successfully.");
		    	}
		    	else
		    	{
				System.out.println("File decryption failed.\n");
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
			System.out.println("-e <source file> <key file> <destination file>");
			System.out.println("Encrypt a file using the specified Cryptomni " +
					           "key.\nThe used portion of the key is deleted.\n");
			System.out.println("-d <encrypted file> <key file> <destination file>");
			System.out.println("Decrypt a file using the specified Cryptomni " +
					           "key.\nThe used portion of the key is deleted.\n");
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
		
        try (BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(filename, false)))
        {
		// Write random bytes to the file.
            out.write(pseudoRandom);
        }
        catch (IOException e)
        {
            // Return false because of the IOException.
            return false;
        }
        // Return true since the operation was successful.
		return true;
	}
	
	private static boolean transformFile(File inputFile, File keyFile, File outputFile) {
        if (inputFile.length() > keyFile.length()) {
            System.err.println("Error: Key file is shorter than the input file. Aborting.");
            return false;
        }

        final int BUFFER_SIZE = 8192;
        try (BufferedInputStream in = new BufferedInputStream(new FileInputStream(inputFile));
             BufferedInputStream key = new BufferedInputStream(new FileInputStream(keyFile));
             BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(outputFile, false))) {

            byte[] inputBuffer = new byte[BUFFER_SIZE];
            byte[] keyBuffer = new byte[BUFFER_SIZE];
            int bytesRead;

            while ((bytesRead = in.read(inputBuffer)) != -1) {
                int keyBytesRead = key.read(keyBuffer, 0, bytesRead);
                if (keyBytesRead < bytesRead) {
                    System.err.println("Error: Key stream ended prematurely. This should not happen.");
                    out.close();
                    outputFile.delete();
                    return false;
                }

                byte[] outputBuffer = new byte[bytesRead];
                for (int i = 0; i < bytesRead; i++) {
                    outputBuffer[i] = (byte) (inputBuffer[i] ^ keyBuffer[i]);
                }
                out.write(outputBuffer, 0, bytesRead);
            }
        } catch (IOException e) {
            outputFile.delete();
            return false;
        }

        // Shrink or delete the key
        try {
            long inputLength = inputFile.length();
            long keyLength = keyFile.length();

            if (inputLength < keyLength) {
                try (RandomAccessFile raf = new RandomAccessFile(keyFile, "rw")) {
                    FileChannel channel = raf.getChannel();
                    long newSize = keyLength - inputLength;

                    ByteBuffer buffer = ByteBuffer.allocate(8192);
                    long readPos = inputLength;
                    long writePos = 0;

                    while (readPos < keyLength) {
                        buffer.clear();
                        int bytesRead = channel.read(buffer, readPos);
                        if (bytesRead <= 0) {
                            break;
                        }
                        buffer.flip();
                        int bytesWritten = channel.write(buffer, writePos);
                        readPos += bytesWritten;
                        writePos += bytesWritten;
                    }
                    channel.truncate(newSize);
                }
            } else {
                // If the key is used up exactly, delete it.
                keyFile.delete();
            }
        } catch (IOException e) {
            // If shrinking fails, the key might be in a corrupted state.
            return false;
        }
        return true;
    }

	public static boolean encryptFile (File sourceFile, File keyFile,
			File destinationFile) {
		return transformFile(sourceFile, keyFile, destinationFile);
	}

	public static boolean decryptFile (File encryptedFile, File keyFile,
			File destinationFile) {
		return transformFile(encryptedFile, keyFile, destinationFile);
	}
}
