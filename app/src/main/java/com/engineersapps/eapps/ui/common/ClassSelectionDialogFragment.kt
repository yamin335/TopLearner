package com.engineersapps.eapps.ui.common

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.engineersapps.eapps.R
import com.engineersapps.eapps.binding.FragmentDataBindingComponent
import com.engineersapps.eapps.databinding.ClassSelectionDialogFragmentBinding
import com.engineersapps.eapps.models.registration.AcademicClass
import com.engineersapps.eapps.util.RecyclerItemDivider
import com.engineersapps.eapps.util.autoCleared
import dagger.android.support.DaggerDialogFragment

class ClassSelectionDialogFragment internal constructor(
    private val callback: (AcademicClass) -> Unit
): DaggerDialogFragment() {
    private var selectedClass: AcademicClass? = null
    private var classList: List<AcademicClass> = ArrayList()
    private var binding by autoCleared<ClassSelectionDialogFragmentBinding>()
    private var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent()
    private lateinit var allClassListAdapter: AllClassListAdapter

    override fun getTheme(): Int {
        return R.style.DialogFullScreenTheme
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.dialog_fragment_class_selection,
            container,
            false,
            dataBindingComponent
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        allClassListAdapter = AllClassListAdapter {
            selectedClass = it
        }
        binding.classRecycler.addItemDecoration(RecyclerItemDivider(requireContext(), LinearLayoutManager.VERTICAL))
        binding.classRecycler.adapter = allClassListAdapter
        allClassListAdapter.submitList(classList)

        binding.btnSelectClass.setOnClickListener {
            selectedClass?.let { value ->
                binding.progressView.visibility = View.VISIBLE
                callback(value)
            }
        }
    }

    fun submitClassData(classList: List<AcademicClass>) {
        this.classList = classList
    }
}