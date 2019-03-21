package ntw.jumo.smsfire;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import ntw.jumo.smsfire.db.Sms;
import ntw.jumo.smsfire.db.SmsRepository;


/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class SmsFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    private static final String ARG_IS_SPAM = "is_spam";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private boolean mIsSpam =  false;
    private OnListFragmentInteractionListener mListener;
    private MySmsRecyclerViewAdapter mAdapter;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public SmsFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static SmsFragment newInstance(int columnCount, boolean isSpam) {
        SmsFragment fragment = new SmsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        args.putBoolean(ARG_IS_SPAM, isSpam);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
            mIsSpam = getArguments().getBoolean(ARG_IS_SPAM);
        }

        mAdapter = new MySmsRecyclerViewAdapter(mListener);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sms_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            SmsRepository repo =  new SmsRepository(context);

            recyclerView.setAdapter(mAdapter);

            new fetchSmsAsyncTask(repo, mAdapter).execute(mIsSpam);
        }
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(SmsFireContent.SmsFireMessage item);
    }

    private static class fetchSmsAsyncTask extends AsyncTask<Boolean, Void, List>{
        SmsRepository mRepo;
        MySmsRecyclerViewAdapter adapter;

        fetchSmsAsyncTask(SmsRepository repo, MySmsRecyclerViewAdapter adapt){
            mRepo = repo;
            adapter = adapt;
        }

        @Override
        protected List<SmsFireContent.SmsFireMessage> doInBackground(final Boolean... params) {

            List<Sms> smsList = mRepo.getSmsByIsSpam(params[0]);
            List<SmsFireContent.SmsFireMessage> SMS = new ArrayList<SmsFireContent.SmsFireMessage>();

            for (Sms smsItem: smsList) {

                SMS.add(new SmsFireContent.SmsFireMessage(String.valueOf(smsItem.uid), smsItem.message,
                        smsItem.address, smsItem.date));
            }


            return SMS;
        }

        protected void onPostExecute(List result) {
            adapter.updateMessages(result);
        }
    }
}
