package com.prince.bakingapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class BakeAppDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "MobileChef.db";
    private static final int DATABASE_VERSION = 1;


    BakeAppDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_RECIPE_TABLE = "CREATE TABLE " + BakeAppContract.RecipeEntry.TABLE_NAME + " ("
                + BakeAppContract.RecipeEntry._ID + " INTEGER PRIMARY KEY, "
                + BakeAppContract.RecipeEntry.COLUMN_NAME + " TEXT NOT NULL, "
                + BakeAppContract.RecipeEntry.COLUMN_SERVINGS + " INTEGER NOT NULL, "
                + BakeAppContract.RecipeEntry.COLUMN_IMAGE + " TEXT "
                + ");";

        db.execSQL(SQL_CREATE_RECIPE_TABLE);

        final String SQL_CREATE_INGREDIENT_TABLE = "CREATE TABLE " + BakeAppContract.IngredientEntry.TABLE_NAME + " ("
                + BakeAppContract.IngredientEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + BakeAppContract.IngredientEntry.COLUMN_QUANTITY + " INTEGER NOT NULL, "
                + BakeAppContract.IngredientEntry.COLUMN_MEASURE + " TEXT NOT NULL, "
                + BakeAppContract.IngredientEntry.COLUMN_INGREDIENT + " TEXT NOT NULL, "
                + BakeAppContract.IngredientEntry.COLUMN_RECIPE_ID + " INTEGER NOT NULL "
                + ");";

        db.execSQL(SQL_CREATE_INGREDIENT_TABLE);

        final String SQL_CREATE_STEP_TABLE = "CREATE TABLE " + BakeAppContract.StepEntry.TABLE_NAME + " ("
                + BakeAppContract.StepEntry._ID + " INTEGER NOT NULL, "
                + BakeAppContract.StepEntry.COLUMN_SHORT_DESCRIPTION + " TEXT NOT NULL, "
                + BakeAppContract.StepEntry.COLUMN_DESCRIPTION + " TEXT NOT NULL, "
                + BakeAppContract.StepEntry.COLUMN_VIDEO_URL + " TEXT, "
                + BakeAppContract.StepEntry.COLUMN_THUMBNAIL_URL + " TEXT, "
                + BakeAppContract.StepEntry.COLUMN_RECIPE_ID + " INTEGER NOT NULL,"
                + "PRIMARY KEY (" + BakeAppContract.StepEntry._ID + ", " + BakeAppContract.StepEntry.COLUMN_RECIPE_ID + ") "
                + ");";

        db.execSQL(SQL_CREATE_STEP_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(" DROP TABLE IF EXISTS " + BakeAppContract.RecipeEntry.TABLE_NAME);
        onCreate(db);
    }
}
