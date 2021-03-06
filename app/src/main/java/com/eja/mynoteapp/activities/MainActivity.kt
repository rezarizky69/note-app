package com.eja.mynoteapp.activities

import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.eja.mynoteapp.R
import com.eja.mynoteapp.adapter.NoteAdapter
import com.eja.mynoteapp.database.NoteDatabase
import com.eja.mynoteapp.model.ModelNote
import com.eja.mynoteapp.utils.onClickItemListener
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), onClickItemListener {

    private val modelNoteList: MutableList<ModelNote> = ArrayList()
    private var noteAdapter: NoteAdapter? = null
    private var onClickPosition = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)
        assert(supportActionBar != null)

        fabCreateNote.setOnClickListener {
            startActivityForResult(Intent(this, CreateNoteActivity::class.java), REQUEST_ADD)
        }

        noteAdapter = NoteAdapter(modelNoteList, this)
        rvListNote.adapter = noteAdapter

        //change mode List to Grid
        modeGrid()

        //get Data Note
        getNote(REQUEST_SHOW, false)

    }

    private fun modeGrid() {
        rvListNote.layoutManager =
            StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
    }

    private fun modeList() {
        rvListNote.layoutManager = LinearLayoutManager(this)
    }

    private fun getNote(requestCode: Int, deleteNote: Boolean) {

        @Suppress("UNCHECKED_CAST")
        class GetNoteAsyncTask : AsyncTask<Void?, Void?, List<ModelNote>>() {
            override fun doInBackground(vararg params: Void?): List<ModelNote>? {
                return NoteDatabase.getInstance(this@MainActivity)
                    ?.noteDao()?.allNote as List<ModelNote>?
            }

            override fun onPostExecute(notes: List<ModelNote>) {
                super.onPostExecute(notes)
                if (requestCode == REQUEST_SHOW) {
                    modelNoteList.addAll(notes)
                    noteAdapter?.notifyDataSetChanged()
                } else if (requestCode == REQUEST_ADD) {
                    modelNoteList.add(0, notes[0])
                    noteAdapter?.notifyItemInserted(0)
                    rvListNote.smoothScrollToPosition(0)
                } else if (requestCode == REQUEST_UPDATE) {
                    modelNoteList.removeAt(onClickPosition)
                    if (deleteNote) {
                        noteAdapter?.notifyItemRemoved(onClickPosition)
                    } else {
                        modelNoteList.add(onClickPosition, notes[onClickPosition])
                        noteAdapter?.notifyItemChanged(onClickPosition)
                    }
                }
            }
        }
        GetNoteAsyncTask().execute()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_ADD && resultCode == RESULT_OK) {
            getNote(REQUEST_ADD, false)
        } else if (requestCode == REQUEST_UPDATE && requestCode == RESULT_OK) {
            if (data != null) {
                getNote(REQUEST_UPDATE, data.getBooleanExtra("NoteDelete", false))
            }
        }
    }

    override fun onClick(modelNote: ModelNote, position: Int) {
        onClickPosition = position
        val intent = Intent(this, CreateNoteActivity::class.java)
        intent.putExtra("EXTRA", true)
        intent.putExtra("EXTRA_NOTE", modelNote)
        startActivityForResult(intent, REQUEST_UPDATE)
    }

    companion object {
        private const val REQUEST_ADD = 1
        private const val REQUEST_UPDATE = 2
        private const val REQUEST_SHOW = 3
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.listView -> modeList()
            R.id.gridView -> modeGrid()
        }
        return super.onOptionsItemSelected(item)
    }
}