package com.nan_app.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.nan_app.R
import com.nan_app.databinding.ItemClientBinding
import com.nan_app.entities.Clients
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

class ClientAdapter(
    private var clientList: MutableList<Clients>,
    private var onItemsSelected: (Int) -> Unit
) : RecyclerView.Adapter<ClientAdapter.ClientHolder>() {

    class ClientHolder(view: View) : RecyclerView.ViewHolder(view) {

        private var binding = ItemClientBinding.bind(view)
        fun render(client: Clients, onItemSelected: (Int) -> Unit) {
            binding.txtId.text = client.id.toString()
            binding.txtName.text = client.Name
            binding.txtLastName.text = client.LastName

            setClientStateValue(client.FinishDay)
            setBirthday(client.Birthday)
            setImageClient(client.ImageUri)

            itemView.setOnClickListener { onItemSelected(layoutPosition) }
        }

        private fun setClientStateValue(finishDay: String) {
            if (finishDay != "") {
                val formatter1 = DateTimeFormatter.ofPattern("d/M/yyyy")
                val formatter2 = DateTimeFormatter.ofPattern("dd/MM/yyyy")

                val finishDate: LocalDate = try {
                    LocalDate.parse(finishDay, formatter1)
                } catch (e: DateTimeParseException) {
                    LocalDate.parse(finishDay, formatter2)
                }

                val currentDate = LocalDate.now()
                val daysUntilFinish = currentDate.until(finishDate).days

                when {
                    daysUntilFinish < 0 -> {
                        binding.txtState.text = ContextCompat.getString(
                            binding.txtState.context,
                            R.string.state_out_of_day
                        )
                        binding.txtState.setBackgroundColor(
                            ContextCompat.getColor(
                                binding.txtState.context,
                                R.color.text_state_out_of_day
                            )
                        )
                    }

                    daysUntilFinish in 0..5 -> {
                        binding.txtState.text = ContextCompat.getString(
                            binding.txtState.context,
                            R.string.state_next_day_out
                        )
                        binding.txtState.setBackgroundColor(
                            ContextCompat.getColor(
                                binding.txtState.context,
                                R.color.text_state_next_out
                            )
                        )
                    }

                    else -> {
                        binding.txtState.text = ContextCompat.getString(
                            binding.txtState.context,
                            R.string.state_day_ok
                        )
                        binding.txtState.setBackgroundColor(
                            ContextCompat.getColor(
                                binding.txtState.context,
                                R.color.text_state_day_ok
                            )
                        )
                    }
                }

            } else
                binding.txtState.text = ""
        }

        private fun setImageClient(uri: String) {
            if (uri != "null")
                if (uri != "") {
                    Glide.with(binding.imageProductItem.context)
                        .load(uri)
                        .into(binding.imageProductItem)
                }
        }

        @SuppressLint("SetTextI18n")
        fun setBirthday(date: String) {
            val dateBirthday = binding.txtBirthday
            if (date != "") {
                val birthday = date.split("/").toMutableList()
                when (birthday[1]) {
                    '1'.toString() -> birthday[1] = "Enero"
                    '2'.toString() -> birthday[1] = "Febrero"
                    '3'.toString() -> birthday[1] = "Marzo"
                    '4'.toString() -> birthday[1] = "Abril"
                    '5'.toString() -> birthday[1] = "Mayo"
                    '6'.toString() -> birthday[1] = "Junio"
                    '7'.toString() -> birthday[1] = "Julio"
                    '8'.toString() -> birthday[1] = "Agosto"
                    '9'.toString() -> birthday[1] = "Septiembre"
                    "10" -> birthday[1] = "Octubre"
                    "11" -> birthday[1] = "Noviembre"
                    "12" -> birthday[1] = "Diciembre"
                }
                dateBirthday.text = "${birthday[0]} de ${birthday[1]}"
            } else
                dateBirthday.text = date
        }
    }

    override fun getItemCount() = clientList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClientHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.item_client, parent, false)
        return ClientHolder(view)
    }

    override fun onBindViewHolder(holder: ClientHolder, position: Int) {
        holder.render(clientList[position], onItemsSelected)
    }

    fun updateList(listClient: MutableList<Clients>) {
        this.clientList = listClient
        notifyDataSetChanged()
    }
}
