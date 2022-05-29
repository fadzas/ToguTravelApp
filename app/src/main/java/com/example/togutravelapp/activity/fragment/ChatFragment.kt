package com.example.togutravelapp.activity.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.togutravelapp.adapter.FbMessageAdapter
import com.example.togutravelapp.data.MessageData
import com.example.togutravelapp.databinding.FragmentChatBinding
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.*

class ChatFragment : Fragment() {
    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!
    private lateinit var chatdb : FirebaseDatabase
    private lateinit var chatAdapter : FbMessageAdapter
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val manager = LinearLayoutManager(requireActivity())
        manager.stackFromEnd = true
        binding.chatRv.layoutManager = manager

        auth = Firebase.auth
        val fbUser = auth.currentUser
        chatdb = Firebase.database
        val msgRef = chatdb.reference.child(MESSAGES_CHILD)

        val options = FirebaseRecyclerOptions.Builder<MessageData>()
            .setQuery(msgRef, MessageData::class.java)
            .build()
        chatAdapter = FbMessageAdapter(options,fbUser?.displayName)
        binding.chatRv.adapter = chatAdapter

        binding.sendChatButton.setOnClickListener {
            val msg = MessageData(
                binding.chatMessageEdittext.text.toString(),
                fbUser?.displayName.toString(),
                fbUser?.uid,
                fbUser?.photoUrl.toString(),
                Date().time
            )
            msgRef.push().setValue(msg) { e,_ ->
                if (e != null) Toast.makeText(requireContext(), "error sending message" + e.message, Toast.LENGTH_SHORT).show()
            }
            binding.chatMessageEdittext.setText("")
        }
    }

    override fun onResume() {
        super.onResume()
        chatAdapter.startListening()
    }

    override fun onPause() {
        chatAdapter.stopListening()
        super.onPause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    companion object {
        const val MESSAGES_CHILD = "messages"
    }
}