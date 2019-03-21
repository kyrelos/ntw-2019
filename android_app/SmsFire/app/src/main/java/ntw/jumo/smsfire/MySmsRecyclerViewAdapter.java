package ntw.jumo.smsfire;

import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ntw.jumo.smsfire.SmsFragment.OnListFragmentInteractionListener;
import ntw.jumo.smsfire.SmsFireContent.SmsFireMessage;
import ntw.jumo.smsfire.db.Sms;
import ntw.jumo.smsfire.db.SmsRepository;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link SmsFireMessage} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MySmsRecyclerViewAdapter extends RecyclerView.Adapter<MySmsRecyclerViewAdapter.ViewHolder> {

    private  List<SmsFireMessage> mValues = new ArrayList<SmsFireMessage>();;
    private final OnListFragmentInteractionListener mListener;

    public MySmsRecyclerViewAdapter(OnListFragmentInteractionListener listener) {
        mListener = listener;
    }

    public void updateMessages(List<SmsFireMessage> list){
        mValues = list;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_sms, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mAddressView.setText(mValues.get(position).address);
        holder.mMessageView.setText(mValues.get(position).message);

        Date date = new Date(Long.parseLong(mValues.get(position).date));
        holder.mDateView.setText(DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(date));

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mAddressView;
        public final TextView mMessageView;
        public final TextView mDateView;
        public SmsFireMessage mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mAddressView = (TextView) view.findViewById(R.id.address);
            mMessageView = (TextView) view.findViewById(R.id.message);
            mDateView = (TextView) view.findViewById(R.id.date);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mMessageView.getText() + "'";
        }
    }
}
