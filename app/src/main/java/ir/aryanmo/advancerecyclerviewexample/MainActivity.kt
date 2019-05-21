package ir.aryanmo.advancerecyclerviewexample

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.TextView
import ir.aryanmo.advancerecyclerview.AdvanceRecyclerView
import ir.aryanmo.advancerecyclerview.ItemSwipeCallback
import ir.aryanmo.advancerecyclerview.ItemView
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    var lastItemPosition = -1
    var lastItemName = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val myList = getList()

        re.onAdapterListener = object : AdvanceRecyclerView.OnAdapterListener() {
            override fun selectItemView(nextPosition: Int): Int {
                return if (nextPosition % 10 != 0) R.layout.test_item else R.layout.test_item2
            }

            override fun onBindViewHolder(holder: AdvanceRecyclerView.ViewHolder, position: Int, itemView: ItemView) {
                super.onBindViewHolder(holder, position, itemView)
                holder.itemView.findViewById<TextView>(R.id.text).text = myList[position]
            }
        }
        val itemSwipeCallback = ItemSwipeCallback(
            object : ItemSwipeCallback.OnSwipeItemListener {
                override fun onSwipeToLeft(position: Int) {
                    Log.e("Ari", "remove -> $position -> ${myList[position]}")
                    lastItemPosition = position
                    lastItemName = myList[position]
                    Log.e("Ari", "myList before remove -> ${myList.size - 1}")
                    myList.removeAt(position)
                    re.notifyItemRemove(position)
                    Log.e("Ari", "myList after remove -> ${myList.size - 1}")
                }

                override fun onSwipeToRight(position: Int) {
                }
            },
            ItemSwipeCallback.LEFT_DIR
        )

        itemSwipeCallback.setupSwipeLeftLayout(
            ContextCompat.getDrawable(this, R.drawable.abc_ic_menu_cut_mtrl_alpha)!!,
            ColorDrawable(ContextCompat.getColor(this, R.color.colorPrimary))
        )

        itemSwipeCallback.setupSwipeRightLayout(
            ContextCompat.getDrawable(this, R.drawable.abc_ic_menu_share_mtrl_alpha)!!,
            ColorDrawable(ContextCompat.getColor(this, R.color.colorAccent))
        )

        re.setSwipeListener(itemSwipeCallback)


        re.init(myList.size, ItemView(R.layout.test_item), ItemView(R.layout.test_item2))

        goTo.setOnClickListener {
            myList.add(lastItemPosition, lastItemName)
            re.notifyItemInsert(lastItemPosition)
        }
        smoothGoTo.setOnClickListener { re.smoothScrollToPos(10) }
        re.isInitialize


    }

    private fun getList(): ArrayList<String> {
        val list = arrayListOf<String>()
        list.add("salam")
        list.add("kgobi")
        list.add("chetori")
        list.add("chekhabara")
        list.add("chikara mikoni")
        list.add("chikara nemikoni")
        list.add("Ba Salam khdemat shoma")
        list.add("ahmad")
        list.add("Ali")
        list.add("Aryan")
        list.add("Farzin")
        list.add("Moji")
        list.add("mojtaba sh")
        list.add("soheyl")
        list.add("Bahram")
        list.add("Sahand")
        list.add("salam")
        list.add("kgobi")
        list.add("chetori")
        list.add("chekhabara")
        list.add("chikara mikoni")
        list.add("chikara nemikoni")
        list.add("Ba Salam khdemat shoma")
        list.add("ahmad")
        list.add("Ali")
        list.add("Aryan")
        list.add("Farzin")
        list.add("Moji")
        list.add("mojtaba sh")
        list.add("soheyl")
        list.add("Bahram")
        list.add("Sahand")
        list.add("salam")
        list.add("kgobi")
        list.add("chetori")
        list.add("chekhabara")
        list.add("chikara mikoni")
        list.add("chikara nemikoni")
        list.add("Ba Salam khdemat shoma")
        list.add("ahmad")
        list.add("Ali")
        list.add("Aryan")
        list.add("Farzin")
        list.add("Moji")
        list.add("mojtaba sh")
        list.add("soheyl")
        list.add("Bahram")
        list.add("Sahand")
        list.add("salam")
        list.add("kgobi")
        list.add("chetori")
        list.add("chekhabara")
        list.add("chikara mikoni")
        list.add("chikara nemikoni")
        list.add("Ba Salam khdemat shoma")
        list.add("ahmad")
        list.add("Ali")
        list.add("Aryan")
        list.add("Farzin")
        list.add("Moji")
        list.add("mojtaba sh")
        list.add("soheyl")
        list.add("Bahram")
        list.add("Sahand")
        return list
    }


}
