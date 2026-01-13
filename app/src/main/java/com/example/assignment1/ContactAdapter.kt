package com.example.assignment1

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.assignment1.databinding.ItemContactBinding

class ContactAdapter (
    private var contacts: List<ContactItem>,
    private val onUpdateClick : (ContactItem) -> Unit,
    private val onDeleteClick : (ContactItem) -> Unit
    ) : RecyclerView.Adapter<ContactAdapter.ContactViewHolder>(){

    inner class ContactViewHolder(val binding : ItemContactBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val binding = ItemContactBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ContactViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val contact = contacts[position]
        holder.binding.tvContactName.text = contact.name

        holder.binding.tvContactName.text = contact.name
        holder.binding.tvContactPhone.text = contact.phone

        holder.binding.btnUpdate.setOnClickListener { onUpdateClick(contact) }
        holder.binding.btnDelete.setOnClickListener { onDeleteClick(contact) }
    }

    override fun getItemCount() = contacts.size

    fun updateData(newList: List<ContactItem>){
        contacts = newList
        notifyDataSetChanged()
    }
    }
