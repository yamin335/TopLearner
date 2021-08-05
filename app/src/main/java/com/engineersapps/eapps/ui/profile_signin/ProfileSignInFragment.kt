package com.engineersapps.eapps.ui.profile_signin

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.engineersapps.eapps.BR
import com.engineersapps.eapps.R
import com.engineersapps.eapps.api.ApiCallStatus
import com.engineersapps.eapps.databinding.ProfileSignInBinding
import com.engineersapps.eapps.models.registration.AcademicClass
import com.engineersapps.eapps.models.registration.InquiryAccount
import com.engineersapps.eapps.ui.LoginHandlerCallback
import com.engineersapps.eapps.ui.common.BaseFragment
import com.engineersapps.eapps.util.AppConstants.generalUserTypeID
import com.engineersapps.eapps.util.BitmapUtilss
import com.engineersapps.eapps.util.showErrorToast
import com.engineersapps.eapps.util.showSuccessToast
import jp.wasabeef.glide.transformations.RoundedCornersTransformation
import java.io.File
import java.security.SecureRandom
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ProfileSignInFragment : BaseFragment<ProfileSignInBinding, ProfileSignInViewModel>() {

    override val bindingVariable: Int
        get() = BR.viewModel
    override val layoutId: Int
        get() = R.layout.fragment_profile_sign_in

    override val viewModel: ProfileSignInViewModel by viewModels { viewModelFactory }
    private var listener: LoginHandlerCallback? = null

//    private var titleCityList = arrayOf("--Select City--")
//    private var titleUpazillaList = arrayOf("--Select Upazilla--")
    private var titleGenderList = arrayOf("--সিলেক্ট করুন--")
    private var titleClassList = arrayOf("--সিলেক্ট করুন--")

//    lateinit var cityAdapter: ArrayAdapter<String>
//    lateinit var upazillaAdapter: ArrayAdapter<String>
    lateinit var genderAdapter: ArrayAdapter<String>
    lateinit var classAdapter: ArrayAdapter<String>

//    lateinit var profileCameraLauncher: ActivityResultLauncher<Intent>
//    lateinit var nidFrontCameraLauncher: ActivityResultLauncher<Intent>
//    lateinit var nidBackCameraLauncher: ActivityResultLauncher<Intent>

    lateinit var picFromCameraLauncher: ActivityResultLauncher<Intent>
    lateinit var picFromGalleryLauncher: ActivityResultLauncher<Intent>

    //lateinit var imageCropperListener: FaceDetectionListener
    lateinit var currentPhotoPath: String

    lateinit var registrationHelper: InquiryAccount

    var placeholder: BitmapDrawable? = null

    val args: ProfileSignInFragmentArgs by navArgs()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        //ClassEditFragment.selectedClass = null
        DistrictEditFragment.selectedCity = null
        UpazillaEditFragment.selectedUpazilla = null
        if (context is LoginHandlerCallback) {
            listener = context
        } else {
            throw RuntimeException("$context must implement LoginHandlerCallback")
        }
    }

    override fun onDetach() {
        super.onDetach()
        //ClassEditFragment.selectedClass = null
        DistrictEditFragment.selectedCity = null
        UpazillaEditFragment.selectedUpazilla = null
        listener = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivity.window?.setSoftInputMode(
            WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
        )

//        requireActivity().onBackPressedDispatcher.addCallback(this, true) {
//            val tt = 0
//        }
    }

//    override fun onDestroy() {
//        super.onDestroy()
//        requireActivity().window?.setSoftInputMode(
//            WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN
//        )
//    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateStatusBarBackgroundColor("#1E4356")
        registerToolbar(viewDataBinding.toolbar)

        viewModel.apiCallStatus.observe(viewLifecycleOwner, Observer {
            viewDataBinding.btnSubmit.isEnabled = it != ApiCallStatus.LOADING
        })

        registrationHelper = args.registrationHelper
        if (registrationHelper.isRegistered == true) {
            preferencesHelper.isLoggedIn = true
            listener?.onLoggedIn()
        }

        placeholder =
            BitmapUtilss.transformDrawable( // has white background because it's not transparent, so rounding will be visible
                requireContext(),
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.doctor_1
                ),  // transformation to be applied
                RoundedCornersTransformation(
                    128,
                    0,
                    RoundedCornersTransformation.CornerType.ALL
                ),  // size of the target in pixels
                256
            )

        Glide.with(requireContext())
            .load(viewModel.profileBitmap)
            .circleCrop()
            .placeholder(placeholder)
            .into(viewDataBinding.rivProfileImage)

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

