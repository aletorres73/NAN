package com.nan_app.fragments.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.nan_app.R
import com.nan_app.adapters.ClientAdapter
import com.nan_app.adapters.ClientClickListener

class HomeFragment : Fragment() {

    private lateinit var v: View
    private lateinit var recClient: RecyclerView
    private lateinit var adapter: ClientAdapter
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    private lateinit var viewModel: HomeViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        v = inflater.inflate(R.layout.fragment_home, container, false)

        recClient = v.findViewById(R.id.rvClient)
        swipeRefreshLayout = v.findViewById(R.id.swipeRefreshLayout)


        viewModel = ViewModelProvider(requireActivity())[HomeViewModel::class.java]

        viewModel.init()
        swipeRefreshLayout.setOnRefreshListener {
            viewModel.refresh()
            swipeRefreshLayout.isRefreshing = false
        }

        return v
    }

    override fun onStart() {
        super.onStart()

        viewModel.viewState.observe(viewLifecycleOwner) { state ->
                when (state) {
                HomeViewModel.STATE_EMPTY -> {
                    viewModel.loadList()
                    Toast.makeText(requireContext(), "Lista vacía", Toast.LENGTH_SHORT).show()
                }

                HomeViewModel.STATE_LOADING -> {
                    viewModel.loadList()
//                    Toast.makeText(requireContext(), "Cargando...", Toast.LENGTH_SHORT).show()
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
                    Toast.makeText(requireContext(), "Cliente eliminado", Toast.LENGTH_SHORT).show()

                }

            }
        }
    }
    private fun showDoneState() {
        viewModel.ClientListDb.observe(viewLifecycleOwner){
            adapter = ClientAdapter(it, object : ClientClickListener {
                override fun onCardClick(position: Int) {
                    Toast.makeText(requireContext(),"Hiciste click en un cliente..",Toast.LENGTH_SHORT).show()                }

                override fun onDeleteButtonClick(position: Int) {
                    showDeleteConfirmationDialog(position)
                }

                override fun onEditButtonClick(position: Int) {
                    viewModel.getCurrentClient(position)
                    goEditFragment()
                }
            })
            recClient.layoutManager = LinearLayoutManager(context)
            recClient.adapter = adapter
            adapter.notifyItemRemoved(it.size -1)
        }
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