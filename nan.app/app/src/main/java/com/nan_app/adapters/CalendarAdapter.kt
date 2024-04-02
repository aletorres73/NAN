package com.nan_app.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nan_app.R
import com.nan_app.databinding.ItemCalendarBinding
import com.nan_app.entities.Clients

class CalendarAdapter(
    private var time: List<String>,
    private var listClient: List<Clients>,
    private var dayOfWeekStr: String,
    private var onItemSelected:(Int)->Unit
) : RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder>() {

    class CalendarViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private var binding = ItemCalendarBinding.bind(view)
        private val spaces = listOf(
            binding.textBed1,
            binding.textBed2,
            binding.textBed3,
            binding.textBed4,
            binding.textBed5)

        fun render(
            calendarList: String,
            listClient: List<Clients>,
            dayOfWeekStr: String,
            onItemSelected: (Int) -> Unit
        ) {
            binding.itemTime.text = calendarList
            getDayFromClient(listClient,dayOfWeekStr)

            itemView.setOnClickListener { onItemSelected(layoutPosition) }
        }

        private fun getDayFromClient(listClient: List<Clients>, dayOfWeekStr: String) {
            for( space in spaces){
                for(client in listClient) {
                    if (space.text.isEmpty())
                        if(client.dates.isNotEmpty())
                            space.text = client.dates[dayOfWeekStr]

                }
            }

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_calendar, parent, false)
        return CalendarViewHolder(view)
    }

    override fun onBindViewHolder(holder: CalendarViewHolder, position: Int) {
        holder.render(time[position],listClient,dayOfWeekStr,onItemSelected)
    }

    override fun getItemCount() = time.size

    fun updateList(){
        notifyDataSetChanged()
    }
}
