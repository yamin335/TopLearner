package com.rtchubs.engineerbooks.ui.live_video

import android.Manifest
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.WindowManager
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import com.rtchubs.engineerbooks.BR
import com.rtchubs.engineerbooks.R
import com.rtchubs.engineerbooks.api.TokenInformation
import com.rtchubs.engineerbooks.databinding.LiveVideoFragmentBinding
import com.rtchubs.engineerbooks.databinding.PinNumberBinding
import com.rtchubs.engineerbooks.models.registration.InquiryAccount
import com.rtchubs.engineerbooks.ui.LoginHandlerCallback
import com.rtchubs.engineerbooks.ui.NavDrawerHandlerCallback
import com.rtchubs.engineerbooks.ui.common.BaseFragment
import com.rtchubs.engineerbooks.util.hideKeyboard
import com.rtchubs.engineerbooks.util.showErrorToast
import com.rtchubs.engineerbooks.util.showWarningToast
import org.json.JSONObject

class LiveVideoFragment : BaseFragment<LiveVideoFragmentBinding, LiveVideoViewModel>() {

    override val bindingVariable: Int
        get() = BR.viewModel
    override val layoutId: Int
        get() = R.layout.fragment_live_video

    override val viewModel: LiveVideoViewModel by viewModels { viewModelFactory }

    private var drawerListener: NavDrawerHandlerCallback? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (context is NavDrawerHandlerCallback) {
            drawerListener = context
        } else {
            throw RuntimeException("$context must implement LoginHandlerCallback")
        }
    }

    override fun onDetach() {
        super.onDetach()
        drawerListener = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewDataBinding.appLogo.setOnClickListener {
            drawerListener?.toggleNavDrawer()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.toolbar_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

}