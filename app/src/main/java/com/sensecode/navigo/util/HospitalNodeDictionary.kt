package com.sensecode.navigo.util

/**
 * A robust mapping dictionary that links specific Hospital Destination concepts 
 * to their common English and Hindi (Devanagari) synonyms.
 * 
 * This ensures that standard hospital nodes (like OPD, Emergency, Pharmacy) 
 * have consistent text-to-node mapping across different spoken languages offline.
 */
object HospitalNodeDictionary {

    data class NodeDefinition(
        val nodeType: String,
        val synonyms: List<String>
    )

    val definitions = listOf(
        NodeDefinition(
            nodeType = "NODE_PHARMACY",
            synonyms = listOf(
                // English
                "Pharmacy", "Dispensary", "Medical Store", "Chemist", "Drug Store",
                // Hindi / Hinglish
                "फार्मेसी", "दवाखाना", "दवाई की दुकान", "मेडिकल काउंटर", "पर्चा काउंटर"
            )
        ),
        NodeDefinition(
            nodeType = "NODE_EMERGENCY",
            synonyms = listOf(
                // English
                "Emergency", "Casualty", "Trauma Center", "Urgent Care", "ER",
                // Hindi / Hinglish
                "इमरजेंसी", "आपातकालीन", "आपातकालीन कक्ष", "कैजुअल्टी", "ट्रॉमा"
            )
        ),
        NodeDefinition(
            nodeType = "NODE_OPD",
            synonyms = listOf(
                // English
                "OPD", "Outpatient Department", "Consultation", "Doctor Clinic", "Checkup",
                // Hindi / Hinglish
                "ओपीडी", "बाह्य रोगी विभाग", "डॉक्टर को दिखाना", "परामर्श", "चेकअप"
            )
        ),
        NodeDefinition(
            nodeType = "NODE_ICU",
            synonyms = listOf(
                // English
                "ICU", "Intensive Care Unit", "Critical Care", "CCU", "NICU",
                // Hindi / Hinglish
                "आईसीयू", "गहन चिकित्सा इकाई", "इंटेन्सिव केयर", "क्रिटिकल केयर"
            )
        ),
        NodeDefinition(
            nodeType = "NODE_OT",
            synonyms = listOf(
                // English
                "OT", "Operation Theatre", "Surgery", "Operating Room",
                // Hindi / Hinglish
                "ओटी", "ऑपरेशन थिएटर", "शल्य चिकित्सा कक्ष", "चीर-फाड़ कक्ष", "सर्जरी"
            )
        ),
        NodeDefinition(
            nodeType = "NODE_LAB",
            synonyms = listOf(
                // English
                "Lab", "Pathology", "Blood Test", "Diagnostic Center", "Sample Collection",
                // Hindi / Hinglish
                "लैब", "पैथोलॉजी", "खून की जांच", "रक्त परीक्षण", "सैंपल टेस्ट", "जांच केंद्र"
            )
        ),
        NodeDefinition(
            nodeType = "NODE_RADIOLOGY",
            synonyms = listOf(
                // English
                "X-Ray", "MRI", "CT Scan", "Ultrasound", "Radiology",
                // Hindi / Hinglish
                "एक्सरे", "एमआरआई", "सीटी स्कैन", "अल्ट्रासाउंड", "रेडियोलॉजी"
            )
        ),
        NodeDefinition(
            nodeType = "NODE_RECEPTION",
            synonyms = listOf(
                // English
                "Reception", "Help Desk", "Inquiry", "Information", "Main Desk",
                // Hindi / Hinglish
                "रिसेप्शन", "पूछताछ", "सहायता केंद्र", "जानकारी डेस्क", "स्वागत कक्ष"
            )
        ),
        NodeDefinition(
            nodeType = "NODE_BILLING",
            synonyms = listOf(
                // English
                "Billing", "Accounts", "Cashier", "Payment Counter", "Discharge Desk",
                // Hindi / Hinglish
                "बिलिंग", "भुगतान", "पैसे जमा करने", "नकद काउंटर", "खाता शाखा"
            )
        ),
        NodeDefinition(
            nodeType = "NODE_RESTROOM",
            synonyms = listOf(
                // English
                "Restroom", "Toilet", "Washroom", "Bathroom", "WC",
                // Hindi / Hinglish
                "शौचालय", "बाथरूम", "टॉयलेट", "वॉशरूम", "प्रसाधन", "सुलभ शौचालय"
            )
        )
    )

    /**
     * Resolves an offline query to a unified Node Definition Type (e.g., "NODE_OPD")
     * by checking against the dictionary of English and UTF-8 Devanagari strings.
     */
    fun matchQueryToNodeType(query: String): String? {
        val lowerQuery = query.trim().lowercase()
        return definitions.firstOrNull { def ->
            def.synonyms.any { synonym ->
                lowerQuery.contains(synonym.lowercase())
            }
        }?.nodeType
    }
}

