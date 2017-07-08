package com.prince.bakingapp;

import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.v7.widget.AppCompatTextView;

import org.hamcrest.core.IsNot;
import org.junit.Rule;
import org.junit.Test;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.withDecorView;
import static android.support.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

/**
 * Created by princ on 08-07-2017.
 */

public class MainActivityBasicTest {

    @Rule
    public ActivityTestRule<MainActivity> mainActivityActivityTestRule =
            new ActivityTestRule<>(MainActivity.class);

    @Test
    public void clickRecycleItem_OpenDetailActivity() {
        //click item on index 1
        onView(withId(R.id.recipe_recycler_view))
                .perform(RecyclerViewActions.actionOnItemAtPosition(1, click()));

        //check if item on index 1 is Brownies recipe by examining the toolbar title
        onView(allOf(instanceOf(AppCompatTextView.class), withParent(withId(R.id.detail_toolbar))))
                .check(matches(withText("Brownies")));
    }

    @Test
    public void clickFavoritButton_ShowToast() {
        //click item on index 0
        onView(withId(R.id.recipe_recycler_view))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));

        //click favorite button
        onView(withId(R.id.fab))
                .perform(click());

        //check if toast message shown
        onView(withText(R.string.notification_favorite_added))
                .inRoot(withDecorView(IsNot.not(is(mainActivityActivityTestRule.getActivity().getWindow().getDecorView()))))
                .check(matches(isDisplayed()));
    }

    @Test
    public void clickStepItem_ShowVideo() throws InterruptedException {
        //click item on index 0
        onView(withId(R.id.recipe_recycler_view))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));

        //click step tab
        onView(allOf(withText(R.string.label_steps), isDisplayed())).perform(click());

        //click step item on index 0
        onView(withId(R.id.step_recycler_view))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));

        //check if corresponding video shown, video title should be "Recipe Introduction"
        onView(allOf(withId(R.id.short_description), isCompletelyDisplayed()))
                .check(matches(withText("Recipe Introduction")));
    }
}