package unipd.se18.ocrcamera;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** Classe di testing per capire se il riconoscimento blur può funzionare
 * Leonardo Pratesi - gruppo 1
 *
 *
 */
public class BlurCalculatioAllImages extends AppCompatActivity {


    private final String PHOTOS_FOLDER = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/OCRCameraDB";
    ArrayList<Double> blurval = new ArrayList<>();
    ListView listView;
    ArrayAdapter<String> adapter;
    ArrayList<String> imgname = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.listavalori);
        super.onCreate(savedInstanceState);
        File path = new File(PHOTOS_FOLDER);
        String[] fileNames = path.list();

        //blur processing
        for (int i = 0; i < fileNames.length; i++) {
            try {
                //if (getExtension(fileNames[i]) == "jpg") {
                    File f = new File(PHOTOS_FOLDER, fileNames[i]);
                    Bitmap image = BitmapFactory.decodeStream(new FileInputStream(f));
                    Log.e("target1", "got");
                    double blurru = CameraActivity.blurValue(image);
                    blurval.add(blurru);
                    imgname.add(fileNames[i] +" = "+ blurru);


               // }

            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Log.e("errore", "filenotfound");
            }
              catch (IllegalArgumentException  e) {
                e.printStackTrace();
                  Log.e("errore", "illegalargument");

              }
        }
        //View preparation
        listView = (ListView) findViewById(R.id.listview);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, imgname);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });

    }

    /**
     * Method to get file extension leonardo Pratesi
     * @param file
     * @return file extension
     */
    public String getExtension(String file) {
        int dotposition = file.lastIndexOf(".");
        String filename_Without_Ext = file.substring(0, dotposition);
        String ext = file.substring(dotposition + 1, file.length());
        Log.e(ext, ext);
        return ext;

    }

}



