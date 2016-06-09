package pl.schibsted.smartemailinput;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.text.InputType;
import android.util.AttributeSet;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jacek Kwiecień on 09.06.16.
 */
public class SmartEmailInput extends AppCompatAutoCompleteTextView implements EmailInputMvp.View {

    public interface ActivityProvider {
        Activity provideActivity();
    }

    private EmailInputMvp.Presenter presenter;
    private ActivityProvider activityProvider;
    private String currentText;

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

        presenter = new EmailInputPresenter(this, EmailInputMvp.RepositoryProvider.provideRepository(getContext()));

        boolean permissionRequired = ContextCompat.checkSelfPermission(context, Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED;
        List<EmailAccount> accounts = new ArrayList<>();
        if (permissionRequired) {
            accounts.add(EmailAccount.rationaleDummy());
            setAdapter(new EmailAdapter(getContext(), presenter, accounts, permissionRequired));
        } else {
            presenter.loadAccounts();
        }
    }

    @Override
    public void onAccountsPermissionRequested() {
        ActivityCompat.requestPermissions(activityProvider.provideActivity(), new String[]{Manifest.permission.GET_ACCOUNTS}, RequestCodes.GET_ACCOUNTS_PERMISSION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case RequestCodes.GET_ACCOUNTS_PERMISSION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
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
        setAdapter(new EmailAdapter(getContext(), presenter, accounts, false));
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        activityProvider = null;
    }
}
