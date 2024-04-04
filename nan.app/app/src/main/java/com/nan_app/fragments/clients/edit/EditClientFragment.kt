package com.nan_app.fragments.clients.edit

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat.*
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.nan_app.R
import com.nan_app.databinding.FragmentEditClientBinding
import com.nan_app.entities.Clients
import com.nan_app.fragments.datePicker.DatePickerFragment

class EditClientFragment : Fragment() {

    private var _binding: FragmentEditClientBinding? = null
    private val binding get() = _binding!!
    private lateinit var vm: EditClientViewModel

    companion object {
        private const val REQUEST_GALLERY = 1001
        private const val REQUEST_CAMERA = 1002
        private const val DATE_PICKER = "datePicker"

    }

    private var imageUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditClientBinding.inflate(layoutInflater)
        vm = ViewModelProvider(this)[EditClientViewModel::class.java]

        loadClientInfo()

        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUI()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun initUI() {
        super.onStart()
        vm.loadState(EditClientViewModel.STATE_INIT)

        vm.viewState.observe(viewLifecycleOwner) { state ->
            when (state) {
                EditClientViewModel.STATE_INIT -> {
                    vm.currentClient.observe(viewLifecycleOwner) { currentClient ->

                        binding.btnUpdate.setOnClickListener {
//                            if (checkInput()) {
                                binding.edProgressBar.isVisible = true
                                binding.btnUpdate.isClickable = false
                                vm.updatedClient(
                                    getEditedClient(currentClient),
                                    currentClient.id
                                )
//                            } else
//                                vm.loadState(EditClientViewModel.STATE_ERROR_UPDATE_CLIENT)
                        }
                        binding.btnDeleteClient.setOnClickListener {
                            showDeleteConfirmationDialog(
                                currentClient.id,
                                currentClient.ImageName,
                                currentClient.Name,
                                currentClient.LastName
                            )
                        }

                        binding.btnEdDeleteImg.setOnClickListener {
                            binding.edProgressBar.isVisible = true
//                            if (currentClient.ImageUri == "") vm.loadState(EditClientViewModel.STATE_IMAGE_EMPTY)
                            vm.loadState(EditClientViewModel.STATE_BUTTON_DELETE_IMAGE)
                        }

                        binding.btnEditImage.setOnClickListener {
                            if (currentClient.ImageName == "") showOptionsDialog()
                            if (currentClient.ImageName == "null") showOptionsDialog()
                            else vm.loadState(EditClientViewModel.STATE_INIT)
                        }

                    }

                    binding.edTxtBirthday.setOnClickListener {
                        vm.loadState(EditClientViewModel.STATE_SELECT_BIRTHDAY)
                    }

                    binding.edTxtDayPay.setOnClickListener {
                        vm.loadState(EditClientViewModel.STATE_SELECT_PAYDAY)
                    }

                    binding.edtxtFinishDay.setOnClickListener {
                        vm.loadState(EditClientViewModel.STATE_SELECT_FINISHDAY)
                    }
                }

                EditClientViewModel.STATE_ERROR_UPDATE_CLIENT -> {
                    showToast("No se actualizaron los datos")
                    vm.loadState(EditClientViewModel.STATE_INIT)
                }

                EditClientViewModel.STATE_DONE_UPDATE_CLIENT -> {
                    val action =
                        EditClientFragmentDirections.actionEditClientFragmentToHomeFragment()
                    findNavController().navigate(action)
                    binding.edProgressBar.isVisible = false
                    vm.loadState(EditClientViewModel.STATE_INIT)
                }

                EditClientViewModel.STATE_BUTTON_DELETE_IMAGE -> {
                    vm.currentClient.observe(viewLifecycleOwner) { currentClient ->

                        if (!vm.imageSaved) {
                            if (currentClient.ImageUri == "")
                                vm.loadState(EditClientViewModel.STATE_IMAGE_EMPTY)
                            else {
                                vm.deleteImage(currentClient.ImageName)
                                currentClient.ImageName = ""
                                vm.loadState(EditClientViewModel.STATE_INIT)
                            }
                        } else {
                            binding.imageEditedClient.setImageDrawable(
                                getDrawable(
                                    binding.imageEditedClient.context,
                                    R.drawable.df
                                )
                            )
                            binding.edProgressBar.isVisible = false
                            vm.loadState(EditClientViewModel.STATE_INIT)
                        }
                    }
                }

                EditClientViewModel.STATE_DONE_IMAGE_DELETE -> {
                    vm.currentClient.observe(viewLifecycleOwner) { currentClient ->

                        currentClient.ImageUri = ""
                        currentClient.ImageName = ""
                        binding.imageEditedClient.setImageDrawable(
                            getDrawable(
                                binding.imageEditedClient.context,
                                R.drawable.df
                            )
                        )
                        showToast("Imagen borrada")

                        vm.updateClientByImage(currentClient, currentClient.id)
                        binding.edProgressBar.isVisible = false
                        vm.loadState(EditClientViewModel.STATE_INIT)

                    }
                }

                EditClientViewModel.STATE_ERROR_IMAGE_DELETE -> {
                    showToast("Error al borrar la imagen")
                    binding.imageEditedClient.setImageDrawable(
                        getDrawable(binding.imageEditedClient.context, R.drawable.df)
                    )
                    binding.edProgressBar.isVisible = false
                    vm.loadState(EditClientViewModel.STATE_INIT)
                }

                EditClientViewModel.STATE_IMAGE_EMPTY -> {
                    showToast("No hay imagen cargada")
                    binding.edProgressBar.isVisible = false
                    vm.loadState(EditClientViewModel.STATE_INIT)
                }

                EditClientViewModel.STATE_GALLERY -> {
                    openGallery()
                    vm.loadState(EditClientViewModel.STATE_INIT)
                }

                EditClientViewModel.STATE_CAMERA -> {
                    openCamera()
                    vm.loadState(EditClientViewModel.STATE_INIT)
                }

                EditClientViewModel.STATE_SELECT_BIRTHDAY -> {
                    val datePicker = DatePickerFragment { year, month, day ->
                        onDateSelectedBirthday(
                            year, month, day
                        )
                    }
                    datePicker.show(parentFragmentManager, DATE_PICKER)
                }

                EditClientViewModel.STATE_SELECT_PAYDAY -> {
                    val datePicker = DatePickerFragment { year, month, day ->
                        onDateSelectedDayPay(
                            year, month, day
                        )
                    }
                    datePicker.show(parentFragmentManager, DATE_PICKER)
                }

                EditClientViewModel.STATE_SELECT_FINISHDAY -> {
                    val datePicker = DatePickerFragment { year, month, day ->
                        onDateSelectedFinshDay(
                            year, month, day
                        )
                    }
                    datePicker.show(parentFragmentManager, DATE_PICKER)
                }

                EditClientViewModel.STATE_CLIENT_DELETED -> {
                    showToast("Alumno eliminado")
                    findNavController().popBackStack()
                    binding.edProgressBar.isVisible = false
                    vm.loadState(EditClientViewModel.STATE_INIT)
                }

                EditClientViewModel.STATE_ERROR_DELETE_CLIENT -> {
                    showToast("Error al eliminar alumno")
                    binding.edProgressBar.isVisible = true
                    binding.btnUpdate.isClickable = true
                    vm.loadState(EditClientViewModel.STATE_INIT)
                }
            }
        }
    }

