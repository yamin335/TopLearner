package com.engineersapps.eapps.ui.profile_signin

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import androidx.appcompat.widget.SearchView

import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.engineersapps.eapps.BR
import com.engineersapps.eapps.R
import com.engineersapps.eapps.databinding.UpazillaEditFragmentBinding
import com.engineersapps.eapps.models.registration.Upazilla
import com.engineersapps.eapps.ui.common.BaseFragment
import com.engineersapps.eapps.util.RecyclerItemDivider

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
                upazillaListAdapter.filter.filter(query)
                return false
            }

            override fun onQueryTextChange(query: String?): Boolean {
                // filter recycler view when text is changed
                upazillaListAdapter.filter.filter(query)
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