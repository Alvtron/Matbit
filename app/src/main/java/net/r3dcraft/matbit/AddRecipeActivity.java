package net.r3dcraft.matbit;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.UploadTask;

/**
 * Created by Thomas Angeland, student at Ostfold University College, on 25.10.2017.
 * Sources: http://www.theappguruz.com/blog/android-take-photo-camera-gallery-code-sample
 */

public class AddRecipeActivity extends AppCompatActivity {
    private static final String TAG = "AddRecipeActivity";
    private Context context;
    private User user;
    private Recipe recipe;
    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    private ImageView recipeImageThumbnail;
    private String userChoosenTask;
    private Bitmap bitmap;
    private byte[] imageViewRecipeImage;
    private EditText editTitle;
    private EditText editInfo;
    private EditText editAmount;
    private EditText editIngrediens;
    private EditText editStep;
    private EditText editPortions;
    private Spinner spinnerTimeHour;
    private Spinner spinnerTimeMinutes;
    private Spinner spinnerMeasurement;
    private Spinner spinnerCategory;
    private ListView listViewIngredients;
    private ListView listViewSteps;
    private Button btnImageSelect;
    private Button btnAddIngredient;
    private Button btnAddStep;
    private Button btnCreateRecipe;

    private int hour, minutes;
    private List<Integer> timeValues;
    private ArrayAdapter<Integer> hourAdapter;
    private ArrayAdapter<Integer> minutesAdapter;
    private String measurement;
    private ArrayAdapter<CharSequence> measurementAdapter;

    private ArrayList<Ingredient> ingredients;
    private IngredientAdapter ingredientAdapter;

    private ArrayList<Step> steps;
    private ArrayAdapter<Step> stepAdapter;

