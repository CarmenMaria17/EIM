// declaratie functii

fun calculateSum(numbers: List<Int>): Int {
    return numbers.sum()
}

// data classes

data class Person(val name: String, val age: Int)

//Stilul functional ca first class citizen.

val numbers = listOf(1, 2, 3, 4, 5)
val evenNumbers = numbers.filter { it % 2 == 0 } // Output: [2, 4]

// null saftey

var name: String = "John" // Non-nullable string
// name = null // Compilation error: Null cannot be a value of a non-null type String

// smart casting

fun printStringLength(text: Any) {
    if (text is String) {
        println("The length of the string is: ${text.length}") // No need for explicit casting
    }
}
printStringLength("Hello, Kotlin!") // Output: "The length of the string is: 14"

// corutine in limbaj
fun main() = runBlocking {
    launch {
        delay(1000L)
        println("Sarcină în corutină: Salut din corutină!")
    }
    println("Sarcină în main: Aștept...")
    delay(2000L)
}



// LOGCAT
// Metodă Logcat	Nivel de prioritate	Descriere
// Log.v()	VERBOSE	Cel mai detaliat – toate mesajele, inclusiv debugging intern
// Log.d()	DEBUG	Pentru mesaje de depanare (testare, verificări de flux)
// Log.i()	INFO	Informații generale, starea aplicației
// Log.w()	WARN	Avertismente, posibile probleme
// Log.e()	ERROR	Erori grave, excepții, crash-uri
// Log.wtf()	ASSERT	Probleme critice („What a Terrible Failure”)


// LAB4 - intents:

// INTENT EXPLICIT - De exemplu, dacă vrem sa pornim o activitate SecondActivity la apasarea unui buton vom folosi urmatorul cod:

val btn = findViewById(R.id.open_activity_button)

btn.setOnClickListener { 
    startActivity(Intent(this@MainActivity, SecondActivity::class.java)) 
}

//Pentru a trimite date între activități folosim metodele putExtra() și getStringExtra() astfel:
val intent = Intent(this@MainActivity, SecondActivity::class.java)
intent.putExtra("EXTRA_MESSAGE", "Hello, Second Activity!")
startActivity(intent)

//În SecondActivity, pentru a primi datele trimise:
val message = intent.getStringExtra("EXTRA_MESSAGE")
textView.text = message

// INTENT IMPLICIT - De exemplu, pentru a deschide un link web folosim următorul cod:
val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.example.com"))
startActivity(webIntent)

//De exemplu, dacă ai conținut pe care vrei să-l partajezi cu alte persoane, creează un intent cu acțiunea ACTION_SEND
//și adaugă extra-uri care specifică conținutul de partajat. Atunci când apelezi `startActivity()`` cu acest intent, 
//utilizatorul poate selecta o aplicație prin intermediul căreia să partajeze conținutul.

val sendIntent = Intent().apply {
    action = Intent.ACTION_SEND
    // Date pe care vrem sa le trimitem catre activitatea pe care urmeaza sa o pornim
    val textMessage = "Salut! Uite un mesaj pe care vreau să-l împărtășesc cu tine."
    putExtra(Intent.EXTRA_TEXT, textMessage)
    type = "text/plain"
}

// Try to invoke the intent.
try {
    startActivity(sendIntent)
} catch (e: ActivityNotFoundException) {
    // Define what your app should do if no activity can handle the intent.
    Toast.makeText(this, "Nu există nicio aplicație disponibilă pentru a partaja acest conținut.", Toast.LENGTH_SHORT).show()
}

// Transmiterea de informații între componente prin intermediul intențiilor

// Obținerea valorii secțiunii extra corespunzătoare unei intenții poate fi obținute folosind metoda getExtras(),
//în timp ce specificarea unor informații care vor fi asociate unei intenții poate fi realizată printr-un apel al metodei putExtras().

val intent = Intent(this, AnotherActivity::class.java)
intent.putExtra("username", "john_doe")
intent.putExtra("age", 30)
startActivity(intent)

// În AnotherActivity, pentru a primi datele trimise:
val username = intent.getStringExtra("username")
val age = intent.getIntExtra("age", 0) // 0 este valoarea implicită dacă "age" nu este găsit
textView.text = "Username: $username, Age: $age"

//Acest lucru se realizează prin lansarea în execuție a activității copil prin intermediul metodei startActivityForResult().
//În momentul în care este finalizată, va fi invocată automat metoda onActivityResult() de la nivelul activității părinte.
private const val ANOTHER_ACTIVITY_REQUEST_CODE = 2017

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btn = findViewById<Button>(R.id.open_activity_button)
        btn.setOnClickListener {
            val intent = Intent(this, SecondActivity::class.java)
            intent.putExtra("some_key", someValue)
            // Pornim intentul și adăugăm un cod de cerere pentru a filtra în onActivityResult
            startActivityForResult(intent, ANOTHER_ACTIVITY_REQUEST_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            ANOTHER_ACTIVITY_REQUEST_CODE -> {
                if (resultCode == Activity.RESULT_OK) {
                    val bundle = data?.extras
                    // procesează datele din bundle aici
                }
            }
            // alte coduri de cerere pot fi tratate aici
        }
    }
}

// În SecondActivity, pentru a trimite datele înapoi:
class SecondActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.second_activity)

        // Preluăm intentul de la activitatea părinte
        val intentFromParent = intent
        val data = intentFromParent.extras
        // 'data' conține acum "some_key", pe care îl putem folosi

        // Returnăm niște date către activitatea care a pornit această activitate
        val intentToParent = Intent()
        intentToParent.putExtra("another_key", anotherValue)
        setResult(Activity.RESULT_OK, intentToParent)
        finish()
    }
}

// ResultsLauncher API
class MainActivity : AppCompatActivity() {
    // Definește un ActivityResultLauncher
    private lateinit var startForResult: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inițializează launcher-ul cu un callback pentru rezultat
        startForResult = registerForActivityResult(StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // Procesează rezultatul aici
                val data: Intent? = result.data
                val someData = data?.getStringExtra("another_key")
                // Folosește `someData` cum este necesar
            }
        }

        val btn = findViewById<Button>(R.id.open_activity_button)
        btn.setOnClickListener {
            val intent = Intent(this, SecondActivity::class.java).apply {
                putExtra("some_key", "someValue")
            }
            // Lansează activitatea copil folosind launcher-ul
            startForResult.launch(intent)
        }
    }
}

// in second activity pentru a trimite datele înapoi:
class SecondActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.second_activity)

        // Presupunând că vrei să returnezi rezultatul la un anumit eveniment, de exemplu, la apăsarea unui buton
        val someButton: Button = findViewById(R.id.some_button)
        someButton.setOnClickListener {
            val returnIntent = Intent().apply {
                putExtra("another_key", "anotherValue")
            }
            setResult(RESULT_OK, returnIntent)
            finish()
        }

        // Dacă activitatea se încheie fără a seta explicit un rezultat, poți să nu faci nimic sau să setezi RESULT_CANCELED
    }
}


