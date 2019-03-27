package ir.aryanmo.advancerecyclerviewexample

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import ir.aryanmo.advancerecyclerview.AdvanceRecyclerView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val myList = getList()

        re.onAdapterListener = object : AdvanceRecyclerView.OnAdapterListener {
            override fun onCreateViewHolder(): View? {
                return null
            }

            override fun onBindViewHolder(holder: AdvanceRecyclerView.ViewHolder, position: Int) {
                holder.itemView.findViewById<TextView>(R.id.text).text = myList[position]
            }
        }

        re.init(R.layout.test_item, myList.size, null)

        goTo.setOnClickListener {
            if (re.isInitialize) {
                myList.add("saom")
                myList.add("hlfg")
                myList.add("sifkpasj")
                myList.add("dop")
                myList.add("rez")
                re.notifyDataSetChanged(myList.size)
            }
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
        return list
    }
}
