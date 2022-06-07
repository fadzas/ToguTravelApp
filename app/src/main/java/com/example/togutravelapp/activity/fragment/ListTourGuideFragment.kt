package com.example.togutravelapp.activity.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.togutravelapp.R
import com.example.togutravelapp.activity.ChatListActivity
import com.example.togutravelapp.adapter.ListTourGuideAdapter
import com.example.togutravelapp.data.DummyTourGuideData
import com.example.togutravelapp.databinding.FragmentListTourGuideBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import de.hdodenhof.circleimageview.CircleImageView

class ListTourGuideFragment : Fragment() {
    private var _binding: FragmentListTourGuideBinding? = null
    private val binding get() = _binding!!
    private lateinit var rvTogu: RecyclerView
    private lateinit var profile : CircleImageView
    private lateinit var auth : FirebaseAuth
    private lateinit var msgButton : FloatingActionButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListTourGuideBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        profile = binding.listToguProfile
        auth = Firebase.auth

        Glide.with(this)
            .load(auth.currentUser!!.photoUrl)
            .centerCrop()
            .into(profile)
        msgButton = binding.chatListButton
        msgButton.setOnClickListener {
            intentToMessageActivity()
        }
        rvTogu = binding.rvTogu
        rvTogu.setHasFixedSize(true)

        setToguData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setToguData(){
        val dummyListTogu = getDummyToguData()
        rvTogu.layoutManager = GridLayoutManager(requireContext(),2)
        rvTogu.adapter = ListTourGuideAdapter(dummyListTogu)
    }

    private fun getDummyToguData():List<DummyTourGuideData>{

        val imageUrlList = resources.getStringArray(R.array.tgUrl)
        val nameList = resources.getStringArray(R.array.tgName)
        val genderList = resources.getStringArray(R.array.tgGender)
        val ratingList = resources.getStringArray(R.array.tgRating)
        val priceList = resources.getStringArray(R.array.tgPrice)
        val listTogu = ArrayList<DummyTourGuideData>()
        for (i in nameList.indices){
            listTogu.add(
                DummyTourGuideData(
                    tgUrl = imageUrlList[i],
                    tgName = nameList[i],
                    tgGender = genderList[i],
                    tgRating = ratingList[i],
                    tgPrice = priceList[i],
                )
            )
        }
        return listTogu
    }

    private fun intentToMessageActivity(){
        val intent = Intent(activity,ChatListActivity::class.java)
        startActivity(intent)
    }
}