package pl.schibsted.smartemailinput;

import android.content.Context;
import android.support.annotation.NonNull;

import java.util.List;

/**
 * Created by Jacek Kwiecień on 09.06.16.
 */
public interface EmailInputMvp {

    class RepositoryProvider {
        private RepositoryProvider() {
            // no instance
        }

        private static Repository repository = null;

        public synchronized static Repository provideRepository(@NonNull Context context) {
            if (null == repository) {
                repository = new EmailInputRepository(context);
            }
            return repository;
        }
    }

    interface Presenter {
        void onAccountsPermissionGranted();

        void onPermissionDenied();

        void requestAccountsPermission();

        void loadAccounts();
    }

    interface View {
        void onAccountsPermissionRequested();

        void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults);

        void onPermissionDenied();

        void onAccountsLoaded(List<EmailAccount> accounts);
    }

    interface Repository {
        List<EmailAccount> getAccounts();
    }

    interface RationaleProvider {
        String provideRationaleMessage();
    }
}
