package pl.schibsted.smartemailinput;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.util.Patterns;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by Jacek Kwiecień on 09.06.16.
 */
public class EmailInputRepository implements EmailInputMvp.Repository {

    private Context context;

    public EmailInputRepository(Context context) {
        this.context = context;
    }

    @Override
    public List<EmailAccount> getAccounts() {
        final List<EmailAccount> emailAccounts = new ArrayList<>();
        Pattern emailPattern = Patterns.EMAIL_ADDRESS;
        Account[] accounts = AccountManager.get(context).getAccounts();
        for (Account account : accounts) {
            if (emailPattern.matcher(account.name).matches()) {
                String email = account.name;
                EmailAccount emailAccount = EmailAccount.getInstance(email);
                if (!emailAccounts.contains(emailAccount)) {
                    emailAccounts.add(emailAccount);
                }
            }
        }
        return emailAccounts;
    }
}
