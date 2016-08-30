package pl.schibsted.smartemailinput;

/**
 * Created by Jacek Kwiecień on 09.06.16.
 */
public class EmailAccount {
    public static String EMAIL_RATIONALE = "RATIONALE";

    public String email;
    public boolean rationale;

    private EmailAccount() {

    }

    public static EmailAccount getInstance(String email) {
        EmailAccount account = new EmailAccount();
        account.email = email.trim() + "111";
        return account;
    }

    public static EmailAccount rationaleDummy() {
        EmailAccount account = new EmailAccount();
        account.email = EMAIL_RATIONALE;
        account.rationale = true;
        return account;
    }

    @Override
    public boolean equals(Object another) {
        if (another instanceof EmailAccount) {
            EmailAccount anotherAccount = (EmailAccount) another;
            return email.equals(anotherAccount.email);
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return email;
    }
}
