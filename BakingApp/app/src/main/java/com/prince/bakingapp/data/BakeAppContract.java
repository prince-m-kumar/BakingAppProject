package com.prince.bakingapp.data;

import android.net.Uri;
import android.provider.BaseColumns;

import com.google.common.collect.ImmutableList;
/**
 * Created by princ on 08-07-2017.
 */
public class BakeAppContract {
    public static final String AUTHORITY = "com.prince.bakingapp";
    static final String PATH_RECIPE = "recipe";
    static final String PATH_INGREDIENT = "ingredient";
    static final String PATH_STEP = "step";
    static final String PATH_SERVINGS = "servings";


    private static final Uri BASE_URI = Uri.parse("content://" + AUTHORITY);

    public static final class RecipeEntry implements BaseColumns {
        static final String TABLE_NAME = "recipes";

        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_SERVINGS = "servings";
        public static final String COLUMN_IMAGE = "image";

        public static final int POSITION_ID = 0;
        public static final int POSITION_NAME = 1;
        public static final int POSITION_SERVINGS = 2;
        public static final int POSITION_IMAGE = 3;

        public static final ImmutableList<String> RECIPE_COLUMNS = ImmutableList.of(
                _ID,
                COLUMN_NAME,
                COLUMN_SERVINGS,
                COLUMN_IMAGE
        );

        /**
         * Matches: /recipe/
         */
        public static Uri buildDirUri() {
            return BASE_URI
                    .buildUpon()
                    .appendPath(PATH_RECIPE)
                    .build();
        }

        /**
         * Matches: /recipe/[_id]/
         */
        public static Uri buildItemUri(long recipeId) {
            return BASE_URI
                    .buildUpon()
                    .appendPath(PATH_RECIPE)
                    .appendPath(Long.toString(recipeId))
                    .build();
        }

        /**
         * Read recipe ID item detail URI.
         */
        public static long getRecipeId(Uri uri) {
            return Long.parseLong(uri.getLastPathSegment());
        }
    }

    public static final class IngredientEntry implements BaseColumns {
        static final String TABLE_NAME = "ingredients";

        public static final String COLUMN_QUANTITY = "quantity";
        public static final String COLUMN_MEASURE = "measure";
        public static final String COLUMN_INGREDIENT = "ingredient";
        public static final String COLUMN_RECIPE_ID = "recipe_id";

        public static final int POSITION_ID = 0;
        public static final int POSITION_QUANTITY = 1;
        public static final int POSITION_MEASURE = 2;
        public static final int POSITION_INGREDIENT = 3;
        public static final int POSITION_RECIPE_ID = 4;

        public static final ImmutableList<String> INGREDIENT_COLUMNS = ImmutableList.of(
                _ID,
                COLUMN_QUANTITY,
                COLUMN_MEASURE,
                COLUMN_INGREDIENT,
                COLUMN_RECIPE_ID
        );

        /**
         * Matches: /recipe/[_id]/ingredient
         */
        public static Uri buildDirUri(long recipeId) {
            return BASE_URI
                    .buildUpon()
                    .appendPath(PATH_RECIPE)
                    .appendPath(Long.toString(recipeId))
                    .appendPath(PATH_INGREDIENT)
                    .build();
        }

        /**
         * Matches: /recipe/[_id]/ingredient/servings/#
         */
        public static Uri buildServingsUri(long recipeId, int oldServings, int newServings) {
            return BASE_URI
                    .buildUpon()
                    .appendPath(PATH_RECIPE)
                    .appendPath(Long.toString(recipeId))
                    .appendPath(PATH_INGREDIENT)
                    .appendPath(PATH_SERVINGS)
                    .appendPath(Integer.toString(oldServings))
                    .appendPath(Integer.toString(newServings))
                    .build();
        }

        /**
         * Read recipe ID from URI.
         */
        static long getRecipeId(Uri uri) {
            return Long.parseLong(uri.getPathSegments().get(1));
        }

        /**
         * Read servings from URI.
         */
        static long getOldServings(Uri uri) {
            return Long.parseLong(uri.getPathSegments().get(4));
        }

        static long getNewServings(Uri uri) {
            return Long.parseLong(uri.getPathSegments().get(5));
        }
    }

    public static final class StepEntry implements BaseColumns {
        static final String TABLE_NAME = "steps";

        public static final String COLUMN_SHORT_DESCRIPTION = "shortDescription";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_VIDEO_URL = "videoURL";
        public static final String COLUMN_THUMBNAIL_URL = "thumbnailURL";
        public static final String COLUMN_RECIPE_ID = "recipe_id";

        public static final int POSITION_ID = 0;
        public static final int POSITION_SHORT_DESCRIPTION = 1;
        public static final int POSITION_DESCRIPTION = 2;
        public static final int POSITION_VIDEO_URL = 3;
        public static final int POSITION_THUMBNAIL_URL = 4;
        public static final int POSITION_RECIPE_ID = 5;

        public static final ImmutableList<String> STEP_COLUMNS = ImmutableList.of(
                _ID,
                COLUMN_SHORT_DESCRIPTION,
                COLUMN_DESCRIPTION,
                COLUMN_VIDEO_URL,
                COLUMN_THUMBNAIL_URL,
                COLUMN_RECIPE_ID
        );

        /**
         * Matches: /recipe/[_id]/step
         */
        public static Uri buildDirUri(long recipeId) {
            return BASE_URI
                    .buildUpon()
                    .appendPath(PATH_RECIPE)
                    .appendPath(Long.toString(recipeId))
                    .appendPath(PATH_STEP)
                    .build();
        }

        /**
         * Matches: /step/[_id]
         */
        public static Uri buildItemUri(long recipeId, long stepId) {
            return BASE_URI
                    .buildUpon()
                    .appendPath(PATH_RECIPE)
                    .appendPath(Long.toString(recipeId))
                    .appendPath(PATH_STEP)
                    .appendPath(Long.toString(stepId))
                    .build();
        }

        /**
         * Read recipe ID item detail URI.
         */
        public static long getRecipeId(Uri uri) {
            return Long.parseLong(uri.getPathSegments().get(1));
        }

        /**
         * Read step ID item detail URI.
         */
        public static long getStepId(Uri uri) {
            return Long.parseLong(uri.getLastPathSegment());
        }
    }
}
