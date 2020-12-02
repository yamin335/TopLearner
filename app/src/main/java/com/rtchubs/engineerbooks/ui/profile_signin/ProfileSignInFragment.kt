package com.rtchubs.engineerbooks.ui.profile_signin

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Matrix
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.viewModels
import com.bumptech.glide.GenericTransitionOptions.with
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
import com.rtchubs.engineerbooks.databinding.ProfileSignInBinding
import com.rtchubs.engineerbooks.models.registration.City
import com.rtchubs.engineerbooks.models.registration.Gender
import com.rtchubs.engineerbooks.models.registration.Upazilla
import com.rtchubs.engineerbooks.ui.LoginHandlerCallback
import com.rtchubs.engineerbooks.ui.common.BaseFragment
import com.rtchubs.engineerbooks.util.BitmapUtilss
import com.squareup.picasso.Picasso
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

private const val REQUEST_TAKE_PHOTO_NID_FRONT = 1
private const val REQUEST_IMAGE_CAPTURE = 0x12345

class ProfileSignInFragment : BaseFragment<ProfileSignInBinding, ProfileSignInViewModel>() {


    override val bindingVariable: Int
        get() = BR.viewModel
    override val layoutId: Int
        get() = R.layout.fragment_profile_sign_in

    override val viewModel: ProfileSignInViewModel by viewModels { viewModelFactory }

    var rivNidFrontCaptureImage: String = ""
    var rivNidBackCaptureImage: String = ""

//    val args: ProfileSignInFragmentArgs by navArgs()

    private var listener: LoginHandlerCallback? = null

    private val cityList = ArrayList<City>()
    private var titleCityList = arrayOf("--Select City--")

    private val upazillaList = ArrayList<Upazilla>()
    private var titleUpazillaList = arrayOf("--Select Upazilla--")

    private val genderList = ArrayList<Gender>()
    private var titleGenderList = arrayOf("--Select Gender--")

    lateinit var cityAdapter: ArrayAdapter<String>
    lateinit var upazillaAdapter: ArrayAdapter<String>
    lateinit var genderAdapter: ArrayAdapter<String>

    lateinit var cameraLauncher: ActivityResultLauncher<Intent>
    lateinit var imageCropperListener: FaceDetectionListener
    lateinit var currentPhotoPath: String

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is LoginHandlerCallback) {
            listener = context
        } else {
            throw RuntimeException("$context must implement LoginHandlerCallback")
        }
    }

    override fun onDetach() {
        super.onDetach()
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

        Glide.with(requireContext()).load(R.drawable.doctor_1).circleCrop().into(viewDataBinding.rivProfileImage)

        imageCropperListener = object : FaceDetectionListener {
            override fun onFaceDetected(result: com.darwin.viola.still.model.Result) {
                val faceList = result.facePortraits
                if (faceList.isNotEmpty()) {
                    Glide.with(requireContext()).load(faceList[0].face).circleCrop().placeholder(R.drawable.doctor_1).into(
                        viewDataBinding.rivProfileImage
                    )
                    //viewDataBinding.rivProfileImage.setImageBitmap()
                }
            }

            override fun onFaceDetectionFailed(error: FaceDetectionError, message: String) {
                val errorDescription = error.message
                val tt = error.name
            }
        }

        cameraLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
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

        val temp = Array(viewModel.cities.size + 1) {""}
        temp[0] = "--Select City--"
        viewModel.cities.forEachIndexed { index, city ->
            temp[index + 1] = city.name ?: "Unknown"
        }
        titleCityList = temp

        cityAdapter = ArrayAdapter(requireContext(), R.layout.spinner_item, titleCityList)
        cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        viewDataBinding.spCity.adapter = cityAdapter

        viewDataBinding.spCity.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View,
                position: Int,
                id: Long
            ) {
                if (position != 0) {
                    try {
                        val upazillaList = viewModel.getUpazzilla(viewModel.cities[position - 1].id)
                        val tempUpazilla = Array(upazillaList.size + 1) {""}
                        tempUpazilla[0] = "--Select Upazilla--"
                        upazillaList.forEachIndexed { index, upazilla ->
                            tempUpazilla[index + 1] = upazilla.name ?: "Unknown"
                        }
                        titleUpazillaList = tempUpazilla

                        upazillaAdapter = ArrayAdapter(
                            requireContext(),
                            R.layout.spinner_item,
                            titleUpazillaList
                        )
                        upazillaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        viewDataBinding.spUpazilla.adapter = upazillaAdapter
                    } catch (e: IndexOutOfBoundsException) {

                    }
                } else {
                    //viewModel.selectedTicketCategory.value = null
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        upazillaAdapter = ArrayAdapter(requireContext(), R.layout.spinner_item, titleUpazillaList)
        upazillaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        viewDataBinding.spUpazilla.adapter = upazillaAdapter

        viewDataBinding.spUpazilla.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View,
                position: Int,
                id: Long
            ) {
                if (position != 0) {
                    try {
                        //viewModel.selectedTicketCategory.value = ticketCategoryList[position - 1]
                    } catch (e: IndexOutOfBoundsException) {

                    }
                } else {
                    //viewModel.selectedTicketCategory.value = null
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
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
                if (position != 0) {
                    try {
                        //viewModel.selectedTicketCategory.value = ticketCategoryList[position - 1]
                    } catch (e: IndexOutOfBoundsException) {

                    }
                } else {
                    //viewModel.selectedTicketCategory.value = null
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        viewDataBinding.firstName.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewDataBinding.btnSubmit.isEnabled = s.toString().length > 3
            }

        })

//        val nidFrontData = args.NIDData.frontData
//        val nidBackData = args.NIDData.backData

//        viewDataBinding.nameField.setText(nidFrontData.name)
//        viewDataBinding.birthDayField.setText(nidFrontData.birthDate)
//        viewDataBinding.nidField.setText(nidFrontData.nidNo)
//        viewDataBinding.addressField.setText(nidBackData.birthPlace)

        viewDataBinding.btnSubmit.setOnClickListener {
            preferencesHelper.isLoggedIn = true
            listener?.onLoggedIn()
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

        viewDataBinding.rivNidFrontImage.setOnClickListener {
            //dispatchTakePictureIntent("rivNidFrontImage")
        }

        viewDataBinding.rivNidBackImage.setOnClickListener {
            //dispatchTakePictureIntent("rivNidBackImage")
        }

        viewDataBinding.rivProfileImage.setOnClickListener {
            takeProfileImageFromCamera()
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
                    cameraLauncher.launch(takePictureIntent)
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