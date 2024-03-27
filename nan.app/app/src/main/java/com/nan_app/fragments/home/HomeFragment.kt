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
import com.nan_app.adapters.ClientAdapter
import com.nan_app.adapters.ClientClickListener
import com.nan_app.databinding.FragmentHomeBinding
import com.nan_app.entities.Clients
import androidx.appcompat.widget.SearchView

class HomeFragment : Fragment() {

    private  var _binding: FragmentHomeBinding? = null
    private val  binding get() = _binding!!
    private lateinit var adapter: ClientAdapter

    private lateinit var viewModel: HomeViewModel
    private var deletePosition = 0
    private var adapterItemCount = 0
    private var listClient = mutableListOf<Clients>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(layoutInflater)

        viewModel = ViewModelProvider(requireActivity())[HomeViewModel::class.java]
        viewModel.init()

        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.refresh()
            binding.swipeRefreshLayout.isRefreshing = false
        }
        ui()

        return binding.root
    }
    private fun ui() {
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

                HomeViewModel.STATE_DELETE -> {
                    listClient.removeAt(deletePosition)
                    adapter.notifyItemRemoved(deletePosition)
                    adapter.notifyItemRangeChanged(deletePosition, adapterItemCount)
                    Toast.makeText(requireContext(), "Cliente eliminado", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun filterList() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                searchByName(query.orEmpty())
                return false
            }

            override fun onQueryTextChange(newText: String?) = false
        })
    }

    private fun searchByName(query: String) {
        val filteredByName = listClient.filter {
            it.Name
                .lowercase()
                .contains(query)
        }
        if (filteredByName.isNotEmpty()) {
            viewModel.updateListeDB(filteredByName.toMutableList())
            adapter.updateList(filteredByName.toMutableList())
        }
    }

    private fun showDoneState() {
        adapter = ClientAdapter(listClient) { position -> onItemSelected(position) }
        binding.rvClient.layoutManager = LinearLayoutManager(context)
        binding.rvClient.adapter = adapter

    }

    private fun onItemSelected(position: Int) {
        viewModel.getCurrentClient(position)
        goEditFragment()
    }

    private fun goEditFragment() {
        val action = HomeFragmentDirections.actionHomeFragmentToEditClientFragment()
        findNavController().navigate(action)
    }

}