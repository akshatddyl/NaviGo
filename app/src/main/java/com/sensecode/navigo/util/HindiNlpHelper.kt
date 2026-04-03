package com.sensecode.navigo.util

/**
 * HindiNlpHelper — Offline Hindi natural language understanding for indoor navigation.
 *
 * This handles the gap between how people SPEAK in Hindi and how nodes are NAMED in English.
 * No external API needed. Works entirely offline.
 *
 * Pipeline:
 * 1. Strip conversational filler ("मुझे बताओ", "कहाँ है", "ले चलो" etc.)
 * 2. Stem Hindi words (remove common suffixes: -ों, -ने, -ना, -ें, -ाएं etc.)
 * 3. Match stemmed words against intent clusters (concept groups)
 * 4. Return matching English node types and name ke
 * ywords
 *
 * Example flows:
 * - "मुझे खाने की जगह बताओ" → strip "मुझे..बताओ" → "खाने की जगह" → stem "खाना" → intent: FOOD → "canteen"
 * - "बाहर कैसे जाऊं" → strip "कैसे जाऊं" → "बाहर" → intent: EXIT → "exit"
 * - "पानी कहाँ मिलेगा" → strip "कहाँ मिलेगा" → "पानी" → intent: CANTEEN → "canteen"
 * - "हाथ धोने कहाँ जाऊं" → "हाथ धोना" → intent: TOILET → "restroom"
 */
object HindiNlpHelper {

    /**
     * Result of Hindi NLP processing.
     * Contains extracted keywords and matched intent(s).
     */
    data class NlpResult(
        /** English keywords extracted from Hindi speech */
        val englishKeywords: List<String>,
        /** Matched node types (e.g., "toilet", "canteen", "elevator") */
        val matchedNodeTypes: List<String>,
        /** The cleaned/stripped version of the input text */
        val cleanedText: String,
        /** Numbers extracted from the input */
        val extractedNumbers: List<Int>
    )

    // ══════════════════════════════════════════════════
    // STEP 1: Conversational filler phrases to strip
    // ══════════════════════════════════════════════════

    private val fillerPhrases = listOf(
        // "tell me" / "show me" patterns
        "मुझे बताओ", "मुझे बताइए", "मुझे दिखाओ", "बता दो", "बताओ",
        "बताइए", "दिखाओ", "दिखा दो",
        // "where is" patterns
        "कहाँ है", "कहां है", "कहाँ हैं", "कहां हैं",
        "किधर है", "किधर हैं", "कहाँ पर", "कहां पर",
        "kahan hai", "kaha hai", "kidhar hai", "kahan jana hai",
        // "take me to" / "I want to go" patterns
        "मुझे ले चलो", "ले चलो", "ले जाओ", "मुझे ले जाओ",
        "मुझे जाना है", "जाना है", "जाना चाहता हूं", "जाना चाहती हूं",
        "जाना चाहता हूँ", "जाना चाहती हूँ",
        "mujhe jana hai", "jana hai", "le chalo", "le jao",
        // "I need" / "I want" patterns
        "मुझे चाहिए", "चाहिए", "मुझे", "हमें",
        "mujhe chahiye", "chahiye", "mujhe",
        // "how to go" patterns
        "कैसे जाऊं", "कैसे जाएं", "कैसे पहुंचूं", "कैसे पहुंचे",
        "कैसे जाऊँ", "कैसे जाये", "kaise jau", "kaise jana hai",
        // "where can I find"
        "कहाँ मिलेगा", "कहां मिलेगा", "कहाँ मिलेगी", "कहां मिलेगी",
        "कहाँ मिलेंगे", "कहां मिलेंगे", "kahan milega",
        // Generic fillers
        "की जगह", "का रास्ता", "की तरफ", "की ओर",
        "के पास", "के लिए", "का", "की", "के",
        "ke pass", "ke paas", "ke liye", "ki taraf", "ka rasta",
        "hai", "hain", "wala", "wali", "wale", "kahan", "kaha", "kidhar", "pass", "paas",
        "है", "हैं", "वाला", "वाली", "वाले",
        "कृपया", "please", "ज़रा", "यहाँ", "यहां", "yahan", "idhar",
        "सबसे नज़दीक", "सबसे नजदीक", "nearest", "पास",
        "sabse nazdeek", "sabse kareeb"
    )