//        profileCameraLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
//
//            if (result.resultCode != Activity.RESULT_OK) return@registerForActivityResult
//            val photoUri = result?.data?.data
//            //val file = File(currentPhotoPath)
//            photoUri?.let {
//                val imageBitmap = BitmapUtilss.getBitmapFromContentUri(requireContext().contentResolver, it)
//
//                imageBitmap?.let { bitmap ->
//                    viewModel.profileBitmap = BitmapUtilss.getResizedBitmap(bitmap, 500)
//
//                    Glide.with(requireContext())
//                        .load(viewModel.profileBitmap)
//                        .circleCrop()
//                        .placeholder(placeholder)
//                        .into(viewDataBinding.rivProfileImage)
//                }
//
////                val viola = Viola(imageCropperListener)
////                val faceOption = FaceOptions.Builder().cropAlgorithm(CropAlgorithm.SQUARE)
////                    .enableProminentFaceDetection()
////                    .enableDebug()
////                    .build()
////                val bitmap = imageBitmap ?: return@registerForActivityResult
////                viola.detectFace(bitmap, faceOption)
//            }
////            val file = File(currentPhotoPath)
////            val imageBitmap = BitmapUtilss.getBitmapFromContentUri(requireContext().contentResolver, Uri.fromFile(file))
////
////            val viola = Viola(imageCropperListener)
////            val faceOption = FaceOptions.Builder().cropAlgorithm(CropAlgorithm.SQUARE)
////                    .enableProminentFaceDetection()
////                    .enableDebug()
////                    .build()
////            val bitmap = imageBitmap ?: return@registerForActivityResult
////            viola.detectFace(bitmap, faceOption)
//        }

        picFromCameraLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode != Activity.RESULT_OK) return@registerForActivityResult
            val file = File(currentPhotoPath)
            val imageBitmap = BitmapUtilss.getBitmapFromContentUri(
                requireContext().contentResolver, Uri.fromFile(
                    file
                )
            )
            val bitmap = imageBitmap ?: return@registerForActivityResult

            viewModel.profileBitmap = BitmapUtilss.getResizedBitmap(bitmap, 500)
            Glide.with(requireContext())
                .load(viewModel.profileBitmap)
                .circleCrop()
                .placeholder(placeholder)
                .into(viewDataBinding.rivProfileImage)
        }

        picFromGalleryLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode != Activity.RESULT_OK) return@registerForActivityResult
            val photoUri = result?.data?.data
            photoUri?.let {
                val imageBitmap = BitmapUtilss.getBitmapFromContentUri(
                    requireContext().contentResolver, photoUri
                )
                val bitmap = imageBitmap ?: return@registerForActivityResult

                viewModel.profileBitmap = BitmapUtilss.getResizedBitmap(bitmap, 500)
                Glide.with(requireContext())
                    .load(viewModel.profileBitmap)
                    .circleCrop()
                    .placeholder(placeholder)
                    .into(viewDataBinding.rivProfileImage)
            }
        }

