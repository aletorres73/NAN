package com.nan_app.fragments.clients

import android.Manifest
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
import androidx.fragment.app.createViewModelLazy
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.nan_app.R
import com.nan_app.entities.Clients

class CreateClientFragment : Fragment() {

    private lateinit var v: View

    companion object{
        private val REQUEST_GALLERY = 1001
        private val REQUEST_CAMERA = 1002

    }


    lateinit var inputName      : EditText
    lateinit var inputLastName  : EditText
    lateinit var inputBirthday  : EditText
    lateinit var inputPhone     : EditText
    lateinit var inputEmail     : EditText
    lateinit var inputDayPay    : EditText
    lateinit var inputFinishDay : EditText
    lateinit var inputAmount    : EditText
    lateinit var inputId        : EditText

    lateinit var btnMakeClient  : Button
    lateinit var btnLoadImage   : Button
    lateinit var btnDeleteImage : Button
    lateinit var btnSelectDays  : Button

    lateinit var imageClient    : ImageView

    private var newClient : Clients = Clients()

    private lateinit var viewModel: CreateClientViewModel

    private var imageUri : Uri? = null
    private val urlLoadImage = "https://png.pngtree.com/png-clipart/20230824/original/pngtree-upload-users-user-arrow-tray-picture-image_8325109.png"
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        v = inflater.inflate(R.layout.fragment_create_client, container, false)
        viewModel = ViewModelProvider(requireActivity())[CreateClientViewModel::class.java]

        inflateViews(v)

        return v
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onStart() {
        super.onStart()

        loadImage(urlLoadImage)
        viewModel.loadState("init")

        viewModel.viewState.observe(viewLifecycleOwner){state->
            when(state){
                CreateClientViewModel.STATE_INIT-> {
                    btnMakeClient.setOnClickListener {
                        viewModel.loadState("newClient")
                    }
                    btnLoadImage.setOnClickListener {
                        showOptionsDialog()
                    }
                    btnDeleteImage.setOnClickListener {
                        viewModel.loadState("deleteImage")
                    }
                }
                CreateClientViewModel.STATE_LOAD_NEW_CLIENT->{
                    if(!checkInput())
                        viewModel.loadState("errorMakeNewClient")
                    else
                        viewModel.loadNewClient(getInputs())
                }
                CreateClientViewModel.STATE_ERROR_NEW_CLIENT->{
                    showToast("No se pudo cargar alumno nuevo")
                    viewModel.loadState("init")
                }
                CreateClientViewModel.STATE_DONE_NEW_CLIENT->{
                    showToast("Alumno agregado")
                    viewModel.loadState("init")
                    findNavController().popBackStack()

                }
                CreateClientViewModel.STATE_GALLERY->{
                    openGallery()
                    viewModel.loadState("init")
                }
                CreateClientViewModel.STATE_CAMERA->{
                    openCamera()
                    viewModel.loadState("init")
                }
                CreateClientViewModel.STATE_DELETE_IMAGE->{
                    if(newClient.ImageUri == ""){
                        viewModel.loadState("emptyImage")
                        viewModel.loadState("init")

                    }
                    else {
                        viewModel.deleteImage(imageUri!!)
                        viewModel.loadState("init")
                    }
                }
                CreateClientViewModel.STATE_IMAGE_EMPTY->{
                    showToast("No hay imagen cargada")
                }
                CreateClientViewModel.STATE_DONE_IMAGE_DELETE->{
                    showToast("Imagen borrada")
                    newClient.ImageUri = ""
                    loadImage(urlLoadImage)
                    viewModel.loadState("init")
                }
                CreateClientViewModel.STATE_ERROR_IMAGE_DELETE->{
                    showToast("Error al borrar imagen")
                    viewModel.loadState("init")
                }
            }

        }
    }

    private fun showToast(msg: String){
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
    }

