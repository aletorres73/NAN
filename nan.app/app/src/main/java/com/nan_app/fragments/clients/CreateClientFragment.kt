package com.nan_app.fragments.clients

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.provider.ContactsContract.CommonDataKinds.Phone
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.nan_app.R
import com.nan_app.entities.Clients
import com.nan_app.fragments.home.HomeViewModel
import org.koin.core.component.getScopeId

class CreateClientFragment : Fragment() {

    private lateinit var v: View

    lateinit var inputName      : EditText
    lateinit var inputLastName  : EditText
    lateinit var inputBirthday  : EditText
    lateinit var inputPhone     : EditText
    lateinit var inputEmail     : EditText
    lateinit var inputDayPay    : EditText
    lateinit var inputFinishDay : EditText
    lateinit var inputAmount    : EditText

    lateinit var btnMakeClient  : Button
    lateinit var btnLoadImage   : Button
    lateinit var btnDeleteImage : Button
    lateinit var btnSelectDays  : Button

    private var newClient : Clients = Clients()

    private lateinit var viewModel: CreateClientViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        v = inflater.inflate(R.layout.fragment_create_client, container, false)
        viewModel = ViewModelProvider(requireActivity())[CreateClientViewModel::class.java]

        inflateViews(v)
        return v
    }

    override fun onStart() {
        super.onStart()

        btnMakeClient.setOnClickListener {
            viewModel.initState()
            viewModel.viewState.observe(viewLifecycleOwner){state ->
                when(state){
                    CreateClientViewModel.STATE_INIT->{
                        if(!checkInput()) viewModel.error()
                        else viewModel.loading()
                    }
                    CreateClientViewModel.STATE_LOADING->{
                        viewModel.makeNewClient(getInputs())
                        viewModel.done()
                    }
                    CreateClientViewModel.STATE_DONE->{
                        Toast.makeText(requireContext(), "Cliente agregado", Toast.LENGTH_SHORT).show()
                        findNavController().popBackStack()
                    }
                    CreateClientViewModel.STATE_ERROR->{
                        Toast.makeText(requireContext(), "Error al crear cliente nuevo", Toast.LENGTH_SHORT).show()
                        findNavController().popBackStack()
                    }
                }
            }
        }



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

        btnMakeClient  = v.findViewById(R.id.btnMakeClient)
        btnLoadImage   = v.findViewById(R.id.btnLoadImageClient)
        btnDeleteImage = v.findViewById(R.id.btnDeletImg)
        btnSelectDays  = v.findViewById(R.id.btnDaySelect)
    }
    private fun getInputs(): Clients{
//        newClient.id         = viewModel.getNewId()
        newClient.id         = viewModel.getNewId()
        newClient.Name       = inputName.text.toString()
        newClient.LastName   = inputLastName.text.toString()
        newClient.Birthday   = inputBirthday.text.toString()
        newClient.Email      = inputEmail.text.toString()
        newClient.Phone      = inputPhone.text.toString()
        newClient.PayDay     = inputDayPay.text.toString()
        newClient.FinishDay  = inputFinishDay.text.toString()
        newClient.State      = ""
        newClient.ImageUri   = ""
        newClient.AmountClass= ""

        return newClient
    }
    private fun checkInput(): Boolean{
        if(inputName.text.isEmpty()){
            Toast.makeText(requireContext(), "Ingresar Nombre", Toast.LENGTH_SHORT).show()
            return false
        }
        else if(inputLastName.text.isEmpty()){
            Toast.makeText(requireContext(), "Ingresar Apellido", Toast.LENGTH_SHORT).show()
            return false
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


}