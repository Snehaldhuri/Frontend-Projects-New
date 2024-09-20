package com.diipl.moviebeam.ui.mainmenu

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.provider.Settings
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.diipl.moviebeam.R
import com.diipl.moviebeam.data.dto.btn.BtnModel
import com.diipl.moviebeam.data.local.PreferenceDataStoreConstants
import com.diipl.moviebeam.data.local.PreferenceDataStoreHelper
import com.diipl.moviebeam.ui.base.BaseActivity.Companion.currentActivity
import com.diipl.moviebeam.utils.Constants
import com.diipl.moviebeam.utils.getHeightInPercent
import com.diipl.moviebeam.utils.getWidthInPercent
import com.diipl.moviebeam.utils.handleFocusChange
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private const val TAG = "MainMenuBtnAdapter"

class MainMenuBtnAdapter(
    private var onMenuItemClicked: (BtnModel) -> Unit
) :
    RecyclerView.Adapter<MainMenuBtnAdapter.MyViewHolder>() {
    var itemList: List<BtnModel> = mutableListOf()
    var count = 0

    //Variables from datastore
    private val preferenceDataStoreHelper = PreferenceDataStoreHelper(currentActivity!!)
    private var gradientStartColor = Constants.DEFAULTGRADIENTSTARTCOLOR
    private var gradientEndColor = Constants.DEFAULTGRADIENTENDCOLOR

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.iv_menu_icon)
        val textView: TextView = itemView.findViewById(R.id.tv_menu_title)
        val card: ConstraintLayout = itemView.findViewById(R.id.clHomeMenuButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_button, parent, false)
        initializeDatastoreParams()
        val params = view.layoutParams
        params.width = getWidthInPercent(parent.context, 21)
        params.height = getHeightInPercent(parent.context, 16)

        return MyViewHolder(view)
    }

    override fun getItemCount(): Int = itemList.size


    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemId(position: Int): Long {
        return super.getItemId(position)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = itemList[position]

        holder.imageView.setImageResource(item.imageResId)
        holder.textView.text = item.title

        /*
                holder.card.postDelayed(
                    {
                        if (holder.absoluteAdapterPosition == 0) {
                            holder.card.requestFocus()
                        }
                    },200
                )
        */
        holder.card.setBackgroundResource(R.drawable.btn_bg_gradient_default)

     /*   holder.card.setOnFocusChangeListener { v, b ->
            if (b) {
                v.background = getGradientColor()
            } else {
                v.setBackgroundResource(R.drawable.btn_bg_gradient_default)
            }
        }*/

        holder.card.handleFocusChange()

        holder.card.setOnClickListener {
            onMenuItemClicked(item)
        }

        holder.itemView.setOnKeyListener { view, i, keyEvent ->
            if (i == KeyEvent.KEYCODE_TV_INPUT) Log.e(TAG, "onBindViewHolder: KEYCODE_TV_INPUT")
            if (i == KeyEvent.KEYCODE_NAVIGATE_IN) Log.e(
                TAG,
                "onBindViewHolder: KEYCODE_NAVIGATE_IN"
            )
            if (i == KeyEvent.KEYCODE_AVR_INPUT) Log.e(TAG, "onBindViewHolder: KEYCODE_AVR_INPUT")
            if (i == KeyEvent.KEYCODE_STB_INPUT) Log.e(TAG, "onBindViewHolder: KEYCODE_STB_INPUT")
            if (holder.absoluteAdapterPosition == 0) {
                if (i == KeyEvent.KEYCODE_DPAD_LEFT) {
                    count++
                    Log.e(TAG, "KEYCODE_DPAD_LEFT: $count")
                    if (count == 20) {
                        val intent = Intent(Intent.ACTION_VIEW)
                        intent.action = Settings.ACTION_SETTINGS
                        intent.flags =
                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        view.context.startActivity(intent)
                        count = 0
                    }
                }
            }
            if (i == KeyEvent.KEYCODE_DPAD_RIGHT || i == KeyEvent.KEYCODE_DPAD_DOWN || i == KeyEvent.KEYCODE_DPAD_UP) {
                count = 0
            }
            false
        }

    }

    private fun getGradientColor(): GradientDrawable {
        val gradientDrawable = GradientDrawable(
            GradientDrawable.Orientation.TR_BL,
            intArrayOf(Color.parseColor(gradientStartColor), Color.parseColor(gradientEndColor))
        )
        gradientDrawable.cornerRadius = 20f
        gradientDrawable.gradientType = GradientDrawable.LINEAR_GRADIENT

        gradientDrawable.setGradientCenter(0.0468f, 0.6542f)
        return gradientDrawable
    }

    private fun initializeDatastoreParams() {
        CoroutineScope(Dispatchers.Default).launch {
            gradientStartColor = getGradientStartColor()
            gradientEndColor = getGradientEndColor()
        }
    }

    private suspend fun getGradientStartColor(): String {
        return preferenceDataStoreHelper.getFirstPreference(
            PreferenceDataStoreConstants.GRADIENT_COLOR_START_KEY,
            Constants.DEFAULTGRADIENTSTARTCOLOR
        )
    }

    private suspend fun getGradientEndColor(): String {
        return preferenceDataStoreHelper.getFirstPreference(
            PreferenceDataStoreConstants.GRADIENT_COLOR_END_KEY,
            Constants.DEFAULTGRADIENTENDCOLOR
        )
    }

}


