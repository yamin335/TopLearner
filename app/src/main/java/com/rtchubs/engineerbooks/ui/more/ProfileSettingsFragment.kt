package com.rtchubs.engineerbooks.ui.more

import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.google.android.gms.vision.Frame
import com.google.android.gms.vision.face.FaceDetector
import com.rtchubs.engineerbooks.BR
import com.rtchubs.engineerbooks.BuildConfig
import com.rtchubs.engineerbooks.R
import com.rtchubs.engineerbooks.api.ApiEndPoint.PROFILE_IMAGES
import com.rtchubs.engineerbooks.camerax.CameraXActivity
import com.rtchubs.engineerbooks.databinding.ProfileSettingsFragmentBinding
import com.rtchubs.engineerbooks.models.registration.AcademicClass
import com.rtchubs.engineerbooks.models.registration.InquiryAccount
import com.rtchubs.engineerbooks.ui.common.BaseFragment
import com.rtchubs.engineerbooks.ui.profile_signin.*
import com.rtchubs.engineerbooks.util.BitmapUtilss
import com.rtchubs.engineerbooks.util.BitmapUtilss.transformDrawable
import com.rtchubs.engineerbooks.util.showErrorToast
import com.rtchubs.engineerbooks.util.showSuccessToast
import jp.wasabeef.glide.transformations.RoundedCornersTransformation
import kotlinx.android.synthetic.main.fragment_profile_settings.*
import kotlinx.coroutines.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class ProfileSettingsFragment : BaseFragment<ProfileSettingsFragmentBinding, ProfileSettingsViewModel>() {

    override val bindingVariable: Int
        get() = BR.viewModel
    override val layoutId: Int
        get() = R.layout.fragment_profile_settings

    override val viewModel: ProfileSettingsViewModel by viewModels { viewModelFactory }

    private var titleClassList = arrayOf("--Select Class--")
    private var titleGenderList = arrayOf("--Select Gender--")

    lateinit var genderAdapter: ArrayAdapter<String>
    lateinit var classAdapter: ArrayAdapter<String>

    lateinit var profileCameraLauncher: ActivityResultLauncher<Intent>
    lateinit var nidFrontCameraLauncher: ActivityResultLauncher<Intent>
    lateinit var nidBackCameraLauncher: ActivityResultLauncher<Intent>

    //lateinit var imageCropperListener: FaceDetectionListener
    lateinit var currentPhotoPath: String

    lateinit var userData: InquiryAccount
    var placeholder: BitmapDrawable? = null

    companion object {
        private var allClass = ArrayList<AcademicClass>()
    }

    override fun onDestroy() {
        super.onDestroy()
        ClassEditFragment.selectedClass = null
        DistrictEditFragment.selectedCity = null
        UpazillaEditFragment.selectedUpazilla = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateStatusBarBackgroundColor("#1E4356")
        registerToolbar(viewDataBinding.toolbar)

        userData = preferencesHelper.getUser()

        placeholder =
            transformDrawable( // has white background because it's not transparent, so rounding will be visible
                requireContext(),
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.doctor_1
                ),  // transformation to be applied
                RoundedCornersTransformation(128, 0, RoundedCornersTransformation.CornerType.ALL),  // size of the target in pixels
                256
            )

        Glide.with(requireContext())
            .load(placeholder)
            .into(viewDataBinding.rivProfileImage)

        viewDataBinding.firstName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.firstName = s?.toString() ?: ""
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })

        viewDataBinding.lastName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.lastName = s?.toString() ?: ""
            }

            override fun afterTextChanged(s: Editable?) {

            }

        })

        viewDataBinding.fatherName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.fatherName = s?.toString() ?: ""
            }

            override fun afterTextChanged(s: Editable?) {

            }

        })

        viewDataBinding.instituteField.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.institute = s?.toString() ?: ""
            }

            override fun afterTextChanged(s: Editable?) {

            }

        })

        viewDataBinding.rollField.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.roll = s?.toString() ?: ""
            }

            override fun afterTextChanged(s: Editable?) {

            }

        })

        viewDataBinding.emailField.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.email = s?.toString() ?: ""
            }

            override fun afterTextChanged(s: Editable?) {

            }

        })

        viewDataBinding.addressField.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.address = s?.toString() ?: ""
            }

            override fun afterTextChanged(s: Editable?) {

            }

        })

