package com.practicum.playlistmaker

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
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
import com.practicum.playlistmaker.api.ItunesApi
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchActivity : AppCompatActivity() {
    private val gson = Gson()
    private val retrofit = Retrofit.Builder()
        .baseUrl(ITUNES_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val itunesApiService = retrofit.create(ItunesApi::class.java)

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

    private lateinit var history: SearchHistory
    private lateinit var historyListener: OnSharedPreferenceChangeListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        history = SearchHistory(this)

        buttonHistoryClear = findViewById(R.id.clearTrackListHistory)
        editTextSearch = findViewById(R.id.edit_text_search)
        buttonClear = findViewById(R.id.icon_clear)
        buttonBack = findViewById(R.id.btn_back)
        progressBarView = findViewById(R.id.progressBar)
        placeholderEmpty = findViewById(R.id.placeholderEmpty)
        placeholderError = findViewById(R.id.placeholderError)
        placeholderError.findViewById<Button>(R.id.placeholderErrorButton).setOnClickListener {
            findTrack(textSearch)
        }
        tracksHistoryView = findViewById(R.id.tracksHistory)

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
            history.clearTracks()
        }

        historyListener = OnSharedPreferenceChangeListener { sharedPreferences, key ->
            if(key == SearchHistory.SHARED_PREFS_KEY) {
                trackHistoryAdapter.setList(history.getTracks())
                visibleHistory()
            }
        }

        history.registerListener(historyListener)

        trackHistoryAdapter.setList(history.getTracks())
        visibleHistory()
    }

    override fun onDestroy() {
        super.onDestroy()

        history.unregisterListener(historyListener)
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
            history.addTracks(track)

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
        progressBarView.visibility = View.VISIBLE
        trackAdapter.setList(mutableListOf())

        itunesApiService.findTrack(str).enqueue(object: Callback<TrackResponse> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(call: Call<TrackResponse>, response: Response<TrackResponse>) {
                hiddenPlaceholders()
                progressBarView.visibility = View.GONE

                if(response.code() == 200) {
                    if(response.body()?.results?.isNotEmpty() == true) {
                        trackAdapter.setList(response.body()?.results!!)
                    } else {
                        showPlaceholderEmpty()
                    }

                } else {
                    showPlaceholderEmpty()
                }
            }

            @SuppressLint("NotifyDataSetChanged")
            override fun onFailure(call: Call<TrackResponse>, t: Throwable) {
                progressBarView.visibility = View.GONE
                showPlaceholderError()
            }
        } )
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
        if(history.getTracks().isNotEmpty() && trackAdapter.list.isEmpty() && !editTextSearch.isFocused) {
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