//        nidFrontCameraLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
//            val file = File(currentPhotoPath)
//            val imageBitmap = BitmapUtilss.getBitmapFromContentUri(requireContext().contentResolver, Uri.fromFile(file))
//            val bitmap = imageBitmap ?: return@registerForActivityResult
//            //viewModel.nidFrontBitmap = file
//            viewModel.nidFrontBitmap = BitmapUtilss.getResizedBitmap(bitmap, 500)
//            viewDataBinding.rivNidFrontImage.setImageBitmap(bitmap)
//        }
//
//        nidBackCameraLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
//            val file = File(currentPhotoPath)
//            val imageBitmap = BitmapUtilss.getBitmapFromContentUri(requireContext().contentResolver, Uri.fromFile(file))
//            val bitmap = imageBitmap ?: return@registerForActivityResult
//            //viewModel.nidBackBitmap = file
//            viewModel.nidBackBitmap = BitmapUtilss.getResizedBitmap(bitmap, 500)
//            viewDataBinding.rivNidBackImage.setImageBitmap(bitmap)
//        }

        val tempGender = Array(viewModel.allGender.size + 1) {""}
        tempGender[0] = "--সিলেক্ট করুন--"
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

        var genderIndex = 0
        viewModel.allGender.forEachIndexed { index, gender ->
            if (gender.name?.equals(viewModel.selectedGender?.name ?: "N/A", true) == true) genderIndex = index + 1
        }
        viewDataBinding.spGender.setSelection(genderIndex, true)

//        viewDataBinding.tvClass.text = "--Select Class--"
        viewDataBinding.city.text = "--সিলেক্ট করুন--"
        viewDataBinding.tvUpazilla.text = "--সিলেক্ট করুন--"

//        ClassEditFragment.selectedClass?.let {
//            viewModel.selectedClass = it
//        }

        DistrictEditFragment.selectedCity?.let {
            viewModel.selectedCity = it
            if (UpazillaEditFragment.selectedUpazilla == null) {
                viewModel.selectedUpazilla = null
            }
        }

        UpazillaEditFragment.selectedUpazilla?.let {
            viewModel.selectedUpazilla = it
        }

//        viewDataBinding.tvClass.text = viewModel.selectedClass?.name ?: "--Select Class--"
        viewDataBinding.city.text = viewModel.selectedCity?.name ?: "--সিলেক্ট করুন--"
        viewDataBinding.tvUpazilla.text = viewModel.selectedUpazilla?.name ?: "--সিলেক্ট করুন--"

        viewDataBinding.city.setOnClickListener {
            navigateTo(ProfileSignInFragmentDirections.actionProfileSignInFragmentToDistrictEditFragment1())
        }

        viewDataBinding.tvUpazilla.setOnClickListener {
            val cityId = DistrictEditFragment.selectedCity?.id
            if (cityId == null) {
                showErrorToast(requireContext(), "Please select city first!")
                return@setOnClickListener
            }
            navigateTo(ProfileSignInFragmentDirections.actionProfileSignInFragmentToUpazillaEditFragment1(cityId))
        }

//        viewDataBinding.tvClass.setOnClickListener {
//            navigateTo(ProfileSignInFragmentDirections.actionProfileSignInFragmentToClassEditFragment1())
//        }

//        val tempDistricts = Array(viewModel.allDistricts.value?.size ?: 0 + 1) {""}
//        tempDistricts[0] = "--Select City--"
//        viewModel.allDistricts.value?.forEachIndexed { index, city ->
//            tempDistricts[index + 1] = city.name ?: "Unknown"
//        }
//        titleCityList = tempDistricts
//        cityAdapter = ArrayAdapter(requireContext(), R.layout.spinner_item, titleCityList)
//        cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//        viewDataBinding.spCity.adapter = cityAdapter
//
//        val tempUpazilla = Array(viewModel.allUpazilla.value?.size ?: 0 + 1) {""}
//        tempUpazilla[0] = "--Select Upazilla--"
//        viewModel.allUpazilla.value?.forEachIndexed { index, upazilla ->
//            tempUpazilla[index + 1] = upazilla.name ?: "Unknown"
//        }
//        titleUpazillaList = tempUpazilla
//        upazillaAdapter = ArrayAdapter(requireContext(), R.layout.spinner_item, titleUpazillaList)
//        upazillaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//        viewDataBinding.spUpazilla.adapter = upazillaAdapter

