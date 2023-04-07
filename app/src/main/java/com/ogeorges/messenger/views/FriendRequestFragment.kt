package com.ogeorges.messenger.views

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.ogeorges.messenger.R
import com.ogeorges.messenger.databinding.FragmentFriendRequestBinding
import com.ogeorges.messenger.viewModels.FriendRequestViewModel
import com.ogeorges.messenger.views.adapter.FriendRequestAdapter

class FriendRequestFragment : Fragment() {

    companion object;

    private lateinit var viewModel: FriendRequestViewModel
    private var _binding: FragmentFriendRequestBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFriendRequestBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[FriendRequestViewModel::class.java]

        binding.friendRequestFragmentBottomNav.setOnItemSelectedListener {
            when(it.itemId){ R.id.bottomMenuList -> findNavController().navigate(R.id.action_friendRequestFragment_to_friendListFragment)
            }
            true
        }
        val col = if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE){
            2
        }
        else{
            1
        }
        binding.friendRequestFragmentList.adapter = FriendRequestAdapter(ArrayList(), viewLifecycleOwner.lifecycleScope)
        binding.friendRequestFragmentList.layoutManager = GridLayoutManager(requireContext(), col)

        binding.friendRequestFragmentSearchButton.setOnClickListener {
            viewModel.filterAdapterData(
                binding.friendRequestFragmentList.adapter as FriendRequestAdapter,
                binding.friendRequestFragmentSearchBar.text.toString())
            binding.friendRequestFragmentAddName.text.clear()
        }

        viewModel.getAllFriendRequests()

        return binding.root
    }

    override fun onResume() {
        super.onResume()

        binding.friendRequestFragmentBottomNav.selectedItemId = R.id.bottomMenuRequests
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.friendRequestFragmentAdd.setOnClickListener {
            val friendName = binding.friendRequestFragmentAddName.text.toString()
            if (friendName.trim() == "") return@setOnClickListener
            viewModel.addFriend(friendName)
            Snackbar.make(view, "Request sent", Snackbar.LENGTH_LONG).show()
            binding.friendRequestFragmentAddName.text.clear()
        }

        viewModel.getFriendRequests.observe(viewLifecycleOwner){
            if (it == null) return@observe
            (binding.friendRequestFragmentList.adapter as FriendRequestAdapter).friendRequests = ArrayList(it)
        }

    }

}