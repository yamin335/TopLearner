package com.engineersapps.eapps.ui.common

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.view.Window
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.engineersapps.eapps.R
import com.engineersapps.eapps.binding.FragmentDataBindingComponent
import com.engineersapps.eapps.databinding.ClassSelectionDialogFragmentBinding
import com.engineersapps.eapps.models.registration.AcademicClass
import com.engineersapps.eapps.util.RecyclerItemDivider
import dagger.android.support.DaggerAppCompatDialogFragment


class ClassSelectionDialogFragment internal constructor(
    private val callback: (AcademicClass) -> Unit
): DaggerAppCompatDialogFragment() {
    private var selectedClass: AcademicClass? = null
    private var classList: List<AcademicClass> = ArrayList()
    private var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent()
    private lateinit var allClassListAdapter: AllClassListAdapter

//    override fun getTheme(): Int {
//        return R.style.DialogFullScreenTheme
//    }

    /** The system calls this only when creating the layout in a dialog. */
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())

        val inflater = requireActivity().layoutInflater
        // Inflate the layout for this fragment
        val binding: ClassSelectionDialogFragmentBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.dialog_fragment_class_selection,
            null,
            false,
            dataBindingComponent
        )

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

        binding.btnCancel.setOnClickListener {
            dismiss()
        }

        builder.setView(binding.root)
//            .setNegativeButton(
//                "cancel"
//            ) { dialog, _ ->
//                dialog.dismiss()
//            }
//            .setPositiveButton(
//                "ok"
//            ) { dialog, _ ->
//
//            }
        val dialog = builder.create()
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        return dialog

//        val dialog = super.onCreateDialog(savedInstanceState)
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
//        return dialog
    }

    fun submitClassData(classList: List<AcademicClass>) {
        this.classList = classList
    }
}