//        imageCropperListener = object : FaceDetectionListener {
//            override fun onFaceDetected(result: com.darwin.viola.still.model.Result) {
//                val faceList = result.facePortraits
//                if (faceList.isNotEmpty()) {
//                    //viewModel.profileBitmap = faceList[0].face
//                    viewModel.profileBitmap = BitmapUtilss.getResizedBitmap(faceList[0].face, 500)
//                    Glide.with(requireContext()).load(faceList[0].face).circleCrop().placeholder(R.drawable.doctor_1).into(
//                        viewDataBinding.rivProfileImage
//                    )
//                    //viewDataBinding.rivProfileImage.setImageBitmap()
//                }
//            }
//
//            override fun onFaceDetectionFailed(error: FaceDetectionError, message: String) {
//                showErrorToast(requireContext(), message)
//            }
//        }

        profileCameraLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->

            if (result.resultCode != RESULT_OK) return@registerForActivityResult
            val photoUri = result?.data?.data
            //val file = File(currentPhotoPath)
            photoUri?.let {
                val imageBitmap = BitmapUtilss.getBitmapFromContentUri(
                    requireContext().contentResolver,
                    it
                )

                imageBitmap?.let { bitmap ->
                    viewModel.profileBitmap = BitmapUtilss.getResizedBitmap(bitmap, 500)

                    Glide.with(requireContext())
                        .load(viewModel.profileBitmap)
                        .circleCrop()
                        .placeholder(placeholder)
                        .into(viewDataBinding.rivProfileImage)
                }

//                val viola = Viola(imageCropperListener)
//                val faceOption = FaceOptions.Builder().cropAlgorithm(CropAlgorithm.SQUARE)
//                    .enableProminentFaceDetection()
//                    .enableDebug()
//                    .build()
//                val bitmap = imageBitmap ?: return@registerForActivityResult
//                viola.detectFace(bitmap, faceOption)
            }
        }

        nidFrontCameraLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val file = File(currentPhotoPath)
            val imageBitmap = BitmapUtilss.getBitmapFromContentUri(
                requireContext().contentResolver, Uri.fromFile(
                    file
                )
            )
            val bitmap = imageBitmap ?: return@registerForActivityResult
            //viewModel.nidFrontBitmap = file
            viewModel.nidFrontBitmap = BitmapUtilss.getResizedBitmap(bitmap, 500)
            viewDataBinding.rivNidFrontImage.setImageBitmap(bitmap)
        }

        nidBackCameraLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val file = File(currentPhotoPath)
            val imageBitmap = BitmapUtilss.getBitmapFromContentUri(
                requireContext().contentResolver, Uri.fromFile(
                    file
                )
            )
            val bitmap = imageBitmap ?: return@registerForActivityResult
            //viewModel.nidBackBitmap = file
            viewModel.nidBackBitmap = BitmapUtilss.getResizedBitmap(bitmap, 500)
            viewDataBinding.rivNidBackImage.setImageBitmap(bitmap)
        }

        val tempGender = Array(viewModel.allGender.size + 1) {""}
        tempGender[0] = "--Select Gender--"
        viewModel.allGender.forEachIndexed { index, gender ->
            tempGender[index + 1] = gender.name ?: "Unknown"
        }
        titleGenderList = tempGender
        genderAdapter = ArrayAdapter(requireContext(), R.layout.spinner_item, titleGenderList)
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        viewDataBinding.spGender.adapter = genderAdapter

        viewDataBinding.spGender.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View,
                position: Int,
                id: Long
            ) {
                if (position > 0 && viewModel.allGender.isNotEmpty()) {
                    try {
                        viewModel.selectedGender = viewModel.allGender[position - 1]
                    } catch (e: IndexOutOfBoundsException) {
                        e.printStackTrace()
                    }
                } else {
                    viewModel.selectedGender = null
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        val tempClass = Array(allClass.size + 1) {""}
        tempClass[0] = "--Select Class--"
        allClass.forEachIndexed { index, academicClass ->
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
                        if (allClass.isNotEmpty()) {
                            viewModel.selectedClass = allClass[position - 1]
                        }
                    } catch (e: IndexOutOfBoundsException) {
                        e.printStackTrace()
                    }
                } else {
                    viewModel.selectedClass = null
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        prepareClassData(userData)

//        val nidFrontData = args.NIDData.frontData
//        val nidBackData = args.NIDData.backData

//        viewDataBinding.nameField.setText(nidFrontData.name)
//        viewDataBinding.birthDayField.setText(nidFrontData.birthDate)
//        viewDataBinding.nidField.setText(nidFrontData.nidNo)
//        viewDataBinding.addressField.setText(nidBackData.birthPlace)

        viewModel.profileUpdateResponse.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            it?.let { data ->
                data.Account?.let { account ->
                    userData = account
                    preferencesHelper.saveUser(account)
                    prepareUserData(userData)
                    showSuccessToast(requireContext(), "Successfully profile updated.")
                    preferencesHelper.shouldClearBackStackOfHomeNav = true
                    viewModel.profileUpdateResponse.postValue(null)
                }
            }
//            if (it == null) {
//                showErrorToast(requireContext(), "Couldn't update profile at this moment!")
//            }
        })

        viewModel.allAcademicClass.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            if (allClass.isEmpty()) {
                it?.let {
                    val temp = Array(it.size + 1) { "" }
                    temp[0] = "--Select Class--"
                    it.forEachIndexed { index, academicClass ->
                        temp[index + 1] = academicClass.name ?: "Unknown"
                    }
                    titleClassList = temp
                    classAdapter = ArrayAdapter(
                        requireContext(),
                        R.layout.spinner_item,
                        titleClassList
                    )
                    classAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    viewDataBinding.spClass.adapter = classAdapter
                    allClass = it as ArrayList<AcademicClass>

                    prepareClassData(userData)
                }
            }
        })

        viewModel.allImageUrls.observe(viewLifecycleOwner, androidx.lifecycle.Observer { data ->
            if (data == null) {
                viewModel.updateUserProfile(userData)
                return@Observer
            }

            data.profilepic?.let {
                if (it.isNotEmpty()) userData.profilePic = it
            }
            data.nidfront?.let {
                if (it.isNotEmpty()) userData.nidFrontPic = it
            }
            data.nidback?.let {
                if (it.isNotEmpty()) userData.nidBackPic = it
            }

            viewModel.updateUserProfile(userData)
        })

        viewDataBinding.city.setOnClickListener {
            navigateTo(ProfileSettingsFragmentDirections.actionProfileSettingsFragmentToDistrictEditFragment())
        }

        viewDataBinding.tvUpazilla.setOnClickListener {
            var cityId = DistrictEditFragment.selectedCity?.id
            if (cityId == null) {
                if (userData.CityID?.toString() == null) {
                    showErrorToast(requireContext(), "Please select city first!")
                    return@setOnClickListener
                }
                cityId = userData.CityID?.toString() ?: return@setOnClickListener
            }
            navigateTo(
                ProfileSettingsFragmentDirections.actionProfileSettingsFragmentToUpazillaEditFragment(
                    cityId
                )
            )
        }

