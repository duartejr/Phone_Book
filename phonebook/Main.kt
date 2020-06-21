package phonebook

import java.io.File
import kotlin.math.floor
import kotlin.math.sqrt

var timeEndJump = System.currentTimeMillis()

fun main() {
    val find = File("/home/duarte/IdeaProjects/find.txt")
    val directory = File("/home/duarte/IdeaProjects/directory.txt")
    val queries = find.readLines()
    var phones = directory.readLines().toMutableList()

    for (i in phones.indices) {
        phones[i] = phones[i].split(" ").toTypedArray().drop(1).joinToString(separator = " ")
    }

    val nEntries = queries.size
    println("Start searching (linear search)...")
    var timeIni = System.currentTimeMillis()
    val founds = linearSearch(queries, phones)
    var timeSearch = System.currentTimeMillis() - timeIni
    println("Found $founds / $nEntries. Time taken: " +
            String.format("%1\$tM min. %1\$tS sec. %1\$tL ms.", timeSearch))

    println("\nStart searching (bubble sort + jump search) ...")
    val timeLimit = 10_000
    bubbleJumpSearch(queries, phones, timeLimit)

    println("\nStart searching (quick sort + binary search) ...")
    timeIni = System.currentTimeMillis()
    phones = quickSort(phones)
    val timeSort = System.currentTimeMillis() - timeIni
    val timeIniSearch = System.currentTimeMillis()
    var nFounds = 0
    for (query in queries) {
        if (binarySearch(phones, query, 0, phones.size)) {
            nFounds += 1
        }
    }
    val timeEndSearch = System.currentTimeMillis() - timeIniSearch
    timeSearch = System.currentTimeMillis() - timeIni
    println("Found $nFounds / $nEntries. Time taken: " +
            String.format("%1\$tM min. %1\$tS sec. %1\$tL ms.", timeSearch))
    println(String.format("Sorting time: %1\$tM min. %1\$tS sec. %1\$tL ms.", timeSort))
    println(String.format("Searching time: %1\$tM min. %1\$tS sec. %1\$tL ms.", timeEndSearch))

    println("\nStart searching (hash table)...")
    timeIni = System.currentTimeMillis()
    val records = HashMap<String, Boolean>()
    for (k in phones) {
        records[k] = true
    }
    val timeSearching = System.currentTimeMillis()
    nFounds = 0
    for (query in queries) {
        if (query in records.keys) {
            nFounds += 1
        }
    }
    val timeEnd = System.currentTimeMillis()
    val totalTime = timeEnd - timeIni
    val createTime = timeSearching - timeIni
    val searchTime = timeEnd - timeSearching
    println("Found $nFounds / $nEntries. Time taken: " +
            String.format("%1\$tM min. %1\$tS sec. %1\$tL ms.", totalTime))
    println(String.format("Creating time: %1\$tM min. %1\$tS sec. %1\$tL ms.", createTime))
    println(String.format("Searching time: %1\$tM min. %1\$tS sec. %1\$tL ms.", searchTime))

}

fun linearSearch(queries: List<String>, phones: MutableList<String>): Int{
    var nFounds = 0
    for (query in queries){
        for (phone in phones) {
            if (query in phone) {
                nFounds += 1
                break
            }
        }
    }
    return nFounds
}

fun bubbleSort(list: MutableList<String>): MutableList<String> {
    var swap = true
    while (swap) {
        swap = false
        for (i in 0 until list.size - 1) {
            if (list[i] > list[i+1]) {
                val temp = list[i]
                list[i] = list[i + 1]
                list[i + 1] = temp
                swap = true
            }
        }
    }
    return list
}

fun jumpSearch(queries: List<String>, phones: MutableList<String>, timeIni: Long): Int{
    var nFounds = 0
    val n = phones.size
    var line: String
    val timeIniSearch = System.currentTimeMillis()
    var step = floor(sqrt(n.toDouble())).toInt()
    for (query in queries){
        var pos = 0
        loop@ while (pos < n - 1) {
            line = phones[pos].split(" ").takeLast(2).joinToString(separator = " ")
            if (line == query) {
                nFounds += 1
                break@loop
            } else if (line > query){
                if (pos == 0) break@loop
                for (i in pos-1..pos-step+1) {
                    line = phones[i].split(" ").takeLast(2).joinToString(separator = " ")
                    if (line == query){
                        nFounds += 1
                        break@loop
                    }
                }
            }
            timeEndJump = System.currentTimeMillis()
            pos += step
        }
    }
    val timeEndSearch = System.currentTimeMillis()
    val nEntries = queries.size
    println("Found $nFounds / $nEntries. Time taken: " +
            String.format("%1\$tM min. %1\$tS sec. %1\$tL ms.\n", timeEndSearch))
    var timeSort = timeEndSearch - timeIni
    println(String.format("Sorting time : %1\$tM min. %1\$tS sec. %1\$tL ms.", timeSort))
    var timeSearch = timeEndSearch - timeIniSearch
    println(String.format("Searching time: %1\$tM min. %1\$tS sec. %1\$tL ms.\n", timeSearch))
    return 0
}

fun bubbleJumpSearch(queries: List<String>, list: MutableList<String>, timeLimit: Int): Int {
    val timeIniSort = System.currentTimeMillis()
    var swap = true
    while (swap) {
        swap = false
        for (i in 0 until list.size - 1) {
            val ownerI = list[i].split(" ").takeLast(2).joinToString(separator = " ")
            val ownerII = list[i + 1].split(" ").takeLast(2).joinToString(separator = " ")
            if (ownerI > ownerII) {
                val temp = list[i]
                list[i] = list[i + 1]
                list[i + 1] = temp
                swap = true
            }
            val timeEndSort = System.currentTimeMillis()
            if ((timeEndSort - timeIniSort) > timeLimit) {
                val timeInitSearch = System.currentTimeMillis()
                val founds = linearSearch(queries, list)
                val nEntries = queries.size
                val timeEndSearch = System.currentTimeMillis()
                val timeProcess = timeEndSearch - timeIniSort
                println("Found $founds / $nEntries. Time taken: " +
                        String.format("%1\$tM min. %1\$tS sec. %1\$tL ms.", timeProcess))
                var timeSort = timeEndSort - timeIniSort
                println(String.format("Sorting time : %1\$tM min. %1\$tS sec. %1\$tL ms. - STOPPED, moved to linear search", timeSort))
                val timeSearch = timeEndSearch - timeInitSearch
                println(String.format("Searching time: %1\$tM min. %1\$tS sec. %1\$tL ms.", timeSearch))
                return 0
            }
        }
    }
    jumpSearch(queries, list, timeIniSort)
    return 0
}

fun quickSort(list: MutableList<String>): MutableList<String> {
    if (list.count() < 2) {
        return list
    }
    val pivot = list[list.count() / 2]
    val equal = list.filter { it == pivot }.toMutableList()
    val less = list.filter { it < pivot }.toMutableList()
    val greater = list.filter { it > pivot }.toMutableList()

    return (quickSort(less) + equal + quickSort(greater)).toMutableList()
}

fun binarySearch(list: MutableList<String>, x: String, left: Int, right: Int): Boolean {
    if (left > right) {
        return false
    }

    val mid = (left + right) / 2
    return when {
        x in list[mid] -> {
            true
        }
        x < list[mid] -> {
            binarySearch(list, x, left, mid - 1)
        }
        else -> {
            binarySearch(list, x, mid + 1, right)
        }
    }
}