package Utility;

import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * Created by SadiqMdAsif on 27/10/2016.
 */

public class CompressZip {
    private static final int BUFFER = 2048;

    public static boolean createDirIfNotExists(String path) {
        boolean ret = true;
        File dir = new File(Environment.getExternalStorageDirectory(), path);
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                Log.e("createDirIfNotExists: ", "Problem creating  folder");
                ret = false;
            }
        }
        return ret;
    }


    /* ==========How to use ========

        ZipManager zipManager = new ZipManager();
        zipManager.unzip(inputPath + inputFile, outputPath);
    */

    /* ===========How to Use==========
     declare an array for storing the files i.e the path of your source files
        String[] s = new String[2];

    Type the path of the files in here
        s[0] = inputPath + "/image.jpg";
        s[1] = inputPath + "/textfile.txt"; // /sdcard/ZipDemo/textfile.txt

    first parameter is d files second parameter is zip file name
        CompressZip compressZip = new ZipManager();

        calling the zip function
        compressZip.zip(s, inputPath + inputFile);

     */


    /*
      Button btnZip = (Button) findViewById(R.id.cmdZip);

            btnZip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CompressZip compressZip = new CompressZip();
                    String[] file = new String[1];
                    file[0] = Environment.getExternalStorageDirectory() + "/" + Global.DatabaseFolder + "/" + Global.DatabaseName;
                    String path = File.separator + Global.DatabaseFolder + File.separator + "Zip" + File.separator;
                    String output = "testfile.zip";
                    compressZip.zip(file, path, output);
                }
            });

     */
    public void zip(@NonNull String[] _files, @NonNull String path, @NonNull String zipFileName) {
        try {

            //create target location folder if not exist
            createDirIfNotExists(path);
            File mZipFile = new File(path, zipFileName);
            if (mZipFile.exists()) {
                mZipFile.delete();
            } else {
                try {
                    mZipFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            BufferedInputStream origin = null;
            FileOutputStream dest = new FileOutputStream(path + File.separator + zipFileName);
            ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(dest));

            byte data[] = new byte[BUFFER];

            for (int i = 0; i < _files.length; i++) {
                Log.v("Compress", "Adding: " + _files[i]);
                FileInputStream fi = new FileInputStream(_files[i]);
                origin = new BufferedInputStream(fi, BUFFER);

                ZipEntry entry = new ZipEntry(_files[i].substring(_files[i].lastIndexOf("/") + 1));
                out.putNextEntry(entry);
                int count;

                while ((count = origin.read(data, 0, BUFFER)) != -1) {
                    out.write(data, 0, count);
                }
                origin.close();
            }

            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
               Button unZip = (Button) findViewById(R.id.cmdUnzip);
            unZip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CompressZip compressZip = new CompressZip();
                    String zipPath = File.separator + Global.DatabaseFolder + File.separator + "Zip" + File.separator;
                    String zipName = "testfile.zip";
                    String zipLocation = Environment.getExternalStorageDirectory() + zipPath + zipName;
                    String output = File.separator + Global.DatabaseFolder + File.separator + "Unzip";
                    compressZip.unzip(zipLocation, output);
                }
            });
     */


    public void unzip(@NonNull String _zipFile, @NonNull String _targetLocation) {

        //create target location folder if not exist
        createDirIfNotExists(_targetLocation);

        try {
            FileInputStream fin = new FileInputStream(_zipFile);
            ZipInputStream zin = new ZipInputStream(fin);
            ZipEntry ze = null;
            while ((ze = zin.getNextEntry()) != null) {

                //create dir if required while unzipping
                if (ze.isDirectory()) {
                    createDirIfNotExists(ze.getName());
                } else {
                    FileOutputStream fout = new FileOutputStream(Environment.getExternalStorageDirectory() + _targetLocation + File.separator + ze.getName());
                    for (int c = zin.read(); c != -1; c = zin.read()) {
                        fout.write(c);
                    }

                    zin.closeEntry();
                    fout.close();
                }

            }
            zin.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
