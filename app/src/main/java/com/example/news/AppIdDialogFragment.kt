package com.example.news

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import org.koin.core.component.KoinComponent

class AppIdDialogFragment : DialogFragment(), KoinComponent {

    companion object {
        const val REQUEST_KEY = "AppIdDialogFragmentResult"
        const val APP_ID = "app_id"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = inflater.inflate(R.layout.app_id_dialog_fragment, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        isCancelable = false

        view.findViewById<Button>(R.id.save).setOnClickListener {
            val contents = view.findViewById<EditText>(R.id.app_id_edittext).text.toString()
            if (contents.isNotEmpty()) {
                parentFragmentManager.setFragmentResult(
                    REQUEST_KEY,
                    Bundle().apply {
                        putString(APP_ID, contents)
                    }
                )

                dismiss()
            }
        }
    }
}
