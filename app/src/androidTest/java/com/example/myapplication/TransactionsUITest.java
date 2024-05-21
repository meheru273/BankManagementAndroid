package com.example.myapplication;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class TransactionsUITest {

    @Rule
    public ActivityTestRule<Transactions> activityRule = new ActivityTestRule<>(Transactions.class);

    private ActivityScenario<Transactions> scenario;

    @Before
    public void setUp() {
        scenario = ActivityScenario.launch(Transactions.class);
    }

    @After
    public void tearDown() {
        scenario.close();
    }

    @Test
    public void testLayoutElementsExist() {
        scenario.onActivity(activity -> {
            ConstraintLayout mainLayout = activity.findViewById(R.id.main);
            ImageView imageView = activity.findViewById(R.id.img1);
            TextView textView = activity.findViewById(R.id.textView);
            RecyclerView recyclerView = activity.findViewById(R.id.tran);
            Button button = activity.findViewById(R.id.button);

            assertNotNull(mainLayout);
            assertNotNull(imageView);
            assertNotNull(textView);
            assertNotNull(recyclerView);
            assertNotNull(button);
        });
    }

    @Test
    public void testTextViewContent() {
        scenario.onActivity(activity -> {
            TextView textView = activity.findViewById(R.id.textView);
            assertEquals("Transaction", textView.getText().toString());
        });
    }

    @Test
    public void testButtonContent() {
        scenario.onActivity(activity -> {
            Button button = activity.findViewById(R.id.button);
            assertEquals("Transaction", button.getText().toString());
        });
    }
}