//        viewDataBinding.tvClass.setOnClickListener {
//            navigateTo(ProfileSettingsFragmentDirections.actionProfileSettingsFragmentToClassEditFragment())
//        }

        viewModel.userProfileInfo.observe(
            viewLifecycleOwner,
            androidx.lifecycle.Observer { userInfo ->
                userInfo?.let {
                    userData = it
                    preferencesHelper.saveUser(it)
                    prepareUserData(it)
                }

                if (userInfo == null) {
                    prepareUserData(userData)
                }
            })

        viewDataBinding.btnSubmit.setOnClickListener {
            if (viewDataBinding.firstName.text.toString().isEmpty()) {
                viewDataBinding.firstName.requestFocus()
                showErrorToast(requireContext(), "Please enter first name!")
                return@setOnClickListener
            }
            userData.firstName = viewDataBinding.firstName.text.toString()

            if (viewDataBinding.lastName.text.toString().isEmpty()) {
                viewDataBinding.lastName.requestFocus()
                showErrorToast(requireContext(), "Please enter last name!")
                return@setOnClickListener
            }
            userData.lastName = viewDataBinding.lastName.text.toString()

            if (viewDataBinding.fatherName.text.toString().isEmpty()) {
                viewDataBinding.fatherName.requestFocus()
                showErrorToast(requireContext(), "Please enter father's name!")
                return@setOnClickListener
            }
            userData.altContactPerson = viewDataBinding.fatherName.text.toString()
//            if (viewDataBinding.birthDayField.text.toString().isEmpty()) {
//                viewDataBinding.birthDayField.requestFocus()
//                showErrorToast(requireContext(), "Please enter birth date!")
//                return@setOnClickListener
//            }
//            if (viewDataBinding.nidField.text.toString().isEmpty()) {
//                viewDataBinding.nidField.requestFocus()
//                showErrorToast(requireContext(), "Please enter NID number!")
//                return@setOnClickListener
//            }
//            if (viewDataBinding.nidField.text.toString().length < 10) {
//                viewDataBinding.nidField.requestFocus()
//                showErrorToast(requireContext(), "Please enter valid NID number!")
//                return@setOnClickListener
//            }
//            userData.nidnumber = viewDataBinding.nidField.text.toString()
//            if (viewDataBinding.emailField.text.toString().isEmpty()) {
//                viewDataBinding.emailField.requestFocus()
//                showErrorToast(requireContext(), "Please enter email address!")
//                return@setOnClickListener
//            }
//            userData.email = viewDataBinding.emailField.text.toString()
//            if (viewDataBinding.addressField.text.toString().isEmpty()) {
//                viewDataBinding.addressField.requestFocus()
//                showErrorToast(requireContext(), "Please enter your address!")
//                return@setOnClickListener
//            }
            if (viewModel.selectedGender == null) {
                viewDataBinding.spGender.requestFocus()
                showErrorToast(requireContext(), "Please select your gender!")
                return@setOnClickListener
            }
            userData.gender = viewModel.selectedGender?.name

            viewModel.selectedClass?.let {
                userData.class_id = it.id?.toInt()
                userData.ClassName = it.name
            }


            if (DistrictEditFragment.selectedCity != null && UpazillaEditFragment.selectedUpazilla == null) {
                showErrorToast(requireContext(), "Please select your upazilla!")
                return@setOnClickListener
            }
            //userData.city = viewModel.selectedCity?.name

//            ClassEditFragment.selectedClass?.let {
//                userData.class_id = it.id?.toInt() ?: 0
//                userData.ClassName = it.name
//            }

            DistrictEditFragment.selectedCity?.let {
                userData.CityID = it.id?.toInt() ?: 0
                userData.city = it.name
            }

            UpazillaEditFragment.selectedUpazilla?.let {
                userData.UpazilaID = it.id?.toInt() ?: 0
                userData.upazila = it.name
            }

            userData.email = viewDataBinding.emailField.text.toString()
            userData.address = viewDataBinding.addressField.text.toString()
            userData.institute = viewDataBinding.instituteField.text.toString()
            userData.rollnumber = viewDataBinding.rollField.text.toString()
//            userData.upazila = viewModel.selectedUpazilla?.name
//            if (viewModel.selectedClass == null) {
//                viewDataBinding.spClass.requestFocus()
//                showErrorToast(requireContext(), "Please select your class!")
//                return@setOnClickListener
//            }

            if (viewModel.profileBitmap != null || viewModel.nidFrontBitmap != null || viewModel.nidBackBitmap != null) {
                viewModel.uploadProfileImagesToServer(userData.mobile ?: "", userData.Folder ?: "")
            } else {
                viewModel.updateUserProfile(userData)
            }

            //viewModel.uploadProfileImagesToServer()
//            print("Model: ${Build.MODEL} -- ID: ${Build.ID} -- Manufacturer: ${Build.MANUFACTURER}")
//            val helper = args.userData
//            val name = viewDataBinding.nameField.text.toString()
//
//            // For test only
//            val inputStream: InputStream = mContext.assets.open("grace_hopper.jpg")
//            val bitmap: Bitmap = BitmapFactory.decodeStream(inputStream)
//            val userImage = BitmapUtilss.fileFromBitmap(bitmap, mContext).asFilePart("UserImage")
//            viewModel.registerNewUser(helper.mobile, helper.otp,
//                helper.pinNumber, name, helper.operator, Build.ID,
//                Build.MANUFACTURER, Build.MODEL, nidFrontData.nidNo, null, null, userImage)
//                .observe(viewLifecycleOwner, androidx.lifecycle.Observer {response ->
//
//                response?.let {
//                    when {
//                        it.isSuccess == true -> {
//                            preferencesHelper.isRegistered = true
//                            preferencesHelper.isTermsAccepted = true
//                            preferencesHelper.pinNumber = helper.pinNumber
//                            preferencesHelper.mobileNo = helper.mobile
//                            preferencesHelper.operator = helper.operator
//                            preferencesHelper.deviceId = Build.ID
//                            preferencesHelper.deviceName = Build.MANUFACTURER
//                            preferencesHelper.deviceModel = Build.MODEL
//
//                            showSuccessToast(mContext, registrationSuccessMessage)
//                            navController.navigate(ProfileSignInFragmentDirections.actionProfileSignInFragmentToSignInFragment())
//                        }
//                        it.isSuccess == false && it.errorMessage != null -> {
//                            showWarningToast(mContext, it.errorMessage)
//                        }
//                        else -> {
//                            showWarningToast(mContext, commonErrorMessage)
//                        }
//                    }
//                }
//            })
        }

        viewDataBinding.rivNidFrontImage.setOnClickListener {
            takeNIDFrontImageFromCamera()
        }

        viewDataBinding.rivNidBackImage.setOnClickListener {
            takeNIDBackImageFromCamera()
        }

        viewDataBinding.rivProfileImage.setOnClickListener {
            takeProfileImageFromCamera()
        }

        if (allClass.isEmpty()) {
            viewModel.getAcademicClass()
        }

        if (checkNetworkStatus()) {
            viewModel.getUserProfileInfo(userData.mobile ?: "")
        } else {
            prepareUserData(userData)
        }
    }

    private fun prepareUserData(user: InquiryAccount) {

        if (viewModel.profileBitmap == null) {
            Glide.with(requireContext())
                .load("${PROFILE_IMAGES}/${user.Folder}/${user.profilePic}")
                .circleCrop()
                .placeholder(placeholder)
                .into(viewDataBinding.rivProfileImage)
        } else {
            Glide.with(requireContext())
                .load(viewModel.profileBitmap)
                .circleCrop()
                .placeholder(placeholder)
                .into(viewDataBinding.rivProfileImage)
        }

        Glide.with(requireContext())
            .load("${PROFILE_IMAGES}/${user.Folder}/${user.nidFrontPic}")
            .placeholder(R.drawable.doctor_1)
            .into(viewDataBinding.rivNidFrontImage)

        Glide.with(requireContext())
            .load("${PROFILE_IMAGES}/${user.Folder}/${user.nidBackPic}")
            .placeholder(R.drawable.doctor_1)
            .into(viewDataBinding.rivNidBackImage)

        if (viewModel.firstName.isEmpty()) {
            viewDataBinding.firstName.setText(user.firstName)
        } else {
            viewDataBinding.firstName.setText(viewModel.firstName)
        }

        if (viewModel.lastName.isEmpty()) {
            viewDataBinding.lastName.setText(user.lastName)
        } else {
            viewDataBinding.lastName.setText(viewModel.lastName)
        }

        if (viewModel.fatherName.isEmpty()) {
            viewDataBinding.fatherName.setText(user.altContactPerson)
        } else {
            viewDataBinding.fatherName.setText(viewModel.fatherName)
        }

        if (viewModel.institute.isEmpty()) {
            viewDataBinding.instituteField.setText(user.institute)
        } else {
            viewDataBinding.instituteField.setText(viewModel.institute)
        }

        if (viewModel.roll.isEmpty()) {
            viewDataBinding.rollField.setText(user.rollnumber)
        } else {
            viewDataBinding.rollField.setText(viewModel.roll)
        }

        if (viewModel.email.isEmpty()) {
            viewDataBinding.emailField.setText(user.email)
        } else {
            viewDataBinding.emailField.setText(viewModel.email)
        }

        if (viewModel.address.isEmpty()) {
            viewDataBinding.addressField.setText(user.address)
        } else {
            viewDataBinding.addressField.setText(viewModel.address)
        }

        viewDataBinding.city.text = user.city
        //viewDataBinding.tvClass.text = user.ClassName
        viewDataBinding.tvUpazilla.text = user.upazila

        //viewDataBinding.nidField.setText(user.nidnumber)

        if (viewModel.selectedGender == null) {
            var genderIndex = 0
            viewModel.allGender.forEachIndexed { index, gender ->
                if (gender.name?.equals(user.gender ?: "N/A", true) == true) genderIndex = index + 1
            }
            viewDataBinding.spGender.setSelection(genderIndex, true)
        } else {
            var genderIndex = 0
            viewModel.allGender.forEachIndexed { index, gender ->
                if (gender.name?.equals(viewModel.selectedGender?.name ?: "N/A", true) == true) genderIndex = index + 1
            }
            viewDataBinding.spGender.setSelection(genderIndex, true)
        }

        prepareClassData(user)

//        ClassEditFragment.selectedClass?.name?.let {
//            viewDataBinding.tvClass.text = it
//        }

        DistrictEditFragment.selectedCity?.name?.let {
            viewDataBinding.city.text = it
            if (UpazillaEditFragment.selectedUpazilla == null) {
                viewDataBinding.tvUpazilla.text = getString(R.string.select_upazilla)
            }
        }

        UpazillaEditFragment.selectedUpazilla?.name?.let {
            viewDataBinding.tvUpazilla.text = it
        }

//        var districtIndex = 0
//        viewModel.allDistricts.value?.forEachIndexed { index, district ->
//            if (district.name?.equals(user.city ?: "N/A", true) == true) districtIndex = index + 1
//        }
//        viewDataBinding.spCity.setSelection(districtIndex, true)

//        runBlocking {
//
//        }
    }

    private fun prepareClassData(user: InquiryAccount) {
        if (viewModel.selectedClass == null) {
            var academicClassIndex = 0
            allClass.forEachIndexed { index, academicClass ->
                if (academicClass.id?.equals(user.class_id?.toString() ?: "0", true) == true) academicClassIndex = index + 1
            }
            viewDataBinding.spClass.setSelection(academicClassIndex, true)
        } else {
            var academicClassIndex = 0
            allClass.forEachIndexed { index, academicClass ->
                if (academicClass.id?.equals(viewModel.selectedClass?.id ?: "0", true) == true) academicClassIndex = index + 1
            }
            viewDataBinding.spClass.setSelection(academicClassIndex, true)
        }
    }

