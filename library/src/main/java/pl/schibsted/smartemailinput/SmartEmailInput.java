package pl.schibsted.smartemailinput;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.util.AttributeSet;

import java.util.ArrayList;
import java.util.List;

import pl.schibsted.smartemailinput.sample.R;


/**
 * Created by Jacek Kwiecień on 09.06.16.
 */
public class SmartEmailInput extends AppCompatAutoCompleteTextView implements EmailInputMvp.View, EmailInputMvp.RationaleProvider {

    public interface ActivityProvider {
        Activity provideActivity();

        boolean shouldShowRequestPermissionRationale(String permission);
    }

    private EmailInputMvp.Presenter presenter;
    private ActivityProvider activityProvider;
    private String rationaleMessage;

    public SmartEmailInput(Context context) {
        super(context);
        init(context);
    }

    public SmartEmailInput(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SmartEmailInput(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void setActivityProvider(ActivityProvider provider) {
        this.activityProvider = provider;
    }

    private void init(Context context) {
        setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        rationaleMessage = getContext().getString(R.string.permission_rationale);

        presenter = new EmailInputPresenter(this, EmailInputMvp.RepositoryProvider.provideRepository(getContext()));
        if (Build.VERSION.SDK_INT < 26) {
            //Autofill from Android 8 works better
            boolean permissionRequired = ContextCompat.checkSelfPermission(context, Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED;

            List<EmailAccount> accounts = new ArrayList<>();
            boolean permissionDeniedPermanently = presenter.isPermissionDeniedPermanently();
            if (permissionRequired && !permissionDeniedPermanently) {
                accounts.add(EmailAccount.rationaleDummy());
                setAdapter(new EmailAdapter(getContext(), presenter, this, accounts, permissionRequired));
            } else if (!permissionDeniedPermanently) {
                presenter.loadAccounts();
            }
        }

        setFilters(new InputFilter[]{new SpaceTrimmerInputFilter()});
    }

    @Override
    public void onAccountsPermissionRequested() {
        ActivityCompat.requestPermissions(activityProvider.provideActivity(), new String[]{Manifest.permission.GET_ACCOUNTS}, RequestCodes.GET_ACCOUNTS_PERMISSION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case RequestCodes.GET_ACCOUNTS_PERMISSION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED && !activityProvider.shouldShowRequestPermissionRationale(Manifest.permission.GET_ACCOUNTS)) {
                    presenter.denyPermissionPermanently();
                } else if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    presenter.onAccountsPermissionGranted();
                } else {
                    presenter.onPermissionDenied();
                }
            }
            break;
        }
    }

    @Override
    public void onPermissionDenied() {

    }

    @Override
    public void onAccountsLoaded(List<EmailAccount> accounts) {
        setAdapter(new EmailAdapter(getContext(), presenter, this, accounts, false));
    }

    @Override
    public void onPermissionDeniedPermanently() {
        setAdapter(null);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        activityProvider = null;
    }

    @Override
    public String provideRationaleMessage() {
        return rationaleMessage;
    }

    public void setRationaleMessage(String rationaleMessage) {
        this.rationaleMessage = rationaleMessage;
    }

    private class SpaceTrimmerInputFilter implements InputFilter {

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            String text = source.toString();
            if (text.contains(" ")) {
                return "";
            }
            return null;
        }
    }
}
