package com.medinin.medininapp.interfaces;

import android.os.Bundle;

/**
 * Created by Bharath on 06/29/19.
 */

public interface ChangeFragmentListener {
    void changeFragment(int viewId);
    void changeFragment(int viewId, Bundle bundle);
    void removeFromBackStack();
}