//    private fun takeProfileImageFromCamera() {
//        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//        try {
//            cameraLauncher.launch(takePictureIntent)
//        } catch (e: ActivityNotFoundException) {
//            // display error state to the user
//        }
//    }

    private fun takeProfileImageFromCamera() {
        profileCameraLauncher.launch(Intent(requireContext(), CameraXActivity::class.java))
//        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
//            // Ensure that there's a camera activity to handle the intent
//            takePictureIntent.resolveActivity(requireActivity().packageManager)?.also {
//                // Create the File where the photo should go
//                val photoFile: File? = try {
//                    createImageFile()
//                } catch (ex: IOException) {
//                    // Error occurred while creating the File
//                    ex.printStackTrace()
//                    null
//                }
//                // Continue only if the File was successfully created
//                photoFile?.also {
//                    val photoURI: Uri = FileProvider.getUriForFile(
//                        requireContext(),
//                        "${BuildConfig.APPLICATION_ID}.provider",
//                        it
//                    )
//                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
//                    profileCameraLauncher.launch(takePictureIntent)
//                }
//            }
//        }
    }

    private fun takeNIDFrontImageFromCamera() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(requireActivity().packageManager)?.also {
                // Create the File where the photo should go
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    // Error occurred while creating the File
                    ex.printStackTrace()
                    null
                }
                // Continue only if the File was successfully created
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        requireContext(),
                        "${BuildConfig.APPLICATION_ID}.provider",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    nidFrontCameraLauncher.launch(takePictureIntent)
                }
            }
        }
    }

    private fun takeNIDBackImageFromCamera() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(requireActivity().packageManager)?.also {
                // Create the File where the photo should go
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    // Error occurred while creating the File
                    ex.printStackTrace()
                    null
                }
                // Continue only if the File was successfully created
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        requireContext(),
                        "${BuildConfig.APPLICATION_ID}.provider",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    nidBackCameraLauncher.launch(takePictureIntent)
                }
            }
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(Date())
        val storageDir: File? = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }
    }

    fun getFace(context: Context, data: Bitmap): Bitmap? {
        try {
            var bitmap = data
            if (bitmap.width > bitmap.height) {
                val matrix = Matrix()
                matrix.postRotate(270f)
                if (bitmap.width > 1500) matrix.postScale(0.5f, 0.5f)
                bitmap = Bitmap.createBitmap(
                    bitmap,
                    0,
                    0,
                    bitmap.width,
                    bitmap.height,
                    matrix,
                    true
                )
            }
            val faceDetector = FaceDetector.Builder(context).setProminentFaceOnly(true).setTrackingEnabled(
                false
            ).build()
            val frame = Frame.Builder().setBitmap(bitmap).build()
            val faces = faceDetector.detect(frame)
            var results: Bitmap? = null
            for (i in 0 until faces.size()) {
                val thisFace = faces.valueAt(i)
                val x = thisFace.position.x
                val y = thisFace.position.y
                val x2 = x / 4 + thisFace.width
                val y2 = y / 4 + thisFace.height
                results = Bitmap.createBitmap(bitmap, x.toInt(), y.toInt(), x2.toInt(), y2.toInt())
            }
            return results
        } catch (e: Exception) {
            Log.e("GET_FACE", e.message)
        }
        return null
    }

}