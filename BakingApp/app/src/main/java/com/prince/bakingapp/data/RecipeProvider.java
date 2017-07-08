package com.prince.bakingapp.data;

import android.content.ContentProvider;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;

import static com.prince.bakingapp.data.Contract.AUTHORITY;
import static com.prince.bakingapp.data.Contract.PATH_INGREDIENT;
import static com.prince.bakingapp.data.Contract.PATH_RECIPE;
import static com.prince.bakingapp.data.Contract.PATH_STEP;
import static com.prince.bakingapp.data.Contract.IngredientEntry;
import static com.prince.bakingapp.data.Contract.RecipeEntry;
import static com.prince.bakingapp.data.Contract.StepEntry;

public class RecipeProvider extends ContentProvider {
    private static final int RECIPE = 100;
    private static final int RECIPE_BY_ID = 101;
    private static final int INGREDIENT = 200;
    private static final int STEP = 300;
    private static final int STEP_BY_ID = 301;

    private static final UriMatcher uriMatcher = buildUriMatcher();

    private DbHelper dbHelper;

    private static UriMatcher buildUriMatcher() {
        UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        matcher.addURI(AUTHORITY, PATH_RECIPE, RECIPE);
        matcher.addURI(AUTHORITY, PATH_RECIPE + "/#", RECIPE_BY_ID);
        matcher.addURI(AUTHORITY, PATH_RECIPE + "/#/" + PATH_INGREDIENT, INGREDIENT);
        matcher.addURI(AUTHORITY, PATH_RECIPE + "/#/" + PATH_STEP, STEP);
        matcher.addURI(AUTHORITY, PATH_RECIPE + "/#/" + PATH_STEP + "/#", STEP_BY_ID);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        dbHelper = new DbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        Cursor returnCursor;
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        switch (uriMatcher.match(uri)) {
            case RECIPE:
                returnCursor = db.query(
                        RecipeEntry.TABLE_NAME,
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
                        RecipeEntry.TABLE_NAME,
                        projection,
                        RecipeEntry._ID + " = ?",
                        new String[]{Long.toString(RecipeEntry.getRecipeId(uri))},
                        null,
                        null,
                        sortOrder
                );
                break;
            case INGREDIENT:
                returnCursor = db.query(
                        IngredientEntry.TABLE_NAME,
                        projection,
                        IngredientEntry.COLUMN_RECIPE_ID + " = ?",
                        new String[]{Long.toString(IngredientEntry.getRecipeId(uri))},
                        null,
                        null,
                        sortOrder
                );
                break;
            case STEP:
                returnCursor = db.query(
                        StepEntry.TABLE_NAME,
                        projection,
                        StepEntry.COLUMN_RECIPE_ID + " = ?",
                        new String[]{Long.toString(StepEntry.getRecipeId(uri))},
                        null,
                        null,
                        sortOrder
                );
                break;
            case STEP_BY_ID:
                returnCursor = db.query(
                        StepEntry.TABLE_NAME,
                        projection,
                        StepEntry.COLUMN_RECIPE_ID + " = ? AND " + StepEntry._ID + " = ?",
                        new String[]{Long.toString(StepEntry.getRecipeId(uri)),
                                Long.toString(StepEntry.getStepId(uri))},
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
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Uri returnUri;

        switch (uriMatcher.match(uri)) {
            case RECIPE:
                db.insertWithOnConflict(
                        RecipeEntry.TABLE_NAME,
                        null,
                        values,
                        SQLiteDatabase.CONFLICT_REPLACE
                );
                returnUri = RecipeEntry.buildDirUri();
                break;
            case INGREDIENT:
                db.insert(
                        IngredientEntry.TABLE_NAME,
                        null,
                        values
                );
                returnUri = IngredientEntry.buildDirUri(IngredientEntry.getRecipeId(uri));
                break;
            case STEP:
                db.insert(
                        StepEntry.TABLE_NAME,
                        null,
                        values
                );
                returnUri = StepEntry.buildDirUri(StepEntry.getRecipeId(uri));
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
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rowsDeleted;

        switch (uriMatcher.match(uri)) {
            case RECIPE_BY_ID:
                rowsDeleted = db.delete(
                        RecipeEntry.TABLE_NAME,
                        '"' + RecipeEntry.getRecipeId(uri) + '"' + " = " + RecipeEntry._ID,
                        selectionArgs
                );
                break;

            case INGREDIENT:
                rowsDeleted = db.delete(
                        IngredientEntry.TABLE_NAME,
                        IngredientEntry.COLUMN_RECIPE_ID + " = ?",
                        new String[]{Long.toString(IngredientEntry.getRecipeId(uri))}
                );
                break;

            case STEP:
                rowsDeleted = db.delete(
                        StepEntry.TABLE_NAME,
                        StepEntry.COLUMN_RECIPE_ID + " = ?",
                        new String[]{Long.toString(StepEntry.getRecipeId(uri))}
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
        return 0;
    }

    @NonNull
    public ContentProviderResult[] applyBatch(@NonNull ArrayList<ContentProviderOperation> operations)
            throws OperationApplicationException {
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
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
