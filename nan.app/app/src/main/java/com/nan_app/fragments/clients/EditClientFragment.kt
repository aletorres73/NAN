package com.nan_app.fragments.clients

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.nan_app.R
import com.nan_app.entities.Clients
import java.util.Currency

class EditClientFragment : Fragment() {

    private lateinit var v: View

    private lateinit var viewModel: EditClientViewModel

    companion object {
        private val REQUEST_GALLERY = 1001
        private val REQUEST_CAMERA = 1002

    }

    private lateinit var editName: EditText
    private lateinit var editLastName: EditText
    private lateinit var editBirthday: EditText
    private lateinit var editPhone: EditText
    private lateinit var editEmail: EditText
    private lateinit var editPayDay: EditText
    private lateinit var editFinishDay: EditText
    private lateinit var editAmount: EditText
    private lateinit var imageClient: ImageView

    private lateinit var btnUpdateClient: Button
    private lateinit var btnEditImage: Button
    private lateinit var btnDeleteImage: Button

    private var currentClient: Clients = Clients()

    val imageDefect =
        "https://png.pngtree.com/png-clipart/20230824/original/pngtree-upload-users-user-arrow-tray-picture-image_8325109.png"

    //la imagen por defecto quizá debería estar cargada en el drive.
    private var imageUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        v = inflater.inflate(R.layout.fragment_edit_client, container, false)

        viewModel = ViewModelProvider(this)[EditClientViewModel::class.java]

        inflateView(v)
        loadImage(imageDefect)

        currentClient = viewModel.getClient()
        loadClientInfo(currentClient)

        if (currentClient.ImageUri != "")
            if(currentClient.ImageUri != "null")
                loadImage(currentClient.ImageUri)
        else
            loadImage(imageDefect)

