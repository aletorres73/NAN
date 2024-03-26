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
import androidx.navigation.fragment.findNavController
import com.nan_app.R
import com.nan_app.databinding.FragmentCreateClientBinding
import com.nan_app.databinding.FragmentHomeBinding
import com.nan_app.entities.Clients

class CreateClientFragment : Fragment() {

    private lateinit var binding: FragmentCreateClientBinding

    companion object {
        private const val REQUEST_GALLERY = 1001
        private const val REQUEST_CAMERA = 1002

    }

    private var newClient: Clients = Clients()

    private lateinit var viewModel: CreateClientViewModel

    private var imageUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCreateClientBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(requireActivity())[CreateClientViewModel::class.java]

        viewModel.loadState("init")

        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onStart() {
        super.onStart()

        viewModel.viewState.observe(viewLifecycleOwner) { state ->
            when (state) {

                CreateClientViewModel.STATE_INIT -> {
                    binding.btnMakeClient.setOnClickListener {
                        if (!checkInput())
                            viewModel.loadState("errorClientLoad")
                        else {
                            getInputs()
                            if (newClient.ImageName != "")
                                viewModel.loadState("loadNewImage")
                            else
                                viewModel.loadState("newClient")
                        }
                    }
                    binding.btnLoadImageClient.setOnClickListener {
                        showOptionsDialog()
                    }
                    binding.editTextBirthday.setOnClickListener {
                        viewModel.loadState("selectBirthday")
                    }
                    binding.editTextDayPay.setOnClickListener {
                        viewModel.loadState("selectDayPay")
                    }
                    binding.editTextFinishDay.setOnClickListener {
                        viewModel.loadState("selectFinishDay")
                    }
                }

                CreateClientViewModel.STATE_LOAD_NEW_CLIENT -> {
                    viewModel.loadNewClient(newClient)
                }

                CreateClientViewModel.STATE_LOAD_NEW_IMAGE -> {
                    viewModel.loadImage(newClient)
                    viewModel.loadState("init")
                }

                CreateClientViewModel.STATE_ERROR_NEW_CLIENT -> {
                    showToast("No se pudo cargar alumno nuevo")
                    viewModel.loadState("init")
                }

                CreateClientViewModel.STATE_DONE_NEW_CLIENT -> {
                    val action =
                        CreateClientFragmentDirections.actionCreateClientFragmentToHomeFragment()
                    findNavController().navigate(action)
                    showToast("Alumno agregado")
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

                CreateClientViewModel.STATE_SELECT_BIRTHDAY -> {
                    val datePicker = DatePickerFragment { year, month, day ->
                        onDateSelectedBirthday(
                            year,
                            month,
                            day
                        )
                    }
                    datePicker.show(parentFragmentManager, "datePicker")
                }

                CreateClientViewModel.STATE_SELECT_PAYDAY -> {
                    val datePicker = DatePickerFragment { year, month, day ->
                        onDateSelectedDayPay(
                            year,
                            month,
                            day
                        )
                    }
                    datePicker.show(parentFragmentManager, "datePicker")
                }

                CreateClientViewModel.STATE_SELECT_FINISHDAY -> {
                    val datePicker = DatePickerFragment { year, month, day ->
                        onDateSelectedFinshDay(
                            year,
                            month,
                            day
                        )
                    }
                    datePicker.show(parentFragmentManager, "datePicker")
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun onDateSelectedFinshDay(year: Int, month: Int, day: Int) {
        binding.editTextFinishDay.setText("$day/$month/$year")
    }

    @SuppressLint("SetTextI18n")
    private fun onDateSelectedDayPay(year: Int, month: Int, day: Int) {
        binding.editTextDayPay.setText("$day/$month/$year")
    }

    @SuppressLint("SetTextI18n")
    private fun onDateSelectedBirthday(year: Int, month: Int, day: Int) {
        binding.editTextBirthday.setText("$day/$month/$year")
    }


    private fun showToast(msg: String) {
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
    }

    private fun getInputs() {
        newClient.id = binding.editTextId.text.toString().toInt()
        newClient.Name = binding.editTextName.text.toString()
        newClient.LastName = binding.editTextLastName.text.toString()
        newClient.Birthday = binding.editTextBirthday.text.toString()
        newClient.Email = binding.editTextEmail.text.toString()
        newClient.Phone = binding.editTextPhone.text.toString()
        newClient.PayDay = binding.editTextDayPay.text.toString()
        newClient.FinishDay = binding.editTextFinishDay.text.toString()
        newClient.ImageName = viewModel.getImageName()

    }

    private fun checkInput(): Boolean {
        if (binding.editTextId.text.isEmpty()) {
            Toast.makeText(requireContext(), "Ingresar Id ", Toast.LENGTH_SHORT).show()
            return false
        }
        if (viewModel.checkID(binding.editTextId.text.toString().toInt())) {
            Toast.makeText(
                requireContext(),
                "Ya existe un alumnno con el Id ${binding.editTextId.text} ",
                Toast.LENGTH_SHORT
            ).show()
            return false
        }
        if (binding.editTextName.text.isEmpty()) {
            Toast.makeText(requireContext(), "Ingresar Nombre", Toast.LENGTH_SHORT).show()
            return false
        } else if (binding.editTextLastName.text.isEmpty()) {
            Toast.makeText(requireContext(), "Ingresar Apellido", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
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

    private fun getImageCamera(data: Uri?) {
        if (data != null) {
            viewModel.saveImage(data)
            binding.imageCreateClient.setImageURI(data)
        }
    }

    private fun getImageGallery(data: Intent?) {
        imageUri = data?.data
        if (imageUri != null) {
            viewModel.saveImage(imageUri!!)
            binding.imageCreateClient.setImageURI(imageUri)
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
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_GALLERY -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        openGallery()
                    } else
                        Toast.makeText(
                            requireContext(),
                            "No tienes permiso a la galería",
                            Toast.LENGTH_SHORT
                        ).show()
            }

            REQUEST_CAMERA -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    openCamera()
                else
                    Toast.makeText(
                        requireContext(),
                        "No tienes permiso a la cámara",
                        Toast.LENGTH_SHORT
                    ).show()
            }
        }
    }

    private fun arePermissionsGrantedCamera(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_DENIED
                || ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_DENIED
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun arePermissionsGrantedGallery(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.READ_MEDIA_IMAGES
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