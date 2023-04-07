package com.ogeorges.messenger.views

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ogeorges.messenger.R
import com.ogeorges.messenger.databinding.FragmentFriendListBinding
import com.ogeorges.messenger.repositories.Authenticator
import com.ogeorges.messenger.viewModels.FriendListViewModel
import com.ogeorges.messenger.views.adapter.FriendListAdapter

class FriendListFragment : Fragment() {

    companion object;

    private lateinit var viewModel: FriendListViewModel
    private var _binding: FragmentFriendListBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFriendListBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[FriendListViewModel::class.java]

        if (findNavController().backQueue.size > 0) findNavController().clearBackStack(findNavController().backQueue.first().id)

        binding.friendListFragmentLogOut.setOnClickListener {
            viewModel.logout(requireContext().getSharedPreferences("user", Context.MODE_PRIVATE))
            requireActivity().title = requireContext().getString(requireActivity().applicationInfo.labelRes)
            findNavController().navigate(R.id.action_friendListFragment_to_loginFragment)

        }

        binding.friendListFragmentBottomNav.setOnItemSelectedListener {
            when(it.itemId){ R.id.bottomMenuRequests -> findNavController().navigate(R.id.action_friendListFragment_to_friendRequestFragment)
            }
            true
        }

        requireActivity().title = Authenticator.currentUsername

        viewModel.getAllFriends()

        binding.friendListFragmentFriendList.adapter = FriendListAdapter(
            ArrayList(),
            viewLifecycleOwner.lifecycleScope,
            findNavController()
        )
        binding.friendListFragmentFriendList.layoutManager = LinearLayoutManager(requireContext())

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        binding.friendListFragmentBottomNav.selectedItemId = R.id.bottomMenuList
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getFriends.observe(viewLifecycleOwner){
            if (it == null) return@observe
            (binding.friendListFragmentFriendList.adapter as FriendListAdapter).friendList = ArrayList(it)
        }




    }

}