    private fun inflateViews(v: View) {
        inputName      = v.findViewById(R.id.editTextName)
        inputLastName  = v.findViewById(R.id.editTextLastName)
        inputBirthday  = v.findViewById(R.id.editTextBirthday)
        inputPhone     = v.findViewById(R.id.editTextPhone)
        inputEmail     = v.findViewById(R.id.editTextEmail)
        inputDayPay    = v.findViewById(R.id.editTextDayPay)
        inputFinishDay = v.findViewById(R.id.editTextFinishDay)
        inputAmount    = v.findViewById(R.id.editTextAmount)
        inputId        = v.findViewById(R.id.editTextId)

        btnMakeClient  = v.findViewById(R.id.btnMakeClient)
        btnLoadImage   = v.findViewById(R.id.btnLoadImageClient)
        btnDeleteImage = v.findViewById(R.id.btnDeletImg)
        btnSelectDays  = v.findViewById(R.id.btnDaySelect)

        imageClient = v.findViewById(R.id.imageCreateClient)
    }
    private fun getInputs(): Clients{
        newClient.id         = inputId.text.toString().toInt()
        newClient.Name       = inputName.text.toString()
        newClient.LastName   = inputLastName.text.toString()
        newClient.Birthday   = inputBirthday.text.toString()
        newClient.Email      = inputEmail.text.toString()
        newClient.Phone      = inputPhone.text.toString()
        newClient.PayDay     = inputDayPay.text.toString()
        newClient.FinishDay  = inputFinishDay.text.toString()
        newClient.State      = ""
        newClient.AmountClass= ""

        return newClient
    }
    private fun checkInput(): Boolean{
        if (inputId.text.isEmpty()){
            Toast.makeText(requireContext(), "Ingresar Id ", Toast.LENGTH_SHORT).show()
            return false
        }
        if(inputName.text.isEmpty()){
            Toast.makeText(requireContext(), "Ingresar Nombre", Toast.LENGTH_SHORT).show()
            return false
        }
        else if(inputLastName.text.isEmpty()){
            Toast.makeText(requireContext(), "Ingresar Apellido", Toast.LENGTH_SHORT).show()
            return false
        }
        else if(newClient.ImageUri.isEmpty()){
            newClient.ImageUri = ""
            return true
        }
/*        else if(inputBirthday.text.isEmpty()){
            Toast.makeText(requireContext(), "Ingresar fecha de nacimiento", Toast.LENGTH_SHORT).show()
            return false
        }*/
/*        else if(inputPhone.text.isEmpty()){
            Toast.makeText(requireContext(), "Ingresar número de teléfono", Toast.LENGTH_SHORT).show()
            return false
        }
        else if(inputEmail.text.isEmpty()){
            Toast.makeText(requireContext(), "Ingresar dirección de correo", Toast.LENGTH_SHORT).show()
            return false
        }
        else if(inputDayPay.text.isEmpty()){
            Toast.makeText(requireContext(), "Ingresar fecha de pago", Toast.LENGTH_SHORT).show()
            return false
        }*/
        return true

    }
    private fun loadImage(uri: String){

        Glide.with(this)
            .load(uri)
            .into(imageClient)
    }

    private fun openCamera(){
        if(arePermissionsGrantedCamera()){
            val value = ContentValues()
            value.put(MediaStore.Images.Media.TITLE, "New Image")
            imageUri= requireActivity().contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,value)
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
            startActivityForResult(cameraIntent,REQUEST_CAMERA)
        }
        else requestPermissions(REQUEST_CAMERA)
    }
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun openGallery() {
        if(arePermissionsGrantedGallery()){
            val intentGallery = Intent(Intent.ACTION_PICK)
            intentGallery.type = "image/*"
            startActivityForResult(intentGallery, REQUEST_GALLERY)
        }
        else requestPermissions(REQUEST_GALLERY)
    }
    private fun getImageCamera(data: Uri?){
        if (data != null) {
            viewModel.uploadImage(data)
            viewModel.viewUrl.observe(viewLifecycleOwner) {
                newClient.ImageUri = it
                loadImage(newClient.ImageUri)
            }
            Toast.makeText(requireContext(), "Imagen cargada", Toast.LENGTH_SHORT).show()
        }
    }
    private fun getImageGallery(data: Intent?) {
        imageUri = data?.data
        if (imageUri != null) {
            viewModel.uploadImage(imageUri!!)
            viewModel.viewUrl.observe(viewLifecycleOwner) {
                newClient.ImageUri = it
                loadImage(newClient.ImageUri)
            }
            Toast.makeText(requireContext(), "Imagen cargada", Toast.LENGTH_SHORT).show()
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
            REQUEST_GALLERY->{
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        openGallery()
                    }
                else
                    Toast.makeText(requireContext(),"No tienes permiso a la galería",Toast.LENGTH_SHORT).show()
            }
            REQUEST_CAMERA->{
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    openCamera()
                else
                    Toast.makeText(requireContext(),"No tienes permiso a la cámara",Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun arePermissionsGrantedCamera(): Boolean {
        return ContextCompat.checkSelfPermission(requireContext(),Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED
                ||  ContextCompat.checkSelfPermission(requireContext(),Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED
        }
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun arePermissionsGrantedGallery(): Boolean {
        return ContextCompat.checkSelfPermission(requireContext(),Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_DENIED
    }

    private fun requestPermissions(request : Int) {
        when(request){
            REQUEST_CAMERA->{
                val permissions = arrayOf(Manifest.permission.CAMERA)
                requestPermissions(permissions, request)
            }
            REQUEST_GALLERY->{
                val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                requestPermissions(permissions, request)
            }
        }

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



}