    // ══════════════════════════════════════════════════
    // STEP 2: Hindi suffixes for stemming
    // ══════════════════════════════════════════════════

    private val hindiSuffixes = listOf(
        // Plural forms
        "ों", "ियों", "ियाँ", "ियां", "ाएं", "ाएँ",
        // Verb forms
        "ने", "ना", "नी", "ता", "ती", "ते",
        "ाने", "ाना", "ानी",
        // Case markers
        "ें", "ो", "ी", "े", "ा",
    )

    // ══════════════════════════════════════════════════
    // STEP 3: Intent clusters — concept groups
    // Each cluster maps a set of Hindi keywords (both
    // Devanagari and Romanized) to English node types
    // and name keywords
    // ══════════════════════════════════════════════════

    data class IntentCluster(
        val hindiKeywords: Set<String>,
        val englishNames: List<String>,
        val nodeTypes: List<String>
    )

    private val intentClusters = listOf(
        // ── FOOD / EATING ──
        IntentCluster(
            hindiKeywords = setOf(
                "खाना", "खान", "भोजन", "भोज", "लंच", "lunch",
                "नाश्ता", "nashta", "breakfast", "भूख", "bhookh",
                "कैंटीन", "kanteen", "canteen", "mess", "मेस",
                "भोजनालय", "bhojanaalay", "खाने", "khana", "khane",
                "जलपान", "tiffin", "टिफिन", "पानी", "paani", "water",
                "चाय", "chai", "tea", "coffee", "कॉफी"
            ),
            englishNames = listOf("canteen", "cafeteria", "mess", "food"),
            nodeTypes = listOf("canteen")
        ),

        // ── TOILET / RESTROOM ──
        IntentCluster(
            hindiKeywords = setOf(
                "शौचालय", "shauchalay", "sauchalay", "toilet", "टॉयलेट",
                "बाथरूम", "bathroom", "washroom", "वॉशरूम", "restroom",
                "हाथ धो", "haath dho", "पेशाब", "peshab", "लैट्रिन",
                "latrine", "सुलभ शौचालय", "sulabh", "लू", "loo",
                "शौच",  "freshen", "फ्रेशन"
            ),
            englishNames = listOf("restroom", "toilet", "washroom", "bathroom"),
            nodeTypes = listOf("toilet")
        ),

        // ── STAIRS ──
        IntentCluster(
            hindiKeywords = setOf(
                "सीढ़ी", "सीढ़ियाँ", "सीढ़ियां", "seedhi", "seedhiyan",
                "stairs", "सीढ़", "ऊपर जाना", "upar", "ऊपर",
                "नीचे जाना", "neeche", "नीचे", "staircase",
                "ज़ीना", "zeena", "सीड़ी"
            ),
            englishNames = listOf("staircase", "stairs"),
            nodeTypes = listOf("staircase")
        ),

        // ── ELEVATOR / LIFT ──
        IntentCluster(
            hindiKeywords = setOf(
                "लिफ्ट", "lift", "एलिवेटर", "elevator",
                "ऊपर जाने", "ले जाने वाला", "चढ़ना", "उतरना"
            ),
            englishNames = listOf("elevator", "lift"),
            nodeTypes = listOf("elevator")
        ),

        // ── ENTRANCE / GATE ──
        IntentCluster(
            hindiKeywords = setOf(
                "प्रवेश", "pravesh", "entrance", "मुख्य द्वार", "mukhya dwar",
                "गेट", "gate", "दरवाजा", "darwaja", "दरवाज़ा",
                "मुख्य", "main", "अंदर आना", "andar", "अंदर"
            ),
            englishNames = listOf("entrance", "main entrance", "gate", "main"),
            nodeTypes = listOf("entrance")
        ),

        // ── EXIT ──
        IntentCluster(
            hindiKeywords = setOf(
                "निकास", "nikas", "exit", "बाहर", "bahar",
                "बाहर जाना", "bahar jana", "बाहर निकलना",
                "छोड़ना", "जाने दो", "back exit", "rear"
            ),
            englishNames = listOf("exit", "rear exit"),
            nodeTypes = listOf("exit")
        ),

        // ── LIBRARY / STUDY ──
        IntentCluster(
            hindiKeywords = setOf(
                "पुस्तकालय", "pustakalay", "library", "लाइब्रेरी",
                "किताब", "kitab", "book", "पढ़ना", "padhna",
                "पढ़ाई", "padhai", "study", "अध्ययन"
            ),
            englishNames = listOf("library"),
            nodeTypes = listOf("room")
        ),

        // ── LAB / COMPUTER ──
        IntentCluster(
            hindiKeywords = setOf(
                "लैब", "lab", "प्रयोगशाला", "prayogshala", "laboratory",
                "कंप्यूटर", "computer", "कम्प्यूटर", "PC", "पीसी",
                "टेक्नोलॉजी", "technology", "विज्ञान", "science"
            ),
            englishNames = listOf("lab", "computer lab", "computer"),
            nodeTypes = listOf("room")
        ),

        // ── OFFICE / ADMIN ──
        IntentCluster(
            hindiKeywords = setOf(
                "दफ्तर", "daftar", "office", "ऑफिस",
                "कार्यालय", "karyalay", "प्रशासन", "prashasan",
                "एडमिन", "admin", "प्रबंधन", "management",
                "काउंटर", "counter", "enquiry", "इंक्वायरी"
            ),
            englishNames = listOf("office", "admin office", "admin"),
            nodeTypes = listOf("room")
        ),

        // ── RECEPTION / HELP DESK ──
        IntentCluster(
            hindiKeywords = setOf(
                "रिसेप्शन", "reception", "स्वागत", "swagat",
                "हेल्प डेस्क", "help desk", "मदद", "madad",
                "जानकारी", "information", "info", "इन्फो",
                "सहायता", "sahayata"
            ),
            englishNames = listOf("reception", "reception desk"),
            nodeTypes = listOf("room")
        ),

        // ── CLASSROOM / ROOM ──
        IntentCluster(
            hindiKeywords = setOf(
                "कक्षा", "kaksha", "class", "classroom",
                "कमरा", "kamra", "room", "रूम",
                "क्लास", "क्लासरूम"
            ),
            englishNames = listOf("room", "classroom"),
            nodeTypes = listOf("room")
        ),

        // ── MEDICAL / FIRST AID ──
        IntentCluster(
            hindiKeywords = setOf(
                "मेडिकल", "medical", "चिकित्सा", "chikitsa",
                "दवाखाना", "dawakhana", "दवाई", "medicine",
                "डॉक्टर", "doctor", "प्राथमिक चिकित्सा", "first aid",
                "बीमार", "beemar", "तबीयत", "tabiyat",
                "इलाज", "ilaaj", "अस्पताल", "hospital",
                "नर्स", "nurse", "health"
            ),
            englishNames = listOf("medical", "medical room"),
            nodeTypes = listOf("room")
        ),

        // ── FACULTY / TEACHER ──
        IntentCluster(
            hindiKeywords = setOf(
                "फैकल्टी", "faculty", "शिक्षक", "shikshak",
                "अध्यापक", "adhyapak", "टीचर", "teacher",
                "प्रोफेसर", "professor", "सर", "sir", "मैम", "mam",
                "गुरु", "guru", "स्टाफ", "staff"
            ),
            englishNames = listOf("faculty", "faculty room"),
            nodeTypes = listOf("room")
        ),

        // ── SEMINAR / HALL ──
        IntentCluster(
            hindiKeywords = setOf(
                "सभागार", "sabhaagar", "सेमिनार", "seminar",
                "हॉल", "hall", "ऑडिटोरियम", "auditorium",
                "मीटिंग", "meeting", "सभा", "sabha",
                "कार्यक्रम", "program", "event", "इवेंट"
            ),
            englishNames = listOf("seminar hall", "hall", "seminar"),
            nodeTypes = listOf("room")
        ),

        // ── SECURITY ──
        IntentCluster(
            hindiKeywords = setOf(
                "सुरक्षा", "suraksha", "सिक्योरिटी", "security",
                "गार्ड", "guard", "चौकीदार", "chaukidaar",
                "watchman", "पहरेदार", "pahredaar"
            ),
            englishNames = listOf("security", "security office"),
            nodeTypes = listOf("room")
        ),

        // ── PARKING ──
        IntentCluster(
            hindiKeywords = setOf(
                "पार्किंग", "parking", "गाड़ी", "gaadi", "car",
                "बाइक", "bike", "वाहन", "vaahan", "vehicle",
                "two-wheeler", "दो पहिया"
            ),
            englishNames = listOf("parking", "parking entrance"),
            nodeTypes = listOf("entrance")
        ),

        // ── LOBBY / WAITING ──
        IntentCluster(
            hindiKeywords = setOf(
                "लॉबी", "lobby", "इंतज़ार", "intezaar", "wait",
                "बैठक", "baithak", "waiting", "प्रतीक्षालय"
            ),
            englishNames = listOf("lobby", "main lobby"),
            nodeTypes = listOf("junction")
        ),

        // ── MALE / BOYS ──
        IntentCluster(
            hindiKeywords = setOf(
                "पुरुष", "purush", "लड़कों", "ladkon", "boys",
                "gents", "जेंट्स", "मर्द", "male"
            ),
            englishNames = listOf("male"),
            nodeTypes = emptyList()
        ),

        // ── FEMALE / GIRLS ──
        IntentCluster(
            hindiKeywords = setOf(
                "महिला", "mahila", "लड़कियों", "ladkiyon", "girls",
                "ladies", "लेडीज़", "महिलाओं", "female", "औरत"
            ),
            englishNames = listOf("female"),
            nodeTypes = emptyList()
        ),

        // ── ACCESSIBLE / WHEELCHAIR ──
        IntentCluster(
            hindiKeywords = setOf(
                "सुलभ", "sulabh", "विकलांग", "viklang",
                "wheelchair", "व्हीलचेयर", "accessible",
                "दिव्यांग", "divyang", "रैंप", "ramp"
            ),
            englishNames = listOf("accessible"),
            nodeTypes = emptyList()
        )
    )

