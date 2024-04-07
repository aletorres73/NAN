package com.nan_app.fragments.clients.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.nan_app.adapters.ClientAdapter
import com.nan_app.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: ClientAdapter

    private lateinit var viewModel: HomeViewModel
    private var deletePosition = 0
    private var adapterItemCount = 0
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(layoutInflater)

        initView()

        return binding.root
    }

    private fun initView() {
        viewModel = ViewModelProvider(requireActivity())[HomeViewModel::class.java]
        viewModel.init()

        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.refresh()
            binding.swipeRefreshLayout.isRefreshing = false
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUi()
    }

    private fun initUi() {
        viewModel.viewState.observe(viewLifecycleOwner) { state ->
            when (state) {
                HomeViewModel.STATE_INIT -> {
                    viewModel.getList()
                }

                HomeViewModel.STATE_EMPTY -> {
                    Toast.makeText(requireContext(), "Lista vacía", Toast.LENGTH_SHORT).show()
                }

                HomeViewModel.STATE_LOADING -> {
                    viewModel.listClients.observe(viewLifecycleOwner) {

                        adapter = ClientAdapter(it) { position -> onItemSelected(position) }

                        binding.rvClient.layoutManager = LinearLayoutManager(context)
                        binding.rvClient.adapter = adapter

                        adapter.updateList(it.toMutableList())
                    }
                    filterList()
                }

                HomeViewModel.STATE_ERROR -> {
                    Toast.makeText(requireContext(), "Error", Toast.LENGTH_SHORT).show()
                }

                HomeViewModel.STATE_DELETE -> {
                    adapter.notifyItemRemoved(deletePosition)
                    adapter.notifyItemRangeChanged(deletePosition, adapterItemCount)
                    Toast.makeText(requireContext(), "Cliente eliminado", Toast.LENGTH_SHORT).show()
                }

                HomeViewModel.STATE_WAIT -> {}
            }
        }
    }

    private fun filterList() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                viewModel.searchByName(query.orEmpty())
                return false
            }

            override fun onQueryTextChange(newText: String?) = false
        })
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