package com.rtchubs.engineerbooks.ui.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.rtchubs.engineerbooks.BR
import com.rtchubs.engineerbooks.R
import com.rtchubs.engineerbooks.databinding.QuizListFragmentBinding
import com.rtchubs.engineerbooks.ui.common.BaseFragment
import com.rtchubs.engineerbooks.ui.video_play.LoadWebViewFragment

class QuizListFragment : BaseFragment<QuizListFragmentBinding, QuizListViewModel>() {

    override val bindingVariable: Int
        get() = BR.viewModel
    override val layoutId: Int
        get() = R.layout.fragment_quiz_list

    override val viewModel: QuizListViewModel by viewModels { viewModelFactory }

    //lateinit var quizListAdapter: QuizListAdapter

    @SuppressLint("SetJavaScriptEnabled", "ObsoleteSdkInt")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val webSettings = viewDataBinding.webView.settings
        webSettings.javaScriptEnabled = true
        webSettings.allowContentAccess = true
        webSettings.domStorageEnabled = true
        webSettings.setAppCacheEnabled(true)

        try {
            val chapterFields = LoadWebViewFragment.chapter.fields?.filter { it.type == "MCQ" }
            val solution = chapterFields?.first()?.link

            viewDataBinding.webView.loadUrl(solution)
        } catch (e: Exception) {
            e.printStackTrace()
        }

//        quizListAdapter = QuizListAdapter(
//            appExecutors
//        ) { item ->
//
//        }
//
//        viewDataBinding.rvQuizList.adapter = quizListAdapter
//
//        val list = ArrayList<QuizItem>()
//
//        var i = 1
//        while (i <= 10) {
//            var j = 1
//            val answerList = ArrayList<QuizAnswerItem>()
//            val answers = arrayOf("England", "America", "Bangladesh", "South Africa", "Germany", "Australia", "India")
//            var serial = "a."
//            while (j <= 5) {
//                answerList.add(QuizAnswerItem(j, serial, answers[j], answers[j] == "Bangladesh"))
//                when(serial) {
//                    "a." -> serial = "b."
//                    "b." -> serial = "c."
//                    "c." -> serial = "d."
//                    "d." -> serial = "e."
//                    "e." -> serial = "f."
//                    "f." -> serial = "g."
//                }
//                j++
//            }
//            list.add(
//                QuizItem(i, "$i.", "Wich country fought for their mother language in 1952.", answerList)
//            )
//            i++
//        }
//
//        quizListAdapter.submitList(list)
//
//        viewDataBinding.btnSubmit.setOnClickListener {
//
//        }
    }
}