package ir.aryanmo.advancerecyclerview

import android.support.annotation.AnimRes
import android.support.annotation.LayoutRes

class ItemView(
    @field:LayoutRes
    val itemViewId: Int,
    @field:AnimRes
    var animation: Int? = null
) {

    companion object {
        const val DEFAULT_ANIMATION = android.R.anim.slide_in_left
    }

}
