package com.prince.bakingapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.prince.bakingapp.data.Contract.IngredientEntry;
import static com.prince.bakingapp.data.Contract.RecipeEntry;
import static com.prince.bakingapp.data.Contract.StepEntry;

class DbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "bakingApp.db";
    private static final int DATABASE_VERSION = 1;


    DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_RECIPE_TABLE = "CREATE TABLE " + RecipeEntry.TABLE_NAME + " ("
                + RecipeEntry._ID + " INTEGER PRIMARY KEY, "
                + RecipeEntry.COLUMN_NAME + " TEXT NOT NULL, "
                + RecipeEntry.COLUMN_SERVINGS + " INTEGER NOT NULL, "
                + RecipeEntry.COLUMN_IMAGE + " TEXT "
                + ");";

        db.execSQL(SQL_CREATE_RECIPE_TABLE);

        final String SQL_CREATE_INGREDIENT_TABLE = "CREATE TABLE " + IngredientEntry.TABLE_NAME + " ("
                + IngredientEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + IngredientEntry.COLUMN_QUANTITY + " INTEGER NOT NULL, "
                + IngredientEntry.COLUMN_MEASURE + " TEXT NOT NULL, "
                + IngredientEntry.COLUMN_INGREDIENT + " TEXT NOT NULL, "
                + IngredientEntry.COLUMN_RECIPE_ID + " INTEGER NOT NULL "
                + ");";

        db.execSQL(SQL_CREATE_INGREDIENT_TABLE);

        final String SQL_CREATE_STEP_TABLE = "CREATE TABLE " + StepEntry.TABLE_NAME + " ("
                + StepEntry._ID + " INTEGER NOT NULL, "
                + StepEntry.COLUMN_SHORT_DESCRIPTION + " TEXT NOT NULL, "
                + StepEntry.COLUMN_DESCRIPTION + " TEXT NOT NULL, "
                + StepEntry.COLUMN_VIDEO_URL + " TEXT, "
                + StepEntry.COLUMN_THUMBNAIL_URL + " TEXT, "
                + StepEntry.COLUMN_RECIPE_ID + " INTEGER NOT NULL,"
                + "PRIMARY KEY (" + StepEntry._ID + ", " + StepEntry.COLUMN_RECIPE_ID + ") "
                + ");";

        db.execSQL(SQL_CREATE_STEP_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(" DROP TABLE IF EXISTS " + RecipeEntry.TABLE_NAME);
        onCreate(db);
    }
}
