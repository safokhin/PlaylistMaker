package com.practicum.playlistmaker

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.practicum.playlistmaker.api.ItunesApi
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchActivity : AppCompatActivity() {
    private val itunesBaseUrl = "https://itunes.apple.com"
    private val retrofit = Retrofit.Builder()
        .baseUrl(itunesBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val itunesApiService = retrofit.create(ItunesApi::class.java)
    private var textSearch: String = ""
    private var trackList = ArrayList<Track>()

    val trackAdapter = TrackAdapter(trackList)

    private lateinit var placeholderEmpty: View
    private lateinit var placeholderError: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        placeholderEmpty = findViewById<View>(R.id.placeholderEmpty)
        placeholderError = findViewById<View>(R.id.placeholderError)
        placeholderError.findViewById<Button>(R.id.placeholderErrorButton).setOnClickListener {
            findTrack(textSearch)
        }

        val buttonBack = findViewById<MaterialToolbar>(R.id.btn_back)
        val editTextSearch = findViewById<EditText>(R.id.edit_text_search)
        val buttonClear = findViewById<ImageView>(R.id.icon_clear)

        buttonBackHandler(buttonBack)
        buttonClearHandler(buttonClear, editTextSearch)
        textWatcherHandler(buttonClear, editTextSearch)

        val recyclerTrackList = findViewById<RecyclerView>(R.id.recyclerTrackList)
        recyclerTrackList.adapter = trackAdapter
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
    private fun buttonBackHandler(buttonBack: MaterialToolbar) {
        buttonBack.setNavigationOnClickListener {
            finish()
        }
    }

    /** Обработчик клика кнопки "Очистить поиск" */
    private fun buttonClearHandler(buttonClear: ImageView, editTextSearch: EditText) {
        buttonClear.setOnClickListener {
            editTextSearch.setText("")
            trackList.clear()
            placeholderEmpty.visibility = View.GONE
            placeholderError.visibility = View.GONE
            trackAdapter.notifyDataSetChanged()

            val inputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(editTextSearch.windowToken, 0)
        }
    }

    /** Обработчик изменения текста */
    private fun textWatcherHandler(buttonClear: ImageView, editTextSearch: EditText) {
        val textWatcherSearch = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                textSearch = s.toString().trim()
                buttonClear.visibility = clearButtonVisibility(s)
            }

            override fun afterTextChanged(s: Editable?) {}
        }

        editTextSearch.addTextChangedListener(textWatcherSearch)

        editTextSearch.setOnEditorActionListener { _, actionId, _ ->
            // Запрос отправляется только если поле не пустое (иначе не реагируем)
            if (actionId == EditorInfo.IME_ACTION_DONE && !textSearch.isEmpty()) {
                findTrack(textSearch)

                true
            }

            false
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

        itunesApiService.findTrack(str).enqueue(object: Callback<TrackResponse> {
            override fun onResponse(call: Call<TrackResponse>, response: Response<TrackResponse>) {
                trackList.clear()
                placeholderEmpty.visibility = View.GONE
                placeholderError.visibility = View.GONE

                if(response.code() == 200) {
                    if(response.body()?.results?.isNotEmpty() == true) {
                        trackList.addAll(response.body()?.results!!)
                    } else {
                        placeholderEmpty.visibility = View.VISIBLE
                    }

                } else {
                    placeholderError.visibility = View.VISIBLE
                }

                // Обновление списка всегда (успешный запрос -> ошибка)
                trackAdapter.notifyDataSetChanged()
            }

            override fun onFailure(call: Call<TrackResponse>, t: Throwable) {
                trackList.clear()
                placeholderEmpty.visibility = View.GONE
                placeholderError.visibility = View.VISIBLE

                trackAdapter.notifyDataSetChanged()
            }
        } )
    }

    companion object {
        const val TEXT_SEARCH = "TEXT_SEARCH"
    }
}


