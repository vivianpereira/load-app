package com.udacity


sealed class ButtonState {
    object Loading : ButtonState() // downloading in progress
    object Completed : ButtonState() // downloading is finished
}