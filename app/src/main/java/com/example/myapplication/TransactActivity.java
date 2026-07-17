package com.example.myapplication;

import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/** Amount entry (custom keypad) -> PIN confirm -> success, wired to the Firebase facade. */
public class TransactActivity extends AppCompatActivity {

    public static final String EXTRA_FLOW = "flow";

    private String flow;
    private final StringBuilder amount = new StringBuilder();
    private final StringBuilder pin = new StringBuilder();
    private boolean processing = false;

    private FinancialServicesFacade facade;
    private SessionManager session;

    private ViewFlipper flipper;
    private TextView amountBig, pinSub, successMsg, successAmount, receiptFrom, receiptDate;
    private EditText recipientInput;
    private boolean showRecipient;
    private View[] dots;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transact);

        flow = getIntent().getStringExtra(EXTRA_FLOW);
        if (flow == null) flow = "cashin";
        facade = new FinancialServicesFacade();
        session = new SessionManager(this);

        flipper = findViewById(R.id.flipper);
        amountBig = findViewById(R.id.t_amount_big);
        pinSub = findViewById(R.id.t_pin_sub);
        successMsg = findViewById(R.id.t_success_msg);
        successAmount = findViewById(R.id.t_success_amount);
        receiptFrom = findViewById(R.id.t_receipt_from);
        receiptDate = findViewById(R.id.t_receipt_date);
        recipientInput = findViewById(R.id.t_recipient_input);
        dots = new View[]{findViewById(R.id.t_dot0), findViewById(R.id.t_dot1),
                findViewById(R.id.t_dot2), findViewById(R.id.t_dot3)};

        configureFlow();
        buildPad(findViewById(R.id.t_amount_pad), new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9", ".", "0", "back"}, false);
        buildPad(findViewById(R.id.t_pin_pad), new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9", "", "0", "back"}, true);
        setupChips();

        findViewById(R.id.t_back).setOnClickListener(v -> finish());
        findViewById(R.id.t_pin_back).setOnClickListener(v -> {
            pin.setLength(0);
            renderDots();
            flipper.setDisplayedChild(0);
        });
        findViewById(R.id.t_continue).setOnClickListener(v -> onContinue());
        findViewById(R.id.t_done).setOnClickListener(v -> finish());

        renderAmount();
        renderDots();
    }

    private void configureFlow() {
        String title;
        showRecipient = flow.equals("transfer") || flow.equals("bill");
        switch (flow) {
            case "cashout": title = "Cash Out"; break;
            case "transfer": title = "Send Money"; break;
            case "bill": title = "Pay Bill"; break;
            default: title = "Cash In"; break;
        }
        ((TextView) findViewById(R.id.t_title)).setText(title);
        ((TextView) findViewById(R.id.t_from)).setText("My Wallet");

        View card = findViewById(R.id.t_recipient_card);
        card.setVisibility(showRecipient ? View.VISIBLE : View.GONE);
        if (showRecipient) {
            boolean bill = flow.equals("bill");
            ((TextView) findViewById(R.id.t_recipient_label)).setText(bill ? "Biller" : "To");
            ((ImageView) findViewById(R.id.t_recipient_icon))
                    .setImageResource(bill ? R.drawable.ic_bolt : R.drawable.ic_user);
            recipientInput.setHint(bill ? R.string.biller_hint : R.string.recipient_hint);
        }
    }

    private void setupChips() {
        int[] ids = {R.id.t_chip1, R.id.t_chip2, R.id.t_chip3, R.id.t_chip4};
        int[] vals = {500, 1000, 5000, 10000};
        for (int i = 0; i < ids.length; i++) {
            TextView chip = findViewById(ids[i]);
            final int value = vals[i];
            chip.setText(MoneyUtils.CURRENCY + " " + MoneyUtils.group(String.valueOf(value)));
            chip.setOnClickListener(v -> {
                amount.setLength(0);
                amount.append(value);
                renderAmount();
            });
        }
    }

    private void buildPad(GridLayout grid, String[] keys, boolean isPin) {
        grid.removeAllViews();
        for (String k : keys) {
            TextView key = new TextView(this);
            GridLayout.LayoutParams lp = new GridLayout.LayoutParams();
            lp.width = 0;
            lp.height = Ui.dp(this, isPin ? 60 : 56);
            lp.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1, 1f);
            key.setLayoutParams(lp);
            key.setGravity(Gravity.CENTER);
            key.setTextColor(ContextCompat.getColor(this, R.color.ink));
            key.setTextSize(24);
            key.setTypeface(Typeface.DEFAULT_BOLD);
            if (k.isEmpty()) {
                grid.addView(key);
                continue;
            }
            key.setText(k.equals("back") ? "⌫" : k);
            key.setBackgroundResource(R.drawable.bg_key);
            final String kk = k;
            key.setOnClickListener(v -> {
                if (isPin) onPinKey(kk);
                else onAmountKey(kk);
            });
            grid.addView(key);
        }
    }

    private void onAmountKey(String k) {
        String a = amount.toString();
        if (k.equals("back")) {
            if (a.length() > 0) amount.deleteCharAt(amount.length() - 1);
        } else if (k.equals(".")) {
            if (!a.contains(".")) {
                if (a.isEmpty()) amount.append("0");
                amount.append(".");
            }
        } else {
            if (a.contains(".") && a.substring(a.indexOf('.') + 1).length() >= 2) return;
            if (a.replace(".", "").length() >= 9) return;
            amount.append(k);
        }
        renderAmount();
    }

    private void onPinKey(String k) {
        if (processing) return;
        if (k.equals("back")) {
            if (pin.length() > 0) pin.deleteCharAt(pin.length() - 1);
        } else if (pin.length() < 4) {
            pin.append(k);
        }
        renderDots();
        if (pin.length() == 4) verifyPin();
    }

    private void renderAmount() {
        amountBig.setText(MoneyUtils.CURRENCY + " " + MoneyUtils.group(amount.toString()));
    }

    private void renderDots() {
        int filled = ContextCompat.getColor(this, R.color.plum);
        int empty = 0xFFE0D3DA;
        for (int i = 0; i < dots.length; i++) {
            dots[i].setBackgroundTintList(ColorStateList.valueOf(i < pin.length() ? filled : empty));
        }
    }

    private double parseAmount() {
        try {
            return Double.parseDouble(amount.toString());
        } catch (NumberFormatException e) {
            return 0d;
        }
    }

    private void onContinue() {
        if (parseAmount() <= 0) {
            Toast.makeText(this, R.string.err_enter_amount, Toast.LENGTH_SHORT).show();
            return;
        }
        if (showRecipient && recipientInput.getText().toString().trim().isEmpty()) {
            recipientInput.setError(getString(
                    flow.equals("bill") ? R.string.err_enter_biller : R.string.err_enter_recipient));
            recipientInput.requestFocus();
            return;
        }
        pin.setLength(0);
        renderDots();
        pinSub.setText("to confirm " + title() + " of " + MoneyUtils.format(parseAmount()));
        flipper.setDisplayedChild(1);
    }

    private void verifyPin() {
        if (!session.checkPin(pin.toString())) {
            Toast.makeText(this, R.string.wrong_pin, Toast.LENGTH_SHORT).show();
            pin.setLength(0);
            renderDots();
            return;
        }
        processing = true;
        final double amt = parseAmount();
        final String recipient = recipientInput.getText().toString().trim();
        FinancialServicesFacade.OpCallback cb = (ok, message) -> {
            processing = false;
            if (ok) {
                showSuccess(amt);
            } else {
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();
                pin.setLength(0);
                renderDots();
                flipper.setDisplayedChild(0);
            }
        };
        switch (flow) {
            case "cashout": facade.cashOut(amt, cb); break;
            case "transfer": facade.sendMoney(recipient, amt, cb); break;
            case "bill": facade.payBill(recipient, amt, cb); break;
            default: facade.cashIn(amt, cb); break;
        }
    }

    private void showSuccess(double amt) {
        successMsg.setText(title() + " completed successfully");
        successAmount.setText(MoneyUtils.format(amt));
        receiptFrom.setText("My Wallet");
        receiptDate.setText(new SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.US).format(new Date()));
        flipper.setDisplayedChild(2);
    }

    private String title() {
        switch (flow) {
            case "cashout": return "Cash Out";
            case "transfer": return "Send Money";
            case "bill": return "Pay Bill";
            default: return "Cash In";
        }
    }
}
