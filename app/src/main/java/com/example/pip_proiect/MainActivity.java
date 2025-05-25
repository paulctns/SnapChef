package com.example.pip_proiect;

import java.util.ArrayList;
import java.util.List;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * {@code MainActivity} reprezintă activitatea principală a aplicației
 * <p>
 * Funcționalitatea principală a aplicației:
 * <ul>
 *     <li>Capturarea unei fotografii cu camera dispozitivului.</li>
 *     <li>Identificarea automată a alimentelor prezente în imagine, folosind API-ul Gemini.</li>
 *     <li>Generarea automată a unor rețete culinare bazate pe lista de ingrediente detectată.</li>
 *     <li>Salvarea și accesarea istoriei de rețete generate, printr-un meniu lateral (Navigation Drawer).</li>
 * </ul>
 * <p>
 * Toate cererile către API-ul Gemini sunt efectuate asincron, iar rezultatele
 * sunt afișate într-un {@link TextView}.
 */
public class MainActivity extends AppCompatActivity {

    private static final List<RecipeEntry> recipeHistory = new ArrayList<>();
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_PERMISSIONS = 100;

    private String currentPhotoPath;
    private TextView txtResult;
    private ImageView imageView;
    private ImageButton cameraButton;
    private ProgressBar progressBar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        );
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(item -> {
            Toast.makeText(MainActivity.this, "Ai selectat: " + item.getTitle(), Toast.LENGTH_SHORT).show();
            drawerLayout.closeDrawers();
            return true;
        });

        txtResult = findViewById(R.id.txtResult);
        imageView = findViewById(R.id.imageView);
        cameraButton = findViewById(R.id.cameraButton);
        progressBar = findViewById(R.id.progressBar);

        txtResult.setMovementMethod(new android.text.method.ScrollingMovementMethod());

        checkAndRequestPermissions();

        cameraButton.setOnClickListener(view -> dispatchTakePictureIntent());
    }

    private void checkAndRequestPermissions() {
        String[] permissions = {
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
        };

        boolean allGranted = true;
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                allGranted = false;
                break;
            }
        }

        if (!allGranted) {
            ActivityCompat.requestPermissions(this, permissions, REQUEST_PERMISSIONS);
        }
    }
    /**
     * Deschide aplicația Cameră pentru a captura o imagine.
     * Creează un fișier temporar pentru a stoca imaginea capturată.
     */
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Toast.makeText(this, "Eroare la crearea fișierului", Toast.LENGTH_SHORT).show();
                return;
            }

            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        getApplicationContext().getPackageName() + ".provider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }
    /**
     * Primește rezultatul după capturarea imaginii.
     * <p>
     * Dacă imaginea a fost capturată cu succes:
     * <ul>
     *     <li>O afișează în ImageView.</li>
     *     <li>Trimite imaginea la API-ul Gemini pentru identificarea alimentelor.</li>
     *     <li>Trimite lista ingredientelor detectate pentru generarea de rețete.</li>
     *     <li>Actualizează meniul lateral cu rețetele generate.</li>
     * </ul>
     *
     * @param requestCode Codul cererii.
     * @param resultCode  Codul rezultatului.
     * @param data        Datele returnate (nefolosite aici).
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            File imgFile = new File(currentPhotoPath);
            if (imgFile.exists()) {
                Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                imageView.setImageBitmap(bitmap);
                txtResult.setText("Se identifică alimentele...");
                progressBar.setVisibility(View.VISIBLE);

                try {
                    byte[] imageBytes = FileUtils.readFileToByteArray(imgFile);

                    GeminiHelper.sendImageAndTextRequest(imageBytes, "Identifică toate alimentele vizibile în imagine. Răspunde doar cu o listă.", new GeminiHelper.GeminiCallback() {
                        @Override
                        public void onResponse(String foodList) {
                            runOnUiThread(() -> txtResult.setText("Alimente detectate:\n" + foodList.trim() + "\n\nSe generează rețetele..."));

                            GeminiHelper.sendTextRequest("Am aceste ingrediente:\n" + foodList.trim() + "\n\nSugerează 3 rețete simple care pot fi preparate cu ele. Pentru fiecare rețetă, oferă: titlul, ingredientele, valorile nutritionale și pașii de preparare.", new GeminiHelper.GeminiCallback() {
                                @Override
                                public void onResponse(String recipes) {
                                    runOnUiThread(() -> {
                                        progressBar.setVisibility(View.GONE);

                                        String ingredientsClean = foodList.trim();
                                        String recipesClean = recipes.trim();
                                        txtResult.setText("Alimente detectate:\n" + ingredientsClean + "\n\nRețete posibile:\n" + recipesClean);

                                        RecipeEntry entry = new RecipeEntry(ingredientsClean, recipesClean);
                                        recipeHistory.add(entry);

                                        updateSidebarMenu();
                                    });
                                }

                                @Override
                                public void onError(String error) {
                                    runOnUiThread(() -> {
                                        progressBar.setVisibility(View.GONE);
                                        txtResult.setText("Eroare la generarea rețetelor: " + error);
                                    });
                                }
                            });
                        }

                        @Override
                        public void onError(String error) {
                            runOnUiThread(() -> {
                                progressBar.setVisibility(View.GONE);
                                txtResult.setText("Eroare la detectarea alimentelor: " + error);
                            });
                        }
                    });
                } catch (IOException e) {
                    progressBar.setVisibility(View.GONE);
                    txtResult.setText("Eroare la citirea imaginii: " + e.getMessage());
                }
            } else {
                txtResult.setText("Eroare: fișierul nu există.");
            }
        }
    }
    /**
     * Actualizează meniul lateral (Navigation Drawer) cu istoricul rețetelor generate.
     * <p>
     * Fiecare item din meniu afișează lista de ingrediente și rețetele aferente
     * când este selectat.
     */
    private void updateSidebarMenu() {
        Menu menu = navigationView.getMenu();
        menu.clear();

        for (int i = 0; i < recipeHistory.size(); i++) {
            RecipeEntry entry = recipeHistory.get(i);
            String title = "Rețeta " + (i + 1);
            menu.add(title).setOnMenuItemClickListener(item -> {
                txtResult.setText("Alimente detectate:\n" + entry.getIngredients() +
                        "\n\nRețete posibile:\n" + entry.getRecipes());
                drawerLayout.closeDrawers();
                return true;
            });
        }
    }
}
