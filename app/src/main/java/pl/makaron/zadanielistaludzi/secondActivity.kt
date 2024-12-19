package pl.makaron.zadanielistaludzi

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import pl.makaron.zadanielistaludzi.Person
import pl.makaron.zadanielistaludzi.R

class SecondActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private val personListKey = "person_list"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)

        sharedPreferences = getSharedPreferences("PersonPrefs", MODE_PRIVATE)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val personList = loadPersons()
        val adapter = PersonAdapter(personList) { person ->
            deletePerson(person)
        }
        recyclerView.adapter = adapter
    }

    private fun loadPersons(): MutableList<Person> {
        val gson = Gson()
        val personListJson = sharedPreferences.getString(personListKey, null)
        return if (personListJson != null) {
            val type = object : TypeToken<MutableList<Person>>() {}.type
            gson.fromJson(personListJson, type)
        } else {
            mutableListOf()
        }
    }

    private fun deletePerson(person: Person) {
        val gson = Gson()
        val personList = loadPersons()
        if (personList.remove(person)) {
            val editor = sharedPreferences.edit()
            editor.putString(personListKey, gson.toJson(personList))
            editor.apply()
            Toast.makeText(this, "Person removed successfully!", Toast.LENGTH_SHORT).show()
        }
    }

    inner class PersonAdapter(
        private val personList: MutableList<Person>,
        private val onDeleteClick: (Person) -> Unit
    ) : RecyclerView.Adapter<PersonAdapter.PersonViewHolder>() {

        inner class PersonViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val nameTextView: TextView = itemView.findViewById(R.id.nameTextView)
            val detailsTextView: TextView = itemView.findViewById(R.id.detailsTextView)
            val deleteButton: Button = itemView.findViewById(R.id.deleteButton)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PersonViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.person_item, parent, false)
            return PersonViewHolder(view)
        }

        override fun onBindViewHolder(holder: PersonViewHolder, position: Int) {
            val person = personList[position]
            holder.nameTextView.text = "${person.name} ${person.surname}"
            holder.detailsTextView.text = "Age: ${person.age}, Height: ${person.height} cm, Weight: ${person.weight} kg"

            holder.deleteButton.setOnClickListener {
                onDeleteClick(person)
                personList.removeAt(position)
                notifyItemRemoved(position)
            }
        }

        override fun getItemCount(): Int = personList.size
    }
}
