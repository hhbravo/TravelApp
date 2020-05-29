package com.deved.myepxinperu.ui.common

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlin.properties.Delegates

@Suppress("UNCHECKED_CAST")
inline fun <reified T : ViewModel> FragmentActivity.getViewModel(crossinline factory: () -> T): T {

    val vmFactory = object : ViewModelProvider.Factory {
        override fun <U : ViewModel> create(modelClass: Class<U>): U = factory() as U
    }

    return ViewModelProvider(this, vmFactory).get()
}

@Suppress("UNCHECKED_CAST")
inline fun <reified T : ViewModel> Fragment.getViewModel(crossinline factory: () -> T): T {

    val vmFactory = object : ViewModelProvider.Factory {
        override fun <U : ViewModel> create(modelClass: Class<U>): U = factory() as U
    }

    return ViewModelProvider(this, vmFactory).get()
}


fun Context.toast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

inline fun <VH : RecyclerView.ViewHolder, T> RecyclerView.Adapter<VH>.basicDiffUtil(
    initialValue: List<T>,
    crossinline areItemsTheSame: (T, T) -> Boolean = { old, new -> old == new },
    crossinline areContentsTheSame: (T, T) -> Boolean = { old, new -> old == new }
) = Delegates.observable(initialValue) { _, old, new ->
    DiffUtil.calculateDiff(object : DiffUtil.Callback() {
        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
            areItemsTheSame(old[oldItemPosition], new[newItemPosition])

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
            areContentsTheSame(old[oldItemPosition], new[newItemPosition])

        override fun getOldListSize(): Int = old.size

        override fun getNewListSize(): Int = new.size
    }).dispatchUpdatesTo(this@basicDiffUtil)
}

fun ViewGroup.inflate(@LayoutRes layoutRes: Int, attachToRoot: Boolean = true): View =
    LayoutInflater.from(context).inflate(layoutRes, this, attachToRoot)

fun String.validate():Boolean{
    if(this.trim().isEmpty()){
        return false
    }
    return true
}

fun String.validateEmail(value:String):Boolean{
    if(value.trim().isEmpty()){
        return false
    }
    return true
}

fun TextInputLayout.validateInput(value: TextInputEditText, message: String): Boolean {
    this.apply { isErrorEnabled = false }
    if (value.text.isNullOrEmpty()) {
        this.apply { error = message; isErrorEnabled = true }
        return false
    }
    return true
}

fun TextInputLayout.validateInputPasword(password: TextInputEditText,repeatPassword : TextInputEditText, message: String): Boolean {
    this.apply { isErrorEnabled = false }
    val password =  password.text?.trim()
    val newPassword = repeatPassword.text?.trim()
    if (requireNotNull(password).equals(newPassword)) {
        this.apply { error = message; isErrorEnabled = true }
        return false
    }
    return true
}