package com.rtchubs.engineerbooks.ui.profile_signin

import android.app.SearchManager
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.LinearLayout
import androidx.activity.addCallback
import androidx.appcompat.widget.SearchView

import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.rtchubs.engineerbooks.BR
import com.rtchubs.engineerbooks.R
import com.rtchubs.engineerbooks.databinding.DistrictEditFragmentBinding
import com.rtchubs.engineerbooks.models.registration.District
import com.rtchubs.engineerbooks.ui.common.BaseFragment
import com.rtchubs.engineerbooks.util.RecyclerItemDivider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList

class DistrictEditFragment: BaseFragment<DistrictEditFragmentBinding, DistrictEditViewModel>() {

    companion object {
        var selectedCity: District? = null
    }

    override val bindingVariable: Int
        get() = BR.viewModel
    override val layoutId: Int
        get() = R.layout.fragment_district_edit

    override val viewModel: DistrictEditViewModel by viewModels { viewModelFactory }

//    private var titleCityList = arrayOf("--Select City--")
//    lateinit var cityAdapter: ArrayAdapter<String>
    lateinit var searchView: SearchView
    lateinit var districtListAdapter: AllDistrictListAdapter

    //var tempSelectedCity: District? = null
    //val args: OtpSignInFragmentArgs by navArgs()

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

        districtListAdapter = AllDistrictListAdapter {
            selectedCity = it
            UpazillaEditFragment.selectedUpazilla = null
            navController.popBackStack()
        }

        viewDataBinding.districtRecycler.addItemDecoration(RecyclerItemDivider(requireContext(), LinearLayoutManager.VERTICAL))
        viewDataBinding.districtRecycler.adapter = districtListAdapter

//        val tempDistricts = Array(viewModel.allDistricts.value?.size ?: 0 + 1) {""}
//        tempDistricts[0] = "--Select City--"
//        viewModel.allDistricts.value?.forEachIndexed { index, city ->
//            tempDistricts[index + 1] = city.name ?: "Unknown"
//        }
//        titleCityList = tempDistricts
//        cityAdapter = ArrayAdapter(requireContext(), R.layout.spinner_item, titleCityList)
//        cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//        viewDataBinding.spCity.adapter = cityAdapter

//        viewDataBinding.spCity.onItemSelectedListener = object :
//            AdapterView.OnItemSelectedListener {
//            override fun onItemSelected(
//                parent: AdapterView<*>?,
//                view: View,
//                position: Int,
//                id: Long
//            ) {
//                if (position > 0) {
//                    try {
//                        viewModel.allDistricts.value?.let {
//                            if (it.isNotEmpty()) {
//                                tempSelectedCity = it[position - 1]
//                                viewDataBinding.btnSubmit.isEnabled = true
//                            }
//                        }
//
//                    } catch (e: IndexOutOfBoundsException) {
//                        e.printStackTrace()
//                    }
//                } else {
//                    tempSelectedCity = null
//                    viewDataBinding.btnSubmit.isEnabled = false
//                }
//            }
//
//            override fun onNothingSelected(parent: AdapterView<*>?) {}
//        }

        viewModel.allDistricts.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            it?.let { list ->
                districtListAdapter.submitList(list as ArrayList<District>)
//                val temp = Array(it.size + 1) { "" }
//                temp[0] = "--Select City--"
//                it.forEachIndexed { index, city ->
//                    temp[index + 1] = city.name ?: "Unknown"
//                }
//                titleCityList = temp
//                cityAdapter = ArrayAdapter(requireContext(), R.layout.spinner_item, titleCityList)
//                cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//                viewDataBinding.spCity.adapter = cityAdapter
            }
        })

//        viewDataBinding.btnSubmit.setOnClickListener {
//            selectedCity = tempSelectedCity
//            navController.popBackStack()
//        }

        viewModel.getDistricts()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.search_bar, menu)

        // Associate searchable configuration with the SearchView
        val searchManager = requireActivity().getSystemService(Context.SEARCH_SERVICE) as SearchManager
        searchView = menu.findItem(R.id.action_search).actionView as SearchView
        searchView.setSearchableInfo(
            searchManager.getSearchableInfo(requireActivity().componentName)
        )
        searchView.findViewById<EditText>(androidx.appcompat.R.id.search_src_text).setBackgroundResource(R.drawable.abc_textfield_search_default_mtrl_alpha)
        searchView.maxWidth = Int.MAX_VALUE

        // listening to search query text change
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // filter recycler view when query submitted
                districtListAdapter.filter.filter(query)
                return false
            }

            override fun onQueryTextChange(query: String?): Boolean {
                // filter recycler view when text is changed
                districtListAdapter.filter.filter(query)
                return false
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.action_search -> {
                return true
            }

            android.R.id.home -> {
                navController.popBackStack()
                return true
            }
        }
        //if (item.itemId == R.id.action_search) return true
        return super.onOptionsItemSelected(item)
    }

}