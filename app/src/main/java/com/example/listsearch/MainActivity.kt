package com.example.listsearch

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

//Q4
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

            //no need for a submit button, the check is run every time the field is updated
            override fun afterTextChanged(p0: Editable?) {
                updateList()
            }
        })
    }

    //Q1
    //replacing the spaces for "&32" not in place, as it is impossible in the String type
    //to replace in place efficiently one would need to implement the string holder in a linked list
    //which is an inefficient data structure to hold a string
    //there is an alternative that is shifting all the chars to the right as needed,
    //but the cost is high (O(n²)) for such a simple operation
    //therefore it makes much more sense to replace the characters in an auxiliary structure
    fun replaceSpaces(str: String): String {
        return str.replace(" ", "&32")
    }

    //Q2 - iterates through the strings only once, no additional memory usage
    //cost is O(size(a+b)) in time
    fun typoChecker(a: String, b: String): Boolean {
        //check if either string is empty or their length differs more than one
        if (a.isEmpty() || b.isEmpty() || a.length - b.length !in -1..1)
            return false

        //keeps track if an error has been found, as only one is allowed
        var error = false
        var posA = 0
        var posB = 0

        //iterate through the characters of the strings checking if they are equal
        while (posA < a.length || posB < b.length) {

            //if only one of the strings reach the end, return the negated error status
            //(false if there is already an error accounted for)
            if (posA == a.length || posB == b.length)
                return !error

            //if both characters are equal, increment the position
            if (a[posA] == b[posB]) {
                ++posA
                ++posB
            }
            //otherwise checks if it is the first error
            //and increments the counter of the smaller string
            //or from both if their length is the same
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
            //returns false if there is already an error
            else
                return false
        }

        //returns true if the end of both strings is reached simultaneously and there is no error
        return true
    }

    //Q3 - iterates through the strings only once, but uses auxiliary hashmaps to count the chars
    //cost is O(size(a + b)) in time and O(size(charset)) in memory
    fun misspellChecker(a: String, b: String): Boolean {
        //check if both strings have the same length and have the same first character
        if (a.isEmpty() || b.isEmpty() || a.length != b.length || a[0] != b[0])
            return false

        //using a hashmap to keep track of the count of characters in each string to match later
        //this gives efficiency (the time for running is O(size(a + b)), but consumes space in the
        //order of O(size(charset))
        val charsA = HashMap<Char, Int>()
        val charsB = HashMap<Char, Int>()

        //keeps track of how many characters are different, in order to check the 2/3 condition
        var countDiff = 0

        //iterates through both strings comparing the chars and counting their occurrences
        for (i in 0 until Math.max(a.length, b.length)) {
            if (a[i] != b[i])
                ++countDiff
            charsA[a[i]] = charsA[a[i]]?.inc() ?: 1
            charsB[b[i]] = charsB[b[i]]?.inc() ?: 1
        }

        //check for the 2/3 maximum misplaced characters condition
        if (countDiff > a.length * 2 / 3)
            return false

        //if both hashmaps contain the same data and it has at least one difference, it's misspelled
        if (charsA == charsB && countDiff > 0)
            return true
        return false
    }

    fun findMatches(searchString: String): String {
        //displays all strings if the search string is empty
        if (searchString.isEmpty())
            return itemsStr

        //split the items list into an array
        val itemsList = itemsStr.split("\n").toTypedArray()
        var strOut = ""

        //for every item in the list check if there is either a typo or a misspell, adds to output
        for (item in itemsList) {
            if (typoChecker(searchString, item) xor misspellChecker(searchString, item))
                strOut += item + "\n"
        }
        return strOut
    }

    //checks for matches in the itemlist and updates the output accordingly
    fun updateList() {
        val itemList = findViewById<TextView>(R.id.textViewItemList)
        val searchStringInput = findViewById<EditText>(R.id.editTextSearchKeyword)
        val searchString = searchStringInput.text.toString()
        itemList.apply {
            text = findMatches(searchString)
        }
    }
}