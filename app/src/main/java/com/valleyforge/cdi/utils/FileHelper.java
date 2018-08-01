package com.valleyforge.cdi.utils;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Get the Serial number from the Scan record, (Disto D510, LD 520) device
 */
public class FileHelper {


    /**
     * ClassName
     */
    private final static String CLASSTAG = FileHelper.class.getSimpleName();


    /**
     * Load information from a File into a byteArray
     * @param file File Object to be read
     * @return byteArray with the contents of the file
     * @throws IOException Error reading the file
     */
    static public byte[] loadFile(File file) throws IOException {
        final String METHODTAG = CLASSTAG + ".loadFile";

        File updateFile = file;
        FileInputStream fis = null;
        byte[] bytesArray = null;
        final int BUFFER_SIZE = 1024*10; //this is actually bytes


        int position = 0;

        if (isExternalStorageWritable() && isExternalStorageReadable()) {

            try {
                Log.d(CLASSTAG, METHODTAG + ": File.length: " + updateFile.length());
                bytesArray = new byte[(int) updateFile.length()];
                fis = new FileInputStream(updateFile);

                byte[] buffer = new byte[BUFFER_SIZE];
                int read = 0;
                while( ( read = fis.read( buffer ) ) > 0 ){

                    System.arraycopy(buffer,0,bytesArray,position,read);
                    position += read;
                }

                Log.d(CLASSTAG, METHODTAG + ": File.length real: " +position+buffer.length);
                fis.close();

            } catch (IOException e) {

                Log.e(CLASSTAG, METHODTAG + ": Error Loading the file",e);
                throw e;
            }
        }
        return bytesArray;
    }

    /**
     *
     * @param path file path
     * @param filename file name
     * @return byteArray with the contents of the file
     * @throws IOException Error reading the file
     */
    static public byte[] loadFile(String path, String filename ) throws IOException {
        return loadFile(new File(path, filename));
    }

    /**
     * Checks if external storage is available for read and write
     * @return True if the External Storage is writable
     */
    private static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /**
     * Checks if external storage is available to at least read
     * @return True if the external storage is writable
     */
    private static boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    /**
     * Find files in download, documents and root storage folder with specific prefix and file extension
     * @param preFix the prefix of the files that should be found
     * @param fileExtension the file extension of the files that should be found
     * @return a list of the found files
     */
    public static List<File> findFiles(String preFix, String fileExtension){
        List<File> foundFiles = new ArrayList<>();

        // download folder
        File folder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        if (folder.exists() == true) {
            File[] files = folder.listFiles();
            if (files != null){
                for (File file : files) {
                    String fileName = file.getAbsolutePath().substring(file.getAbsolutePath().lastIndexOf("/") + 1);
                    // check prefix
                    if (fileName.startsWith(preFix)) {
                        // check file extension
                        String filenameArray[] = fileName.split("\\.");
                        if (filenameArray == null || filenameArray.length < 1) {
                            break;
                        }
                        String extension = filenameArray[filenameArray.length - 1];
                        if (extension.equalsIgnoreCase(fileExtension)) {
                            foundFiles.add(file);
                        }
                    }
                }
            }
        }

        // documents folder
        folder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
        if (folder.exists() == true) {
            File[] files = folder.listFiles();
            if (files != null){
                for (File file : files) {
                    String fileName = file.getAbsolutePath().substring(file.getAbsolutePath().lastIndexOf("/") + 1);
                    // check prefix
                    if (fileName.startsWith(preFix)) {
                        // check file extension
                        String filenameArray[] = fileName.split("\\.");
                        if (filenameArray == null || filenameArray.length < 1) {
                            break;
                        }
                        String extension = filenameArray[filenameArray.length - 1];
                        if (extension.equalsIgnoreCase(fileExtension)) {
                            foundFiles.add(file);
                        }
                    }
                }
            }
        }


        // external folder
        folder = Environment.getExternalStorageDirectory();
        if (folder.exists() == true) {
            File[] files = folder.listFiles();
            if (files != null){
                for (File file : files) {
                    String fileName = file.getAbsolutePath().substring(file.getAbsolutePath().lastIndexOf("/") + 1);
                    // check prefix
                    if (fileName.startsWith(preFix)) {
                        // check file extension
                        String filenameArray[] = fileName.split("\\.");
                        if (filenameArray == null || filenameArray.length < 1) {
                            break;
                        }
                        String extension = filenameArray[filenameArray.length - 1];
                        if (extension.equalsIgnoreCase(fileExtension)) {
                            foundFiles.add(file);
                        }
                    }
                }
            }
        }

        return foundFiles;
    }

}