//        if (viewModel.selectedGender == null) {
//            val tempGender = Array(viewModel.allGender.size + 1) {""}
//            tempGender[0] = "--Select Gender--"
//            viewModel.allGender.forEachIndexed { index, gender ->
//                tempGender[index + 1] = gender.name ?: "Unknown"
//            }
//            titleGenderList = tempGender
//            genderAdapter = ArrayAdapter(requireContext(), R.layout.spinner_item, titleGenderList)
//            genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//            viewDataBinding.spGender.adapter = genderAdapter
//        }

        val tempClass = Array(allClass.size + 1) {""}
        tempClass[0] = "--সিলেক্ট করুন--"
        allClass.forEachIndexed { index, academicClass ->
            tempClass[index + 1] = academicClass.name ?: "Unknown"
        }
        titleClassList = tempClass
        classAdapter = ArrayAdapter(requireContext(), R.layout.spinner_item, titleClassList)
        classAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        viewDataBinding.spClass.adapter = classAdapter

//        viewDataBinding.spCity.onItemSelectedListener = object :
//            AdapterView.OnItemSelectedListener {
//            override fun onItemSelected(
//                parent: AdapterView<*>?,
//                view: View,
//                position: Int,
//                id: Long
//            ) {
//                if (position > 0) {
//                    try {
//                        viewModel.allDistricts.value?.let {
//                            if (it.isNotEmpty()) {
//                                viewModel.selectedCity = it[position - 1]
//                                viewModel.getUpazilla(viewModel.selectedCity?.id ?: "0")
//                            }
//                        }
//
//                    } catch (e: IndexOutOfBoundsException) {
//                        e.printStackTrace()
//                    }
//                } else {
//                    viewModel.selectedCity = null
//                }
//            }
//
//            override fun onNothingSelected(parent: AdapterView<*>?) {}
//        }

//        viewDataBinding.spUpazilla.onItemSelectedListener = object :
//            AdapterView.OnItemSelectedListener {
//            override fun onItemSelected(
//                parent: AdapterView<*>?,
//                view: View,
//                position: Int,
//                id: Long
//            ) {
//                if (position > 0) {
//                    try {
//                        viewModel.allUpazilla.value?.let {
//                            if (it.isNotEmpty()) {
//                                viewModel.selectedUpazilla = it[position - 1]
//                            }
//                        }
//                    } catch (e: IndexOutOfBoundsException) {
//                        e.printStackTrace()
//                    }
//                } else {
//                    viewModel.selectedUpazilla = null
//                }
//            }
//
//            override fun onNothingSelected(parent: AdapterView<*>?) {}
//        }

//        viewDataBinding.spGender.onItemSelectedListener = object :
//            AdapterView.OnItemSelectedListener {
//            override fun onItemSelected(
//                parent: AdapterView<*>?,
//                view: View,
//                position: Int,
//                id: Long
//            ) {
//                if (position > 0 && viewModel.allGender.isNotEmpty()) {
//                    try {
//                        viewModel.selectedGender = viewModel.allGender[position - 1]
//                    } catch (e: IndexOutOfBoundsException) {
//                        e.printStackTrace()
//                    }
//                } else {
//                    viewModel.selectedGender = null
//                }
//            }
//
//            override fun onNothingSelected(parent: AdapterView<*>?) {}
//        }

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

        var academicClassIndex = 0
        allClass.forEachIndexed { index, academicClass ->
            if (academicClass.id?.equals(viewModel.selectedClass?.id ?: "0", true) == true) academicClassIndex = index + 1
        }
        viewDataBinding.spClass.setSelection(academicClassIndex, true)

//        val nidFrontData = args.NIDData.frontData
//        val nidBackData = args.NIDData.backData

//        viewDataBinding.nameField.setText(nidFrontData.name)
//        viewDataBinding.birthDayField.setText(nidFrontData.birthDate)
//        viewDataBinding.nidField.setText(nidFrontData.nidNo)
//        viewDataBinding.addressField.setText(nidBackData.birthPlace)

