package com.nan_app.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.RecyclerView
import com.nan_app.R
import com.nan_app.database.FirebaseDataClientSource
import com.nan_app.entities.Clients
import com.nan_app.entities.clientModule
import com.nan_app.fragments.home.HomeViewModel
import org.koin.java.KoinJavaComponent

class ClientAdapter(
    private var clientlist: MutableList<Clients>,
//    var onClick: (Int) -> Unit
    private val clikListener: ClientClickListener
): RecyclerView.Adapter<ClientAdapter.ClientHolder>() {

    class ClientHolder(v: View) : RecyclerView.ViewHolder(v) {
        private var view: View

        val clientSource: HomeViewModel by KoinJavaComponent.inject(
            HomeViewModel::class.java
        )
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

        fun getButtonDelete() : Button{
            return view.findViewById(R.id.buttonDelete)

        }

        fun bind(client: Clients){
            setName(client.Name)
            setLastName(client.LastName)
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
//            holder.getCard().setOnClickListener { onClick(position) }
//            holder.getButtonDelete().setOnClickListener {
//                onClick(position)
//                holder.clientSource.deleteClient(clientlist[position].id)
//            }
            holder.getCard().setOnClickListener{ clikListener.onCardClick(position)}
            holder.getButtonDelete().setOnClickListener {
                clikListener.onDeleteButtonClick(position)
            }
        }
    }
}
