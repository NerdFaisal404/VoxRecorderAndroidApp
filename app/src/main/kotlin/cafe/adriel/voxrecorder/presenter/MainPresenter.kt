package cafe.adriel.voxrecorder.presenter

import android.app.Activity
import android.net.Uri
import android.support.v4.app.ShareCompat
import cafe.adriel.voxrecorder.Constant
import cafe.adriel.voxrecorder.R
import cafe.adriel.voxrecorder.model.entity.Recording
import cafe.adriel.voxrecorder.model.repository.RecordingRepository
import cafe.adriel.voxrecorder.util.AnalyticsUtil
import cafe.adriel.voxrecorder.util.orFalse
import cafe.adriel.voxrecorder.util.string
import cafe.adriel.voxrecorder.view.IMainView
import rx.Subscription

class MainPresenter(val view: IMainView): IMainPresenter {

    private val recordingRepo = RecordingRepository()
    
    private var subscription: Subscription? = null

    override fun load() {
        unsubscribe()
        subscription = recordingRepo.get()
                .subscribe ({
                    view.onLoadRecordings(it) }, Throwable::printStackTrace)
    }

    override fun save(recording: Recording) {
        recordingRepo.save(recording)
    }

    override fun rename(recording: Recording, newName: String) {
        recordingRepo.rename(recording, newName)
    }

    override fun delete(recording: Recording) {
        recordingRepo.delete(recording)
    }

    override fun share(activity: Activity, recording: Recording) {
        val text = string(R.string.this_recording_was_created_using_vox, Constant.GOOGLE_PLAY_URL)
        ShareCompat.IntentBuilder.from(activity)
                .setText(text)
                .setStream(Uri.fromFile(recording.file))
                .setType(Constant.MIME_TYPE_AUDIO)
                .startChooser()
        AnalyticsUtil.shareEvent(recording)
    }

    override fun isValidFileName(fileName: String) = fileName.isNotEmpty()

    override fun showRenameDialog(recording: Recording) {
        view.showRenameDialog(recording)
    }

    override fun showDeleteDialog(recording: Recording) {
        view.showDeleteDialog(recording)
    }

    override fun unsubscribe() {
        if(subscription != null && !subscription?.isUnsubscribed.orFalse()) {
            subscription?.unsubscribe()
        }
    }

}