    // ══════════════════════════════════════════════════
    // STEP 4: Hindi number words
    // ══════════════════════════════════════════════════

    private val hindiNumbers = mapOf(
        "शून्य" to 0, "shuny" to 0, "zero" to 0,
        "एक" to 1, "ek" to 1,
        "दो" to 2, "do" to 2,
        "तीन" to 3, "teen" to 3,
        "चार" to 4, "char" to 4,
        "पांच" to 5, "panch" to 5, "paanch" to 5,
        "छह" to 6, "chah" to 6, "chhah" to 6,
        "सात" to 7, "saat" to 7,
        "आठ" to 8, "aath" to 8,
        "नौ" to 9, "nau" to 9,
        "दस" to 10, "das" to 10,
        "ग्यारह" to 11, "gyarah" to 11,
        "बारह" to 12, "baarah" to 12,
        // Ordinals
        "पहला" to 1, "पहली" to 1, "pehla" to 1, "pahla" to 1,
        "दूसरा" to 2, "दूसरी" to 2, "doosra" to 2, "dusra" to 2,
        "तीसरा" to 3, "तीसरी" to 3, "teesra" to 3, "tisra" to 3,
        "चौथा" to 4, "चौथी" to 4, "chautha" to 4,
        "पांचवा" to 5, "पांचवीं" to 5, "panchwa" to 5,
        // Special floor words
        "भूतल" to 0, "bhutaal" to 0, "ground" to 0,
        "तहखाना" to -1, "tahkhana" to -1, "basement" to -1,
        "ऊपर" to 1, "upar" to 1, "नीचे" to -1, "neeche" to -1
    )

