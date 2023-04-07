package com.ogeorges.messenger.views

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.ogeorges.messenger.R
import com.ogeorges.messenger.databinding.FragmentLoginBinding
import com.ogeorges.messenger.viewModels.LoginViewModel

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    companion object;

    private lateinit var viewModel: LoginViewModel

//
//    private lateinit var launcher: ActivityResultLauncher<Intent>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[LoginViewModel::class.java]
        if (findNavController().backQueue.size > 0) findNavController().clearBackStack(findNavController().backQueue.first().id)

        binding.loginFragmentGoogle.setOnClickListener {
            viewModel.loginWithGoogle(requireContext(), this, binding.loginFragmentUsernameEditText.text.toString())
            binding.loginFragmentUsernameEditText.text.clear()

        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val username = binding.loginFragmentUsernameEditText
        val password = binding.loginFragmentPasswordEditText
        val navController = findNavController()
        binding.loginFragmentRegisterButton.setOnClickListener {
            navController.navigate(R.id.action_loginFragment_to_registerFragment)
        }

        binding.loginFragmentLoginButton.setOnClickListener {
            viewModel.getAuthResult.observe(viewLifecycleOwner, object : Observer<Int>{
                override fun onChanged(value: Int) {
                    when (value) {
                        -1 -> loadingOn()
                        0 -> {
                            Snackbar.make(view, "Username sau parola invalida", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show()
                            loadingOff(this)
                        }
                        1 -> navController.navigate(R.id.action_loginFragment_to_friendListFragment)
                    }
                }

            })
            viewModel.login(username.text.toString(), password.text.toString(), requireContext().getSharedPreferences("user", Context.MODE_PRIVATE))
        }

        viewModel.getGoogleAuthResult.observe(viewLifecycleOwner){
            when(it){
                -3 -> Snackbar.make(binding.root, "There is a problem with the server", Snackbar.LENGTH_LONG).show()
                -2 -> Snackbar.make(binding.root, "Failed", Snackbar.LENGTH_LONG).show()
                -1 -> Snackbar.make(binding.root, "Username invalid or taken", Snackbar.LENGTH_LONG).show()
                0 -> {
                    Snackbar.make(binding.root, "Success", Snackbar.LENGTH_LONG).show()
                    navController.navigate(R.id.action_loginFragment_to_friendListFragment)
                }
            }

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 420){
            viewModel.loginWithGoogleResult(
                data,
                resultCode,
                requireContext().getSharedPreferences("user", Context.MODE_PRIVATE)
            )
        }

    }

    private fun loadingOn(){
        (binding.loginFragmentForm as LinearLayout).children.forEach {
            it.isEnabled = false
        }
        binding.root.children.forEach {
            it.isEnabled = false
        }

        val view = binding.loginFragmentLoading
        view.visibility = View.VISIBLE
        val animator = ObjectAnimator.ofFloat(view, View.ROTATION, view.rotation + 360f)
        animator.duration = 1000
        animator.repeatMode = ValueAnimator.RESTART
        animator.repeatCount = -1
        animator.start()
    }

    private fun loadingOff(o: Observer<Int>) {
        (binding.loginFragmentForm as LinearLayout).children.forEach {
            it.isEnabled = true
        }
        viewModel.getAuthResult.removeObserver(o)
        binding.loginFragmentLoading.visibility = View.GONE
    }


}