
package jdz.farmKing.utils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileExporter {
	  /**
     * Export a resource embedded into a Jar file to the local file path.
     *
     * @param resourceName ie.: "/SmartLibrary.dll"
     * @return The path to the exported resource
	 * @throws IOException 
     * @throws Exception
     */
    static public void ExportResource(String resourceName, String destinationPath) {
        InputStream stream = null;
        OutputStream resStreamOut = null;
        try {
            stream = FileExporter.class.getResourceAsStream(resourceName);//note that each / is a directory down in the "jar tree" been the jar the root of the tree
            if(stream == null) {
                throw new Exception("Cannot get resource \"" + resourceName + "\" from Jar file.");
            }

            int readBytes;
            byte[] buffer = new byte[4096];
            resStreamOut = new FileOutputStream(destinationPath);
            while ((readBytes = stream.read(buffer)) > 0) {
                resStreamOut.write(buffer, 0, readBytes);
            }
        } catch (Exception ex) {
            ErrorLogger.createLog(ex);
        } finally {
        	try{
	            stream.close();
	            resStreamOut.close();
        	}
        	catch(Exception ex2) {}
        }
    }
}
