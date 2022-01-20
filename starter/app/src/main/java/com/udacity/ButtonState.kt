package com.udacity


sealed class ButtonState {
    object Clicked : ButtonState() // clicked for downloading
    object Loading : ButtonState() // downloading in progress
    object Completed : ButtonState() // downloading is finished
}