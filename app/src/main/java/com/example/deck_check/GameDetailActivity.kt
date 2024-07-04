package com.example.deck_check

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.* // Per gestire in maniera asincrona le operazioni di accesso alla rete
                            // e di scraping senza bloccare il thread principale
import org.json.JSONObject // Per gestire l'attributo "application_config"
import org.jsoup.Jsoup // Per le funzioni di scraping
import java.io.IOException


class GameDetailActivity : AppCompatActivity() {

    // Elementi dell'interfaccia utente
    private lateinit var gameTitleTextView: TextView
    private lateinit var compatibilityTextView: TextView
    private lateinit var compatibilityStatusIcon: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_detail)

        // Inizializza le TextView e la ImageView che conterranno il titolo del gioco
        // e il grado di compatibilità con Steam Deck
        gameTitleTextView = findViewById(R.id.game_title_text_view)
        compatibilityTextView = findViewById(R.id.compatibility_text_view)
        compatibilityStatusIcon = findViewById(R.id.status_icon)
        // Tasto back per tornare alla MainActivity
        val backButton = findViewById<Button>(R.id.back_button)

        // Richiama il valore del gameId passato dalla MainActivity
        val gameId = intent.getStringExtra("game_id")

        // Chiamata alla funzione di scraping
        if (gameId != null) {
            scrapeGameInfo(gameId)
        } else {
            // Gestire in caso di crash dell'applicazione
        }

        // Alla pressione del tasto 'back' rilancia la MainActivity
        backButton.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java)).apply {}
            // Funzione per chiudere questa Activity e rimuoverla dallo stack dell'applicazione
            finish()
        }
    }

    // Funzione di web scraping
    private fun scrapeGameInfo(gameId: String) {
        // Avvia una coroutine al di fuori del thread principale che utilizza Dispatchers.IO
        // che è ottimizzato per eseguire operazioni come l'accesso ai file o le chiamate di rete
        // per evitare di bloccare il thread principale e causare la mancata risposta dell'app
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Imposta l'URL dinamico con il gameId
                val url = "https://store.steampowered.com/app/$gameId/"
                // Imposta lo user agent per evitare problemi di accesso alla pagina web
                val userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36"

                // Accede alla pagina web e ne ricava il file .html da analizzare
                val doc = Jsoup.connect(url).userAgent(userAgent).get()

                // Trova l'elemento con id "appHubAppName" che contiene il titolo del gioco
                val appTitleElement = doc.getElementById("appHubAppName")

                // Estrae il titolo del gioco impostando un valore di default nel caso
                // non riesca a trovare il titolo o il gameId sia errato
                val gameTitle = appTitleElement?.text() ?: "Game not found"

                // Trova l'elemento <div> con l'attributo "application_config"
                val appConfigElement = doc.getElementById("application_config")

                // Estrae il grado di compatibilità del gioco, gestendo eventuali eccezioni
                val deckVerifiedStatus = if (appConfigElement != null) {
                    // Seleziona l'attributo "data-deckcompatibility"
                    val dataDeckCompatibility = appConfigElement.attr("data-deckcompatibility")
                    if (dataDeckCompatibility.isNotEmpty()) {
                        try {
                            // Istanzia un Json per gestire le coppie chiave-valore
                            // dell'attributo "data-deckcompatibility"
                            val jsonDeckCompatibility = JSONObject(dataDeckCompatibility)
                            // Ricava il valore intero di "resolved_category"
                            // che indica il grado di compatibilità con Steam Deck
                            val compatibilityValue = jsonDeckCompatibility.getInt("resolved_category")
                            // In base al valore numerico estratto
                            // imposta il messaggio da mostrare a schermo
                            when (compatibilityValue) {
                                3 -> "Steam Deck Verified"
                                2 -> "Playable on Steam Deck"
                                1 -> "Not supported on Steam Deck"
                                else -> "Steam Deck compatibility unknown"
                            }
                        } catch (e: Exception) {
                            "Error parsing Deck compatibility information."
                        }
                    } else {
                        "Steam Deck compatibility unknown"
                    }
                } else {
                    "Steam Deck compatibility unknown"
                }

                // Aggiorna l'interfaccia utente nel thread principale
                withContext(Dispatchers.Main) {
                    gameTitleTextView.text = gameTitle
                    compatibilityTextView.text = deckVerifiedStatus
                    updateStatusIcon(deckVerifiedStatus)
                }

            }
            // Gestione delle eccezioni
            catch (e: IOException) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    // Mostra in Logcat le eventuali eccezioni
                    Log.e("Eccezione","Si è verificata un'eccezione:/n${e.message}")
                }
            }
        }
    }

    // Aggiorna l'icona di stato
    private fun updateStatusIcon(status: String) {
        when (status) {
            "Steam Deck Verified" -> compatibilityStatusIcon.setImageResource(R.drawable.verified)
            "Playable on Steam Deck" -> compatibilityStatusIcon.setImageResource(R.drawable.playable)
            "Not supported on Steam Deck" -> compatibilityStatusIcon.setImageResource(R.drawable.unsupported)
            //"Steam Deck compatibility unknown" -> compatibilityStatusIcon.setImageResource(R.drawable.unknown)
            else -> compatibilityStatusIcon.setImageResource(R.drawable.unknown)
        }
    }
}
