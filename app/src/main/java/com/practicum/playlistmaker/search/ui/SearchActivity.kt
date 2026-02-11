package com.practicum.playlistmaker.search.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivitySearchBinding
import com.practicum.playlistmaker.player.ui.PlayerActivity
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.search.domain.models.SearchActivityState

class SearchActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySearchBinding

    private lateinit var viewModel: SearchViewModel

    private var isClickAllowed = true
    private val handler = Handler(Looper.getMainLooper())

    private var trackAdapter = TrackAdapter { selectTrackHandler(it) }
    private var trackHistoryAdapter = TrackAdapter { selectTrackHandler(it) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager

        viewModel = ViewModelProvider(this, SearchViewModel.getFactory())
            .get(SearchViewModel::class.java)

        viewModel.observeSearchActivity().observe(this) {
            renderActivity(it)
        }

        searchInputInit()

        // Обработчик клика кнопки "Назад"
        binding.btnBack.setNavigationOnClickListener { finish() }
        // Обработчик клика кнопки "Очистить поиск"
        binding.btnClear.setOnClickListener {
            binding.editTextSearch.setText("")
            // Закрытие клавиатуры
            inputMethodManager?.hideSoftInputFromWindow(binding.editTextSearch.windowToken, 0)
        }

        // Обработчик клика кнопки "Обновить"
        binding.trackError.placeholderErrorButton.setOnClickListener {
            viewModel.searchTrackDebounce(binding.editTextSearch.text.toString(), true)
        }


        binding.recyclerTrackList.adapter = trackAdapter

        binding.tracksHistory.recyclerTrackListHistory.adapter = trackHistoryAdapter
        binding.tracksHistory.clearTrackListHistory.setOnClickListener { viewModel.clearHistory() }

        // Фокусировка при открытии окна
        binding.editTextSearch.requestFocus()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onResume() {
        super.onResume()

        if (viewModel.observeSearchActivity().value !is SearchActivityState.Content) {
            viewModel.loadHistory()
        }
    }

    /** Обработчик клика при выборе трека */
    private fun selectTrackHandler(track: Track) {
        if (clickDebounce()) {
            viewModel.addHistory(track)

            // Переход на страницу плеера + передача информации
            val intent = Intent(this, PlayerActivity::class.java)
            intent.putExtra(PlayerActivity.EXTRA_TRACK_KEY, Gson().toJson(track))
            startActivity(intent)
        }
    }

    /** Подключение логики для поля "Поиск" */
    private fun searchInputInit() {
        val textWatcherSearch = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Отображение иконки очистки значения в поисковике
                binding.btnClear.isVisible = !s.isNullOrEmpty()

                val textSearch = s.toString().trim()

                // Если поле пустое, то происходит отображение истории. Иначе - запрос
                if(textSearch.isEmpty()) {
                    viewModel.searchTrackDebounce("") // Очистит latestSearchQuery
                    viewModel.loadHistory()
                } else {
                    viewModel.searchTrackDebounce(textSearch)
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        }

        binding.editTextSearch.addTextChangedListener(textWatcherSearch)
    }

    /** Отображение основного контента */
    private fun showContent(tracks: List<Track>) {
        binding.apply {
            progressBar.visibility = View.GONE
            trackEmpty.placeholderEmpty.visibility = View.GONE
            trackError.placeholderError.visibility = View.GONE
            tracksHistory.tracksHistory.visibility = View.GONE
            recyclerTrackList.visibility = View.VISIBLE
        }

        trackAdapter.setList(tracks)
    }

    /** Отображение истории треков */
    private fun showHistory(tracks: List<Track>) {
        binding.apply {
            progressBar.visibility = View.GONE
            trackEmpty.placeholderEmpty.visibility = View.GONE
            trackError.placeholderError.visibility = View.GONE
            tracksHistory.tracksHistory.visibility = View.VISIBLE
            recyclerTrackList.visibility = View.GONE
        }

        trackHistoryAdapter.setList(tracks)
    }

    private fun showLoader() {
        binding.apply {
            progressBar.visibility = View.VISIBLE
            trackEmpty.placeholderEmpty.visibility = View.GONE
            trackError.placeholderError.visibility = View.GONE
            tracksHistory.tracksHistory.visibility = View.GONE
            recyclerTrackList.visibility = View.GONE
        }
    }


    /** Отображение пустого списка */
    private fun showPlaceholderEmpty() {
        binding.apply {
            progressBar.visibility = View.GONE
            trackEmpty.placeholderEmpty.visibility = View.VISIBLE
            trackError.placeholderError.visibility = View.GONE
            tracksHistory.tracksHistory.visibility = View.GONE
            recyclerTrackList.visibility = View.GONE
        }
    }

    private fun showPlaceholderError() {
        binding.apply {
            progressBar.visibility = View.GONE
            trackEmpty.placeholderEmpty.visibility = View.GONE
            trackError.placeholderError.visibility = View.VISIBLE
            tracksHistory.tracksHistory.visibility = View.GONE
            recyclerTrackList.visibility = View.GONE
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

    /** Отрисовка состояний приложения */
    private fun renderActivity(state: SearchActivityState) {
        when(state) {
            is SearchActivityState.Content -> showContent(state.tracks)
            is SearchActivityState.History -> showHistory(state.tracks)
            is SearchActivityState.Loading -> showLoader()
            is SearchActivityState.Empty -> showPlaceholderEmpty()
            is SearchActivityState.Error -> showPlaceholderError()
        }
    }

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }
}