package com.nan_app.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
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

        init {
            this.view = v
        }

        fun setName(name: String) {
            val txtName: TextView = view.findViewById(R.id.txtName)
            txtName.text = name
        }

        fun setLastName(lastname: String) {
            val txtLastName: TextView = view.findViewById(R.id.txtLastName)
            txtLastName.text = lastname
        }

        fun setId(id : String){
            val txtId : TextView = view.findViewById(R.id.txtId)
            txtId.text = id
        }

        fun setImageClient(uri : String){
            val imageClient : ImageView =view.findViewById(R.id.imageProductItem)
            if(uri != "null")
                if (uri != "")
                {
                    Glide.with(imageClient.context)
                    .load(uri)
                    .into(imageClient)
                }
        }

        fun setBirthday(date: String){
            val dateBirtday: TextView = view.findViewById(R.id.textBirthday)
            dateBirtday.text = date
        }

        fun setPayDay(date: String){
            val datePayDay: TextView = view.findViewById(R.id.textBirthday)
            datePayDay.text = date
        }

        fun setFinishDay(date: String){
            val dateFinishDay: TextView = view.findViewById(R.id.textBirthday)
            dateFinishDay.text = date
        }

        fun getCard(): CardView {
            return view.findViewById(R.id.cardImage)
        }

        fun getButtonDelete() : Button{
            return view.findViewById(R.id.btnDeleteClient)

        }
        fun getButtonEdit():Button{
            return view.findViewById(R.id.btnEditClient)
        }

        fun bind(client: Clients){
            setName(client.Name)
            setLastName(client.LastName)
            setId(client.id.toString())
            setImageClient(client.ImageUri)
            setBirthday(client.Birthday)
            setPayDay(client.PayDay)
            setFinishDay(client.FinishDay)
        }
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
