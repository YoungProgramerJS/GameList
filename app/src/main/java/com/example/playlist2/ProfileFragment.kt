package com.example.playlist2

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.activity.result.contract.ActivityResultContracts

class ProfileFragment : Fragment() {

    private lateinit var profileImage: ImageView
    private lateinit var darkOverlay: View
    private lateinit var editText: TextView
    private lateinit var usernameText: TextView
    private lateinit var editUsernameField: EditText
    private lateinit var descriptionText: TextView
    private lateinit var descriptionEditField: EditText
    private lateinit var editButton: Button
    private var isEditingMode = false // Flaga trybu edycji

    // Rejestrator wyboru zdjęcia
    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            val bitmap: Bitmap = MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, it)
            profileImage.setImageBitmap(bitmap)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Powiąż elementy UI
        profileImage = view.findViewById(R.id.profileImage)
        darkOverlay = view.findViewById(R.id.darkOverlay)
        editText = view.findViewById(R.id.editText)
        usernameText = view.findViewById(R.id.username)
        editButton = view.findViewById(R.id.editButton)
        descriptionText = view.findViewById(R.id.description)
        descriptionEditField = view.findViewById(R.id.descriptionEditField)
        editUsernameField = view.findViewById(R.id.editUsernameField)

        // Obsługa kliknięcia na avatar tylko w trybie edycji
        val avatarContainer = view.findViewById<FrameLayout>(R.id.avatarContainer)
        avatarContainer.setOnClickListener {
            if (isEditingMode) {
                openGallery() // Otwórz galerię w trybie edycji
            }
        }

        // Obsługa przycisku Edit
        editButton.setOnClickListener {
            toggleEditMode()
        }

        // Obsługa kliknięcia w napis "Edit" w trybie edycji
        editText.setOnClickListener {
            if (isEditingMode) {
                openGallery() // Otwórz galerię po kliknięciu w napis Edit
            }
        }

        // Domyślne ukrycie przyciemnionego tła i napisu "Edit"
        darkOverlay.visibility = View.GONE
        editText.visibility = View.GONE
    }

    // Przełączenie trybu edycji
    private fun toggleEditMode() {
        isEditingMode = !isEditingMode
        if (isEditingMode) {
            // Włącz tryb edycji
            usernameText.visibility = View.GONE
            editUsernameField.visibility = View.VISIBLE
            editUsernameField.setText(usernameText.text)

            descriptionText.visibility = View.GONE
            descriptionEditField.visibility = View.VISIBLE
            descriptionEditField.setText(descriptionText.text)

            editButton.text = "Save"

            // Pokaż przyciemnione tło i napis "Edit"
            darkOverlay.visibility = View.VISIBLE
            editText.visibility = View.VISIBLE
        } else {
            // Zapisz zmiany i wyłącz tryb edycji
            usernameText.text = editUsernameField.text
            descriptionText.text = descriptionEditField.text

            usernameText.visibility = View.VISIBLE
            editUsernameField.visibility = View.GONE

            descriptionText.visibility = View.VISIBLE
            descriptionEditField.visibility = View.GONE

            editButton.text = "Edit"

            // Ukryj przyciemnione tło i napis "Edit"
            darkOverlay.visibility = View.GONE
            editText.visibility = View.GONE
        }
    }

    // Funkcja otwierająca galerię
    private fun openGallery() {
        pickImageLauncher.launch("image/*")
    }
}
