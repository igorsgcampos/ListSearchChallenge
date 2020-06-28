package com.example.listsearch

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.TextView

class MainActivity : AppCompatActivity() {
    private val itemsStr = "item a\n" +
            "item-b\n" +
            "item_c\n" +
            "item d\n" +
            "itme e\n" +
            "item.f\n" +
            "ietm.g\n" +
            "imte.h\n"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val searchStringInput = findViewById<EditText>(R.id.editTextSearchKeyword)
        val itemListTextView = findViewById<TextView>(R.id.textViewItemList)
        itemListTextView.text = itemsStr
        searchStringInput.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }
            override fun afterTextChanged(p0: Editable?) {
                updateList()
            }
        })
    }
    fun typoChecker(a: String, b: String): Boolean {
        if (a.isEmpty() || b.isEmpty() || a.length - b.length !in -1..1)
            return false
        var error = false
        var posA = 0
        var posB = 0
        while (posA < a.length || posB < b.length) {
            if (posA == a.length || posB == b.length)
                return !error
            if (a[posA] == b[posB]) {
                ++posA
                ++posB
            }
            else if (!error) {
                error = true
                if (a.length < b.length)
                    ++posB
                else if (a.length > b.length)
                    ++posA
                else {
                    ++posA
                    ++posB
                }
            }
            else
                return false
        }
        return true
    }
    fun misspellChecker(a: String, b: String): Boolean {
        if (a.isEmpty() || b.isEmpty() || a.length != b.length || a[0] != b[0])
            return false
        val charsA = HashMap<Char, Int>()
        val charsB = HashMap<Char, Int>()
        var countDiff = 0
        for (i in 0 until Math.max(a.length, b.length)) {
            if (a[i] != b[i])
                ++countDiff
            charsA[a[i]] = charsA[a[i]]?.inc() ?: 1
            charsB[b[i]] = charsB[b[i]]?.inc() ?: 1
        }
        if (countDiff > a.length * 2 / 3)
            return false
        if (charsA == charsB && countDiff > 0)
            return true
        return false
    }
    fun findMatches(searchString: String): String {
        if (searchString.isEmpty())
            return itemsStr
        val itemsList = itemsStr.split("\n").toTypedArray()
        var strOut = ""
        for (item in itemsList) {
            if (typoChecker(searchString, item) xor misspellChecker(searchString, item))
                strOut += item + "\n"
        }
        return strOut
    }
    fun     updateList() {
        val itemList = findViewById<TextView>(R.id.textViewItemList)
        val searchStringInput = findViewById<EditText>(R.id.editTextSearchKeyword)
        val searchString = searchStringInput.text.toString()
        itemList.apply {
            text = findMatches(searchString)
        }
    }
}