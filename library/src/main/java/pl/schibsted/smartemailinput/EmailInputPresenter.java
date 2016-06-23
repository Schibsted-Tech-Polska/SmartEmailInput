package pl.schibsted.smartemailinput;

import java.util.List;

/**
 * Created by Jacek Kwiecień on 09.06.16.
 */
public class EmailInputPresenter implements EmailInputMvp.Presenter {

    private EmailInputMvp.View view;
    private EmailInputMvp.Repository repository;

    public EmailInputPresenter(EmailInputMvp.View view, EmailInputMvp.Repository repository) {
        this.view = view;
        this.repository = repository;
    }

    @Override
    public void onAccountsPermissionGranted() {
        List<EmailAccount> accounts = repository.getAccounts();
        view.onAccountsLoaded(accounts);
    }

    @Override
    public void onPermissionDenied() {
        view.onPermissionDenied();
    }

    @Override
    public void requestAccountsPermission() {
        view.onAccountsPermissionRequested();
    }

    @Override
    public void loadAccounts() {
        List<EmailAccount> accounts = repository.getAccounts();
        view.onAccountsLoaded(accounts);
    }

    @Override
    public void denyPermissionPermanently() {
        repository.denyPermissionPermanently();
        view.onPermissionDeniedPermanently();
    }

    @Override
    public boolean isPermissionDeniedPermanently() {
        return repository.isPermissionDeniedPermanently();
    }
}
