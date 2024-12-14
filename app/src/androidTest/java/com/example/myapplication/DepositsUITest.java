package com.example.myapplication;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class DepositsUITest {

    @Rule
    public ActivityTestRule<Deposits> activityRule = new ActivityTestRule<>(Deposits.class);

    private ActivityScenario<Deposits> scenario;

    @Before
    public void setUp() {
        scenario = ActivityScenario.launch(Deposits.class);
    }

    @After
    public void tearDown() {
        scenario.close();
    }

    @Test
    public void testLayoutElementsExist() {
        scenario.onActivity(activity -> {
            ConstraintLayout mainLayout = activity.findViewById(R.id.main);
            ImageView imageView = activity.findViewById(R.id.ImageView);
            TextView textViewSavings = activity.findViewById(R.id.textView4);
            TextView textViewBalance = activity.findViewById(R.id.textViewBalance);
            TextView textViewInterestRate = activity.findViewById(R.id.textViewInterestRate);
            Button button = activity.findViewById(R.id.button);

            assertNotNull("Main layout is null", mainLayout);
            assertNotNull("ImageView is null", imageView);
            assertNotNull("Savings TextView is null", textViewSavings);
            assertNotNull("Balance TextView is null", textViewBalance);
            assertNotNull("Interest Rate TextView is null", textViewInterestRate);
            assertNotNull("Button is null", button);
        });
    }

    @Test
    public void testTextViewSavingsContent() {
        scenario.onActivity(activity -> {
            TextView textViewSavings = activity.findViewById(R.id.textView4);
            assertEquals("Savings", textViewSavings.getText().toString());
        });
    }

    @Test
    public void testTextViewBalanceContent() {
        scenario.onActivity(activity -> {
            TextView textViewBalance = activity.findViewById(R.id.textViewBalance);
            assertNotNull("TextView for balance is null, check initialization", textViewBalance);
            assertEquals("Expected text for balance does not match", "Balance:", textViewBalance.getText().toString());
        });
    }

    @Test
    public void testTextViewInterestRateContent() {
        scenario.onActivity(activity -> {
            TextView textViewInterestRate = activity.findViewById(R.id.textViewInterestRate);
            assertNotNull("TextView for interest rate is null, check initialization", textViewInterestRate);
            assertEquals("Expected text for interest rate does not match", "Interest Rate:", textViewInterestRate.getText().toString());
        });
    }


    @Test
    public void testButtonText() {
        scenario.onActivity(activity -> {
            Button button = activity.findViewById(R.id.button);
            assertEquals("Savings", button.getText().toString());
        });
    }
}
