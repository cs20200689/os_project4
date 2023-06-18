package com.example.pj4test.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.pj4test.ProjectConfiguration
import com.example.pj4test.audioInference.ConversationClassifier
import com.example.pj4test.databinding.FragmentAudioBinding


class AudioFragment: Fragment(), ConversationClassifier.DetectorListener {
    private val TAG = "AudioFragment"

    private var _fragmentAudioBinding: FragmentAudioBinding? = null

    private val fragmentAudioBinding
        get() = _fragmentAudioBinding!!

    // classifiers
    lateinit var conversationClassifier: ConversationClassifier

    // views
    lateinit var conversationView: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _fragmentAudioBinding = FragmentAudioBinding.inflate(inflater, container, false)

        return fragmentAudioBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        conversationView = fragmentAudioBinding.SnapView

        conversationClassifier = ConversationClassifier()
        conversationClassifier.initialize(requireContext())
        conversationClassifier.setDetectorListener(this)
    }

    override fun onPause() {
        super.onPause()
        conversationClassifier.stopInferencing()
    }

    override fun onResume() {
        super.onResume()
        conversationClassifier.startInferencing()
    }

    override fun onResults(score: Float) {
        activity?.runOnUiThread {
            if (score > ConversationClassifier.THRESHOLD) {
                conversationView.text = "CONVERSATION - " + counter.get_conversation()
                conversationView.setBackgroundColor(ProjectConfiguration.activeBackgroundColor)
                conversationView.setTextColor(ProjectConfiguration.activeTextColor)
                // plus conversation count
                counter.plus_conversation()
                // End audio recognition if the conversation lasts for more than 10 seconds
                if (counter.get_conversation() > 10) {
                    conversationClassifier.stopInferencing()
                }
            } else {
                conversationView.text = "NO CONVERSATION - " + counter.get_conversation()
                conversationView.setBackgroundColor(ProjectConfiguration.idleBackgroundColor)
                conversationView.setTextColor(ProjectConfiguration.idleTextColor)
            }
        }
    }
}