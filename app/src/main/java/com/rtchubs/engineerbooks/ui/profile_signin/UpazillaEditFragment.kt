package com.rtchubs.engineerbooks.ui.profile_signin

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.widget.SearchView

import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.rtchubs.engineerbooks.BR
import com.rtchubs.engineerbooks.R
import com.rtchubs.engineerbooks.databinding.UpazillaEditFragmentBinding
import com.rtchubs.engineerbooks.models.registration.District
import com.rtchubs.engineerbooks.models.registration.Upazilla
import com.rtchubs.engineerbooks.ui.common.BaseFragment
import com.rtchubs.engineerbooks.util.RecyclerItemDivider

class UpazillaEditFragment: BaseFragment<UpazillaEditFragmentBinding, UpazillaEditViewModel>() {

    companion object {
        var selectedUpazilla: Upazilla? = null
    }

    override val bindingVariable: Int
        get() = BR.viewModel
    override val layoutId: Int
        get() = R.layout.fragment_upazilla_edit

    override val viewModel: UpazillaEditViewModel by viewModels { viewModelFactory }

//    private var titleUpazillaList = arrayOf("--Select Upazilla--")
//    lateinit var upazillaAdapter: ArrayAdapter<String>
//    var tempSelectedUpazilla: Upazilla? = null

    lateinit var searchView: SearchView
    lateinit var upazillaListAdapter: AllUpazillaListAdapter

    val args: UpazillaEditFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
//        requireActivity().onBackPressedDispatcher.addCallback {
//            // close search view on back button pressed
//            if (!searchView.isIconified) {
//                searchView.isIconified = true
//            } else {
//                navController.popBackStack()
//            }
//        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateStatusBarBackgroundColor("#1E4356")
        registerToolbar(viewDataBinding.toolbar)

        upazillaListAdapter = AllUpazillaListAdapter {
            selectedUpazilla = it
            navController.popBackStack()
        }

        viewDataBinding.upazillaRecycler.addItemDecoration(RecyclerItemDivider(requireContext(), LinearLayoutManager.VERTICAL))
        viewDataBinding.upazillaRecycler.adapter = upazillaListAdapter

//        val tempUpazilla = Array(viewModel.allUpazilla.value?.size ?: 0 + 1) {""}
//        tempUpazilla[0] = "--Select Upazilla--"
//        viewModel.allUpazilla.value?.forEachIndexed { index, upazilla ->
//            tempUpazilla[index + 1] = upazilla.name ?: "Unknown"
//        }
//        titleUpazillaList = tempUpazilla
//        upazillaAdapter = ArrayAdapter(requireContext(), R.layout.spinner_item, titleUpazillaList)
//        upazillaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//        viewDataBinding.spUpazilla.adapter = upazillaAdapter

//        viewDataBinding.spUpazilla.onItemSelectedListener = object :
//            AdapterView.OnItemSelectedListener {
//            override fun onItemSelected(
//                parent: AdapterView<*>?,
//                view: View,
//                position: Int,
//                id: Long
//            ) {
//                if (position > 0) {
//                    try {
//                        viewModel.allUpazilla.value?.let {
//                            if (it.isNotEmpty()) {
//                                tempSelectedUpazilla = it[position - 1]
//                                viewDataBinding.btnSubmit.isEnabled = true
//                            }
//                        }
//                    } catch (e: IndexOutOfBoundsException) {
//                        e.printStackTrace()
//                    }
//                } else {
//                    tempSelectedUpazilla = null
//                    viewDataBinding.btnSubmit.isEnabled = false
//                }
//            }
//
//            override fun onNothingSelected(parent: AdapterView<*>?) {}
//        }

        viewModel.allUpazilla.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            it?.let { list ->
                upazillaListAdapter.submitList(list as ArrayList<Upazilla>)
//                val temp = Array(it.size + 1) {""}
//                temp[0] = "--Select Upazilla--"
//                it.forEachIndexed { index, upazilla ->
//                    temp[index + 1] = upazilla.name ?: "Unknown"
//                }
//                titleUpazillaList = temp
//                upazillaAdapter = ArrayAdapter(requireContext(), R.layout.spinner_item, titleUpazillaList)
//                upazillaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//                viewDataBinding.spUpazilla.adapter = upazillaAdapter
            }
        })

//        viewDataBinding.btnSubmit.setOnClickListener {
//            selectedUpazilla = tempSelectedUpazilla
//            navController.popBackStack()
//        }

        viewModel.getUpazilla(args.cityId)
    }
}