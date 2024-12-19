package pl.makaron.zadanielistaludzi
import pl.makaron.zadanielistaludzi.Person
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import pl.makaron.zadanielistaludzi.R

class MainActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private val personListKey = "person_list"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sharedPreferences = getSharedPreferences("PersonPrefs", MODE_PRIVATE)

        val nameInput = findViewById<EditText>(R.id.nameInput)
        val surnameInput = findViewById<EditText>(R.id.surnameInput)
        val ageInput = findViewById<EditText>(R.id.ageInput)
        val heightInput = findViewById<EditText>(R.id.heightInput)
        val weightInput = findViewById<EditText>(R.id.weightInput)
        val saveButton = findViewById<Button>(R.id.saveButton)
        val listButton = findViewById<Button>(R.id.listButton)

        saveButton.setOnClickListener {
            val name = nameInput.text.toString().trim()
            val surname = surnameInput.text.toString().trim()
            val age = ageInput.text.toString().toIntOrNull()
            val height = heightInput.text.toString().toIntOrNull()
            val weight = weightInput.text.toString().toIntOrNull()

            if (validateInputs(name, surname, age, height, weight)) {
                val person = Person(name, surname, age!!, height!!, weight!!)
                savePerson(person)
                Toast.makeText(this, "Person saved successfully!", Toast.LENGTH_SHORT).show()
                clearInputs(nameInput, surnameInput, ageInput, heightInput, weightInput)
            }
        }

        listButton.setOnClickListener {
            val intent = Intent(this, SecondActivity::class.java)
            startActivity(intent)
        }
    }

    private fun validateInputs(name: String, surname: String, age: Int?, height: Int?, weight: Int?): Boolean {
        return when {
            name.isEmpty() || surname.isEmpty() -> {
                Toast.makeText(this, "Name and surname are required.", Toast.LENGTH_SHORT).show()
                false
            }
            age == null || age <= 0 -> {
                Toast.makeText(this, "Age must be greater than 0.", Toast.LENGTH_SHORT).show()
                false
            }
            height == null || height !in 50..250 -> {
                Toast.makeText(this, "Height must be between 50 and 250 cm.", Toast.LENGTH_SHORT).show()
                false
            }
            weight == null || weight !in 3..200 -> {
                Toast.makeText(this, "Weight must be between 3 and 200 kg.", Toast.LENGTH_SHORT).show()
                false
            }
            else -> true
        }
    }

    private fun savePerson(person: Person) {
        val editor = sharedPreferences.edit()
        val gson = Gson()

        val personListJson = sharedPreferences.getString(personListKey, null)
        val personList: MutableList<Person> = if (personListJson != null) {
            val type = object : TypeToken<MutableList<Person>>() {}.type
            gson.fromJson(personListJson, type)
        } else {
            mutableListOf()
        }

        personList.add(person)
        editor.putString(personListKey, gson.toJson(personList))
        editor.apply()
    }

    private fun clearInputs(vararg inputs: EditText) {
        inputs.forEach { it.text.clear() }
    }
}
