package com.nan_app.fragments.calendar

import android.app.Dialog
import android.icu.util.Calendar
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.nan_app.R
import com.nan_app.adapters.CalendarAdapter
import com.nan_app.databinding.DialogCalendarBinding
import com.nan_app.databinding.FragmentCalendarBinding

class CalendarFragment : Fragment() {

    private lateinit var viewModel: CalendarViewModel
    private lateinit var adapter: CalendarAdapter

    private var _binding: FragmentCalendarBinding? = null
    private val binding get() = _binding!!

    private lateinit var dayOfWeekStr: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCalendarBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(requireActivity())[CalendarViewModel::class.java]

        initUI()

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.viewState.observe(viewLifecycleOwner) { state ->
            when (state) {
                CalendarViewModel.STATE_INIT -> {
                    getDate()
                }

                CalendarViewModel.STATE_LOAD_LIST -> {
                    viewModel.loadListCalendar()
                    viewModel.calendarList.observe(viewLifecycleOwner) {
                        adapter = CalendarAdapter(
                            it,
                            viewModel.getLisClient(),
                            dayOfWeekStr
                        ) { position ->
                            onItemSelected(position)
                        }

                        binding.rvCalendar.layoutManager = LinearLayoutManager(context)
                        binding.rvCalendar.adapter = adapter
                    }
                }
            }
        }
    }

    private fun onItemSelected(position: Int) {
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.dialog_calendar)

        val spinnerName : Spinner = dialog.findViewById(R.id.spinnerClient)

        val listClientSpinner = viewModel.getListNameClient()
        val listNameAdapter = ArrayAdapter(
            dialog.layoutInflater.context,
            android.R.layout.simple_gallery_item,
            listClientSpinner
        )
        listNameAdapter.setDropDownViewResource(android.R.layout.simple_gallery_item)
        spinnerName.adapter = listNameAdapter

       dialog.show()

    }

    private fun initUI() {
        viewModel.loadState(CalendarViewModel.STATE_INIT)
    }

    private fun getDate() {

        binding.calendarView.setOnDateChangeListener { view, year, month, dayOfMonth ->
            val calendar = Calendar.getInstance()
            calendar.set(year, month, dayOfMonth)
            val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
            val days =
                arrayOf("Domingo", "Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado")
            dayOfWeekStr = days[dayOfWeek - 1]

//            binding.titleCalendar.text = "$dayOfMonth/${month + 1}/$year, $dayOfWeekStr"
            viewModel.loadState(CalendarViewModel.STATE_LOAD_LIST)
        }

    }
}
