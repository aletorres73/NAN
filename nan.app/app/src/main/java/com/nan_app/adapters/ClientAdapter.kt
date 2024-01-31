package com.nan_app.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.nan_app.R
import com.nan_app.entities.Clients

class ClientAdapter(
    private var clientlist: MutableList<Clients>,
    var onClick: (Int) -> Unit
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

        fun getCard(): CardView {
            return view.findViewById(R.id.cardImage)
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
            holder.setName(clientlist[position].Name)
            holder.setLastName(clientlist[position].LastName)
            holder.getCard().setOnClickListener { onClick(position) }
        }
    }
}
