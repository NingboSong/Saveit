package com.example.saveit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.fragment.app.DialogFragment;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import helpclass.TypeSpinnerArrayAdapter;

/**
 * The Money activity.
 * @author Zilin.Song
 */
public class MoneyActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {


    /**
     * The constant MONEY_VALUE_KEY.
     */
    public static final String MONEY_VALUE_KEY = "money_value";
    /**
     * The constant MONEY_DATE_KEY.
     */
    public static final String MONEY_DATE_KEY = "money_date";
    /**
     * The constant MONEY_INSTRUCTIONS_KEY.
     */
    public static final String MONEY_INSTRUCTIONS_KEY = "money_instructions";
    /**
     * The constant MONEY_TYPE_KEY.
     */
    public static final String MONEY_TYPE_KEY = "money_type";
    /**
     * The constant MONEY_ITEM_POSITION_KEY.
     */
    public static final String MONEY_ITEM_POSITION_KEY = "money_item_pos";
    /**
     * The constant MONEY_PATH_KEY.
     */
    public static final String MONEY_PATH_KEY = "money_path";

    /**
     * The constant EMPTY_STRING.
     */
    public static final String EMPTY_STRING = "";
    private int itemPosition = -1;
    /**
     * The Current photo path.
     */
    String currentPhotoPath;


    private EditText valueEditText;
    private EditText instructionsEditText;
    private EditText dateEditText;
    private Button datePickButton;
    private Spinner typeSpinner;
    private FloatingActionButton submitDataButton;
    private FloatingActionButton CameraButton;
    private ImageView imageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_money);
        assignObject();
        initTypeSpinner();
        initListener();
        initContent();
    }

    private void assignObject() {
        valueEditText = findViewById(R.id.ValueEditText);
        instructionsEditText = findViewById(R.id.instructionsEditText);
        typeSpinner = findViewById(R.id.spinner);
        submitDataButton = findViewById(R.id.submitDataButton);
        CameraButton = findViewById(R.id.CameraButton);
        dateEditText = findViewById(R.id.DateEditText);
        datePickButton = findViewById(R.id.DatePickBottom);
        imageView = findViewById(R.id.imageView);


    }

    private void initTypeSpinner() {
        ArrayAdapter<String> mAdapter;
        String[] mStringArray;
        mStringArray = getResources().getStringArray(R.array.costorincome);
        mAdapter = new TypeSpinnerArrayAdapter(MoneyActivity.this, mStringArray);
        typeSpinner.setAdapter(mAdapter);
    }

    private void initListener() {

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentPhotoPath != null) {
                    fullScreen();

                }
            }
        });
        imageView.setLongClickable(true);
        imageView.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                imageView.setImageDrawable(null);
                currentPhotoPath = null;
                return true;
            }
        });

        CameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });

        datePickButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment datePicker = new DatePickerFragment();
                datePicker.show(getSupportFragmentManager(), "date picker");
            }
        });

        typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String info = adapterView.getItemAtPosition(i).toString();
                if (info.replaceAll("\u00A0", "").equals("Cost")) {
                    valueEditText.setTextColor(Color.GREEN);
                } else
                    valueEditText.setTextColor(Color.RED);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        submitDataButton.setOnClickListener(v -> {
            String value = valueEditText.getText().toString();
            final String instruction = instructionsEditText.getText().toString();
            final String type = typeSpinner.getSelectedItem().toString();
            final String date = dateEditText.getText().toString();
            if (type.equals("Cost") && !value.startsWith("-")) {
                if (value.startsWith("+"))
                    value = value.substring(1);
                value = "-" + value;
            } else if (type.equals("Income") && !value.startsWith("+")) {
                if (value.startsWith("-"))
                    value = value.substring(1);
                value = "+" + value;
            }
            if (isComplete(value, instruction, type, date)) {
                final Intent resultIntent = getFilledIntent(value, instruction, type, date, currentPhotoPath);
                setResult(Activity.RESULT_OK, resultIntent);
                finish();
            } else {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private boolean isComplete(final String value, final String instruction, final String type, final String date) {
        return !value.equals(EMPTY_STRING) && !instruction.equals(EMPTY_STRING) && !type.equals(EMPTY_STRING) && !date.equals(EMPTY_STRING);
    }

    private Intent getFilledIntent(final String value, final String instruction, final String type, final String date, final String path) {
        final Intent activityResultIntent = new Intent();

        // check and set list position
        if (itemPosition != -1)
            activityResultIntent.putExtra(MONEY_ITEM_POSITION_KEY, itemPosition);

        // use params to fill intent
        activityResultIntent.putExtra(MONEY_VALUE_KEY, value);
        activityResultIntent.putExtra(MONEY_DATE_KEY, date);
        activityResultIntent.putExtra(MONEY_INSTRUCTIONS_KEY, instruction);
        activityResultIntent.putExtra(MONEY_TYPE_KEY, type);
        activityResultIntent.putExtra(MONEY_PATH_KEY, path);

        return activityResultIntent;
    }

    /**
     * @param view       the picker associated with the dialog
     * @param year       the selected year
     * @param month      the selected month (0-11 for compatibility with
     *                   {@link Calendar#MONTH})
     * @param dayOfMonth the selected day of the month (1-31, depending on
     */
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        SimpleDateFormat format0 = new SimpleDateFormat("dd.MM.yyyy");
        String time = format0.format(c.getTime());
        dateEditText.setText(time);
    }

    private void initContent() {
        final Intent currentIntent = getIntent();
        itemPosition = currentIntent.getIntExtra(MONEY_ITEM_POSITION_KEY, -1);

        if (currentIntent.hasExtra(MONEY_VALUE_KEY))
            valueEditText.setText(currentIntent.getStringExtra(MONEY_VALUE_KEY));

        if (currentIntent.hasExtra(MONEY_INSTRUCTIONS_KEY))
            instructionsEditText.setText(currentIntent.getStringExtra(MONEY_INSTRUCTIONS_KEY));

        if (currentIntent.hasExtra(MONEY_TYPE_KEY)) {
            String type = currentIntent.getStringExtra(MONEY_TYPE_KEY);
            // obtain index of selected item by creating list from given string array
            int index = Arrays.asList(getResources().getStringArray(R.array.costorincome)).indexOf(type);
            typeSpinner.setSelection(index);
        }
        if (currentIntent.hasExtra(MONEY_DATE_KEY)) {
            dateEditText.setText(currentIntent.getStringExtra(MONEY_DATE_KEY));
        }
        if (currentIntent.hasExtra(MONEY_PATH_KEY)) {
            currentPhotoPath = currentIntent.getStringExtra(MONEY_PATH_KEY);
            File photoFile = new File(currentPhotoPath);
            Uri photoURI = FileProvider.getUriForFile(this,
                    "com.example.saveit",
                    photoFile);
            imageView.setImageURI(photoURI);
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.saveit",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, 0);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new android.icu.text.SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        File photoFile = new File(currentPhotoPath);
        Uri photoURI = FileProvider.getUriForFile(this,
                "com.example.saveit",
                photoFile);

        imageView.setImageURI(photoURI);

    }

    /**
     * Full screen , start BigPhotoActivity.
     */
    public void fullScreen() {
        Intent intent = new Intent(this, BigPhotoActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("URI", currentPhotoPath);
        intent.putExtras(bundle);
        startActivity(intent);
    }

}



