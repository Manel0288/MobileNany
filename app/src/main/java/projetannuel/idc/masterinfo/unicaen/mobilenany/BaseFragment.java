package projetannuel.idc.masterinfo.unicaen.mobilenany;

import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

public abstract class BaseFragment extends Fragment {

        protected FragmentActivity mActivity;

        @Override
        public void onAttach(Context context) {
            super.onAttach(context);

            if (context instanceof FragmentActivity){
                mActivity = (FragmentActivity) context;
            }
        }
}
