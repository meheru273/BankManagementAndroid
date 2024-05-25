package com.example.myapplication;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class EmployeeRegisterUITest {

    @Rule
    public ActivityTestRule<EmployeeRegisterActivity> mActivityRule = new ActivityTestRule<>(EmployeeRegisterActivity.class);

    @Test
    public void testEnterName() {
        onView(withId(R.id.nameField)).perform(replaceText("Asif Mahmud"), closeSoftKeyboard());
        onView(withId(R.id.nameField)).check(matches(withText("Asif Mahmud")));
    }

    @Test
    public void testEnterEmail() {
        onView(withId(R.id.emailField)).perform(replaceText("asif@gmail.com"), closeSoftKeyboard());
        onView(withId(R.id.emailField)).check(matches(withText("asif@gmail.com")));
    }

    @Test
    public void testEnterPassword() {
        onView(withId(R.id.passwordField)).perform(replaceText("123456"), closeSoftKeyboard());
        onView(withId(R.id.passwordField)).check(matches(withText("123456")));
    }

    @Test
    public void testClickRegisterButton() {
        onView(withId(R.id.registerButton)).perform(click());
    }
}
