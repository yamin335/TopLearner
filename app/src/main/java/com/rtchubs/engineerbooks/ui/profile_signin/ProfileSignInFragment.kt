package com.rtchubs.engineerbooks.ui.profile_signin

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.content.FileProvider
import androidx.fragment.app.viewModels
import com.rtchubs.engineerbooks.BR
import com.rtchubs.engineerbooks.R
import com.rtchubs.engineerbooks.databinding.ProfileSignInBinding
import com.rtchubs.engineerbooks.models.registration.City
import com.rtchubs.engineerbooks.models.registration.Gender
import com.rtchubs.engineerbooks.models.registration.Upazilla
import com.rtchubs.engineerbooks.ui.LoginHandlerCallback
import com.rtchubs.engineerbooks.ui.common.BaseFragment
import timber.log.Timber
import java.io.File
import java.io.IOException
import java.util.*

private const val REQUEST_TAKE_PHOTO_NID_FRONT = 1

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
            override fun onItemSelected(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
                if (position != 0) {
                    try {
                        val upazillaList = viewModel.getUpazzilla(viewModel.cities[position - 1].id)
                        val tempUpazilla = Array(upazillaList.size + 1) {""}
                        tempUpazilla[0] = "--Select Upazilla--"
                        upazillaList.forEachIndexed { index, upazilla ->
                            tempUpazilla[index + 1] = upazilla.name ?: "Unknown"
                        }
                        titleUpazillaList = tempUpazilla

                        upazillaAdapter = ArrayAdapter(requireContext(), R.layout.spinner_item, titleUpazillaList)
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
            override fun onItemSelected(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
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
            override fun onItemSelected(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
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
    }

    private fun dispatchTakePictureIntent(captureFor: String) {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(mContext.packageManager)?.also {
                // Create the File where the photo should go
                val photoFile: File? = try {
                    createImageFile(captureFor)
                } catch (ex: IOException) {
                    // Error occurred while creating the File
                    null
                }
                // Continue only if the File was successfully created
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        mContext,
                        "com.rtchubs.engineerbooks.fileprovider",
                        it
                    )
                    Timber.d("received file uri : $photoURI")

                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO_NID_FRONT)
                }
            }
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(captureFor: String): File {
        // Create an image file name
        // val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val timeStamp: String = Date().time.toString()
        val storageDir: File = mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!

        Timber.d("file directory : $storageDir")

        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            if (captureFor == "rivNidFrontImage") {
                rivNidFrontCaptureImage = absolutePath
            } else if (captureFor == "rivNidBackImage") {
                rivNidBackCaptureImage = absolutePath
            }
            //galleryAddPic(absolutePath)
        }
    }

    // need this when file is saved in external storage public directory
   /* private fun galleryAddPic(absolutePath: String) {
        Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE).also { mediaScanIntent ->
            val f = File(absolutePath)
            mediaScanIntent.data = Uri.fromFile(f)
            cntx.sendBroadcast(mediaScanIntent)
        }
    }*/

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_TAKE_PHOTO_NID_FRONT && resultCode == Activity.RESULT_OK) {


            //Intent data is returning empty

            /*data?.data?.let { uri ->
                viewDataBinding.rivNidFrontImage.setImageURI(uri)
            } ?: run {
                Timber.e("uri is null")
            }*/

           /* val imageBitmap = data?.extras?.get("data") as? Bitmap
            viewDataBinding.rivNidFrontImage.setImageBitmap(imageBitmap)*/


        }
    }

}