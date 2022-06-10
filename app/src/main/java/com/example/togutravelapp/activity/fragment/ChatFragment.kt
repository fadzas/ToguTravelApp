package com.example.togutravelapp.activity.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.togutravelapp.activity.ChatListActivity
import com.example.togutravelapp.adapter.FbMessageAdapter
import com.example.togutravelapp.data.DummyTourGuideData
import com.example.togutravelapp.data.MessageData
import com.example.togutravelapp.data.repository.UserRepository
import com.example.togutravelapp.databinding.FragmentChatBinding
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*


class ChatFragment : Fragment() {
    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!

    private lateinit var chatdb : FirebaseDatabase
    private lateinit var chatAdapter : FbMessageAdapter
    private lateinit var personAvatar : CircleImageView
    private lateinit var personName : TextView
    private lateinit var messagesRv : RecyclerView
    private lateinit var auth: FirebaseAuth
    private lateinit var type: String

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
        messagesRv = binding.chatRv
        auth = Firebase.auth
        val repo = UserRepository(requireContext())

        chatdb = Firebase.database
        personAvatar = binding.chatAva
        personName = binding.chatName

        val bundle = arguments
        val emailPerson = bundle!!.getString(MESSAGES_PERSON)!!
        val name = bundle.getString(MESSAGES_NAME)!!
        val url = bundle.getString(MESSAGES_URL)!!
        type = bundle.getString(MESSAGES_TYPE)!!

        val userData : DummyTourGuideData = if (auth.currentUser != null){
            DummyTourGuideData(
                tgEmail = auth.currentUser!!.email.toString(),
                tgUrl = auth.currentUser!!.photoUrl.toString(),
                tgName = auth.currentUser!!.displayName.toString()
            )
        } else {
            DummyTourGuideData(
                tgEmail = repo.getUserLoginInfoSession().email,
                tgUrl = repo.getUserProfileImage().toString(),
                tgName = repo.getUserLoginInfoSession().nama
            )
        }

        val invalidEmailUser = userData.tgEmail.toString()+"-2"
        val validEmailUser = invalidEmailUser.replace(".","dot")

        //val invalidEmailPerson = usersLogin.tgEmail.toString()+"-1"
        //val validEmailPerson = invalidEmail.replace(".","dot")

        val msgRefUser = chatdb.reference.child(validEmailUser)
            .child(MESSAGES_PERSON)
            .child(emailPerson)
            .child(MESSAGES_CHILD)

        val msgRefPerson = chatdb.reference.child(emailPerson)
            .child(MESSAGES_PERSON)
            .child(validEmailUser)
            .child(MESSAGES_CHILD)

        val options = FirebaseRecyclerOptions.Builder<MessageData>()
            .setQuery(msgRefUser, MessageData::class.java)
            .build()

        messagesRv.layoutManager = manager
        chatAdapter = FbMessageAdapter(options,userData.tgName)
        messagesRv.adapter = chatAdapter

        chatAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver(){
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                super.onItemRangeInserted(positionStart, itemCount)
                val msgCount = chatAdapter.itemCount
                val lastPos = manager.findLastCompletelyVisibleItemPosition()
                if (lastPos == -1 ||
                    (positionStart >= (msgCount - 1) && lastPos == (positionStart - 1))){
                    messagesRv.scrollToPosition(positionStart)
                }
            }
        })

        personName.text = name
        Glide.with(this)
            .load(url)
            .centerCrop()
            .into(personAvatar)

        binding.sendChatButton.setOnClickListener {
            val msg = MessageData(
                binding.chatMessageEdittext.text.toString(),
                userData.tgName.toString(),
                validEmailUser,
                userData.tgUrl.toString(),
                Date().time
            )
            msgRefUser.push().setValue(msg) { e,_ ->
                if (e != null) Toast.makeText(requireContext(), "error sending message" + e.message, Toast.LENGTH_SHORT).show()
            }
            msgRefPerson.push().setValue(msg)
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
        if (type == "chatlist")
            (activity as ChatListActivity).enableAllButton()
    }

    companion object {
        const val MESSAGES_CHILD = "messages"
        const val MESSAGES_PERSON = "users"
        const val MESSAGES_NAME = "name"
        const val MESSAGES_URL = "url"
        const val MESSAGES_TYPE = "type"
    }
}