package com.example.deck_check

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Instanzia e inizializza il tasto di ricerca
        val searchButton = findViewById<Button>(R.id.search_button)
        // Inizializza la barra di ricerca
        val gameIdInput = findViewById<EditText>(R.id.game_id_input)

        // Alla pressione del tasto di ricerca ricava il gameId inserito
        // e lo passa alla GameDetailActivity
        searchButton.setOnClickListener {
            // Elimina eventuali spazi vuoti
            val gameId = gameIdInput.text.toString().trim()
            if (gameId.isNotEmpty()) {
                startActivity(Intent(this, GameDetailActivity::class.java).putExtra("game_id", gameId))
                // Funzione per chiudere questa Activity e rimuoverla dallo stack dell'applicazione
                finish()
            } else {
                // Gestire in caso di crash dell'applicazione
            }
        }
    }
}