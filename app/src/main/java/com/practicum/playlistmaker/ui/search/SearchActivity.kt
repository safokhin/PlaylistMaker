package com.practicum.playlistmaker.ui.search

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.google.gson.Gson
import com.practicum.playlistmaker.Creator
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.domain.api.TracksHistoryInteractor
import com.practicum.playlistmaker.domain.api.TracksSearchInteractor
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.ui.player.PlayerActivity

class SearchActivity : AppCompatActivity() {
    private val gson = Gson()

    private var isClickAllowed = true
    private val handler = Handler(Looper.getMainLooper())

    private lateinit var progressBarView: ProgressBar
    private lateinit var placeholderEmpty: View
    private lateinit var placeholderError: View
    private lateinit var tracksHistoryView: View
    private lateinit var buttonBack: MaterialToolbar
    private lateinit var editTextSearch: EditText
    private lateinit var buttonClear: ImageView
    private lateinit var buttonHistoryClear: Button

    private var textSearch: String = ""

    private lateinit var trackAdapter: TrackAdapter
    private lateinit var trackHistoryAdapter: TrackAdapter

    private lateinit var tracksSearchInteractor: TracksSearchInteractor
    private lateinit var tracksHistoryInteractor: TracksHistoryInteractor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        tracksSearchInteractor = Creator.provideTracksSearchInteractor()
        tracksHistoryInteractor = Creator.provideTracksHistoryInteractor(this)

        initViews()

        placeholderError.findViewById<Button>(R.id.placeholderErrorButton).setOnClickListener {
            findTrack(textSearch)
        }

        // Фокусировка при открытии окна
        editTextSearch.requestFocus()

        textWatcherInit()
        buttonBackHandler()
        buttonClearHandler()

        val recyclerTrackList = findViewById<RecyclerView>(R.id.recyclerTrackList)
        trackAdapter = TrackAdapter(mutableListOf()) { track ->
            selectTrackHandler(track)
        }
        recyclerTrackList.adapter = trackAdapter

        // История поиска
        val recyclerTrackListHistory = findViewById<RecyclerView>(R.id.recyclerTrackListHistory)
        trackHistoryAdapter = TrackAdapter(mutableListOf()) { track ->
            selectTrackHandler(track)

        }
        recyclerTrackListHistory.adapter = trackHistoryAdapter

        buttonHistoryClear.setOnClickListener {
            tracksHistoryInteractor.clearHistory()
            visibleHistory()
        }

        trackHistoryAdapter.setList(tracksHistoryInteractor.getHistory())
        visibleHistory()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(TEXT_SEARCH, textSearch)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)

        val textSearchVal = savedInstanceState.getString(TEXT_SEARCH)
        val editTextSearch = findViewById<EditText>(R.id.edit_text_search)

        editTextSearch.setText(textSearchVal)
    }

    private fun initViews() {
        buttonHistoryClear = findViewById(R.id.clearTrackListHistory)
        editTextSearch = findViewById(R.id.edit_text_search)
        buttonClear = findViewById(R.id.icon_clear)
        buttonBack = findViewById(R.id.btn_back)
        progressBarView = findViewById(R.id.progressBar)
        placeholderEmpty = findViewById(R.id.placeholderEmpty)
        placeholderError = findViewById(R.id.placeholderError)
        tracksHistoryView = findViewById(R.id.tracksHistory)
    }

    /** Обработчик клика кнопки "Назад" */
    private fun buttonBackHandler() {
        buttonBack.setNavigationOnClickListener { finish() }
    }

    /** Обработчик клика кнопки "Очистить поиск" */
    @SuppressLint("NotifyDataSetChanged")
    private fun buttonClearHandler() {
        buttonClear.setOnClickListener {
            editTextSearch.setText("")
            hiddenPlaceholders()
            trackAdapter.setList(mutableListOf())
            editTextSearch.clearFocus()

            val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(editTextSearch.windowToken, 0)
        }
    }

    /** Обработчик клика при выборе трека */
    private fun selectTrackHandler(track: Track) {
        if (clickDebounce()) {
            // Добавление трека в историю
            tracksHistoryInteractor.addHistory(track)

            // Переход на страницу плеера + передача информации
            val intent = Intent(this, PlayerActivity::class.java)
            intent.putExtra(PlayerActivity.EXTRA_TRACK_KEY, gson.toJson(track))
            startActivity(intent)
        }
    }

    /** Подключение логики для поля "Поиск" */
    private fun textWatcherInit() {
        val handler = Handler(Looper.getMainLooper())
        val searchRunnable = Runnable { findTrack(textSearch) }
        
        val textWatcherSearch = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                textSearch = s.toString().trim()
                buttonClear.visibility = clearButtonVisibility(s)

                // Запрос отправляется только если поле не пустое (иначе не реагируем)
                if(!textSearch.isEmpty()) {
                    handler.removeCallbacks(searchRunnable)
                    handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        }

        editTextSearch.addTextChangedListener(textWatcherSearch)


        editTextSearch.setOnFocusChangeListener { view, hasFocus ->
            visibleHistory()
        }
    }

    /** Отображение EditText */
    private fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrBlank()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    /** Поиск треков */
    private fun findTrack(str: String) {
        hiddenPlaceholders()
        progressBarView.visibility = View.VISIBLE
        trackAdapter.setList(emptyList())

        tracksSearchInteractor.searchTracks(str) { result ->
            progressBarView.visibility = View.GONE

            result.onSuccess { tracks ->
                if(tracks.isEmpty()) {
                    showPlaceholderEmpty()
                } else {
                    trackAdapter.setList(tracks)
                }
            }.onFailure {
                showPlaceholderError()
            }
        }
    }

    private fun showPlaceholderEmpty() {
        placeholderEmpty.visibility = View.VISIBLE
        placeholderError.visibility = View.GONE
    }

    private fun showPlaceholderError() {
        placeholderEmpty.visibility = View.GONE
        placeholderError.visibility = View.VISIBLE
    }

    private fun hiddenPlaceholders() {
        placeholderEmpty.visibility = View.GONE
        placeholderError.visibility = View.GONE
    }

    private fun visibleHistory() {
        if(tracksHistoryInteractor.getHistory().isNotEmpty() && trackAdapter.list.isEmpty() && !editTextSearch.isFocused) {
            trackHistoryAdapter.setList(tracksHistoryInteractor.getHistory())
            tracksHistoryView.visibility = View.VISIBLE
        } else {
            tracksHistoryView.visibility = View.GONE
        }
    }

    private fun clickDebounce() : Boolean {
        val current = isClickAllowed

        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private const val CLICK_DEBOUNCE_DELAY = 1000L

        const val TEXT_SEARCH = "TEXT_SEARCH"
    }
}