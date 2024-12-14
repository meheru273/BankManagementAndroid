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
public class SendMoneyUITest {

    @Rule
    public ActivityTestRule<SendMoney> activityRule = new ActivityTestRule<>(SendMoney.class);

    private ActivityScenario<SendMoney> scenario;

    @Before
    public void setUp() {
        scenario = ActivityScenario.launch(SendMoney.class);
    }

    @After
    public void tearDown() {
        scenario.close();
    }

    @Test
    public void testLayoutElementsExist() {
        scenario.onActivity(activity -> {
            ConstraintLayout mainLayout = activity.findViewById(R.id.main);
            ImageView imageView = activity.findViewById(R.id.imageView);
            TextView sendMoneyLabel = activity.findViewById(R.id.sendMoneyLabel);
            EditText sendAmount = activity.findViewById(R.id.sendAmount);
            EditText receiverAccountNumber = activity.findViewById(R.id.receiverAccountNumber);
            Button sendButton = activity.findViewById(R.id.sendButton);

            assertNotNull(mainLayout);
            assertNotNull(imageView);
            assertNotNull(sendMoneyLabel);
            assertNotNull(sendAmount);
            assertNotNull(receiverAccountNumber);
            assertNotNull(sendButton);
        });
    }

    @Test
    public void testSendMoneyLabelContent() {
        scenario.onActivity(activity -> {
            TextView sendMoneyLabel = activity.findViewById(R.id.sendMoneyLabel);
            assertEquals("Send Money", sendMoneyLabel.getText().toString());
        });
    }

    @Test
    public void testSendAmountHint() {
        scenario.onActivity(activity -> {
            EditText sendAmount = activity.findViewById(R.id.sendAmount);
            assertEquals("Amount", sendAmount.getHint().toString());
        });
    }

    @Test
    public void testReceiverAccountNumberHint() {
        scenario.onActivity(activity -> {
            EditText receiverAccountNumber = activity.findViewById(R.id.receiverAccountNumber);
            assertEquals("Sender Email", receiverAccountNumber.getHint().toString());
        });
    }
}
