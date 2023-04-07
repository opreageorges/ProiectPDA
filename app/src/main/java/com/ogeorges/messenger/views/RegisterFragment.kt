package com.ogeorges.messenger.views

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.ogeorges.messenger.R
import com.ogeorges.messenger.databinding.FragmentRegisterBinding
import com.ogeorges.messenger.viewModels.RegisterViewModel

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    companion object;

    private lateinit var viewModel: RegisterViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[RegisterViewModel::class.java]
        val username = binding.registerFragmentUsername
        val password = binding.registerFragmentPassword
        val email = binding.registerFragmentEmailAddress

        binding.registerFragmentLoading.visibility = View.GONE

        viewModel.getRegisterForm.observe(viewLifecycleOwner
        ) { form ->
            binding.registerFragmentRegisterButton.isActivated = (form["ok"] == 1)
            form["username"]?.let {
                username.error = getString(it)
            }
            form["email"]?.let {
                email.error = getString(it)
            }
            form["password"]?.let {
                password.error = getString(it)
            }
        }

        val afterTextChangedListener = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable) {
                viewModel.registerDataChanged(
                    username.text.toString(),email.text.toString() , password.text.toString()
                )
            }
        }

        username.addTextChangedListener(afterTextChangedListener)
        password.addTextChangedListener(afterTextChangedListener)
        email.addTextChangedListener(afterTextChangedListener)


        val transitionListener = object : MotionLayout.TransitionListener{
            var startOrEnd = true
            override fun onTransitionStarted(
                motionLayout: MotionLayout?,
                startId: Int,
                endId: Int
            ) {}

            override fun onTransitionChange(
                motionLayout: MotionLayout?,
                startId: Int,
                endId: Int,
                progress: Float
            ) {
            }

            override fun onTransitionCompleted(motionLayout: MotionLayout?, currentId: Int) {
                if (startOrEnd){
                    motionLayout!!.transitionToState(R.id.middle)
                }else{
                    motionLayout!!.transitionToState(R.id.end)
                }
                startOrEnd = !startOrEnd
            }

            override fun onTransitionTrigger(
                motionLayout: MotionLayout?,
                triggerId: Int,
                positive: Boolean,
                progress: Float
            ) {
            }

        }

        binding.registerFragmentRegisterButton.setOnClickListener {
            viewModel.onRegister(username.text.toString(),email.text.toString() , password.text.toString())
            viewModel.getRegisterResult.observe(viewLifecycleOwner, object : Observer<Int>{
                override fun onChanged(value: Int) {
                    when (value) {
                        -1 -> loadingOn(transitionListener)
                        0 -> {
                            Snackbar.make(view, "Register failed", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show()
                            loadingOff(this, transitionListener)
                        }
                        1 -> {
                            Snackbar.make(view, "Register successful", Snackbar.LENGTH_LONG).setAction("Action", null).show()
                            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
                        }
                    }
                }

            })
        }

        binding.registerFragmentBackButton.setOnClickListener {
            findNavController().popBackStack()
        }

    }

    private fun loadingOn(transitionListener: MotionLayout.TransitionListener) {
        binding.registerFragmentLayout.transitionToState(R.id.middle)
        binding.registerFragmentLayout.transitionToState(R.id.end)
        binding.registerFragmentLayout.addTransitionListener(transitionListener)
        binding.registerFragmentLinearLayout.children.forEach {
            it.isEnabled = false
        }

    }

    private fun loadingOff(o: Observer<Int>, tl: MotionLayout.TransitionListener) {
        binding.registerFragmentLayout.removeTransitionListener(tl)
        binding.registerFragmentLayout.transitionToState(R.id.start)
        binding.registerFragmentLinearLayout.children.forEach {
            it.isEnabled = true
        }
        viewModel.getRegisterResult.removeObserver(o)
    }
}