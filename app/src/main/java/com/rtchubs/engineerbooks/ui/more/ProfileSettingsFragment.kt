package com.rtchubs.engineerbooks.ui.more

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Matrix
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.darwin.viola.still.FaceDetectionListener
import com.darwin.viola.still.Viola
import com.darwin.viola.still.model.CropAlgorithm
import com.darwin.viola.still.model.FaceDetectionError
import com.darwin.viola.still.model.FaceOptions
import com.google.android.gms.vision.Frame
import com.google.android.gms.vision.face.FaceDetector
import com.rtchubs.engineerbooks.BR
import com.rtchubs.engineerbooks.BuildConfig
import com.rtchubs.engineerbooks.R
import com.rtchubs.engineerbooks.databinding.ProfileSettingsFragmentBinding
import com.rtchubs.engineerbooks.models.registration.InquiryAccount
import com.rtchubs.engineerbooks.ui.common.BaseFragment
import com.rtchubs.engineerbooks.ui.profile_signin.ProfileSignInViewModel
import com.rtchubs.engineerbooks.util.BitmapUtilss
import com.rtchubs.engineerbooks.util.showErrorToast
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class ProfileSettingsFragment : BaseFragment<ProfileSettingsFragmentBinding, ProfileSignInViewModel>() {


    override val bindingVariable: Int
        get() = BR.viewModel
    override val layoutId: Int
        get() = R.layout.fragment_profile_settings

    override val viewModel: ProfileSignInViewModel by viewModels { viewModelFactory }

    private var titleCityList = arrayOf("--Select City--")
    private var titleUpazillaList = arrayOf("--Select Upazilla--")
    private var titleGenderList = arrayOf("--Select Gender--")
    private var titleClassList = arrayOf("--Select Class--")

    lateinit var cityAdapter: ArrayAdapter<String>
    lateinit var upazillaAdapter: ArrayAdapter<String>
    lateinit var genderAdapter: ArrayAdapter<String>
    lateinit var classAdapter: ArrayAdapter<String>

    lateinit var profileCameraLauncher: ActivityResultLauncher<Intent>
    lateinit var nidFrontCameraLauncher: ActivityResultLauncher<Intent>
    lateinit var nidBackCameraLauncher: ActivityResultLauncher<Intent>

    lateinit var imageCropperListener: FaceDetectionListener
    lateinit var currentPhotoPath: String

    lateinit var userData: InquiryAccount

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateStatusBarBackgroundColor("#1E4356")
        registerToolbar(viewDataBinding.toolbar)

        userData = preferencesHelper.getUser()

        prepareUserData(userData)

        Glide.with(requireContext()).load(R.drawable.doctor_1).circleCrop().into(viewDataBinding.rivProfileImage)

        imageCropperListener = object : FaceDetectionListener {
            override fun onFaceDetected(result: com.darwin.viola.still.model.Result) {
                val faceList = result.facePortraits
                if (faceList.isNotEmpty()) {
                    //viewModel.profileBitmap = faceList[0].face
                    viewModel.profileBitmap = BitmapUtilss.getResizedBitmap(faceList[0].face, 500)
                    Glide.with(requireContext()).load(faceList[0].face).circleCrop().placeholder(R.drawable.doctor_1).into(
                        viewDataBinding.rivProfileImage
                    )
                    //viewDataBinding.rivProfileImage.setImageBitmap()
                }
            }

            override fun onFaceDetectionFailed(error: FaceDetectionError, message: String) {
                showErrorToast(requireContext(), message)
            }
        }

        profileCameraLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val file = File(currentPhotoPath)
            val imageBitmap = BitmapUtilss.getBitmapFromContentUri(requireContext().contentResolver, Uri.fromFile(file))

            val viola = Viola(imageCropperListener)
            val faceOption = FaceOptions.Builder().cropAlgorithm(CropAlgorithm.SQUARE)
                .enableProminentFaceDetection()
                .enableDebug()
                .build()
            val bitmap = imageBitmap ?: return@registerForActivityResult
            viola.detectFace(bitmap, faceOption)
        }

        nidFrontCameraLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val file = File(currentPhotoPath)
            val imageBitmap = BitmapUtilss.getBitmapFromContentUri(requireContext().contentResolver, Uri.fromFile(file))
            val bitmap = imageBitmap ?: return@registerForActivityResult
            //viewModel.nidFrontBitmap = file
            viewModel.nidFrontBitmap = BitmapUtilss.getResizedBitmap(bitmap, 500)
            viewDataBinding.rivNidFrontImage.setImageBitmap(bitmap)
        }

        nidBackCameraLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val file = File(currentPhotoPath)
            val imageBitmap = BitmapUtilss.getBitmapFromContentUri(requireContext().contentResolver, Uri.fromFile(file))
            val bitmap = imageBitmap ?: return@registerForActivityResult
            //viewModel.nidBackBitmap = file
            viewModel.nidBackBitmap = BitmapUtilss.getResizedBitmap(bitmap, 500)
            viewDataBinding.rivNidBackImage.setImageBitmap(bitmap)
        }

        val tempDistricts = Array(viewModel.allDistricts.value?.size ?: 0 + 1) {""}
        tempDistricts[0] = "--Select City--"
        viewModel.allDistricts.value?.forEachIndexed { index, city ->
            tempDistricts[index + 1] = city.name ?: "Unknown"
        }
        titleCityList = tempDistricts
        cityAdapter = ArrayAdapter(requireContext(), R.layout.spinner_item, titleCityList)
        cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        viewDataBinding.spCity.adapter = cityAdapter

        val tempUpazilla = Array(viewModel.allUpazilla.value?.size ?: 0 + 1) {""}
        tempUpazilla[0] = "--Select Upazilla--"
        viewModel.allUpazilla.value?.forEachIndexed { index, upazilla ->
            tempUpazilla[index + 1] = upazilla.name ?: "Unknown"
        }
        titleUpazillaList = tempUpazilla
        upazillaAdapter = ArrayAdapter(requireContext(), R.layout.spinner_item, titleUpazillaList)
        upazillaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        viewDataBinding.spUpazilla.adapter = upazillaAdapter

        val tempGender = Array(viewModel.allGender.size + 1) {""}
        tempGender[0] = "--Select Gender--"
        viewModel.allGender.forEachIndexed { index, gender ->
            tempGender[index + 1] = gender.name ?: "Unknown"
        }
        titleGenderList = tempGender
        genderAdapter = ArrayAdapter(requireContext(), R.layout.spinner_item, titleGenderList)
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        viewDataBinding.spGender.adapter = genderAdapter

        val tempClass = Array(viewModel.allAcademicClass.value?.size ?: 0 + 1) {""}
        tempClass[0] = "--Select Class--"
        viewModel.allAcademicClass.value?.forEachIndexed { index, academicClass ->
            tempClass[index + 1] = academicClass.name ?: "Unknown"
        }
        titleClassList = tempClass
        classAdapter = ArrayAdapter(requireContext(), R.layout.spinner_item, titleClassList)
        classAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        viewDataBinding.spClass.adapter = classAdapter

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
                                viewModel.selectedCity = it[position - 1]
                                viewModel.getUpazilla(viewModel.selectedCity?.id ?: "0")
                            }
                        }

                    } catch (e: IndexOutOfBoundsException) {
                        e.printStackTrace()
                    }
                } else {
                    viewModel.selectedCity = null
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

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
                                viewModel.selectedUpazilla = it[position - 1]
                            }
                        }
                    } catch (e: IndexOutOfBoundsException) {
                        e.printStackTrace()
                    }
                } else {
                    viewModel.selectedUpazilla = null
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

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
                                viewModel.selectedClass = it[position - 1]
                            }
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

