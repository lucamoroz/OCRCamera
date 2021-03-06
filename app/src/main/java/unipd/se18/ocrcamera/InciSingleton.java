package unipd.se18.ocrcamera;

import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import unipd.se18.ocrcamera.inci.Inci;
import unipd.se18.ocrcamera.inci.Ingredient;
import unipd.se18.ocrcamera.inci.IngredientsExtractor;
import unipd.se18.ocrcamera.inci.NameMatchIngredientsExtractor;
import unipd.se18.ocrcamera.inci.TextAutoCorrection;

/**
 * Using singleton design pattern for single time inci db loading, ingredients extractor and
 * text corrector initialization, allergens manager initialization.
 * @author Francesco Pham
 */
public class InciSingleton {
    private static volatile InciSingleton ourInstance;
    private static final String TAG = "TextAutoCorrection";

    private IngredientsExtractor ingredientsExtractor;
    private TextAutoCorrection textCorrector;
    private List<Ingredient> listInciIngredients;
    private AllergensManager allergensManager;

    public static InciSingleton getInstance(Context context) {
        if (ourInstance == null) {
            synchronized (InciSingleton.class) {
                if (ourInstance == null) ourInstance = new InciSingleton(context);
            }
        }
        return ourInstance;
    }

    private InciSingleton(Context context) {
        if (ourInstance != null){
            throw new RuntimeException("Use getInstance() method to get the single instance of this class.");
        }

        //Load list of ingredients from INCI DB
        InputStream inciDbStream = context.getResources().openRawResource(R.raw.incidb);
        try {
            this.listInciIngredients = Inci.getListIngredients(inciDbStream);
        } catch (IOException e) {
            Log.e(TAG, "Error reading csv");
        }

        //initialize ingredients extractor
        this.ingredientsExtractor = new NameMatchIngredientsExtractor(listInciIngredients);

        //Load wordlist and initialize text corrector
        InputStream wordListStream = context.getResources().openRawResource(R.raw.inciwordlist);
        try {
            this.textCorrector = new TextAutoCorrection(wordListStream);
        } catch (IOException e) {
            Log.e(TAG,"Error reading word list");
        }

        //Load allergens manager
        this.allergensManager = new AllergensManager(context);
    }

    public IngredientsExtractor getIngredientsExtractor(){
        return this.ingredientsExtractor;
    }

    public TextAutoCorrection getTextCorrector(){
        return this.textCorrector;
    }

    public List<Ingredient> getListInciIngredients() {
        return this.listInciIngredients;
    }

    public AllergensManager getAllergensManager() {
        return this.allergensManager;
    }

}
