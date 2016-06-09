package pl.schibsted.smartemailinput;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import pl.schibsted.smartemailinput.sample.R;

/**
 * Created by Jacek Kwiecień on 09.06.16.
 */
public class EmailAdapter extends ArrayAdapter<EmailAccount> {

    private final int TYPE_ACCOUNT = 0;
    private final int TYPE_RATIONALE = 1;

    private EmailInputMvp.Presenter presenter;
    private EmailInputMvp.RationaleProvider rationaleProvider;
    private List<EmailAccount> allAccounts;
    private List<EmailAccount> originalAccounts;
    private AccountFilter filter;
    private boolean permissionRequired = false;

    public EmailAdapter(Context context, EmailInputMvp.Presenter presenter, EmailInputMvp.RationaleProvider rationaleProvider, List<EmailAccount> accounts, boolean permissionRequired) {
        super(context, android.R.layout.simple_dropdown_item_1line, accounts);
        this.presenter = presenter;
        this.rationaleProvider = rationaleProvider;
        this.allAccounts = accounts;
        this.originalAccounts = new ArrayList<>(accounts);
        this.permissionRequired = permissionRequired;
    }

    @Override
    public int getCount() {
        return permissionRequired ? 1 : allAccounts.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (permissionRequired) {
            return TYPE_RATIONALE;
        } else {
            return TYPE_ACCOUNT;
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder;

        if (row == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            int layout = permissionRequired ? R.layout.view_permission_rationale : android.R.layout.simple_dropdown_item_1line;
            row = inflater.inflate(layout, parent, false);
            holder = permissionRequired ? new RationaleViewHolder(row) : new AccountViewHolder(row);
            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        EmailAccount account = getItem(position);
        holder.bind(account);

        return row;
    }

    @Override
    public EmailAccount getItem(int position) {
        return allAccounts.get(position);
    }

    @Override
    public Filter getFilter() {
        if (filter == null) filter = new AccountFilter();
        return filter;
    }

    private static abstract class ViewHolder {

        public abstract void bind(EmailAccount account);
    }

    private static class AccountViewHolder extends ViewHolder {
        public TextView label;

        public AccountViewHolder(View view) {
            label = (TextView) view.findViewById(android.R.id.text1);
        }

        @Override
        public void bind(EmailAccount account) {
            label.setText(account.email);
        }
    }

    private class RationaleViewHolder extends ViewHolder {

        public View view;
        public TextView label;

        public RationaleViewHolder(View view) {
            this.view = view;
            this.label = (TextView) view.findViewById(R.id.label);

            this.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    presenter.requestAccountsPermission();
                }
            });
        }

        @Override
        public void bind(EmailAccount account) {
            label.setText(rationaleProvider.provideRationaleMessage());
        }
    }


    private class AccountFilter extends Filter {
        private Object lock = "";

        @Override
        protected FilterResults performFiltering(CharSequence prefix) {
            FilterResults results = new FilterResults();

            if (originalAccounts == null) {
                synchronized (lock) {
                    originalAccounts = new ArrayList<>(allAccounts);
                }
            }

            if (prefix == null || prefix.length() == 0) {
                synchronized (lock) {
                    ArrayList<EmailAccount> list = new ArrayList<>(originalAccounts);
                    results.values = list;
                    results.count = list.size();
                }
            } else {
                final String prefixString = prefix.toString().toLowerCase();

                List<EmailAccount> values = originalAccounts;
                int count = values.size();

                ArrayList<EmailAccount> newAccounts = new ArrayList<>(count);

                for (int i = 0; i < count; i++) {
                    EmailAccount account = values.get(i);
                    if (permissionRequired) {
                        newAccounts.add(account);
                    } else if (account.email.toLowerCase().contains(prefixString)) {
                        newAccounts.add(account);
                    }
                }

                results.values = newAccounts;
                results.count = newAccounts.size();
            }
            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            if (results.values != null) {
                allAccounts = (ArrayList<EmailAccount>) results.values;
            } else {
                allAccounts = new ArrayList<>();
            }
            if (results.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }
    }

}
