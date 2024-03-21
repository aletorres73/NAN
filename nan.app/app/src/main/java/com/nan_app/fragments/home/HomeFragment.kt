package com.nan_app.fragments.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.nan_app.R
import com.nan_app.adapters.ClientAdapter
import com.nan_app.adapters.ClientClickListener
import com.nan_app.entities.Clients

class HomeFragment : Fragment() {

    private lateinit var v                  : View
    private lateinit var recClient          : RecyclerView
    private lateinit var adapter            : ClientAdapter
    private lateinit var swipeRefreshLayout : SwipeRefreshLayout
    private lateinit var editTextFilter     : EditText

    private lateinit var viewModel: HomeViewModel
    private var deletePosition  = 0
    private var adapterItemCount  = 0
    private var listClient      = mutableListOf<Clients>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        v = inflater.inflate(R.layout.fragment_home, container, false)

        recClient           = v.findViewById(R.id.rvClient)
        swipeRefreshLayout  = v.findViewById(R.id.swipeRefreshLayout)
        editTextFilter      = v.findViewById(R.id.editTextFilter)

        viewModel = ViewModelProvider(requireActivity())[HomeViewModel::class.java]
        viewModel.init()


        swipeRefreshLayout.setOnRefreshListener {
            viewModel.refresh()
            swipeRefreshLayout.isRefreshing = false
        }


        viewModel.viewState.observe(viewLifecycleOwner) { state ->
            when (state) {
                HomeViewModel.STATE_EMPTY -> {
                    Toast.makeText(requireContext(), "Lista vacía", Toast.LENGTH_SHORT).show()
                }
                HomeViewModel.STATE_LOADING -> {
                    listClient = viewModel.loadList()
                    filterList()
                    viewModel.viewState.value = HomeViewModel.STATE_DONE
                }
                HomeViewModel.STATE_DONE -> {
                    showDoneState()
                }
                HomeViewModel.STATE_ERROR -> {
                    Toast.makeText(requireContext(), "Error", Toast.LENGTH_SHORT).show()
                }
                HomeViewModel.STATE_INIT -> {
                    viewModel.getList()
                }
                HomeViewModel.STATE_DELETE->{
                    listClient.removeAt(deletePosition)
                    adapter.notifyItemRemoved(deletePosition)
                    adapter.notifyItemRangeChanged(deletePosition, adapterItemCount)
                    Toast.makeText(requireContext(), "Cliente eliminado", Toast.LENGTH_SHORT).show()
                }
            }
        }
        return v
    }

    private fun filterList() {

        editTextFilter.addTextChangedListener {itemFiltered ->
            val listFilteredByName = listClient.filter {
                it.Name
                    .lowercase()
                    .contains(itemFiltered.toString().lowercase())
            }

            val listFilteredById = listClient.filter {
                it.id.toString() == itemFiltered.toString()
            }

            val listFilteredByLastName = listClient.filter {
                it.LastName
                    .lowercase()
                    .contains(itemFiltered.toString().lowercase())
            }

/*            val listFilteredByBirthday = listClient.filter {
                var month = ""
                when(itemFiltered.toString().lowercase()){
                    "enero"     ->{month = "1"}
                    "febrero"   ->{month = "2"}
                    "marzo"     ->{month = "3"}
                    "abril"     ->{month = "4"}
                    "mayo"      ->{month = "5"}
                    "junio"     ->{month = "6"}
                    "julio"     ->{month = "7"}
                    "agosto"    ->{month = "8"}
                    "septiembre"->{month = "9"}
                    "octubre"   ->{month = "10"}
                    "noviembre" ->{month = "11"}
                    "diciembre" ->{month = "12"}
                }
                it.Birthday.split() == month

            }*/
            val listFilteredByPayDay = listClient.filter {
                it.PayDay
                    .lowercase()
                    .contains(itemFiltered.toString().lowercase())
            }
            val listFilteredByFinishDay = listClient.filter {
                it.FinishDay
                    .lowercase()
                    .contains(itemFiltered.toString().lowercase())
            }
            val listFilteredByState = listClient.filter {
                it.State
                    .lowercase()
                    .contains(itemFiltered.toString().lowercase())
            }
            val listFilteredByAmountClass = listClient.filter {
                it.AmountClass
                    .lowercase()
                    .contains(itemFiltered.toString().lowercase())
            }

            if(listFilteredByName.isNotEmpty()){
                viewModel.updateListeDB(listFilteredByName.toMutableList())
                adapter.updateList(listFilteredByName.toMutableList())
            }
            if(listFilteredById.isNotEmpty()){
                viewModel.updateListeDB(listFilteredById.toMutableList())
                adapter.updateList(listFilteredById.toMutableList())
            }
            if(listFilteredByLastName.isNotEmpty()){
                viewModel.updateListeDB(listFilteredByLastName.toMutableList())
                adapter.updateList(listFilteredByLastName.toMutableList())
            }
/*            if(listFilteredByBirthday.isNotEmpty()){
                viewModel.updateListeDB(listFilteredByBirthday.toMutableList())
                adapter.updateList(listFilteredByBirthday.toMutableList())
            }*/
            if(listFilteredByPayDay.isNotEmpty()){
                viewModel.updateListeDB(listFilteredByPayDay.toMutableList())
                adapter.updateList(listFilteredByPayDay.toMutableList())
            }
            if(listFilteredByFinishDay.isNotEmpty()){
                viewModel.updateListeDB(listFilteredByFinishDay.toMutableList())
                adapter.updateList(listFilteredByFinishDay.toMutableList())
            }
            if(listFilteredByState.isNotEmpty()){
                viewModel.updateListeDB(listFilteredByState.toMutableList())
                adapter.updateList(listFilteredByState.toMutableList())
            }
            if(listFilteredByAmountClass.isNotEmpty()){
                viewModel.updateListeDB(listFilteredByAmountClass.toMutableList())
                adapter.updateList(listFilteredByAmountClass.toMutableList())
            }



        }    }

    private fun showDoneState() {
        adapter = ClientAdapter(
            listClient,
            object : ClientClickListener {

                override fun onCardClick(position: Int) {
                    Toast.makeText(requireContext(),"Hiciste click en un cliente..",Toast.LENGTH_SHORT).show()                }

                override fun onDeleteButtonClick(position: Int) {
                    showDeleteConfirmationDialog(position)
                    deletePosition = position
                    adapterItemCount = adapter.itemCount
                }

                override fun onEditButtonClick(position: Int) {
                    viewModel.getCurrentClient(position)
                    goEditFragment()
                }
            })
        recClient.layoutManager = LinearLayoutManager(context)
        recClient.adapter = adapter

    }
    private fun showDeleteConfirmationDialog(position: Int) {
        val alertDialogBuilder = AlertDialog.Builder(requireContext())

        alertDialogBuilder.setTitle("Eliminar cliente")
        alertDialogBuilder.setMessage("¿Estás seguro de que deseas eliminar este cliente?")

        alertDialogBuilder.setPositiveButton("Sí") { _, _ ->
            viewModel.deleteClient(position)
        }

        alertDialogBuilder.setNegativeButton("No") { _, _ ->
        }

        val alertDialog: AlertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }
    private fun goEditFragment(){
        val action = HomeFragmentDirections.actionHomeFragmentToEditClientFragment()
        findNavController().navigate(action)
    }

}