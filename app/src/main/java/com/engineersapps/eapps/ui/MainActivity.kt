package com.engineersapps.eapps.ui

import android.app.Activity
import android.content.*
import android.content.res.Configuration
import android.os.Bundle
import android.view.MenuItem
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import com.engineersapps.eapps.BuildConfig
import com.engineersapps.eapps.R
import com.engineersapps.eapps.databinding.MainActivityBinding
import com.engineersapps.eapps.models.registration.InquiryAccount
import com.engineersapps.eapps.prefs.PreferencesHelper
import com.engineersapps.eapps.util.*
import com.google.android.material.navigation.NavigationView
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.testing.FakeAppUpdateManager
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.ActivityResult
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.android.play.core.tasks.Task
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

interface LoginHandlerCallback {
    fun onLoggedIn()
}

interface LogoutHandlerCallback {
    fun onLoggedOut()
}

interface NavDrawerHandlerCallback {
    fun toggleNavDrawer()
}

interface MyCourseTabSelection {
    fun selectMyCourseTab()
}

interface ShowHideBottomNavCallback {
    fun showOrHideBottomNav(showHide: Boolean)
}

interface ConfigurationChangeCallback {
    fun onNewConfiguration(newConfig: Configuration)
}

private const val APP_UPDATE_REQUEST_CODE = 1669
private const val APP_UPDATE_TYPE_SUPPORTED = AppUpdateType.IMMEDIATE
class MainActivity : DaggerAppCompatActivity(), LogoutHandlerCallback,
    NavDrawerHandlerCallback, ShowHideBottomNavCallback, NavigationHost,
    MyCourseTabSelection, NavigationView.OnNavigationItemSelectedListener {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var preferencesHelper: PreferencesHelper

    private val viewModel: MainActivityViewModel by viewModels {
        // Get the ViewModel.
        viewModelFactory
    }

    lateinit var binding: MainActivityBinding

    private var currentNavController: LiveData<NavController>? = null

    private var currentNavHostFragment: LiveData<NavHostFragment>? = null

    private lateinit var updateListener: InstallStateUpdatedListener

    private val fragmentWithoutBottomNav = setOf(
        R.id.splashFragment,
        R.id.viewPagerFragment,
        R.id.signInFragment,
        R.id.termsAndConditions,
        R.id.otpSignInFragment,
        R.id.pinNumberFragment,
        R.id.profileSignInFragment
    )

    var configurationChangeCallback: ConfigurationChangeCallback? = null

    lateinit var userData: InquiryAccount

    private lateinit var tokenExpireReceiver: BroadcastReceiver

    override fun onResume() {
        super.onResume()
        LocalBroadcastManager.getInstance(this).registerReceiver(
            tokenExpireReceiver,
            IntentFilter(AppConstants.INTENT_TOKEN_EXPIRED)
        )

        checkForAppUpdate()
    }

    override fun onPause() {
        super.onPause()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(tokenExpireReceiver)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        checkForAppUpdate()

        tokenExpireReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                intent?.let {
                    viewModel.loginUser(preferencesHelper.getUser())
                }
            }
        }

        viewModel.loginResponse.observe(this, androidx.lifecycle.Observer {
            it?.let { data ->
                data.Account?.let { account ->
                    if (account.isRegistered == true) {
                        preferencesHelper.accessToken = data.Token?.AccessToken
                        preferencesHelper.accessTokenExpiresIn = data.Token?.AtExpires ?: 0
                        preferencesHelper.isLoggedIn = true
                        preferencesHelper.saveUser(account)
                        showWarningToast(this, "Your session refreshed, please try again!")
                    }
                }
            }
        })

        window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )

        userData = preferencesHelper.getUser()

        binding = DataBindingUtil.setContentView(this@MainActivity, R.layout.activity_main)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        binding.mainContainer.showBottomNav = true
        binding.drawerNavigation.setNavigationItemSelectedListener(this)

        binding.mainContainer.bottomNav.itemIconTintList = null

        binding.navDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)

//        viewModel.userProfileInfo.observe(this, androidx.lifecycle.Observer { userInfo ->
//            userInfo?.let {
//                userInfo.isSubscribed = true
//                if (userInfo.isSubscribed == true) {
//                    preferencesHelper.isDeviceTimeChanged = false
//                    binding.mainContainer.uiBlockerOnTimeChange.visibility = View.GONE
//                }
//                viewModel.userProfileInfo.postValue(null)
//            }
//        })

