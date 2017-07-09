package com.prince.bakingapp.utils;

import android.os.AsyncTask;

import com.firebase.jobdispatcher.JobService;

public class RecipeJobService extends JobService {
    AsyncTask<Object, Object, Object> backgroundTask;

    @Override
    public boolean onStartJob(final com.firebase.jobdispatcher.JobParameters job) {
        class RecipeAsyncTask extends AsyncTask<Object, Object, Object> {
            @Override
            protected Object doInBackground(Object[] params) {
                RecipeSyncTask.getRecipes(RecipeJobService.this);
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                jobFinished(job, false);
            }
        }

        backgroundTask = new RecipeAsyncTask();
        backgroundTask.execute();
        return true;
    }

    @Override
    public boolean onStopJob(com.firebase.jobdispatcher.JobParameters job) {
        if (backgroundTask != null) backgroundTask.cancel(true);
        return true;
    }
}
