package com.example.myapplication;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class LoginUITest {

    @Rule
    public ActivityTestRule<LoginActivity> mActivityRule = new ActivityTestRule<>(LoginActivity.class);

    @Test
    public void testLoginUIElementsDisplayed() {
        onView(withId(R.id.emailField)).check(matches(isDisplayed()));

        onView(withId(R.id.passwordField)).check(matches(isDisplayed()));

        onView(withId(R.id.loginButton)).check(matches(isDisplayed()));

        onView(withId(R.id.signUpButton)).check(matches(isDisplayed()));
    }

    @Test
    public void testUserCanEnterEmailAndPassword() {
        onView(withId(R.id.emailField)).perform(replaceText("cse@gmail.com"), closeSoftKeyboard());
        onView(withId(R.id.passwordField)).perform(replaceText("cdicse123"), closeSoftKeyboard());

        onView(withId(R.id.emailField)).check(matches(withText("cse@gmail.com")));
        onView(withId(R.id.passwordField)).check(matches(withText("cdicse123")));
    }


    @Test
    public void testLoginButtonInvalidCredentials() {
        // will show login failed if the credentials are wrong
        onView(withId(R.id.emailField)).perform(replaceText("ami@gmail.com"), closeSoftKeyboard());
        onView(withId(R.id.passwordField)).perform(replaceText("wrongpass"), closeSoftKeyboard());

        onView(withId(R.id.loginButton)).perform(click());
        onView(withText(org.hamcrest.Matchers.containsString("Login failed"))).inRoot(new ToastMatcher()).check(matches(isDisplayed()));
    }
}