    private fun showDeleteConfirmationDialog(
        id: Int,
        imageName: String,
        name: String,
        lastName: String
    ) {
        val fullName = "$name $lastName"
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        alertDialogBuilder.setTitle("Eliminar Alumno")
        alertDialogBuilder.setMessage("¿Deseas a $fullName de la lista de alumnos?")

        alertDialogBuilder.setPositiveButton("Sí") { _, _ ->
            binding.edProgressBar.isVisible = true
            vm.deleteClient(id, imageName)
        }

        alertDialogBuilder.setNegativeButton("No") { _, _ ->
        }

        val alertDialog: AlertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    private fun checkInput(): Boolean {
        if (binding.edTextName.text.isEmpty())
            if (binding.edTxtLastName.text.isEmpty())
                if (binding.edTxtBirthday.text.isEmpty())
                    if (binding.edTxtPhone.text.isEmpty())
                        if (binding.edTxtEmail.text.isEmpty())
                            if (binding.edTxtDayPay.text.isEmpty())
                                if (binding.edtxtFinishDay.text.isEmpty())
                                    return false
//                                    if (binding.edTxtAmount.text.isEmpty())


        return true
    }

    @SuppressLint("SetTextI18n")
    private fun onDateSelectedFinshDay(year: Int, month: Int, day: Int) {
        binding.edtxtFinishDay.setText("$day/$month/$year")
    }

    @SuppressLint("SetTextI18n")
    private fun onDateSelectedDayPay(year: Int, month: Int, day: Int) {
        binding.edTxtDayPay.setText("$day/$month/$year")
    }

    @SuppressLint("SetTextI18n")
    private fun onDateSelectedBirthday(year: Int, month: Int, day: Int) {
        binding.edTxtBirthday.setText("$day/$month/$year")
    }

    private fun loadClientInfo() {
        vm.getClient()
        vm.currentClient.observe(viewLifecycleOwner) {

            if (it.Name != "") binding.edTextName.hint = it.Name
            if (it.LastName != "") binding.edTxtLastName.hint = it.LastName
            if (it.Birthday != "") binding.edTxtBirthday.hint = it.Birthday
            if (it.Phone != "") binding.edTxtPhone.hint = it.Phone
            if (it.Email != "") binding.edTxtEmail.hint = it.Email
            if (it.PayDay != "") binding.edTxtDayPay.hint = it.PayDay
            if (it.FinishDay != "") binding.edtxtFinishDay.hint = it.FinishDay
            if (it.AmountClass != "")
                when (it.AmountClass) {
                    "4" -> {
                        binding.edSpinnerAmount.setSelection(0)
                    }

                    "8" -> {
                        binding.edSpinnerAmount.setSelection(1)
                    }

                    "12" -> {
                        binding.edSpinnerAmount.setSelection(2)
                    }
                }

            if (it.ImageUri == "" || it.ImageUri == "null")
                binding.imageEditedClient.setImageDrawable(
                    getDrawable(
                        binding.imageEditedClient.context,
                        R.drawable.df
                    )
                )
            else loadImage(it.ImageUri)
        }
    }

    private fun getEditedClient(currentClient: Clients): Clients {
        currentClient.Name = binding.edTextName.text.toString()
        currentClient.LastName = binding.edTxtLastName.text.toString()
        currentClient.Birthday = binding.edTxtBirthday.text.toString()
        currentClient.Phone = binding.edTxtPhone.text.toString()
        currentClient.Email = binding.edTxtEmail.text.toString()
        currentClient.PayDay = binding.edTxtDayPay.text.toString()
        currentClient.FinishDay = binding.edtxtFinishDay.text.toString()
        currentClient.AmountClass = binding.edSpinnerAmount.selectedItem.toString()
        currentClient.ImageUri = vm.getUri()
        currentClient.ImageName = vm.getImageName()

        return currentClient
    }

    private fun loadImage(uri: String) {

        Glide.with(this).load(uri).into(binding.imageEditedClient)
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
                0 -> vm.loadState(EditClientViewModel.STATE_GALLERY)
                1 -> vm.loadState(EditClientViewModel.STATE_CAMERA)
            }
        }
        builder.show()
    }

    private fun openCamera() {
        if (arePermissionsGrantedCamera()) {
            val value = ContentValues()
            value.put(MediaStore.Images.Media.TITLE, "New Image")
            imageUri = requireActivity().contentResolver.insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, value
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

    private fun getImageCamera(data: Uri?) {
        if (data != null) {
            vm.saveImage(data)
            binding.imageEditedClient.setImageURI(data)
        }
    }

    private fun getImageGallery(data: Intent?) {
        imageUri = data?.data
        if (imageUri != null) {
            vm.saveImage(imageUri!!)
            binding.imageEditedClient.setImageURI(imageUri)
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
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_GALLERY -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    openGallery()
                } else Toast.makeText(
                    requireContext(), "No tienes permiso a la galería", Toast.LENGTH_SHORT
                ).show()
            }

            REQUEST_CAMERA -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) openCamera()
                else Toast.makeText(
                    requireContext(), "No tienes permiso a la cámara", Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun arePermissionsGrantedCamera(): Boolean {
        return checkSelfPermission(
            requireContext(), Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_DENIED || checkSelfPermission(
            requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_DENIED
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun arePermissionsGrantedGallery(): Boolean {
        return checkSelfPermission(
            requireContext(), Manifest.permission.READ_MEDIA_IMAGES
        ) == PackageManager.PERMISSION_DENIED
    }

    private fun requestPermissions(request: Int) {
        when (request) {
            REQUEST_CAMERA -> {
                val permissions = arrayOf(Manifest.permission.CAMERA)
                requestPermissions(permissions, request)
            }

            REQUEST_GALLERY -> {
                val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                requestPermissions(permissions, request)
            }
        }
    }
}