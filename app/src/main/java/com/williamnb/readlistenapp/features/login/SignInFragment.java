package com.williamnb.readlistenapp.features.login;

import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.williamnb.readlistenapp.R;
import com.williamnb.readlistenapp.base.BaseFragment;
import com.williamnb.readlistenapp.databinding.FragmentSignInBinding;
import com.williamnb.readlistenapp.utilities.Constants;
import com.williamnb.readlistenapp.utilities.preferences.PreferenceManager;

public class SignInFragment extends BaseFragment<FragmentSignInBinding, SignInViewModel> {

    private PreferenceManager preferenceManager;

    @Override
    public SignInViewModel createViewModel() {
        return new ViewModelProvider(this).get(SignInViewModel.class);
    }

    @Override
    public FragmentSignInBinding createViewBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentSignInBinding.inflate(inflater, container, false);
    }

    @Override
    public void initializeView() {
        hideBottomNavigationView(true);
        preferenceManager = new PreferenceManager(requireContext());
        if (preferenceManager.getBoolean(Constants.KEY_IS_SIGNED_IN)) {
            findNavController().navigate(R.id.actionToChatMain);
        }
    }

    @Override
    public void initializeComponent() {
    }

    @Override
    public void initializeEvents() {
        viewBinding.btnSignIn.setOnClickListener(view -> {
            if (isValidSignInDetails()) {
                signIn();
            }
        });
        viewBinding.tvCreateNewAccount.setOnClickListener(view -> findNavController().navigate(R.id.actionSignUp));
        viewBinding.btnBack.setOnClickListener(view -> findNavController().popBackStack());
    }

    @Override
    public void initializeData() {}

    private void signIn() {
        loading(true);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Constants.KEY_COLLECTION_USERS)
                .whereEqualTo(Constants.KEY_EMAIL, viewBinding.inputAccount.getText().toString())
                .whereEqualTo(Constants.KEY_PASSWORD, viewBinding.inputPassword.getText().toString())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null
                            && task.getResult().getDocuments().size() > 0) {
                        DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                        preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN, true);
                        preferenceManager.putString(Constants.KEY_USER_ID, documentSnapshot.getId());
                        preferenceManager.putString(Constants.KEY_NAME, documentSnapshot.getString(Constants.KEY_NAME));
                        preferenceManager.putString(Constants.KEY_IMAGE, documentSnapshot.getString(Constants.KEY_IMAGE));
                        findNavController().navigate(R.id.actionToChatMain);
                    } else {
                        loading(false);
                        viewModel.showToast("Kh??ng th??? ????ng nh???p", getContext());
                    }
                });
    }

    private void loading(Boolean isLoading) {
        if (isLoading) {
            viewBinding.btnSignIn.setVisibility(View.INVISIBLE);
            viewBinding.progressBar.setVisibility(View.VISIBLE);
        } else {
            viewBinding.btnSignIn.setVisibility(View.VISIBLE);
            viewBinding.progressBar.setVisibility(View.INVISIBLE);
        }
    }

    private Boolean isValidSignInDetails() {
        if (viewBinding.inputAccount.getText().toString().trim().isEmpty()) {
            viewModel.showToast("Nh???p Email", getContext());
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(viewBinding.inputAccount.getText().toString()).matches()) {
            viewModel.showToast("Nh???p ????ng ?????nh d???ng email", getContext());
            return false;
        } else if (viewBinding.inputPassword.getText().toString().trim().isEmpty()) {
            viewModel.showToast("Nh???p m???t kh???u", getContext());
            return false;
        } else {
            return true;
        }
    }
}
