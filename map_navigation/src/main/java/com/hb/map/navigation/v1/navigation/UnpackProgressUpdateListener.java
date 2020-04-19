package com.hb.map.navigation.v1.navigation;

/**
 * Updates any UI elements on the status of the TAR unpacking.
 */
class UnpackProgressUpdateListener implements UnpackUpdateTask.ProgressUpdateListener {

    private final RouteTileDownloadListener listener;

    UnpackProgressUpdateListener(RouteTileDownloadListener listener) {
        this.listener = listener;
    }

    @Override
    public void onProgressUpdate(Long progress) {
        if (listener != null) {
            listener.onProgressUpdate(progress.intValue());
        }
    }

    @Override
    public void onCompletion() {
        listener.onCompletion();
    }
}
