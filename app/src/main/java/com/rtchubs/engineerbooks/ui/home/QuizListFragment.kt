package com.rtchubs.engineerbooks.ui.home

import androidx.fragment.app.viewModels
import com.rtchubs.engineerbooks.BR
import com.rtchubs.engineerbooks.R
import com.rtchubs.engineerbooks.databinding.QuizListFragmentBinding
import com.rtchubs.engineerbooks.ui.common.BaseFragment

class QuizListFragment : BaseFragment<QuizListFragmentBinding, QuizListViewModel>() {

    override val bindingVariable: Int
        get() = BR.viewModel
    override val layoutId: Int
        get() = R.layout.fragment_quiz_list

    override val viewModel: QuizListViewModel by viewModels { viewModelFactory }
}