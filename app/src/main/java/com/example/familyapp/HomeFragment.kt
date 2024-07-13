package com.example.familyapp

import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeFragment : Fragment() {
    lateinit var inviteAdapter: InviteAdapter
    private val listContacts: ArrayList<ContactModel> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val listMembers = listOf<MemberModel>(
            MemberModel("Abhiyudaya",
                "9th building, 2nd floor, Maldiv road, Manali",
                "",
                ""),
            MemberModel("Saksham",
                "9th building, 3rd floor, Maldiv road, Manali",
                "",
                ""),
            MemberModel("Aniket",
                "9th building, 4th floor, Maldiv road, Manali",
                "",
                ""),
        )

        val adapter = MemberAdapter(listMembers)

        val recycler = requireView().findViewById<RecyclerView>(R.id.recycler_member)
        recycler.layoutManager = LinearLayoutManager(requireContext())
        recycler.adapter = adapter



        inviteAdapter = InviteAdapter(listContacts)
        fetchDatabaseContacts()

        CoroutineScope(Dispatchers.IO).launch{
            Log.d("FetchContact","Coroutine starts")

            insertDatabaseContacts(fetchContacts())
            
        }



        val inviteRecycler = requireView().findViewById<RecyclerView>(R.id.recycler_invite)
        inviteRecycler.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
        inviteRecycler.adapter = inviteAdapter

    }

    private fun fetchDatabaseContacts() {
        val database = FamilyDatabase.getDatabase(requireContext())

        database.ContactDao().getAllContacts().observe(viewLifecycleOwner){

            listContacts.clear()
            listContacts.addAll(it)

            inviteAdapter.notifyDataSetChanged()

        }
    }

    private suspend fun insertDatabaseContacts(listContacts: ArrayList<ContactModel>) {
        val database = FamilyDatabase.getDatabase(requireContext())

        database.ContactDao().insertAll(listContacts)

    }

    private fun fetchContacts(): ArrayList<ContactModel> {

        Log.d("FetchContact","fetchContacts: start")

        val cr = requireActivity().contentResolver
        val cursor = cr.query(ContactsContract.Contacts.CONTENT_URI,null,null,null,null)

        val listContacts:ArrayList<ContactModel> = ArrayList()

        if (cursor!=null && cursor.count>0){

            while (cursor!=null && cursor.moveToNext()){
                val id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID))
                val name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
                val hasPhoneNumber = cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))

                if (  hasPhoneNumber>0  ){
                    val pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID+" = ?",
                        arrayOf(id),
                        ""
                        )
                    if(pCur!=null && pCur.count>0){
                        while (pCur!=null && pCur.moveToNext()){
                            val phoneNumber = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                            listContacts.add(ContactModel(name,phoneNumber))
                        }
                        pCur.close()
                    }
                }
            }
            if (cursor!=null){
                cursor.close()
            }
        }
        Log.d("FetchContact","fetchContacts: ends")
        return listContacts
    }

    companion object {

        @JvmStatic
        fun newInstance() = HomeFragment()

    }
}