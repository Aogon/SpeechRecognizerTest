package app.sakai.tororoimo.speechrecognizertest

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), SimpleRecognizerListener.SimpleRecognizerResponseListener {

    private lateinit var speechRecognizer: SpeechRecognizer
    private lateinit var recognizerIntent: Intent

    // true → スタート状態, false → ストップ状態
    private var speechState = false
    private var permissionState = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // 2. 音声認識のためのパーミッションチェック。
        val recordAudioPermission = android.Manifest.permission.RECORD_AUDIO
        val currentPermissionState = ContextCompat.checkSelfPermission(this, recordAudioPermission)
        if (currentPermissionState != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this as Activity, recordAudioPermission)) {
                // 拒否した場合
                permissionState = false
            } else {
                // 許可した場合
                ActivityCompat.requestPermissions(this, arrayOf(recordAudioPermission), 1)
                permissionState = true
            }
        }

        // 3. SpeechRecognizer のインスタンスを createSpeechRecognizer(Context) で生成し、 RecognitionListener を登録する。
        setupSpeechRecognizer()

        // 4. SpeechRecognizer 用の Intent を生成。
        setupRecognizerIntent()

        button.setOnClickListener {
            if (speechState) {
                button.text = "スタート"
                stopListening()
            } else {
                button.text = "ストップ"
                startListening()
            }
        }
    }

    private fun setupSpeechRecognizer() {
        // SpeechRecognizer のインスタンスを生成
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
        speechRecognizer.setRecognitionListener(SimpleRecognizerListener(this))
    }

    private fun setupRecognizerIntent() {
        // Recognizer のレスポンスを取得するための Intent を生成する
        recognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, packageName)
    }

    private fun startListening() {
        speechState = true
        // 5. SpeechRecognizer の __startListening(Intent)__ に先ほど生成した SpeechRecognizer 用の Intent を渡し音声認識を開始する。
        speechRecognizer.startListening(recognizerIntent)
    }

    private fun stopListening() {
        speechState = false
        // 6. __stopListening()__ で音声認識を止める。
        speechRecognizer.stopListening()
    }

    // 7. onResultsから取得した値で判定し、特定の言葉を表示する
    override fun onResultsResponse(speechText: String) {
        // ここは適宜任意の文字列に変えてください
        if (speechText == "おはよう") {
            Toast.makeText(this, "ございます！", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, speechText, Toast.LENGTH_SHORT).show()
        }
    }
}

class SimpleRecognizerListener(private val listener: SimpleRecognizerResponseListener)
    : RecognitionListener {

    interface SimpleRecognizerResponseListener {
        fun onResultsResponse(speechText: String)
    }

    override fun onReadyForSpeech(p0: Bundle?) {

    }

    override fun onRmsChanged(p0: Float) {
    }

    override fun onBufferReceived(p0: ByteArray?) {
    }

    override fun onPartialResults(p0: Bundle?) {
    }

    override fun onEvent(p0: Int, p1: Bundle?) {
    }

    override fun onBeginningOfSpeech() {
    }

    override fun onEndOfSpeech() {
    }

    override fun onError(p0: Int) {
    }

    // 7. onResultsから取得した値で判定し、特定の言葉を表示する
    override fun onResults(bundle: Bundle?) {
        if (bundle == null) {
            listener.onResultsResponse("")
            return
        }

        val key = SpeechRecognizer.RESULTS_RECOGNITION
        val result = bundle.getStringArrayList(key)
        // なぜかスペースが入力されてしまう時があったので、スペースがあった場合は取り除くようにした。
        val speechText = result?.get(0)?.replace("\\s".toRegex(), "")

        if (speechText.isNullOrEmpty()) {
            listener.onResultsResponse("")
        } else {
            listener.onResultsResponse(speechText)
        }
    }
}