//        viewModel.allDistricts.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
//            it?.let {
//                val temp = Array(it.size + 1) {""}
//                temp[0] = "--Select City--"
//                it.forEachIndexed { index, city ->
//                    temp[index + 1] = city.name ?: "Unknown"
//                }
//                titleCityList = temp
//                cityAdapter = ArrayAdapter(requireContext(), R.layout.spinner_item, titleCityList)
//                cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//                viewDataBinding.spCity.adapter = cityAdapter
//            }
//        })

//        viewModel.allUpazilla.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
//            it?.let {
//                val temp = Array(it.size + 1) {""}
//                temp[0] = "--Select Upazilla--"
//                it.forEachIndexed { index, upazilla ->
//                    temp[index + 1] = upazilla.name ?: "Unknown"
//                }
//                titleUpazillaList = temp
//                upazillaAdapter = ArrayAdapter(requireContext(), R.layout.spinner_item, titleUpazillaList)
//                upazillaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//                viewDataBinding.spUpazilla.adapter = upazillaAdapter
//            }
//        })

        viewModel.allAcademicClass.observe(viewLifecycleOwner, Observer {
            if (allClass.isEmpty()) {
                it?.let {
                    val temp = Array(it.size + 1) {""}
                    temp[0] = "--সিলেক্ট করুন--"
                    it.forEachIndexed { index, academicClass ->
                        temp[index + 1] = academicClass.name ?: "Unknown"
                    }
                    titleClassList = temp
                    classAdapter = ArrayAdapter(requireContext(), R.layout.spinner_item, titleClassList)
                    classAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    viewDataBinding.spClass.adapter = classAdapter
                    allClass = it as ArrayList<AcademicClass>
                }
            }
        })

        viewModel.registrationResponse.observe(viewLifecycleOwner, Observer {
            it?.let { data ->
                data.Account?.let { account ->
                    if (account.isRegistered == true) {
                        preferencesHelper.accessToken = data.Token?.AccessToken
                        preferencesHelper.accessTokenExpiresIn = data.Token?.AtExpires ?: 0
                        preferencesHelper.isLoggedIn = true
                        preferencesHelper.saveUser(account)
                        showSuccessToast(requireContext(), "Registration successful!")
                        listener?.onLoggedIn()
                    }
                }
            }
        })

        viewModel.allImageUrls.observe(viewLifecycleOwner, Observer { data ->
            if (data == null) {
                viewModel.registerNewUser(registrationHelper)
                return@Observer
            }

            registrationHelper.profilePic = data.profilepic
            registrationHelper.nidFrontPic = data.nidfront
            registrationHelper.nidBackPic = data.nidback
            viewModel.registerNewUser(registrationHelper)
        })

        viewDataBinding.btnSubmit.setOnClickListener {
            if (viewDataBinding.firstName.text.toString().isEmpty()) {
                viewDataBinding.firstName.requestFocus()
                showErrorToast(requireContext(), "Please enter first name!")
                return@setOnClickListener
            }
            registrationHelper.first_name = viewDataBinding.firstName.text.toString()

//            if (viewDataBinding.lastName.text.toString().isEmpty()) {
//                viewDataBinding.lastName.requestFocus()
//                showErrorToast(requireContext(), "Please enter last name!")
//                return@setOnClickListener
//            }
//            registrationHelper.last_name = viewDataBinding.lastName.text.toString()

            registrationHelper.last_name = viewDataBinding.firstName.text.toString()
                if (viewDataBinding.fatherName.text.toString().isEmpty()) {
                viewDataBinding.fatherName.requestFocus()
                showErrorToast(requireContext(), "Please enter father's name!")
                return@setOnClickListener
            }
            registrationHelper.altContactPerson = viewDataBinding.fatherName.text.toString()

//            if (viewDataBinding.birthDayField.text.toString().isEmpty()) {
//                viewDataBinding.birthDayField.requestFocus()
//                showErrorToast(requireContext(), "Please enter birth date!")
//                return@setOnClickListener
//            }
//
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
//            registrationHelper.nidnumber = viewDataBinding.nidField.text.toString()

//            if (viewDataBinding.emailField.text.toString().isEmpty()) {
//                viewDataBinding.emailField.requestFocus()
//                showErrorToast(requireContext(), "Please enter email address!")
//                return@setOnClickListener
//            }
            registrationHelper.email = viewDataBinding.emailField.text.toString()

//            if (viewDataBinding.addressField.text.toString().isEmpty()) {
//                viewDataBinding.addressField.requestFocus()
//                showErrorToast(requireContext(), "Please enter your address!")
//                return@setOnClickListener
//            }
            registrationHelper.address = viewDataBinding.addressField.text.toString()

            if (viewModel.selectedGender == null) {
                viewDataBinding.spGender.requestFocus()
                showErrorToast(requireContext(), "Please select your gender!")
                return@setOnClickListener
            }
            registrationHelper.gender = viewModel.selectedGender?.name

            if (viewModel.selectedCity == null) {
                showErrorToast(requireContext(), "Please select your city!")
                return@setOnClickListener
            }
            registrationHelper.city = viewModel.selectedCity?.name
            registrationHelper.CityID = viewModel.selectedCity?.id?.toInt()

            if (viewModel.selectedUpazilla == null) {
                showErrorToast(requireContext(), "Please select your upazilla!")
                return@setOnClickListener
            }
            registrationHelper.upazila = viewModel.selectedUpazilla?.name
            registrationHelper.UpazilaID = viewModel.selectedUpazilla?.id?.toInt()

            if (viewModel.selectedClass == null) {
                showErrorToast(requireContext(), "Please select your class!")
                return@setOnClickListener
            }
            registrationHelper.class_id = viewModel.selectedClass?.id?.toInt()
            registrationHelper.ClassName = viewModel.selectedClass?.name

            registrationHelper.institute = viewDataBinding.instituteField.text.toString()
            registrationHelper.rollnumber = viewDataBinding.rollField.text.toString()

            registrationHelper.customer_type_id = generalUserTypeID
            registrationHelper.isRegistered = true
            val selectionTime: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(
                Date()
            )
            val random = "${1 + SecureRandom().nextInt(9999999)}"
            val folder = "media_folder_${selectionTime}_$random"
            registrationHelper.Folder = folder

            if (viewModel.profileBitmap != null || viewModel.nidFrontBitmap != null || viewModel.nidBackBitmap != null) {
                viewModel.uploadProfileImagesToServer(registrationHelper.mobile ?: "", registrationHelper.Folder ?: "")
            } else {
                viewModel.registerNewUser(registrationHelper)
            }

            //viewModel.uploadProfileImagesToServer()
//            print("Model: ${Build.MODEL} -- ID: ${Build.ID} -- Manufacturer: ${Build.MANUFACTURER}")
//            val helper = args.registrationHelper
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

//        viewDataBinding.rivNidFrontImage.setOnClickListener {
//            takeNIDFrontImageFromCamera()
//        }
//
//        viewDataBinding.rivNidBackImage.setOnClickListener {
//            takeNIDBackImageFromCamera()
//        }

        viewDataBinding.rivProfileImage.setOnClickListener {
            //takeProfileImage()
        }

        //viewModel.getDistricts()
        if (allClass.isEmpty()) {
            viewModel.getAcademicClass()
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

//    private fun takeProfileImage() {
//        if (PermissionUtils.isCameraAndGalleryPermissionGranted(requireActivity())) {
//            selectImage()
//        }
//    }
//
//    private fun selectImage() {
//        val items = arrayOf<CharSequence>(
//            getString(R.string.take_picture), getString(R.string.choose_from_gallery),
//            getString(R.string.cancel)
//        )
//        val builder = AlertDialog.Builder(mContext)
//        builder.setTitle(getString(R.string.choose_an_option))
//        builder.setItems(items) { dialog: DialogInterface, item: Int ->
//            if (items[item] == getString(R.string.take_picture)) {
//                if (PermissionUtils.isCameraPermission(requireActivity())) {
//                    captureImageFromCamera()
//                }
//            } else if (items[item] == getString(R.string.choose_from_gallery)) {
//                if (PermissionUtils.isGalleryPermission(requireActivity())) {
//                    chooseImageFromGallery()
//                }
//            } else if (items[item] == getString(R.string.cancel)) {
//                dialog.dismiss()
//            }
//        }
//        builder.show()
//    }
//
//    private fun captureImageFromCamera() {
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
//                    val flags: Int = takePictureIntent.flags
//                    // check that the nested intent does not grant URI permissions
//                    if (flags and Intent.FLAG_GRANT_READ_URI_PERMISSION == 0 &&
//                        flags and Intent.FLAG_GRANT_WRITE_URI_PERMISSION == 0
//                    ) {
//                        // redirect the nested Intent
//                        picFromCameraLauncher.launch(takePictureIntent)
//                    }
//                }
//            }
//        }
//    }
//
//    private fun chooseImageFromGallery() {
//        val galleryIntent = Intent(
//            Intent.ACTION_PICK
//        )
//        galleryIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
//        galleryIntent.resolveActivity(requireActivity().packageManager)?.let {
//            val flags: Int = galleryIntent.flags
//            // check that the nested intent does not grant URI permissions
//            if (flags and Intent.FLAG_GRANT_READ_URI_PERMISSION == 0 &&
//                flags and Intent.FLAG_GRANT_WRITE_URI_PERMISSION == 0
//            ) {
//                // redirect the nested Intent
//                picFromGalleryLauncher.launch(galleryIntent)
//            }
//        }
//    }
//
//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<String?>,
//        grantResults: IntArray
//    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        if (requestCode == PERMISSION_REQUEST_CODE && grantResults.isNotEmpty()) {
//            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                if (PermissionUtils.isCameraAndGalleryPermissionGranted(requireActivity())) {
//                    selectImage()
//                }
//            }
//        }
//    }

//    private fun takeNIDFrontImageFromCamera() {
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
//                    nidFrontCameraLauncher.launch(takePictureIntent)
//                }
//            }
//        }
//    }

//    private fun takeNIDBackImageFromCamera() {
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
//                    nidBackCameraLauncher.launch(takePictureIntent)
//                }
//            }
//        }
//    }

//    @Throws(IOException::class)
//    private fun createImageFile(): File {
//        // Create an image file name
//        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(Date())
//        val storageDir: File? = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
//        return File.createTempFile(
//            "JPEG_${timeStamp}_", /* prefix */
//            ".jpg", /* suffix */
//            storageDir /* directory */
//        ).apply {
//            // Save a file: path for use with ACTION_VIEW intents
//            currentPhotoPath = absolutePath
//        }
//    }

//    fun getFace(context: Context, data: Bitmap): Bitmap? {
//        try {
//            var bitmap = data
//            if (bitmap.width > bitmap.height) {
//                val matrix = Matrix()
//                matrix.postRotate(270f)
//                if (bitmap.width > 1500) matrix.postScale(0.5f, 0.5f)
//                bitmap = Bitmap.createBitmap(
//                    bitmap,
//                    0,
//                    0,
//                    bitmap.width,
//                    bitmap.height,
//                    matrix,
//                    true
//                )
//            }
//            val faceDetector = FaceDetector.Builder(context).setProminentFaceOnly(true).setTrackingEnabled(
//                false
//            ).build()
//            val frame = Frame.Builder().setBitmap(bitmap).build()
//            val faces = faceDetector.detect(frame)
//            var results: Bitmap? = null
//            for (i in 0 until faces.size()) {
//                val thisFace = faces.valueAt(i)
//                val x = thisFace.position.x
//                val y = thisFace.position.y
//                val x2 = x / 4 + thisFace.width
//                val y2 = y / 4 + thisFace.height
//                results = Bitmap.createBitmap(bitmap, x.toInt(), y.toInt(), x2.toInt(), y2.toInt())
//            }
//            return results
//        } catch (e: Exception) {
//            Log.e("GET_FACE", e.message)
//        }
//        return null
//    }

    companion object {
        var allClass = ArrayList<AcademicClass>()
    }

}