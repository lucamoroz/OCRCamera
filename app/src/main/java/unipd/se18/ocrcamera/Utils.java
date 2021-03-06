package unipd.se18.ocrcamera;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.support.media.ExifInterface;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;

public class Utils {
    /**
     * @param filePath name of a jpeg file to convert to bitmap
     * @return image converted to bitmap
     */
    public static Bitmap loadBitmapFromFile(String filePath) {
        Bitmap bitmap = BitmapFactory.decodeFile(filePath);

        //rotate pic according to EXIF data (Francesco Pham)
        try {
            ExifInterface exif = new ExifInterface(filePath);
            int rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            int rotationInDegrees = Utils.exifToDegrees(rotation);
            Matrix matrix = new Matrix();
            if (rotation != 0) {matrix.preRotate(rotationInDegrees);}
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    /**
     * Stores the captured image into a temporary file useful to pass large data between activities
     * and returns the file's path.
     * @param context The reference of the current activity
     * @param bitmap The captured image to store into the file. Not null or empty.
     * @param name The name of the file. Not null or empty.
     * @return The files path
     * @author Leonardo Rossi
     */
    static String tempFileImage(Context context, Bitmap bitmap, String name)
    {

        File outputDir = context.getCacheDir();
        File imageFile = new File(outputDir, name + ".jpg");

        OutputStream os;
        try {
            os = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
            os.flush();
            os.close();
        } catch (Exception e) {
            Log.e(context.getClass().getSimpleName(), "Error writing file", e);
        }

        return imageFile.getAbsolutePath();
    }


    /**
     * @param filePath
     * @return String with the text inside the file pointed by filePath, empty string if file doesn't exist
     */
    public static String getTextFromFile(String filePath) {

        String text ="";
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(filePath));
            text = br.readLine();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return text;

    }

    /**
     * @param filePath path to file
     * @return file extension if exists, null otherwise
     */
    public static String getFileExtension(String filePath) {
        int strLength = filePath.lastIndexOf(".");
        if (strLength > 0)
            return filePath.substring(strLength + 1).toLowerCase();
        return null;
    }

    /**
     * @param filePath path to file
     * @return file name without extension if exists, null otherwise
     */
    public static String getFilePrefix(String filePath) {

        int start = filePath.lastIndexOf("/") + 1;
        int end = filePath.lastIndexOf(".");
        if (end > start)
            return filePath.substring(start, end);
        return null;
    }


    /**
     * @param json JSON object with containing the array given as param
     * @param name array name in JSON object
     * @return the array in the JSON object converted to String
     */
    public static String[] getStringArrayFromJSON(JSONObject json, String name) throws JSONException {
        JSONArray jsonArray = json.getJSONArray(name);
        String[] array = new String[jsonArray.length()];
        for (int i = 0; i < jsonArray.length(); i++) {
            array[i] = jsonArray.getString(i);
        }
        return array;
    }

    /**
     * Convert exif orientation information from image into degree
     * @param exifOrientation Exif orientation value
     * @return degree
     * @author Francesco Pham
     */
    private static int exifToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) { return 90; }
        else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {  return 180; }
        else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {  return 270; }
        return 0;
    }

}