    private String category;
    private ArrayAdapter<CharSequence> categoryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_recipe);
        context = AddRecipeActivity.this;

        timeValues= new ArrayList<Integer>();
        for (int i = 0; i <= 60; i++) {
            timeValues.add(i);
        }

        ingredients = new ArrayList<Ingredient>();
        ingredientAdapter = new IngredientAdapter(context, ingredients);

        steps = new ArrayList<Step>();

        recipeImageThumbnail = (ImageView) findViewById(R.id.activity_add_recipe_image);

        editTitle = (EditText) findViewById(R.id.activity_add_recipe_title);
        editInfo = (EditText) findViewById(R.id.activity_add_recipe_info);
        editAmount = (EditText) findViewById(R.id.activity_add_recipe_ingredient_amount);
        editIngrediens = (EditText) findViewById(R.id.activity_add_recipe_ingredient_name);
        editStep = (EditText) findViewById(R.id.activity_add_recipe_step);
        editPortions = (EditText) findViewById(R.id.activity_add_recipe_portions);

        spinnerTimeHour = (Spinner) findViewById(R.id.activity_add_recipe_time_hours);
        hourAdapter = new ArrayAdapter<Integer>(context, android.R.layout.simple_spinner_item, timeValues);
        hourAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTimeHour.setAdapter(hourAdapter);
        spinnerTimeHour.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                hour = StringTools.stringToInt(parent.getItemAtPosition(position).toString());
            }
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerTimeMinutes = (Spinner) findViewById(R.id.activity_add_recipe_time_minutes);
        minutesAdapter = new ArrayAdapter<Integer>(context, android.R.layout.simple_spinner_item, timeValues);
        minutesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTimeMinutes.setAdapter(minutesAdapter);
        spinnerTimeMinutes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                minutes = StringTools.stringToInt(parent.getItemAtPosition(position).toString());
            }
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerMeasurement = (Spinner) findViewById(R.id.activity_add_recipe_ingredient_measurement);
        measurementAdapter = ArrayAdapter.createFromResource(this, R.array.measurements, android.R.layout.simple_spinner_item);
        measurementAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMeasurement.setAdapter(measurementAdapter);
        spinnerMeasurement.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                measurement = parent.getItemAtPosition(position).toString();
            }
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerCategory = (Spinner) findViewById(R.id.activity_add_recipe_category);
        categoryAdapter = ArrayAdapter.createFromResource(this, R.array.categories, android.R.layout.simple_spinner_item);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(categoryAdapter);
        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                category = parent.getItemAtPosition(position).toString();
            }
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        listViewIngredients = (ListView) findViewById(R.id.activity_add_recipe_listview_ingredients);
        listViewSteps = (ListView) findViewById(R.id.activity_add_recipe_listview_steps);

        btnImageSelect = (Button) findViewById(R.id.activity_add_recipe_image_btn);
        btnImageSelect.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });
        btnAddIngredient = (Button) findViewById(R.id.activity_add_recipe_btn_ingredient);
        btnAddIngredient.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Ingredient ingredient = new Ingredient("Main", editIngrediens.getText().toString(), Double.parseDouble(editAmount.getText().toString()), measurement);
                ingredientAdapter.getData().add(ingredient);
                listViewIngredients.setAdapter(ingredientAdapter);
                ingredientAdapter.notifyDataSetChanged();

            }
        });
        btnAddStep = (Button) findViewById(R.id.activity_add_recipe_btn_step);
        btnAddStep.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                steps.add(new Step(editStep.getText().toString()));
                stepAdapter = new ArrayAdapter<Step>(context, android.R.layout.simple_list_item_1, steps);
                listViewSteps.setAdapter(stepAdapter);
                stepAdapter.notifyDataSetChanged();
            }
        });
        btnCreateRecipe = (Button) findViewById(R.id.activity_add_recipe_btn_create_recipe);
        btnCreateRecipe.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean valid = true;
                Integer portions = StringTools.stringToInt(editPortions.getText().toString());
                if (imageViewRecipeImage == null) {
                    Toast.makeText(context, "Du må laste opp et bilde!", Toast.LENGTH_SHORT).show();
                    valid = false;
                }
                if(editTitle.getText().toString().trim().matches("")) {
                    editTitle.setError("Du må ha en tittel");
                    valid = false;
                }
                if(hour * 60 + minutes < 1) {
                    Toast.makeText(context, "Velg en tid som virker fornuftig", Toast.LENGTH_SHORT).show();
                    valid = false;
                }
                if(ingredients.isEmpty()) {
                    editIngrediens.setError("Du må skrive minst en ingrediens");
                    valid = false;
                }
                if(steps.isEmpty()) {
                    editStep.setError("Du må skrive minst ett steg");
                    valid = false;
                }
                if(category == null || category.trim() == "") {
                    valid = false;
                }
                if(portions == null || portions < 1) {
                    editPortions.setError("Porsjoner kan ikke være 0");
                    valid = false;
                }

                if (valid) {
                    recipe = new Recipe();
                    recipe.createNewRecipe(editTitle.getText().toString(),
                            hour * 60 + minutes,
                            Integer.parseInt(editPortions.getText().toString()),
                            category,
                            editInfo.getText().toString(),
                            steps,
                            ingredients
                    );

                    // Upload recipe data to new unique firebase key
                    recipe.setId(MatbitDatabase.uploadNewRecipe(recipe.getData()));
                    // Add and upload new recipe to user
                    user.addRecipe(recipe.getId(), recipe.getData().getDatetime_created());
                    user.uploadRecipes();
                    // Upload recipe photo
                    UploadTask uploadTask = MatbitDatabase.RECIPE_PHOTOS.child(recipe.getId() +  ".jpg").putBytes(imageViewRecipeImage);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle unsuccessful uploads
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                            Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        }
                    });
                    // Show dialog box for next step
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setMessage("Oppskriften er lastet opp!")
                            .setPositiveButton("Gå til oppskrift", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    finish();
                                    MatbitDatabase.gotToRecipe(context, recipe);
                                }
                            })
                            .setNegativeButton("Gå til startsiden", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    finish();
                                    startActivity(new Intent(context, MainActivity.class));
                                }
                            });
                    builder.show();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        MatbitDatabase.USERS.child(MatbitDatabase.USER.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user = new User(dataSnapshot);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "createUserFromDatabase: Cancelled", databaseError.toException());
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case Utility.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if(userChoosenTask.equals("Take Photo"))
                        cameraIntent();
                    else if(userChoosenTask.equals("Choose from Library"))
                        galleryIntent();
                } else {
                    //code for deny
                }
                break;
        }
    }

    private void selectImage() {
        final CharSequence[] items = { "Take Photo", "Choose from Library",
                "Cancel" };

        AlertDialog.Builder builder = new AlertDialog.Builder(AddRecipeActivity.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result=Utility.checkPermission(AddRecipeActivity.this);

                if (items[item].equals("Take Photo")) {
                    userChoosenTask ="Take Photo";
                    if(result)
                        cameraIntent();

                } else if (items[item].equals("Choose from Library")) {
                    userChoosenTask ="Choose from Library";
                    if(result)
                        galleryIntent();

                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void galleryIntent()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"),SELECT_FILE);
    }

    private void cameraIntent()
    {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE)
                onSelectFromGalleryResult(data);
            else if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);
        }
    }

    private void onCaptureImageResult(Intent data) {
        bitmap = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        imageViewRecipeImage = bytes.toByteArray();
        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");

        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        recipeImageThumbnail.setImageBitmap(bitmap);
    }

    private void onSelectFromGalleryResult(Intent data) {

        bitmap = null;
        if (data != null) {
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        recipeImageThumbnail.setImageBitmap(bitmap);
    }
}