//        val nidFrontData = args.NIDData.frontData
//        val nidBackData = args.NIDData.backData

//        viewDataBinding.nameField.setText(nidFrontData.name)
//        viewDataBinding.birthDayField.setText(nidFrontData.birthDate)
//        viewDataBinding.nidField.setText(nidFrontData.nidNo)
//        viewDataBinding.addressField.setText(nidBackData.birthPlace)

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

        viewModel.profileUpdateResponse.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            it?.let { data ->
                data.Account?.let { account ->
                    preferencesHelper.saveUser(account)
                }
                navController.popBackStack()
            }
        })

        viewModel.allImageUrls.observe(viewLifecycleOwner, androidx.lifecycle.Observer { data ->
            if (data == null) {
                viewModel.updateUserProfile(userData, preferencesHelper.accessToken ?: "")
                return@Observer
            }

            userData.profilePic = data.profilepic
            userData.nidFrontPic = data.nidfront
            userData.nidBackPic = data.nidback
            viewModel.updateUserProfile(userData, preferencesHelper.accessToken ?: "")
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
            if (viewDataBinding.birthDayField.text.toString().isEmpty()) {
                viewDataBinding.birthDayField.requestFocus()
                showErrorToast(requireContext(), "Please enter birth date!")
                return@setOnClickListener
            }
            if (viewDataBinding.nidField.text.toString().isEmpty()) {
                viewDataBinding.nidField.requestFocus()
                showErrorToast(requireContext(), "Please enter NID number!")
                return@setOnClickListener
            }
            if (viewDataBinding.nidField.text.toString().length < 10) {
                viewDataBinding.nidField.requestFocus()
                showErrorToast(requireContext(), "Please enter valid NID number!")
                return@setOnClickListener
            }
            userData.nidnumber = viewDataBinding.nidField.text.toString()
            if (viewDataBinding.emailField.text.toString().isEmpty()) {
                viewDataBinding.emailField.requestFocus()
                showErrorToast(requireContext(), "Please enter email address!")
                return@setOnClickListener
            }
            userData.email = viewDataBinding.emailField.text.toString()
            if (viewDataBinding.addressField.text.toString().isEmpty()) {
                viewDataBinding.addressField.requestFocus()
                showErrorToast(requireContext(), "Please enter your address!")
                return@setOnClickListener
            }
            if (viewModel.selectedGender == null) {
                viewDataBinding.spGender.requestFocus()
                showErrorToast(requireContext(), "Please select your gender!")
                return@setOnClickListener
            }
            userData.gender = viewModel.selectedGender?.name
            if (viewModel.selectedCity == null) {
                viewDataBinding.spCity.requestFocus()
                showErrorToast(requireContext(), "Please select your city!")
                return@setOnClickListener
            }
            userData.city = viewModel.selectedCity?.name
            if (viewModel.selectedUpazilla == null) {
                viewDataBinding.spUpazilla.requestFocus()
                showErrorToast(requireContext(), "Please select your upazilla!")
                return@setOnClickListener
            }
            userData.upazila = viewModel.selectedUpazilla?.name
            if (viewModel.selectedClass == null) {
                viewDataBinding.spClass.requestFocus()
                showErrorToast(requireContext(), "Please select your class!")
                return@setOnClickListener
            }

            if (viewModel.profileBitmap != null || viewModel.nidFrontBitmap != null || viewModel.nidBackBitmap != null) {
                viewModel.uploadProfileImagesToServer()
            } else {
                viewModel.updateUserProfile(userData, preferencesHelper.accessToken ?: "")
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

        viewModel.getDistricts()
        viewModel.getAcademicClass()
    }

    fun prepareUserData(user: InquiryAccount) {
        viewDataBinding.firstName.setText(user.firstName)
        viewDataBinding.lastName.setText(user.lastName)
        viewDataBinding.fatherName.setText(user.altContactPerson)
        viewDataBinding.emailField.setText(user.email)
        viewDataBinding.nidField.setText(user.nidnumber)
        viewDataBinding.addressField.setText(user.address1)
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
                    profileCameraLauncher.launch(takePictureIntent)
                }
            }
        }
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