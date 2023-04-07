package com.ogeorges.messenger.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.Timestamp
import com.ogeorges.messenger.databinding.FragmentMessageRoomBinding
import com.ogeorges.messenger.models.Message
import com.ogeorges.messenger.viewModels.MessageRoomViewModel
import com.ogeorges.messenger.views.adapter.MessageAdapter

class MessageRoomFragment : Fragment() {

    private lateinit var viewModel: MessageRoomViewModel
    private var _binding: FragmentMessageRoomBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMessageRoomBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[MessageRoomViewModel::class.java]
        viewModel.friendUsername = requireArguments().getString("friendUsername")!!
        viewModel.getMessagesFromDB()
        requireActivity().title = requireActivity().title.toString() + " -> " + viewModel.friendUsername
        binding.fragmentMessageRoomBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.fragmentMessageRoomMessageArea.adapter = MessageAdapter(
            ArrayList(listOf(
                Message("Loading",
                    Timestamp.now(),
                "1",
                true)))
        )

        val messageAreaLayout = LinearLayoutManager(requireContext())
        messageAreaLayout.stackFromEnd = true
        binding.fragmentMessageRoomMessageArea.layoutManager = messageAreaLayout

        binding.fragmentMessageRoomSend.setOnClickListener {
            val messageBody = binding.fragmentMessageRoomInput.text.toString().trim()
            if (messageBody == "") return@setOnClickListener
            val adapter = binding.fragmentMessageRoomMessageArea.adapter as MessageAdapter
            binding.fragmentMessageRoomInput.text.clear()
            val message = Message(messageBody, Timestamp.now(), (adapter.lastIndex() + 1).toString(), true)
            adapter.add(message)
            viewModel.sendMessage(message)
            binding.fragmentMessageRoomMessageArea.smoothScrollToPosition(adapter.lastIndex() - 1)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getMessages.observe(viewLifecycleOwner){
            if (it == null) return@observe
            (binding.fragmentMessageRoomMessageArea.adapter as MessageAdapter).messageList = ArrayList(it)
            val lastIndex = (binding.fragmentMessageRoomMessageArea.adapter!! as MessageAdapter).lastIndex() -1
            if (lastIndex <= 0) return@observe
            binding.fragmentMessageRoomMessageArea.smoothScrollToPosition(
                lastIndex)
        }

    }

}