    // ══════════════════════════════════════════════════
    // PUBLIC API
    // ══════════════════════════════════════════════════

    /**
     * Process Hindi speech and extract structured navigation intent.
     * Handles full conversational Hindi like "मुझे खाने की जगह बताओ".
     */
    fun process(spokenText: String): NlpResult {
        val original = spokenText.trim()

        // Step 1: Strip conversational filler
        var cleaned = stripFillerPhrases(original.lowercase())

        // Step 2: Extract numbers
        val numbers = extractNumbers(cleaned)

        // Step 3: Stem remaining words
        val stemmed = stemHindiWords(cleaned)

        // Step 4: Match against intent clusters
        val matchedClusters = matchIntentClusters(stemmed, original.lowercase())

        // Collect all english keywords and node types
        val englishKeywords = matchedClusters.flatMap { it.englishNames }.distinct()
        val nodeTypes = matchedClusters.flatMap { it.nodeTypes }.distinct()

        return NlpResult(
            englishKeywords = englishKeywords,
            matchedNodeTypes = nodeTypes,
            cleanedText = cleaned,
            extractedNumbers = numbers
        )
    }

    /**
     * Parse a floor number from Hindi speech.
     * Handles: "पहला", "दो", "do", "2", "ground floor", "भूतल" etc.
     */
    fun parseFloorNumber(spokenText: String): Int {
        val lower = spokenText.lowercase().trim()

        // Check Hindi number map
        for ((word, number) in hindiNumbers) {
            if (lower.contains(word.lowercase())) {
                return number
            }
        }

        // English word numbers
        val englishNumbers = mapOf(
            "zero" to 0, "one" to 1, "two" to 2, "three" to 3, "four" to 4,
            "five" to 5, "six" to 6, "seven" to 7, "eight" to 8, "nine" to 9,
            "ten" to 10, "first" to 1, "second" to 2, "third" to 3, "fourth" to 4,
            "fifth" to 5, "ground" to 0, "lobby" to 0, "basement" to -1,
            "1st" to 1, "2nd" to 2, "3rd" to 3, "4th" to 4, "5th" to 5
        )
        for ((word, number) in englishNumbers) {
            if (lower.contains(word)) return number
        }

        // Extract digits
        val digits = Regex("\\d+").findAll(lower)
        return digits.firstOrNull()?.value?.toIntOrNull() ?: 0
    }