//        binding.mainContainer.tryAgainUSB.setOnClickListener {
//            val batteryStatus: Intent? = IntentFilter(Intent.ACTION_BATTERY_CHANGED).let { ifilter ->
//                registerReceiver(null, ifilter)
//            }
//            val status: Int = batteryStatus?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1
//
//            if (status != BatteryManager.BATTERY_STATUS_CHARGING) {
//                binding.mainContainer.uiBlockerOnUSBDetection.visibility = View.GONE
//            }
//        }

        // Setup multi-backStack supported bottomNav
        if (savedInstanceState == null) {
            setupBottomNavigationBar()
        } // Else, need to wait for onRestoreInstanceState

        viewModel.allMyCoursesFromDB.observe(this, Observer {
            it?.let { myCourses ->
                if (myCourses.isNotEmpty()) {
                    binding.mainContainer.bottomNav.selectedItemId = R.id.my_course_nav
                }
            }
        })
    }

    private fun checkForAppUpdate() {
        val appUpdateManager : AppUpdateManager
        if (BuildConfig.DEBUG) {
            appUpdateManager = FakeAppUpdateManager(baseContext)
            appUpdateManager.setUpdateAvailable(16)
        } else {
            appUpdateManager = AppUpdateManagerFactory.create(baseContext)
        }

        val appUpdateInfo = appUpdateManager.appUpdateInfo

        appUpdateInfo.addOnSuccessListener {
            handleUpdate(appUpdateManager, appUpdateInfo)
        }
    }

    private fun handleUpdate(manager: AppUpdateManager, info: Task<AppUpdateInfo>) {
        if (APP_UPDATE_TYPE_SUPPORTED == AppUpdateType.IMMEDIATE) {
            handleImmediateUpdate(manager, info)
        } else if (APP_UPDATE_TYPE_SUPPORTED == AppUpdateType.FLEXIBLE) {
            handleFlexibleUpdate(manager, info)
        }
    }

    private fun handleImmediateUpdate(manager: AppUpdateManager, info: Task<AppUpdateInfo>) {
        if ((info.result.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE ||
                    info.result.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS)
            && info.result.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {

            manager.startUpdateFlowForResult(info.result,
                AppUpdateType.IMMEDIATE, this, APP_UPDATE_REQUEST_CODE)
        }

        // Simulates an immediate update
        if (BuildConfig.DEBUG) {
            val fakeAppUpdate = manager as FakeAppUpdateManager
            if (fakeAppUpdate.isImmediateFlowVisible) {
                fakeAppUpdate.userAcceptsUpdate()
                fakeAppUpdate.downloadStarts()
                fakeAppUpdate.downloadCompletes()
                //launchRestartDialog(manager)
            }
        }
    }

    private fun handleFlexibleUpdate(manager: AppUpdateManager, info: Task<AppUpdateInfo>) {
        if (info.result.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS
            && info.result.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
            handleImmediateUpdate(manager, info)
            return
        }

        if ((info.result.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE ||
                    info.result.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS)
            && info.result.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)) {

            //btn_update.visibility = View.VISIBLE
            setUpdateAction(manager, info)
        }
    }

    private fun setUpdateAction(manager: AppUpdateManager, info: Task<AppUpdateInfo>) {
        updateListener = InstallStateUpdatedListener {
            when (it.installStatus()) {
                InstallStatus.FAILED, InstallStatus.UNKNOWN -> {
                    checkForAppUpdate()
                }
                InstallStatus.PENDING -> {
                    //tv_status.text = getString(R.string.info_pending)
                }
                InstallStatus.CANCELED -> {
                    //tv_status.text = getString(R.string.info_canceled)
                }
                InstallStatus.DOWNLOADING -> {
                    //tv_status.text = getString(R.string.info_downloading)
                }
                InstallStatus.DOWNLOADED -> {
                    //tv_status.text = getString(R.string.info_installing)
                    launchRestartDialog(manager)
                }
                InstallStatus.INSTALLING -> {
                    //tv_status.text = getString(R.string.info_installing)
                }
                InstallStatus.INSTALLED -> {
                    //tv_status.text = getString(R.string.info_installed)
                    manager.unregisterListener(updateListener)
                }
                else -> {
                    //tv_status.text = getString(R.string.info_restart)
                }
            }
        }

        manager.registerListener(updateListener)
        manager.startUpdateFlowForResult(info.result, AppUpdateType.FLEXIBLE, this,
            APP_UPDATE_REQUEST_CODE)

        // Simulates a flexible update
        if (BuildConfig.DEBUG) {
            val fakeAppUpdate = manager as FakeAppUpdateManager
            if (fakeAppUpdate.isConfirmationDialogVisible) {
                fakeAppUpdate.userAcceptsUpdate()
                fakeAppUpdate.downloadStarts()
                fakeAppUpdate.downloadCompletes()
                fakeAppUpdate.completeUpdate()
                fakeAppUpdate.installCompletes()
            }
        }
    }

    private fun launchRestartDialog(manager: AppUpdateManager) {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.update_title))
            .setMessage(getString(R.string.update_message))
            .setPositiveButton(getString(R.string.action_restart)) { _, _ ->
                manager.completeUpdate()
            }
            .create().show()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        configurationChangeCallback?.onNewConfiguration(newConfig)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        // Now that BottomNavigationBar has restored its instance state
        // and its selectedItemId, we can proceed with setting up the
        // BottomNavigationBar with Navigation
        setupBottomNavigationBar()
    }

    /**
     * Called on first creation and when restoring state.
     */
    private fun setupBottomNavigationBar() {

        val navGraphIds = listOf(
            R.navigation.home_nav_graph,
            R.navigation.free_book_nav_graph,
            R.navigation.live_nav_graph,
            R.navigation.my_course_nav_graph,
            R.navigation.more_nav_graph
        )

        // Setup the bottom navigation view with a payment_graph of navigation graphs
        val (controller, navHost) = binding.mainContainer.bottomNav.setupWithNavController(
            navGraphIds = navGraphIds,
            fragmentManager = supportFragmentManager,
            containerId = R.id.nav_host_container,
            intent = intent
        )

        // Whenever the selected controller changes, setup the action bar.
        controller.observe(this, Observer { navController ->
//            appBarConfiguration = AppBarConfiguration(
//                navGraph = navController.graph,
//                drawerLayout = drawer_layout
//            )
            // Set up ActionBar
//            setSupportActionBar(toolbar)
//            setupActionBarWithNavController(navController)

            navController.addOnDestinationChangedListener { _, destination, _ ->
                hideKeyboard()
                binding.mainContainer.showBottomNav = destination.id !in fragmentWithoutBottomNav
            }

//            setupActionBarWithNavController(navController)
        })

        currentNavController = controller
        currentNavHostFragment = navHost

        currentNavController?.observe(this, Observer { homeController ->
            if (homeController.graph.startDestination == R.id.home2Fragment && preferencesHelper.shouldClearBackStackOfHomeNav) {
                CoroutineScope(Dispatchers.Main.immediate).launch {
                    delay(700)
                    homeController.popBackStack(
                        homeController.graph.startDestination, false
                    )
                }
                preferencesHelper.shouldClearBackStackOfHomeNav = false
            }
        })
    }

    override fun onBackPressed() {
        /**
         * If the drawer is open, the behavior changes based on the API level.
         * When gesture nav is enabled (Q+), we want back to exit when the drawer is open.
         * When button navigation is enabled (on Q or pre-Q) we want to close the drawer on back.
         */
        if (binding.navDrawer.isDrawerOpen(binding.drawerNavigation) && binding.navDrawer.shouldCloseDrawerFromBackPress()) {
            binding.navDrawer.closeDrawer(GravityCompat.START)
        } else {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    override fun toggleNavDrawer() {
//        if (binding.navDrawer.isDrawerOpen(binding.drawerNavigation)) {
//            binding.navDrawer.closeDrawer(GravityCompat.START)
//        } else {
//            binding.navDrawer.openDrawer(GravityCompat.START)
//        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return currentNavController?.value?.navigateUp() ?: false || super.onSupportNavigateUp()
    }

    override fun onLoggedOut() {
        viewModel.clearAllData().observe(this, Observer {
            if (it) {
                viewModel.onLogOut(preferencesHelper)
                startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                overridePendingTransition(R.anim.slide_in_left, R.anim.fade_out)
                finish()
            }
        })
    }

    override fun registerToolbarWithNavigation(toolbar: Toolbar) {
        setSupportActionBar(toolbar)
        currentNavController?.value?.let {
            setupActionBarWithNavController(it)
        }
    }

    companion object {
        const val REQUEST_APP_UPDATE = 557
        private const val ERROR_DIALOG_REQUEST_CODE = 1

        private const val NAV_ID_NONE = -1

        /** Use external media if it is available, our app's file directory otherwise */
        fun getOutputDirectory(context: Context): File {
            val appContext = context.applicationContext
            val mediaDir = context.externalMediaDirs.firstOrNull()?.let {
                File(it, appContext.resources.getString(R.string.app_name)).apply { mkdirs() } }
            return if (mediaDir != null && mediaDir.exists())
                mediaDir else appContext.filesDir
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.nav_exams -> {
                currentNavController?.value?.let { navController ->
                    when (navController.graph.id) {
                        R.id.home_nav -> {
                            //navController.navigate(Home2FragmentDirections.actionHome2FragmentToExamsNavGraph())
                        }
                    }
                }
            }

            R.id.nav_settings -> {
            }

            R.id.nav_info -> {
            }
        }

        //close navigation drawer
        binding.navDrawer.closeDrawer(GravityCompat.START);
        return true
    }

    override fun showOrHideBottomNav(showHide: Boolean) {
        binding.mainContainer.showBottomNav = showHide
    }

    override fun selectMyCourseTab() {
        binding.mainContainer.bottomNav.selectedItemId = R.id.my_course_nav
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            APP_UPDATE_REQUEST_CODE -> {
                when (resultCode) {
                    Activity.RESULT_OK -> {
                        if (APP_UPDATE_TYPE_SUPPORTED == AppUpdateType.IMMEDIATE) {
                            showSuccessToast(baseContext, getString(R.string.toast_updated))
                        } else {
                            showSuccessToast(baseContext, getString(R.string.toast_started))
                        }
                    }
                    Activity.RESULT_CANCELED -> {
                        showWarningToast(baseContext, getString(R.string.toast_cancelled))
                    }
                    ActivityResult.RESULT_IN_APP_UPDATE_FAILED -> {
                        showErrorToast(baseContext, getString(R.string.toast_failed))
                        // If the update is cancelled or fails,
                        // you can request to start the update again.
                        checkForAppUpdate()
                    }
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}