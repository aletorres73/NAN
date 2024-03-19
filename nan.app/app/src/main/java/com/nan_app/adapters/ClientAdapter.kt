package com.nan_app.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.nan_app.R
import com.nan_app.entities.Clients

class ClientAdapter(
    private var clientlist: MutableList<Clients>,
    private val clikListener: ClientClickListener
): RecyclerView.Adapter<ClientAdapter.ClientHolder>() {

    class ClientHolder(v: View) : RecyclerView.ViewHolder(v) {
        private var view: View
        init { this.view = v }
        private fun setName(name: String) {
            val txtName: TextView = view.findViewById(R.id.txtName)
            txtName.text = name }
        fun setLastName(lastname: String) {
            val txtLastName: TextView = view.findViewById(R.id.txtLastName)
            txtLastName.text = lastname }
        private fun setId(id : String){
            val txtId : TextView = view.findViewById(R.id.txtId)
            txtId.text = id }
        private fun setImageClient(uri : String){
            val imageClient : ImageView =view.findViewById(R.id.imageProductItem)
            if(uri != "null")
                if (uri != "")
                {
                    Glide.with(imageClient.context)
                    .load(uri)
                    .into(imageClient)
                }
        }

        @SuppressLint("SetTextI18n")
        fun setBirthday(date: String){
            val dateBirthday: TextView = view.findViewById(R.id.textBirthday)
            if(date != "")
            {
                val birthday = date.split("/").toMutableList()
                when (birthday[1]){
                    '1'.toString() -> birthday[1] = "Enero"
                    '2'.toString() -> birthday[1] = "Febrero"
                    '3'.toString() -> birthday[1] = "Marzo"
                    '4'.toString() -> birthday[1] = "Abril"
                    '5'.toString() -> birthday[1] = "Mayo"
                    '6'.toString() -> birthday[1] = "Junio"
                    '7'.toString() -> birthday[1] = "Julio"
                    '8'.toString() -> birthday[1] = "Agosto"
                    '9'.toString() -> birthday[1] = "Septiembre"
                    "10"-> birthday[2] = "Octubre"
                    "11"-> birthday[2] = "Noviembre"
                    "12"-> birthday[2] = "Diciembre" }
                dateBirthday.text = "${birthday[0]} de ${birthday[1]}"
            }
            else
                dateBirthday.text = date
        }

        private fun setPayDay(date: String){
            val datePayDay: TextView = view.findViewById(R.id.txtPayDay)
            datePayDay.text = date }

        private fun setFinishDay(date: String){
            val dateFinishDay: TextView = view.findViewById(R.id.txtFinishDay)
            dateFinishDay.text = date }

        fun getCard(): CardView {
            return view.findViewById(R.id.cardImage)}

        fun getButtonDelete() : Button{
            return view.findViewById(R.id.btnDeleteClient)}
        fun getButtonEdit():Button{
            return view.findViewById(R.id.btnEditClient)}

        fun bind(client: Clients){
            setName(client.Name)
            setLastName(client.LastName)
            setId(client.id.toString())
            setImageClient(client.ImageUri)
            setBirthday(client.Birthday)
            setPayDay(client.PayDay)
            setFinishDay(client.FinishDay) }
    }

    override fun getItemCount(): Int = clientlist.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClientHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.item_client, parent, false)
        return ClientHolder(view)
    }

    override fun onBindViewHolder(holder: ClientHolder, position: Int) {
        if (position < (itemCount + 1)) {
            holder.bind(clientlist[position])
//            holder.getCard().setOnClickListener{ clikListener.onCardClick(position)}
            holder.getButtonDelete().setOnClickListener {
                clikListener.onDeleteButtonClick(position)
            }
            holder.getButtonEdit().setOnClickListener {
                clikListener.onEditButtonClick(position)
            }
        }
    }
}
