package com.prince.bakingapp.data;

import android.content.ContentProvider;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;

import static com.prince.bakingapp.data.BakeAppContract.AUTHORITY;
import static com.prince.bakingapp.data.BakeAppContract.PATH_INGREDIENT;
import static com.prince.bakingapp.data.BakeAppContract.PATH_RECIPE;
import static com.prince.bakingapp.data.BakeAppContract.PATH_SERVINGS;
import static com.prince.bakingapp.data.BakeAppContract.PATH_STEP;

/**
 * Created by princ on 08-07-2017.
 */
public class BakeAppRecipeProvider extends ContentProvider {
    private static final int RECIPE = 100;
    private static final int RECIPE_BY_ID = 101;
    private static final int INGREDIENT = 200;
    private static final int INGREDIENT_SERVINGS = 201;
    private static final int STEP = 300;
    private static final int STEP_BY_ID = 301;

    private static final UriMatcher uriMatcher = buildUriMatcher();

    private BakeAppDbHelper bakeAppDbHelper;

    private static UriMatcher buildUriMatcher() {
        UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        matcher.addURI(AUTHORITY, PATH_RECIPE, RECIPE);
        matcher.addURI(AUTHORITY, PATH_RECIPE + "/#", RECIPE_BY_ID);
        matcher.addURI(AUTHORITY, PATH_RECIPE + "/#/" + PATH_INGREDIENT, INGREDIENT);
        matcher.addURI(AUTHORITY, PATH_RECIPE + "/#/" + PATH_INGREDIENT + "/" + PATH_SERVINGS + "/#/#", INGREDIENT_SERVINGS);
        matcher.addURI(AUTHORITY, PATH_RECIPE + "/#/" + PATH_STEP, STEP);
        matcher.addURI(AUTHORITY, PATH_RECIPE + "/#/" + PATH_STEP + "/#", STEP_BY_ID);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        bakeAppDbHelper = new BakeAppDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        Cursor returnCursor;
        SQLiteDatabase db = bakeAppDbHelper.getReadableDatabase();

        switch (uriMatcher.match(uri)) {
            case RECIPE:
                returnCursor = db.query(
                        BakeAppContract.RecipeEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;

            case RECIPE_BY_ID:
                returnCursor = db.query(
                        BakeAppContract.RecipeEntry.TABLE_NAME,
                        projection,
                        BakeAppContract.RecipeEntry._ID + " = ?",
                        new String[]{Long.toString(BakeAppContract.RecipeEntry.getRecipeId(uri))},
                        null,
                        null,
                        sortOrder
                );
                break;
            case INGREDIENT:
                returnCursor = db.query(
                        BakeAppContract.IngredientEntry.TABLE_NAME,
                        projection,
                        BakeAppContract.IngredientEntry.COLUMN_RECIPE_ID + " = ?",
                        new String[]{Long.toString(BakeAppContract.IngredientEntry.getRecipeId(uri))},
                        null,
                        null,
                        sortOrder
                );
                break;
            case STEP:
                returnCursor = db.query(
                        BakeAppContract.StepEntry.TABLE_NAME,
                        projection,
                        BakeAppContract.StepEntry.COLUMN_RECIPE_ID + " = ?",
                        new String[]{Long.toString(BakeAppContract.StepEntry.getRecipeId(uri))},
                        null,
                        null,
                        sortOrder
                );
                break;
            case STEP_BY_ID:
                returnCursor = db.query(
                        BakeAppContract.StepEntry.TABLE_NAME,
                        projection,
                        BakeAppContract.StepEntry.COLUMN_RECIPE_ID + " = ? AND " + BakeAppContract.StepEntry._ID + " = ?",
                        new String[]{Long.toString(BakeAppContract.StepEntry.getRecipeId(uri)),
                                Long.toString(BakeAppContract.StepEntry.getStepId(uri))},
                        null,
                        null,
                        sortOrder
                );
                break;
            default:
                throw new UnsupportedOperationException("Unknown URI:" + uri);
        }

        Context context = getContext();
        if (context != null) {
            returnCursor.setNotificationUri(context.getContentResolver(), uri);
        }

        return returnCursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch (uriMatcher.match(uri)) {
            case RECIPE:
                return "vnd.android.cursor.dir" + "/" + AUTHORITY + "/" + PATH_RECIPE;
            case RECIPE_BY_ID:
                return "vnd.android.cursor.item" + "/" + AUTHORITY + "/" + PATH_RECIPE;
            case INGREDIENT:
                return "vnd.android.cursor.dir" + "/" + AUTHORITY + "/" + PATH_RECIPE;
            case STEP:
                return "vnd.android.cursor.dir" + "/" + AUTHORITY + "/" + PATH_RECIPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        SQLiteDatabase db = bakeAppDbHelper.getWritableDatabase();
        Uri returnUri;
        long insertedId;

        switch (uriMatcher.match(uri)) {
            case RECIPE:
                insertedId = db.insertWithOnConflict(
                        BakeAppContract.RecipeEntry.TABLE_NAME,
                        null,
                        values,
                        SQLiteDatabase.CONFLICT_REPLACE
                );
                //returns recipe/[recipeId]
                returnUri = ContentUris.withAppendedId(BakeAppContract.RecipeEntry.buildDirUri(), insertedId);
                break;
            case INGREDIENT:
                insertedId = db.insert(
                        BakeAppContract.IngredientEntry.TABLE_NAME,
                        null,
                        values
                );
                //returns recipe/[recipeId]/ingredient/[id]
                returnUri = ContentUris.withAppendedId(BakeAppContract.IngredientEntry.buildDirUri(BakeAppContract.IngredientEntry.getRecipeId(uri)), insertedId);
                break;
            case STEP:
                insertedId = db.insert(
                        BakeAppContract.StepEntry.TABLE_NAME,
                        null,
                        values
                );
                //returns recipe/[recipeId]/step/[id]
                returnUri = ContentUris.withAppendedId(BakeAppContract.StepEntry.buildDirUri(BakeAppContract.StepEntry.getRecipeId(uri)), insertedId);
                break;
            default:
                throw new UnsupportedOperationException("Unknown URI:" + uri);
        }

        Context context = getContext();
        if (context != null) {
            context.getContentResolver().notifyChange(uri, null);
        }

        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase db = bakeAppDbHelper.getWritableDatabase();
        int rowsDeleted;

        switch (uriMatcher.match(uri)) {
            case RECIPE_BY_ID:
                rowsDeleted = db.delete(
                        BakeAppContract.RecipeEntry.TABLE_NAME,
                        '"' + BakeAppContract.RecipeEntry.getRecipeId(uri) + '"' + " = " + BakeAppContract.RecipeEntry._ID,
                        selectionArgs
                );
                break;

            case INGREDIENT:
                rowsDeleted = db.delete(
                        BakeAppContract.IngredientEntry.TABLE_NAME,
                        BakeAppContract.IngredientEntry.COLUMN_RECIPE_ID + " = ?",
                        new String[]{Long.toString(BakeAppContract.IngredientEntry.getRecipeId(uri))}
                );
                break;

            case STEP:
                rowsDeleted = db.delete(
                        BakeAppContract.StepEntry.TABLE_NAME,
                        BakeAppContract.StepEntry.COLUMN_RECIPE_ID + " = ?",
                        new String[]{Long.toString(BakeAppContract.StepEntry.getRecipeId(uri))}
                );
                break;
            default:
                throw new UnsupportedOperationException("Unknown URI:" + uri);
        }

        if (rowsDeleted != 0) {
            Context context = getContext();
            if (context != null) {
                context.getContentResolver().notifyChange(uri, null);
            }
        }

        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase db = bakeAppDbHelper.getWritableDatabase();
        int rowsUpdated = 0;

        switch (uriMatcher.match(uri)) {
            case INGREDIENT_SERVINGS:
                long recipeId = BakeAppContract.IngredientEntry.getRecipeId(uri);
                Double oldServings = (double) BakeAppContract.IngredientEntry.getOldServings(uri);
                Double newServings = (double) BakeAppContract.IngredientEntry.getNewServings(uri);
                Double servingFactor = newServings / oldServings;

                if (oldServings * newServings >= 1) {
                    db.beginTransaction();
                    try {
                        db.execSQL("UPDATE " + BakeAppContract.RecipeEntry.TABLE_NAME +
                                        " SET " + BakeAppContract.RecipeEntry.COLUMN_SERVINGS + " =  ?" +
                                        " WHERE " + BakeAppContract.RecipeEntry._ID + " = ?",
                                new String[]{Double.toString(newServings), Long.toString(recipeId)});

                        db.execSQL("UPDATE " + BakeAppContract.IngredientEntry.TABLE_NAME +
                                        " SET " + BakeAppContract.IngredientEntry.COLUMN_QUANTITY + " = " + BakeAppContract.IngredientEntry.COLUMN_QUANTITY + " * ?" +
                                        " WHERE " + BakeAppContract.IngredientEntry.COLUMN_RECIPE_ID + " = ?",
                                new String[]{Double.toString(servingFactor), Long.toString(recipeId)});
                        db.setTransactionSuccessful();

                        if (db.compileStatement("SELECT changes()").simpleQueryForLong() > 0)
                            rowsUpdated = 1;
                    } catch (SQLiteException e) {
                        //Error in between database transaction
                    } finally {
                        db.endTransaction();
                    }
                    db.close();
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown URI:" + uri);
        }

        if (rowsUpdated != 0) {
            Context context = getContext();
            if (context != null) {
                context.getContentResolver().notifyChange(uri, null);
            }
        }

        return rowsUpdated;
    }

    @NonNull
    public ContentProviderResult[] applyBatch(@NonNull ArrayList<ContentProviderOperation> operations)
            throws OperationApplicationException {
        final SQLiteDatabase db = bakeAppDbHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            final int numOperations = operations.size();
            final ContentProviderResult[] results = new ContentProviderResult[numOperations];
            for (int i = 0; i < numOperations; i++) {
                results[i] = operations.get(i).apply(this, results, i);
            }
            db.setTransactionSuccessful();
            return results;
        } finally {
            db.endTransaction();
        }
    }
}
