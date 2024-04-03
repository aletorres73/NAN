package com.nan_app.fragments.calendar

import android.app.Dialog
import android.icu.util.Calendar
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.nan_app.R
import com.nan_app.adapters.CalendarAdapter
import com.nan_app.databinding.FragmentCalendarBinding

class CalendarFragment : Fragment() {

    companion object {
        const val SABADO = "Sábado"
        const val DOMINGO = "Domingo"
    }

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
                    if (dayOfWeekStr == DOMINGO || dayOfWeekStr == SABADO)
                        binding.rvCalendar.isVisible = false
                    else {
                        binding.rvCalendar.isVisible = true
                        adapter = CalendarAdapter(
                            viewModel.getLisClient(),
                            dayOfWeekStr
                        ) { onItemSelected(it) }

                        binding.rvCalendar.layoutManager = LinearLayoutManager(context)
                        binding.rvCalendar.adapter = adapter

                        viewModel.loadState(CalendarViewModel.STATE_WAIT)
                    }
                }

                CalendarViewModel.STATE_WAIT -> {}
            }
        }
    }


    private fun onItemSelected(position: Int) {
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.dialog_calendar)
        dialog.show()

        val spinnerName: Spinner = dialog.findViewById(R.id.spinnerClient)

        val listClientSpinner = viewModel.getListNameClient()
        val listNameAdapter = ArrayAdapter(
            dialog.layoutInflater.context,
            android.R.layout.simple_gallery_item,
            listClientSpinner
        )

        listNameAdapter.setDropDownViewResource(android.R.layout.simple_gallery_item)
        spinnerName.adapter = listNameAdapter

        val buttonAdd = dialog.findViewById<Button>(R.id.dialogButtonAdd)
        buttonAdd.setOnClickListener {

            val clientId = viewModel.getClientId(spinnerName.selectedItemId.toInt())
            val time: String = getTime(position)
            val day = dayOfWeekStr

            viewModel.setClientOnCalendar(clientId, time, day)
            viewModel.loadState(CalendarViewModel.STATE_WAIT)

            dialog.dismiss()
        }
    }

    private fun getTime(position: Int): String {
        return when (position) {
            0 -> {
                "8am"
            }

            1 -> {
                "9am"
            }

            2 -> {
                "10am"
            }

            3 -> {
                "11am"
            }

            4 -> {
                "16pm"
            }

            5 -> {
                "17pm"
            }

            6 -> {
                "18pm"
            }

            7 -> {
                "19pm"
            }

            else -> {
                ""
            }
        }
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

            viewModel.loadState(CalendarViewModel.STATE_LOAD_LIST)
        }
    }
}
