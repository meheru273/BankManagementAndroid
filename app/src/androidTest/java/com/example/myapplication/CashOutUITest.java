package com.example.myapplication;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import android.widget.Button;
import android.widget.EditText;
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
public class CashOutUITest {

    @Rule
    public ActivityTestRule<CashOut> activityRule = new ActivityTestRule<>(CashOut.class);

    private ActivityScenario<CashOut> scenario;

    @Before
    public void setUp() {
        scenario = ActivityScenario.launch(CashOut.class);
    }

    @After
    public void tearDown() {
        scenario.close();
    }

    @Test
    public void testLayoutElementsExist() {
        scenario.onActivity(activity -> {
            ConstraintLayout mainLayout = activity.findViewById(R.id.main);
            ImageView imageView = activity.findViewById(R.id.cashout);
            TextView textView = activity.findViewById(R.id.textView2);
            EditText editText = activity.findViewById(R.id.cashamount);
            Button button = activity.findViewById(R.id.button);

            assertNotNull(mainLayout);
            assertNotNull(imageView);
            assertNotNull(textView);
            assertNotNull(editText);
            assertNotNull(button);
        });
    }

    @Test
    public void testTextViewContent() {
        scenario.onActivity(activity -> {
            TextView textView = activity.findViewById(R.id.textView2);
            assertEquals("Cash Out", textView.getText().toString());
        });
    }

    @Test
    public void testEditTextContent() {
        scenario.onActivity(activity -> {
            EditText editText = activity.findViewById(R.id.cashamount);
            assertEquals("Enter Amount", editText.getText().toString());
        });
    }

    @Test
    public void testButtonContent() {
        scenario.onActivity(activity -> {
            Button button = activity.findViewById(R.id.button);
            assertEquals("Cash Out", button.getText().toString());
        });
    }
}
