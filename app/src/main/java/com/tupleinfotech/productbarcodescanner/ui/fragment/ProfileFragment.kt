package com.tupleinfotech.productbarcodescanner.ui.fragment

import android.content.SharedPreferences
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.tupleinfotech.productbarcodescanner.R
import com.tupleinfotech.productbarcodescanner.databinding.FragmentProfileBinding
import com.tupleinfotech.productbarcodescanner.ui.adapter.ProfileItemAdapter
import com.tupleinfotech.productbarcodescanner.util.Constants
import com.tupleinfotech.productbarcodescanner.util.PreferenceHelper
import com.tupleinfotech.productbarcodescanner.util.PreferenceHelper.host
import com.tupleinfotech.productbarcodescanner.util.PreferenceHelper.userfullname
import com.tupleinfotech.productbarcodescanner.util.PreferenceHelper.userprofileimage
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    //region VARIABLES
    private var _binding                                    : FragmentProfileBinding?               =  null
    private val binding                                     get()                                   =  _binding!!
    private var profileItemAdapter                          : ProfileItemAdapter                    = ProfileItemAdapter()
    private val profileItemData                             : ArrayList<String>                     = arrayListOf()
    private lateinit var prefs                              : SharedPreferences

    //endregion VARIABLES

    //region OVERRIDE METHODS (LIFECYCLE)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val view = binding.root
        prefs = PreferenceHelper.customPreference(requireContext(), Constants.CUSTOM_PREF_NAME)

        init()

        return view
    }

    //endregion OVERRIDE METHODS (LIFECYCLE)

    //region INIT METHOD
    private fun init() {
        loadImage()
        onBackPressed()
        initProfileFields()
        initChangePassword()
    }
    //endregion INIT METHOD

    //region BUTTON FUNCTIONALITY

    private fun initChangePassword(){
        binding.btnChangePassword.setOnClickListener{
            val bundle = Bundle()
            bundle.putBoolean("isFromProfile",true)
            findNavController().navigate(R.id.changePasswordFragment,bundle)
            profileItemData.clear()
        }
    }
    //endregion BUTTON FUNCTIONALITY

    //region ALL FUNCTIONS

    private fun loadImage(){
        if (prefs.userfullname.toString()[0].uppercaseChar().toString().trim().isEmpty() && prefs.userfullname.toString()[0].uppercaseChar().toString().trim().isBlank()){
            binding.profileLayout.shapeableImageViewtext.text = prefs.userfullname.toString()[0].uppercaseChar().toString().trim()
        }

        Glide.with(binding.root)
            .load(prefs.host+"v1/"+prefs.userprofileimage)
            .fitCenter()
            .listener(object : RequestListener<Drawable?> {

                override fun onResourceReady(
                    resource: Drawable,
                    model: Any,
                    target: Target<Drawable?>?,
                    dataSource: DataSource,
                    isFirstResource: Boolean
                ): Boolean {
                    binding.profileLayout.shapeableImageViewtext.visibility = View.GONE
                    return false
                }

                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable?>,
                    isFirstResource: Boolean
                ): Boolean {
                    binding.profileLayout.shapeableImageViewtext.visibility = View.VISIBLE
                    return false
                }
            })
            .into(binding.profileLayout.shapeableImageView)
    }
    private fun initProfileFields(){

        profileItemData.add("Full Name")
        profileItemData.add("Email")
        profileItemData.add("Phone")
        profileItemData.add("Address")
        profileItemData.add("DOB")
        profileItemData.add("Gender")

        val linearLayoutManager : RecyclerView.LayoutManager    = LinearLayoutManager(requireActivity())
        val recyclerViewItemList                                = binding.profileRv
        recyclerViewItemList.layoutManager                      = linearLayoutManager
        recyclerViewItemList.itemAnimator                       = DefaultItemAnimator()
        profileItemAdapter                                      = ProfileItemAdapter()

        profileItemAdapter.apply {
            updateList(profileItemData)
        }

        recyclerViewItemList.adapter                            = profileItemAdapter

    }

    //endregion ALL FUNCTIONS

    //region BACK EVENT FUNCTIONS

    private fun onBackPressed() {
        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().popBackStack()
                profileItemData.clear()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,onBackPressedCallback)

    }
    //endregion BACK EVENT FUNCTIONS

    //region API SERVICE
    //endregion API SERVICE
}