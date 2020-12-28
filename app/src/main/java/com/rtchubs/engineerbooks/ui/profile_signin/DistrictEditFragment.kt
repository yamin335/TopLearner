package com.rtchubs.engineerbooks.ui.profile_signin

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter

import androidx.fragment.app.viewModels
import com.rtchubs.engineerbooks.BR
import com.rtchubs.engineerbooks.R
import com.rtchubs.engineerbooks.databinding.DistrictEditFragmentBinding
import com.rtchubs.engineerbooks.models.registration.District
import com.rtchubs.engineerbooks.ui.common.BaseFragment

class DistrictEditFragment: BaseFragment<DistrictEditFragmentBinding, DistrictEditViewModel>() {

    companion object {
        var selectedCity: District? = null
    }

    override val bindingVariable: Int
        get() = BR.viewModel
    override val layoutId: Int
        get() = R.layout.fragment_district_edit

    override val viewModel: DistrictEditViewModel by viewModels { viewModelFactory }

    private var titleCityList = arrayOf("--Select City--")
    lateinit var cityAdapter: ArrayAdapter<String>

    var tempSelectedCity: District? = null
    //val args: OtpSignInFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateStatusBarBackgroundColor("#1E4356")
        registerToolbar(viewDataBinding.toolbar)

        val tempDistricts = Array(viewModel.allDistricts.value?.size ?: 0 + 1) {""}
        tempDistricts[0] = "--Select City--"
        viewModel.allDistricts.value?.forEachIndexed { index, city ->
            tempDistricts[index + 1] = city.name ?: "Unknown"
        }
        titleCityList = tempDistricts
        cityAdapter = ArrayAdapter(requireContext(), R.layout.spinner_item, titleCityList)
        cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        viewDataBinding.spCity.adapter = cityAdapter

        viewDataBinding.spCity.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View,
                position: Int,
                id: Long
            ) {
                if (position > 0) {
                    try {
                        viewModel.allDistricts.value?.let {
                            if (it.isNotEmpty()) {
                                tempSelectedCity = it[position - 1]
                                viewDataBinding.btnSubmit.isEnabled = true
                            }
                        }

                    } catch (e: IndexOutOfBoundsException) {
                        e.printStackTrace()
                    }
                } else {
                    tempSelectedCity = null
                    viewDataBinding.btnSubmit.isEnabled = false
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        viewModel.allDistricts.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            it?.let {
                val temp = Array(it.size + 1) {""}
                temp[0] = "--Select City--"
                it.forEachIndexed { index, city ->
                    temp[index + 1] = city.name ?: "Unknown"
                }
                titleCityList = temp
                cityAdapter = ArrayAdapter(requireContext(), R.layout.spinner_item, titleCityList)
                cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                viewDataBinding.spCity.adapter = cityAdapter
            }
        })

        viewDataBinding.btnSubmit.setOnClickListener {
            selectedCity = tempSelectedCity
            navController.popBackStack()
        }

        viewModel.getDistricts()
    }
}