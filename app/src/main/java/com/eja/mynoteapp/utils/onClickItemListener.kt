package com.eja.mynoteapp.utils

import com.eja.mynoteapp.model.ModelNote

interface onClickItemListener {
    fun onClick(modelNote: ModelNote, position: Int)
}