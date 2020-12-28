package com.rtchubs.engineerbooks.ui.profile_signin

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter

import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.rtchubs.engineerbooks.BR
import com.rtchubs.engineerbooks.R
import com.rtchubs.engineerbooks.databinding.UpazillaEditFragmentBinding
import com.rtchubs.engineerbooks.models.registration.Upazilla
import com.rtchubs.engineerbooks.ui.common.BaseFragment

class UpazillaEditFragment: BaseFragment<UpazillaEditFragmentBinding, UpazillaEditViewModel>() {

    companion object {
        var selectedUpazilla: Upazilla? = null
    }

    override val bindingVariable: Int
        get() = BR.viewModel
    override val layoutId: Int
        get() = R.layout.fragment_upazilla_edit

    override val viewModel: UpazillaEditViewModel by viewModels { viewModelFactory }

    private var titleUpazillaList = arrayOf("--Select Upazilla--")
    lateinit var upazillaAdapter: ArrayAdapter<String>
    var tempSelectedUpazilla: Upazilla? = null
    val args: UpazillaEditFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateStatusBarBackgroundColor("#1E4356")
        registerToolbar(viewDataBinding.toolbar)

        val tempUpazilla = Array(viewModel.allUpazilla.value?.size ?: 0 + 1) {""}
        tempUpazilla[0] = "--Select Upazilla--"
        viewModel.allUpazilla.value?.forEachIndexed { index, upazilla ->
            tempUpazilla[index + 1] = upazilla.name ?: "Unknown"
        }
        titleUpazillaList = tempUpazilla
        upazillaAdapter = ArrayAdapter(requireContext(), R.layout.spinner_item, titleUpazillaList)
        upazillaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        viewDataBinding.spUpazilla.adapter = upazillaAdapter

        viewDataBinding.spUpazilla.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View,
                position: Int,
                id: Long
            ) {
                if (position > 0) {
                    try {
                        viewModel.allUpazilla.value?.let {
                            if (it.isNotEmpty()) {
                                tempSelectedUpazilla = it[position - 1]
                                viewDataBinding.btnSubmit.isEnabled = true
                            }
                        }
                    } catch (e: IndexOutOfBoundsException) {
                        e.printStackTrace()
                    }
                } else {
                    tempSelectedUpazilla = null
                    viewDataBinding.btnSubmit.isEnabled = false
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        viewModel.allUpazilla.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            it?.let {
                val temp = Array(it.size + 1) {""}
                temp[0] = "--Select Upazilla--"
                it.forEachIndexed { index, upazilla ->
                    temp[index + 1] = upazilla.name ?: "Unknown"
                }
                titleUpazillaList = temp
                upazillaAdapter = ArrayAdapter(requireContext(), R.layout.spinner_item, titleUpazillaList)
                upazillaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                viewDataBinding.spUpazilla.adapter = upazillaAdapter
            }
        })

        viewDataBinding.btnSubmit.setOnClickListener {
            selectedUpazilla = tempSelectedUpazilla
            navController.popBackStack()
        }

        viewModel.getUpazilla(args.cityId)
    }
}