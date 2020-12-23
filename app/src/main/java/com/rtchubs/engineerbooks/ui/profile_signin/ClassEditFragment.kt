package com.rtchubs.engineerbooks.ui.profile_signin

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter

import androidx.fragment.app.viewModels
import com.rtchubs.engineerbooks.BR
import com.rtchubs.engineerbooks.R
import com.rtchubs.engineerbooks.databinding.ClassEditFragmentBinding
import com.rtchubs.engineerbooks.models.registration.AcademicClass
import com.rtchubs.engineerbooks.ui.common.BaseFragment

class ClassEditFragment: BaseFragment<ClassEditFragmentBinding, ClassEditViewModel>() {

    companion object {
        var selectedClass: AcademicClass? = null
    }

    override val bindingVariable: Int
        get() = BR.viewModel
    override val layoutId: Int
        get() = R.layout.fragment_class_edit

    override val viewModel: ClassEditViewModel by viewModels { viewModelFactory }

    private var titleClassList = arrayOf("--Select Class--")
    lateinit var classAdapter: ArrayAdapter<String>

    //val args: OtpSignInFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateStatusBarBackgroundColor("#1E4356")
        registerToolbar(viewDataBinding.toolbar)

        val tempClass = Array(viewModel.allAcademicClass.value?.size ?: 0 + 1) {""}
        tempClass[0] = "--Select Class--"
        viewModel.allAcademicClass.value?.forEachIndexed { index, academicClass ->
            tempClass[index + 1] = academicClass.name ?: "Unknown"
        }
        titleClassList = tempClass
        classAdapter = ArrayAdapter(requireContext(), R.layout.spinner_item, titleClassList)
        classAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        viewDataBinding.spClass.adapter = classAdapter

        viewDataBinding.spClass.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View,
                position: Int,
                id: Long
            ) {
                if (position > 0) {
                    try {
                        viewModel.allAcademicClass.value?.let {
                            if (it.isNotEmpty()) {
                                selectedClass = it[position - 1]
                                viewDataBinding.btnSubmit.isEnabled = true
                            }
                        }
                    } catch (e: IndexOutOfBoundsException) {
                        e.printStackTrace()
                    }
                } else {
                    selectedClass = null
                    viewDataBinding.btnSubmit.isEnabled = false
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        viewModel.allAcademicClass.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            it?.let {
                val temp = Array(it.size + 1) {""}
                temp[0] = "--Select Class--"
                it.forEachIndexed { index, academicClass ->
                    temp[index + 1] = academicClass.name ?: "Unknown"
                }
                titleClassList = temp
                classAdapter = ArrayAdapter(requireContext(), R.layout.spinner_item, titleClassList)
                classAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                viewDataBinding.spClass.adapter = classAdapter
            }
        })

        viewDataBinding.btnSubmit.setOnClickListener {
            navController.popBackStack()
        }

        viewModel.getAcademicClass()
    }
}