        return v
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onStart() {
        super.onStart()

        viewModel.loadState("init")

        viewModel.viewState.observe(viewLifecycleOwner) { state ->
            when (state) {

                EditClientViewModel.STATE_INIT -> {
                    btnUpdateClient.setOnClickListener {
                        viewModel.updatedClient(getEditedClient(currentClient), currentClient.id)
                    }

                    btnDeleteImage.setOnClickListener {
                        if (currentClient.ImageUri == "") {
                            viewModel.loadState("emptyImage")
                        } else
                            viewModel.loadState("deleteImage")
                    }
                    btnEditImage.setOnClickListener {
                        if(currentClient.ImageName == "")
                                showOptionsDialog()
                        if(currentClient.ImageName == "null")
                                showOptionsDialog()
                        else
                            viewModel.loadState("init")
                    }
                    editBirthday.setOnClickListener {
                        viewModel.loadState("selectBirthday")
                    }
                    editPayDay.setOnClickListener {
                        viewModel.loadState("selectDayPay")
                    }
                    editFinishDay.setOnClickListener {
                        viewModel.loadState("selectFinishDay")
                    }

                }

                EditClientViewModel.STATE_ERROR_UPDATE_CLIENT -> {
                    showToast("No se pudieron actualizar los datos")
                    viewModel.loadState("init")
                }

                EditClientViewModel.STATE_DONE_UPDATE_CLIENT -> {
                    Toast.makeText(requireContext(), "Datos actualizados", Toast.LENGTH_SHORT)
                        .show()
                    val action = EditClientFragmentDirections.actionEditClientFragmentToHomeFragment()
                    findNavController().navigate(action)
                    viewModel.loadState("init")
                }

                EditClientViewModel.STATE_DELETE_IMAGE -> {
                    if (currentClient.ImageUri == "") {
                        viewModel.loadState("emptyImage")
                        viewModel.loadState("init")

                    } else {
                        viewModel.deleteImage(currentClient.ImageName)
                        currentClient.ImageName = ""
                        viewModel.loadState("init")
                    }
                }

                EditClientViewModel.STATE_DONE_IMAGE_DELETE -> {
                    currentClient.ImageUri = ""
                    currentClient.ImageName = ""
                    loadImage(imageDefect)
                    showToast("Imagen borrada")

                    viewModel.updateClientByImage(currentClient, currentClient.id)

                    viewModel.loadState("init")
                }

                EditClientViewModel.STATE_ERROR_IMAGE_DELETE -> {
                    showToast("Error al borrar la imagen")
                    viewModel.loadState("init")
                }

                EditClientViewModel.STATE_IMAGE_EMPTY -> {
                    showToast("No hay imagen cargada")
                    viewModel.loadState("init")
                }

                CreateClientViewModel.STATE_GALLERY -> {
                    openGallery()
                    viewModel.loadState("init")
                }

                CreateClientViewModel.STATE_CAMERA -> {
                    openCamera()
                    viewModel.loadState("init")
                }
                CreateClientViewModel.STATE_SELECT_BIRTHDAY->{
                    val datePicker = DatePickerFragment { year, month, day -> onDateSelectedBirthday(year, month, day) }
                    datePicker.show(parentFragmentManager,"datePicker")
                }

                CreateClientViewModel.STATE_SELECT_PAYDAY->{
                    val datePicker = DatePickerFragment { year, month, day -> onDateSelectedDayPay(year, month, day) }
                    datePicker.show(parentFragmentManager,"datePicker")
                }

                CreateClientViewModel.STATE_SELECT_FINISHDAY->{
                    val datePicker = DatePickerFragment { year, month, day -> onDateSelectedFinshDay(year, month, day) }
                    datePicker.show(parentFragmentManager,"datePicker")
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun onDateSelectedFinshDay(year: Int, month: Int, day: Int) {
        editFinishDay.setText("$day/$month/$year")
    }

    @SuppressLint("SetTextI18n")
    private fun onDateSelectedDayPay(year: Int, month: Int, day: Int) {
        editPayDay.setText("$day/$month/$year")
    }

    @SuppressLint("SetTextI18n")
    private fun onDateSelectedBirthday(year: Int, month: Int, day: Int) {
        editBirthday.setText("$day/$month/$year")
    }
    private fun inflateView(v: View) {
        editName        = v.findViewById(R.id.edTextName)
        editLastName    = v.findViewById(R.id.edTxtLastName)
        editBirthday    = v.findViewById(R.id.edTxtBirthday)
        editPhone       = v.findViewById(R.id.edTxtPhone)
        editEmail       = v.findViewById(R.id.edTxtEmail)
        editPayDay      = v.findViewById(R.id.edTxtDayPay)
        editFinishDay   = v.findViewById(R.id.edtxtFinishDay)
        editAmount      = v.findViewById(R.id.edTxtAmount)

        btnUpdateClient = v.findViewById(R.id.btnUpdate)
        btnEditImage    = v.findViewById(R.id.btnEditImage)
        btnDeleteImage  = v.findViewById(R.id.btnEdDeleteImg)

        imageClient     = v.findViewById(R.id.imageEditedClient)
    }

    private fun loadClientInfo(currentClient: Clients) {
        if (currentClient.Name != "")
            editName.hint = currentClient.Name
        if (currentClient.LastName != "")
            editLastName.hint = currentClient.LastName
        if (currentClient.Birthday != "")
            editBirthday.hint = currentClient.Birthday
        if (currentClient.Phone != "")
            editPhone.hint = currentClient.Phone
        if (currentClient.Email != "")
            editEmail.hint = currentClient.Email
        if (currentClient.PayDay != "")
            editPayDay.hint = currentClient.PayDay
        if (currentClient.FinishDay != "")
            editFinishDay.hint = currentClient.FinishDay
        if (currentClient.AmountClass != "")
            editAmount.hint = currentClient.AmountClass

    }

    private fun getEditedClient(currentClient: Clients): Clients {
        currentClient.Name          = editName.text.toString()
        currentClient.LastName      = editLastName.text.toString()
        currentClient.Birthday      = editBirthday.text.toString()
        currentClient.Phone         = editPhone.text.toString()
        currentClient.Email         = editEmail.text.toString()
        currentClient.Phone         = editPhone.text.toString()
        currentClient.PayDay        = editPayDay.text.toString()
        currentClient.FinishDay     = editFinishDay.text.toString()
        currentClient.AmountClass   = editAmount.text.toString()
        currentClient.ImageUri      = viewModel.getUri()
        currentClient.ImageName     = viewModel.getImageName()

        return currentClient
    }

    private fun loadImage(uri: String) {

        Glide.with(this)
            .load(uri)
            .into(imageClient)
    }

    private fun showToast(msg: String) {
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun showOptionsDialog() {
        val options = arrayOf("Abrir desde Galería", "Abrir Cámara")
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Selecciona una opción")
        builder.setItems(options) { dialog, which ->
            when (which) {
                0 -> viewModel.loadState("openGallery")
                1 -> viewModel.loadState("openCamera")
            }
        }
        builder.show()
    }

    private fun openCamera() {
        if (arePermissionsGrantedCamera()) {
            val value = ContentValues()
            value.put(MediaStore.Images.Media.TITLE, "New Image")
            imageUri = requireActivity().contentResolver.insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                value
            )
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
            startActivityForResult(cameraIntent, REQUEST_CAMERA)
        } else requestPermissions(REQUEST_CAMERA)
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun openGallery() {
        if (arePermissionsGrantedGallery()) {
            val intentGallery = Intent(Intent.ACTION_PICK)
            intentGallery.type = "image/*"
            startActivityForResult(intentGallery, REQUEST_GALLERY)
        } else requestPermissions(REQUEST_GALLERY)
    }
    private fun getImageCamera(data: Uri?){
        if (data != null) {

            viewModel.saveImage(data)
            imageClient.setImageURI(data)
        }
    }

    private fun getImageGallery(data: Intent?) {
        imageUri = data?.data
        if (imageUri != null) {

            viewModel.saveImage(imageUri!!)
            imageClient.setImageURI(imageUri)
        }

    }
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_GALLERY) {
            getImageGallery(data)
        }
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CAMERA) {
            getImageCamera(imageUri)
        }
    }

    @Deprecated("Deprecated in Java")
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            REQUEST_GALLERY ->{
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        openGallery()
                    }
                    else
                        Toast.makeText(requireContext(),"No tienes permiso a la galería",Toast.LENGTH_SHORT).show()
            }
            REQUEST_CAMERA ->{
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    openCamera()
                else
                    Toast.makeText(requireContext(),"No tienes permiso a la cámara",Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun arePermissionsGrantedCamera(): Boolean {
        return ContextCompat.checkSelfPermission(requireContext(),
            Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED
                ||  ContextCompat.checkSelfPermission(requireContext(),
            Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun arePermissionsGrantedGallery(): Boolean {
        return ContextCompat.checkSelfPermission(requireContext(),Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_DENIED
    }

    private fun requestPermissions(request : Int) {
        when(request){
            REQUEST_CAMERA ->{
                val permissions = arrayOf(Manifest.permission.CAMERA)
                requestPermissions(permissions, request)
            }
            REQUEST_GALLERY ->{
                val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                requestPermissions(permissions, request)
            }
        }
    }
}