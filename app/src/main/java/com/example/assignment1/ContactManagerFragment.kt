package com.example.assignment1

import android.content.ContentUris
import android.content.ContentValues
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.assignment1.databinding.FragmentContactManagerBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class ContactItem(val id: Long, val name: String, val phone: String, val uri: Uri)

class ContactManagerFragment : Fragment() {
    private var _binding : FragmentContactManagerBinding? = null
    private val binding get() = _binding!!

    private  lateinit var contactAdapter: ContactAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentContactManagerBinding.inflate(inflater,container,false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        refreshList()

        //--CREATE--
        binding.btnAdd.setOnClickListener {
            val name = binding.etName.text.toString()
            val phone = binding.etPhone.text.toString()

            if(name.isNotEmpty() && phone.isNotEmpty()){
                addContact(name,phone) //Insert logic

                binding.etName.text.clear()
                binding.etPhone.text.clear()
                refreshList()
            }
        }
    }
    private fun setupRecyclerView(){
        contactAdapter = ContactAdapter(
            contacts = emptyList(),
            onUpdateClick = {contact ->
                // UPDATE: Uses text currently in the Name field
                val newName = binding.etName.text.toString()
                if(newName.isNotEmpty()){
                    updateContactName(contact.id,newName)
                    refreshList()
                } else {
                    Toast.makeText(context,"Enter a name first" , Toast.LENGTH_SHORT).show()
                }
            },
            onDeleteClick = {contact ->
                //DELETE" uses the unique URI
                deleteContact(contact.uri)
                refreshList()
            }
        )
        binding.rvContacts.layoutManager = LinearLayoutManager(requireContext())
        binding.rvContacts.adapter = contactAdapter
    }


    //Helper to run READ operation in bg, when the list is updated or delete or written, the whole list will be called
    private fun refreshList(){
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO){
            val contacts = getAllContacts() // Query logic of ContentResolver
            withContext(Dispatchers.Main){
                contactAdapter.updateData(contacts)
            }
        }

    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // to avoid memory leaks
    }

    //Now the original Game of ContentResolver function begins
    // --- 1. READ (The 'R' in CRUD) ---
    // This method uses the 'query' method to find contacts
    private fun getAllContacts( ) : List<ContactItem> {
        val list = mutableListOf<ContactItem>()
        val uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI
        val projection = arrayOf(
            ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER
        )
        requireContext().contentResolver.query(uri,projection,null,null,null)?.use{
            cursor ->
            val idIdx = cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.CONTACT_ID)
            val nameIdx = cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
            val  numIdx = cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER)

            while(cursor.moveToNext()){
                val id = cursor.getLong(idIdx)
                val name = cursor.getString(nameIdx)
                val phone = cursor.getString(numIdx)
                //Creating specific URI for this person so that we can update/delete later

                val personUri = ContentUris.withAppendedId(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,id)
                list.add(ContactItem(id, name, phone, uri))
            }
        }
        return list
    }
    // --- 2. CREATE (The 'C' in CRUD) ---
    // For Contacts, "insert" is complex because they live in multiple tables.
    // A simpler way is using the Intent, but here is the raw ContentResolver logic:
    private fun addContact(name:String,phone:String){
        val values = ContentValues().apply {
            put(ContactsContract.RawContacts.ACCOUNT_TYPE,null as String?)
            put(ContactsContract.RawContacts.ACCOUNT_NAME,null as String?)
        }
        //Insert into RawContacts to get an ID
        val rawContactUri = requireContext().contentResolver.insert(ContactsContract.RawContacts.CONTENT_URI,values)
        val rawContactId = ContentUris.parseId(rawContactUri!!)

        //Add the Name
        val nameValues = ContentValues().apply {
            put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId)
            put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
            put(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME,name)
        }
        requireContext().contentResolver.insert(ContactsContract.Data.CONTENT_URI,nameValues)

        //Add the phone number
        val phoneValues = ContentValues().apply {
            put(ContactsContract.Data.RAW_CONTACT_ID,rawContactId)
            put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
            put(ContactsContract.CommonDataKinds.Phone.NUMBER,phone)
        }
        requireContext().contentResolver.insert(ContactsContract.Data.CONTENT_URI,phoneValues)
    }

    //For Update
    private  fun updateContactName(contactId : Long ,name:String){

    }
    //For Delete
    private fun deleteContact(contactUri : Uri) {}
}