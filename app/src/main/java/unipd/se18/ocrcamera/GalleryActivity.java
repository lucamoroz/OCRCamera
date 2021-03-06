package unipd.se18.ocrcamera;



import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import unipd.se18.ocrcamera.inci.Ingredient;
import unipd.se18.ocrcamera.inci.IngredientsExtractor;


/**
 * Gallery activity
 * @author Stefano Romanello - Fragment suggestion Leonardo Rossi
 */
public class GalleryActivity extends BaseActivity {

    //Code for internet permission
    private static final int REQUEST_PERMISSION_CODE = 500;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);


        if(verifyStoragePermission())
        {
            LoadHomeCards loadHomeCards = new LoadHomeCards();
            loadHomeCards.execute();
        }

    }

    /**
     * Function for load the home fragment from onActivityCreated and
     * onRequestPermissionsResult in case I get the storage permission from the permission
     */
    FragmentManager fm;
    private void loadHomeFragment()
    {
        fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.fragmentPlaceHolder, new MainFragment(),getString(R.string.homeFragmentTag));
        ft.commit();
    }

    @Override
    public void onBackPressed() {
        int backStackEntryCount = getSupportFragmentManager().getBackStackEntryCount();
        //If there is more than 1 fragment running I close the
        //current one (DetailsFragment) and return to the prevous (HomeFragment)
        //Or I just call the default onBackPressed which closes the activity
        if (backStackEntryCount != 0) {
            fm.popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    /**
     * Simple async for load the images while showing a SpinnerDialog
     */
    private class LoadHomeCards extends AsyncTask<View, Integer, Void>
    {
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute()
        {
            progressDialog = new ProgressDialog(GalleryActivity.this);
            progressDialog.setTitle(getString(R.string.progressDialogGalleryTitle));
            progressDialog.setMessage(getString(R.string.progressDialogGalleryMessage));
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.show();
        }

        /***
         * Load the actual fragment with the cards
         */
        @Override
        protected Void doInBackground(View... voids)
        {
            loadHomeFragment();
            return null;
        }

        /**
         * Dismiss the dialog
         * @param params is the result of the connection test
         */
        @Override
        protected void onPostExecute(Void params)
        {
            progressDialog.dismiss();
        }
    }



    /************************************************************/
    /***************************FRAGMENTS************************/
    /************************************************************/


    /**
     * Fragment of the gallery layout
     * @author Romanello Stefano
     */
    public static class MainFragment extends Fragment {

        GalleryManager.RecycleCardsAdapter cardAdapter;

        /**
         * Event triggered once everything in the activity have finished loading
         */
        @Override
        public void onActivityCreated(Bundle savedInstanceState)
        {
            super.onActivityCreated(savedInstanceState);
            ActionBar actionBar =((GalleryActivity)getActivity()).getSupportActionBar();
            actionBar.setTitle(R.string.galleryFragmentTitle);
            actionBar.setDisplayHomeAsUpEnabled(false);
            actionBar.setHomeButtonEnabled(false);
        }

        /**
         * Event triggered when I create the view
         */
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState)
        {
            // Defines the xml file for the fragment
            return inflater.inflate(R.layout.activity_gallery_content, parent, false);
        }

        /**
         * This event is triggered soon after onCreateView().
         * Any view setup should occur here.
         */
        @Override
        public void onViewCreated(View view, Bundle savedInstanceState)
        {
            //Create the container for all my cards
            RecyclerView picturesRecycleView = view.findViewById(R.id.recycle_view);
            picturesRecycleView.setHasFixedSize(true);

            //Set a grid (2 columns) from my recycleView
            GridLayoutManager recycleLayoutManager = new GridLayoutManager(view.getContext(), 2, GridLayoutManager.VERTICAL, false);
            picturesRecycleView.setLayoutManager(recycleLayoutManager);
            picturesRecycleView.setItemAnimator(new DefaultItemAnimator());

            //Load the cards using the RecycleCardsAdapter into the picturesRecycleView
            ArrayList<GalleryManager.PhotoStructure> photos = GalleryManager.getImages();
            cardAdapter = new GalleryManager.RecycleCardsAdapter(view.getContext(), photos);
            picturesRecycleView.setAdapter(cardAdapter);
        }


        /**
         * Event when the main fragment change its hidden status.
         * @param hidden true=is hidden, false=is not hidden.
         */
        @Override
        public void onHiddenChanged(boolean hidden) {
            //I have to understand if the user has deleted an image
            //If the answer is yes I have to remove the item from the recycleView
            if(!hidden)
            {
                //When I delete something from the DetailsFragment using the method GalleryManager.deleteImage(photoInfos);
                //I set in the GalleryManager an object containing the PhotoStructure to delete.
                //Calling .getDeletedPhotoPosition() I check if the object is null, if not I return the position of
                //the PhotoStructure to delete

                int deletedPosition=cardAdapter.getDeletedPhotoPosition();
                if(deletedPosition!=-1)
                {
                    //have to remove the photo from the recycler
                    cardAdapter.photosList.remove(deletedPosition);
                    cardAdapter.notifyItemRemoved(deletedPosition);
                }

                //Restore the actionBar
                ActionBar actionBar =((GalleryActivity)getActivity()).getSupportActionBar();
                actionBar.setTitle(R.string.galleryFragmentTitle);
                actionBar.setDisplayHomeAsUpEnabled(false);
                actionBar.setHomeButtonEnabled(false);
            }

        }
    }

    /**
     * Fragment of the detailed photo layout
     * @author Romanello Stefano
     */
    public static class DetailFragment extends Fragment
    {
        //Object used to store the photo that I want to load.
        private GalleryManager.PhotoStructure photoInfos;

        /**
         * Event triggered once everything in the activity have finished loading
         */
        @Override
        public void onActivityCreated(Bundle savedInstanceState)
        {
            super.onActivityCreated(savedInstanceState);

            ActionBar actionBar =((GalleryActivity)getActivity()).getSupportActionBar();
            actionBar.setTitle(R.string.galleryDetailsFragmentTitle);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }

        /**
         * Event triggered when I create the view
         */
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState)
        {
            // Defines the xml file for the fragment
            View view = inflater.inflate(R.layout.activitygallerydetails, parent, false);
            //Very important part. Without this line the fragment does't know that it has a menu and it will not trigger onOptionsItemSelected
            setHasOptionsMenu(true);
            return view;
        }

        /**
         * Add the delete button to the actionBar
         */
        @Override
        public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
            // Inflate the menu items for use in the action bar
            inflater.inflate(R.menu.gallery_details_menu, menu);
            super.onCreateOptionsMenu(menu, inflater);
        }

        /**
         * Listner for actionBar buttons of the DetailsFragment
         * @param item value of the item clicked
         */
        @Override
        public boolean onOptionsItemSelected(MenuItem item)
        {
            switch (item.getItemId()) {
                //I've clicked the back buton on the actionBar
                case android.R.id.home: closeDetailFragment(); break;
                //Delete the current photo and close the detailsFragment
                case R.id.galleryDelete: deleteCurrentPhoto(); break;
                default: break;
            }
            return super.onOptionsItemSelected(item);
        }

        /**
         * This event is triggered soon after onCreateView()
         */
        @Override
        public void onViewCreated(View view, Bundle savedInstanceState)
        {
            //Load the information from the bundle
            Bundle bundle = getArguments();
            if (bundle != null) {
                photoInfos = (GalleryManager.PhotoStructure)bundle.getSerializable(getString(R.string.serializableObjectName));
            }

            ListView ingredientsListView = ((GalleryActivity) getActivity()).findViewById(R.id.ingredients_list_gallery);
            ImageView imageView = ((GalleryActivity) getActivity()).findViewById(R.id.imageViewGalleryDetailPhoto);

            //Convert the arrayList<String> of ingredients obtained from the metadata to List<Ingredient>

            //Get a single string with all ingredients fount from the ocr
            String formattedIngredients = photoInfos.ingredients.toString()
                    .replace("[", "")  //remove the right bracket
                    .replace("]", "")  //remove the left bracket
                    .trim();
            IngredientsExtractor extractor = InciSingleton.getInstance(((GalleryActivity) getActivity())).getIngredientsExtractor();
            List<Ingredient> ingredientsToPrint = extractor.findListIngredients(formattedIngredients);

            //Use tha adapter to display the ingredients
            AdapterIngredient adapter = new AdapterIngredient(((GalleryActivity) getActivity()), ingredientsToPrint);
            ingredientsListView.setAdapter(adapter);

            //event onClick for display the details of the ingredients
            ingredientsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Ingredient selectedIngredient = (Ingredient) parent.getItemAtPosition(position);

                    //performing a click on OCR recognized text causes a crash (because selectedIngredient is null)
                    if(selectedIngredient != null) {
                        String inciName = selectedIngredient.getInciName();
                        String description = selectedIngredient.getDescription();
                        String function = selectedIngredient.getFunction();
                        FragmentManager fm = getFragmentManager();
                        IngredientDetailsFragment detailsFragment = IngredientDetailsFragment.newInstance(inciName, description, function);
                        detailsFragment.show(fm, "fragment_ingredient_details");
                    }
                }
            });

            imageView.setImageBitmap(photoInfos.photo);
        }

        /**
         * Action triggered when I click the delete icon in the actionBar
         * Method colled from onOptionsItemSelected
         */
        private void deleteCurrentPhoto()
        {
            try {
                //Delete the actual image
                GalleryManager.deleteImage(photoInfos);

                //Close current fragment
                closeDetailFragment();

            } catch (IOException e) {
                //Manage the exaption with a dialog
                AlertDialog.Builder builder;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder = new AlertDialog.Builder(getContext(), android.R.style.Theme_Material_Dialog_Alert);
                } else {
                    builder = new AlertDialog.Builder(getContext());
                }
                builder.setTitle("Error")
                        .setMessage("Error while deleting the photo. Retry")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                //I'm not performing any action because this allert is just for provide information
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        }

        /**
         * Method for closing the current fragment
         */
        private void closeDetailFragment()
        {
            getFragmentManager().popBackStack();
        }
    }


    /*********************************************/
    /******************PERMISSIONS****************/
    /*********************************************/


    /**
     * Verify if the user granted the permission
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if(requestCode==REQUEST_PERMISSION_CODE)
        {
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                // permission denied
                showPermissionErrorDialog();
            }
            else
            {
                LoadHomeCards loadHomeCards = new LoadHomeCards();
                loadHomeCards.execute();
            }
        }
    }

    /**
     * Verify if the app has the storage permission
     * @return boolean of the current status (before asking the permission)
     * if its false this prevent the app to load the fragment that uses the internal storage
     */
    public boolean verifyStoragePermission()
    {
        //Check and in case Ask for permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_PERMISSION_CODE);
            return false;
        }
        else
        {
            return true;
        }
    }

    /**
     * Show a simple error message before closing the activity in case bad permissioN
     */
    public void showPermissionErrorDialog()
    {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(this);
        }
        builder.setTitle(R.string.alertDialogGalleryTitle)
                .setMessage(R.string.alertDialogGalleryMessage)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with closing the activity
                        finish();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    /*
     * override BaseActivity's abstract methods to give information
     * about the layout and menu item that should be selected
     */

    @Override
    int getContentViewId(){
        return R.layout.activity_gallery;
    }

    @Override
    int getNavigationMenuItemId(){
        return R.id.nav_gallery;
    }

}
