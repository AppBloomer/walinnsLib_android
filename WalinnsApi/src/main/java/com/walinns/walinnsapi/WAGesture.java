package com.walinns.walinnsapi;

import android.app.Activity;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by walinnsinnovation on 30/12/17.
 */

public class WAGesture {
    public WAGesture(WalinnsAPIClient mWalinnsTrackerClient, Activity parent) {
        this.trackGestures(mWalinnsTrackerClient, parent);
    }
    private void trackGestures(WalinnsAPIClient mWalinnsTrackerClient, Activity parent) {
        parent.getWindow().getDecorView().setOnTouchListener(this.getGestureTrackerTouchListener(mWalinnsTrackerClient));
    }
    private View.OnTouchListener getGestureTrackerTouchListener(final WalinnsAPIClient mWalinnsTrackerClient) {
        return new View.OnTouchListener() {
            private long mSecondFingerTimeDown = -1L;
            private long mFirstToSecondFingerDifference = -1L;
            private int mGestureSteps = 0;
            private long mTimePassedBetweenTaps = -1L;
            private boolean mDidTapDownBothFingers = false;
            private final int TIME_BETWEEN_FINGERS_THRESHOLD = 100;
            private final int TIME_BETWEEN_TAPS_THRESHOLD = 1000;
            private final int TIME_FOR_LONG_TAP = 2500;

            public boolean onTouch(View v, MotionEvent event) {
                if(event.getPointerCount() > 2) {
                    this.resetGesture();
                    return false;
                } else {
                    switch(event.getActionMasked()) {
                        case 0:
                            this.mFirstToSecondFingerDifference = System.currentTimeMillis();
                            break;
                        case 1:
                            if(System.currentTimeMillis() - this.mFirstToSecondFingerDifference < 100L) {
                                if(System.currentTimeMillis() - this.mSecondFingerTimeDown >= 2500L) {
                                    if(this.mGestureSteps == 3) {
                                        mWalinnsTrackerClient.track_("$ab_gesture1");
                                        this.resetGesture();
                                    }

                                    this.mGestureSteps = 0;
                                } else {
                                    this.mTimePassedBetweenTaps = System.currentTimeMillis();
                                    if(this.mGestureSteps < 4) {
                                        ++this.mGestureSteps;
                                    } else if(this.mGestureSteps == 4) {
                                        mWalinnsTrackerClient.track_("$ab_gesture2");
                                        this.resetGesture();
                                    } else {
                                        this.resetGesture();
                                    }
                                }
                            }
                        case 2:
                        case 3:
                        case 4:
                        default:
                            break;
                        case 5:
                            if(System.currentTimeMillis() - this.mFirstToSecondFingerDifference < 100L) {
                                if(System.currentTimeMillis() - this.mTimePassedBetweenTaps > 1000L) {
                                    this.resetGesture();
                                }

                                this.mSecondFingerTimeDown = System.currentTimeMillis();
                                this.mDidTapDownBothFingers = true;
                            } else {
                                this.resetGesture();
                            }
                            break;
                        case 6:
                            if(this.mDidTapDownBothFingers) {
                                this.mFirstToSecondFingerDifference = System.currentTimeMillis();
                            } else {
                                this.resetGesture();
                            }
                    }

                    return false;
                }
            }

            private void resetGesture() {
                this.mFirstToSecondFingerDifference = -1L;
                this.mSecondFingerTimeDown = -1L;
                this.mGestureSteps = 0;
                this.mTimePassedBetweenTaps = -1L;
                this.mDidTapDownBothFingers = false;
            }
        };
    }
}
