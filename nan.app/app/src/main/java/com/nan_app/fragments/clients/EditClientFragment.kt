package com.nan_app.fragments.clients

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.nan_app.R
import com.nan_app.entities.Clients
import java.util.Currency

class EditClientFragment : Fragment() {

    private lateinit var  v : View

    private lateinit var viewModel: EditClientViewModel


    private lateinit var editName       : EditText
    private lateinit var editLastName   : EditText
    private lateinit var editBirthday   : EditText
    private lateinit var editPhone      : EditText
    private lateinit var editEmail      : EditText
    private lateinit var editPayDay     : EditText
    private lateinit var editFinishDay  : EditText
    private lateinit var editAmount     : EditText
    private lateinit var imageClient    :ImageView

    private lateinit var btnUpdateClient: Button
    private lateinit var btnEditImage   : Button
    private lateinit var btnDeleteImage : Button

    private var currentClient : Clients = Clients()

    val imageDefect = "https://png.pngtree.com/png-clipart/20230824/original/pngtree-upload-users-user-arrow-tray-picture-image_8325109.png"


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
        if(currentClient.ImageUri != "")
            loadImage(currentClient.ImageUri)
        else
            loadImage(imageDefect)

        return v
    }

    override fun onStart() {
        super.onStart()

        viewModel.loadState("init")

        viewModel.viewState.observe(viewLifecycleOwner){state ->
            when(state) {

                EditClientViewModel.STATE_INIT -> {
                    btnUpdateClient.setOnClickListener {
                        viewModel.updatedClient(getEditedClient(currentClient), currentClient.id)
                    }

                    btnDeleteImage.setOnClickListener {
                        if(currentClient.ImageUri == ""){
                            viewModel.loadState("emptyImage")
                        }
                        else
                            viewModel.loadState("deleteImage")
                    }
                    btnEditImage.setOnClickListener {
//                        showOptionDialog()
                    }
                }
                EditClientViewModel.STATE_ERROR_UPDATE_CLIENT->{
                    showToast("No se pudieron actualizar los datos")
                    viewModel.loadState("init")
                }
                EditClientViewModel.STATE_DONE_UPDATE_CLIENT->{
                    Toast.makeText(requireContext(), "Datos actualizados", Toast.LENGTH_SHORT).show()
                    findNavController().popBackStack()
                }
                EditClientViewModel.STATE_DELETE_IMAGE->{
                    if(currentClient.ImageUri == ""){
                        viewModel.loadState("emptyImage")
                        viewModel.loadState("init")

                    }
                    else {
                        viewModel.deleteImage(currentClient.ImageName)
                        viewModel.loadState("init")
                    }
                }
                EditClientViewModel.STATE_DONE_IMAGE_DELETE->{
                    currentClient.ImageUri  = ""
                    currentClient.ImageName = ""
                    loadImage(imageDefect)
                    showToast("Imagen borrada")

                    viewModel.updateClientByImage(currentClient, currentClient.id)

                    viewModel.loadState("init")
                }
                EditClientViewModel.STATE_ERROR_IMAGE_DELETE->{
                    showToast("Error al borrar la imagen")
                    viewModel.loadState("init")
                }
                EditClientViewModel.STATE_IMAGE_EMPTY->{
                    showToast("No hay imagen cargada")
                    viewModel.loadState("init")
                }
            }
        }
    }

    private fun inflateView(v: View){
        editName       = v.findViewById(R.id.edTextName)
        editLastName   = v.findViewById(R.id.edTxtLastName)
        editBirthday   = v.findViewById(R.id.edTxtBirthday)
        editPhone      = v.findViewById(R.id.edTxtPhone)
        editEmail      = v.findViewById(R.id.edTxtEmail)
        editPayDay     = v.findViewById(R.id.edTxtDayPay)
        editFinishDay  = v.findViewById(R.id.edtxtFinishDay)
        editAmount     = v.findViewById(R.id.edTxtAmount)

        btnUpdateClient= v.findViewById(R.id.btnUpdate)
        btnEditImage   = v.findViewById(R.id.btnEditImage)
        btnDeleteImage = v.findViewById(R.id.btnEdDeleteImg)

        imageClient = v.findViewById(R.id.imageEditedClient)


    }

    private fun loadClientInfo(currentClient : Clients){
        if(currentClient.Name != "")
            editName.hint       = currentClient.Name
        if(currentClient.LastName != "")
            editLastName.hint   = currentClient.LastName
        if(currentClient.Birthday != "")
            editBirthday.hint   = currentClient.Birthday
        if(currentClient.Phone != "")
            editPhone.hint      = currentClient.Phone
        if(currentClient.Email != "")
            editEmail.hint      = currentClient.Email
        if(currentClient.PayDay != "")
            editPayDay.hint     = currentClient.PayDay
        if(currentClient.FinishDay != "")
            editFinishDay.hint  = currentClient.FinishDay
        if(currentClient.AmountClass != "")
            editAmount.hint     = currentClient.AmountClass
    }

    private fun getEditedClient(currentClient: Clients): Clients{
        currentClient.Name          = editName.text.toString()
        currentClient.LastName      = editLastName.text.toString()
        currentClient.Birthday      = editBirthday.text.toString()
        currentClient.Phone         = editPhone.text.toString()
        currentClient.Email         = editEmail.text.toString()
        currentClient.Phone         = editPhone.text.toString()
        currentClient.PayDay        = editPayDay.text.toString()
        currentClient.FinishDay     = editFinishDay.text.toString()
        currentClient.AmountClass   = editAmount.text.toString()


        return currentClient
    }

    private fun loadImage(uri : String){

        Glide.with(this)
            .load(uri)
            .into(imageClient)
    }

    private fun showToast(msg: String){
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
    }

}