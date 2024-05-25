package com.example.myapplication;

import androidx.test.espresso.Espresso;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    @Rule
    public ActivityScenarioRule<MainActivity> activityRule = new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void testNavigationAndDataDisplay() {
        // Assume "user1" data is already set up in Firebase for this test or mock the Firebase response

        // Test clicks on each card and observe navigation or changes
        Espresso.onView(withId(R.id.card1)).perform(click());
        Espresso.onView(withId(R.id.card2)).perform(click());
        Espresso.onView(withId(R.id.card3)).perform(click());
        Espresso.onView(withId(R.id.card4)).perform(click());
        Espresso.onView(withId(R.id.card5)).perform(click());
        Espresso.onView(withId(R.id.card6)).perform(click());

        // Check if the TextViews display the expected result after fetching data
        Espresso.onView(withId(R.id.name)).check(matches(isDisplayed()));
        Espresso.onView(withId(R.id.name2)).check(matches(isDisplayed()));
        Espresso.onView(withId(R.id.coins)).check(matches(isDisplayed()));
    }
}