    // ══════════════════════════════════════════════════
    // PRIVATE HELPERS
    // ══════════════════════════════════════════════════

    private fun stripFillerPhrases(text: String): String {
        var cleaned = text
        // Sort by length descending so longer phrases match first
        for (filler in fillerPhrases.sortedByDescending { it.length }) {
            cleaned = cleaned.replace(filler.lowercase(), " ")
        }
        return cleaned.replace("\\s+".toRegex(), " ").trim()
    }

    private fun extractNumbers(text: String): List<Int> {
        val numbers = mutableListOf<Int>()

        // Extract digit sequences
        Regex("\\d+").findAll(text).forEach {
            it.value.toIntOrNull()?.let { n -> numbers.add(n) }
        }

        // Extract Hindi number words
        val words = text.split("\\s+".toRegex())
        for (word in words) {
            hindiNumbers[word.trim()]?.let { numbers.add(it) }
        }

        return numbers.distinct()
    }

    private fun stemHindiWords(text: String): String {
        val words = text.split("\\s+".toRegex())
        val stemmed = words.map { word ->
            var stem = word
            // Try to strip suffixes (longest first)
            for (suffix in hindiSuffixes.sortedByDescending { it.length }) {
                if (stem.endsWith(suffix) && stem.length > suffix.length + 1) {
                    stem = stem.removeSuffix(suffix)
                    break // Only strip one suffix
                }
            }
            stem
        }
        return stemmed.joinToString(" ")
    }

    private fun matchIntentClusters(processedText: String, originalText: String): List<IntentCluster> {
        val matched = mutableListOf<IntentCluster>()
        val allWords = (processedText.split("\\s+".toRegex()) +
                originalText.split("\\s+".toRegex())).map { it.trim().lowercase() }.toSet()

        for (cluster in intentClusters) {
            var clusterMatched = false

            // Check if any keyword appears in the text
            for (keyword in cluster.hindiKeywords) {
                val keyLower = keyword.lowercase()
                // Word-level match
                if (keyLower in allWords) {
                    clusterMatched = true
                    break
                }
                // Substring match (for multi-word keywords or partial matches)
                if (processedText.contains(keyLower) || originalText.contains(keyLower)) {
                    clusterMatched = true
                    break
                }
            }

            if (clusterMatched) {
                matched.add(cluster)
            }
        }

        return matched
    }
}
