package com.nan_app.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
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

/*        fun setImageClient():{
            val imageClient : ImageView =
        }*/

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
//            setImageClient()
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
